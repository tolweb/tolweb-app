package org.tolweb.treegrowserver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PagePusher;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.page.PageContributor;

/*
 * Created on Dec 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchPusherTest extends AbstractTreeGrowActionSimulator {
    private BatchPusher batchPusher;
    private NodeDAO publicNodeDAO, miscNodeDAO;
    private ImageDAO imageDAO;
    private PageDAO publicPageDAO, workingPageDAO, approvalPageDAO;
    private PagePusher pagePusher;
    
    public BatchPusherTest(String name) {
        super(name);
        batchPusher = (BatchPusher) context.getBean("batchPusher");
        publicNodeDAO = (NodeDAO) context.getBean("publicNodeDAO");
        imageDAO = (ImageDAO) context.getBean("imageDAO");
        miscNodeDAO = (NodeDAO) context.getBean("nodeDAO");
        publicPageDAO = (PageDAO) context.getBean("publicPageDAO");
        workingPageDAO = (PageDAO) context.getBean("workingPageDAO");
        approvalPageDAO = (PageDAO) context.getBean("approvalPageDAO");
        pagePusher = (PagePusher) context.getBean("pagePusher");
    }
    
    public void testDeletingPage() throws Exception {
        DocumentManipulator manipulator = new DocumentManipulator() {
            public void manipulateDocument(Document doc) {
                // Delete the basidiomycota page and then verify that its children
                // pages show up on the fungi page and that the other node (Uredinomycetes)
                // is also showing up on the fungi page
                Element basidiomycotaElement = findNodeNamed(doc, "Basidiomycota");
                basidiomycotaElement.setAttribute(XMLConstants.HASPAGE, XMLConstants.ZERO);
                basidiomycotaElement.removeChild(XMLConstants.PAGE);
                Element basidiomycotaPage = basidiomycotaElement.getChild(XMLConstants.PAGE);
                if (basidiomycotaPage != null) {
                    basidiomycotaPage.detach();
                }
            }
        };
        Long fungiNodeId = new Long(2377);
        Long basidiomycotaNodeId = new Long(20520);
        runDownloadAndUpload(fungiNodeId, 2, danny, manipulator, null);
        MappedPage workingFungiPage = workingPageDAO.getPageForNode(workingNodeDAO.getNodeWithId(fungiNodeId));
        MappedPage publicFungiPage = publicPageDAO.getPageForNode(publicNodeDAO.getNodeWithId(fungiNodeId)); 
        // Make sure that there is no page for basidiomycota in working or public
        MappedNode workingNode = workingNodeDAO.getNodeWithId(basidiomycotaNodeId);
        MappedPage workingPage = workingPageDAO.getPageForNode(workingNode);
        assertNull(workingPage);
        MappedNode publicNode = publicNodeDAO.getNodeWithId(basidiomycotaNodeId);
        MappedPage publicPage = publicPageDAO.getPageForNode(publicNode);
        assertNull(publicPage);
        // Then check the pages for Ustilaginomycetes and Hymenomycetes and make sure that
        // they think their parent page is now Fungi
        Long hymenNodeId = new Long(20531);
        MappedNode workingHymenomycetes = workingNodeDAO.getNodeWithId(hymenNodeId);
        assertEquals(workingHymenomycetes.getPageId(), workingFungiPage.getPageId());
        MappedPage workingHymenomycetesPage = workingPageDAO.getPageForNode(workingHymenomycetes);
        assertEquals(workingHymenomycetesPage.getParentPageId(), workingFungiPage.getPageId());        
        MappedNode hymenomycetes = publicNodeDAO.getNodeWithId(hymenNodeId);
        assertEquals(hymenomycetes.getPageId(), publicFungiPage.getPageId());
        MappedPage hymenomycetesPage = publicPageDAO.getPageForNode(hymenomycetes);
        assertEquals(hymenomycetesPage.getParentPageId(), publicFungiPage.getPageId());
        Set workingHymenomycetesAncestors = workingPageDAO.getAncestorPageIds(workingHymenomycetesPage.getPageId());
        assertEquals(workingHymenomycetesAncestors.size(), 4);
        assertTrue(workingHymenomycetesAncestors.contains(new Long(1)));
        assertTrue(workingHymenomycetesAncestors.contains(new Long(276)));
        assertTrue(workingHymenomycetesAncestors.contains(new Long(2633)));
        assertTrue(workingHymenomycetesAncestors.contains(new Long(2636)));        
    }
    
    public void testAddingNewPage() throws Exception {
        addOpisthokontsPage();
        MappedPage publicOpisthokontsPage = publicPageDAO.getPageForNode(publicNodeDAO.getNodeWithId(new Long(2372)));
        MappedPage workingOpisthokontsPage = workingPageDAO.getPageForNode(workingNodeDAO.getNodeWithId(new Long(2372)));              
        
        Long eukaryotesPageId = new Long(276);        

        // Check, then reset the parent page ids on all these pages and nodes
        checkAndSavePage(eukaryotesPageId, new Long(2374), publicOpisthokontsPage, workingOpisthokontsPage);
        checkAndSavePage(eukaryotesPageId, new Long(2377), publicOpisthokontsPage, workingOpisthokontsPage);
        checkAndSaveNode(eukaryotesPageId, new Long(2378), publicOpisthokontsPage, workingOpisthokontsPage);
        checkAndSaveNode(eukaryotesPageId, new Long(2375), publicOpisthokontsPage, workingOpisthokontsPage);        
        // Delete the pages that are no longer needed
        publicPageDAO.deletePage(publicOpisthokontsPage);
        workingPageDAO.deletePage(workingOpisthokontsPage);      
    }
    
    private void addOpisthokontsPage() throws Exception {
        DocumentManipulator manipulator = new DocumentManipulator() {
            public void manipulateDocument(Document doc) {
                Element opisthokontsElement = findNodeNamed(doc, "opisthokonts");
                opisthokontsElement.setAttribute(XMLConstants.HASPAGE, XMLConstants.ONE);
                MappedPage page = new MappedPage();
                page.setTitleIllustrations(new TreeSet());
                page.setTitle("opisthokonts");
                PageContributor contributor = new PageContributor();
                contributor.setContributor(danny);
                contributor.setIsAuthor(true);
                SortedSet contributors = new TreeSet();
                contributors.add(contributor);
                page.setContributors(contributors);
                MappedTextSection section = new MappedTextSection();
                section.setHeading("Introduction");
                section.setText("this is some intro text for the opisthokonts");
                SortedSet textSections = new TreeSet();
                textSections.add(section);
                page.setTextSections(textSections);
                Element pageElement = serverXMLWriter.constructPageElement(page, new HashSet(), new HashSet(), null, null, false);
                opisthokontsElement.addContent(pageElement);
            }
        };
        runDownloadAndUpload(new Long(3), 1, danny, manipulator, null);        
    }
    
    private void checkAndSavePage(Long newParentPageId, Long nodeId, MappedPage publicOpisthokontsPage,
            MappedPage workingOpisthokontsPage) {
        MappedPage publicPage = publicPageDAO.getPageForNode(publicNodeDAO.getNodeWithId(nodeId));
        assertEquals(publicPage.getParentPageId(), publicOpisthokontsPage.getPageId());
        MappedPage workingPage = workingPageDAO.getPageForNode(workingNodeDAO.getNodeWithId(nodeId));
        assertEquals(workingPage.getParentPageId(), workingOpisthokontsPage.getPageId());        
        publicPage.setParentPageId(newParentPageId);
        publicPageDAO.savePage(publicPage);
        workingPage.setParentPageId(newParentPageId);
        workingPageDAO.savePage(workingPage);
        checkAndSaveNode(newParentPageId, nodeId, publicOpisthokontsPage, workingOpisthokontsPage);
    }
    
    private void checkAndSaveNode(Long newParentPageId, Long nodeId, MappedPage publicOpisthokontsPage,
            MappedPage workingOpisthokontsPage) {
        MappedNode workingNode = workingNodeDAO.getNodeWithId(nodeId);
        assertEquals(workingNode.getPageId(), workingOpisthokontsPage.getPageId());
        MappedNode publicNode = publicNodeDAO.getNodeWithId(nodeId);        
        assertEquals(publicNode.getPageId(), publicOpisthokontsPage.getPageId());
        publicNode.setPageId(newParentPageId);
        publicNodeDAO.saveNode(publicNode);
        workingNode.setPageId(newParentPageId);
        workingNodeDAO.saveNode(workingNode);
    }
    
    private void verifyParentPage(PageDAO pageDAO, NodeDAO nodeDAO, Long nodeId, Long expectedParentPageId) {
        MappedPage fetchedPage = pageDAO.getPageForNode(nodeDAO.getNodeWithId(nodeId));
        assertEquals(fetchedPage.getParentPageId(), expectedParentPageId);
    }
    
    public void testPushingNewNode() throws Exception {
        final String newName = "New Eukaryotic Clade";
        DocumentManipulator manipulator = new DocumentManipulator() {
            public void manipulateDocument(Document doc) {        
                addNewNodeToNode(doc, newName, "Eukaryotes");
            }
        };
        runDownloadAndUpload(new Long(3), 1, danny, manipulator, null);
        MappedNode newPublicNode = (MappedNode) publicNodeDAO.findNodesExactlyNamed(newName).get(0);
        assertNotNull(newPublicNode);
        MappedNode eukaryotes = (MappedNode) publicNodeDAO.getNodeWithId(new Long(3));        
        assertEquals(publicNodeDAO.getNumChildrenForNode(eukaryotes), new Integer(7));
        MappedNode newWorkingNode = (MappedNode) workingNodeDAO.findNodesExactlyNamed(newName).get(0);
        assertEquals(newPublicNode.getNodeId(), newWorkingNode.getNodeId());
        workingNodeDAO.deleteNode(newWorkingNode, false);
        publicNodeDAO.deleteNode(newPublicNode, false);
    }
    
    public void testPushingChangedParentNode() throws Exception {
        // In this test, we'll move Alveolates to be a child of Opisthokonts.
        // We'll verify that it's parent gets changed, but that no values get 
        // changed (since it's a border case and not actually checked out)
        DocumentManipulator manipulator = new DocumentManipulator() {
            public void manipulateDocument(Document doc) {
                moveNodeToNewParent(doc, "Alveolates", "opisthokonts");
            }
        };        
        AdditionalProcessor processor = new AdditionalProcessor() {
            public void doAdditionalProcessing() {
                // Change some notes on the Alveolates 
                // (to simulate another upload since someone else could have had them checked out)
                MappedNode workingAlveolatesNode = (MappedNode) workingNodeDAO.findNodesExactlyNamed("Alveolates").get(0);
                workingAlveolatesNode.setDescription("this is some description");
                workingNodeDAO.saveNode(workingAlveolatesNode);                
            }
        };
        runDownloadAndUpload(new Long(3), 1, danny, manipulator, processor);
        String originalAlveolatesDescription = "(dinoflagellates, ciliates, apicomplexa)";

        // Ok, so we should be able to verify that the parent of the public Alveolates is
        // Opisthokonts, however the description should remain the same
        MappedNode publicAlveolatesNode = (MappedNode) publicNodeDAO.findNodesExactlyNamed("Alveolates").get(0);
        assertEquals(publicAlveolatesNode.getParentNodeId(), new Long(2372));
        assertEquals(publicAlveolatesNode.getDescription(), originalAlveolatesDescription);
        
        // Set things back to normal
        MappedNode workingAlveolatesNode = (MappedNode) workingNodeDAO.findNodesExactlyNamed("Alveolates").get(0);
        workingAlveolatesNode.setParentNodeId(new Long(3));
        workingAlveolatesNode.setDescription(originalAlveolatesDescription);
        workingNodeDAO.saveNode(workingAlveolatesNode);
        publicAlveolatesNode.setParentNodeId(new Long(3));
        publicNodeDAO.saveNode(publicAlveolatesNode);
    }
    
    public void testDeletingNode() throws Exception {
        // Create a new node in working and public, then delete it.
        // Verify that it is no longer in the database
        final String newNodeName = "new eukaryotes child";
        MappedNode newEukaryotesChild = new MappedNode();
        newEukaryotesChild.setParentNodeId(new Long(3));
        newEukaryotesChild.setName(newNodeName);
        newEukaryotesChild.setPageId(new Long(276));
        newEukaryotesChild.setOrderOnParent(new Integer(0));
        workingNodeDAO.saveNode(newEukaryotesChild);
        ((BatchPusherImpl) batchPusher).getNodePusher().pushNodeToDB(newEukaryotesChild, publicNodeDAO);
        ((BatchPusherImpl) batchPusher).getNodePusher().pushNodeToDB(newEukaryotesChild, miscNodeDAO);
        
        // Create an image an attach it to the new node.  Once the node is deleted it should move
        // up to eukaryotes
        NodeImage newNodeImage = new NodeImage();
        newNodeImage.setUsePermission(NodeImage.EVERYWHERE_USE);
        newNodeImage.setLocation("xxx.gif");
        newNodeImage.addToNodesSet(newEukaryotesChild);
        imageDAO.addImage(newNodeImage, danny, false);
        int imageId = newNodeImage.getId();

        
        
        // Verify that eukaryotes now has the additional child in both dbs
        MappedNode workingEukaryotes = workingNodeDAO.getNodeWithId(new Long(3));
        assertEquals(workingNodeDAO.getNumChildrenForNode(workingEukaryotes), new Integer(7));
        MappedNode publicEukaryotes = publicNodeDAO.getNodeWithId(new Long(3));
        assertEquals(publicNodeDAO.getNumChildrenForNode(publicEukaryotes), new Integer(7));
        DocumentManipulator manipulator = new DocumentManipulator() {
            public void manipulateDocument(Document doc) {
                deleteNode(doc, newNodeName);
            }
        }; 
        runDownloadAndUpload(new Long(3), 1, danny, manipulator, null);
        // Verify that the node doesn't exist in working or public
        List nodes = workingNodeDAO.findNodesExactlyNamed(newNodeName);
        assertEquals(nodes.size(), 0);
        nodes = publicNodeDAO.findNodesExactlyNamed(newNodeName);
        assertEquals(nodes.size(), 0);
        // Also verify that eukaryotes is back down to its correct children size
        assertEquals(workingNodeDAO.getNumChildrenForNode(workingEukaryotes), new Integer(6));
        assertEquals(publicNodeDAO.getNumChildrenForNode(publicEukaryotes), new Integer(6));
        
        NodeImage sameImage = imageDAO.getImageWithId(imageId);
        assertEquals(sameImage.getNodesSet().size(), 1);
        MappedNode node = (MappedNode) sameImage.getNodesSet().iterator().next();
        assertEquals(node.getNodeId(), new Long(3));
        imageDAO.deleteImage(sameImage);
    }
    
    protected void runDownloadAndUpload(Long rootNodeId, int depth, Contributor contributor, 
            DocumentManipulator manipulator, AdditionalProcessor processor) throws Exception {
        super.runDownloadAndUpload(rootNodeId, depth, contributor, manipulator, processor);
        Long batchId = createdBatch.getBatchId();
        //batchSubmitter.submitBatchForPublication(batchId, danny);
        //batchPusher.pushBatchToPublic(batchId);        
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        Long pageId = new Long(2635);
        MappedPage basidiomycotaApprovalPage = approvalPageDAO.getPageWithId(pageId);
        assertNotNull(basidiomycotaApprovalPage);
        MappedPage basidiomycotaWorkingPage = workingPageDAO.getPageWithId(pageId);
        /*if (basidiomycotaWorkingPage == null) {
            pagePusher.pushPageToDB(basidiomycotaApprovalPage, workingPageDAO);
        }
        MappedPage basidiomycotaPublicPage = publicPageDAO.getPageWithId(pageId);
        if (basidiomycotaPublicPage == null) {
            pagePusher.pushPageToDB(basidiomycotaApprovalPage, publicPageDAO);
        }*/
    }
}