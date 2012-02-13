/*
 * Created on Jul 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IPage;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractLearningEditorPage extends AbstractTreehouseContributorPage {
	public void pageValidate(PageEvent event) {
		super.pageValidate(event);
		Contributor contr = getContributor();
		if (!contr.getIsLearningEditor()) {
			IPage treehouseLoginPage = getRequestCycle().getPage(Login.TREEHOUSE_LOGIN_PAGE);
			throw new PageRedirectException(treehouseLoginPage);
		}
	}
}
