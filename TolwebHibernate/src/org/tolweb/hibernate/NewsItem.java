package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author lenards
 * @hibernate.class table="NewsItems"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class NewsItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2492245079046472308L;

	private Long newsItemId;
	private String newsItemText;
	private Boolean active;
	private Date createdDate;
	private String createdBy;
	
	/**
	 * @hibernate.id generator-class="native" column="newsItemId" unsaved-value="-1"
	 * @return the newsItemId
	 */
	public Long getNewsItemId() {
		return newsItemId;
	}
	/**
	 * @param newsItemId the newsItemId to set
	 */
	public void setNewsItemId(Long newsItemId) {
		this.newsItemId = newsItemId;
	}
	/**
	 * @hibernate.property 
	 * @return the newsItemText
	 */
	public String getNewsItemText() {
		return newsItemText;
	}
	/**
	 * @param newsItemText the newsItemText to set
	 */
	public void setNewsItemText(String newsItemText) {
		this.newsItemText = newsItemText;
	}
	/**
	 * @hibernate.property
	 * @return the createdDate
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
	 * @hibernate.property
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @hibernate.property
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}
}
