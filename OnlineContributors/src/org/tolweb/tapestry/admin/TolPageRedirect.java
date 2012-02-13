package org.tolweb.tapestry.admin;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.ViewBranchOrLeaf;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class TolPageRedirect extends BasePage implements IExternalPage, BaseInjectable, PageInjectable {
	public static final String TOL_PAGE_PARAMETER = "tol_page_id";
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String paramText = cycle.getParameter(TOL_PAGE_PARAMETER);
		if (StringUtils.notEmpty(paramText)) {
			Long pageId = Long.parseLong(paramText);
			MappedPage mpage = getWorkingPageDAO().getPageWithId(pageId);
			ViewBranchOrLeaf page = (ViewBranchOrLeaf)cycle.getPage("ViewBranchOrLeaf");
			page.setPageId(pageId);
			page.setTolPage(mpage);
			cycle.activate(page);
		}
	}

}
