package io.chef.jenkinsci.plugins.chef_automate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Collections;
import java.util.Set;

/**
 * Custom Jenkins pipeline step which performs linting for Chef
 * cookbooks using cookstyle from the ChefDK.
 */
public class ChefCookbookCookstyleStep extends Step {

    private static final long serialVersionUID = 1L;

    private String installation;

    @DataBoundConstructor
    public ChefCookbookCookstyleStep() {}

    @DataBoundSetter
    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getInstallation() {
        return installation;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {

        return new ChefCookbookCookstyleStep.Execution(installation, context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "chef_cookbook_cookstyle";
        }

        @Override
        public String getDisplayName() {
            return "Chef Cookbook Lint (Cookstyle)";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }

   public static class Execution extends SynchronousStepExecution<Void>  {

        @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")
        private transient final String installation;

        Execution(String installation, StepContext context) {

            super(context);
            this.installation = installation;
        }

        @Override protected Void run() throws Exception {
            try
            {
                ArgumentListBuilder command = new ArgumentListBuilder();
                command.addTokenized("chef exec cookstyle . --format progress");

                Launcher launcher =  getContext().get(Launcher.class);

                Launcher.ProcStarter p = launcher.launch()
                        .pwd(getContext().get(FilePath.class))
                        .cmds(command)
                        .stdout(getContext().get(TaskListener.class));
                if (p.join() != 0) {
                throw new AbortException("Chefspec Failed");
                }

            } catch (Exception ex) {

                System.out.println("Trying run Chef Cookstyle. Could not do that because of: " + ex.getLocalizedMessage());

            }
            return null;
        }
   }

 
}
