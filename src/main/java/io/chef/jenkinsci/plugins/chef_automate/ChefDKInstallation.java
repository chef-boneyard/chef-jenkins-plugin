package io.chef.jenkinsci.plugins.chef_automate;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

public class ChefDKInstallation extends ToolInstallation 
        implements EnvironmentSpecific<ChefDKInstallation>, NodeSpecific<ChefDKInstallation>, Serializable {

	private static final long serialVersionUID = 1L;

	@DataBoundConstructor
	public ChefDKInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
		super(name, home, properties);
	}

	@Override
	public ChefDKInstallation forNode(Node node, TaskListener log) throws IOException, InterruptedException {
		return new ChefDKInstallation(getName(), translateFor(node, log), getProperties().toList());
	}

	@Override
	public ChefDKInstallation forEnvironment(EnvVars environment) {
		return new ChefDKInstallation(getName(), environment.expand(getHome()), getProperties().toList());
	}
	
	public static ChefDKInstallation[] allInstallations() {
		ChefDKInstallation.DescriptorImpl chefDKDescriptor = Jenkins.getInstance().getDescriptorByType(ChefDKInstallation.DescriptorImpl.class);
        return chefDKDescriptor.getInstallations();
	}
	
	 public static ChefDKInstallation getInstallation(String chefDKInstallation) throws IOException {
		 ChefDKInstallation[] installations = allInstallations();
        if (chefDKInstallation == null) {
            if (installations.length == 0) {
                throw new IOException("ChefDK not found");
            }
            return installations[0];
        } else {
            for (ChefDKInstallation installation: installations) {
                if (chefDKInstallation.equals(installation.getName())) {
                    return installation;
                }
            }
        }
        throw new IOException("ChefDK not found");
    }

	@Extension @Symbol("chefdk")
    public static class DescriptorImpl extends ToolDescriptor<ChefDKInstallation> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            super.configure(req, json);
            save();
            return true;
        }

        @Override
        public String getDisplayName() {
            return "ChefDK";
        }
        
        public ListBoxModel doFillInstallationItems() {
            ListBoxModel model = new ListBoxModel();
            for (ChefDKInstallation tool : ChefDKInstallation.allInstallations()) {
                model.add(tool.getName());
            }
            return model;
        }
    }
}
