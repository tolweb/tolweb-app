/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.callback.ExternalCallback;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.PageContributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class CacheAndPublicAwarePage extends BasePage implements
		IExternalPage, PageBeginRenderListener, UserInjectable,
		CookieInjectable, RequestInjectable, NodeInjectable, PageInjectable,
		AccessoryInjectable, MiscInjectable, BaseInjectable {
    public abstract boolean getUseCache();
    public abstract void setUseCache(boolean value);
    public abstract boolean getUseGlossary();
    public abstract void setUseGlossary(boolean value);
    public abstract boolean getIsNonPublic();
    public abstract void setIsNonPublic(boolean value);
	
    /**
     * Constant used to store the node dao in the request cycle
     */
    public static final String NODE_DAO = "nodedao";
    /**
     * Constant used to store the page dao in the request cycle
     */
    public static final String PAGE_DAO = "pagedao";
    /**
     * Constant used to store the accessory page dao in the request cycle
     */
    public static final String ACC_PAGE_DAO = "accpagedao";
    /**
     * Constant used to store whether or note to use the cache in the request cycle
     */
    public static final String USE_CACHE = "usecache"; 
    /**
     * Constant used to store the contributor with this request in the request cycle
     */
    public static final String CONTRIBUTOR = "contributor";
    /**
     * Whether we are hitting the working db or not
     */
    public static final String WORKING = "working";
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		boolean useCache = true;		
		setUseGlossary(getIsGlossaryOn());
	    if (!getIsPublic()) {
	        setIsNonPublic(true);
	        // Dont use cache on beta or dev
	        useCache = false;
	    } else {
	        setIsNonPublic(false);
	    }
		if (getIsWorking()) {
		    // Dont use cache on working
		    useCache = false;
		}
		setUseCache(useCache);
        // try to authenticate -- in case the user is going to preview from TreeGrow
        getCookieAndContributorSource().authenticateExternalPage(cycle);
		setupRequestCycleAttributes();                 
    }
    
    public void pageBeginRender(PageEvent event) {
        setupRequestCycleAttributes();
        if (getIsWorking()) {
            Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
            if (!getCookieAndContributorSource().getCookieIsEnabled(CookieAndContributorSource.WORKING_COOKIE) && contr == null) {
            	redirectToLogin();
            }            
        }        
    }
    
    protected void redirectToLogin() {
        WorkingLoginPage page = (WorkingLoginPage) getRequestCycle().getPage("WorkingLoginPage");
        page.setCallback(new ExternalCallback(this, getRequestCycle().getListenerParameters()));
        throw new PageRedirectException(page);    	
    }
    
    public boolean getIsLive() {
        String host = getTapestryHelper().getDomainName();
        // DEVN: for background on why the below conditional is used, see the comments for 
        // the involved helper methods
        if (isBetaSite(host) || isDevSite(host)) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Determines if the host is really the beta development site. 
     * @param host the host the operating website
     * @return true indicates that it is the beta development site, otherwise false. 
     */
    private boolean isBetaSite(String host) {
    	return isDevelopment(host, "beta");
    }

    /**
     * Determines if the host is really the dev development site. 
     * @param host the host the operating website
     * @return true indicates that it is the beta development site, otherwise false.
     */
    private boolean isDevSite(String host) {
    	return isDevelopment(host, "dev");
    }    
    
    /**
     * Determines if the host is really a development site. 
     * @param host the host the operating website
     * @param devPrefix the host prefix used - likely either 'beta' or 'dev' 
     * @return true indicates that it is the beta development site, otherwise false.
     */
    private boolean isDevelopment(String host, String devPrefix) {
    	return host != null && (isNotFauxDevelopment(host, devPrefix));
    }
    
    /**
	 * Determines if a host is not a faux development site. 
	 * 
     * Here's some background:
     * The problem is http://beta.working.tolweb.org/ is actually the live site - and this 
     * means that people do things expecting it to not have any impact.  I've been burned by 
     * this.  Ideally, we've make it so that apache redirects beta.working.tolweb.org to the 
     * correct working.beta.tolweb.org.  But we do something to make it so this is at not showing 
     * the "oh gosh" beta box (note - the above is same for dev).
     * 
     * @param host the host the operating website
     * @param devPrefix the host prefix used - likely either 'beta' or 'dev'
     * @return true indicates that it is the beta development site, otherwise false.
     */
    private boolean isNotFauxDevelopment(String host, String devPrefix) {
    	return host.indexOf(devPrefix) != -1 && host.indexOf(devPrefix+".working") == -1;
    }
    
    public boolean getIsWorking() {
        String host = getTapestryHelper().getDomainName();
        String isWorking = getRequest().getParameterValue(WORKING);
        if (host != null && host.indexOf("working") != -1 || StringUtils.notEmpty(isWorking)) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean getIsPublic() {
        String host = getTapestryHelper().getDomainName();
        if (host != null && host.indexOf("working") != -1 || host != null && host.indexOf("approval") != -1) {
            return false;
        } else {
            return true;
        }
    }    
    
    public boolean getIsGlossaryOn() {
        return getCookieAndContributorSource().getCookieIsEnabled(CookieAndContributorSource.GLOSSARY_COOKIE);
    }

	public String getAuthorsString() {
	    return getAuthorsString(getContributors());
	}
	
	@SuppressWarnings("unchecked")
	protected String getAuthorsString(Collection contributors) {
	    ArrayList names = new ArrayList();
		Iterator it = contributors.iterator();
		while (it.hasNext()) {
		    PageContributor contr = (PageContributor) it.next();
		    if (contr.getIsAuthor()) {
		        names.add(contr.getContributor().getDisplayName());
		    }
		}
		return StringUtils.returnCommaAndJoinedString(names);	    
	}
	
	public boolean getInternetInfoOnTop() {
	    return false;
	}
	
	public boolean getHasRandomPics() {
	    return false;
	}

	protected String getSearchURL(IRequestCycle cycle) {
	    return getSearchURL(cycle, null);
	}
	
	protected String getSearchURL(IRequestCycle cycle, String taxonName) {
	    String host = getTapestryHelper().getDomainName();
	    if (host != null && host.indexOf("dev") != -1) {
	        host = "dev.tolweb.org";
	    } else if (host != null && host.indexOf("beta") != -1) {
	        host = "beta.tolweb.org";
	    } else {
	        host = "tolweb.org";
	    }
	    String searchUrl;
	    if (StringUtils.notEmpty(taxonName)) {	    	
	        searchUrl = "http://" + host + String.format(getConfiguration().getSearchUrl(), taxonName);
	    } else {
	        searchUrl = "http://" + host + "/tree/home.pages/groupsearch.html";
	    }
	    return searchUrl;
	}
	
	protected void setupRequestCycleAttributes() {
	    PageDAO pageDao;
	    NodeDAO nodeDao;
	    AccessoryPageDAO accPageDao;
	    boolean useCache = false;
	    if (getIsPublic()) {
	        pageDao = getPublicPageDAO();
	        nodeDao = getPublicNodeDAO();
	        accPageDao = getPublicAccessoryPageDAO();
	        useCache = true;
	    } else {
	        pageDao = getWorkingPageDAO();
	        nodeDao = getWorkingNodeDAO();
	        accPageDao = getWorkingAccessoryPageDAO();
	    }
	    getRequestCycle().setAttribute(NODE_DAO, nodeDao);
	    getRequestCycle().setAttribute(PAGE_DAO, pageDao);
	    getRequestCycle().setAttribute(ACC_PAGE_DAO, accPageDao);
	    getRequestCycle().setAttribute(USE_CACHE, Boolean.valueOf(useCache));
	    
		// Set up the contributor associated with this request -- various
		// components want this to see if the 'edit' link should be displayed
        Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
        if (contr != null) {
            getRequestCycle().setAttribute(CONTRIBUTOR, contr);
        }
	}
	
	public Contributor getEditingContributor() {
		return (Contributor) getRequestCycle().getAttribute(CONTRIBUTOR);
	}
	
	public PageDAO getPageDAO() {
	    return (PageDAO) getRequestCycle().getAttribute(PAGE_DAO);
	}
	
	public NodeDAO getNodeDAO() {
		return (NodeDAO) getRequestCycle().getAttribute(NODE_DAO);
	}
	
	public AccessoryPageDAO getAccessoryPageDAO() {
		return (AccessoryPageDAO) getRequestCycle().getAttribute(ACC_PAGE_DAO);
	}
	/**
	 * This actually needs to be overridden by subclasses!  Not a tapestry property
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public abstract Collection getContributors();
}
