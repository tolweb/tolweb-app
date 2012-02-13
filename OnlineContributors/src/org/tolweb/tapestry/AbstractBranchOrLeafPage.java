package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.ArchivedPageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.ArchivedPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeNameComparator;
import org.tolweb.treegrow.main.StringUtils;

public abstract class AbstractBranchOrLeafPage extends CacheAndPublicAwarePage implements NodeInjectable, 
		PageInjectable, BaseInjectable, PageDetachListener {
	private MappedNode node;
	
	public abstract MappedPage getTolPage();
	public abstract void setTolPage(MappedPage value);
	public abstract MappedNode getCurrentNode();
	public abstract void setCurrentNode(MappedNode value);
	public abstract String getSelectedAnchor();
	public abstract void setSelectedAnchor(String value);
	public abstract boolean getOpenSubgroups();
	public abstract void setOpenSubgroups(boolean value);
    
    public void pageDetached(PageEvent event) {
    	node = null;
    }
    
    public void doNodeActivate(MappedNode node, IRequestCycle cycle) {
    	Object[] parameters = new Object[] {node.getName(), node.getNodeId().toString()};
    	activateExternalPage(parameters, cycle);
    	cycle.activate(this);
    }
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        super.activateExternalPage(parameters, cycle);
		String groupName = null;
		String nodeIdString = null;
		String dateString = null;
		boolean inactiveFetch = false;
		
	    String searchUrl = getSearchURL(cycle);
	    try {
	        groupName = (String) parameters[0];
	        if (StringUtils.isEmpty(groupName)) {
	        	// an empty group name is not acceptable
	        	throw new RuntimeException();
	        }
	    } catch (Exception e) {
	        gotoErrorPage(searchUrl);
	    }
	    groupName = groupName.replace('_', ' ');
		if (parameters.length > 1) {
			nodeIdString = (String) parameters[1];
			if (parameters.length > 2) {
				dateString = (String) parameters[2];
			}
		}	 
		Long nodeIdLong = null;
		if (StringUtils.notEmpty(nodeIdString)) {
			try {
				nodeIdLong = Long.valueOf(nodeIdString);
			} catch (Exception e) {}
		}

	    searchUrl = getSearchURL(cycle, groupName);
	    Date archiveDate = null;
	    if (StringUtils.notEmpty(dateString)) {
			archiveDate = getUrlBuilder().parseArchiveDateFromURLString(dateString);
			if (archiveDate == null) {
				inactiveFetch = true;
			}
	    }
    	findNodeAndPageBasedOnName(groupName, searchUrl, nodeIdLong, archiveDate, inactiveFetch);
    }
	
	public String getGroupName() {
		return getTolPage().getGroupName();
	}    
	protected void gotoErrorPage(String searchUrl) {
		throw new RedirectException(searchUrl);
	}
	protected void gotoErrorPage() {
		throw new RedirectException(getSearchURL(getRequestCycle()));
	}
	/**
		// logic:
		// if (nameisNumeric) {
		//   treat name as id and look for a node with that id
		// } else {
		//   get nodes w/ name matching name
		//   if nodes match:
		//     node is first match
		//     if more than one node:
		//       loop over nodes and find the one that has the id.  use it
		//   else:
		//     use the id and pick a node with that id
		// }
	 * @param groupName
	 * @param searchUrl
	 */
	@SuppressWarnings("unchecked")
	private void findNodeAndPageBasedOnName(String groupName, String searchUrl, Long nodeId, Date archiveDate, boolean inactiveFetch) {
		MappedNode node = null;
		MappedPage page = null;
		if (StringUtils.getIsNumeric(groupName)) {
			nodeId = Long.valueOf(groupName);
			node = getNodeDAO().getNodeWithId(nodeId, inactiveFetch);
			if (node != null) {
				setNode(node);
				setTolPage(getPageForNode(node, inactiveFetch));
			} else {
				gotoErrorPage(searchUrl);
			}
		} else {
		    List nodes;
		    try {
	            nodes = getNodeDAO().findNodesExactlyNamed(groupName);
		    } catch (Exception e) {
		        nodes = new ArrayList();
		    }
		    if (nodes.size() == 0) {
		    	// no name match, so
		    	// check to see if there is an id
		    	if (nodeId != null) {
		    		node = getNodeDAO().getNodeWithId(nodeId, inactiveFetch);
		    		if (node != null) {
		    			// found it so check to see if it has a page
		    			// if it does, then set it
		    			page = getPageForNode(node, inactiveFetch); 
		    			setTolPage(page);
		    			setNode(page.getMappedNode());
		    			return;
		    		}
		    	}
		    	// check the leading slash since it can't be placed in the url
		    	// and some groups of fungi start /s
		    	if (!groupName.startsWith("/")) {
		    		findNodeAndPageBasedOnName("/" + groupName, searchUrl, nodeId, archiveDate, inactiveFetch);
		    		return;
		    	} else {
		    		gotoErrorPage(searchUrl);
		    	}
		    }	    
		    try {
		    	node = (MappedNode) nodes.get(0);
		    	// only verify the id if there is more than one name match
		    	// and the id is not null
		    	if (nodes.size() > 1 && nodeId != null) {
				    Iterator it = nodes.iterator();
				    while (it.hasNext() && page == null) {
				        MappedNode nextNode = (MappedNode) it.next();
				        if (nextNode.getNodeId().equals(nodeId)) {
				        	node = nextNode;
				        	break;
				        }
				    }
		    	}
		    	page = getPageForNode(node, inactiveFetch);
			    if (node == null || page == null) {
			        throw new RuntimeException();
			    } else {
			    	if (archiveDate != null) {
				    	ArchivedPageDAO dao = getArchivedPageDAO(); 
				    	ArchivedPage archivedPage = dao.getArchivedPageForPageClosestToDate(page.getPageId(), archiveDate);
				    	if (archivedPage != null) {
				    		// check the date to see if it's the latest version.  if it is, then
				    		// there is no need to redirect
				    		Date latestRevisionDate = dao.getMostRecentVersionDateForPage(page.getPageId());
				    		if (!latestRevisionDate.equals(archivedPage.getArchiveDate())) {
				    			String currentURL = getUrlBuilder().getPublicURLForObject(page);
					    		String archivedFileContent = getPageArchiver().getArchivedPageContent(archivedPage, currentURL);
					    		if (StringUtils.notEmpty(archivedFileContent)) {
					    			IPage archivePage = getRequestCycle().getPage("ArchivedPageDisplay");
					    			PropertyUtils.write(archivePage, "contentString", archivedFileContent);
					    			throw new PageRedirectException(archivePage);
					    		}				    			
				    		}
				    	}
			    	}
			    	setTolPage(page);			    	
			    	setNode(page.getMappedNode());
			    }
		    } catch (RedirectException e) {
		    	throw e;
		    } catch (PageRedirectException e) {
		    	throw e;
		    } catch (Exception e) {
		        gotoErrorPage(searchUrl);
		    }		
		}
	}
	
	private MappedPage getPageForNode(MappedNode node, boolean inactiveFetch) {
		MappedPage page = null;
		if (!inactiveFetch) {
			page = getPageDAO().getPage(node);
		} else {
			page = getPageDAO().getPageForNode(node);
		}
		return page;
	}    
    
	@SuppressWarnings("unchecked")
    public List getNamedNodesOnPage() {
        List nodes = ((PageDAO) getRequestCycle().getAttribute(PAGE_DAO)).getNodesOnPage(getTolPage(), true);
        Collections.sort(nodes, new NodeNameComparator());
        return nodes;
    }
    public void refreshToPage(MappedPage page) {
    	String host = getTapestryHelper().getDomainName();
    	String hostPrefix = "http://" + host + "/";
    	String url = getUrlBuilder().getURLForBranchPage(hostPrefix, page.getGroupName(), page.getMappedNode().getNodeId());
        throw new RedirectException(url);
    }    
    public Object[] getCurrentNodeEditPageParameters() {
        return getParams(getCurrentNode().getNodeId());
    }
    public Long getPageId() {
    	return getTolPage().getPageId();
    }
    public void setPageId(Long value) {
    	MappedPage tolPage =  getWorkingPageDAO().getPageWithId(value);
    	setTolPage(tolPage);
    }    
    protected Object[] getParams(Long nodeId) {
        Object[] params = new Object[3];
        Contributor contr = getEditingContributor();
        params[0] = contr.getEmail();
        params[1] = contr.getPassword();
        params[2] = nodeId;
        return params;        
    }   
    protected MappedPage getPageFromRequestCycle(IRequestCycle cycle) {
        setupRequestCycleAttributes();
        Long pageId = (Long) cycle.getListenerParameters()[0];
        return getWorkingPageDAO().getPageWithId(pageId);        
    }
    protected void redirectToThisPage(IRequestCycle cycle) {
        if (getTolPage() == null) {
            setTolPage(getPageFromRequestCycle(cycle));
        }
        Object[] params = new Object[2];
        params[0] = getTolPage().getMappedNode().getName();
        params[1] = getTolPage().getMappedNode().getNodeId().toString();
        cycle.setListenerParameters(params);
        activateExternalPage(params, cycle);
    }    
    /**
     * @return Returns the node.
     */
    public MappedNode getNode() {
    	if (node != null) {
    		return node;
    	} else {
    		return getTolPage().getMappedNode();
    	}
    }

    /**
     * @param node The node to set.
     */
    public void setNode(MappedNode node) {
        this.node = node;
    }

    public NodeDAO getNodeDAO() {
        return (NodeDAO) getRequestCycle().getAttribute(NODE_DAO);
    }
}
