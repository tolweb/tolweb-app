/*
 * Created on Jan 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.treegrow.main.XMLConstants;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchResultsBuilderTest extends AbstractTreeGrowActionSimulator {
    private BatchResultsBuilder resultsBuilder;
    
    public BatchResultsBuilderTest(String name) {
        super(name);
        resultsBuilder = (BatchResultsBuilder) context.getBean("batchResultsBuilder");
    }
    
    public void testBuildUploadBatchResultsDocument() throws Exception {
        runDownloadAndUpload(new Long(16421), 1, katja, null, null);
        // Then submit the batch so we can make sure the editors show up correctly
        //batchSubmitter.submitBatchForPublication(createdBatch.getBatchId(), katja);
        Document resultsDocument = resultsBuilder.buildUploadBatchResultsDocument(katja);
        assertNotNull(resultsDocument);
        Element uploadBatchElement = resultsDocument.getRootElement().getChild(XMLConstants.UPLOAD_BATCH);
        assertNotNull(uploadBatchElement);
        assertEquals(uploadBatchElement.getAttributeValue(XMLConstants.BATCHID), createdBatch.getBatchId().toString());
        assertEquals(uploadBatchElement.getAttributeValue(XMLConstants.SUBMITTED), XMLConstants.ONE);
        Element pagesElement = uploadBatchElement.getChild(XMLConstants.PAGES);
        assertNotNull(pagesElement);
        assertEquals(pagesElement.getChildren(XMLConstants.PAGE).size(), 1);
        assertEquals(uploadBatchElement.getAttributeValue(XMLConstants.ROOT_GROUP_ID), "16421");
        assertEquals(uploadBatchElement.getChildText(XMLConstants.ROOT_GROUP), "Homo sapiens");
        assertEquals(uploadBatchElement.getAttributeValue(XMLConstants.CAN_PUSH_PUBLIC), XMLConstants.ONE);
        assertEquals(uploadBatchElement.getAttributeValue(XMLConstants.IS_SOLE_AUTHOR), XMLConstants.ONE);
        Element downloadsElement = uploadBatchElement.getChild(XMLConstants.DOWNLOADS);
        assertNull(downloadsElement);
    }
}
