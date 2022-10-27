package com.onepassword.jenkins.plugins.config;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

@Extension
@Symbol("1Password Config")
public class OnePasswordGlobalConfig extends GlobalConfiguration {
    private OnePasswordConfig config;

    public static OnePasswordGlobalConfig get() {
        return ExtensionList.lookupSingleton(OnePasswordGlobalConfig.class);
    }

    public OnePasswordGlobalConfig() {
        load();
    }

    public OnePasswordConfig getConfig() {
        return config;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        return true;
    }

    @DataBoundSetter
    public void setConfig(OnePasswordConfig config) {
        this.config = config;
        save();
    }

    @Extension(ordinal = 0)
    public static class ForJob extends OnePasswordConfigResolver {
        @NonNull
        @Override
        public OnePasswordConfig forJob(@NonNull Item job) {
            return OnePasswordGlobalConfig.get().getConfig();
        }
    }
}
