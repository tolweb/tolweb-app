package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.SortedSet;

import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectComponent;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.Permission;
import org.tolweb.treegrow.main.UsePermissable;
import org.tolweb.treegrow.page.PageContributor;

public abstract class EditPageContributors extends AbstractPageEditingPage implements UserInjectable, MiscInjectable {
    public abstract void setAddContributorSelected(boolean value);
    public abstract boolean getAddContributorSelected();
    public abstract void setNewlyApprovedContributor(Contributor contr);
	public abstract boolean getRememberSelected();
    @InjectComponent("usePerms")
    public abstract UsePermissionRadios getUsePermissionRadios();	
	
	
    public void searchForContributor(IRequestCycle cycle) {
    	setAddContributorSelected(true);
    }
    public void doSave(IRequestCycle cycle) {
        super.doSave(cycle);
        if (getAddContributorSelected()) {
            ContributorSearchPage search = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
            search.doActivate(cycle, AbstractWrappablePage.NEW_FORM_WRAPPER, getPageName(), (byte)0, getNode().getNodeId(), Contributor.ACCESSORY_CONTRIBUTOR, false, true);
        }
		if (getRememberSelected()) {
		    getUsePermissionHelper().saveContributorUsePermissionDefault(getContributor(), getTolPage(), false);
		}
    }
    @SuppressWarnings("unchecked")
    public void addContributor(PageContributor contr) {
        SortedSet contrs = getTolPage().getContributors();
        getReorderHelper().addToSet(contrs, contr);
        savePage();
        // if the contributor was previously not approved, go ahead and approve them
        Contributor newContributor = contr.getContributor();
        if (!newContributor.getIsApproved()) {
        	Contributor editingContributor = getContributor();
        	ContributorDAO dao = getContributorDAO();
        	EditNewTreehouseContributors.approveContributor(newContributor, editingContributor, dao);
        	setNewlyApprovedContributor(newContributor);
        	// also need to add this node to the set of nodes that the contr can edit
        	Permission nodePermission = new Permission(getNode().getNodeId().intValue(), getNode().getName(), newContributor);
        	newContributor.addToPermissions(nodePermission);
        	getPermissionChecker().updatePermissionsForContributor(newContributor);
        }
    }
    public void useFirstAuthorCopyrightOwnersLicenseDefault() {
    	if (getTolPage().getContributors() != null && !getTolPage().getContributors().isEmpty()) {
    		
    		Byte useDefault = getFirstAuthorCopyrightOwnersLicenseDefault();
    		if (useDefault != null) {
    			byte b = useDefault.byteValue();
    			getUsePermissionRadios().setRadiosSelection(b);
    			//getUsePermissionRadios().setUsePermissionSelection(b);
    		}
    	}
    }
    @SuppressWarnings("unchecked")
    private Byte getFirstAuthorCopyrightOwnersLicenseDefault() {
    	for (Iterator itr = getTolPage().getContributors().iterator(); itr.hasNext(); ) {
    		PageContributor pageContr = (PageContributor)itr.next();
    		if (pageContr.isAuthor() && pageContr.isContact()) {
    			return pageContr.getContributor().getNoteUseDefault();
    		}
    	}
    	return null;
    }
    
    public IRender getDelegate() {
        return new BaseHTMLEditorDelegate();
    }
    @SuppressWarnings("unchecked")
    protected SortedSet getOrderedCollection() {
        return getTolPage().getContributors();
    }
    
    public UsePermissable getMappedPage() {
    	return getTolPage();
    }
}
