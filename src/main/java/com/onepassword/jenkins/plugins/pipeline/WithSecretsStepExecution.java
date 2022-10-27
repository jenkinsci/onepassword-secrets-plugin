package com.onepassword.jenkins.plugins.pipeline;

import com.onepassword.jenkins.plugins.OnePasswordAccessor;
import com.onepassword.jenkins.plugins.log.MaskingConsoleLogFilter;
import hudson.EnvVars;
import hudson.console.ConsoleLogFilter;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.Secret;
import org.jenkinsci.plugins.workflow.steps.*;

import java.io.IOException;
import java.util.*;

public class WithSecretsStepExecution extends StepExecution {

    private static final long serialVersionUID = 7094121466904976191L;
    private final WithSecretsStep step;

    WithSecretsStepExecution(WithSecretsStep step, StepContext context) {
        super(context);
        this.step = step;
    }

    @Override
    public boolean start() throws Exception {
        Run<?, ?> run = getContext().get(Run.class);
        TaskListener listener = getContext().get(TaskListener.class);
        EnvVars envVars = getContext().get(EnvVars.class);

        Map<String, String> overrides = OnePasswordAccessor
                .loadSecrets(run, listener.getLogger(), envVars,
                        step.getConfig(), step.getSecrets());

        List<String> secretValues = new ArrayList<>();
        secretValues.addAll(overrides.values());

        getContext().newBodyInvoker()
                .withContext(EnvironmentExpander.merge(getContext().get(EnvironmentExpander.class),
                        new Overrider(overrides)))
                .withContext(BodyInvoker
                        .mergeConsoleLogFilters(getContext().get(ConsoleLogFilter.class),
                                new MaskingConsoleLogFilter(run.getCharset().name(), secretValues)))
                .withCallback(new Callback())
                .start();
        return false;
    }

    private static final class Overrider extends EnvironmentExpander {

        private static final long serialVersionUID = 1;

        private final Map<String, Secret> overrides = new HashMap<>();

        Overrider(Map<String, String> overrides) {
            for (Map.Entry<String, String> override : overrides.entrySet()) {
                this.overrides.put(override.getKey(), Secret.fromString(override.getValue()));
            }
        }

        @Override
        public void expand(EnvVars env) throws IOException, InterruptedException {
            for (Map.Entry<String, Secret> override : overrides.entrySet()) {
                env.override(override.getKey(), override.getValue().getPlainText());
            }
        }

        @Override
        public Set<String> getSensitiveVariables() {
            return Collections.unmodifiableSet(overrides.keySet());
        }
    }

    private static class Callback extends BodyExecutionCallback.TailCall {
        @Override
        protected void finished(StepContext context) throws Exception {
        }
    }
}
