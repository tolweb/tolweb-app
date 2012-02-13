/*
 * Created on May 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditPageStatus extends AbstractPageEditingPage implements PageInjectable, MiscInjectable {
	public boolean doAdditionalSecurityCheck(Contributor contr, Long nodeId) {
	    return getPermissionChecker().checkHasEditingPermissionForNode(contr, nodeId);
	}
	
	public IPropertySelectionModel getStatusSelectionModel() {
	    return getPropertySelectionFactory().createModelFromList(MappedPage.getValidPageStatuses());
	}
	
    public void doSave(IRequestCycle cycle) {
        savePage();
        if (getSubmitSelected()) {
            getPagePusher().pushPageStatusToDB(getTolPage(), getPublicPageDAO());
            getCacheAccess().evictAllPageObjectsFromCache(getTolPage());
            setError("Status Successfully Published");
        }
    }
}
