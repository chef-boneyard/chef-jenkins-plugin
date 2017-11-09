package io.chef.jenkinsci.plugins.chef_automate;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;

/**
 * A simple echo back statement.
 */
public class ChefCookbookPublishToChefServer extends ChefCookbookStep {


    @DataBoundConstructor
    public ChefCookbookPublishToChefServer() {}

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new PublishChefServerExecution(context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "chef_cookbook_publish_chef";
        }

        @Override
        public String getDisplayName() {
            return "Chef Cookbook Publish to Chef Server";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }

    public static class PublishChefServerExecution extends ChefExecution {
        
    	PublishChefServerExecution(StepContext context) throws AbortException {
            super(context);
        }

        private static final long serialVersionUID = 1L;
        
        @Override
		protected String[] getCommands() throws AbortException {
            String cookbook_name = parseCookbookName();
			return new String [] {
	                "chef exec knife ssl fetch --server-url 'https://api.chef.io/organizations/jonmorrow'",
	                "knife cookbook upload " + cookbook_name + " --cookbook-path .. --server-url 'https://api.chef.io/organizations/jonmorrow' --key /vagrant/.chef/jmorrow.pem --user jmorrow"
	            };
		}
        
        private String parseCookbookName() throws AbortException {
         	try {
				FilePath ws = getContext().get(FilePath.class);
				FilePath md = ws.child("metadata.rb");
				if (md.exists()) {
				    Scanner scanner = new Scanner(md.read());
					while(scanner.hasNextLine()) {
						String line = scanner.nextLine();
						if(line.startsWith("name")) {
							String name = line.replace("name", "").replace("'", "").replace("\"", "").trim();
							return name;
						}
					}
				} else {
					md = ws.child("metadata.json");
					if (md.exists()) {
						JSONParser jsonParser = new JSONParser();
						JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(md.read(), "UTF-8"));
						if(jsonObject.containsKey("name")) {
							return (String) jsonObject.get("name");
						}
					}
				}
				throw new AbortException("Failed to find cookbook name.");
			} catch (IOException | InterruptedException e) {
				throw new AbortException("Failed to find cookbook name.");
			} catch (ParseException e) {
				throw new AbortException("Failed to find cookbook name.");
			}
        }
    }

}