package org.tolweb.hivemind;

import org.tolweb.btol.Project;
import org.tolweb.btol.dao.ProjectDAO;
import org.tolweb.treegrow.main.StringUtils;

public class ProjectHelperImpl implements ProjectHelper {
	private TapestryHelper tapestryHelper;
	private ProjectDAO projectDAO;
	
	public Project getProjectFromRequest() {
		String host = getHost();
		if (StringUtils.notEmpty(host)) {
			return getProjectDAO().getProjectWithDomain(host);
		} else {
			return null;
		}
	}
	public String getProjectNameFromRequest() {
		String host = getHost(); 
		if (StringUtils.notEmpty(host)) {		
			return getProjectDAO().getProjectNameWithDomain(host);
		} else {
			return null;
		}
		
	}
	public Long getProjectIdFromRequest() {
		String host = getHost();
		if (StringUtils.notEmpty(host)) {		
			return getProjectDAO().getProjectIdWithDomain(host);
		} else {
			return null;
		}
		
	}
	public Long getProjectIdOrDefault() {
		Long projectId = getProjectIdFromRequest();
		if (projectId == null) {
			projectId = 1L;
		}
		return projectId;
	}
	private String getHost() {
		return getTapestryHelper().getDomainName();
	}

	public TapestryHelper getTapestryHelper() {
		return tapestryHelper;
	}

	public void setTapestryHelper(TapestryHelper tapestryHelper) {
		this.tapestryHelper = tapestryHelper;
	}
	public ProjectDAO getProjectDAO() {
		return projectDAO;
	}
	public void setProjectDAO(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}
}
