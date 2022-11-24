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

public class OnePasswordConfigConnectTest {

//    @Rule
//    public JenkinsRule j = new JenkinsRule();
//
//    private final String basePath = "src/test/resources/com/onepassword/jenkins/plugins/config/connect/";
//
//    private final StringCredentialsImpl c =
//            new StringCredentialsImpl(
//                    CredentialsScope.GLOBAL, "connect-credential-id",
//                    "1Password Connect Credential", Secret.fromString(System.getenv("OP_TOKEN")));
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
//    public void testConfigFunction() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigFunction.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//    }
//
//    @Test
//    public void testConfigPipeline() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigPipeline.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//    }
//
//    @Test
//    public void testConfigGlobal() throws Exception {
//        OnePasswordGlobalConfig globalConfig = GlobalConfiguration.all().get(OnePasswordGlobalConfig.class);
//        OnePasswordConfig config = new OnePasswordConfig();
//        config.setConnectHost(TEST_CONNECT_HOST);
//        config.setConnectCredentialId("connect-credential-id");
//        config.setConnectCredential(c);
//        globalConfig.setConfig(config);
//        globalConfig.save();
//
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigGlobal.groovy",
//                Charset.defaultCharset())
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//
//        globalConfig.setConfig(null);
//        globalConfig.save();
//    }
//
//    @Test
//    public void testConfigMergeHost() throws Exception {
//        OnePasswordGlobalConfig globalConfig = GlobalConfiguration.all().get(OnePasswordGlobalConfig.class);
//        OnePasswordConfig config = new OnePasswordConfig();
//        config.setConnectHost(TEST_CONNECT_HOST);
//        globalConfig.setConfig(config);
//        globalConfig.save();
//
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigMergeHost.groovy",
//                Charset.defaultCharset())
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//
//        globalConfig.setConfig(null);
//        globalConfig.save();
//    }
//
//    @Test
//    public void testConfigMergeTokenId() throws Exception {
//        OnePasswordGlobalConfig globalConfig = GlobalConfiguration.all().get(OnePasswordGlobalConfig.class);
//        OnePasswordConfig config = new OnePasswordConfig();
//        config.setConnectCredentialId("connect-credential-id");
//        globalConfig.setConfig(config);
//        globalConfig.save();
//
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigMergeTokenId.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//
//        globalConfig.setConfig(null);
//        globalConfig.save();
//    }
//
//    @Test
//    public void testConfigFromEnv() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigFromEnv.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//    }
//
//    @Test
//    public void testConfigPriorityHost() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigPriorityHost.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//    }
//
//    @Test
//    public void testConfigPriorityToken() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigPriorityToken.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertSuccess(project);
//        j.assertLogContains(TEST_SUCCESS, build);
//        j.assertLogNotContains(TEST_FAILURE, build);
//    }
//
//    @Test
//    public void testConfigNoHost() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigNoHost.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertStatus(Result.FAILURE, project);
//        j.assertLogContains("The Connect host is not configured", build);
//        j.assertLogNotContains("We shall never get here", build);
//    }
//
//    @Test
//    public void testConfigNoToken() throws Exception {
//        CredentialsProvider.lookupStores(j.jenkins)
//                .iterator()
//                .next()
//                .removeCredentials(Domain.global(), c);
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigNoToken.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertStatus(Result.FAILURE, project);
//        j.assertLogContains("Property 'connect-credential-id' is currently unavailable", build);
//        j.assertLogNotContains("We shall never get here", build);
//    }
//
//    @Test
//    public void testConfigNoConfig() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigNoConfig.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertStatus(Result.FAILURE, project);
//        j.assertLogContains("No config found - please configure 1Password", build);
//        j.assertLogNotContains("No credential has been configured - please provide either the credential and host of the Connect instance or the credential of the Service Account Token.", build);
//    }
//
//    @Test
//    public void testConfigNoSpecifiedToken() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigNoSpecifiedToken.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertStatus(Result.FAILURE, project);
//        j.assertLogContains("The Connect credential is not configured - please provide the credential of the Connect instance.", build);
//        j.assertLogNotContains("We shall never get here", build);
//    }
//
//    @Test
//    public void testConfigWrongHost() throws Exception {
//        WorkflowJob project = j.createProject(WorkflowJob.class);
//        project.setDefinition(new CpsFlowDefinition(readFile(basePath + "testConfigWrongHost.groovy",
//                Charset.defaultCharset())
//                .replace("OP_HOST", TEST_CONNECT_HOST)
//                .replace("OP_CLI_URL", OP_CLI_URL),
//                true));
//
//        WorkflowRun build = j.buildAndAssertStatus(Result.FAILURE, project);
//        j.assertLogContains("Error retrieving secret op://acceptance-tests/test-secret/password:", build);
//        j.assertLogNotContains("We shall never get here", build);
//    }
}
