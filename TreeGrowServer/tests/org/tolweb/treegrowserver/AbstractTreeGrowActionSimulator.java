/*
 * Created on Dec 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrowserver.dao.DownloadDAO;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;
import org.tolweb.treegrowserver.dao.UploadDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractTreeGrowActionSimulator extends ApplicationContextTestAbstract {
    protected ServerXMLReader serverXMLReader;
    protected ServerXMLWriter serverXMLWriter;
    protected DownloadCheckin downloadCheckin;
    protected DownloadBuilder downloadBuilder;
    protected UploadBuilder uploadBuilder;
    protected BatchSubmitter batchSubmitter;
    protected UploadBatchDAO uploadBatchDAO;    
    protected UploadBatch createdBatch;
    protected NodeDAO workingNodeDAO;    
    protected Contributor danny, katja;
    
    public AbstractTreeGrowActionSimulator(String name) {
        super(name);
        serverXMLReader = (ServerXMLReader) context.getBean("serverXMLReader");
        serverXMLWriter = (ServerXMLWriter) context.getBean("serverXMLWriter");
        downloadCheckin = (DownloadCheckin) context.getBean("downloadCheckin");
        downloadBuilder = (DownloadBuilder) context.getBean("downloadBuilder");
        uploadBuilder = (UploadBuilder) context.getBean("uploadBuilder");
        batchSubmitter = (BatchSubmitter) context.getBean("batchSubmitter");
        uploadBatchDAO = (UploadBatchDAO) context.getBean("uploadBatchDAO");
        workingNodeDAO = (NodeDAO) context.getBean("workingNodeDAO");
        ContributorDAO contributorDAO = (ContributorDAO) context.getBean("contributorDAO");
        danny = contributorDAO.getContributorWithId("664");
        assertNotNull(danny);
        katja = contributorDAO.getContributorWithId("663");
        assertNotNull(katja);
    }    
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        if (createdBatch == null) {
            return;
        }
        // Delete the download associated with the batch
        DownloadDAO downloadDAO = (DownloadDAO) context.getBean("downloadDAO");
        UploadDAO uploadDAO = (UploadDAO) context.getBean("uploadDAO");
        Set downloadsSet = new HashSet();
        for (Iterator iter = createdBatch.getUploads().iterator(); iter.hasNext();) {
            Upload upload = (Upload) iter.next();
            downloadsSet.add(upload.getDownload());
            upload.setIsClosed(true);
            uploadDAO.saveUpload(upload);
        }
        for (Iterator iter = downloadsSet.iterator(); iter.hasNext();) {
            Download download = (Download) iter.next();
            downloadDAO.deleteDownload(download);
        }
        createdBatch.setIsClosed(true);
        uploadBatchDAO.saveBatch(createdBatch);
    }    
    
    protected void checkInDownload(Document doc) {
        int downloadId = serverXMLReader.fetchDownloadId(doc.getRootElement());
        downloadCheckin.doFullDownloadCheckin(new Long(downloadId));
    }
    
    protected String getUploadBatchIdFromDocument(Document doc) {
        return doc.getRootElement().getAttributeValue(XMLConstants.BATCHID);        
    }
    
    protected void moveNodeToNewParent(Document doc, String nodeName, String newParentName) {
        try {                       
            Element nodeToMoveElement = findNodeNamed(doc, nodeName);
            assertNotNull(nodeToMoveElement);
            // Remove the element from its parent
            nodeToMoveElement.detach();
            // Find the new parent
            Element newParentElement = findNodeNamed(doc, newParentName);
            assertNotNull(newParentElement);
            // figure out how many nodes are there
            int numNodesUnderNewParent = getNumChildrenForNodeElement(newParentElement);
            // Set the order on parent on the moved node
            nodeToMoveElement.setAttribute(XMLConstants.ORDER, "" + numNodesUnderNewParent);
            // Now paste the new element under its parent
            addNewChildToParent(newParentElement, nodeToMoveElement);
            assertEquals(nodeToMoveElement.getParent().getParent(), newParentElement);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }        
    }
    
    protected void deleteNode(Document doc, String nodeName) {
        try {
            Element nodeToDeleteElement = findNodeNamed(doc, nodeName);
            assertNotNull(nodeToDeleteElement);
            // Remove it from its parent -- same as deleting
            nodeToDeleteElement.detach();
        }  catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    protected void addNewNodeToNode(Document doc, String newNodeName, String parentName) {
        try {
            Element parentElement = findNodeNamed(doc, parentName);
            assertNotNull(parentElement);
            int numChildren = getNumChildrenForNodeElement(parentElement);
            MappedNode node = new MappedNode();
            node.setOrderOnParent(new Integer(numChildren));
            node.setName(newNodeName);
            node.setNodeId(new Long(-1));
            node.setSynonyms(new TreeSet());
            Element newNodeElement = serverXMLWriter.constructNodeElement();
            serverXMLWriter.fleshOutNode(node, newNodeElement);
            addNewChildToParent(parentElement, newNodeElement);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);            
        }
    }
    
    protected void addNewChildToParent(Element parentElement, Element childElement) {
        Element nodesElement = parentElement.getChild(XMLConstants.NODES);
        if (nodesElement == null) {
            nodesElement = new Element(XMLConstants.NODES);
            parentElement.addContent(nodesElement);
        }
        nodesElement.addContent(childElement);        
    }
    
    /**
     * @param parentElement
     * @return
     */
    protected int getNumChildrenForNodeElement(Element parentElement) {
        Element nodesElement = parentElement.getChild(XMLConstants.NODES);
        if (nodesElement != null) {
            return nodesElement.getChildren(XMLConstants.NODE).size();
        } else {
            return 0;
        }
    }

    protected void setNodeDontPublish(Document doc, String nodeName) {
        setNodeAttributeAndValueForNodeNamed(doc, nodeName, XMLConstants.DONTPUBLISH, XMLConstants.ONE);
    }
    
    protected void setNodeExtinct(Document doc, String nodeName) {
        setNodeAttributeAndValueForNodeNamed(doc, nodeName, XMLConstants.EXTINCT, "" + Node.EXTINCT);
    }
    
    protected void setNodePhylesis(Document doc, String nodeName, int phylesisValue) {
        setNodeAttributeAndValueForNodeNamed(doc, nodeName, XMLConstants.PHYLESIS, "" + phylesisValue);
    }
    
    
    
    /**
     * @param nodeElement
     * @param dontpublish
     * @param one
     */
    private void setNodeAttributeAndValueForNodeNamed(Document doc, String nodeName, String attributeName, String attributeValue) {
        Element nodeElement = findNodeNamed(doc, nodeName);
        nodeElement.setAttribute(attributeName, attributeValue);
    }   
    
    protected Element findNodeNamed(Document doc, String nodeName) {
        try {
	        XPath nodeLocatorPath = XPath.newInstance("//NODE[normalize-space(string(NAME))='" + nodeName +  "' and @HASPAGE>=0]");
	        Element nodeElement = (Element) nodeLocatorPath.selectSingleNode(doc.getRootElement());
	        return nodeElement;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    protected void runDownloadAndUpload(Long rootNodeId, int depth, Contributor contributor, 
            DocumentManipulator manipulator, AdditionalProcessor processor) throws Exception {
        Document downloadDoc = downloadBuilder.buildDownload(rootNodeId, depth, true, true,
                contributor, null, false, false);
        if (manipulator != null) {
            manipulator.manipulateDocument(downloadDoc);
        }
        String xmlDocString = printOutDocument(downloadDoc);
        Document uploadDoc = uploadBuilder.buildUpload(xmlDocString);
        String uploadBatchId = getUploadBatchIdFromDocument(uploadDoc);
        Long batchId = new Long(uploadBatchId);
        if (processor != null) {
	        // Before submitting, allow for some other processing to occur
	        processor.doAdditionalProcessing();
        }
        checkInDownload(downloadDoc);
        createdBatch = uploadBatchDAO.getUploadBatchWithId(batchId);        
    }
}

interface DocumentManipulator {
    public void manipulateDocument(Document doc);
}

interface AdditionalProcessor {
    public void doAdditionalProcessing();
}
