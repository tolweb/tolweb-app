/*
 * Created on Sep 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseDataConfirm extends BasePage implements AccessoryInjectable, BaseInjectable {
    public abstract void setTreehouse(MappedAccessoryPage value);
    public abstract MappedAccessoryPage getTreehouse();
    public static final String TREEHOUSE_SUBMITTED_PAGE = "TreehouseSubmitted";
    public static final String TREEHOUSE_PROPERTY = "treehouse"; 
    
	public void submitTreehouse(IRequestCycle cycle) {
        getAccessoryPageHelper().submitTreehouse(cycle, getTreehouse(), getSubmitPageName(), getPropertyName(), false, null, null);
	}
	
	public String getSubmitPageName() {
	    return TREEHOUSE_SUBMITTED_PAGE;
	}
	
	public String getPropertyName() {
	    return TREEHOUSE_PROPERTY;
	}
	
	public void editTreehouse(IRequestCycle cycle) {
		cycle.activate("TreehouseEditPublish");
		setTreehouse(null);
	}   
	public String getWorkingUrl() {
		return getUrlBuilder().getWorkingURLForObject(getTreehouse());
	}
}
