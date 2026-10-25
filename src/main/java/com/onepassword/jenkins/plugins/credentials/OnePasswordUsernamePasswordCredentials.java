package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsSnapshotTaker;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;

import java.util.function.Supplier;

/**
 * A {@link StandardUsernamePasswordCredentials} whose password is fetched lazily from 1Password.
 * This backs 1Password items of category {@code LOGIN}. The username is resolved eagerly (it is not
 * secret) when the credential is listed.
 */
public class OnePasswordUsernamePasswordCredentials extends BaseStandardCredentials
        implements StandardUsernamePasswordCredentials {

    private final String username;
    private final Supplier<Secret> password;

    public OnePasswordUsernamePasswordCredentials(String id, String description, String username, Supplier<Secret> password) {
        super(id, description);
        this.username = username;
        this.password = password;
    }

    @NonNull
    @Override
    public String getUsername() {
        return username == null ? "" : username;
    }

    @NonNull
    @Override
    public Secret getPassword() {
        return password.get();
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @NonNull
        @Override
        public String getDisplayName() {
            return "1Password Username with password";
        }

        @Override
        public boolean isApplicable(CredentialsProvider provider) {
            return provider instanceof OnePasswordCredentialsProvider;
        }
    }

    @Extension
    public static class SnapshotTaker extends CredentialsSnapshotTaker<OnePasswordUsernamePasswordCredentials> {
        @Override
        public Class<OnePasswordUsernamePasswordCredentials> type() {
            return OnePasswordUsernamePasswordCredentials.class;
        }

        @Override
        public OnePasswordUsernamePasswordCredentials snapshot(OnePasswordUsernamePasswordCredentials credential) {
            return new OnePasswordUsernamePasswordCredentials(
                    credential.getId(), credential.getDescription(), credential.getUsername(),
                    new Snapshot<>(credential.getPassword()));
        }
    }
}
