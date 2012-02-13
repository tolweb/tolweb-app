/*
 * Created on Jul 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.hibernate.MappedNode;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TolPagesList extends BaseComponent {
    public abstract Object[] getCurrentPage();
    public abstract MappedNode getNode();
    public abstract int getIndex();
    @SuppressWarnings("unchecked")
    public abstract List getPages();
    public abstract String getOnPageClass();
    public abstract boolean getIsDropDownList();
    @Parameter
    public abstract boolean getIsIndexPage();
    @Parameter
    public abstract Long getProjectId();
    
    public void prepareForRender(IRequestCycle cycle) {
	    super.prepareForRender(cycle);
    }
    
	public boolean getIsThisPage() {
	    Object[] currentPage = getCurrentPage();
	    String pageName = (String) currentPage[0];
	    return getNode().getName().equals(pageName);
	}
	
	public String getCurrentGroupName() {
	    return ((String) getCurrentPage()[0]).replace(' ', '_');
	}	
	
	public Long getCurrentNodeId() {
		return (Long) getCurrentPage()[2];
	}
	
	public String getCurrentGroupNameWithSpaces() {
	    return ((String) getCurrentPage()[0]);
	}
	
	public String getLiClass() {
        if (getIsThisPage() && getIsLast() && getIsDropDownList()) {
            return "last_item_current_taxon";
        } else if (getIsThisPage()) {
	        return getOnPageClass();
	    } else if (getIsLast()) {
	        return "last_item";
	    } else {
	        return null;
	    }
	}
    
    public boolean getIsLast() {
        return getIndex() == getPages().size() - 1;
    }
}
