/*
 * Created on Jul 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.Student;
import org.tolweb.misc.NodeHelper;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * Provides a Contributor Profile page that provides details 
 * about the contributions of a particular contributor. 
 * 
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ContributorDetailPage extends BasePage implements IExternalPage, PageInjectable, 
		ImageInjectable, AccessoryInjectable, CookieInjectable, UserInjectable, NodeInjectable, BaseInjectable {
    @Persist("session")
	public abstract Contributor getViewedContributor();	
	public abstract void setViewedContributor(Contributor value);
	public abstract void setPreviousNodeId(Long nodeId);
	public abstract void setNumImages(int value);
	public abstract void setHasProjects(boolean value);

	public abstract List<String> getAssociatedGroups();
	public abstract void setAssociatedGroups(List<String> groups);
	
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        try {
        	if (parameters != null && parameters.length > 0) {
	            int contributorId = ((Number) parameters[0]).intValue();
	            initializeContributor(contributorId);
	            if (parameters.length > 1) {
	            	Long nodeId = (Long) parameters[1];
	            	setPreviousNodeId(nodeId);
	            }	            
        	} else {
        		initializeContributor(Contributor.BLANK_CONTRIBUTOR.getId());
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	setViewedContributor(null);
        	setAssociatedGroups(new ArrayList());
        }
    }
	private void initializeContributor(int contributorId) {
		ContributorDAO contrDao = getContributorDAO(); 
		Contributor contr = contrDao.getContributorWithId("" + contributorId);
		setViewedContributor(contr);
		if (contr != null) {
			setNumImages(getImageDAO().getNumImagesForContributor(contr));
		}
		setHasProjects(contrDao.getNumProjectsForContributor(contr) > 0);
		// adding the ability to show the "associated" groups - for sci-contributors, 
		// that's the 'assigned groups', 'groups of interest' for all others
		List nodeIds = contrDao.getNodeIdsForContributor(contr);
		List<String> groups = NodeHelper.getNodeNameList(getMiscNodeDAO().getNodesWithIds(nodeIds));
		setAssociatedGroups(groupNameLinks(groups));
	}

    /**
     * Returns a HTML anchored list of group names associated  
     * with a contributor.
     * @param groups a list of group names 
     * @return a list of group names with appropriate HTML anchors (links)
     */
	private List<String> groupNameLinks(List<String> groups) {
		List<String> linkedUp = new ArrayList<String>();
		for (String group : groups) {
			linkedUp.add(getUrlBuilder().getPublicLinkForBranchPage(group));
		}
		return linkedUp;		
	}
	
	/**
	 * Returns a comma separated list of HTML anchored, group names 
	 * associated with a contributor
     * @return a string of group names with appropriate HTML anchors (links)
	 */
	public String getAssociatedGroupString() {
		return StringUtils.returnCommaAndJoinedString(getAssociatedGroups());
	}
    
    public String getTitle() {
        if (getViewedContributor() != null) {
            return getViewedContributor().getDisplayName() + " Tree of Life Contributor Profile ";
        } else {
            return "No Contributor Found";
        }
    }
    
    public String getContributorTypeString() {
    	String typeString = getViewedContributor().getContributorTypeString();
    	if (getViewedContributor().getContributorType() != Contributor.OTHER_SCIENTIST) {
    		typeString += " Contributor"; 
    	}
    	return typeString;
    }
    
    @SuppressWarnings("unchecked")
    public boolean getHasPublishedPages() {
    	List groupNames = getPublicPageDAO().getGroupNamesContributorOwns(getViewedContributor());
    	List treehouseNames = getPublicAccessoryPageDAO().getTreehouseTitlesIdsForContributor(getViewedContributor());
    	List articleNotes = getPublicAccessoryPageDAO().getArticleNoteTitlesIdsForContributor(getViewedContributor());
    	return groupNames.size() > 0 || treehouseNames.size() > 0 || articleNotes.size() > 0;
    }

    public String getAttachedGroupsHeading() {
    	byte contrType = getViewedContributor().getContributorType();
    	if (contrType == Contributor.SCIENTIFIC_CONTRIBUTOR) {
    		return "Assigned Groups";
    	}
    	return "Groups of Interest";
    }
    
    public boolean getHasSubjectAreasOfInterest() {
    	return StringUtils.notEmpty(getViewedContributor().getInterestsString());
    }
    public boolean getHasGeographicAreasOfInterest() {
    	return StringUtils.notEmpty(getViewedContributor().getGeographicAreaInterest());
    }
    public boolean getCanEditInfo() {
    	Contributor visitContr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
    	return visitContr != null && getPermissionChecker().checkHasEditingPermissionForContributor(visitContr, getViewedContributor());
    }
    public void editInfo(IRequestCycle cycle) {
    	Contributor contrToEdit = getContributorDAO().getContributorWithId((Number) cycle.getListenerParameters()[0]);
    	Contributor visitContr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
    	
    	if (contrToEdit != null && (contrToEdit.isScientificContributor() || contrToEdit.isGeneralContributor())) {
	    	ScientificContributorRegistration editPage = null;
	    	String editPageName;
	    	if (visitContr.getId() == contrToEdit.getId()) {
	    		editPageName = "ScientificContributorRegistration";
	    	} else {
	    		editPageName = "ScientificContributorRegistrationOther";
	    	}
	    	editPage = (ScientificContributorRegistration) cycle.getPage(editPageName);
	    	editPage.clearPersistentProperties();
	    	editPage.setEditedContributor(contrToEdit);
	    	editPage.setFromProfilePage(true);
	    	cycle.activate(editPage);
    	} else if (contrToEdit != null && contrToEdit.isTreehouseContributor()) {
    		TreehouseContributorRegistration editPage = (TreehouseContributorRegistration) cycle.getPage("TreehouseContributorRegistration");
    		editPage.clearPersistentProperties();
    		editPage.setEditedContributor(contrToEdit);
    		cycle.activate(editPage);
    	} else {
    		ImageContributorRegistration editPage = (ImageContributorRegistration) cycle.getPage("ImageContributorRegistration");
    		editPage.clearPersistentProperties();
    		editPage.setEditedContributor(contrToEdit);
    		cycle.activate(editPage);
    	}
    }
    public boolean getIsStudent() {
    	return Student.class.isInstance(getViewedContributor());
    }
    public int getNumImages() {
    	return getImageDAO().getNumImagesForContributor(getViewedContributor());
    }
}
