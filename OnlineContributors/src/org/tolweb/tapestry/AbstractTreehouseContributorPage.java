/*
 * Created on Jul 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.Student;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractTreehouseContributorPage extends AbstractContributorPage implements CookieInjectable {
	public String getLoginPageName() {
		return Login.TREEHOUSE_LOGIN_PAGE;
	}
	public void prepareForRender(IRequestCycle cycle) {
	    super.prepareForRender(cycle);
	    getCookieAndContributorSource().addWorkingCookie();
	}
	protected byte getUserType() {
		return Contributor.TREEHOUSE_CONTRIBUTOR;
	}	
    public boolean getIsStudent() {
        return getContributorExists() && getContributor() != null &&
            Student.class.isInstance(getContributor());
    }
}
