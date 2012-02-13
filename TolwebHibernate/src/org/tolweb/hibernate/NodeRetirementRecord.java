package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author lenards
 * @hibernate.class table="NodeRetirementLog"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class NodeRetirementRecord implements Serializable {
	/** */
	private static final long serialVersionUID = 8865059487484208448L;
	private Long id = Long.valueOf(-1); 
	private Long nodeId;
	private String nodeName;
	private Long retiredFromClade;
	private String retiredBy;
	private Date timestamp;
	
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
	 * @return the nodeName
	 * @hibernate.property
	 */
	public String getNodeName() {
		return nodeName;
	}
	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	/**
	 * @return the retiredFromClade
	 * @hibernate.property
	 */
	public Long getRetiredFromClade() {
		return retiredFromClade;
	}
	/**
	 * @param retiredFromClade the retiredFromClade to set
	 */
	public void setRetiredFromClade(Long retiredFromClade) {
		this.retiredFromClade = retiredFromClade;
	}
	
	/**
	 * @return the retiredBy
	 * @hibernate.property
	 */
	public String getRetiredBy() {
		return retiredBy;
	}
	/**
	 * @param retiredBy the retiredBy to set
	 */
	public void setRetiredBy(String retiredBy) {
		this.retiredBy = retiredBy;
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
