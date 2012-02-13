/*
 * Created on Nov 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class FlushCache extends BasePage implements IExternalPage, RequestInjectable, 
		PageInjectable, ImageInjectable, UserInjectable, BaseInjectable, AccessoryInjectable {
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        String pageId = getRequest().getParameterValue(RequestParameters.PAGE_ID);
        String contributorId = getRequest().getParameterValue(RequestParameters.CONTRIBUTOR_ID);
        String imageId = getRequest().getParameterValue(RequestParameters.IMAGE_ID);
        String accessoryPageId = getRequest().getParameterValue(RequestParameters.ACCESSORY_PAGE_ID);
        if (StringUtils.notEmpty(pageId)) {
        	// this can be multiple page ids comma-separated
        	String[] pageIds = pageId.split(",");
        	for (String pageIdString : pageIds) {
    	        MappedPage page = getPublicPageDAO().getPageWithId(Long.valueOf(pageIdString));
    	        if (page != null) {
    	            getCacheAccess().evictAllPageObjectsFromCache(page);
    	        }				
			}
        } else if (contributorId != null) {
            Contributor contributor = getContributorDAO().getContributorWithId(contributorId);
            if (contributor != null) {
                getCacheAccess().evictContributorFromCache(contributor);
            }
        } else if (imageId != null) {
            NodeImage image = getImageDAO().getImageWithId(Integer.parseInt(imageId));
            if (image != null) {
                getCacheAccess().evictImageFromCache(image);
            }
        } else if (accessoryPageId != null) {
        	MappedAccessoryPage page = getPublicAccessoryPageDAO().getAccessoryPageWithId(Long.valueOf(accessoryPageId));
        	if (page != null) {
        		getCacheAccess().evictAccessoryPageObjectsFromCache(page);
        	}
        }
    }
}
