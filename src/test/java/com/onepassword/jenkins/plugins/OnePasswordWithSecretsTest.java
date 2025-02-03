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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.onepassword.jenkins.plugins.util.TestConstants.*;

@WithJenkins
class OnePasswordWithSecretsTest {

    private JenkinsRule j;

    private final String basePath = "src/test/resources/com/onepassword/jenkins/plugins/pipeline/";

    private final StringCredentialsImpl c =
            new StringCredentialsImpl(
                    CredentialsScope.GLOBAL, "connect-credential-id",
                    "1Password Connect Credential", Secret.fromString(System.getenv("OP_TOKEN")));

    @BeforeEach
    void init(JenkinsRule j) throws IOException {
        this.j = j;
        CredentialsProvider.lookupStores(j.jenkins)
                .iterator()
                .next()
                .addCredentials(Domain.global(), c);
    }

    @Test
    void testSecretsFunction() throws Exception {
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
    void testSecretsPipeline() throws Exception {
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
    void testSecretsMultiline() throws Exception {
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
    void testSecretsFromEnv() throws Exception {
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
    void testSecretsWrongReference() throws Exception {
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
    void testSecretsMasked() throws Exception {
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
    void testSecretsMultilineMasked() throws Exception {
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
