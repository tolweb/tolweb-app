package org.tolweb.hibernate;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.Validate;

/**
 * Models a log of all taxa automatically reconciled with inactive nodes.
 * @author lenards
 * @hibernate.class table="AutoReconciliationLog"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class AutoReconciliationRecord implements Serializable {
	/** */
	private static final long serialVersionUID = 9108766984656395548L;
	private Long id = Long.valueOf(-1); 
	private Long taxaImportId;
	private String cladeName;
	private Long nodeId; 
	private Long reconcileType;
	private Date timestamp;

	public static final Long MATCHED_NODE = Long.valueOf(1);
	public static final Long NEW_NODE = Long.valueOf(2);
	public static final Long ORPHANED_NODE = Long.valueOf(4);
	
	public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MMM/d HH:mm");
	
	public AutoReconciliationRecord() { }
	
	public AutoReconciliationRecord(MappedNode mnode, Long reconcileType) {
		Validate.notNull(mnode);
		setCladeName(mnode.getName());
		setNodeId(mnode.getNodeId());
		setReconcileType(reconcileType);
	}
	
	public AutoReconciliationRecord(Long taxaImportId, MappedNode mnode, Long reconcileType) {
		this(mnode, reconcileType);
		setTaxaImportId(taxaImportId);
	}	
	
	public String getTimestampFormatted() {
		return dateFormat.format(getTimestamp());
	}
	
	/**
	 * Returns the auto-generated identity for the record
	 * @return the id
	 * @hibernate.id generator-class="native" unsaved-value="-1" 
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
	 * @return the taxaImportId
	 * @hibernate.property
	 */
	public Long getTaxaImportId() {
		return taxaImportId;
	}
	/**
	 * @param taxaImportId the taxaImportId to set
	 */
	public void setTaxaImportId(Long taxaImportId) {
		this.taxaImportId = taxaImportId;
	}
	
	/**
	 * @return the cladeName
	 * @hibernate.property
	 */
	public String getCladeName() {
		return cladeName;
	}
	/**
	 * @param cladeName the cladeName to set
	 */
	public void setCladeName(String cladeName) {
		this.cladeName = cladeName;
	}
	
	/**
	 * @return the nodeId
	 * @hibernate.property
	 */
	public Long getNodeId() {
		return nodeId;
	}
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
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
	 * @return the reconcileType
	 * @hibernate.property  
	 */
	public Long getReconcileType() {
		return reconcileType;
	}
	/**
	 * @param reconcileType the reconcileType to set
	 */
	public void setReconcileType(Long reconcileType) {
		this.reconcileType = reconcileType;
	}
}
