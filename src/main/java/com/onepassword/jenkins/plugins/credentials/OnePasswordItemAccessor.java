package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.onepassword.jenkins.plugins.config.OnePasswordConfig;
import com.onepassword.jenkins.plugins.config.OnePasswordGlobalConfig;
import com.onepassword.jenkins.plugins.exception.OnePasswordException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Talks to 1Password through the {@code op} CLI on behalf of the credentials provider.
 *
 * <p>Unlike the build-time accessor, this runs at the system level (no {@link hudson.model.Run}
 * context), so it resolves the Connect / Service Account tokens directly from the Jenkins system
 * credentials store rather than through {@code CredentialsProvider.lookupCredentials(...)} — which
 * would otherwise re-enter this very provider while it is listing credentials.
 */
final class OnePasswordItemAccessor {

    // Environment variables understood by the op CLI.
    private static final String ENV_OP_CONNECT_HOST = "OP_CONNECT_HOST";
    private static final String ENV_OP_CONNECT_TOKEN = "OP_CONNECT_TOKEN";
    private static final String ENV_OP_SERVICE_ACCOUNT_TOKEN = "OP_SERVICE_ACCOUNT_TOKEN";
    private static final String ENV_OP_INTEGRATION_NAME = "OP_INTEGRATION_NAME";
    private static final String ENV_OP_INTEGRATION_ID = "OP_INTEGRATION_ID";
    private static final String ENV_OP_INTEGRATION_BUILDNUMBER = "OP_INTEGRATION_BUILDNUMBER";

    private static final String OP_INTEGRATION_NAME = "1Password Jenkins Plugin";
    private static final String OP_INTEGRATION_ID = "JEN";
    private static final String OP_INTEGRATION_BUILDNUMBER = "0001001";

    private OnePasswordItemAccessor() {
    }

    /** A lightweight view of a 1Password item as returned by {@code op item list}. */
    static final class ItemSummary implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String id;
        private final String title;
        private final String category;

        ItemSummary(String id, String title, String category) {
            this.id = id;
            this.title = title;
            this.category = category;
        }

        String getId() {
            return id;
        }

        String getTitle() {
            return title;
        }

        String getCategory() {
            return category;
        }
    }

    /** List the items in {@code vault}, optionally restricted to those carrying {@code tag}. */
    static List<ItemSummary> listItems(OnePasswordConfig config, String vault, String tag) {
        List<String> command = new ArrayList<>();
        command.add(opBinary(config));
        command.add("item");
        command.add("list");
        command.add("--vault");
        command.add(vault);
        if (StringUtils.isNotBlank(tag)) {
            command.add("--tags");
            command.add(tag);
        }
        command.add("--format");
        command.add("json");

        String output = run(config, command);
        List<ItemSummary> items = new ArrayList<>();
        if (StringUtils.isBlank(output)) {
            return items;
        }
        JSONArray array = JSONArray.fromObject(output);
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            String id = item.optString("id");
            String title = item.optString("title");
            String category = item.optString("category");
            if (StringUtils.isNotBlank(id)) {
                items.add(new ItemSummary(id, title, category));
            }
        }
        return items;
    }

    /** Resolve a single {@code op://vault/item/field} reference to its plaintext value. */
    static String read(OnePasswordConfig config, String reference) {
        List<String> command = new ArrayList<>();
        command.add(opBinary(config));
        command.add("read");
        command.add(reference);
        String value = run(config, command);
        if (StringUtils.isBlank(value)) {
            throw new OnePasswordException("Secret with reference " + reference + " is empty.");
        }
        return value;
    }

    private static String opBinary(OnePasswordConfig config) {
        String opCLIPath = config != null ? config.getOpCLIPath() : null;
        return StringUtils.isBlank(opCLIPath) ? "op" : opCLIPath + "/op";
    }

    private static String run(OnePasswordConfig config, List<String> command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        applyAuthEnvironment(config, pb.environment());
        try {
            Process process = pb.start();
            String stdout;
            String stderr;
            try (BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                 BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                stdout = out.lines().collect(Collectors.joining(System.lineSeparator()));
                stderr = err.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            int exit = process.waitFor();
            if (exit != 0) {
                throw new OnePasswordException("Error running command " + command + ":\n" + stderr + "\n");
            }
            return stdout;
        } catch (IOException e) {
            throw new OnePasswordException("Error running command " + command + ":\n" + e.getMessage() + "\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OnePasswordException("Interrupted while running command " + command);
        }
    }

    private static void applyAuthEnvironment(OnePasswordConfig config, Map<String, String> env) {
        if (config != null) {
            if (StringUtils.isNotBlank(config.getConnectHost())) {
                env.put(ENV_OP_CONNECT_HOST, config.getConnectHost());
            }
            String connectToken = resolveTokenValue(config.getConnectCredentialId());
            if (connectToken != null) {
                env.put(ENV_OP_CONNECT_TOKEN, connectToken);
            }
            String serviceAccountToken = resolveTokenValue(config.getServiceAccountCredentialId());
            if (serviceAccountToken != null) {
                env.put(ENV_OP_SERVICE_ACCOUNT_TOKEN, serviceAccountToken);
            }
        }
        env.put(ENV_OP_INTEGRATION_NAME, OP_INTEGRATION_NAME);
        env.put(ENV_OP_INTEGRATION_ID, OP_INTEGRATION_ID);
        env.put(ENV_OP_INTEGRATION_BUILDNUMBER, OP_INTEGRATION_BUILDNUMBER);
    }

    /**
     * Look up a Secret text credential by id in the Jenkins system store.
     *
     * <p>Deliberately reads the system store directly instead of going through
     * {@code CredentialsProvider.lookupCredentials(...)} to avoid re-entering
     * {@link OnePasswordCredentialsProvider} while it is enumerating credentials.
     */
    private static String resolveTokenValue(String credentialId) {
        if (StringUtils.isBlank(credentialId)) {
            return null;
        }
        for (com.cloudbees.plugins.credentials.Credentials credentials : SystemCredentialsProvider.getInstance().getCredentials()) {
            if (credentials instanceof StringCredentials) {
                StringCredentials stringCredentials = (StringCredentials) credentials;
                if (credentialId.equals(stringCredentials.getId())) {
                    return stringCredentials.getSecret().getPlainText();
                }
            }
        }
        return null;
    }

    /** The auth config shared with the rest of the plugin, or {@code null} if unconfigured. */
    static OnePasswordConfig resolveConfig() {
        return OnePasswordGlobalConfig.get().getConfig();
    }
}
