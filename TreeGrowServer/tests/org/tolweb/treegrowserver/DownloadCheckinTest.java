/*
 * Created on Dec 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Document;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.dao.DownloadDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadCheckinTest extends ApplicationContextTestAbstract {
    private DownloadCheckin checkin;
    private DownloadBuilder builder;
    private DownloadDAO downloadDAO;
    private ServerXMLReader serverXMLReader;
    
    public DownloadCheckinTest(String name) {
        super(name);
        builder = (DownloadBuilder) context.getBean("downloadBuilder");
        checkin = (DownloadCheckin) context.getBean("downloadCheckin");
        serverXMLReader = (ServerXMLReader) context.getBean("serverXMLReader");
        downloadDAO = (DownloadDAO) context.getBean("downloadDAO");
    }
    
    public void testFullCheckin() {
        // Build a download and then verify that it has been checked in according
        // to the expected outcomes, which are:
        // 1) Actual download nodes set to not active
        // 2) Barrier download nodes set to barrier (no change)
        // 3) The download itself should be set to closed
        
        // Create Danny as a contributor
        Contributor danny = new Contributor();
        danny.setId(664);
        
        // Do a 1-deep download of cephalopoda
        Document downloadDoc = builder.buildDownload(new Long(19386), 1, false, true, danny, null, false, false);
        int downloadId = serverXMLReader.fetchDownloadId(downloadDoc.getRootElement());
        checkin.doFullDownloadCheckin(new Long(downloadId));
        Download download = downloadDAO.getDownloadWithId(new Long(downloadId));
        // Check the download status of the nodes, two should be barrier, everything else inactive
        Set shouldBeBarrierIds = new HashSet();
        shouldBeBarrierIds.add(new Long(19397));
        shouldBeBarrierIds.add(new Long(19400));
        for (Iterator iter = download.getDownloadedNodes().iterator(); iter.hasNext();) {
            DownloadNode dn = (DownloadNode) iter.next();
            if (shouldBeBarrierIds.contains(dn.getNodeId())) {
                assertEquals(dn.getActive(), DownloadNode.BARRIER_ACTIVE);
            } else {
                assertEquals(dn.getActive(), DownloadNode.NOT_ACTIVE);
            }
        }
        assertEquals(download.getIsActive(), false);
    }
    
    public void testSubtreeCheckIn() {
        // Build a download and checkin a subtree
        // 1) Previously active nodes in the subtree should be not active
        // 2) Previously active nodes not in the subtree should still be active
        // 3) All barrier nodes should still be barrier nodes
        
        // Create danny as a contributor
        Contributor danny = new Contributor();
        danny.setId(664);
        
        // Do a 2-deep download of cephalopoda
        Document downloadDoc = builder.buildDownload(new Long(19386), 2, false, true, danny, null, false, false);
        int downloadId = serverXMLReader.fetchDownloadId(downloadDoc.getRootElement());
        // Checkin the subtree rooted at coleoidea
        checkin.doSubtreeDownloadCheckin(new Long(downloadId), new Long(19400));
        Download download = downloadDAO.getDownloadWithId(new Long(downloadId));
        Set barrierNodesSet = new HashSet();
        Set inactiveNodesSet = new HashSet();
        // coleoidea
        inactiveNodesSet.add(new Long(19400));
        // belemnoidea
        inactiveNodesSet.add(new Long(19402));
        // neocoleoidea
        inactiveNodesSet.add(new Long(19403));
        // decapodiformes
        barrierNodesSet.add(new Long(19404));
        // octopodiformes
        barrierNodesSet.add(new Long(19405));
        for (Iterator iter = download.getDownloadedNodes().iterator(); iter.hasNext();) {
            DownloadNode dn = (DownloadNode) iter.next();
            if (inactiveNodesSet.contains(dn.getNodeId())) {
                assertEquals(dn.getActive(), DownloadNode.NOT_ACTIVE);
            } else if (barrierNodesSet.contains(dn.getNodeId())) {
                assertEquals(dn.getActive(), DownloadNode.BARRIER_ACTIVE);
            } else {
                assertEquals(dn.getActive(), DownloadNode.ACTIVE);
            }
        }
        assertEquals(download.getIsActive(), true);
        // Do a full checkin for cleanup purposes
        checkin.doFullDownloadCheckin(new Long(downloadId));
    }
}
