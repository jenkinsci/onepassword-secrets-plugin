package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.onepassword.jenkins.plugins.config.OnePasswordConfig;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the item-category &rarr; Jenkins-credential-type mapping. These do not touch the
 * {@code op} CLI: only the credential shell (type, id, description) is asserted, never the lazily
 * fetched value.
 */
public class CredentialsFactoryTest {

    private static final OnePasswordConfig CONFIG = new OnePasswordConfig();

    private static OnePasswordItemAccessor.ItemSummary item(String category) {
        return new OnePasswordItemAccessor.ItemSummary("item-id-123", "MY_ITEM", category);
    }

    @Test
    public void passwordMapsToStringCredential() {
        Optional<StandardCredentials> c = CredentialsFactory.create(CONFIG, "my-vault", item("PASSWORD"));
        assertTrue(c.isPresent());
        assertTrue(c.get() instanceof OnePasswordStringCredentials);
        assertEquals("MY_ITEM", c.get().getId());
    }

    @Test
    public void apiCredentialMapsToStringCredential() {
        Optional<StandardCredentials> c = CredentialsFactory.create(CONFIG, "my-vault", item("API_CREDENTIAL"));
        assertTrue(c.isPresent());
        assertTrue(c.get() instanceof OnePasswordStringCredentials);
    }

    @Test
    public void secureNoteMapsToStringCredential() {
        Optional<StandardCredentials> c = CredentialsFactory.create(CONFIG, "my-vault", item("SECURE_NOTE"));
        assertTrue(c.isPresent());
        assertTrue(c.get() instanceof OnePasswordStringCredentials);
    }

    @Test
    public void loginMapsToUsernamePasswordCredential() {
        Optional<StandardCredentials> c = CredentialsFactory.create(CONFIG, "my-vault", item("LOGIN"));
        assertTrue(c.isPresent());
        assertTrue(c.get() instanceof OnePasswordUsernamePasswordCredentials);
    }

    @Test
    public void categoryIsCaseInsensitive() {
        Optional<StandardCredentials> c = CredentialsFactory.create(CONFIG, "my-vault", item("password"));
        assertTrue(c.isPresent());
        assertTrue(c.get() instanceof OnePasswordStringCredentials);
    }

    @Test
    public void unsupportedCategoryIsIgnored() {
        assertFalse(CredentialsFactory.create(CONFIG, "my-vault", item("CREDIT_CARD")).isPresent());
        assertFalse(CredentialsFactory.create(CONFIG, "my-vault", item("DOCUMENT")).isPresent());
    }

    @Test
    public void idFallsBackToItemIdWhenTitleBlank() {
        OnePasswordItemAccessor.ItemSummary noTitle =
                new OnePasswordItemAccessor.ItemSummary("item-id-123", "", "PASSWORD");
        Optional<StandardCredentials> c = CredentialsFactory.create(CONFIG, "my-vault", noTitle);
        assertTrue(c.isPresent());
        assertEquals("item-id-123", c.get().getId());
    }
}
