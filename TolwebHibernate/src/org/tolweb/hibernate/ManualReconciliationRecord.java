package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Date;

/**
 * Preserves the state of the source and target nodes before 
 * a swap operation is preformed (leaving the target node 
 * permanently retired from the system).  
 * @author lenards
 * @hibernate.class table="ManualReconciliationLog"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class ManualReconciliationRecord implements Serializable {
	/** */
	private static final long serialVersionUID = 8219637801074359796L;
	private Long id = Long.valueOf(-1);
	private Long basalNodeId;
	private Long sourceNodeId;
    private String sourceNodeName;
    private String sourceNodeManifest;
    private Long targetNodeId;
    private String targetNodeName;
    private String targetNodeManifest;
    private String resolvedBy;
    private Date timestamp;
    
    public String toString() {
    	return "[id:" + id + " basal node id:" + basalNodeId + " time:" + timestamp + "]";
    }
    
	/**
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
	
	/**
	 * @return the sourceNodeId
	 * @hibernate.property
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
	 * @return the sourceNodeName
	 * @hibernate.property
	 */
	public String getSourceNodeName() {
		return sourceNodeName;
	}
	/**
	 * @param sourceNodeName the sourceNodeName to set
	 */
	public void setSourceNodeName(String sourceNodeName) {
		this.sourceNodeName = sourceNodeName;
	}
	
	/**
	 * @return the sourceNodeManifest
	 * @hibernate.property
	 */
	public String getSourceNodeManifest() {
		return sourceNodeManifest;
	}
	/**
	 * @param sourceNodeManifest the sourceNodeManifest to set
	 */
	public void setSourceNodeManifest(String sourceNodeManifest) {
		this.sourceNodeManifest = sourceNodeManifest;
	}
	
	/**
	 * @return the targetNodeId
	 * @hibernate.property
	 */
	public Long getTargetNodeId() {
		return targetNodeId;
	}
	/**
	 * @param targetNodeId the targetNodeId to set
	 */
	public void setTargetNodeId(Long targetNodeId) {
		this.targetNodeId = targetNodeId;
	}
	
	/**
	 * @return the targetNodeName
	 * @hibernate.property
	 */
	public String getTargetNodeName() {
		return targetNodeName;
	}
	/**
	 * @param targetNodeName the targetNodeName to set
	 */
	public void setTargetNodeName(String targetNodeName) {
		this.targetNodeName = targetNodeName;
	}
	
	/**
	 * @return the targetNodeManifest
	 * @hibernate.property
	 */
	public String getTargetNodeManifest() {
		return targetNodeManifest;
	}
	/**
	 * @param targetNodeManifest the targetNodeManifest to set
	 */
	public void setTargetNodeManifest(String targetNodeManifest) {
		this.targetNodeManifest = targetNodeManifest;
	}
	
	/**
	 * @return the resolvedBy
	 * @hibernate.property
	 */
	public String getResolvedBy() {
		return resolvedBy;
	}
	/**
	 * @param resolvedBy the resolvedBy to set
	 */
	public void setResolvedBy(String resolvedBy) {
		this.resolvedBy = resolvedBy;
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
}
