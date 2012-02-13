/*
 * Created on Jan 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.methods.GetMethod;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;

/**
 * @author dmandel
 *
 * Class whose job is to find an appropriate parent node to reattach
 * objects to when nodes get deleted.
 */
public abstract class AbstractObjectReattacher {
    protected NodeDAO workingNodeDAO;
    protected NodeDAO publicNodeDAO;
    
    /**
     * Returns a 2-item list with the node to be deleted as the first item and
     * the new parent node as the second item
     * @param nodeId The node id of the node to be deleted
     * @param sourceNodeDAO The dao to look for nodes in
     * @param nodeIds A set of nodeIds to not use as potential new parents
     * @return
     */
    protected List getNewParentForDeletedNode(Long nodeId, NodeDAO sourceNodeDAO, Set nodeIds) {
        // Get all images and acc pages attached to this node and attach them to the 
        // node's parent in public
        MappedNode nodeToDelete = sourceNodeDAO.getNodeWithId(nodeId);
        if (nodeToDelete == null) {
            // If it doesn't exist in the destination DAO, we can't possibly find its parent,
            // so just return
            return null;
        }
        MappedNode newParentNode = sourceNodeDAO.getParentNodeForNode(nodeToDelete);
        while (true) {
            if (newParentNode == null) {
                return null;
            }
            if (nodeIds != null && nodeIds.contains(newParentNode.getNodeId())) {
	            newParentNode = sourceNodeDAO.getParentNodeForNode(newParentNode);
            } else {
                // not in the list of ids to not use, so use it
                break;
            }
        }
        List returnList = new ArrayList();
        returnList.add(nodeToDelete);
        returnList.add(newParentNode);
        return returnList;
    }
    
    protected Set deleteNodeAndAssociatedPage(NodeDAO nodeDAO, PageDAO pageDAO, MappedNode node, boolean recreateAncestorsAfterDelete) {
        Set ancestorIds = null;    	
        if (node != null) {
            if (pageDAO != null) {
		        MappedPage page = pageDAO.getPageForNode(node);
		        if (page != null) {
		            pageDAO.deletePage(page);
		        }
            }
            if (recreateAncestorsAfterDelete) {
            	ancestorIds = nodeDAO.getAncestorsForNode(node.getNodeId());
            }
	        nodeDAO.deleteNode(node, recreateAncestorsAfterDelete);        
	        if (ancestorIds != null && ancestorIds.size() > 0) {
	        	// node has been deleted, but keep ancestor info around for the purposes
	        	// of being able to delete full trees in public
	        	nodeDAO.resetAncestorsForNode(node.getNodeId(), ancestorIds);
	        }
        }
        return ancestorIds;
    }
    
    /**
     * @return Returns the publicNodeDAO.
     */
    public NodeDAO getPublicNodeDAO() {
        return publicNodeDAO;
    }
    /**
     * @param publicNodeDAO The publicNodeDAO to set.
     */
    public void setPublicNodeDAO(NodeDAO publicNodeDAO) {
        this.publicNodeDAO = publicNodeDAO;
    }
    /**
     * @return Returns the workingNodeDAO.
     */
    public NodeDAO getWorkingNodeDAO() {
        return workingNodeDAO;
    }
    /**
     * @param workingNodeDAO The workingNodeDAO to set.
     */
    public void setWorkingNodeDAO(NodeDAO workingNodeDAO) {
        this.workingNodeDAO = workingNodeDAO;
    }
}
