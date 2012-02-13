/*
 * Created on Sep 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.AccessoryPageNode;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class LinkedGroups extends CachedOutputComponent implements PageInjectable, BaseInjectable {
    public abstract MappedAccessoryPage getAccPage();
    public abstract Collection getLinkedGroups();
    public abstract void setLinkedGroups(Collection value);
	public abstract MappedNode getCurrentLinkedGroup();
	
	public abstract ArrayList getProcessedLinkedGroups();
	public abstract void setProcessedLinkedGroups(ArrayList groups);
	
    public void prepareForRender(IRequestCycle cycle) {
        super.prepareForRender(cycle);
	    ArrayList returnList = new ArrayList();
	    Iterator it = getLinkedGroups().iterator();
	    while (it.hasNext()) {
	        AccessoryPageNode nd = (AccessoryPageNode) it.next();
	        if (nd.getIsPrimaryAttachedNode()) {
	            returnList.add(0, nd.getNode());
	        } else {
	            returnList.add(nd.getNode());
	        }
	    }
	    //setLinkedGroups(returnList);
	    setProcessedLinkedGroups(returnList);
    }
    
	
	public String getCurrentLinkedGroupLinkString() {
		URLBuilder builder = getUrlBuilder();
	    return builder.getURLForNode(getCurrentLinkedGroup());
	}
	
	public boolean getCurrentLinkedGroupHasPage() {
	    PageDAO pageDao = getPageDAO();
	    return pageDao.getNodeHasPage(getCurrentLinkedGroup());
	}   
	
	protected String getCachedOutput() {
	    String result = getCacheAccess().getLinkedGroupsForPage(getAccPage());
	    return result;
	}
	
	protected void setCachedOutput(String value) {
	    getCacheAccess().setLinkedGroupsForPage(getAccPage(), value);
	}
}
