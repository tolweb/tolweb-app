/*
 * Created on Mar 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.event.PageEvent;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractScientificContributorPage extends AbstractContributorPage {
	public String getLoginPageName() {
		return Login.SCIENTIFIC_LOGIN_PAGE;
	}
	
	public void pageValidate(PageEvent event) {}
	
	protected byte getUserType() {
		return Contributor.ACCESSORY_CONTRIBUTOR;
	}
}
