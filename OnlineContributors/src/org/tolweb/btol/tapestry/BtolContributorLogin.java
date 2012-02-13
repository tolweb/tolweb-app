package org.tolweb.btol.tapestry;

import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.btol.Project;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.tapestry.ContributorLogin;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class BtolContributorLogin extends ContributorLogin implements PageBeginRenderListener, 
		CookieInjectable, UserInjectable, ProjectInjectable, MiscInjectable {
	public static final String LOGOUT = "logout";
	
	/**
	 * This can be set by a page that went to execute an external callback
	 * after logging in
	 * @return
	 */
	@Persist("flash")
	public abstract Object[] getExternalParameters();
	public abstract void setExternalParameters(Object[] params);
	/**
	 * This can be set by a page that went to execute an external callback
	 * after logging in
	 * @return
	 */	
	@Persist("flash")
	public abstract String getExternalPageName();
	public abstract void setExternalPageName(String value);


	@InjectPage("btol/ProjectHome")
	public abstract ProjectHome getProjectHomePage();	
	
	public Object[] getProjectHomeExternalParameters() {
		Long projectId = getProjectHelper().getProjectIdFromRequest();
		if (projectId == null) {
			projectId = 1L;
		}
		return new Object[] { projectId };
	}
	
	public String getDestinationPageName() {
		if (StringUtils.notEmpty(getExternalPageName())) {
			return getExternalPageName();
		} else {
			return "btol/ProjectHome";
		}
	}
	public Object[] getExternalPageParameters() {
		if (getExternalParameters() != null) {
			return getExternalParameters();
		} else {
			return getProjectHomeExternalParameters();
		}
	}

	public String getProjectName() {
		String projectName = getProjectHelper().getProjectNameFromRequest();
		return projectName;
	}

	public void pageBeginRender(PageEvent evt) {
		Boolean isLogout = (Boolean) evt.getRequestCycle().getAttribute(BtolContributorLogin.LOGOUT);
		// only attempt this if the user didn't just logout
		if (isLogout == null || !isLogout) {
			Contributor contr = getCookieAndContributorSource().getContributorFromAuthCookie();
			if (contr != null) {
				Project project = getProjectHelper().getProjectFromRequest();
				// if they aren't a member, they don't get access
				if (project.getContributorCanViewProject(contr)) {
					// redirect to the project home
					setContributor(contr);
					getCookieAndContributorSource().loginProject(project);
	            	String url = getTapestryHelper().getExternalServiceUrl("btol/ProjectHome", getProjectHomeExternalParameters());				
	            	setRedirectURL(url);
	            	throw new RedirectException(url);
					/*ExternalCallback callback = new ExternalCallback(homePage, getExternalPageParameters());
					callback.performCallback(getRequestCycle());
					throw new PageRedirectException(homePage);*/
				}
			}
		}
	}
}
