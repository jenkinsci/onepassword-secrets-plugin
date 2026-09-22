package com.onepassword.jenkins.plugins.credentials;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.CredentialsStoreAction;
import com.cloudbees.plugins.credentials.domains.Domain;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.ModelObject;
import hudson.security.ACL;
import hudson.security.Permission;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;

import java.util.Collections;
import java.util.List;

/**
 * The read-only {@link CredentialsStore} backing {@link OnePasswordCredentialsProvider}. Credentials
 * live in 1Password, so every mutating operation is unsupported; only {@link CredentialsProvider#VIEW}
 * is granted.
 */
public class OnePasswordCredentialsStore extends CredentialsStore {

    private final OnePasswordCredentialsProvider provider;
    private final OnePasswordCredentialsStoreAction action = new OnePasswordCredentialsStoreAction();

    OnePasswordCredentialsStore(OnePasswordCredentialsProvider provider) {
        super(OnePasswordCredentialsProvider.class);
        this.provider = provider;
    }

    @NonNull
    @Override
    public ModelObject getContext() {
        return Jenkins.get();
    }

    @Override
    public boolean hasPermission(@NonNull Authentication a, @NonNull Permission permission) {
        // Read-only store: only VIEW is ever granted, and only if the user has it on Jenkins.
        return CredentialsProvider.VIEW.equals(permission) && Jenkins.get().getACL().hasPermission(a, permission);
    }

    @NonNull
    @Override
    public List<Credentials> getCredentials(@NonNull Domain domain) {
        if (Domain.global().equals(domain)) {
            return provider.getCredentials(Credentials.class, Jenkins.get(), ACL.SYSTEM);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean addCredentials(@NonNull Domain domain, @NonNull Credentials credentials) {
        throw new UnsupportedOperationException("The 1Password credentials store is read-only; manage items in 1Password.");
    }

    @Override
    public boolean removeCredentials(@NonNull Domain domain, @NonNull Credentials credentials) {
        throw new UnsupportedOperationException("The 1Password credentials store is read-only; manage items in 1Password.");
    }

    @Override
    public boolean updateCredentials(@NonNull Domain domain, @NonNull Credentials current, @NonNull Credentials replacement) {
        throw new UnsupportedOperationException("The 1Password credentials store is read-only; manage items in 1Password.");
    }

    @NonNull
    @Override
    public CredentialsStoreAction getStoreAction() {
        return action;
    }

    /** Surfaces the store under Manage Jenkins &rarr; Credentials. */
    public final class OnePasswordCredentialsStoreAction extends CredentialsStoreAction {

        private static final String ICON_CLASS = "symbol-lock";

        private OnePasswordCredentialsStoreAction() {
        }

        @NonNull
        @Override
        public CredentialsStore getStore() {
            return OnePasswordCredentialsStore.this;
        }

        @Override
        public String getIconClassName() {
            return isVisible() ? ICON_CLASS : null;
        }

        @Override
        public String getDisplayName() {
            return "1Password";
        }
    }
}
