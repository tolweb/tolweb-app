/*
 * Created on Nov 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.dao.DownloadDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NodeSearchResultsBuilder {
    private NodeDAO nodeDAO;
    private PermissionChecker permissionChecker;
    private ContributorDAO contributorDAO;
    private DownloadDAO downloadDAO;
    private PageDAO pageDAO;
    
    public Document buildSearchResultsDocument(String groupName, String userId, String password, boolean exactMatch) {
        Contributor contr = getContributorDAO().getContributorWithEmail(userId);
        Document validationDoc = getPermissionChecker().checkPermission(contr, password);
        if (validationDoc != null) {
            // No contributor, or invalid password
            return validationDoc;
        }
        Integer numNodes;
        if (exactMatch) {
            numNodes = getNodeDAO().getNumNodesExactlyNamed(groupName);
        } else {
            numNodes = getNodeDAO().getNumNodesNamed(groupName);
        }
        if (numNodes.intValue() == 0) {
            return getNothingFoundDocument();
        } else if (numNodes.intValue() > 50) {
            return getTooManyFoundDocument();
        } else {
            List nodes;
            if (exactMatch) {
                nodes = getNodeDAO().findNodesExactlyNamed(groupName);
            } else {
                nodes = getNodeDAO().findNodesNamed(groupName);
            }
            return constructResultsDocument(nodes, contr, password);
        }	
    }
    
    public Document getNodeUpdates(List nodeIds, Contributor contr, String password) {
        List nodes = new ArrayList();
        for (Iterator iter = nodeIds.iterator(); iter.hasNext();) {
            Long nodeId = (Long) iter.next();
            MappedNode nextNode = getNodeDAO().getNodeWithId(nodeId);
            nodes.add(nextNode);
        }
        return constructResultsDocument(nodes, contr, password);
    }
    
    private Document constructResultsDocument(List nodes, Contributor contr, String password) {
        Element matchesElement = new Element(XMLConstants.MATCHES);        
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            MappedNode node = (MappedNode) it.next();
            boolean hasPage = getPageDAO().getNodeHasPage(node);
            Element matchElement = new Element(XMLConstants.MATCH);
            matchesElement.addContent(matchElement);
            matchElement.setAttribute(XMLConstants.NAME, node.getName());
            matchElement.setAttribute(XMLConstants.ID, node.getNodeId().toString());
            
            Element lockedElement = getDownloadDAO().getNodeIsLocked(node, contr);
            if (lockedElement != null) {
                matchElement.addContent(lockedElement);
            } else {
                matchElement.setAttribute(XMLConstants.HASPAGE, hasPage ? XMLConstants.ONE : XMLConstants.ZERO);
                // find out the name and id of the containing page
                if (!hasPage) {
                    MappedNode parentNode = getPageDAO().getNodeForPageNodeIsOn(node);
                    matchElement.setAttribute(XMLConstants.PARENTPAGE_NODE_ID, parentNode.getNodeId().toString());
                    matchElement.setAttribute(XMLConstants.PARENTPAGE_NAME, parentNode.getName());
                }
                Document permissionDoc = getPermissionChecker().checkPermissionForNode(contr, password, node.getNodeId());
                matchElement.setAttribute(XMLConstants.PERMISSIONS, permissionDoc == null ? XMLConstants.ONE : XMLConstants.ZERO);
            }
        }
        return new Document(matchesElement);        
    }
    
    public Document getNothingFoundDocument() {
        return constructErrorDocument(404, "No matches");
    }
    
    public Document getTooManyFoundDocument() {
        return constructErrorDocument(200200, "Too many matches");
    }
    
    private Document constructErrorDocument(int errorNum, String errorText) {
        Element errorElement = new Element(XMLConstants.ERROR);
        Element errorNumElement = new Element(XMLConstants.ERRORNUM);
        errorElement.addContent(errorNumElement);
        errorNumElement.setText("" + errorNum);
        Element errorTextElement = new Element(XMLConstants.ERRORTEXT);
        errorElement.addContent(errorTextElement);
        errorTextElement.setText(errorText);
        return new Document(errorElement);
    }

    public ContributorDAO getContributorDAO() {
        return contributorDAO;
    }
    public void setContributorDAO(ContributorDAO contributorDao) {
        this.contributorDAO = contributorDao;
    }
    public NodeDAO getNodeDAO() {
        return nodeDAO;
    }
    public void setNodeDAO(NodeDAO nodeDao) {
        this.nodeDAO = nodeDao;
    }
    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }
    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
    public DownloadDAO getDownloadDAO() {
        return downloadDAO;
    }
    public void setDownloadDAO(DownloadDAO downloadDAO) {
        this.downloadDAO = downloadDAO;
    }
    public PageDAO getPageDAO() {
        return pageDAO;
    }
    public void setPageDAO(PageDAO pageDAO) {
        this.pageDAO = pageDAO;
    }    
}
