/*
 * Created on Nov 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.tolweb.dao.BaseDAOImpl;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.Download;
import org.tolweb.treegrowserver.Upload;
import org.tolweb.treegrowserver.UploadBatch;
import org.tolweb.treegrowserver.UploadNode;
import org.tolweb.treegrowserver.UploadPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadBatchDAOImpl extends BaseDAOImpl implements UploadBatchDAO {
    private NodeDAO nodeDAO;
    private NodeDAO workingNodeDAO;
    private PermissionChecker permissionChecker;

	/* (non-Javadoc)
	 * @see org.tolweb.treegrowserver.dao.UploadBatchDAO#getUploadBatchWithId(java.lang.Long)
	 */
	public UploadBatch getUploadBatchWithId(Long id) {
		return (UploadBatch) getHibernateTemplate().load(UploadBatch.class, id);
	}
	
	public UploadBatch getUploadBatchForUpload(Upload upload) {
	    return getUploadBatchBasedOnUploadProperty("uploadId", upload.getUploadId());
	}
    
    public Long getUploadBatchIdForUpload(Upload upload) {
        String queryString = "select distinct UB.batchId from org.tolweb.treegrowserver.UploadBatch UB " + 
        "join UB.uploads as upload join upload.uploadedNodes as nodes " + 
        "where UB.isClosed=0 and upload.uploadId=" + upload.getUploadId();
        return (Long) getFirstObjectFromQuery(queryString);
    }
    
    public boolean getContributorIsEditorForBatch(Long batchId, Contributor contr) {
        if (batchId != null && contr != null) {
            String queryString = "select UB.batchId from org.tolweb.treegrowserver.UploadBatch UB " + 
            " join UB.editors as editors where UB.batchId=" + batchId + " and editors.id=" + contr.getId();
            Object firstResult = getFirstObjectFromQuery(queryString);
            return firstResult != null;
        } else {
            return false;
        }
    }
	
	public UploadBatch getUploadBatchForDownload(Download download) {
	    return getUploadBatchBasedOnUploadProperty("download.downloadId", download.getDownloadId());
	}
	
	public void saveBatch(UploadBatch batch) {
	    getHibernateTemplate().saveOrUpdate(batch);
	}
	
	private UploadBatch getUploadBatchBasedOnUploadProperty(String propertyName, Long value) {
		List results = getHibernateTemplate().find("select UB from org.tolweb.treegrowserver.UploadBatch UB " + 
				"join UB.uploads as upload where upload." + propertyName + "=" + value);
		if (results != null && results.size() > 0) {
			return (UploadBatch) results.get(0);
		} else {
			return null;
		}		    
	}

    /* (non-Javadoc)
     * @see org.tolweb.treegrowserver.dao.UploadBatchDAO#getRootNodeForBatch(org.tolweb.treegrowserver.UploadBatch)
     */
    public Long getRootNodeIdForBatch(UploadBatch batch) {
        Set nodesIdsSet = new HashSet();
        for (Iterator iter = batch.getUploads().iterator(); iter.hasNext();) {
            Upload nextUpload = (Upload) iter.next();
            Iterator it = nextUpload.getUploadedNodes().iterator();
            while (it.hasNext()) {
                UploadNode node = (UploadNode) it.next();
                nodesIdsSet.add(node.getNodeId());
            }
        }
        Long rootNodeId = null;
        if (nodesIdsSet.size() > 0) {
            Session session = null;
            try {
			    session = getSession();
				Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				String sqlString = "SELECT node_id, count(ancestor_id) C FROM NODEANCESTORS  where node_id in (" + StringUtils.returnCommaJoinedString(nodesIdsSet) + ") group by node_id  order by C";
				ResultSet results = selectStatement.executeQuery(sqlString);
				while (results.next()) {
				    // The root node is the one with the least ancestors, so grab the first node id and use it
				    rootNodeId = new Long(results.getInt(1));
				    // Verify that it exists in working
				    if (getWorkingNodeDAO().getNodeExistsWithId(rootNodeId)) {
				        break;
				    } else {
				        System.out.println("missing node id that should be root is: " + rootNodeId + " and missing batch id is: " + batch.getBatchId());
				    }
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootNodeId;
    }

    /* (non-Javadoc)
     * @see org.tolweb.treegrowserver.dao.UploadBatchDAO#getActiveUploadBatchForNodes(java.util.Set)
     */
    public UploadBatch getActiveUploadBatchForNodes(Set nodeIds) {
        if (nodeIds.size() > 0) {
            String queryString = "select distinct UB from org.tolweb.treegrowserver.UploadBatch UB " + 
    		"join UB.uploads as upload join upload.uploadedNodes as nodes " + 
    		"where UB.isClosed=0 and nodes.nodeId in (" + StringUtils.returnCommaJoinedString(nodeIds) + ")";
            List list = getHibernateTemplate().find(queryString);
            if (list.size() > 0) {
                return (UploadBatch) list.get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * Checks to see if there is an active TreeGrow upload batch
     * for the associated page.  If there is, return that batch
     * else return null
     * @param page
     * @return
     */
    public UploadBatch getActiveUploadBatchForPage(MappedPage page) {
        List pages = getActiveUploadedPagesForPage(page);
        if (pages != null && pages.size() > 0) {
            List batches = getUploadBatchesFromIdsAndPages(pages);
            return (UploadBatch) batches.get(0);
        } else {
            return null;
        }
    }
    
    public List getActiveUploadedPagesForPage(MappedPage page) {
        String queryString = "select distinct UB.batchId from org.tolweb.treegrowserver.UploadBatch UB " + 
        "join UB.uploads as upload join upload.uploadedNodes as nodes " + 
        "where UB.isClosed=0 and nodes.nodeId=" + page.getMappedNode().getNodeId();
        return getOnlyIdsAndPages(queryString);
    }

    /* (non-Javadoc)
     * @see org.tolweb.treegrowserver.dao.UploadBatchDAO#getEditorsForBatch(org.tolweb.treegrowserver.UploadBatch)
     */
    public List getEditorsForBatch(UploadBatch batch) {
        Long rootNodeId = getRootNodeIdForBatch(batch);
        Set ancestors = getNodeDAO().getAncestorsForNode(rootNodeId);
        return getPermissionChecker().getScientificEditorsForNodes(ancestors);
    }
    
    public List getActiveBatchesForContributor(Contributor contributor, boolean onlyIdAndPages) {
        Hashtable batches = new Hashtable(); 
        if (onlyIdAndPages) {
            return doAlternateUploadBatchFetch(contributor);
        } else {
            
        }
        String selectString = !onlyIdAndPages ? "select distinct UB " : "select distinct UB.batchId, pages.pageId";
        String queryString = selectString + " from org.tolweb.treegrowserver.UploadBatch UB " + 
        "join UB.uploads as upload join upload.uploadedPages as pages join upload.download as download where UB.isClosed=0 and " + 
        "download.contributor.id=" + contributor.getId() + " and upload.isClosed=0";
        List contributorsBatches = getHibernateTemplate().find(queryString);
        return contributorsBatches;
    }
    
    private List doAlternateUploadBatchFetch(Contributor contributor) {
        String queryString = "select distinct UB.batchId from org.tolweb.treegrowserver.UploadBatch UB " + 
        "join UB.uploads as upload join upload.download as download where UB.isClosed=0 and " + 
        "download.contributor.id=" + contributor.getId() + " and upload.isClosed=0";
        return getOnlyIdsAndPages(queryString);
    }
    
    private List getOnlyIdsAndPages(String queryString) {
        List batchIds = getHibernateTemplate().find(queryString);
        if (batchIds.size() > 0) {
            String pageIds = "select distinct UB.batchId, pages.pageId from org.tolweb.treegrowserver.UploadBatch UB join UB.uploads as upload " +
                "join upload.uploadedPages as pages where UB.batchId in (" + StringUtils.returnCommaJoinedString(batchIds) + ")";
            List pageIdsList = getHibernateTemplate().find(pageIds);
            return pageIdsList;
        } else {
            return new ArrayList();
        }
    }
    
    /**
     * Constructs a list of upload batches based on an object array returned
     * from a an UploadPages query
     * @param idsAndPageIds
     * @return
     */
    private List getUploadBatchesFromIdsAndPages(List idsAndPageIds) {
        Long lastBatchId = null;
        UploadBatch currentBatch = null;
        Set allPageIds = new HashSet();
        Set currentUploadPages = null;
        List batches = new ArrayList();
        for (Iterator iter = idsAndPageIds.iterator(); iter.hasNext();) {
            Object[] nextPairs = (Object[]) iter.next();
            Long currentBatchId = (Long) nextPairs[0];
            Long nextPageId = (Long) nextPairs[1];
            allPageIds.add(nextPageId);
            if (lastBatchId == null || !currentBatchId.equals(lastBatchId)) {
                if (currentBatch != null) {
                    currentBatch.setUploadedPagesSet(currentUploadPages);
                    // store it in the list
                    batches.add(currentBatch);
                }
                currentBatch = new UploadBatch();
                currentBatch.setBatchId(currentBatchId);
                currentUploadPages = new HashSet();
            }
            UploadPage up = new UploadPage();
            up.setPageId(nextPageId);
            currentUploadPages.add(up);
            lastBatchId = currentBatchId;
        }
        // need to do this once we've broken out of the loop as well -- fencepost problem
        if (currentBatch != null) {
            currentBatch.setUploadedPagesSet(currentUploadPages);
            batches.add(currentBatch);
        }  
        return batches;
    }
    
    public List getBatchesContributorCanEdit(Contributor contributor) {
        if (contributor.getEditingRootNodeId() != null) {
            String queryString = "select distinct UB from org.tolweb.treegrowserver.UploadBatch UB " + 
            "join UB.editors as editors where UB.isClosed=0 and editors.id=" + contributor.getId() + " and UB.isSubmitted=1";
            List editableBatches = getHibernateTemplate().find(queryString);
            return editableBatches;
        } else {
            return new ArrayList();
        }
    }
    
    public Set getAllBatchesForContributor(Contributor contributor) {
        Hashtable allBatches = new Hashtable();
        List activeBatches = getActiveBatchesForContributor(contributor, false);
        addBatchesToHash(activeBatches, allBatches);
        List editorBatches = getBatchesContributorCanEdit(contributor);
        addBatchesToHash(editorBatches, allBatches);
        Enumeration enume = allBatches.elements();
        Set returnedBatches = new HashSet();
        while (enume.hasMoreElements()) {
            UploadBatch batch = (UploadBatch) enume.nextElement();
            returnedBatches.add(batch);
        }
        return returnedBatches;
    }
    
    private void addBatchesToHash(List batches, Hashtable allBatches) {
        for (Iterator iter = batches.iterator(); iter.hasNext();) {
            UploadBatch batch = (UploadBatch) iter.next();
            allBatches.put(batch.getBatchId(), batch);
        }
    }
    
    /**
     * @return Returns the nodeDAO.
     */
    public NodeDAO getNodeDAO() {
        return nodeDAO;
    }
    /**
     * @param nodeDAO The nodeDAO to set.
     */
    public void setNodeDAO(NodeDAO nodeDAO) {
        this.nodeDAO = nodeDAO;
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

    /* (non-Javadoc)
     * @see org.tolweb.treegrowserver.dao.UploadBatchDAO#getActiveUploadBatches()
     */
    public List getActiveUploadBatches() {
        return getHibernateTemplate().find("from org.tolweb.treegrowserver.UploadBatch where closed=0");
    }
}
