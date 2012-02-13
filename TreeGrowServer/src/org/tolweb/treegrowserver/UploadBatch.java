/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="Upload_Batches"
 */
public class UploadBatch {
    private Long batchId;
    private boolean isSubmitted;
    private boolean isClosed;
    private Contributor contributor;
    private Date submittedDate;
    private Set uploads;
    private Set editors;
    private List sortedUploadedPages;
    private Set storedUploadPages;
    private int revisionType;
    private boolean allChildrenUnchecked;
    private String submissionComment;

    public boolean getIsEditor(Contributor contr) {
    	Iterator it = getEditors().iterator();
    	while (it.hasNext()) {
    		Contributor nextContributor = (Contributor) it.next();
    		if (nextContributor.getId() == contr.getId()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * @hibernate.id generator-class="native" column="batch_id" unsaved-value="null"
     * @return
     */    
    public Long getBatchId() {
        return batchId;
    }
    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }
    /**
     * @hibernate.many-to-one column="contributor_id" class="org.tolweb.treegrow.main.Contributor"
     * @return
     */
    public Contributor getContributor() {
        return contributor;
    }
    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }
    /**
     * @hibernate.property column="closed"
     * @return
     */
    public boolean getIsClosed() {
        return isClosed;
    }
    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }
    /**
     * @hibernate.property column="submitted"
     * @return
     */
    public boolean getIsSubmitted() {
        return isSubmitted;
    }
    public void setIsSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }
    /**
     * @hibernate.property column="submitted_date"
     * @return
     */
    public Date getSubmittedDate() {
        return submittedDate;
    }
    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }
    
    /**
     * @hibernate.set table="Uploads_To_Batches"
     * @hibernate.collection-key column="batch_id"
     * @hibernate.collection-many-to-many class="org.tolweb.treegrowserver.Upload" column="upload_id"
     */
    public Set getUploads() {
        if (uploads == null) {
            uploads = new HashSet();
        }
        return uploads;
    }
    
    public void setUploads(Set value) {
        uploads = value;
    }
    
    public void addToUploads(Upload upload) {
        getUploads().add(upload);
    }
    
	/**
	 * @hibernate.set table="Editors_For_Batches"
     * @hibernate.collection-key column="batch_id"
     * @hibernate.collection-many-to-many class="org.tolweb.treegrow.main.Contributor" column="editor_id"
	 */
	public Set getEditors() {
		return editors;
	}
	/**
	 * @param editors The editors to set.
	 */
	public void setEditors(Set editors) {
		this.editors = editors;
	}
	
	/**
	 * Iterates through the uploads in this batch and chks to see if any of them have
	 * this node in their UploadedNodes sets
	 * @param node
	 * @return
	 */
	public boolean getNodeHasBeenUploadedInBatch(Long nodeId) {
	    return getNodeHasBeenUploadedInBatch(nodeId, null);
	}

	/**
	 * Iterates through the uploads in this batch and chks to see if any of them have
	 * this node in their UploadedNodes sets.  If the download parameter is not null,
	 * it restricts the search to only those uploads associated with the download.
	 * @param node
	 * @param download The download to restrict uploads to
	 * @return
	 */	
	public boolean getNodeHasBeenUploadedInBatch(Long nodeId, Download download) {
	    if (uploads != null) {
	        UploadNode un = new UploadNode();
	        un.setNodeId(nodeId);
	        Iterator it = uploads.iterator();
	        while (it.hasNext()) {
	            Upload upload = (Upload) it.next();
	            if (upload.getUploadedNodes().contains(un)) {
	                if (download == null) {
	                    return true;
	                } else {
	                    if (upload.getDownload().getDownloadId().equals(download.getDownloadId())) {
	                        return true;
	                    }
	                }
	            }
	        }
	        return false;
	    } else {
	        return false;
	    }	    
	}
    
    /**
     * Used to loop through all of the uploads and remove the pages
     * associated with them
     * @param pageIds
     */
    public void removePagesFromUpload(Collection pageIds) {
        for (Iterator iter = getUploads().iterator(); iter.hasNext();) {
            Upload nextUpload = (Upload) iter.next();
            Set uploadedPages = nextUpload.getUploadedPages();
            for (Iterator iterator = new HashSet(uploadedPages).iterator(); iterator
                    .hasNext();) {
                UploadPage nextPage = (UploadPage) iterator.next();
                if (pageIds.contains(nextPage.getPageId())) {
                    uploadedPages.remove(nextPage);
                }
            }
            nextUpload.setUploadedPages(uploadedPages);
        }
    }
	
	public Set getUploadedPagesSet() {
        if (storedUploadPages != null) {
            return storedUploadPages;
        } else {
    	    Set uploadedPagesSet = new HashSet();
    	    for (Iterator iter = getUploads().iterator(); iter.hasNext();) {
                Upload nextUpload = (Upload) iter.next();
                uploadedPagesSet.addAll(nextUpload.getUploadedPages());
            }
    	    return uploadedPagesSet;
        }
	}
    
    public void setUploadedPagesSet(Set value) {
        storedUploadPages = value;
    }
	
	/**
	 * Returns a set of all the UploadNodes that have been uploaded as part of this batch
	 * @return
	 */
	public Set getUploadedNodesSet() {
	    Set uploadNodesSet = new HashSet();
	    for (Iterator iter = getUploads().iterator(); iter.hasNext();) {
            Upload upload = (Upload) iter.next();
            uploadNodesSet.addAll(upload.getUploadedNodes());
        }
	    return uploadNodesSet;
	}
	
	/**
	 * Returns a merged set of UploadNodes and DownloadNodes that were part of any upload
	 * or download related to this batch.
	 * @return
	 */
	public Set getUploadedAndDownloadedNodesSet() {
	    Set uploadedAndDownloadedNodesSet = new HashSet();
	    uploadedAndDownloadedNodesSet.addAll(getUploadedNodesSet());
	    uploadedAndDownloadedNodesSet.addAll(getDownloadedNodesSet());
	    return uploadedAndDownloadedNodesSet;
	}
	
	public Set getUploadedAndDownloadedNodeIdsSet() {
	    Set returnSet = new HashSet();
	    Set wrappersSet = getUploadedAndDownloadedNodesSet();
	    for (Iterator iter = wrappersSet.iterator(); iter.hasNext();) {
            AbstractNodeWrapper element = (AbstractNodeWrapper) iter.next();
            returnSet.add(element.getNodeId());
        }
	    return returnSet;
	}
	
	public Set getDownloadedNodesSet() {
	    Set downloadNodesSet = new HashSet();
	    Set downloadsSet = constructDownloadsSet();
	    for (Iterator iter = downloadsSet.iterator(); iter.hasNext();) {
            Download download = (Download) iter.next();
            downloadNodesSet.addAll(download.getDownloadedNodes());
        }	    
	    return downloadNodesSet;
	}
	
	/**
	 * Returns a set of all the downloaded nodes that were deleted
	 * in this batch
	 * @return
	 */
	public Set getDeletedNodeIdsSet() {
	    Set deletedNodesSet = new HashSet();
	    Set downloadNodesSet = getDownloadedNodesSet();
	    for (Iterator iter = downloadNodesSet.iterator(); iter.hasNext();) {
            DownloadNode dn = (DownloadNode) iter.next();
            if (dn.getWasDeleted()) {
                deletedNodesSet.add(dn.getNodeId());
            }
        }
	    return deletedNodesSet;
	}
	
	public Upload getMostRecentUpload() {
	    Upload mostRecentUpload = null;
	    for (Iterator iter = getUploads().iterator(); iter.hasNext();) {
            Upload upload = (Upload) iter.next();
            if (mostRecentUpload == null || upload.getUploadDate().after(mostRecentUpload.getUploadDate())) {
                mostRecentUpload = upload;
            }
        }
	    return mostRecentUpload;
	}
	
	public boolean getHasOnlyOneAuthor() {
	    Contributor lastContributor = null;
	    for (Iterator iter = getUploads().iterator(); iter.hasNext();) {
            Upload upload = (Upload) iter.next();
            Contributor nextContributor = upload.getDownload().getContributor();
            if (lastContributor == null) {
                lastContributor = nextContributor; 
            } else {
                if (nextContributor.getId() != lastContributor.getId()) {
                    return false;
                }
            }
        }
	    return true;
	}
	
	private Set constructDownloadsSet() {
	    Set downloadsSet = new HashSet();
	    for (Iterator iter = getUploads().iterator(); iter.hasNext();) {
            Upload upload = (Upload) iter.next();
            downloadsSet.add(upload.getDownload());
	    }
	    return downloadsSet;
	}

    /**
     * transient property for storing pages according
     * to their tree position (used for publication purposes)
     * @return Returns the sortedUploadedPages.
     */
    public List getSortedUploadedPages() {
        return sortedUploadedPages;
    }

    /**
     * @param sortedUploadedPages The sortedUploadedPages to set.
     */
    public void setSortedUploadedPages(List sortedUploadedPages) {
        this.sortedUploadedPages = sortedUploadedPages;
    }

	public int getRevisionType() {
		return revisionType;
	}

	public void setRevisionType(int revisionType) {
		this.revisionType = revisionType;
	}

	public boolean getAllChildrenUnchecked() {
		return allChildrenUnchecked;
	}
	public void setAllChildrenUnchecked(boolean allChildrenUnchecked) {
		this.allChildrenUnchecked = allChildrenUnchecked;
	}

	public String getSubmissionComment() {
		return submissionComment;
	}

	public void setSubmissionComment(String submissionComment) {
		this.submissionComment = submissionComment;
	}
}
