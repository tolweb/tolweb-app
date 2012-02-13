/*
 * Created on Mar 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ArticleNoteManager extends AbstractContributorPage implements IExternalPage, 
		UserInjectable, AccessoryInjectable, BaseInjectable {
    
    public void activateExternalPage(Object[] params, IRequestCycle cycle) {
	    Contributor contributor = getCookieAndContributorSource().authenticateExternalPage(cycle);
	    if (contributor == null) {
	        throw new PageRedirectException("ScientificContributorsLogin");
	    }
    }    
    
    public void pageValidate(PageEvent event) {
        
    }
    
    public void createNewNote(IRequestCycle cycle) {
        Contributor contr = getContributor();
		MappedAccessoryPage page = getAccessoryPageHelper().initializeNewAccessoryPageInstance(contr, false, getWorkingAccessoryPageDAO());
		if (logger.isDebugEnabled()) {
			logger.debug("Contributor " + getContributor().getName() + " just created article or note: " + " with id: " + page.getAccessoryPageId());		    
		}
		EditArticleNote editPage = (EditArticleNote) cycle.getPage("EditArticleNote");
		editPage.setAccPage(page);
		cycle.activate(editPage);
    }
}
