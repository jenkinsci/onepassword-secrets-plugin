package com.onepassword.jenkins.plugins;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import hudson.model.Result;
import hudson.util.Secret;
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

public class OnePasswordWithSecretsTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private final String basePath = "src/test/resources/com/onepassword/jenkins/plugins/pipeline/";

    private final StringCredentialsImpl c =
            new StringCredentialsImpl(
                    CredentialsScope.GLOBAL, "connect-credential-id",
                    "1Password Connect Credential", Secret.fromString(System.getenv("OP_TOKEN")));

    @Before
    public void init() throws IOException {
        CredentialsProvider.lookupStores(j.jenkins)
                .iterator()
                .next()
                .addCredentials(Domain.global(), c);
    }

    @Test
    public void testSecretsFunction() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testSecretsFunction.groovy",
                Charset.defaultCharset())
                .replace("OP_HOST", TEST_CONNECT_HOST)
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));
        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);
    }

    @Test
    public void testSecretsPipeline() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testSecretsPipeline.groovy",
                Charset.defaultCharset())
                .replace("OP_HOST", TEST_CONNECT_HOST)
                .replace("OP_CLI_URL",OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);
    }

    @Test
    public void testSecretsMultiline() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testSecretsMultiline.groovy",
                Charset.defaultCharset())
                .replace("OP_HOST", TEST_CONNECT_HOST)
                .replace("OP_CLI_URL",OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);
    }

    @Test
    public void testSecretsFromEnv() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testSecretsFromEnv.groovy",
                Charset.defaultCharset())
                .replace("OP_HOST", TEST_CONNECT_HOST)
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);
    }

    @Test
    public void testSecretsWrongReference() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testSecretsWrongReference.groovy",
                Charset.defaultCharset())
                .replace("OP_HOST", TEST_CONNECT_HOST)
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertStatus(Result.FAILURE, project);
        j.assertLogContains("Error retrieving secret op://test-vault/docker/usernamez:", build);
        j.assertLogNotContains("We shall never get here", build);
    }

    @Test
    public void testSecretsMasked() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testSecretsMasked.groovy",
                Charset.defaultCharset())
                .replace("OP_HOST", TEST_CONNECT_HOST)
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogNotContains(TEST_SECRET, build);
    }

    @Test
    public void testSecretsMultilineMasked() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testSecretsMultilineMasked.groovy",
                Charset.defaultCharset())
                .replace("OP_HOST", TEST_CONNECT_HOST)
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogNotContains("-----BEGIN PRIVATE KEY-----", build);
        j.assertLogNotContains("RGVhciBzZWN1cml0eSByZXNlYXJjaGVyLApXaGls", build);
        j.assertLogNotContains("-----END PRIVATE KEY-----", build);
    }
}
