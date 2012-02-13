/*
 * Created on Jun 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.event.PageBeginRenderListener;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourceEditMainContent extends
        AbstractTreehouseEditingPage implements PageBeginRenderListener {
    public static final String PROGRESS_PROPERTY = "pageContentProgress";
    
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}    
}
