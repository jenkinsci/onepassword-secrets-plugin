package com.onepassword.jenkins.plugins.config;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.ExtensionPoint;
import hudson.model.Item;

public abstract class OnePasswordConfigResolver implements ExtensionPoint {
    @NonNull
    public abstract OnePasswordConfig forJob(@NonNull Item job);

}
