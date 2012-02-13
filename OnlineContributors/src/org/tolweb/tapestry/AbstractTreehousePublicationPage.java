package org.tolweb.tapestry;

import java.util.Hashtable;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.EditComment;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;

public abstract class AbstractTreehousePublicationPage extends
        ApproveRejectTreehousePage implements AccessoryInjectable, MiscInjectable {
    public abstract void setDontPublish(boolean value);
    public abstract boolean getDontPublish();
    protected abstract EditComment contructEditComment();
    
    public void pageValidate(PageEvent event) {
    }
    
    public void doActivate(MappedAccessoryPage treehouse, boolean isApprove, boolean isTreehouse, 
            String returnPageName, IRequestCycle cycle) {
        setReturnPageName(returnPageName);        
        super.doActivate(treehouse, isApprove, isTreehouse, returnPageName, cycle);
    }
    
    public Boolean getIsTreehouse() {
        return true;
    }
    
    public void setIsTreehouse(Boolean value) {}    

    public void dontPublish(IRequestCycle cycle) {
        setDontPublish(true);
    }
    
    public void setIsApprove(Boolean value) {}
    
    @SuppressWarnings("unchecked")
    public void commentsSubmit(IRequestCycle cycle) {
        if (getDontPublish()) {
            cycle.activate(getReturnPageName());
        } else {
            if (!getDelegate().getHasErrors()) {
                if (!getIsTeacherApproval()) {
                    EditComment comment = contructEditComment();
                    getReorderHelper().addToSet(getTreehouseToApproveOrReject().getEditComments(), comment);
                    getTreehouseToApproveOrReject().setIsSubmittedToTeacher(getIsApprove());
                    // need to do a save here in order to ensure the edit comment gets saved
                    getWorkingAccessoryPageDAO().saveAccessoryPage(getTreehouseToApproveOrReject());                
                    super.commentsSubmit(cycle);
                } else {
                    Hashtable additionalPageArgs = new Hashtable();
                    additionalPageArgs.put("wasApprovedTreehouse", true);
                    // if it's the teacher approving something to the editor,
                    // it's really just a submit for approval, so don't do the
                    // normal publication stuff
                    getAccessoryPageHelper().submitTreehouse(cycle, getTreehouseToApproveOrReject(), getReturnPageName(), 
                            "submittedTreehouse", false, getComments(), additionalPageArgs);
                }
            }
        }
    }
    
    public abstract boolean getIsTeacherApproval();
}
