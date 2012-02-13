/*
 * Created on Nov 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.AbstractNodeWrapper;
import org.tolweb.treegrowserver.AbstractTreeGrowActionSimulator;
import org.tolweb.treegrowserver.Upload;
import org.tolweb.treegrowserver.UploadBatch;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadBatchDAOTest extends AbstractTreeGrowActionSimulator {
	private UploadBatchDAO dao;
	private DownloadDAO downloadDAO;
	
	public UploadBatchDAOTest(String name) {
		super(name);
		dao = (UploadBatchDAO) context.getBean("uploadBatchDAO");
		downloadDAO = (DownloadDAO) context.getBean("downloadDAO");
	}
	
	public void testGetBatchesForContributor() throws Exception {
	    List batches = dao.getActiveBatchesForContributor(katja, false);
	    assertEquals(batches.size(), 0);
	    // Now let's run a download with katja and verify the batch shows up in the list
	    runDownloadAndUpload(new Long(1), 1, katja, null, null);
	    Set onlyIdsSet = new HashSet();
	    for (Iterator iter = createdBatch.getUploadedAndDownloadedNodesSet().iterator(); iter.hasNext();) {
            AbstractNodeWrapper element = (AbstractNodeWrapper) iter.next();
            onlyIdsSet.add(element.getNodeId());
        }
	    System.out.println("going to check for active downloads\n\n\n");
	    List activeDownloads = downloadDAO.getActiveDownloadsForNodeIds(onlyIdsSet);
	    assertEquals(activeDownloads.size(), 0);
	    batches = dao.getActiveBatchesForContributor(katja, false);
	    assertEquals(batches.size(), 1);
	}
	
	public void testGetEditableBatchesForContributor() throws Exception {
	    runDownloadAndUpload(new Long(16421), 1, danny, null, null);
	    Long batchId = createdBatch.getBatchId();
	    //batchSubmitter.submitBatchForPublication(batchId, danny);
	    List editableBatches = dao.getBatchesContributorCanEdit(katja);
	    assertEquals(editableBatches.size(), 1);
	}
	
	/*public void testGetUploadBatchWithId() {
		UploadBatch batch = dao.getUploadBatchWithId(new Long(861));
		assertEquals(batch.getEditors().size(), 3);
		assertEquals(batch.getUploads().size(), 8);
		for (Iterator iter = batch.getUploads().iterator(); iter.hasNext();) {
            Upload element = (Upload) iter.next();
            System.out.println("next upload's id is: " + element.getUploadId());
        }
		
		Contributor katja = new Contributor();
		katja.setId(663);
		assertTrue(batch.getIsEditor(katja));
		
		Upload upload = (Upload) batch.getUploads().iterator().next();
		assertEquals(batch.getBatchId(), dao.getUploadBatchForUpload(upload).getBatchId());
		assertEquals(batch.getBatchId(), dao.getUploadBatchForDownload(upload.getDownload()).getBatchId());
		assertEquals(dao.getRootNodeIdForBatch(batch), new Long(14510));
	}
	
	public void testGetActiveUploadBatchForNodes() {
	    // All of these nodes are part of this active batch, so try each one of them
	    int[] nodeIds = new int[] {14510, 14515, 14513, 14512, 14516, 14514, 14517, 14519, 14518};
	    HashSet totalSet = new HashSet();
	    for (int i = 0; i < nodeIds.length; i++) {
            int j = nodeIds[i];
            HashSet idSet = new HashSet();
            idSet.add(new Long(j));
            totalSet.add(new Long(j));
            UploadBatch ub = dao.getActiveUploadBatchForNodes(idSet);
            assertNotNull(ub);
            assertEquals(ub.getBatchId(), new Long(861));
        }
	    UploadBatch ub = dao.getActiveUploadBatchForNodes(totalSet);
	    assertNotNull(ub);
	    assertEquals(ub.getBatchId(), new Long(861));
	}*/
}
