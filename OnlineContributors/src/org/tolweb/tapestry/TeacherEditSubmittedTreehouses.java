package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class TeacherEditSubmittedTreehouses extends AbstractTreehouseContributorPage implements UserInjectable, 
		TreehouseInjectable, AccessoryInjectable, BaseInjectable {
    public abstract MappedAccessoryPage getCurrentTreehouse();
    public abstract MappedAccessoryPage getSubmittedTreehouse();
    
    @SuppressWarnings("unchecked")
    public List getTreehouses() {
        Contributor teacher = getContributor();
        return getWorkingAccessoryPageDAO().getSubmittedTreehousesForTeacher(teacher);
    }
    
    public String getCurrentViewUrl() {
        return getUrlBuilder().getWorkingURLForObject(getCurrentTreehouse());
    }
    
    public String getViewLinkClass() {
        if (getPublicAccessoryPageDAO().getAccessoryPageExistsWithId(getCurrentTreehouse().getAccessoryPageId())) {
            return "trhspublished";
        } else {
            return null;
        }
    }
    
    public void approveTreehouse(IRequestCycle cycle) {
        ApproveTreehousePage page = (ApproveTreehousePage) cycle.getPage("ApproveTreehousePage");
        page.doActivate(getTreehouseFromCycleParams(cycle), true, true, getPageName(), cycle);
    }
    
    public void rejectTreehouse(IRequestCycle cycle) {
        ReviseTreehousePage page = (ReviseTreehousePage) cycle.getPage("ReviseTreehousePage");
        page.doActivate(getTreehouseFromCycleParams(cycle), false, true, getPageName(), cycle);        
    }    

    private MappedAccessoryPage getTreehouseFromCycleParams(IRequestCycle cycle) {
        Long treehouseId = (Long) cycle.getListenerParameters()[0];
        return getWorkingAccessoryPageDAO().getAccessoryPageWithId(treehouseId);
    }
}
