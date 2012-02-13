/*
 * Created on Jun 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.Persist;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehousePortfolioSearchPage extends AbstractTreehouseEditingPage {
	@Persist("client")
	public abstract boolean getDontCheckPermissions();
    public abstract void setDontCheckPermissions(boolean value);
    public abstract void setEditedObjectType(String value);
    @InitialValue("'Portfolio'")
    @Persist("client")
    public abstract String getEditedObjectType();
    
    public void returnToEditing(IRequestCycle cycle) {
        cycle.activate(getReturnPageName());
    }
}
