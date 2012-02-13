/*
 * Created on Jul 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.log4j.Logger;
import org.apache.tapestry.IPage;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageValidateListener;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractContributorPage extends AbstractManagerPage implements PageValidateListener, 
	PageBeginRenderListener, UserInjectable, CookieInjectable {
    protected Logger logger;
    
    public AbstractContributorPage() {
        logger = Logger.getLogger(getClass());
    }
    
	public void pageValidate(PageEvent event) {
		checkAuth();
	}
	public void pageBeginRender(PageEvent event) {
		checkAuth();
	}	

	protected void checkAuth() {
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		if (contr == null || contr.getContributorType() > getUserType()) {
			redirectToLogin();
		}
	}	
	
	public String getLoginPageName() {
		return Login.IMAGE_LOGIN_PAGE;
	}
	
	protected void redirectToLogin() {
		IPage page = getRequestCycle().getPage(getLoginPageName());
		throw new PageRedirectException(page);	    
	}
	protected byte getUserType() {
		return Contributor.REVIEWER;
	}	
}
