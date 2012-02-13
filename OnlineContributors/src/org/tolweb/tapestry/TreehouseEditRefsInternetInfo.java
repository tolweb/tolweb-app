/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.ListEditMap;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.treegrow.page.InternetLink;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseEditRefsInternetInfo extends AbstractTreehouseEditingPage implements PageBeginRenderListener {
	public abstract String getNumExtraLinks();
	public abstract void setAddedLinks(boolean value);
	public abstract boolean getAddedLinks();
	public abstract void setLinksListEditMap(ListEditMap value);
	public abstract ListEditMap getLinksListEditMap();
	public abstract void setCurrentLink(InternetLink value);
	public abstract InternetLink getCurrentLink();
	
	public static final String PROGRESS_PROPERTY = "refsProgress";
	
	public int getStepNumber() {
	    if (getIsWebquest()) {
	        return 7;
	    } else {
	        return 4;
	    }
	}
	
	public void pageBeginRender(PageEvent event) {
	    initLinksMap();	    
	}
	
	@SuppressWarnings("unchecked")
	private void initLinksMap() {
	    ListEditMap linksMap = new ListEditMap();
	    Iterator it = getTreehouse().getInternetLinks().iterator();
	    while (it.hasNext()) {
	        InternetLink link = (InternetLink) it.next();
	        linksMap.add(Integer.valueOf(link.getOrder()), link);
	    }
	    setLinksListEditMap(linksMap);
	}
	
	public void synchronizeLink(IRequestCycle cycle) {
	    ListEditMap map = getLinksListEditMap();
	    InternetLink link = (InternetLink) map.getValue();
	    if (link == null) {
	        //displaySynchError();
	    }
	    setCurrentLink(link);
	}
	
	public void addLinksSubmit(IRequestCycle cycle) {
		String numExtraLinksStr = getNumExtraLinks();
		int numExtraLinks = -1;
		try {
			numExtraLinks = Integer.parseInt(numExtraLinksStr);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		MappedAccessoryPage treehouse = getTreehouse();
		for (int i = 0; i < numExtraLinks; i++) {
			InternetLink link = new InternetLink();
			link.setComments("");
			link.setSiteName("");
			link.setUrl("");
			treehouse.addToInternetLinks(link);
		}
		setAddedLinks(true);
	}
	
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}	
}
