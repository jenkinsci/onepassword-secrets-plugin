package com.onepassword.jenkins.plugins.config;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.Serializable;
import java.util.List;

import static hudson.Util.fixEmptyAndTrim;

public class OnePasswordConfig extends AbstractDescribableImpl<OnePasswordConfig> implements Serializable {

    private String connectHost;
    private String connectCredentialId;
    private StringCredentials connectCredential;
    private String serviceAccountCredentialId;
    private StringCredentials serviceAccountCredential;
    private String opCLIPath;

    @DataBoundConstructor
    public OnePasswordConfig() {
    }

    public OnePasswordConfig(OnePasswordConfig toConfig) {
        this.connectHost = toConfig.getConnectHost();
        this.connectCredentialId = toConfig.getConnectCredentialId();
        this.connectCredential = toConfig.getConnectCredential();
        this.serviceAccountCredentialId = toConfig.getServiceAccountCredentialId();
        this.serviceAccountCredential = toConfig.getServiceAccountCredential();
        this.opCLIPath = toConfig.getOpCLIPath();
    }

    public OnePasswordConfig mergeWithParent(OnePasswordConfig parent) {
        if (parent == null) {
            return this;
        }

        OnePasswordConfig result = new OnePasswordConfig(this);
        if (StringUtils.isBlank(result.connectHost)) {
            result.setConnectHost(parent.getConnectHost());
        }
        if (StringUtils.isBlank(result.getConnectCredentialId())) {
            result.setConnectCredentialId(parent.getConnectCredentialId());
        }
        if (result.connectCredential == null) {
            result.setConnectCredential(parent.getConnectCredential());
        }
        if (StringUtils.isBlank(result.getServiceAccountCredentialId())) {
            result.setServiceAccountCredentialId(parent.getServiceAccountCredentialId());
        }
        if (result.serviceAccountCredential == null) {
            result.setServiceAccountCredential(parent.getServiceAccountCredential());
        }

        return result;
    }

    public String getConnectHost() {
        return connectHost;
    }

    public String getConnectCredentialId() {
        return connectCredentialId;
    }

    public StringCredentials getConnectCredential() {
        return connectCredential;
    }

    public String getServiceAccountCredentialId() {
        return serviceAccountCredentialId;
    }

    public StringCredentials getServiceAccountCredential() {
        return serviceAccountCredential;
    }

    public String getOpCLIPath() {
        return opCLIPath;
    }

    @DataBoundSetter
    public void setConnectHost(String connectHost) {
        this.connectHost = fixEmptyAndTrim(connectHost);
    }

    @DataBoundSetter
    public void setConnectCredentialId(String connectCredentialId) {
        this.connectCredentialId = fixEmptyAndTrim(connectCredentialId);
    }

    @DataBoundSetter
    public void setConnectCredential(StringCredentials connectCredential) {
        this.connectCredential = connectCredential;
    }

    @DataBoundSetter
    public void setServiceAccountCredentialId(String serviceAccountCredentialId) {
        this.serviceAccountCredentialId = fixEmptyAndTrim(serviceAccountCredentialId);
    }

    @DataBoundSetter
    public void setServiceAccountCredential(StringCredentials serviceAccountCredential) {
        this.serviceAccountCredential = serviceAccountCredential;
    }

    @DataBoundSetter
    public void setOpCLIPath(String opCLIPath) {
        this.opCLIPath = opCLIPath;
    }

    public boolean hasConnectHost() {
        return connectHost != null;
    }

    public boolean hasConnectCredentialId() {
        return connectCredentialId != null;
    }

    public boolean hasServiceAccountCredentialId() {
        return serviceAccountCredentialId != null;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<OnePasswordConfig> {
        @Override
        @NonNull
        public String getDisplayName() {
            return "1Password Config";
        }

        public ListBoxModel doFillConnectCredentialIdItems(@AncestorInPath Item item, @QueryParameter String uri) {
            List<DomainRequirement> domainRequirements = URIRequirementBuilder.fromUri(uri).build();
            return new StandardListBoxModel().includeEmptyValue().includeAs(ACL.SYSTEM, item,
                    StringCredentials.class, domainRequirements);
        }

        public ListBoxModel doFillServiceAccountCredentialIdItems(@AncestorInPath Item item, @QueryParameter String uri) {
            List<DomainRequirement> domainRequirements = URIRequirementBuilder.fromUri(uri).build();
            return new StandardListBoxModel().includeEmptyValue().includeAs(ACL.SYSTEM, item,
                    StringCredentials.class, domainRequirements);
        }
    }
}
