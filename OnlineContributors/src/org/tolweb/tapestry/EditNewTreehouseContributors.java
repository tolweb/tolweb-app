/*
 * Created on Aug 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditNewTreehouseContributors extends AbstractLearningEditorPage implements PageBeginRenderListener, 
		UserInjectable, BaseInjectable {
	@SuppressWarnings("unchecked")
    public abstract List getNewContributors();
	@SuppressWarnings("unchecked")
    public abstract void setNewContributors(List value);
    public abstract void setApprovedContributor(Contributor contributor);
    public abstract void setRejectedContributor(Contributor contributor);
    public abstract Contributor getRejectedContributor();
    public abstract Contributor getApprovedContributor();
    public abstract void setComments(String value);
    @SuppressWarnings("unchecked")
	public void pageBeginRender(PageEvent event) {
		ContributorDAO dao = getContributorDAO(); 
	    if (getRejectedContributor() != null) {
	        dao.deleteContributor(getRejectedContributor(), null);
	    }
	    List newContributors = fetchNewContributors();
	    Contributor approvedContributor = getApprovedContributor();
	    if (approvedContributor != null) {
		    for (Iterator iter = newContributors.iterator(); iter.hasNext();) {
	            Contributor contr = (Contributor) iter.next();
	            if (contr.getId() == approvedContributor.getId()) {
	                newContributors.remove(contr);
	                approveContributor(contr, getContributor(),
	                		dao);
	                setApprovedContributor(contr);
	                break;
	            }
	        }
	    }
	    setNewContributors(newContributors);	    
	}
	
	public static void approveContributor(Contributor contr, Contributor approvingContributor,
			ContributorDAO dao) {
        contr.setContributorType(contr.getUnapprovedContributorType());
        contr.setUnapprovedContributorType(Contributor.ANY_CONTRIBUTOR);
        contr.setConfirmationDate(new Date());		
        contr.setConfirmerContributorId(Long.valueOf(approvingContributor.getId()));
        dao.saveContributor(contr);        
	}
	
	public void approveContributor(IRequestCycle cycle) {
	    Contributor toApprove = getContributorFromRequestCycle(cycle);
	    gotoApproveRejectPage(toApprove, true, cycle);
	}
	
	public void rejectContributor(IRequestCycle cycle) {
	    Contributor toReject = getContributorFromRequestCycle(cycle);
	    gotoApproveRejectPage(toReject, false, cycle);
	}
	
	private void gotoApproveRejectPage(Contributor contr, boolean isApprove, IRequestCycle cycle) {
	    ApproveRejectTreehouseContributorPage approveRejectPage = (ApproveRejectTreehouseContributorPage) cycle.getPage(getApproveRejectPageName());
	    approveRejectPage.setEditedContributor(contr);
	    approveRejectPage.setIsApprove(Boolean.valueOf(isApprove));
	    approveRejectPage.setReturnPageName(getPageName());
	    cycle.activate(approveRejectPage);
	}
	
	protected String getApproveRejectPageName() {
		return "ApproveRejectTreehouseContributorPage";
	}
	
	@SuppressWarnings("unchecked")
	protected List fetchNewContributors() {
		return getContributorDAO().getNewTreehouseContributors();
	}
	
	/**
	 * Gets the contributor based on their id from the service parameters in
	 * the request cycle
	 * @param cycle The request cycle containing the contributor id
	 * @return The Contributor with the given id
	 */
	@SuppressWarnings("unchecked")
	private Contributor getContributorFromRequestCycle(IRequestCycle cycle) {
	    int id = ((Integer) cycle.getListenerParameters()[0]).intValue();
	    Iterator it = getNewContributors().iterator();
	    while (it.hasNext()) {
	        Contributor nextContr = (Contributor) it.next();
	        if (nextContr.getId() == id) {
	            getNewContributors().remove(nextContr);
	            return nextContr;
	        }
	    }
	    return null;
	}
}
