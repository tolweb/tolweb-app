/*
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageValidateListener;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditSubmittedArticleNotes extends EditSubmittedTreehouses implements PageValidateListener, UserInjectable, AccessoryInjectable {
	public void pageValidate(PageEvent event) {
        Contributor contributor = getContributor();
        if (contributor == null || !getPermissionChecker().isEditor(getContributor())) {
            throw new PageRedirectException("ScientificContributorsLogin");
        }
    }
	
	public boolean getIsTreehouses() {
	    return false;
	}
	
	@SuppressWarnings("unchecked")
	public List getSubmittedPages() {
	    return getWorkingAccessoryPageDAO().getSubmittedArticlesAndNotes();
	}
	
	public String getPublicUrl() {
		return getUrlBuilder().getPublicURLForObject(getApprovedOrRejectedTreehouse());
	}
}
