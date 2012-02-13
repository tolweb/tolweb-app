/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.ListEditMap;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.page.AccessoryPageContributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseEditAboutPage extends AbstractTreehouseEditingPage implements PageBeginRenderListener, UserInjectable, 
		TreehouseInjectable, MiscInjectable {
	public abstract void setContributorsListEditMap(ListEditMap value);
	public abstract ListEditMap getContributorsListEditMap();
	public abstract void setCurrentContributor(AccessoryPageContributor value);
	public abstract AccessoryPageContributor getCurrentContributor();
    public abstract void setErrorMessage(String value);
    public abstract void setRemovedGroup(boolean value);
	public abstract AccessoryPageContributor getContributorToDelete();
	public abstract void setContributorSearchSelected(boolean value);
	public abstract boolean getContributorSearchSelected();
	public abstract boolean getRememberSelected();
	
	public static final String PROGRESS_PROPERTY = "aboutPageProgress";
	
	public void pageBeginRender(PageEvent event) {
	    initContributorsMap();	    
	}
    
	@SuppressWarnings("unchecked")
	private void initContributorsMap() {
	    ListEditMap contributorsMap = new ListEditMap();
	    Iterator it = getTreehouse().getContributors().iterator();
	    while (it.hasNext()) {
	        AccessoryPageContributor contr = (AccessoryPageContributor) it.next();
	        contributorsMap.add(Integer.valueOf(contr.getContributorId()), contr);
	    }
	    setContributorsListEditMap(contributorsMap);
	}
	
	public void synchronizeContributor(IRequestCycle cycle) {
	    ListEditMap map = getContributorsListEditMap();
	    AccessoryPageContributor contr = (AccessoryPageContributor) map.getValue();
	    if (contr == null) {
	        displaySynchError();
	    }
	    setCurrentContributor(contr);
	}
	
	public boolean doAdditionalFormProcessing(IRequestCycle cycle) {
		if (getRememberSelected()) {
		    getUsePermissionHelper().saveContributorUsePermissionDefault(getContributor(), getTreehouse(), false);
		}	    
	    if (getTreehouse().getAboutPageProgress() != MappedAccessoryPage.COMPLETE) {
	        getTreehouse().setAboutPageProgress(MappedAccessoryPage.WORK_IN_PROGRESS);
	    }
		if (getContributorToDelete() != null) {
			AccessoryPageContributor contributor = getContributorToDelete();
			getTreehouse().removeFromContributors(contributor);
			setSelectedAnchor("trhscontributors");
			return false;
		} else if (getContributorSearchSelected()) {
			ContributorSearchPage contrSearchPage = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
			contrSearchPage.doActivate(cycle, AbstractWrappablePage.TREEHOUSE_WRAPPER, "TreehouseEditAboutPage");
			return true;
		}
		return false;
	}
	
	public void removeContributor(IRequestCycle cycle) {
	}	
	
	public void contributorSearchSubmit(IRequestCycle cycle) {
	    setContributorSearchSelected(true);
	}
	
	private void displaySynchError() {
        setErrorMessage("Your form submission is out of date.  Please retry.");
        throw new PageRedirectException(this);	    
	}	
	
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}	
}
