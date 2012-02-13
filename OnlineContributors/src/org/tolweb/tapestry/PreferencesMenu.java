/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.InjectPage;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.tapestry.injections.CookieInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PreferencesMenu extends BaseComponent implements CookieInjectable {
	public String getGlossString() {
	    if (getIsCookieEnabled(CookieAndContributorSource.GLOSSARY_COOKIE)) {
	        return "Hide Glossary Entries";
	    } else {
	        return "Show Glossary Entries";
	    }
	}
	
	public String getInternetLinksString() {
	    if (getIsCookieEnabled(CookieAndContributorSource.INET_INFO_COOKIE)) {
	        return "Move Internet Links to Bottom";
	    } else {
	        return "Move Internet Links to Top";
	    }
	}
	
	public String getTaxonListsString() {
	    if (getIsCookieEnabled(CookieAndContributorSource.TAXON_LIST_COOKIE)) {
	        return "Show Trees When Available";
	    } else {
	        return "Show Only Taxon Lists";
	    }
	}
	
	public String getRandomPicsString() {
	    if (getIsCookieEnabled(CookieAndContributorSource.RANDOM_IMAGES_COOKIE)) {
	        return "Hide Random Pictures From This Group";
	    } else {
	        return "Show Random Pictures From This Group";
	    }
	}   
    
    @InjectPage("ViewBranchOrLeaf")
    public abstract ViewBranchOrLeaf getViewBranchOrLeaf();
    
	private boolean getIsCookieEnabled(String cookieName) {
	    return getCookieAndContributorSource().getCookieIsEnabled(cookieName);
	}
}
