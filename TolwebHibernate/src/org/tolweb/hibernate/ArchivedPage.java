package org.tolweb.hibernate;

import java.util.Date;


/**
 * @hibernate.class table="ArchivedPages"
 * @author dmandel
 *
 */
public class ArchivedPage extends PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2138773688979388338L;
	private Long pageId;
	private Long nodeId;
	private Date archiveDate;
	private String pageTitle;
	private String status;
	private boolean isMajorRevision;
	/**
	 * @hibernate.property
	 * @return
	 */
	public Date getArchiveDate() {
		return archiveDate;
	}
	public void setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public boolean getMajorRevision() {
		return isMajorRevision;
	}
	public void setMajorRevision(boolean isMajorRevision) {
		this.isMajorRevision = isMajorRevision;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public Long getPageId() {
		return pageId;
	}
	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	/**
	 * @hibernate.property
	 * @return
	 */		
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */			
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
