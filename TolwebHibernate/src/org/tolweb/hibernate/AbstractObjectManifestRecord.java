package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractObjectManifestRecord implements Serializable {
	private Long id = Long.valueOf(-1); 
	private String keyValue; 
	private String manifest;
	private String updatedBy;
	private Date timestamp;
	
	public AbstractObjectManifestRecord() {
		
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
	 * @return the key
	 */
	public String getKeyValue() {
		return keyValue;
	}
	
	/**
	 * @param key the key to set
	 */
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}
	
	/**
	 * @hibernate.property 
	 * @return the manifest
	 */
	public String getManifest() {
		return manifest;
	}
	
	/**
	 * @param manifest the manifest to set
	 */
	public void setManifest(String manifest) {
		this.manifest = manifest;
	}
	
	/**
	 * @hibernate.property 
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}
	
	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
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
