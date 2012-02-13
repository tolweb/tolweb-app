/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="Uploads"
 */
public class Upload {
    private Long uploadId;
    private Date uploadDate;
    private Download download;
    private boolean isClosed;
    private String xmlDoc;
    private boolean isUndoable;
    private Set uploadedNodes, uploadedPages;
    
    /**
     * @hibernate.property column="closed"
     * @return
     */
    public boolean getIsClosed() {
        return isClosed;
    }
    public void setIsClosed(boolean closed) {
        this.isClosed = closed;
    }
    /**
     * @hibernate.many-to-one class="org.tolweb.treegrowserver.Download" column="download_id"
     * @return
     */
    public Download getDownload() {
        return download;
    }
    public void setDownload(Download download) {
        this.download = download;
    }
    /**
     * @hibernate.property column="is_undoable"
     * @return
     */
    public boolean getIsUndoable() {
        return isUndoable;
    }
    public void setIsUndoable(boolean isUndoable) {
        this.isUndoable = isUndoable;
    }
    /**
     * @hibernate.property column="date_time"
     * @return
     */
    public Date getUploadDate() {
        return uploadDate;
    }
    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
    /**
     * @hibernate.id generator-class="native" column="upload_id" unsaved-value="null"
     * @return
     */
    public Long getUploadId() {
        return uploadId;
    }
    public void setUploadId(Long uploadId) {
        this.uploadId = uploadId;
    }
    /**
     * @hibernate.property column="xml_doc"
     * @return
     */
    public String getXmlDoc() {
        return xmlDoc;
    }
    public void setXmlDoc(String xmlDoc) {
        this.xmlDoc = xmlDoc;
    }
    
	/**
	 * @hibernate.set table="Upload_Nodes" lazy="false"
	 * @hibernate.collection-composite-element class="org.tolweb.treegrowserver.UploadNode"
	 * @hibernate.collection-key column="upload_id"
	 */
    public Set getUploadedNodes() {
        if (uploadedNodes == null) {
            uploadedNodes = new HashSet();
        }
        return uploadedNodes;
    }    
    
    public void setUploadedNodes(Set value) {
        uploadedNodes = value;
    }
    
    public void addToUploadedNodes(UploadNode value) {
        getUploadedNodes().add(value);
    }

	/**
	 * @hibernate.set table="Upload_Pages" lazy="false"
	 * @hibernate.collection-composite-element class="org.tolweb.treegrowserver.UploadPage"
	 * @hibernate.collection-key column="upload_id"
	 */    
    public Set getUploadedPages() {
        if (uploadedPages == null) {
            uploadedPages = new HashSet();
        }
        return uploadedPages;
    }
    
    public void setUploadedPages(Set value) {
        uploadedPages = value;
    }
    
    public void addToUploadedPages(UploadPage value) {
        getUploadedPages().add(value);
    }
    
    public String toString() {
        return "Upload with id: " + uploadId + "and root node: " + download.getRootNode().getName();
    }
}
