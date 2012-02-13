/*
 * Created on Sep 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import java.util.Collection;

import net.sf.ehcache.Element;

import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CacheAccess {
    public static final byte ACCESSORY_PAGE_TEXT_CACHE = 0;
    public static final byte PAGE_TEXT_CACHE = 1;
    public String getOtherGroupsForNode(MappedNode node);
    public void setOtherGroupsForNode(MappedNode node, String value);
    public String getQuickNavForNode(MappedNode node);
    public void setQuickNavForNode(MappedNode node, String value);    
    public String getAccessoryPageText(MappedAccessoryPage page, boolean useGloss);
    public void setAccessoryPageText(MappedAccessoryPage page, String value, boolean useGloss);
    public String getAccessoryPageJavascript(MappedAccessoryPage page);
    public void setAccessoryPageJavascript(MappedAccessoryPage page, String value);
    public String getLinkedGroupsForPage(MappedAccessoryPage page);
    public void setLinkedGroupsForPage(MappedAccessoryPage page, String value);
    public String getTextSectionTextForPage(MappedPage value, boolean useGloss);
    public void setTextSectionTextForPage(MappedPage page, String value, boolean useGloss);
    public String getPageJavascript(MappedPage page);
    public void setPageJavascript(MappedPage page, String value);    
    public void setTitleIllustrationsForPage(MappedPage page, String value, boolean isJavascript);
    public String getTitleIllustrationsForPage(MappedPage page, boolean isJavascript);
    public void setTaxonListForPage(MappedPage page, String value);
    public String getTaxonListForPage(MappedPage page);
    public void setPageIsIncomplete(MappedPage page, Boolean isIncomplete);
    public Boolean getPageIsIncomplete(MappedPage page);
    
    public void evictAllPageObjectsFromCache(MappedPage page);
    public void evictAccessoryPageObjectsFromCache(MappedAccessoryPage page);
    public void evictContributorFromCache(Contributor contributor);
    public void evictImageFromCache(NodeImage image);
    public void evictNodesFromCache(Collection<Long> nodeIds);
}
