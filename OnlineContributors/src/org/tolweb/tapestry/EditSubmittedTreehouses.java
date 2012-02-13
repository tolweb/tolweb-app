/*
 * Created on Jul 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.contrib.table.components.Table;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.misc.RendererFactory;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class EditSubmittedTreehouses extends AbstractLearningEditorPage implements PageBeginRenderListener, UserInjectable, 
		TreehouseInjectable, BaseInjectable {
	@SuppressWarnings("unchecked")
	public abstract List getSubmittedTreehouses();
	@SuppressWarnings("unchecked")
	public abstract void setSubmittedTreehouses(List value);
	public abstract void setWasRejectedTreehouse(boolean value);
	public abstract void setWasApprovedTreehouse(boolean value);
	public abstract void setApprovedOrRejectedTreehouse(MappedAccessoryPage value);
	public abstract MappedAccessoryPage getApprovedOrRejectedTreehouse();
	public abstract void setToEmail(String value);
    public abstract String getToEmail();
    public abstract String getTeacherEmail();
    
	public abstract void setComments(String value);
	
	public String getEditWindowName() {
		return RendererFactory.EDIT_TREEHOUSE_WINDOW_NAME;
	}
    
    public MappedAccessoryPage getCurrentTreehouse() {
        return (MappedAccessoryPage) ((Table) getComponents().get("table")).getTableRow();
    }
    
    public boolean getIsPublished() {
        return getPublicAccessoryPageDAO().getAccessoryPageExistsWithId(getCurrentTreehouse().getAccessoryPageId()); 
    }
    
    public String getOpenPublishedSpan() {
        if (getIsPublished()) {
            return "<span class=\"trhspublished\">";
        } else {
            return null;
        }
    }
    
    public String getClosePublishedSpan() {
        if (getIsPublished()) {
            return "</span>";
        } else {
            return null;
        }
    }
	
    public void editTreehouse(IRequestCycle cycle) {
	    MappedAccessoryPage treehouse = getTreehouseFromCycleParams(cycle);
	    treehouse.setLastEditedContributor(getContributor());
	    treehouse.setLastEditedDate(new Date());
		setTreehouse(treehouse);
		cycle.activate("TreehouseEditor");	
	}
	
	public void pageBeginRender(PageEvent event) {
	    super.pageBeginRender(event);
		setSubmittedTreehouses(getSubmittedPages());
	}
	
	public void approveTreehouse(IRequestCycle cycle) {
		gotoApproveRejectPage(cycle, true);			
	}
	
	public void rejectTreehouse(IRequestCycle cycle) {
		gotoApproveRejectPage(cycle, false);		
	}
	
	private void gotoApproveRejectPage(IRequestCycle cycle, boolean isApprove) {
		ApproveRejectTreehousePage page = (ApproveRejectTreehousePage) cycle.getPage(getApproveRejectPageName(isApprove));
        page.doActivate(getTreehouseFromCycleParams(cycle), isApprove, getIsTreehouses(), getPageName(), cycle);
        // for the case that it's an editor, not a teacher doing the approval
        if (isApprove) {
            try {
                PropertyUtils.write(page, "isTeacherApproval", false);
            } catch (RuntimeException e) {}
        }
	}
	
	protected String getApproveRejectPageName(boolean isApprove) {
        if (getIsTreehouses()) {
            if (isApprove) {
                return "ApproveTreehousePage";
            } else {
                return "ReviseTreehousePage";
            }
        } else {
            return "ApproveRejectTreehousePage";
        }
	}
	
	@SuppressWarnings("unchecked")
	private MappedAccessoryPage getTreehouseFromCycleParams(IRequestCycle cycle) {
		Long treehouseId = (Long) cycle.getListenerParameters()[0];
		Iterator it = getSubmittedTreehouses().iterator();
		while (it.hasNext()) {
			MappedAccessoryPage treehouse = (MappedAccessoryPage) it.next();
			if (treehouse.getAccessoryPageId().equals(treehouseId)) {
				return treehouse;
			}
		}
		// this should never happen
		return null;
	}
	
	public String getEditJavascriptActionString() {
	    MappedAccessoryPage treehouse = (MappedAccessoryPage) ((Table) getComponents().get("table")).getTableRow();
	    return AbstractSearchResults.getJavascriptActionString(AbstractSearchResults.EDIT_FUNCTION_NAME, "Edit", treehouse.getAccessoryPageId().toString());
	}
	
	public boolean getIsTreehouses() {
	    return true;
	}
	
	@SuppressWarnings("unchecked")
	public List getSubmittedPages() {
	    return getWorkingAccessoryPageDAO().getSubmittedTreehouses();
	}
	
	@SuppressWarnings("unchecked")
    public List getEmailAddresses() {
        ArrayList addresses = new ArrayList();
        addresses.add(getToEmail());
        addresses.add(getConfiguration().getLearningEditorEmail());
        if (StringUtils.notEmpty(getTeacherEmail())) {
            addresses.add(getTeacherEmail());
        }
        return addresses;
    }
	public abstract MappedAccessoryPage getCurrentPage();
	public String getCurrentWorkingUrl() {
		return getUrlBuilder().getWorkingURLForObject(getCurrentPage());
	}
	public String getPublicUrl() {
		return getUrlBuilder().getPublicURLForObject(getApprovedOrRejectedTreehouse());
	}
}
