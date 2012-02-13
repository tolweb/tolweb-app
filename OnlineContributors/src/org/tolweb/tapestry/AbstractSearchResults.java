/*
 * Created on Jul 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractSearchResults extends ReturnablePage {
    public abstract void setInstructions(String value);
	public abstract String getInstructions();
	public abstract void setLinkString(String value);
	public abstract String getLinkString();
	public abstract void setSearchPageName(String value);
	public abstract String getSearchPageName();
    public abstract void setCallbackType(int value);
    public abstract int getCallbackType();
    
	/**
	 * Used to get the javascript editing link for a given object 
	 */
	protected static final String EDIT_LINK_NAME = "editLink";
	public static final String EDIT_FUNCTION_NAME = "editObject";
	public static final int NO_JAVASCRIPT_USE = 0;
	public static final int TREEHOUSE_JAVASCRIPT_USE = 1;
	public static final int EDIT_IMAGE_JAVASCRIPT_USE = 2;
   
	
	protected abstract String getEditWindowName();	
	
	public String getReturnPageName() {
	    return getSearchPageName();
	}
	
	public void setReturnPageName(String value) {
	    if (StringUtils.notEmpty(value)) {
	        setSearchPageName(value);
	    }
	}
    
    public static String getJavascriptActionString(String functionName, String linkText, String id) {
        if (!functionName.equals("selectImage")) {
            return "<a href=\"javascript:" + functionName + "(" +  id + ");\">" + linkText + "</a>";
        } else {
            // For now we aren't going to have any action associated with the formerly
            // insert image id into text function.  In the future this will be a
            // 'insert into image basket' or something like that
            return "";
        }
    }	
}
