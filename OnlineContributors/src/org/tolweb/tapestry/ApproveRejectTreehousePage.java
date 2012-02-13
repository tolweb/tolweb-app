/*
 * Created on Jul 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.Student;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ApproveRejectTreehousePage extends AbstractLearningEditorPage implements AccessoryInjectable {
	public abstract void setTreehouseToApproveOrReject(MappedAccessoryPage page);
	@Persist("session")
	public abstract MappedAccessoryPage getTreehouseToApproveOrReject();
	public abstract void setIsApprove(Boolean value);
	public abstract Boolean getIsApprove();
	public abstract void setIsTreehouse(Boolean value);
	public abstract Boolean getIsTreehouse();
	public abstract String getComments();
	public abstract String getCheckedAreas();
	public abstract void setCheckedAreas(String value);
	@Bean
	public abstract ValidationDelegate getDelegate();
    
    public void doActivate(MappedAccessoryPage treehouse, boolean isApprove, boolean isTreehouse, 
            String returnPageName, IRequestCycle cycle) {
        setTreehouseToApproveOrReject(treehouse);
        setIsApprove(isApprove);
        setIsTreehouse(isTreehouse);
        cycle.activate(this);
    }
	
	public void commentsSubmit(IRequestCycle cycle) {	
        AccessoryPageDAO workingDao = getWorkingAccessoryPageDAO();
		MappedAccessoryPage treehouse = getTreehouseToApproveOrReject();
        // Refetch the treehouse because it's theoretically possible for things
        // to have changed since the objects were initially fetched, and the
        // publishing process depends on things being up-to-date
        treehouse = workingDao.getAccessoryPageWithId(treehouse.getAccessoryPageId());
        Contributor submittedContributor = treehouse.getSubmittedContributor(); 
		String submittorEmail = submittedContributor.getEmail();
		boolean isApprove = getIsApprove().booleanValue();
		getAccessoryPageSubmitter().editorSubmitAccessoryPage(treehouse, isApprove);
        IPage returnPage = cycle.getPage(getSubmittedPagesPageName());
        if (StringUtils.notEmpty(getComments())) {
            PropertyUtils.write(returnPage, "comments", getComments());
        }
        if (StringUtils.notEmpty(getCheckedAreas())) {
            PropertyUtils.write(returnPage, "checkedAreas", getCheckedAreas());
        }        
        PropertyUtils.write(returnPage, "wasApprovedTreehouse", isApprove);
        try {
            PropertyUtils.write(returnPage, "submittedTreehouse", treehouse);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        if (EditSubmittedTreehouses.class.isInstance(returnPage)) {
    		EditSubmittedTreehouses submittedPage = (EditSubmittedTreehouses) cycle.getPage(getSubmittedPagesPageName());
    		submittedPage.setSubmittedTreehouses(null);
    		submittedPage.setApprovedOrRejectedTreehouse(treehouse);
    		submittedPage.setWasRejectedTreehouse(!isApprove);
            if (Student.class.isInstance(submittedContributor)) {
                submittedPage.setToEmail(((Student) submittedContributor).getTeacher().getEmail());
            } else {
                submittedPage.setToEmail(submittorEmail);
            }
        }
		cycle.activate(returnPage);
	}
	
	public String getApproveString() {
	    if (getIsTreehouse().booleanValue()) {
	        return "Approve Treehouse";
	    } else {
	        return "Approve Page";
	    }
	}
	
	public String getRejectString() {
	    if (getIsTreehouse().booleanValue()) {
	        return "Reject Treehouse";
	    } else {
	        return "Reject Page";
	    }	    
	}
	
	public String getSubmittedPagesPageName() {
	    if (getIsTreehouse().booleanValue()) {
	        return "EditSubmittedTreehouses";
	    } else {
	        return "EditSubmittedArticleNotes";
	    }
	}
}

