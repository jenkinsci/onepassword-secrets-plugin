package com.onepassword.jenkins.plugins.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

import static hudson.Util.fixEmptyAndTrim;

public class OnePasswordSecret extends AbstractDescribableImpl<OnePasswordSecret>
        implements Serializable, ExtensionPoint {
    private String envVar;
    private final String secretRef;

    @DataBoundConstructor
    public OnePasswordSecret(@NonNull String envVar, @NonNull String secretRef) {
        this.envVar = fixEmptyAndTrim(envVar);
        this.secretRef = fixEmptyAndTrim(secretRef);
    }

    public String getEnvVar() {
        return StringUtils.isEmpty(envVar) ? secretRef : envVar;
    }

    public String getSecretRef() {
        return this.secretRef;
    }

    @Override
    public String toString() {
        return envVar + " : " + secretRef;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<OnePasswordSecret> {
        @Override
        public String getDisplayName() {
            return "1Password Secret";
        }
    }
}
