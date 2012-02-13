/*
 * Created on Jul 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.Persist;

/**
 * @author dmandel
 *
 * Class that can return to some other page.  Currently used so search pages know which page to return to
 */
public abstract class ReturnablePage extends AbstractWrappablePage {
	// The page to return to in case of search
	public abstract void setReturnPageName(String value);
	@Persist("client")
	public abstract String getReturnPageName();

    public void returnToEditing(IRequestCycle cycle) {
        IPage page = cycle.getPage(getReturnPageName());
        if (page instanceof EditIdPage) {
            Long objectId = (Long) PropertyUtils.read(this, "editedObjectId");
            ((EditIdPage) page).setEditedObjectId(objectId);
        }
        throw new PageRedirectException(page);
    }
}