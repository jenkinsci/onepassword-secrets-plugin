package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import hudson.ExtensionList;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class OnePasswordCredentialsProviderTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private OnePasswordCredentialsProvider provider() {
        return ExtensionList.lookupSingleton(OnePasswordCredentialsProvider.class);
    }

    @Test
    public void providerIsRegistered() {
        assertNotNull(provider());
    }

    @Test
    public void exposesAStoreOnlyForJenkinsRoot() throws Exception {
        OnePasswordCredentialsProvider provider = provider();
        assertNotNull(provider.getStore(j.jenkins));
        assertNull("no per-item store", provider.getStore(j.createFreeStyleProject()));
    }

    @Test
    public void returnsNoCredentialsWhenDisabled() {
        OnePasswordCredentialsProviderConfig.getInstance().setEnabled(false);
        List<Credentials> creds =
                provider().getCredentials(Credentials.class, j.jenkins, ACL.SYSTEM);
        assertTrue(creds.isEmpty());
    }

    @Test
    public void listingIsGracefulWhenEnabledButUnauthenticated() {
        // Enabled with a vault set, but no 1Password auth configured / op available: must not throw,
        // and must return an empty list rather than propagating a failure.
        OnePasswordCredentialsProviderConfig config = OnePasswordCredentialsProviderConfig.getInstance();
        config.setEnabled(true);
        config.setVault("does-not-exist");
        List<Credentials> creds =
                provider().getCredentials(Credentials.class, j.jenkins, ACL.SYSTEM);
        assertTrue(creds.isEmpty());
    }

    @Test
    public void nonSystemAuthenticationGetsNothing() {
        OnePasswordCredentialsProviderConfig.getInstance().setEnabled(true);
        List<Credentials> creds =
                provider().getCredentials(Credentials.class, j.jenkins, Jenkins.ANONYMOUS);
        assertTrue(creds.isEmpty());
    }

    @Test
    public void storeIsListedAmongJenkinsStores() {
        boolean found = false;
        for (CredentialsStore store : CredentialsProvider.lookupStores(j.jenkins)) {
            if (store instanceof OnePasswordCredentialsStore) {
                found = true;
                break;
            }
        }
        assertTrue("1Password store should be registered", found);
    }
}
