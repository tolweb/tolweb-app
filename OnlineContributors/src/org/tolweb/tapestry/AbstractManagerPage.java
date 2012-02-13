/*
 * Created on Aug 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractManagerPage extends ReturnablePage {
    public abstract boolean getNewObjectUploaded();
    public abstract void setNewObjectUploaded(boolean value);
    public abstract int getNewObjectId();
    public abstract void setNewObjectId(int value);
    public abstract String getEditObjectUrl();
    public abstract void setEditObjectUrl(String value);
    
    protected String getEditWindowName() {
    	return "";
    }
	
	public static final String EDIT_LINK_NAME = "editLink"; 
	public void editObject() {}
}
