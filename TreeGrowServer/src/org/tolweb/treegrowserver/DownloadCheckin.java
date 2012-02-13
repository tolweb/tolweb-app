/*
 * Created on Dec 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrowserver.dao.DownloadDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadCheckin extends AbstractObjectReattacher {
    private DownloadDAO downloadDAO;
    private PageDAO workingPageDAO;
    private ImageDAO imageDAO;
    private AccessoryPageDAO workingAccessoryPageDAO;
    private AccessoryPageDAO publicAccessoryPageDAO;
    private NodeDAO miscNodeDAO;
    
    public void doFullDownloadCheckin(Long downloadId) {
        Download download = downloadDAO.getDownloadWithId(downloadId);
        for (Iterator iter = download.getDownloadedNodes().iterator(); iter.hasNext();) {
            DownloadNode node = (DownloadNode) iter.next();
            if (node.getActive() == DownloadNode.ACTIVE) {
                node.setActive(DownloadNode.NOT_ACTIVE);
            }
        }
        download.setIsActive(false);
        downloadDAO.saveDownload(download);
    }
    
    public void doSubtreeDownloadCheckin(Long downloadId, Long subtreeRootId) {
        Download download = downloadDAO.getDownloadWithId(downloadId);
        // Check if the subtree root is in the download
        if (download.getIsNodePartOfDownload(subtreeRootId, true)) {
            // Starting with the subtree, mark descendants of the root
            // that were active parts of the download as not active
            for (Iterator iter = download.getDownloadedNodes().iterator(); iter.hasNext();) {
                DownloadNode dn = (DownloadNode) iter.next();
                if (dn.getNodeId().equals(subtreeRootId)) {
                    dn.setActive(DownloadNode.BARRIER_ACTIVE);
                } else if (miscNodeDAO.getNodeIsAncestor(dn.getNodeId(), subtreeRootId) && dn.getActive() == DownloadNode.ACTIVE
                        || dn.getActive() == DownloadNode.BARRIER_ACTIVE) {
                    dn.setActive(DownloadNode.NOT_ACTIVE);
                }
            }
            downloadDAO.saveDownload(download);
        }
    }

    /**
     * @return Returns the downloadDAO.
     */
    public DownloadDAO getDownloadDAO() {
        return downloadDAO;
    }
    /**
     * @param downloadDAO The downloadDAO to set.
     */
    public void setDownloadDAO(DownloadDAO downloadDAO) {
        this.downloadDAO = downloadDAO;
    }
    /**
     * @return Returns the imageDAO.
     */
    public ImageDAO getImageDAO() {
        return imageDAO;
    }
    /**
     * @param imageDAO The imageDAO to set.
     */
    public void setImageDAO(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }
    /**
     * @return Returns the publicAccessoryPageDAO.
     */
    /*public AccessoryPageDAO getPublicAccessoryPageDAO() {
        return publicAccessoryPageDAO;
    }
    /**
     * @param publicAccessoryPageDAO The publicAccessoryPageDAO to set.
     /
    public void setPublicAccessoryPageDAO(
            AccessoryPageDAO publicAccessoryPageDAO) {
        this.publicAccessoryPageDAO = publicAccessoryPageDAO;
    }*/
    /**
     * @return Returns the workingAccessoryPageDAO.
     */
    public AccessoryPageDAO getWorkingAccessoryPageDAO() {
        return workingAccessoryPageDAO;
    }
    /**
     * @param workingAccessoryPageDAO The workingAccessoryPageDAO to set.
     */
    public void setWorkingAccessoryPageDAO(
            AccessoryPageDAO workingAccessoryPageDAO) {
        this.workingAccessoryPageDAO = workingAccessoryPageDAO;
    }
    /**
     * @return Returns the workingPageDAO.
     */
    public PageDAO getWorkingPageDAO() {
        return workingPageDAO;
    }
    /**
     * @param workingPageDAO The workingPageDAO to set.
     */
    public void setWorkingPageDAO(PageDAO workingPageDAO) {
        this.workingPageDAO = workingPageDAO;
    }
    /**
     * @return Returns the miscNodeDAO.
     */
    public NodeDAO getMiscNodeDAO() {
        return miscNodeDAO;
    }
    /**
     * @param miscNodeDAO The miscNodeDAO to set.
     */
    public void setMiscNodeDAO(NodeDAO miscNodeDAO) {
        this.miscNodeDAO = miscNodeDAO;
    }
}
