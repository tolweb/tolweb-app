/*
 * Created on Feb 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TolPageLink extends BaseComponent implements PageInjectable, BaseInjectable {
    public abstract boolean getIsWorking();
    public abstract boolean getIsPublic();
    public abstract boolean getIsInactiveLink();
    public abstract Object getTolPage();
    public abstract String getGroupName();
    public abstract Long getNodeId();
    
    public String getUrlString() {
        URLBuilder builder = getUrlBuilder();
        boolean isGroup = getGroupName() != null;
        // If the group name is bound, use that instead.  Otherwise use the page
        Object object = getGroupName() != null ? getGroupName() : getTolPage();
        if (getIsWorking() && !getIsInactiveLink()) {
        	if (isGroup) {
        		return builder.getWorkingURLForBranchPage(getGroupName(), getNodeId());
        	} else {
        		return builder.getWorkingURLForObject(object);
        	}
        } else if (getIsPublic()) {
        	if (isGroup) {
        		return builder.getPublicURLForBranchPage(getGroupName(), getNodeId());
        	} else {
        		return builder.getPublicURLForObject(object);
        	}
        } else if (getIsInactiveLink() && getIsWorking()) {
        	return builder.getInactiveWorkingURLForBranchPage(getGroupName(), getNodeId());
        } else {
        	if (isGroup) {
        		return builder.getURLForBranchPage(getGroupName(), getNodeId());
        	} else {
        		return builder.getURLForObject(object);
        	}
        }
    }
}
