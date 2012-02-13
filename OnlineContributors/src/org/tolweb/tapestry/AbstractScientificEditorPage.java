package org.tolweb.tapestry;

import net.sf.tacos.ajax.AjaxUtils;

import org.apache.tapestry.PageRedirectException;

public abstract class AbstractScientificEditorPage extends
		AbstractScientificContributorPage {
	protected void checkAuth() {
		if (!AjaxUtils.isAjaxCycle(getRequestCycle())) {
			super.checkAuth();
			if (!getPermissionChecker().isEditor(getCookieAndContributorSource().getContributorFromSessionOrAuthCookie())) {
				throw new PageRedirectException("ScientificMaterialsManager");
			}
		}
	}
}
