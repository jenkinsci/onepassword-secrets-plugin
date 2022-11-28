package com.onepassword.jenkins.plugins.config;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import hudson.model.Result;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.onepassword.jenkins.plugins.util.TestConstants.*;

public class OnePasswordConfigServiceAccountTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private final String basePath = "src/test/resources/com/onepassword/jenkins/plugins/config/serviceaccount/";
    private final String serviceAccountCredentialId = "service-account-credential-id";

    private final StringCredentialsImpl c =
            new StringCredentialsImpl(
                    CredentialsScope.GLOBAL, serviceAccountCredentialId,
                    "1Password Service Account Credential", Secret.fromString(System.getenv("OP_SA_TOKEN")));

    @Before
    public void init() throws IOException {
        CredentialsProvider.lookupStores(j.jenkins)
                .iterator()
                .next()
                .addCredentials(Domain.global(), c);
    }

    @Test
    public void testConfigFunction() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigFunction.groovy",
                Charset.defaultCharset())
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);
    }

    @Test
    public void testConfigPipeline() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigPipeline.groovy",
                Charset.defaultCharset())
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);
    }

    @Test
    public void testConfigGlobal() throws Exception {
        OnePasswordGlobalConfig globalConfig = GlobalConfiguration.all().get(OnePasswordGlobalConfig.class);
        OnePasswordConfig config = new OnePasswordConfig();
        config.setServiceAccountCredentialId(serviceAccountCredentialId);
        config.setServiceAccountCredential(c);
        globalConfig.setConfig(config);
        globalConfig.save();

        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigGlobal.groovy",
                Charset.defaultCharset())
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);

        globalConfig.setConfig(null);
        globalConfig.save();
    }

    @Test
    public void testConfigMergeTokenId() throws Exception {
        OnePasswordGlobalConfig globalConfig = GlobalConfiguration.all().get(OnePasswordGlobalConfig.class);
        OnePasswordConfig config = new OnePasswordConfig();
        config.setServiceAccountCredentialId(serviceAccountCredentialId);
        globalConfig.setConfig(config);
        globalConfig.save();

        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigMergeTokenId.groovy",
                Charset.defaultCharset())
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);

        globalConfig.setConfig(null);
        globalConfig.save();
    }

    @Test
    public void testConfigFromEnv() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigFromEnv.groovy",
                Charset.defaultCharset())
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);
    }

    @Test
    public void testConfigPriorityToken() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigPriorityToken.groovy",
                Charset.defaultCharset())
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogNotContains(TEST_SUCCESS, build);
        j.assertLogContains(TEST_FAILURE, build);
    }

    @Test
    public void testConfigNoToken() throws Exception {
        CredentialsProvider.lookupStores(j.jenkins)
                .iterator()
                .next()
                .removeCredentials(Domain.global(), c);
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigNoToken.groovy",
                Charset.defaultCharset())
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertStatus(Result.FAILURE, project);
        j.assertLogContains("Property 'service-account-credential-id' is currently unavailable", build);
        j.assertLogNotContains("We shall never get here", build);
    }
}
