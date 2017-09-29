package io.chef.jenkinsci.plugins.chef_automate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import java.util.Collections;
import java.util.Set;

/**
 * A simple echo back statement.
 */
public class ChefCookbookUnitStep extends Step {

    private final String message;

    @DataBoundConstructor
    public ChefCookbookUnitStep(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(message, context);
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

    public static class Execution extends SynchronousStepExecution<Void> {
        
        @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")
        private transient final String message;

        Execution(String message, StepContext context) {
            super(context);
            this.message = message;
        }

        @Override protected Void run() throws Exception {
            getContext().get(TaskListener.class).getLogger().println(message);
            return null;
        }

        private static final long serialVersionUID = 1L;

    }

}