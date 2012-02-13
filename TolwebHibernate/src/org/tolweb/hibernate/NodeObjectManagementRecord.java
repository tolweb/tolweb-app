package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Date;

/**
 * Models a log of all operations performed with the NodeObjectManager
 * @author lenards
 * @hibernate.class table="NodeObjectManagementLog"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class NodeObjectManagementRecord implements Serializable {
	 /** */
	private static final long serialVersionUID = 277118355814638528L;
	private Long id = Long.valueOf(-1);
	 private Long sourceNodeId;
	 private Long destNodeId;
	 private String logEntry;
	 private String modifiedBy;
	 private Date timestamp;
	 
	 public NodeObjectManagementRecord() {
		 timestamp = new Date();
	 }

	/**
	 * @hibernate.id generator-class="native" unsaved-value="-1"
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
	 * @hibernate.property 
	 * @return the sourceNodeId
	 */
	public Long getSourceNodeId() {
		return sourceNodeId;
	}

	/**
	 * @param sourceNodeId the sourceNodeId to set
	 */
	public void setSourceNodeId(Long sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}

	/**
	 * @hibernate.property
	 * @return the destNodeId
	 */
	public Long getDestNodeId() {
		return destNodeId;
	}

	/**
	 * @param destNodeId the destNodeId to set
	 */
	public void setDestNodeId(Long destNodeId) {
		this.destNodeId = destNodeId;
	}

	/**
	 * @hibernate.property
	 * @return the logEntry
	 */
	public String getLogEntry() {
		return logEntry;
	}

	/**
	 * @param logEntry the logEntry to set
	 */
	public void setLogEntry(String logEntry) {
		this.logEntry = logEntry;
	}

	/**
	 * @hibernate.property
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @hibernate.property
	 * @return the timestamp
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
}
