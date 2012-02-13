package org.tolweb.tapestry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class BranchLeafNavigationComponents extends BaseComponent implements 
		TreeGrowServerInjectable, PageInjectable, BaseInjectable, CookieInjectable,
		NodeInjectable, UserInjectable {
	public abstract void setParentNode(MappedNode node);
	public abstract MappedNode getParentNode();
	public abstract boolean getCurrentNodeDownloaded();
	public abstract void setCurrentNodeDownloaded(boolean value);
	public abstract boolean getCurrentNodeSubmitted();
	public abstract void setCurrentNodeSubmitted(boolean value);
	public abstract boolean getCurrentNodeNoPermissions();
	public abstract void setCurrentNodeNoPermissions(boolean value);
	public abstract boolean getCurrentNodeHasPage();
	public abstract void setCurrentNodeHasPage(boolean value);
	
	public abstract void setDownloadedNodesStatus(Hashtable<Long, Boolean> statusTable);
	public abstract Hashtable<Long, Boolean> getDownloadedNodesStatus();
	public abstract void setNodeIdsWithPages(Hashtable<Long, Boolean> nodesWithPagesTable);
	public abstract Hashtable<Long, Boolean> getNodeIdsWithPages();
	public abstract void setHasEditingPermissionsOverRoot(boolean value);
	public abstract boolean getHasEditingPermissionsOverRoot();
	
	@SuppressWarnings("unchecked")
	public void prepareForRender(IRequestCycle cycle) {
		super.prepareForRender(cycle);
		PageDAO dao = ((PageDAO) getPage().getRequestCycle().getAttribute(CacheAndPublicAwarePage.PAGE_DAO));
		setParentNode(dao.getNodeForPageNodeIsOn(getNode()));
		if (!cycle.isRewinding() && ((ViewBranchOrLeaf) getPage()).getIsWorking()) {
			// build up a list of node ids
			List nodes = ((ViewBranchOrLeaf) getPage()).getNamedNodesOnPage();
			if (nodes == null || nodes.size() == 0) {
				setDownloadedNodesStatus(new Hashtable<Long, Boolean>());
				setNodeIdsWithPages(new Hashtable<Long, Boolean>());
			} else {
				List nodeIds = new ArrayList<Long>();
				for (Iterator iter = nodes.iterator(); iter.hasNext();) {
					MappedNode nextNode = (MappedNode) iter.next();
					nodeIds.add(nextNode.getNodeId());
				}
				//long currentTime = System.currentTimeMillis();
				List downloadedNodeIds = getDownloadDAO().getNodesAreDownloaded(nodeIds);
				Hashtable<Long, Boolean> downloadedHash = new Hashtable<Long, Boolean>();
				constructHashFromNodeList(nodes, downloadedNodeIds, downloadedHash);
				setDownloadedNodesStatus(downloadedHash);
				List nodeIdsWithPages = (List) getWorkingPageDAO().getNodeIdsWithPages(nodeIds);
				Hashtable<Long, Boolean> pagesHash = new Hashtable<Long, Boolean>();
				constructHashFromNodeList(nodes, nodeIdsWithPages, pagesHash);
				setNodeIdsWithPages(pagesHash);
				Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();				
				setHasEditingPermissionsOverRoot(getPermissionChecker().checkHasEditingPermissionForNode(contr, getNode().getNodeId()));
			}
		}
	}  
	
	@SuppressWarnings("unchecked")
	private void constructHashFromNodeList(List nodes, List toCheck, Hashtable<Long, Boolean> table) {
		for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			MappedNode nextNode = (MappedNode) iter.next();
			boolean nodeIsDownloaded = toCheck.contains(nextNode.getNodeId());
			table.put(nextNode.getNodeId(), nodeIsDownloaded);
		}		
	}
	
    public MappedPage getTolPage() {
        return ((AbstractBranchOrLeafPage) getPage()).getTolPage();
    }
    public MappedNode getNode() {
        return ((AbstractBranchOrLeafPage) getPage()).getNode();        
    }
    public boolean getHasChildren() {
        NodeDAO dao = (NodeDAO) getPage().getRequestCycle().getAttribute(CacheAndPublicAwarePage.NODE_DAO);
        Integer numChildren = dao.getNumChildrenForNode(getNode()); 
        return  numChildren != null && numChildren.intValue() > 0;
    }    
    public boolean getShowOnlyTaxonLists() {
        return getCookieAndContributorSource().getCookieIsEnabled(CookieAndContributorSource.TAXON_LIST_COOKIE);
    }    
    public boolean getShowTreeDiagram() {
        return !getTolPage().getWriteAsList() && !getShowOnlyTaxonLists() && 
            !getNode().getIsLeaf() && getHasChildren();
    }  
    public String getTreeOrListString() {
        if (getShowTreeDiagram()) {
            return "tree";
        } else {
            return "taxon list";
        }
    }
    public boolean getShowAfterTreeDiv() {
        return StringUtils.notEmpty(getTolPage().getPostTreeText()) || getHasIncompleteSubgroups();
    }
    
    public String getBasicPageTitle() {
        return getNode().getActualPageTitle(false, false, false);
    }    
    public void setWriteAsList(IRequestCycle cycle) {
        ((CacheAndPublicAwarePage) getPage()).setupRequestCycleAttributes();
        setWriteAsListForPage(true, cycle);
    }
    public void setShowTree(IRequestCycle cycle) {
        ((CacheAndPublicAwarePage) getPage()).setupRequestCycleAttributes();
        setWriteAsListForPage(false, cycle);        
    } 
    private void setWriteAsListForPage(boolean value, IRequestCycle cycle) {
        ((CacheAndPublicAwarePage) getPage()).setupRequestCycleAttributes();
        Long pageId = (Long) cycle.getListenerParameters()[0];
        PageDAO pageDao = getWorkingPageDAO();
        MappedPage page = pageDao.getPageWithId(pageId);
        page.setWriteAsList(value);
        pageDao.savePage(page); 
        Contributor contr = (Contributor) cycle.getAttribute(CacheAndPublicAwarePage.CONTRIBUTOR);
        getEditedPageDAO().addEditedPageForContributor(page, contr);
        ((AbstractBranchOrLeafPage) getPage()).refreshToPage(page);
    }  
    
    public String getOpenContainingGroupEm() {
        return getGroupEm(false);
    }
    public String getCloseContainingGroupEm() {
        return getGroupEm(true);
    }
    private String getGroupEm(boolean isClose) {
        MappedNode parent = getParentNode();
        if (parent != null) {        
        	MappedOtherName supertitle = (MappedOtherName) parent.getFirstPreferredOtherName();
        	boolean italicizeName = supertitle != null ? supertitle.getItalicize() : parent.getItalicizeName(); 
            String closeSlash = isClose ? "/" : "";
            return italicizeName ? "<" + closeSlash + "em>" : null;
        } else {
            return null;
        }        
    }
    public String getParentGroupName() {
        MappedNode parent = getParentNode();
        if (parent != null) {
            return parent.getActualPageTitle(false, false, true);
        } else {
            return "";
        }
    }
    public String getParentGroupLinkString() {
    	Long nodeId = getParentNode().getNodeId();
    	return getUrlBuilder().getURLForBranchPage(URLBuilder.NO_HOST_PREFIX, getParentGroupName(), nodeId);
    }
    public String getSubgroupTitleString() {
    	if (getCurrentNodeHasPage()) {
    		return "edit page";
    	} else {
    		return "edit taxon name";
    	}
    }
    public boolean getShowAddSubgroupsLink() {
    	return !getCurrentNode().getIsLeaf() && !getCurrentNodeDownloaded();
    }
    public boolean getShowAddPageLink() {
    	return !getCurrentNodeHasPage() && !getCurrentNodeDownloaded(); 
    }
    public MappedNode getCurrentNode() {
    	return ((ViewBranchOrLeaf) getPage()).getCurrentNode();
    }
    public void createPageForNode(Long pageNodeId, Long nodeIdToAddPageTo) {
    	MappedNode nodeToAddPageTo = getWorkingNodeDAO().getNodeWithId(nodeIdToAddPageTo);
    	Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie(); 
    	MappedPage newPage = getWorkingPageDAO().addPageForNode(nodeToAddPageTo, contr);
    	getEditedPageDAO().addEditedPageForContributor(newPage.getPageId(), contr);
    	((ViewBranchOrLeaf) getPage()).setNodeWithNewPageName(nodeToAddPageTo.getName());
    	((ViewBranchOrLeaf) getPage()).redirectToThisPage(getPage().getRequestCycle());
    }
    /**
     * do db queries to establish certain info about the node
     */
    public void establishCachedNodeValues() {
    	// do various db queries to gather info about the node and then store them
    	// for rendering purposes
    	//Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
    	//Long nodeId = getCurrentNode().getNodeId();
    	// TODO: DANNY -- this is too slow right now, would be best to come up with an en masse query
    	// that was performant
    	//setCurrentNodeNoPermissions(!getPermissionChecker().checkHasPermissionForNode(contr, nodeId));
    	setCurrentNodeNoPermissions(false);
    	setCurrentNodeDownloaded(getNodeIsDownloaded(getCurrentNode()));
    	setCurrentNodeSubmitted(getCurrentNode().getIsSubmitted() && !getHasEditingPermissionsOverRoot());
    	setCurrentNodeHasPage(getNodeHasPage(getCurrentNode()));
    }
    private boolean getNodeIsDownloaded(MappedNode node) {
    	return getBooleanForNodeFromHashtable(node, getDownloadedNodesStatus());
    }
    private boolean getNodeHasPage(MappedNode node) {
    	return getBooleanForNodeFromHashtable(node, getNodeIdsWithPages());
    }
    private boolean getBooleanForNodeFromHashtable(MappedNode node, Hashtable<Long, Boolean> hash) {
    	if (node != null && hash.containsKey(node.getNodeId())) {
    		return hash.get(node.getNodeId());
    	} else {
    		return false;
    	}    	
    }
    public boolean getShowRootAddSubgroupsLink() {
    	return !getDownloadDAO().getNodeIsDownloaded(((ViewBranchOrLeaf) getPage()).getNode().getNodeId());
    }
    public boolean getHasIncompleteSubgroups() {
    	boolean isPublic = ((ViewBranchOrLeaf) getPage()).getIsPublic();
    	// check the cache if it's the public page
    	if (isPublic) {
    		Boolean isIncomplete = getCacheAccess().getPageIsIncomplete(getTolPage());
    		if (isIncomplete != null) {
    			return isIncomplete;
    		}
    	}
    	boolean childOnPageWithoutPageHasIncomplete = false;
    	boolean rootHasIncomplete = ((ViewBranchOrLeaf) getPage()).getNode().getHasIncompleteSubgroups(); 
    	if (!rootHasIncomplete) {
    		// get all of the nodes on that page that are marked incomplete
    		List<Long> nodeIdsWithIncSubgroups = ((ViewBranchOrLeaf) getPage()).getPageDAO().getNodeIdsOnPageWithIncompleteSubgroups(getTolPage().getPageId());
    		if (nodeIdsWithIncSubgroups != null && nodeIdsWithIncSubgroups.size() > 0) {
    			// check to see if any don't have pages
    			Collection<Long> nodeIdsWithPages = ((ViewBranchOrLeaf) getPage()).getPageDAO().getNodeIdsWithPages(nodeIdsWithIncSubgroups);
    			// show the message if there is at least one descendant
    			// node on the page that doesn't have its own page
    			childOnPageWithoutPageHasIncomplete = nodeIdsWithPages != null &&
    				nodeIdsWithPages.size() != nodeIdsWithIncSubgroups.size();
    		}
    	}
    	boolean returnValue = rootHasIncomplete || childOnPageWithoutPageHasIncomplete;
    	if (isPublic) {
    		getCacheAccess().setPageIsIncomplete(getTolPage(), returnValue);
    	}
    	return returnValue;
    }
}
