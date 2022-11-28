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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.onepassword.jenkins.plugins.util.TestConstants.*;

public class OnePasswordFolderConfigServiceAccountTest {

//    @Rule
//    public JenkinsRule j = new JenkinsRule();
//
//    private final String basePath = "src/test/resources/com/onepassword/jenkins/plugins/config/serviceaccount/";
//
//    private final StringCredentialsImpl c =
//            new StringCredentialsImpl(
//                    CredentialsScope.GLOBAL, "service-account-credential-id",
//                    "1Password Service Account Credential", Secret.fromString(System.getenv("OP_SA_TOKEN")));
//
//    @Before
//    public void init() throws IOException {
//        CredentialsProvider.lookupStores(j.jenkins)
//                .iterator()
//                .next()
//                .addCredentials(Domain.global(), c);
//    }
//
//    @Test
//    public void testConfigFolder() throws Exception {
//        Folder folder = j.createProject(Folder.class);
//        OnePasswordConfig config = new OnePasswordConfig();
//        config.setServiceAccountCredentialId("service-account-credential-id");
//        config.setServiceAccountCredential(c);
//        folder.addProperty(new OnePasswordFolderConfig(config));
//
//        WorkflowJob project = folder.createProject(WorkflowJob.class, "test-folder-config");
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigFolder.groovy",
//                Charset.defaultCharset())
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//    }
}
