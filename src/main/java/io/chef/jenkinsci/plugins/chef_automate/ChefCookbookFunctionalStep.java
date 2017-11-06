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
public class ChefCookbookFunctionalStep extends ChefCookbookStep {


    @DataBoundConstructor
    public ChefCookbookFunctionalStep() {}

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new FunctionalExecution(context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "chef_cookbook_functional";
        }

        @Override
        public String getDisplayName() {
            return "Chef Cookbook Functional";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }

    public static class FunctionalExecution extends ChefExecution {
        
        FunctionalExecution(StepContext context) {
            super(context);
        }

        protected String getCommandString() {
            return "chef exec kitchen test --concurrency=5 --destroy=always";
        }

        private static final long serialVersionUID = 1L;
    }

}