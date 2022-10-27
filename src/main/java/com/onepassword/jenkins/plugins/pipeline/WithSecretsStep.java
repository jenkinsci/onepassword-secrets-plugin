package com.onepassword.jenkins.plugins.pipeline;

import com.onepassword.jenkins.plugins.config.OnePasswordConfig;
import com.onepassword.jenkins.plugins.model.OnePasswordSecret;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;

public class WithSecretsStep extends Step implements Serializable {

    private static final long serialVersionUID = 46L;

    private OnePasswordConfig config;
    private List<OnePasswordSecret> secrets;

    @DataBoundConstructor
    public WithSecretsStep() {
    }

    public List<OnePasswordSecret> getSecrets() {
        return secrets;
    }

    @DataBoundSetter
    public void setConfig(OnePasswordConfig config) {
        this.config = config;
    }

    @DataBoundSetter
    public void setSecrets(List<OnePasswordSecret> secrets) {
        this.secrets = secrets;
    }

    public OnePasswordConfig getConfig() {
        return config;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new WithSecretsStepExecution(this, context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections
                    .unmodifiableSet(
                            new HashSet<>(Arrays.asList(TaskListener.class, Run.class, EnvVars.class)));
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

        @Override
        public String getFunctionName() {
            return "withSecrets";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "1Password Secrets";
        }
    }
}
