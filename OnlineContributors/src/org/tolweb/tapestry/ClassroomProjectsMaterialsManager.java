package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ClassroomProjectsMaterialsManager extends AbstractTreehouseContributorPage implements PageBeginRenderListener, UserInjectable, 
		TreehouseInjectable, BaseInjectable {
	public abstract String getProjectTitle();
	public abstract Long getProjectId();
	public abstract String getTreehouseTitle();
	public abstract Long getTreehouseProjectId();
	public abstract String getStudentName();
	public abstract void setSearchProjects(boolean value);
	public abstract boolean getSearchProjects();
	public abstract void setSearchTreehouses(boolean value);
	public abstract boolean getSearchTreehouses();
	@SuppressWarnings("unchecked")
	public abstract List getProjects();
	@SuppressWarnings("unchecked")
	public abstract void setProjects(List value);
	@SuppressWarnings("unchecked")
	public abstract List getTreehouses();
	@SuppressWarnings("unchecked")
	public abstract void setTreehouses(List value);
	public abstract void setShowAllProjectsLink(boolean value);
	public abstract void setShowAllProjectsClicked(boolean value);
	public abstract MappedAccessoryPage getCurrentTreehouse();
	
	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding()) {
			if (getProjects() == null) {
		        Contributor contr = getContributor();
		        if (contr.getIsLearningEditor()) {
		            setProjects(getContributorDAO().getAllActiveProjects());            
		        } else {
		            setProjects(getContributorDAO().getProjectsForContributor(contr));
		        }
		        setShowAllProjectsLink(false);
			}
		}
	}
		
    public void registerNewProject(IRequestCycle cycle) {
    	// clear the cached results since we don't want to show bad data
    	setProjects(null);    	
        ClassroomRegistrationPage page = (ClassroomRegistrationPage) cycle.getPage("ClassroomRegistrationPage");
        page.registerNewProject(cycle);
    }
    public void editRegistrationInfo(IRequestCycle cycle) {
        TreehouseContributorRegistration registrationPage = (TreehouseContributorRegistration) cycle.getPage("TreehouseContributorRegistration");
        registrationPage.setEditedContributor(getContributor());
        cycle.activate(registrationPage);
    }
    public void closeProject(IRequestCycle cycle) {
    	// clear the cached results since we don't want to show bad data
    	setProjects(null);
        Long projectId = Long.valueOf(((Number) cycle.getListenerParameters()[0]).intValue());
        ClassroomProject project = getContributorDAO().getProjectWithId(projectId);
        CloseProjectConfirm confirmPage = (CloseProjectConfirm) cycle.getPage("CloseProjectConfirm");
        confirmPage.setProjectId(projectId);
        confirmPage.setProject(project);
        cycle.activate(confirmPage);
    }
    
    @SuppressWarnings("unchecked")
    public void doSearch(IRequestCycle cycle) {
    	ContributorDAO dao = getContributorDAO();
		Contributor contr = getContributor();    	
    	if (getSearchProjects()) {
    		String projectTitle = getProjectTitle();
    		Long projectId = getProjectId();
    		boolean hasTitle = StringUtils.notEmpty(projectTitle);
    		boolean hasProjectId = projectId != null; 
    		if (hasTitle || hasProjectId) {
    			Hashtable args = new Hashtable();
    			if (hasTitle) {
    				args.put(ContributorDAO.NAME, projectTitle);
    			}
    			if (hasProjectId) {
    				args.put(ContributorDAO.ID, projectId);
    			}
    			if (!contr.getIsLearningEditor()) {
    				args.put(ContributorDAO.CONTRIBUTOR, contr);
    			}
    			setProjects(dao.getProjectsMatchingCriteria(args));
    			setTreehouses(new ArrayList());
    		}
    	} else {
    		String treehouseTitle = getTreehouseTitle();
    		Long treehouseProjectId = getTreehouseProjectId();
    		String studentName = getStudentName();
    		boolean hasTitle = StringUtils.notEmpty(treehouseTitle);
    		boolean hasId = treehouseProjectId != null && treehouseProjectId.intValue() > 0;
    		boolean hasName = StringUtils.notEmpty(studentName);
    		if (hasTitle || hasId || hasName) {
    			boolean doOtherSearch = true;
				List contributorIds = new ArrayList();    			
    			if (hasName || hasId) {
    				Contributor toSearchOn = contr.getIsLearningEditor() ? null : contr;
    				contributorIds = dao.getStudentIdsWithLastNameOrAlias(studentName, toSearchOn, treehouseProjectId);
    				if (contributorIds.size() == 0) {
    					doOtherSearch = false;
    				}
    			}
    			if (doOtherSearch || hasTitle) {
    				Hashtable args = new Hashtable();
    				if (hasTitle) {
    					args.put(AccessoryPageDAO.TITLE, treehouseTitle);
    				}
    				if (contributorIds.size() > 0) {
    					args.put(AccessoryPageDAO.CONTRIBUTOR_IDS, contributorIds);
    				}
    				List treehouses = getWorkingAccessoryPageDAO().getAccessoryPagesMatchingCriteria(args);
					// clear the projects
					setProjects(new ArrayList());
    				setTreehouses(treehouses);
    			}
    		}
    	}
    	setShowAllProjectsLink(true);
    }
    public void showAllProjects(IRequestCycle cycle) {
    	// clear the stored results and go back to showing all the projects
    	setProjects(null);
    	setTreehouses(null);
    	setShowAllProjectsClicked(true);
    }
    public void findProject(IRequestCycle cycle) {
    	setSearchProjects(true);
    }
    public void findTreehouse(IRequestCycle cycle) {
    	setSearchTreehouses(true);
    }
    public String getCurrentWorkingUrl() {
    	return getUrlBuilder().getWorkingURLForObject(getCurrentTreehouse());
    }
}
