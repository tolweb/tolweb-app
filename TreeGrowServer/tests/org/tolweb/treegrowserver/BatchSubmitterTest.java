/*
 * Created on Dec 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.dao.DownloadDAO;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;
import org.tolweb.treegrowserver.dao.UploadDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchSubmitterTest extends AbstractTreeGrowActionSimulator {
    public BatchSubmitterTest(String name) {
        super(name);
    }
    
    public void testSubmitBranchForPublication() {
    	HashSet pageIds = new HashSet();
    	pageIds.add(46L);
    	PublicationBatch batch = batchSubmitter.submitBranchForPublication(43L, danny, pageIds, SubmittedPage.MINOR_REVISION);
    	SubmittedPage newPage = new SubmittedPage();
    	newPage.setPageId(46L);
    	assertFalse(batch.getSubmittedPages().contains(newPage));
    	newPage.setPageId(49L);
    	assertFalse(batch.getSubmittedPages().contains(newPage));
    	newPage.setPageId(65L);
    	assertFalse(batch.getSubmittedPages().contains(newPage));
    	newPage.setPageId(2940L);
    	assertTrue(batch.getSubmittedPages().contains(newPage));
    }
    
    /*public void testDisappearedNode() throws Exception {
        // This will cause a node to disappear by moving a node from a published node to an
        // unpublished node.
        Document downloadDoc = downloadBuilder.buildDownload(new Long(2383), 2, true, true, danny, null, false, false);
        moveNodeToNewParent(downloadDoc,  "Ancyromonas sigmoides", "Apusomonads");
        setNodeDontPublish(downloadDoc, "Apusomonads");
        String xmlDocString = printOutDocument(downloadDoc);
        Document uploadDoc = uploadBuilder.buildUpload(xmlDocString);
        String uploadBatchId = getUploadBatchIdFromDocument(uploadDoc);
        createdBatch = uploadBatchDAO.getUploadBatchWithId(new Long(uploadBatchId));
        /*List disappearedNodes = batchSubmitter.submitBatchForPublication(createdBatch.getBatchId(), danny);
        assertEquals(disappearedNodes.size(), 1);
        MappedNode disappearedNode = (MappedNode) disappearedNodes.get(0);/
        assertEquals(disappearedNode.getNodeId(), new Long(2453));/
        checkInDownload(downloadDoc);
        
        // Restore the changed values
        MappedNode ancyromonasNode = workingNodeDAO.getNodeWithId(new Long(2453));
        ancyromonasNode.setParentNodeId(new Long(2386));
        workingNodeDAO.saveNode(ancyromonasNode);
        MappedNode apusomonadsNode = workingNodeDAO.getNodeWithId(new Long(2387));
        apusomonadsNode.setDontPublish(false);
        workingNodeDAO.saveNode(apusomonadsNode);
    }
    
    public void testSubmitBatchForPublication() throws Exception {
        // Create a download -- do some fake uploads (with the same xml as the download), 
        // do a checkin, then submit
        Document downloadDoc = downloadBuilder.buildDownload(new Long(16418), 1, true, true, danny, null, false, false);
        String xmlDocString = printOutDocument(downloadDoc);
        System.out.println("xml doc is: " + xmlDocString);
        Document uploadDoc = uploadBuilder.buildUpload(xmlDocString);
        uploadDoc = uploadBuilder.buildUpload(xmlDocString);
        String uploadBatchId = getUploadBatchIdFromDocument(uploadDoc);
        // Check in the download
        checkInDownload(downloadDoc);
        Long batchId = new Long(uploadBatchId);
        //batchSubmitter.submitBatchForPublication(batchId, danny);
        // Go through and make sure each of the nodes in the download is submitted.
        // Also make sure that the batch is submitted.
        UploadBatch batch = uploadBatchDAO.getUploadBatchWithId(batchId);
        checkNodesAndBatchForSubmission(batch, true);
        // fetch it again and make sure that the editors were set correctly
        batch = uploadBatchDAO.getUploadBatchWithId(batchId);
        assertEquals(batch.getEditors().size(), 3);
        // Then close it so we can run the tests again without error
        //batchSubmitter.unsubmitBatchForPublication(batch.getBatchId(), danny);
        // fetch it again and make sure that the submission status on everything got set correctly
        batch = uploadBatchDAO.getUploadBatchWithId(batchId);        
        checkNodesAndBatchForSubmission(batch, false);
        createdBatch = batch;
    }
    
    private void checkNodesAndBatchForSubmission(UploadBatch batch, boolean shouldBeSubmitted) {
        assertEquals(batch.getIsSubmitted(), shouldBeSubmitted);
        Set allUploadedNodesSet = batch.getUploadedNodesSet();
        for (Iterator iter = allUploadedNodesSet.iterator(); iter.hasNext();) {
            UploadNode un = (UploadNode) iter.next();
            MappedNode node = workingNodeDAO.getNodeWithId(un.getNodeId());
            assertEquals(node.getIsSubmitted(), shouldBeSubmitted);
        }        
    }*/
}
