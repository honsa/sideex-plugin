package sideex;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import jenkins.org.apache.commons.validator.routines.UrlValidator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wagu.Block;
import wagu.Board;

public class HTTPSEnableItem extends BuildDropDownList {
	private String baseURL;
	private String caFilePath;

    @DataBoundConstructor
    public HTTPSEnableItem(String baseURL, String caFilePath) throws Exception {
    	this.baseURL = StringUtils.trim(baseURL);
		this.caFilePath = StringUtils.trim(caFilePath);
		
		if (this.baseURL.charAt(this.baseURL.length() - 1) != '/') {
			this.baseURL = this.baseURL + "/";
		}
    }
    
    @Override
    public SideeXWebServiceClientAPI getClientAPI(@Nonnull Run<?, ?> build, @Nonnull TaskListener listener,
			String baseURL, ProtocalType type) throws InterruptedException, IOException {
		SideeXWebServiceClientAPI clientAPI = new SideeXWebServiceClientAPI(baseURL, type);
		
		return clientAPI;
	}

    @Extension
    public static class DescriptorImpl extends BuildDropDownListDescriptor {
        @Override
        public String getDisplayName() {
            return "HTTPS (Enable certificate checking)";
        }

        @Override
        public boolean isApplicableAsBuildStep() {
            return true;
        }

		public FormValidation doCheckBaseURL(@QueryParameter String baseURL) {
			try {
				UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
				if(!urlValidator.isValid(baseURL)) {
					throw new Exception("Invalid base URL");
				}
				if(!(new URL(baseURL).getProtocol().equals("https"))) {
					throw new Exception("Invalid protocal");
				}
				return FormValidation.ok();
			} catch (Exception e) {
				return FormValidation.error(e.getMessage());
			}
		}
		
		public FormValidation doCheckCaFilePath(@QueryParameter String caFilePath) {
			try {
				File caFile = new File(caFilePath);
				if(caFile.isDirectory() || !caFile.exists()) {
					throw new Exception();
				}
				return FormValidation.ok();
			} catch (Exception e) {
				return FormValidation.error("Please enter a certificate file path");
			}
		}
    }
	
	public String getBaseURL() {
		return baseURL;
	}
	
	public String getCaFilePath() {
		return caFilePath;
	}
}
