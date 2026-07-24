package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.onepassword.jenkins.plugins.config.OnePasswordConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.util.Secret;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Maps a 1Password item to a concrete Jenkins credential, choosing the credential type from the
 * item's category and wiring up lazy suppliers that fetch the secret value from 1Password on demand.
 *
 * <p>Category mapping (the field referenced in parentheses):
 * <ul>
 *     <li>{@code LOGIN} &rarr; username/password ({@code username} + {@code password})</li>
 *     <li>{@code PASSWORD} &rarr; secret text ({@code password})</li>
 *     <li>{@code API_CREDENTIAL} &rarr; secret text ({@code credential})</li>
 *     <li>{@code SECURE_NOTE} &rarr; secret text ({@code notesPlain})</li>
 * </ul>
 * Other categories (SSH keys, documents, certificates, credit cards, ...) are ignored for now.
 */
final class CredentialsFactory {

    private static final Logger LOG = Logger.getLogger(CredentialsFactory.class.getName());

    private CredentialsFactory() {
    }

    static Optional<StandardCredentials> create(OnePasswordConfig config, String vault,
                                                OnePasswordItemAccessor.ItemSummary item) {
        String id = StringUtils.defaultIfBlank(item.getTitle(), item.getId());
        String description = "1Password item \"" + item.getTitle() + "\" (" + item.getCategory() + ")";
        String category = StringUtils.defaultString(item.getCategory()).toUpperCase();

        switch (category) {
            case "LOGIN":
                String username = readQuietly(config, reference(vault, item.getId(), "username"));
                return Optional.of(new OnePasswordUsernamePasswordCredentials(
                        id, description, username,
                        new SecretSupplier(config, reference(vault, item.getId(), "password"))));
            case "PASSWORD":
                return Optional.of(new OnePasswordStringCredentials(
                        id, description, new SecretSupplier(config, reference(vault, item.getId(), "password"))));
            case "API_CREDENTIAL":
                return Optional.of(new OnePasswordStringCredentials(
                        id, description, new SecretSupplier(config, reference(vault, item.getId(), "credential"))));
            case "SECURE_NOTE":
                return Optional.of(new OnePasswordStringCredentials(
                        id, description, new SecretSupplier(config, reference(vault, item.getId(), "notesPlain"))));
            default:
                return Optional.empty();
        }
    }

    private static String reference(String vault, String itemId, String field) {
        return "op://" + vault + "/" + itemId + "/" + field;
    }

    private static String readQuietly(OnePasswordConfig config, String reference) {
        try {
            return OnePasswordItemAccessor.read(config, reference);
        } catch (RuntimeException e) {
            LOG.log(Level.FINE, "Could not read " + reference + ": " + e.getMessage());
            return "";
        }
    }

    /** Lazily resolves a single 1Password reference to a {@link Secret} when the value is requested. */
    @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
            justification = "The CredentialsSnapshotTaker replaces this live supplier with a resolved "
                    + "Snapshot before the credential is ever serialized to a build agent, so the "
                    + "transient config is never needed after deserialization.")
    private static final class SecretSupplier implements Supplier<Secret>, Serializable {
        private static final long serialVersionUID = 1L;
        // Not serialized: a snapshot replaces this supplier before the credential reaches an agent.
        private final transient OnePasswordConfig config;
        private final String reference;

        SecretSupplier(OnePasswordConfig config, String reference) {
            this.config = config;
            this.reference = reference;
        }

        @Override
        public Secret get() {
            return Secret.fromString(OnePasswordItemAccessor.read(config, reference));
        }
    }
}
