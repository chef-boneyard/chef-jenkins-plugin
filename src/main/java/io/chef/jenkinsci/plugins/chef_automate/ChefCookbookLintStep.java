package io.chef.jenkinsci.plugins.chef_automate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
public class ChefCookbookLintStep extends Step {

    private String installation;

    @DataBoundConstructor
    public ChefCookbookLintStep() {}

    @DataBoundSetter
    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getInstallation() {
        return installation;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(installation, context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "chef_cookbook_lint";
        }

        @Override
        public String getDisplayName() {
            return "Chef Cookbook Lint";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }

    public static class Execution extends SynchronousStepExecution<Boolean> {
        
        @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")
        private transient final String installation;

        Execution(String installation, StepContext context) {
            super(context);
            this.installation = installation;
        }

        @Override protected Boolean run() throws Exception {
            ChefDKInstallation chefdk = ChefDKInstallation.getInstallation(installation);
            ArgumentListBuilder command = new ArgumentListBuilder();
            command.addTokenized(chefdk.getHome() + "/foodcritic .");

            Launcher launcher =  getContext().get(Launcher.class);
            ProcStarter p = launcher.launch()
              .pwd(getContext().get(FilePath.class))
              .cmds(command)
              .stdout(getContext().get(TaskListener.class));
            return p.join() == 0;
        }

        private static final long serialVersionUID = 1L;

    }

}