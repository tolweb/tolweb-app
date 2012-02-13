package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;

public abstract class AbstractContributorSearchOrResultsPage extends ReturnablePage {
    public static final byte SELECT_COPYRIGHT = 0;
	public static final byte SELECT_SUBMITTER = 1;
	public static final byte EDIT_IMAGE_PERMISSIONS = 2;
	public static final byte ADD_CLASSROOM_PROJECT_EDITOR = 3;
	public static final byte EDIT_CONTRIBUTOR_PERMISSIONS = 4;
	public static final byte EDIT_CONTRIBUTOR = 5;
	public static final byte ADD_NODE_TO_CONTRIBUTOR_PERMISSIONS = 6;
	public abstract void setActionType(byte value);
	public abstract byte getActionType();
	public abstract void setEditedObjectId(Long value);
	public abstract Long getEditedObjectId();	

	public void returnToEditing(IRequestCycle cycle) {
    	if (getActionType() != ADD_NODE_TO_CONTRIBUTOR_PERMISSIONS) {
    		super.returnToEditing(cycle);
    	} else {		
    		returnToBranchLeaf();
    	}
    }	
	
	protected void returnToBranchLeaf() {
    	ViewBranchOrLeaf viewPage = (ViewBranchOrLeaf) getRequestCycle().getPage("ViewBranchOrLeaf");
    	viewPage.returnFromContributorPrivilegesEditing(getEditedObjectId(), getRequestCycle());    		
	}
}
