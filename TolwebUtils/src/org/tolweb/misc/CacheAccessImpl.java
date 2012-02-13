/*
 * Created on Sep 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.TreeGifDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.TreeGif;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CacheAccessImpl implements CacheAccess {
    private Cache otherGroupsCache;
    private Cache accessoryPagesCache;
    private Cache linkedGroupsCache;
    private Cache javascriptCache;
    private Cache textSectionsCache;
    private Cache titleIllustrationsCache;
    private Cache taxonListsCache;
    private Cache quickNavCache;
    private Cache isIncompleteCache;

    private ImageDAO imageDAO;

    private ContributorDAO contributorDAO;

    private NodeDAO nodeDAO;
    private NodeDAO workingNodeDAO;

    private PageDAO pageDAO;

    private AccessoryPageDAO accessoryPageDAO;

    private TreeGifDAO treeGifDAO;

    private String lookupStringValue(Cache cache, Serializable cacheKey) {
        try {
            Element cachedElement = cache.get(cacheKey);
            if (cachedElement != null) {
                return (String) cachedElement.getValue();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getOtherGroupsForNode(MappedNode node) {
        return lookupStringValue(getOtherGroupsCache(), getNodeCacheKey(node));
    }

    public void setOtherGroupsForNode(MappedNode node, String value) {
        getOtherGroupsCache().put(new Element(getNodeCacheKey(node), value));
    }

    public String getAccessoryPageText(MappedAccessoryPage page,
            boolean useGloss) {
        String cacheKey = getAccessoryPageCacheKey(page);
        if (useGloss) {
            cacheKey = getGlossaryCacheKey(cacheKey);
        }
        return lookupStringValue(getAccessoryPagesCache(), cacheKey);
    }

    public void setAccessoryPageText(MappedAccessoryPage page, String value,
            boolean useGloss) {
        String cacheKey = getAccessoryPageCacheKey(page);
        if (useGloss) {
            cacheKey = getGlossaryCacheKey(cacheKey);
        }
        getAccessoryPagesCache().put(new Element(cacheKey, value));
    }

    public String getAccessoryPageJavascript(MappedAccessoryPage page) {
        String cacheKey = getJavascriptVarCacheKey(getAccessoryPageCacheKey(page));
        return lookupStringValue(getJavascriptCache(), cacheKey);
    }

    public void setAccessoryPageJavascript(MappedAccessoryPage page,
            String value) {
        String cacheKey = getJavascriptVarCacheKey(getAccessoryPageCacheKey(page));
        getJavascriptCache().put(new Element(cacheKey, value));
    }

    public String getLinkedGroupsForPage(MappedAccessoryPage page) {
        return lookupStringValue(getLinkedGroupsCache(),
                getAccessoryPageCacheKey(page));
    }

    public void setLinkedGroupsForPage(MappedAccessoryPage page, String value) {
        getLinkedGroupsCache().put(
                new Element(getAccessoryPageCacheKey(page), value));
    }

    public String getTextSectionTextForPage(MappedPage page, boolean useGloss) {
        String cacheKey = getPageCacheKey(page);
        if (useGloss) {
            cacheKey = getGlossaryCacheKey(cacheKey);
        }
        String value = lookupStringValue(getTextSectionsCache(), cacheKey);
        if (value != null) {
            try {
                byte[] iso88591bytes = value.getBytes("ISO-8859-1");
                String returnString = new String(iso88591bytes, "ISO-8859-1");
                return returnString;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setTextSectionTextForPage(MappedPage page, String value,
            boolean useGloss) {
        String cacheKey = getPageCacheKey(page);
        if (useGloss) {
            cacheKey = getGlossaryCacheKey(cacheKey);
        }
        getTextSectionsCache().put(new Element(cacheKey, value));
    }

    public String getPageJavascript(MappedPage page) {
        String cacheKey = getPageJavascriptVarCacheKey(getPageCacheKey(page));
        return lookupStringValue(getJavascriptCache(), cacheKey);
    }

    public void setPageJavascript(MappedPage page, String value) {
        String cacheKey = getPageJavascriptVarCacheKey(getPageCacheKey(page));
        getJavascriptCache().put(new Element(cacheKey, value));
    }

    public void setTitleIllustrationsForPage(MappedPage page, String value,
            boolean isJavascript) {
        String cacheKey = getPageCacheKey(page);
        if (isJavascript) {
            cacheKey = getJavascriptVarCacheKey(cacheKey);
        }
        getTitleIllustrationsCache().put(new Element(cacheKey, value));
    }

    public String getTitleIllustrationsForPage(MappedPage page,
            boolean isJavascript) {
        String cacheKey = getPageCacheKey(page);
        if (isJavascript) {
            cacheKey = getJavascriptVarCacheKey(cacheKey);
        }
        return lookupStringValue(getTitleIllustrationsCache(), cacheKey);
    }
    
    public void setPageIsIncomplete(MappedPage page, Boolean isIncomplete) {
    	getIsIncompleteCache().put(new Element(getPageCacheKey(page), "" + isIncomplete));
    }
    
    public Boolean getPageIsIncomplete(MappedPage page) {
    	String boolString = lookupStringValue(getIsIncompleteCache(), getPageCacheKey(page));
    	if (StringUtils.notEmpty(boolString)) {
    		return Boolean.valueOf(boolString);
    	} else {
    		return null;
    	}
    }

    public void setTaxonListForPage(MappedPage page, String value) {
        getTaxonListsCache().put(new Element(getPageCacheKey(page), value));
    }

    public String getTaxonListForPage(MappedPage page) {
        return lookupStringValue(getTaxonListsCache(), getPageCacheKey(page));
    }

    private String getPageCacheKey(MappedPage page) {
        return page.getPageId().toString();
    }

    private String getAccessoryPageCacheKey(MappedAccessoryPage page) {
        return page.getAccessoryPageId().toString();
    }

    private Long getNodeCacheKey(MappedNode node) {
        return node.getNodeId();
    }

    private String getGlossaryCacheKey(String originalCacheKey) {
        return originalCacheKey + "G";
    }

    private String getJavascriptVarCacheKey(String originalCacheKey) {
        return originalCacheKey + "J";
    }

    private String getPageJavascriptVarCacheKey(String originalCacheKey) {
        return originalCacheKey + "Jp";
    }

    public void evictAllPageObjectsFromCache(MappedPage page) {
        // First get all of the component output caches
        MappedNode node = page.getMappedNode();
        if (node != null) {
            getOtherGroupsCache().remove(getNodeCacheKey(node));
            List articles = accessoryPageDAO.getArticlesForNode(node);
            clearCacheForAccessoryPages(articles);
            List notes = accessoryPageDAO.getNotesForNode(node);
            clearCacheForAccessoryPages(notes);
            List treehouses = accessoryPageDAO.getTreehousesForNode(node);
            clearCacheForAccessoryPages(treehouses);
        }
        getTextSectionsCache().remove(getPageCacheKey(page));
        getTextSectionsCache().remove(
                getGlossaryCacheKey(getPageCacheKey(page)));
        getJavascriptCache().remove(
                getPageJavascriptVarCacheKey(getPageCacheKey(page)));
        getTitleIllustrationsCache().remove(getPageCacheKey(page));
        getTitleIllustrationsCache().remove(
                getJavascriptVarCacheKey(getPageCacheKey(page)));
        getTaxonListsCache().remove(getPageCacheKey(page));
        if (node != null) {
			getQuickNavCache().remove(getNodeCacheKey(node));
		}
		getIsIncompleteCache().remove(getPageCacheKey(page));
        // Do the nodes here
        List nodes = getPageDAO().getNodesOnPage(page);
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            MappedNode nd = (MappedNode) it.next();
            getNodeDAO().clearCacheForNode(nd);
        }
        boolean treeGifExists = getTreeGifDAO().getTreeGifExistsForPage(page);
        if (treeGifExists) {
            TreeGif treeGif = getTreeGifDAO().getTreeGifForPage(page);
            getTreeGifDAO().clearCacheForTreeGif(treeGif);
        }
        getNodeDAO().clearCacheForNode(node);
        getPageDAO().clearCacheForPage(page);
    }

    private void clearCacheForAccessoryPages(List pages) {
        Iterator it = pages.iterator();
        while (it.hasNext()) {
            // System.out.println("next object is: " + it.next().getClass());
            Object[] nextPageId = (Object[]) it.next();
            // System.out.println("first element is: " +
            // nextPageId[0].getClass() + " second element is: " +
            // nextPageId[1].getClass());
            Long actualId = (Long) nextPageId[1];
            MappedAccessoryPage page = accessoryPageDAO
                    .getAccessoryPageWithId(actualId);
            if (page != null) {
                evictAccessoryPageObjectsFromCache(page);
            }
        }
    }

    public void evictAccessoryPageObjectsFromCache(MappedAccessoryPage page) {
        // remove both the glossary and non-glossary prepared text
        getAccessoryPagesCache().remove(getAccessoryPageCacheKey(page));
        getAccessoryPagesCache().remove(
                getGlossaryCacheKey(getAccessoryPageCacheKey(page)));
        getJavascriptCache().remove(
                getJavascriptVarCacheKey(getAccessoryPageCacheKey(page)));
        getLinkedGroupsCache().remove(getAccessoryPageCacheKey(page));
        getAccessoryPageDAO().clearCacheForPage(page);
    }

    public void evictContributorFromCache(Contributor contributor) {
        getContributorDAO().clearCacheForContributor(contributor);
    }

    public void evictImageFromCache(NodeImage image) {
        getImageDAO().clearCacheForImage(image);
    }
    
    public void evictNodesFromCache(Collection<Long> nodeIds) {
    	getWorkingNodeDAO().clearCacheForNodeIds(nodeIds);
    }

    public AccessoryPageDAO getAccessoryPageDAO() {
        return accessoryPageDAO;
    }

    public void setAccessoryPageDAO(AccessoryPageDAO accessoryPageDAO) {
        this.accessoryPageDAO = accessoryPageDAO;
    }

    public ContributorDAO getContributorDAO() {
        return contributorDAO;
    }

    public void setContributorDAO(ContributorDAO contributorDAO) {
        this.contributorDAO = contributorDAO;
    }

    public ImageDAO getImageDAO() {
        return imageDAO;
    }

    public void setImageDAO(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    public NodeDAO getNodeDAO() {
        return nodeDAO;
    }

    public void setNodeDAO(NodeDAO nodeDAO) {
        this.nodeDAO = nodeDAO;
    }

    public PageDAO getPageDAO() {
        return pageDAO;
    }

    public void setPageDAO(PageDAO pageDAO) {
        this.pageDAO = pageDAO;
    }

    public TreeGifDAO getTreeGifDAO() {
        return treeGifDAO;
    }

    public void setTreeGifDAO(TreeGifDAO treeGifDAO) {
        this.treeGifDAO = treeGifDAO;
    }

    public String getQuickNavForNode(MappedNode node) {
        return lookupStringValue(getQuickNavCache(), getNodeCacheKey(node));
    }

    public void setQuickNavForNode(MappedNode node, String value) {
        getQuickNavCache().put(new Element(getNodeCacheKey(node), value));
    }

    /**
     * @return Returns the quickNavCache.
     */
    public Cache getQuickNavCache() {
        return quickNavCache;
    }

    /**
     * @param quickNavCache The quickNavCache to set.
     */
    public void setQuickNavCache(Cache quickNavCache) {
        this.quickNavCache = quickNavCache;
    }

    /**
     * @return Returns the accessoryPagesCache.
     */
    public Cache getAccessoryPagesCache() {
        return accessoryPagesCache;
    }

    /**
     * @param accessoryPagesCache The accessoryPagesCache to set.
     */
    public void setAccessoryPagesCache(Cache accessoryPageCache) {
        this.accessoryPagesCache = accessoryPageCache;
    }

    /**
     * @return Returns the javascriptCache.
     */
    public Cache getJavascriptCache() {
        return javascriptCache;
    }

    /**
     * @param javascriptCache The javascriptCache to set.
     */
    public void setJavascriptCache(Cache javascriptCache) {
        this.javascriptCache = javascriptCache;
    }

    /**
     * @return Returns the linkedGroupsCache.
     */
    public Cache getLinkedGroupsCache() {
        return linkedGroupsCache;
    }

    /**
     * @param linkedGroupsCache The linkedGroupsCache to set.
     */
    public void setLinkedGroupsCache(Cache linkedGroupsCache) {
        this.linkedGroupsCache = linkedGroupsCache;
    }

    /**
     * @return Returns the otherGroupsCache.
     */
    public Cache getOtherGroupsCache() {
        return otherGroupsCache;
    }

    /**
     * @param otherGroupsCache The otherGroupsCache to set.
     */
    public void setOtherGroupsCache(Cache otherGroupsCache) {
        this.otherGroupsCache = otherGroupsCache;
    }

    /**
     * @return Returns the taxonListsCache.
     */
    public Cache getTaxonListsCache() {
        return taxonListsCache;
    }

    /**
     * @param taxonListsCache The taxonListsCache to set.
     */
    public void setTaxonListsCache(Cache taxonListsCache) {
        this.taxonListsCache = taxonListsCache;
    }

    /**
     * @return Returns the textSectionsCache.
     */
    public Cache getTextSectionsCache() {
        return textSectionsCache;
    }

    /**
     * @param textSectionsCache The textSectionsCache to set.
     */
    public void setTextSectionsCache(Cache textSectionsCache) {
        this.textSectionsCache = textSectionsCache;
    }

    /**
     * @return Returns the titleIllustrationsCache.
     */
    public Cache getTitleIllustrationsCache() {
        return titleIllustrationsCache;
    }

    /**
     * @param titleIllustrationsCache The titleIllustrationsCache to set.
     */
    public void setTitleIllustrationsCache(Cache titleIllustrationsCache) {
        this.titleIllustrationsCache = titleIllustrationsCache;
    }

	public NodeDAO getWorkingNodeDAO() {
		return workingNodeDAO;
	}

	public void setWorkingNodeDAO(NodeDAO workingNodeDAO) {
		this.workingNodeDAO = workingNodeDAO;
	}

	public Cache getIsIncompleteCache() {
		return isIncompleteCache;
	}

	public void setIsIncompleteCache(Cache isIncompleteCache) {
		this.isIncompleteCache = isIncompleteCache;
	}
}
