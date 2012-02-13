/*
 * Created on Nov 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.Download;
import org.tolweb.treegrowserver.DownloadNode;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadDAOTest extends ApplicationContextTestAbstract {
    private DownloadDAO dao;
    private ContributorDAO contributorDao;
    private NodeDAO nodeDao;
    
    public DownloadDAOTest(String name) {
        super(name);
        dao = (DownloadDAO) context.getBean("downloadDAO");
        nodeDao = (NodeDAO) context.getBean("nodeDAO");
        contributorDao = (ContributorDAO) context.getBean("contributorDAO");
    }
    
    public void testLockedNodes() {
        MappedNode node = (MappedNode) nodeDao.findNodesExactlyNamed("Life").get(0);
        Element lockedElement = dao.getNodeIsLocked(node);
        assertNull(lockedElement);
        
        MappedNode gastropoda = (MappedNode) nodeDao.findNodesExactlyNamed("Gastropoda").get(0);
        lockedElement = dao.getNodeIsLocked(gastropoda);
        //assertNotNull(lockedElement);
    }
    
    public void testCreateNewDownload() {
    	Contributor danny = contributorDao.getContributorWithId("664");
    	Download download = new Download();
    	Date now = new Date();
    	String ip = "127.0.0.1";
    	download.setContributor(danny);
    	download.setDownloadDate(now);
    	download.setIpAddress(ip);
    	download.setIsActive(true);
    	Set downloadNodesSet = new HashSet();
    	download.setDownloadedNodes(downloadNodesSet);
    	constructDownloadNode("Life", downloadNodesSet);
    	constructDownloadNode("Eubacteria", downloadNodesSet);
    	constructDownloadNode("Eukaryotes", downloadNodesSet);
    	constructDownloadNode("Archaea", downloadNodesSet);
    	constructDownloadNode("Viruses", downloadNodesSet);
    	MappedNode life = (MappedNode) nodeDao.findNodesExactlyNamed("Life").get(0);
    	download.setRootNode(life);
    	dao.createNewDownload(download);
    	
    	Long downloadId = download.getDownloadId();
    	Download otherDownload = dao.getDownloadWithId(downloadId);
    	assertEquals(download.getDownloadId(), otherDownload.getDownloadId());
    	assertEquals(download.getContributor().getId(), otherDownload.getContributor().getId());
    	assertEquals(download.getIpAddress(), otherDownload.getIpAddress());
    	assertEquals(download.getIsActive(), otherDownload.getIsActive());
    	assertEquals(download.getDownloadedNodes().size(), otherDownload.getDownloadedNodes().size());
    	assertEquals(download.getRootNode().getNodeId(), otherDownload.getRootNode().getNodeId());
    	
    	checkLockStatus("Life", true);
    	checkLockStatus("Eubacteria", true);
    	checkLockStatus("Eukaryotes", true);
    	checkLockStatus("Archaea", true);
    	checkLockStatus("Viruses", true);    	
    	
    	download.checkIn();
    	dao.saveDownload(download);
    	
    	checkLockStatus("Life", false);
    	checkLockStatus("Eubacteria", false);
    	checkLockStatus("Eukaryotes", false);
    	checkLockStatus("Archaea", false);
    	checkLockStatus("Viruses", false);    	
    	
    	// Now delete the download so as to not pollute the database
    	dao.deleteDownload(download);    	
    }
    
    public void testBatchIdForSubmittedNode() {
    	MappedNode carnivora = (MappedNode) nodeDao.findNodesExactlyNamed("Carnivora").get(0);
    	Contributor katja = new Contributor();
    	katja.setId(663);
    	Element lockedElement = dao.getNodeIsLocked(carnivora, katja);
    	assertNotNull(lockedElement);
    	assertNotNull(lockedElement.getAttributeValue(XMLConstants.BATCHID));
    }
    
    private void checkLockStatus(String nodeName, boolean shouldBeLocked) {
    	MappedNode node = (MappedNode) nodeDao.findNodesExactlyNamed(nodeName).get(0);
    	Element lockedElement = dao.getNodeIsLocked(node);
    	if (shouldBeLocked) {
	    	assertNotNull(lockedElement);
	    	assertEquals(lockedElement.getAttributeValue(XMLConstants.TYPE), XMLConstants.DOWNLOADED);
	    	assertEquals(lockedElement.getAttributeValue(XMLConstants.USER), "dmandel@tolweb.org");    	
    	} else {
    		assertNull(lockedElement);
    	}
    }
    
    private void constructDownloadNode(String nodeName, Set nodesSet, int activeStatus) {
    	MappedNode node = (MappedNode) nodeDao.findNodesExactlyNamed(nodeName).get(0);
    	DownloadNode dn = new DownloadNode();
    	dn.setActive(activeStatus);
    	dn.setNodeId(node.getNodeId());
    	nodesSet.add(dn);    	
    }
    
    private void constructDownloadNode(String nodeName, Set nodesSet) {
    	constructDownloadNode(nodeName, nodesSet, DownloadNode.ACTIVE);
    }
}
