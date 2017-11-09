package io.chef.jenkinsci.plugins.chef_automate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.FilePath;
import hudson.Launcher;
import hudson.AbortException;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundSetter;
import hudson.EnvVars;

/**
 * Custom Jenkins pipeline step which performs linting for Chef
 * cookbooks using cookstyle from the ChefDK.
 */
public abstract class ChefCookbookStep extends Step {

    private static final long serialVersionUID = 1L;

    protected String m_sInstallation;

    @DataBoundSetter
    public void setInstallation(String sInstallation) {
        this.m_sInstallation = sInstallation;
    }

    public String getInstallation() {
        return m_sInstallation;
    }

    // @Override
    // public abstract StepExecution start(StepContext context) throws Exception;

    abstract public static class ChefExecution extends SynchronousStepExecution<Void> {

        @SuppressFBWarnings(value="SE_TRANSIENT_FIELD_NOT_RESTORED", justification="Only used when starting.")

        abstract protected String [] getCommands() throws AbortException;

        ChefExecution(StepContext context) {
            super(context);
        }

        @Override protected Void run() throws Exception {

            String []sCommArr = getCommands();

            if (sCommArr == null || sCommArr.length == 0)
                throw new AbortException("No command to issue");

            for (String sCommand : sCommArr)
            {
                ArgumentListBuilder command = new ArgumentListBuilder();
                command.addTokenized(sCommand);

                StepContext context = getContext();
                Launcher launcher =  context.get(Launcher.class);

                Launcher.ProcStarter p = launcher.launch()
                        .pwd(getContext().get(FilePath.class))
                        .cmds(command)
                        .stdout(getContext().get(TaskListener.class));

                int iRetCode = p.join();
                if (iRetCode != 0) {
                    //System.out.println("Failed to run command: [" + sCommand + "]");
                    throw new AbortException("Failed to run command: [" + sCommand + "] Exit code: " + iRetCode);
                }
            }          
            return null;
        }

        private static final long serialVersionUID = 1L;
    }
}
