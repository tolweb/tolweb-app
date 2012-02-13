package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.tolweb.content.licensing.ContentLicenseClass;

/**
 * @author lenards
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="WebServicesKeys"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class WebServicesKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6198673452261126063L;
	
	private Long id = -1L;
	private String webServicesKey;
	private String userName; 
	private String userEmail;
	private String userUrl;
	private String intendedUse;
	private byte useCategory;
	private Date updated;
	private Date created;

	public static WebServicesKey createWebServicesKey() {
		WebServicesKey wsKey = new WebServicesKey();
		UUID newKey = UUID.randomUUID();
		wsKey.setWebServicesKey(newKey.toString());
		wsKey.setCreated(new Date());
		wsKey.setUpdated(new Date());
		return wsKey;
	}
	
	public UUID getWebServiceKeyAsUUID() {
		return UUID.fromString(getWebServicesKey());
	}
	
	public String toString() {
		return String.format(
					"user-name: %1$s \n user-email: %2$s \n key: %3$s", 
					userName, userEmail, webServicesKey);
	}
	
	/**
	 * Gets the content license class representation of this 
	 * key
	 * @return
	 */
	public ContentLicenseClass getContentLicenseClass() {
		return ContentLicenseClass.fromByte(useCategory);
	}
	
	/**
	 * @hibernate.id generator-class="native" column="id" unsaved-value="-1"
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
	 * @return the webServiceKey
	 */
	public String getWebServicesKey() {
		return webServicesKey;
	}
	/**
	 * @param webServicesKey the webServiceKey to set
	 */
	public void setWebServicesKey(String webServicesKey) {
		this.webServicesKey = webServicesKey;
	}
	/**
	 * @hibernate.property
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @hibernate.property
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}
	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	/**
	 * @hibernate.property
	 * @return the userUrl
	 */
	public String getUserUrl() {
		return userUrl;
	}
	/**
	 * @param userUrl the userUrl to set
	 */
	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}
	/**
	 * @hibernate.property
	 * @return the intendedUse
	 */
	public String getIntendedUse() {
		return intendedUse;
	}
	/**
	 * @param intendedUse the intendedUse to set
	 */
	public void setIntendedUse(String intendedUse) {
		this.intendedUse = intendedUse;
	}
	/**
	 * @hibernate.property
	 * @return the useCategory
	 */
	public byte getUseCategory() {
		return useCategory;
	}
	/**
	 * @param useCategory the useCategory to set
	 */
	public void setUseCategory(byte useCategory) {
		this.useCategory = useCategory;
	}
	/**
	 * @hibernate.property
	 * @return 
	 */
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	/**
	 * @hibernate.property
	 * @return 
	 */
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
