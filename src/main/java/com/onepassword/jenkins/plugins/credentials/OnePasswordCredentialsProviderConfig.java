package com.onepassword.jenkins.plugins.credentials;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.time.Duration;

/**
 * Global configuration for the {@link OnePasswordCredentialsProvider}.
 *
 * <p>Authentication (Connect host / token, or Service Account token, and the {@code op} CLI path)
 * is shared with the rest of the plugin via {@code OnePasswordGlobalConfig}; this configuration only
 * adds the settings that are specific to exposing 1Password items as native Jenkins credentials:
 * which vault to enumerate, an optional tag gate, and the list cache duration.
 */
@Extension
@Symbol("onePasswordCredentialsProvider")
public class OnePasswordCredentialsProviderConfig extends GlobalConfiguration {

    /** "Cache off" is represented as a tiny non-zero duration, mirroring the AWS provider. */
    static final Duration NO_CACHE = Duration.ofNanos(1);
    static final Duration DEFAULT_CACHE = Duration.ofMinutes(5);

    private boolean enabled;
    private String vault;
    private String tag = "jenkins";
    private boolean cache = true;

    public OnePasswordCredentialsProviderConfig() {
        load();
    }

    public static OnePasswordCredentialsProviderConfig getInstance() {
        return ExtensionList.lookupSingleton(OnePasswordCredentialsProviderConfig.class);
    }

    public boolean isEnabled() {
        return enabled;
    }

    @DataBoundSetter
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        save();
    }

    public String getVault() {
        return vault;
    }

    @DataBoundSetter
    public void setVault(String vault) {
        this.vault = hudson.Util.fixEmptyAndTrim(vault);
        save();
    }

    public String getTag() {
        return tag;
    }

    @DataBoundSetter
    public void setTag(String tag) {
        this.tag = hudson.Util.fixEmptyAndTrim(tag);
        save();
    }

    public boolean isCache() {
        return cache;
    }

    @DataBoundSetter
    public void setCache(boolean cache) {
        this.cache = cache;
        save();
    }

    /** The TTL applied to the cached item listing. */
    public Duration getCacheDuration() {
        return cache ? DEFAULT_CACHE : NO_CACHE;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    @NonNull
    @Override
    public String getDisplayName() {
        return "1Password Credentials Provider";
    }
}
