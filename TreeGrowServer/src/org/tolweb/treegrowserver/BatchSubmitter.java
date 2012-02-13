/*
 * Created on Dec 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.dao.PublicationBatchDAO;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;

/**
 * @author dmandel
 *
 * Class that submits upload batches to an editor for publication
 */
public class BatchSubmitter {
    private UploadBatchDAO uploadBatchDAO;
    private NodeDAO workingNodeDAO;
    private NodeDAO publicNodeDAO;
    private PageDAO pageDAO;
    private PublicationBatchDAO publicationBatchDAO;

    public PublicationBatch submitPageForPublication(MappedPage page, Contributor contributor, int revisionType) {
    	Set publishedPages = new HashSet();
        publishedPages.add(submitPage(page.getPageId(), revisionType));
        Long uploadBatchId = null;

        return createPublicationBatchForContributor(publishedPages, contributor, uploadBatchId);
    }
    /**
     * Submit an entire branch for publication
     * @param rootPageId The root page id of the branch to publish
     * @param contributor The contributor doing the publishing
     * @param branchIdsNotToPublish A set of root page ids that should *not* be published
     * @param revisionType The revision type of the publication
     * @return The new publication batch
     */
    public PublicationBatch submitBranchForPublication(Long rootPageId, Contributor contributor, Set branchIdsNotToPublish, int revisionType) {
    	List<Long> descendantPageIds = getPageDAO().getDescendantPageIds(rootPageId);
    	for (Long pageId : new ArrayList<Long>(descendantPageIds)) {
			if (branchIdsNotToPublish.contains(pageId)) {
				descendantPageIds.removeAll(getPageDAO().getDescendantPageIds(pageId));
			}
		}
    	Set<SubmittedPage> publishedPages = new HashSet<SubmittedPage>();
    	for (Long pageId : descendantPageIds) {
			publishedPages.add(submitPage(pageId, revisionType));
		}
    	return createPublicationBatchForContributor(publishedPages, contributor, null);
    }
    
    public PublicationBatch submitSelectedPagesForPublication(UploadBatch batch, Contributor contributor) {
        int revisionType = batch.getRevisionType();
    	Set publishedPages = new HashSet();
        for (Iterator iter = batch.getSortedUploadedPages().iterator(); iter.hasNext();) {
            UploadPage nextPage = (UploadPage) iter.next();
            if (nextPage.getShouldBePublished()) {
                SubmittedPage page = submitPage(nextPage.getPageId(), revisionType);
                publishedPages.add(page);
            }
        }
        return createPublicationBatchForContributor(publishedPages, contributor, batch.getBatchId());
    }
    
    private PublicationBatch createPublicationBatchForContributor(Set submittedPages, Contributor contributor, 
            Long uploadBatchId) {
        PublicationBatch publicationBatch = new PublicationBatch();
        publicationBatch.setSubmittedContributor(contributor);
        publicationBatch.setSubmissionDate(new Date());
        publicationBatch.setSubmittedPages(submittedPages);
        publicationBatch.setUploadBatchId(uploadBatchId);
        getPublicationBatchDAO().saveBatch(publicationBatch);
        return publicationBatch;
    }
    
    private SubmittedPage submitPage(Long pageId, int revisionType) {
        SubmittedPage page = new SubmittedPage();
        page.setPageId(pageId);
        page.setRevisionType(revisionType);
        MappedNode node = getWorkingNodeDAO().getNodeWithId(getPageDAO().getNodeIdForPage(pageId));
        if (node != null) {
	        node.setIsSubmitted(true);
	        getWorkingNodeDAO().saveNode(node);        
        } else {
        	System.err.println("page id attached to non-existent node is: " + pageId);
        }
        return page;
    }
    
    public void unsubmitPublicationBatch(PublicationBatch batch, Contributor editingContributor) {
        unsubmitNodesForPublicationBatch(batch);
        batch.setDecisionDate(new Date());
        batch.setEditingContributor(editingContributor);
        batch.setIsClosed(true);
        batch.setWasPublished(false);
        getPublicationBatchDAO().saveBatch(batch);
    }
    
    public void unsubmitNodesForPublicationBatch(PublicationBatch batch) {
        for (Iterator iter = batch.getSubmittedPages().iterator(); iter.hasNext();) {
            SubmittedPage page = (SubmittedPage) iter.next();
            MappedNode node = getWorkingNodeDAO().getNodeWithId(getPageDAO().getNodeIdForPage(page.getPageId()));
            if (node != null) {
	            node.setIsSubmitted(false);
	            getWorkingNodeDAO().saveNode(node);
            }
        }        
    }
    
    
    /**
     * Submits a batch
     * @param batchId The batch to submit or unsubmit
     * @param contributor The contributor doing it
     * @param isSubmit Whether this is a submit or unsubmit
     * @return A list of nodes that have 'disappeared', or null if an error occurred
     /
    private List submitOrUnsubmitBatchForPublication(Long batchId, Contributor contributor, boolean isSubmit) {
        List disappearedNodes = new ArrayList();
        UploadBatch batch = uploadBatchDAO.getUploadBatchWithId(batchId);
        if (batch != null) {
            batch.setIsSubmitted(isSubmit);
            for (Iterator iter = batch.getUploadedNodesSet().iterator(); iter.hasNext();) {
                UploadNode un = (UploadNode) iter.next();
                MappedNode node = (MappedNode) workingNodeDAO.getNodeWithId(un.getNodeId());
                // It's possible a node was uploaded then deleted, so check if it's null
                if (node != null) {
                    node.setIsSubmitted(isSubmit);
                    workingNodeDAO.saveNode(node);
                }
            }
            if (isSubmit) {
                Set nodesToCheck = batch.getUploadedAndDownloadedNodesSet();
                Long rootNodeId = uploadBatchDAO.getRootNodeIdForBatch(batch);
                for (Iterator iter = nodesToCheck.iterator(); iter.hasNext();) {
                    AbstractNodeWrapper node = (AbstractNodeWrapper) iter.next();
                    MappedNode actualNode = getNodeHasDisappeared(node, rootNodeId); 
                    // Check a merging of downloaded and uploaded nodes
                    if (actualNode != null) {
                        disappearedNodes.add(actualNode);
                    }                     
                }
                batch.setContributor(contributor);
                // Get the editors
                List editors = getUploadBatchDAO().getEditorsForBatch(batch);
                batch.setEditors(new HashSet(editors));
            } else {
                batch.setContributor(null);
                batch.setEditors(new HashSet());
            }
            uploadBatchDAO.saveBatch(batch);
            return disappearedNodes;
        }
        return null;
    }*/
    
    /**
     * Checks whether a node has 'disappeared' according to misuse of the do not publish rule
     * @param node The node wrapper containing the ID to check
     * @param rootNodeId The root node id of the batch (so we know when to stop checking)
     * @return The node, if it has disappeared, null otherwise.
     */
    private MappedNode getNodeHasDisappeared(AbstractNodeWrapper node, Long rootNodeId) {
        // If it's the root its parent can't have changed, so don't bother checking
        if (node.getNodeId().equals(rootNodeId)) {
            return null;
        }
        // If it doesn't exist in public, it can't have disappeared, so don't bother checking
        MappedNode publicNode = publicNodeDAO.getNodeWithId(node.getNodeId());
        MappedNode workingNode = workingNodeDAO.getNodeWithId(node.getNodeId());        
        if (publicNode == null || workingNode == null) {
            return null;
        }
        // If the parent hasn't changed, it can't disappear
        if (workingNode.getParentNodeId().equals(publicNode.getParentNodeId())) {
            return null;
        }
        // Otherwise, walk back to root in working and see if the new parent
        // is a node marked not to publish
        MappedNode currentParent = workingNodeDAO.getParentNodeForNode(workingNode);
        while (currentParent != null && !currentParent.getNodeId().equals(rootNodeId)) {
            if (currentParent.getDontPublish()) {
                // Check to see if the parent is an ancestor of the parent node in public.
                // If it is, there's no problem because this node won't get pushed live anyway
                if (!workingNodeDAO.getNodeIsAncestor(publicNode.getParentNodeId(), currentParent.getNodeId())) {
                    return workingNode;
                }
            }
            currentParent = workingNodeDAO.getParentNodeForNode(currentParent);
        }
        return null;
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
    public void setWorkingNodeDAO(NodeDAO nodeDAO) {
        this.workingNodeDAO = nodeDAO;
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
}
