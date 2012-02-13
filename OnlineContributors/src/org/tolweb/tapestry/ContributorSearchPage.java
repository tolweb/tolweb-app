/*
 * Created on Jun 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ContributorSearchPage extends AbstractContributorSearchOrResultsPage implements IExternalPage, UserInjectable {
    public abstract String getFirstName();
	public abstract String getSurname();
	public abstract String getInstitution();
	public abstract String getEmail();
	public abstract String getAddress();
    public abstract String getError();
    public abstract String getPseudonym();
	public abstract void setError(String value);
    public abstract byte getContributorType();
    public abstract void setContributorType(byte value);
    public abstract boolean getCheckEditingPermissions();
    public abstract void setCheckEditingPermissions(boolean value);
    public abstract boolean getCheckUnapprovedType();
    public abstract void setCheckUnapprovedType(boolean value);
    
    /**
     * This is used in a very specific context.  A user will choose to edit
     * a contributor but not have been logged in, so they will be coming from the
     * login right to this page.
     */
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
    	doActivate(cycle, AbstractWrappablePage.DEFAULT_WRAPPER, "ScientificContributorRegistrationOther", 
				AbstractContributorSearchOrResultsPage.EDIT_CONTRIBUTOR, null, Contributor.ACCESSORY_CONTRIBUTOR,
				true, true);    	
    }
	
	public void doActivate(IRequestCycle cycle, byte wrapperType, String returnPage) {
	    doActivate(cycle, wrapperType, returnPage, ((byte) 0), null, (byte)-1, false, false);
	}
    
    public void doActivate(IRequestCycle cycle, byte wrapperType, String returnPage, byte actionType, Long editedObjectId) {
        doActivate(cycle, wrapperType, returnPage, actionType, editedObjectId, (byte)-1, false, false);        
    }
    
    /**
     * Called from a branch/leaf page to add a contributor to some node
     * @param cycle
     * @param nodeId
     */
    public void addNodeToContributor(IRequestCycle cycle, Long nodeId) {
    	doActivate(cycle, AbstractWrappablePage.SCIENTIFIC_WRAPPER, "ViewBranchOrLeaf", ADD_NODE_TO_CONTRIBUTOR_PERMISSIONS, 
    			nodeId, Contributor.SCIENTIFIC_CONTRIBUTOR, false, false);
    }
    
	
	public void doActivate(IRequestCycle cycle, byte wrapperType, String returnPage, byte actionType, Long editedObjectId, byte contributorType, boolean checkEditingPermissions, boolean checkUnapprovedType) {
	    setWrapperType(wrapperType);
	    setReturnPageName(returnPage);
	    setActionType(actionType);
	    setEditedObjectId(editedObjectId);
        setContributorType(contributorType);
        setCheckEditingPermissions(checkEditingPermissions);
        setCheckUnapprovedType(checkUnapprovedType);
      	cycle.activate(this);
	}
        
	@SuppressWarnings("unchecked")
	public void searchContributors(IRequestCycle cycle) {
            Hashtable args = new Hashtable();
            if (StringUtils.notEmpty(getFirstName())) {
            	args.put(ContributorDAO.FIRSTNAME, getFirstName());
            }
            if (StringUtils.notEmpty(getSurname())) {
            	args.put(ContributorDAO.SURNAME, getSurname());
            }
            if (StringUtils.notEmpty(getInstitution())) {
            	args.put(ContributorDAO.INSTITUTION, getInstitution());
            }
            if (StringUtils.notEmpty(getEmail())) {
            	args.put(ContributorDAO.EMAIL, getEmail());
            }
            if (StringUtils.notEmpty(getAddress())) {
            	args.put(ContributorDAO.ADDRESS, getAddress());
            }
            if (getContributorType() >= 0) {
                args.put(ContributorDAO.TYPE, "" + getContributorType());
                if (getCheckUnapprovedType()) {
                	args.put(ContributorDAO.CHECKUNAPPROVEDTYPE, true);
                }
            }
            if (StringUtils.notEmpty(getPseudonym())) {
                args.put(ContributorDAO.ALIAS, getPseudonym());
            }
            List results = getContributorDAO().findContributors(args);
            // filter out contributors that they don't have permission for 
            if (getCheckEditingPermissions()) {
            	PermissionChecker checker = getPermissionChecker();
            	Contributor editingContributor = getContributor();
            	for (Iterator iter = new ArrayList(results).iterator(); iter.hasNext();) {
					Contributor nextContributor = (Contributor) iter.next();
					if (!checker.checkHasEditingPermissionForContributor(editingContributor, nextContributor)) {
						results.remove(nextContributor);
					}
				}
            }            
            if (results.size() == 0) {
                setError("There are no Contributors matching the specified criteria.  Please try again.");
                return;
            }
            ContributorSearchResults resultsPage = (ContributorSearchResults) cycle.getPage("ContributorSearchResults");
            resultsPage.setContributors(results);
            resultsPage.setWrapperType(getWrapperType());
			resultsPage.setReturnPageName(getReturnPageName());
			resultsPage.setActionType(getActionType());
			resultsPage.setEditedObjectId(getEditedObjectId());
			if (getBodyStyle() != null) {
			    resultsPage.setBodyStyle(getBodyStyle());
			}
            cycle.activate(resultsPage);
	}

}
