/*
 * Created on Apr 11, 2005
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
public abstract class TreehouseEditMedia extends AbstractTreehouseEditingPage {
    public static final String PROGRESS_PROPERTY = "mediaProgress"; 
    
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}    
	
	public int getStepNumber() {
	    if (getIsWebquest()) {
	        return 6;
	    } else {
	        return 3;
	    }
	}
}
