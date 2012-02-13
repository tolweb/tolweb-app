/*
 * Created on Jul 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.Hashtable;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractSearch extends BaseComponent {
	public abstract void setNoResults(boolean value);
	public abstract String getGroupName();
	public abstract void setInstructions(String value);
	public abstract String getInstructions();
	public abstract String getLinkString();
	public abstract String getSearchPageName();
	public abstract void doSearch(IRequestCycle cycle);		
	public abstract void setWrapperType(byte value);
	@Parameter(required = true)
	public abstract byte getWrapperType();
	public abstract String getContainingPageNoResultsAnchor();
	@Parameter
	public abstract String getReturnPageName();
	public abstract void setReturnPageName(String value);
	@Parameter(required = true)
	public abstract int getCallbackType();
	public abstract void setCallbackType(int value);
	
	protected void setSearchResultsVariables(AbstractSearchResults page) {
		page.setInstructions(getInstructions());
		page.setLinkString(getLinkString());
		String searchPageName = getSearchPageName();
		page.setSearchPageName(searchPageName);
	}
    
	@SuppressWarnings("unchecked")
	protected void addIfNotNull(Object value, Object key, Hashtable hash) {
		if (value != null) {
			if (!(value instanceof String)) {
				hash.put(key, value);
			} else {
				if (StringUtils.notEmpty((String) value)) {
					hash.put(key, value);
				}
			}
		}
	}	
	
	protected void setPageProperties() {
		if (StringUtils.notEmpty(getContainingPageNoResultsAnchor())) {
		    try {
		    	PropertyUtils.write(getPage(), "selectedAnchor", getContainingPageNoResultsAnchor());
		    } catch (Exception e) { e.printStackTrace(); }
		}	    
	}
}