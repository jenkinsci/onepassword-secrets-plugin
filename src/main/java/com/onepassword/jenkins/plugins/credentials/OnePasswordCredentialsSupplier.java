package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.onepassword.jenkins.plugins.config.OnePasswordConfig;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lists the eligible items in the configured 1Password vault and turns each into a Jenkins
 * credential. Invoked (and its result cached) by {@link OnePasswordCredentialsProvider}.
 */
class OnePasswordCredentialsSupplier implements Supplier<Collection<StandardCredentials>> {

    private static final Logger LOG = Logger.getLogger(OnePasswordCredentialsSupplier.class.getName());

    @Override
    public Collection<StandardCredentials> get() {
        OnePasswordCredentialsProviderConfig providerConfig = OnePasswordCredentialsProviderConfig.getInstance();
        if (!providerConfig.isEnabled() || StringUtils.isBlank(providerConfig.getVault())) {
            return new ArrayList<>();
        }

        OnePasswordConfig authConfig = OnePasswordItemAccessor.resolveConfig();
        if (authConfig == null) {
            LOG.log(Level.WARNING, "1Password credentials provider is enabled but 1Password authentication is not configured.");
            return new ArrayList<>();
        }

        String vault = providerConfig.getVault();
        // Keep insertion order and de-duplicate on the Jenkins credential id (last write wins is
        // avoided: the first item for a given id is kept, matching how a single store would behave).
        Map<String, StandardCredentials> byId = new LinkedHashMap<>();

        List<OnePasswordItemAccessor.ItemSummary> items =
                OnePasswordItemAccessor.listItems(authConfig, vault, providerConfig.getTag());
        for (OnePasswordItemAccessor.ItemSummary item : items) {
            Optional<StandardCredentials> credential = CredentialsFactory.create(authConfig, vault, item);
            if (credential.isPresent()) {
                String id = credential.get().getId();
                if (byId.containsKey(id)) {
                    LOG.log(Level.WARNING, "Ignoring 1Password item {0}: a credential with id \"{1}\" already exists.",
                            new Object[]{item.getId(), id});
                    continue;
                }
                byId.put(id, credential.get());
            }
        }
        return new ArrayList<>(byId.values());
    }
}
