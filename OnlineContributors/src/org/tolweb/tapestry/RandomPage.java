package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

/**
 * Replacement to the original perl-based random page functionality.
 * 
 * This should completely redirect to the URL associated with the random page. A BranchPage 
 * is mapped to via RewriteRules from within Apache (mod_rewrite being the module responsible 
 * for the actual URL rewriting).  So when testing this, it should redirect you to a URL 
 * that is associated with the application's hostname.  Without having a development 
 * environment that runs Apache with the mod_rewrite rules in place and an /etc/hosts 
 * entry for http://dev.tolweb.org - this will redirect to the assuming PROD server.
 * 
 * Testing URL: http://localhost:8080/onlinecontributors/app?service=external&page=RandomPage
 * 
 * @author lenards
 *
 */
public abstract class RandomPage extends BasePage implements
	IExternalPage, BaseInjectable, PageInjectable, MiscInjectable {
		
	@Override
	public void activateExternalPage(Object[] arg0, IRequestCycle cycle) {
		MappedPage rand = getPublicPageDAO().getRandomPage();
		refreshToPage(rand);
	}

	/**
	 * Performs an arbitrary redirection to a URL. 
	 * 
	 * This is borrowed via "copyboard" inheritance from AbstractBranchOfLeafPage 
	 * as a quickfix to the redirection reimplementation of RandomPage. 
	 * 
	 * @param page the random page to redirect to
	 */
    private void refreshToPage(MappedPage page) {
    	String host = getConfiguration().getHostPrefix();
    	String hostPrefix = "http://" + host + "/";
    	String url = getUrlBuilder().getURLForBranchPage(hostPrefix, page.getGroupName(), page.getMappedNode().getNodeId());
        throw new RedirectException(url);
    } 
}
