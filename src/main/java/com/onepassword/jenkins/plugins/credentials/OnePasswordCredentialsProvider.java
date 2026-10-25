package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A read-only {@link CredentialsProvider} that surfaces 1Password items as native Jenkins
 * credentials, resolved by id. Unlike the pipeline-time {@code withSecrets} step, this makes
 * 1Password-backed credentials usable by system-level consumers that look a credential up by id —
 * SCM checkout, webhook-signature validation, clouds, and so on — without ever storing the secret
 * value in Jenkins.
 *
 * <p>Modeled on the AWS Secrets Manager Credentials Provider: the item listing is cached for a
 * configurable TTL, while each secret value is fetched from 1Password lazily and only when used.
 */
@Extension
public class OnePasswordCredentialsProvider extends CredentialsProvider {

    private static final Logger LOG = Logger.getLogger(OnePasswordCredentialsProvider.class.getName());

    private final OnePasswordCredentialsStore store = new OnePasswordCredentialsStore(this);

    private final Supplier<Collection<StandardCredentials>> credentialsSupplier =
            CustomSuppliers.memoizeWithExpiration(
                    new OnePasswordCredentialsSupplier(),
                    () -> OnePasswordCredentialsProviderConfig.getInstance().getCacheDuration());

    @NonNull
    @Override
    public <C extends Credentials> List<C> getCredentials(@NonNull Class<C> type, ItemGroup itemGroup,
                                                          Authentication authentication) {
        if (!ACL.SYSTEM.equals(authentication)) {
            return Collections.emptyList();
        }
        Collection<StandardCredentials> all = Collections.emptyList();
        try {
            all = credentialsSupplier.get();
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Could not list credentials from 1Password: {0}", e.getMessage());
        }
        return all.stream()
                .filter(c -> type.isAssignableFrom(c.getClass()))
                .map(type::cast)
                .collect(Collectors.toList());
    }

    @Override
    public CredentialsStore getStore(ModelObject object) {
        return object == Jenkins.get() ? store : null;
    }

    @Override
    public String getIconClassName() {
        return "symbol-lock";
    }
}
