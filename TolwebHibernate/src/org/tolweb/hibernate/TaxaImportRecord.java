package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Date;

/**
 * Models a log of taxa import operations performed with the Taxa Import XML Ingest Tools/Workflow
 * @author lenards
 * @hibernate.class table="TaxaImportLog"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class TaxaImportRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8416797252333371331L;

	private Long id = Long.valueOf(-1);
	private Long basalNodeId;
	private String ingest;
	private boolean reconcileWithPrevious;
	private boolean preserveNodeProperties;
	private boolean preserveNodeName;
	private String uploadedBy;
	private Date timestamp;
	
	public String toString() {
		return "[id: " + id + " node-id:" + basalNodeId + " timestamp:" + timestamp + "]";
	}
	
	/**
	 * @hibernate.id generator-class="native" unsaved-value="-1"
	 * Returns the auto-generated identity for the record
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * The string representation of the xml ingest format
	 * @return the ingest
	 * @hibernate.property 
	 */
	public String getIngest() {
		return ingest;
	}
	
	/**
	 * @param ingest the ingest to set
	 */
	public void setIngest(String ingest) {
		this.ingest = ingest;
	}
	
	/**
	 * @return the reconcileWithPrevious
	 * @hibernate.property 
	 */
	public boolean isReconcileWithPrevious() {
		return reconcileWithPrevious;
	}
	
	/**
	 * @param reconcileWithPrevious the reconcileWithPrevious to set
	 */
	public void setReconcileWithPrevious(boolean reconcileWithPrevious) {
		this.reconcileWithPrevious = reconcileWithPrevious;
	}
	
	/**
	 * @return the preserveNodeProperties
	 * @hibernate.property 
	 */
	public boolean isPreserveNodeProperties() {
		return preserveNodeProperties;
	}
	
	/**
	 * @param preserveNodeProperties the preserveNodeProperties to set
	 */
	public void setPreserveNodeProperties(boolean preserveNodeProperties) {
		this.preserveNodeProperties = preserveNodeProperties;
	}
	
	/**
	 * @return the preserveNodeName
	 * @hibernate.property 
	 */
	public boolean isPreserveNodeName() {
		return preserveNodeName;
	}
	
	/**
	 * @param preserveNodeName the preserveNodeName to set
	 */
	public void setPreserveNodeName(boolean preserveNodeName) {
		this.preserveNodeName = preserveNodeName;
	}
	
	/**
	 * @return the uploadedBy
	 * @hibernate.property 
	 */
	public String getUploadedBy() {
		return uploadedBy;
	}
	
	/**
	 * @param uploadedBy the uploadedBy to set
	 */
	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	
	/**
	 * @return the timestamp
	 * @hibernate.property 
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the basalNodeId
	 * @hibernate.property 
	 */
	public Long getBasalNodeId() {
		return basalNodeId;
	}

	/**
	 * @param basalNodeId the basalNodeId to set
	 */
	public void setBasalNodeId(Long basalNodeId) {
		this.basalNodeId = basalNodeId;
	}
}
