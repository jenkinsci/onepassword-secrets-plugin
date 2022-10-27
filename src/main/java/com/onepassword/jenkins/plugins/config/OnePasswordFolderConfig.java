package com.onepassword.jenkins.plugins.config;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.ItemGroup;
import org.kohsuke.stapler.DataBoundConstructor;

public class OnePasswordFolderConfig extends AbstractFolderProperty<AbstractFolder<?>> {

    private final OnePasswordConfig config;

    public OnePasswordFolderConfig() {
        this.config = null;
    }

    @DataBoundConstructor
    public OnePasswordFolderConfig(OnePasswordConfig config) {
        this.config = config;
    }

    public OnePasswordConfig getConfig() {
        return config;
    }

    @Extension
    public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

    }

    @Extension(ordinal = 100)
    public static class ForJob extends OnePasswordConfigResolver {

        @NonNull
        @Override
        public OnePasswordConfig forJob(@NonNull Item job) {
            OnePasswordConfig resultingConfig = null;
            for (ItemGroup g = job.getParent(); g instanceof AbstractFolder;
                 g = ((AbstractFolder) g).getParent()) {
                OnePasswordFolderConfig folderProperty = ((AbstractFolder<?>) g).getProperties()
                        .get(OnePasswordFolderConfig.class);
                if (folderProperty == null) {
                    continue;
                }
                if (resultingConfig != null) {
                    resultingConfig = resultingConfig
                            .mergeWithParent(folderProperty.getConfig());
                } else {
                    resultingConfig = folderProperty.getConfig();
                }
            }

            return resultingConfig;
        }
    }
}
