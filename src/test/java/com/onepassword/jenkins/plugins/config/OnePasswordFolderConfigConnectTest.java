package com.onepassword.jenkins.plugins.config;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
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
class OnePasswordFolderConfigConnectTest {

    private JenkinsRule j;

    private final String basePath = "src/test/resources/com/onepassword/jenkins/plugins/config/connect/";

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
    void testConfigFolder() throws Exception {
        Folder folder = j.createProject(Folder.class);
        OnePasswordConfig config = new OnePasswordConfig();
        config.setConnectHost(TEST_CONNECT_HOST);
        config.setConnectCredentialId("connect-credential-id");
        config.setConnectCredential(c);
        folder.addProperty(new OnePasswordFolderConfig(config));

        WorkflowJob project = folder.createProject(WorkflowJob.class, "test-folder-config");
        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigFolder.groovy",
                Charset.defaultCharset())
                .replace("OP_CLI_URL", OP_CLI_URL),
                true));

        WorkflowRun build = j.buildAndAssertSuccess(project);
        j.assertLogContains(TEST_SUCCESS, build);
        j.assertLogNotContains(TEST_FAILURE, build);
    }
}
