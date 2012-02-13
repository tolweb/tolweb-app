/*
 * Created on Nov 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.tolweb.dao.BaseDAOImpl;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.Download;
import org.tolweb.treegrowserver.DownloadNode;
import org.tolweb.treegrowserver.PublicationBatch;
import org.tolweb.treegrowserver.ServerXMLWriter;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadDAOImpl extends BaseDAOImpl implements DownloadDAO {
    private NodeDAO nodeDao;
    private UploadDAO uploadDAO;
    private PageDAO pageDAO;
    private UploadBatchDAO uploadBatchDAO;
    private PublicationBatchDAO publicationBatchDAO;
    private PermissionChecker permissionChecker;
    private ServerXMLWriter serverXMLWriter;
    
    public boolean getNodeIsDownloaded(Long nodeId) {
        return getOpenDownloadForNode(nodeId) != null;
    }
       
    public Download getOpenDownloadForNode(Long nodeId) {
        String queryString = "select D from org.tolweb.treegrowserver.Download D join D.downloadedNodes node where D.isActive=1 and node.nodeId=" +
        nodeId + " and node.active=1";        
        return (Download) getFirstObjectFromQuery(queryString);        
    }
    
    public List getNodesAreDownloaded(List nodeIds) {
    	String queryString = "select distinct node.nodeId from org.tolweb.treegrowserver.Download D join D.downloadedNodes" +
    		" node where D.isActive=1 and node.nodeId " + StringUtils.returnSqlCollectionString(nodeIds) + " and node.active=1";
    	return getHibernateTemplate().find(queryString);
    }
    
    public Element getNodeIsLocked(MappedNode nd) {
    	return getNodeIsLocked(nd, null);
    }
    
    public Element getNodeIsLocked(Long nodeId) {
        return getNodeIsLocked(nodeId, null);
    }
    
    public Element getNodeIsLocked(MappedNode nd, Contributor contr) {
        return getNodeIsLocked(nd.getNodeId(), contr);
    }
    
    public boolean getDownloadIsActive(Long id) {
        try {
	        Boolean isActive = (Boolean) getHibernateTemplate().find("select D.isActive from org.tolweb.treegrowserver.Download D where D.downloadId=" + id).get(0);
	        return isActive.booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Element getNodeIsLocked(Long nodeId, Contributor contr) {
		try {
			if (getNodeDAO().getNodeIsSubmitted(nodeId)) {
			    // there was no download associated with a submitted batch, so
                // the node must have been submitted using the online tools.
                // look for the submission date and contributor who submitted the page
                PublicationBatchDAO dao = getPublicationBatchDAO();
                PublicationBatch batch = dao.getBatchForSubmittedNode(nodeId);
                // check to see if the contributor has permission over this node
                boolean hasPermissions = (contr != null) &&
                    getPermissionChecker().checkHasEditingPermissionForNode(contr, nodeId);
                Long batchId = null;
                if (hasPermissions) {
                    batchId = new Long(1);
                }
                String nodeName = getNodeDAO().getNameForNodeWithId(nodeId);
                return getServerXMLWriter().createElementForLockedNode(true, batch.getSubmittedContributor().getEmail(), batch.getSubmissionDate(),
                        nodeName, null, batchId);
            }
            // check to see if it's currently downloaded
            Download download = getOpenDownloadForNode(nodeId);
            if (download != null) {
                return getServerXMLWriter().createDownloadedElement(download);
            }    			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		// check to see if the user has permission for the node
		boolean hasPermission = true;
		if (contr != null) {
			hasPermission = getPermissionChecker().checkHasPermissionForNode(contr, nodeId);
		}
		if (!hasPermission) {
			return getServerXMLWriter().createNoPermissionsElement();
		} else {
			return null;
		}
    }
    
    public String getMessageForLockedElement(Element lockedElement) {
        String lockType = lockedElement.getAttributeValue(XMLConstants.TYPE).equals(XMLConstants.SUBMITTED) ? "submitted" : 
            "downloaded";
        return "This node has been " + lockType + " by " + lockedElement.getAttributeValue(XMLConstants.USER) + 
            " on " + lockedElement.getAttributeValue(XMLConstants.DATE_TIME);
    }
    
    public void createNewDownload(Download download) {
    	getHibernateTemplate().save(download);
    }
    
    public void saveDownload(Download download) {
    	getHibernateTemplate().update(download);
    }
    
    public void deleteDownload(Download download) {
    	getHibernateTemplate().delete(download);
    }
    
    public Download getDownloadWithId(Long id) {
    	return (Download) getHibernateTemplate().load(Download.class, id);
    }
    
    public void setNodeDAO(NodeDAO value) {
        nodeDao = value;
    }
    
    public NodeDAO getNodeDAO() {
        return nodeDao;
    }
    
    /**
     * @return Returns the pageDAO.
     */
    public PageDAO getPageDAO() {
        return pageDAO;
    }

    /**
     * @param pageDAO The pageDAO to set.
     */
    public void setPageDAO(PageDAO pageDAO) {
        this.pageDAO = pageDAO;
    }


	/**
	 * @return Returns the uploadDAO.
	 */
	public UploadDAO getUploadDAO() {
		return uploadDAO;
	}
	/**
	 * @param uploadDAO The uploadDAO to set.
	 */
	public void setUploadDAO(UploadDAO uploadDAO) {
		this.uploadDAO = uploadDAO;
	}
	/**
	 * @return Returns the uploadBatchDAO.
	 */
	public UploadBatchDAO getUploadBatchDAO() {
		return uploadBatchDAO;
	}
	/**
	 * @param uploadBatchDAO The uploadBatchDAO to set.
	 */
	public void setUploadBatchDAO(UploadBatchDAO uploadBatchDAO) {
		this.uploadBatchDAO = uploadBatchDAO;
	}

    /* (non-Javadoc)
     * @see org.tolweb.treegrowserver.dao.DownloadDAO#getActiveDownloadForNodeIds(java.util.Collection, org.tolweb.treegrow.main.Contributor)
     */
    public List getActiveDownloadsForNodeIds(Collection nodeIds) {
        if (nodeIds != null && nodeIds.size() > 0) { 
	        String queryString = "select D.downloadId, D.contributor.id from org.tolweb.treegrowserver.Download D join D.downloadedNodes node where D.isActive=1 and node.nodeId in (" +
	        	StringUtils.returnCommaJoinedString(nodeIds) + ") and node.active=1";
	        List ids = getHibernateTemplate().find(queryString);
	        return ids;
        } else {
            return new ArrayList();
        }
    }
    
    public List getActiveDownloadsForUser(Contributor contr) {
        return getHibernateTemplate().find("from org.tolweb.treegrowserver.Download D where D.isActive=1 and D.contributor.id=" + contr.getId());
    }
    
    public void reassignDownloadRootNodesForDeletedNodes(Collection deletedNodeIds, Long newRootNodeId) {
        try {
            Statement updateStatement = getSession().connection().createStatement(); 
            String sql = "update Downloads set rootnode_id=" + newRootNodeId + " where rootnode_id in (" + StringUtils.returnCommaJoinedString(deletedNodeIds) + ")";
            updateStatement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*List downloadsWithOldRoots = getHibernateTemplate().find("from org.tolweb.treegrowserver.Download D where D.rootNode.nodeId in (" + StringUtils.returnCommaJoinedString(deletedNodeIds) + ")");
        for (Iterator iter = downloadsWithOldRoots.iterator(); iter.hasNext();) {
            Download download = (Download) iter.next();
            download.setRootNode(getNodeDAO().getNodeWithId(newRootNodeId));
            saveDownload(download);
        }*/
    }
    
    public List getNodeIdsWithPages(Long downloadId) {
        Download download = getDownloadWithId(downloadId);
        ArrayList nodesWithPages = new ArrayList();
        for (Iterator iter = download.getDownloadedNodes().iterator(); iter.hasNext();) {
            DownloadNode dn = (DownloadNode) iter.next();
            if (!dn.getWasDeleted() && dn.getActive() == DownloadNode.ACTIVE) {
                if (getPageDAO().getNodeHasPage(dn.getNodeId())) {
                    nodesWithPages.add(dn.getNodeId());
                }
            }
        }
        return nodesWithPages;
    }
    public Collection getDeletedNodeIds(Collection nodeIds) {
    	String queryString = "select distinct node.nodeId from org.tolweb.treegrowserver.Download D join D.downloadedNodes" +
			" node where node.nodeId " + StringUtils.returnSqlCollectionString(nodeIds) + " and node.wasDeleted=true";
    	return getHibernateTemplate().find(queryString);
    }

    /**
     * @return Returns the publicationBatchDAO.
     */
    public PublicationBatchDAO getPublicationBatchDAO() {
        return publicationBatchDAO;
    }

    /**
     * @param publicationBatchDAO The publicationBatchDAO to set.
     */
    public void setPublicationBatchDAO(PublicationBatchDAO publicationBatchDAO) {
        this.publicationBatchDAO = publicationBatchDAO;
    }

    /**
     * @return Returns the permissionChecker.
     */
    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    /**
     * @param permissionChecker The permissionChecker to set.
     */
    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

	public ServerXMLWriter getServerXMLWriter() {
		return serverXMLWriter;
	}

	public void setServerXMLWriter(ServerXMLWriter serverXMLWriter) {
		this.serverXMLWriter = serverXMLWriter;
	}
}
