/*
 * Created on Jul 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.contrib.table.components.Table;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.ImageDAO;
import org.tolweb.misc.ContributorNameComparator;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ImageContributorsPage extends BasePage implements PageBeginRenderListener, ImageInjectable, 
		UserInjectable, MiscInjectable {
	@SuppressWarnings("unchecked")
	public abstract List getContributors();
	@SuppressWarnings("unchecked")
	public abstract void setContributors(List value);
	
	@SuppressWarnings("unchecked")
	public void pageBeginRender(PageEvent event) {
	    if (getContributors() == null) {	
	        Set contributorIds = getImageDAO().getDistinctImageContributorsIds();
	        List contributors = getContributorDAO().getContributorsWithIds(contributorIds, true);
	        Collections.sort(contributors, new ContributorNameComparator());
	        setContributors(contributors);
	    }
	}
    
    public AlphabeticalTableModel getTableModel() {
        return getTapestryHelper().getAlphabeticalTableModel("displayName, institution, numImages", this, getContributors().toArray());
    }
    
	public Integer getNumImagesForContributor() {
		Contributor contributor = ((Contributor) ((Table) getComponents().get("table")).getTableRow());
		ImageDAO imgDao = getImageDAO();
		return imgDao.getNumImagesForContributor(contributor);
	}
}
