package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.tolweb.misc.FeatureGroupCategory;

/**
 * @author lenards
 *
 * @hibernate.class table="FeatureGroups"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class FeatureGroup implements Serializable {
	public static final String DESC_FORMAT = "(%1$s)";
	public static final String TO_STRING_FORMAT = "{id:%1$d node-id:%2$d image-id: %3$d active: %4$b}";
	private Long id = -1L;
	private Long nodeId;
	private int imageId;
	private String groupDescription;
	private String featureText;
	private int category;
	private boolean active;
	private Date createdDate;
	
	public FeatureGroup() {
		setActive(true);
	}
	
	public FeatureGroup(MappedNode mnode) {
		setNodeId(mnode.getNodeId());
		setGroupDescription(mnode.getDescription());
		setActive(true);
	}
	
	public String toString() {
		return String.format(TO_STRING_FORMAT, getId(), getNodeId(), getImageId(), isActive());
	}
	
	/**
	 * @return the id
	 * 
	 * @hibernate.id generator-class="native" column="id" unsaved-value="-1"
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
	 * 
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
	 * @return the imageId
	 * 
	 * @hibernate.property 
	 */
	public int getImageId() {
		return imageId;
	}
	/**
	 * @param imageId the imageId to set
	 */
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	/**
	 * @return the groupDescription
	 * 
	 * @hibernate.property 
	 */
	public String getGroupDescription() {
		if (groupDescription.startsWith("(") && groupDescription.endsWith(")")) {
			return groupDescription;
		}
		return String.format(DESC_FORMAT, groupDescription);
	}
	/**
	 * @param groupDescription the groupDescription to set
	 */
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
	
	/**
	 * @return the featureText
	 * @hibernate.property 
	 */
	public String getFeatureText() {
		return featureText;
	}
	/**
	 * @param featureText the featureText to set
	 */
	public void setFeatureText(String featureText) {
		this.featureText = featureText;
	}
	
	/**
	 * @return the active
	 * 
	 * @hibernate.property 
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * @return the createdDate
	 * 
	 * @hibernate.property 
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the category
	 * 
	 * @hibernate.property column="category"
	 */
	public int getCategoryValue() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategoryValue(int category) {
		this.category = category;
	} 

	public FeatureGroupCategory getCategory() {
		return FeatureGroupCategory.getValueBy(category);
	}
	
	public void setCategory(FeatureGroupCategory cat) {
		category = cat.toInt();
	}
}
