/*
 * Created on Apr 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ApproveRejectTreehouseContributorPage extends AbstractLearningEditorPage {
	@Persist("session")
	public abstract Contributor getEditedContributor();
	public abstract void setEditedContributor(Contributor contr);
	public abstract void setIsApprove(Boolean value);
	public abstract Boolean getIsApprove();
	public abstract void setComments(String value);
	public abstract String getComments();
	
	public void commentsSubmit(IRequestCycle cycle) {		
		Contributor contr = getEditedContributor();
		boolean isApprove = getIsApprove().booleanValue(); 
		EditNewTreehouseContributors editPage = (EditNewTreehouseContributors) cycle.getPage(getReturnPageName());
		if (isApprove) {
		    editPage.setApprovedContributor(contr);
		} else {
		    editPage.setRejectedContributor(contr);
		}
		if (StringUtils.notEmpty(getComments())) {
		    editPage.setComments(getComments());
		}
		cycle.activate(editPage);
	}
	
	public String getApproveString() {
        return "Approve Contributor";
	}
	
	public String getRejectString() {
        return "Reject Contributor"; 
	}
}
