package com.onepassword.jenkins.plugins.credentials;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Supplier helpers. {@link #memoizeWithExpiration(Supplier, Supplier)} caches a value for a TTL that
 * is itself looked up lazily, so a configuration change to the cache duration takes effect on the
 * next refresh without recreating the supplier.
 */
final class CustomSuppliers {

    private CustomSuppliers() {
    }

    static <T> Supplier<T> memoizeWithExpiration(Supplier<T> delegate, Supplier<Duration> duration) {
        return new ExpiringMemoizingSupplier<>(delegate, duration);
    }

    private static final class ExpiringMemoizingSupplier<T> implements Supplier<T> {
        private final Supplier<T> delegate;
        private final Supplier<Duration> duration;
        private transient volatile T value;
        private transient volatile long expirationNanos;

        ExpiringMemoizingSupplier(Supplier<T> delegate, Supplier<Duration> duration) {
            this.delegate = delegate;
            this.duration = duration;
        }

        @Override
        public T get() {
            long nanos = expirationNanos;
            long now = System.nanoTime();
            if (nanos == 0 || now - nanos >= 0) {
                synchronized (this) {
                    if (nanos == expirationNanos) {
                        T computed = delegate.get();
                        value = computed;
                        long ttl = Math.max(1L, duration.get().toNanos());
                        long next = now + ttl;
                        // guard against a 0 sentinel after overflow
                        expirationNanos = (next == 0) ? 1 : next;
                        return computed;
                    }
                }
            }
            return value;
        }
    }
}
