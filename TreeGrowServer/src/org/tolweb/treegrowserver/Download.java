package org.tolweb.treegrowserver;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;

/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="Downloads"
 */
public class Download {
    private Long downloadId;
    private Date downloadDate;
    private Contributor contributor;
    private MappedNode rootNode;
    private boolean isActive;
    private String ipAddress;
    private Set downloadedNodes;
    
    /**
     * @hibernate.many-to-one class="org.tolweb.treegrow.main.Contributor" column="contributor_id"
     * @return
     */
    public Contributor getContributor() {
        return contributor;
    }
    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }
    /**
     * @hibernate.property column="date_time"
     * @return
     */
    public Date getDownloadDate() {
        return downloadDate;
    }
    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }
    /**
     * @hibernate.id generator-class="native" column="download_id" unsaved-value="null"
     * @return
     */
    public Long getDownloadId() {
        return downloadId;
    }
    public void setDownloadId(Long downloadId) {
        this.downloadId = downloadId;
    }
    /**
     * @hibernate.property column="ip"
     * @return
     */
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    /**
     * @hibernate.property column="active"
     * @return
     */
    public boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    /**
     * @hibernate.many-to-one class="org.tolweb.hibernate.MappedNode" column="rootnode_id"
     * @return
     */
    public MappedNode getRootNode() {
        return rootNode;
    }
    public void setRootNode(MappedNode rootNode) {
        this.rootNode = rootNode;
    }
    
	/**
	 * @hibernate.set table="Download_Nodes" lazy="false"
	 * @hibernate.collection-composite-element class="org.tolweb.treegrowserver.DownloadNode"
	 * @hibernate.collection-key column="download_id"
	 */
    public Set getDownloadedNodes() {
        return downloadedNodes;
    }
    
    public void setDownloadedNodes(Set value) {
        downloadedNodes = value;
    }
    
    public void addToDownloadedNodes(DownloadNode value) {
    	downloadedNodes.add(value);
    }
    
    public void removeFromDownloadedNodes(DownloadNode value) {
    	downloadedNodes.remove(value);
    }
    
    public DownloadNode getDownloadNodeWithNodeId(Long nodeId) {
        for (Iterator iter = downloadedNodes.iterator(); iter.hasNext();) {
            DownloadNode dn = (DownloadNode) iter.next();
            if (dn.getNodeId() != null && dn.getNodeId().equals(nodeId)) {
                return dn;
            }
        }
        return null;
    }
    
    public boolean getIsNodePartOfDownload(Long nodeId, boolean onlyActive) {
    	Iterator it = downloadedNodes.iterator();
    	while (it.hasNext()) {
    		DownloadNode dn = (DownloadNode) it.next();
    		Long currentNodeId = dn.getNodeId();
    		if (currentNodeId.equals(nodeId)) {
    			if (onlyActive) {
    				if (dn.getActive() == DownloadNode.ACTIVE) {
    					return true;
    				}
    			} else {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public void checkIn() {
    	setIsActive(false);
    	Iterator it = downloadedNodes.iterator();
    	while (it.hasNext()) {
    		DownloadNode node = (DownloadNode) it.next();
    		node.setActive(DownloadNode.NOT_ACTIVE);
    	}
    }
}
