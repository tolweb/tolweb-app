package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.html.BasePage;

public class Home extends BasePage {
	protected void prepareForRender(IRequestCycle cycle) {
		//super.prepareForRender(cycle);
		//throw new RedirectException("http://tolweb.org/tree/home.pages/contributions.html");
		throw new PageRedirectException("ContributorLogin");
	}
}