package com.onepassword.jenkins.plugins.credentials;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * A trivial serializable {@link Supplier} that holds an already-resolved value. Used by the
 * {@code CredentialsSnapshotTaker}s to replace a live, 1Password-backed supplier with a plain value
 * before a credential is sent to a build agent (which has no access to the {@code op} CLI).
 */
final class Snapshot<T extends Serializable> implements Supplier<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private final T value;

    Snapshot(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
