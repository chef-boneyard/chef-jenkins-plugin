package io.chef.jenkinsci.plugins.chef_automate;

import hudson.Extension;
import hudson.model.TaskListener;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Collections;
import java.util.Set;

/**
 * A simple echo back statement.
 */
public class ChefCookbookPublishToChefServer extends ChefCookbookStep {


    @DataBoundConstructor
    public ChefCookbookPublishToChefServer() {}

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new FunctionalExecution(context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "chef_cookbook_publish";
        }

        @Override
        public String getDisplayName() {
            return "Chef Cookbook Publish to Chef Server";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }

    public static class FunctionalExecution extends ChefExecution {
        
        FunctionalExecution(StepContext context) {
            super(context);

            sCommands = new String [] {"chef exec knife ssl fetch", 
                                        "chef exec knife ssl check",
                                        "chef exec knife cookbook list",
                                        "chef exec knife cookbook upload testjenkins"
                                    };
        }

        private static final long serialVersionUID = 1L;
    }

}