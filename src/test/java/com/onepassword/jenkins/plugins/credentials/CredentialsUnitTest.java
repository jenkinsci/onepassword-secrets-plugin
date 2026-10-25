package com.onepassword.jenkins.plugins.credentials;

import com.onepassword.jenkins.plugins.exception.OnePasswordException;
import hudson.util.Secret;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Plain unit tests (no Jenkins) for the pieces that do not touch the {@code op} CLI: the snapshot
 * holder, the expiring cache supplier, and the credential shells / snapshot takers when backed by an
 * already-resolved value.
 */
public class CredentialsUnitTest {

    @Test
    public void snapshotReturnsHeldValue() {
        Snapshot<String> snapshot = new Snapshot<>("value");
        assertEquals("value", snapshot.get());
    }

    @Test
    public void memoizeReusesValueWithinTtl() {
        AtomicInteger calls = new AtomicInteger();
        Supplier<Integer> memoized =
                CustomSuppliers.memoizeWithExpiration(calls::incrementAndGet, () -> Duration.ofHours(1));
        assertEquals(Integer.valueOf(1), memoized.get());
        assertEquals(Integer.valueOf(1), memoized.get());
        assertEquals(Integer.valueOf(1), memoized.get());
        assertEquals("delegate invoked once within the TTL", 1, calls.get());
    }

    @Test
    public void memoizeRecomputesWhenTtlIsEffectivelyZero() {
        AtomicInteger calls = new AtomicInteger();
        Supplier<Integer> memoized =
                CustomSuppliers.memoizeWithExpiration(calls::incrementAndGet, () -> Duration.ofNanos(1));
        memoized.get();
        memoized.get();
        assertTrue("delegate re-invoked once the tiny TTL elapses", calls.get() >= 2);
    }

    @Test
    public void stringCredentialExposesSnapshotValue() {
        Secret secret = Secret.fromString("s3cr3t");
        OnePasswordStringCredentials cred =
                new OnePasswordStringCredentials("id", "desc", new Snapshot<>(secret));
        assertEquals("id", cred.getId());
        assertEquals("s3cr3t", cred.getSecret().getPlainText());
    }

    @Test
    public void stringCredentialSnapshotTakerResolvesValue() {
        OnePasswordStringCredentials live =
                new OnePasswordStringCredentials("id", "desc", () -> Secret.fromString("resolved"));
        OnePasswordStringCredentials.SnapshotTaker taker = new OnePasswordStringCredentials.SnapshotTaker();
        assertSame(OnePasswordStringCredentials.class, taker.type());
        OnePasswordStringCredentials snapshot = taker.snapshot(live);
        assertEquals("resolved", snapshot.getSecret().getPlainText());
    }

    @Test
    public void usernamePasswordCredentialExposesUsernameAndSnapshotPassword() {
        OnePasswordUsernamePasswordCredentials cred = new OnePasswordUsernamePasswordCredentials(
                "id", "desc", "alice", new Snapshot<>(Secret.fromString("pw")));
        assertEquals("alice", cred.getUsername());
        assertEquals("pw", cred.getPassword().getPlainText());
    }

    @Test
    public void usernamePasswordCredentialDefaultsBlankUsername() {
        OnePasswordUsernamePasswordCredentials cred = new OnePasswordUsernamePasswordCredentials(
                "id", "desc", null, new Snapshot<>(Secret.fromString("pw")));
        assertEquals("", cred.getUsername());
    }

    @Test
    public void usernamePasswordSnapshotTakerResolvesPassword() {
        OnePasswordUsernamePasswordCredentials live = new OnePasswordUsernamePasswordCredentials(
                "id", "desc", "bob", () -> Secret.fromString("pw2"));
        OnePasswordUsernamePasswordCredentials.SnapshotTaker taker =
                new OnePasswordUsernamePasswordCredentials.SnapshotTaker();
        assertSame(OnePasswordUsernamePasswordCredentials.class, taker.type());
        OnePasswordUsernamePasswordCredentials snapshot = taker.snapshot(live);
        assertEquals("bob", snapshot.getUsername());
        assertEquals("pw2", snapshot.getPassword().getPlainText());
    }

    @Test
    public void readingAnItemWithoutOpFailsCleanly() {
        // With no op CLI / auth available the lazy read must surface a OnePasswordException rather
        // than leaking a raw process error, covering the failure branch of the accessor.
        assertThrows(OnePasswordException.class,
                () -> OnePasswordItemAccessor.read(new com.onepassword.jenkins.plugins.config.OnePasswordConfig(),
                        "op://vault/item/field"));
    }
}
