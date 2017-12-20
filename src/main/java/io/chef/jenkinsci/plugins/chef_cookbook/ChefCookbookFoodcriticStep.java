package io.chef.jenkinsci.plugins.chef_cookbook;

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
public class ChefCookbookFoodcriticStep extends ChefCookbookStep {

    @DataBoundConstructor
    public ChefCookbookFoodcriticStep() {}

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new FoodCriticExecution(context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "chef_cookbook_foodcritic";
        }

        @Override
        public String getDisplayName() {
            return "Chef Cookbook Lint (Foodcritic)";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }

    public static class FoodCriticExecution extends ChefExecution {
        
        
        FoodCriticExecution(StepContext context) {
            super(context);
        }

        protected String getCommandString() {
            return "chef exec foodcritic .";
        }

        private static final long serialVersionUID = 1L;
    }

}
