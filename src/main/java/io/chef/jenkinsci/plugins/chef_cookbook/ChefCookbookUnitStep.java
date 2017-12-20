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
public class ChefCookbookUnitStep extends ChefCookbookStep {

    @DataBoundConstructor
    public ChefCookbookUnitStep() {}

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new UnitExecution(context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "chef_cookbook_unit";
        }

        @Override
        public String getDisplayName() {
            return "Chef Cookbook Unit";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }

    public static class UnitExecution extends ChefExecution {
        
         UnitExecution(StepContext context) {
            super(context);
        }

        protected String getCommandString() {
            return "chef exec rspec --format progress --format RspecJunitFormatter --out rspec_junit.xml";
        }

        private static final long serialVersionUID = 1L;
    }

}
