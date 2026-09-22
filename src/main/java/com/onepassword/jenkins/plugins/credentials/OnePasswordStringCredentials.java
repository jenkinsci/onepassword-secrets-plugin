package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsSnapshotTaker;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.Secret;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.util.function.Supplier;

/**
 * A {@link StringCredentials} (Secret text) whose value is fetched lazily from 1Password. This backs
 * 1Password items of category {@code PASSWORD}, {@code API_CREDENTIAL} and {@code SECURE_NOTE}.
 */
public class OnePasswordStringCredentials extends BaseStandardCredentials implements StringCredentials {

    private final Supplier<Secret> value;

    public OnePasswordStringCredentials(String id, String description, Supplier<Secret> value) {
        super(id, description);
        this.value = value;
    }

    @NonNull
    @Override
    public Secret getSecret() {
        return value.get();
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @NonNull
        @Override
        public String getDisplayName() {
            return "1Password Secret Text";
        }

        @Override
        public boolean isApplicable(CredentialsProvider provider) {
            return provider instanceof OnePasswordCredentialsProvider;
        }
    }

    @Extension
    public static class SnapshotTaker extends CredentialsSnapshotTaker<OnePasswordStringCredentials> {
        @Override
        public Class<OnePasswordStringCredentials> type() {
            return OnePasswordStringCredentials.class;
        }

        @Override
        public OnePasswordStringCredentials snapshot(OnePasswordStringCredentials credential) {
            return new OnePasswordStringCredentials(
                    credential.getId(), credential.getDescription(), new Snapshot<>(credential.getSecret()));
        }
    }
}
