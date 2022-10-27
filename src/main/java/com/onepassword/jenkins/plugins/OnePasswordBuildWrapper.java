package com.onepassword.jenkins.plugins;

import com.onepassword.jenkins.plugins.config.OnePasswordConfig;
import com.onepassword.jenkins.plugins.log.MaskingConsoleLogFilter;
import com.onepassword.jenkins.plugins.model.OnePasswordSecret;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.console.ConsoleLogFilter;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnePasswordBuildWrapper extends SimpleBuildWrapper {

    private OnePasswordConfig config;
    private List<OnePasswordSecret> secrets;
    private List<String> maskedSecrets = new ArrayList<>();
    protected PrintStream logger;

    @DataBoundConstructor
    public OnePasswordBuildWrapper() {
    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace,
                      Launcher launcher, TaskListener listener, EnvVars envVars) {
        logger = listener.getLogger();
        OnePasswordAccessor.pullAndMergeConfig(build, config, envVars);

        Map<String, String> overrides = OnePasswordAccessor
                .loadSecrets(build, logger, envVars,
                        getConfig(), getSecrets());

        for (Map.Entry<String, String> secret : overrides.entrySet()) {
            maskedSecrets.add(secret.getValue());
            context.env(secret.getKey(), secret.getValue());
        }
    }

    @DataBoundSetter
    public void setSecrets(List<OnePasswordSecret> secrets) {
        this.secrets = secrets;
    }

    public List<OnePasswordSecret> getSecrets() {
        return this.secrets;
    }

    @DataBoundSetter
    public void setConfig(OnePasswordConfig config) {
        this.config = config;
    }

    public OnePasswordConfig getConfig() {
        return this.config;
    }

    @Override
    public ConsoleLogFilter createLoggerDecorator(
            @NonNull final Run<?, ?> build) {
        return new MaskingConsoleLogFilter(build.getCharset().name(), maskedSecrets);
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(OnePasswordBuildWrapper.class);
            load();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "1Password Secrets";
        }
    }
}
