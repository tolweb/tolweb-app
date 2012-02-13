/*
 * Created on Aug 18, 2004
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author dmandel
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Configuration {
    private String learningEditorEmail;
    private String notesEditorEmail;
    private String templatesDirectory;
    private String smtpHost;
    private Collection<String> notesEditorEmails;
    private boolean useExternalStylesheets;
    private String hostPrefix;
    private boolean isBtol;
    private boolean useNewTreeDrawing;
    private String searchUrl;
    
	public String getLearningEditorEmail() {
        return learningEditorEmail;
    }
    public void setLearningEditorEmail(String learningEditorEmail) {
        this.learningEditorEmail = learningEditorEmail;
    }

    /**
     * @return Returns the notesEditorEmail.
     */
    public String getNotesEditorEmail() {
        return notesEditorEmail;
    }
    /**
     * @param notesEditorEmail The notesEditorEmail to set.
     */
    public void setNotesEditorEmail(String notesEditorEmail) {
        this.notesEditorEmail = notesEditorEmail;
        setNotesEditorEmails(Arrays.asList(notesEditorEmail.split(",")));
    }
    /**
     * @return Returns the notesEditorEmails.
     */
    public Collection<String> getNotesEditorEmails() {
        return notesEditorEmails;
    }
    /**
     * @param notesEditorEmails The notesEditorEmails to set.
     */
    public void setNotesEditorEmails(Collection<String> notesEditorEmails) {
        this.notesEditorEmails = notesEditorEmails;
    }
    /**
     * @return Returns the templatesDirectory.
     */
    public String getTemplatesDirectory() {
        return templatesDirectory;
    }
    /**
     * @param templatesDirectory The templatesDirectory to set.
     */
    public void setTemplatesDirectory(String templatesDirectory) {
        this.templatesDirectory = templatesDirectory;
    }
	public String getSmtpHost() {
		return smtpHost;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public boolean getUseExternalStylesheets() {
		return useExternalStylesheets;
	}
	public void setUseExternalStylesheets(boolean useExternalStylesheets) {
		this.useExternalStylesheets = useExternalStylesheets;
	}
	public String getHostPrefix() {
		return hostPrefix;
	}
	public void setHostPrefix(String hostPrefix) {
		this.hostPrefix = hostPrefix;
	}
	public boolean getIsBtol() {
		return isBtol;
	}
	public void setIsBtol(boolean isBtol) {
		this.isBtol = isBtol;
	}
	public boolean isUseNewTreeDrawing() {
		return useNewTreeDrawing;
	}
	public void setUseNewTreeDrawing(boolean useNewTreeDrawing) {
		this.useNewTreeDrawing = useNewTreeDrawing;
	}
    public String getSearchUrl() {
		return searchUrl;
	}
	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}	
}
