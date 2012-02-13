package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.IRequestCycle;

public abstract class ViewContributorProjectsPage extends ContributorDetailPage {
	@SuppressWarnings("unchecked")
	public abstract void setProjects(List value);
	@SuppressWarnings("unchecked")
	public abstract List getProjects();
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		super.activateExternalPage(parameters, cycle);
		setProjects(getContributorDAO().getProjectsForContributor(getViewedContributor()));
	}
	public String getTitle() {
		if (getViewedContributor() != null) {
			return "Classroom Projects for Contributor " + getViewedContributor().getDisplayName();
		} else {
			return "";
		}
	}
}
