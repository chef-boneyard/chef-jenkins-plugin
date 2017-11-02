package io.chef.jenkinsci.plugins.chef_automate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

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
        return new UnitExecution(m_sInstallation, context);
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
        
         UnitExecution(String installation, StepContext context) {
            super(installation, context);
        }

        protected String getCommandString() {
            return "chef exec rspec --format progress --format RspecJunitFormatter --output rspec_junit.xml";
        }

    }

}