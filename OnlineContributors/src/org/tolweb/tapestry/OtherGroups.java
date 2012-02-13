/*
 * Created on Sep 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.helpers.OtherGroupsHelper;
import org.tolweb.tapestry.injections.BaseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class OtherGroups extends CachedOutputComponent implements BaseInjectable {
    @Parameter(required = true)
	public abstract MappedNode getNode();
	public abstract void setGroupsHelper(OtherGroupsHelper helper);
	@Parameter(required = false, defaultValue="null")
	public abstract OtherGroupsHelper getGroupsHelper();
	
	public abstract void setDefaultGroupsHelper(OtherGroupsHelper helper);
	public abstract OtherGroupsHelper getDefaultGroupsHelper();

	@Parameter(defaultValue = "1L")
	public abstract Long getRootNodeId();
	@InjectObject("spring:otherGroupsHelper")
	public abstract OtherGroupsHelper getOtherGroupsHelper();
	
	public void prepareForRender(IRequestCycle cycle) {
	    super.prepareForRender(cycle);
	    PageDAO pageDao = getPageDAO();
	    MappedNode node = getNode();
	    if (node != null && (!getUseCache() || getOutput() == null)) {
	    	if (getGroupsHelper() == null) {
	    		setDefaultGroupsHelper(getOtherGroupsHelper().constructHelperForNode(node, pageDao, getRootNodeId()));
	    	}
	    } else {
	    	setDefaultGroupsHelper(new OtherGroupsHelper());
	    }
	}
	
	public OtherGroupsHelper getGroupsHelperToUse() {
		// if the parameter is bound, use it, otherwise use the default
		if (getGroupsHelper() != null) {
			return getGroupsHelper();
		} else {
			return getDefaultGroupsHelper();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List getSubgroupPages() {
		OtherGroupsHelper helper = getGroupsHelperToUse();
		List pages = helper.getChildPages();
		return pages;
	}
	
	@SuppressWarnings("unchecked")
	public List getSiblingPages() {
		return getGroupsHelperToUse().getSiblingPages();
	}
	
	@SuppressWarnings("unchecked")
	public List getAncestorPages() {
		return getGroupsHelperToUse().getAncestorPages();
	}
	public String getParentGroupName() {
		return getGroupsHelperToUse().getParentGroupName();
	}
	protected String getCachedOutput() {
	    String result = getCacheAccess().getOtherGroupsForNode(getNode());
	    return result;
	}
	
	protected void setCachedOutput(String value) {
	    getCacheAccess().setOtherGroupsForNode(getNode(), value);
	}

	public boolean getIsLeaf() {
	    MappedNode node = getNode(); 
	    return node != null && node.getIsLeaf();
	}

    public boolean getNotRoot() {
        return !getNode().getNodeId().equals(getRootNodeId());
    }
    
    @SuppressWarnings("unchecked")
    public boolean getHasSubgroups() {
    	boolean isLeaf = getIsLeaf();
    	List subgroups = getSubgroupPages();
        return !isLeaf && subgroups.size() > 0;
    }
}