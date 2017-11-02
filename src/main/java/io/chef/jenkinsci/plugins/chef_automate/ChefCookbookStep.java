package io.chef.jenkinsci.plugins.chef_automate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
public abstract class ChefCookbookStep extends Step {

    private static final long serialVersionUID = 1L;

    protected String m_sInstallation;

    protected static String s_sFuncName;
    protected static String s_sDisplayName;

//    @DataBoundConstructor
//    public ChefCookbookStep() {}

    @DataBoundSetter
    public void setInstallation(String sInstallation) {
        this.m_sInstallation = sInstallation;
    }

    public String getInstallation() {
        return m_sInstallation;
    }

    @Override
    public abstract StepExecution start(StepContext context) throws Exception;

    // @Extension
    // public static class DescriptorImpl extends StepDescriptor {

    //     @Override
    //     public String getFunctionName() {
    //         return "chef_cookbook";
    //     }

    //     @Override
    //     public String getDisplayName() {
    //         return "Chef Cookbook";
    //     }

    //     @Override
    //     public Set<? extends Class<?>> getRequiredContext() {
    //         return Collections.singleton(TaskListener.class);
    //     }
    // }

    public static void getArgumentList(ArgumentListBuilder alb);

    public static class Execution extends SynchronousStepExecution<Void> {

        @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")
        private transient final String installation;

        Execution(String installation, StepContext context) {

            super(context);
            this.installation = installation;
        }

        @Override protected Void run() throws Exception {

            ChefDKInstallation chefdk = ChefDKInstallation.getInstallation(installation);

            ArgumentListBuilder command = new ArgumentListBuilder();

            ChefCookbookStep.getArgumentList(command);

            command.addTokenized(chefdk.getHome() + "/cookstyle .");

            Launcher launcher =  getContext().get(Launcher.class);

            Launcher.ProcStarter p = launcher.launch()
                    .pwd(getContext().get(FilePath.class))
                    .cmds(command)
                    .stdout(getContext().get(TaskListener.class));

            return null;
        }

        private static final long serialVersionUID = 1L;
    }
}
