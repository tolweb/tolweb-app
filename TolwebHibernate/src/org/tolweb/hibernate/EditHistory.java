package org.tolweb.hibernate;

import java.util.Date;

/**
 * 
 * @author dmandel
 * @hibernate.class table="EditHistories"
 */
public class EditHistory extends PersistentObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1159220117719694146L;
	private Long createdContributorId;
    private Long lockedContributorId;
    private Long lastEditedContributorId;
    private Date creationDate;
    private Date lockedDate;
    private Date lastEditedDate;
    /**
     * @hibernate.property
     * @return Returns the createdContributorId.
     */
    public Long getCreatedContributorId() {
        return createdContributorId;
    }
    /**
     * @param createdContributorId The createdContributorId to set.
     */
    public void setCreatedContributorId(Long createdContributorId) {
        this.createdContributorId = createdContributorId;
    }
    /**
     * @hibernate.property
     * @return Returns the creationDate.
     */
    public Date getCreationDate() {
        return creationDate;
    }
    /**
     * @param creationDate The creationDate to set.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    /**
     * @hibernate.property
     * @return Returns the lastEditedContributorId.
     */
    public Long getLockedContributorId() {
        return lockedContributorId;
    }
    /**
     * @param lastEditedContributorId The lastEditedContributorId to set.
     */
    public void setLockedContributorId(Long lastEditedContributorId) {
        this.lockedContributorId = lastEditedContributorId;
    }
    /**
     * @hibernate.property
     * @return Returns the lastEditedDate.
     */
    public Date getLockedDate() {
        return lockedDate;
    }
    /**
     * @param lastEditedDate The lastEditedDate to set.
     */
    public void setLockedDate(Date lastEditedDate) {
        this.lockedDate = lastEditedDate;
    }
    /**
     * @hibernate.property
     * @return Returns the lastEditedDate.
     */
    public Date getLastEditedDate() {
        return lastEditedDate;
    }
    /**
     * @param lastEditedDate The lastEditedDate to set.
     */
    public void setLastEditedDate(Date lastEditedDate) {
        this.lastEditedDate = lastEditedDate;
    }
    /**
     * @hibernate.property
     * @return Returns the lastEditedContributorId.
     */
    public Long getLastEditedContributorId() {
        return lastEditedContributorId;
    }
    /**
     * @param lastEditedContributorId The lastEditedContributorId to set.
     */
    public void setLastEditedContributorId(Long lastEditedContributorId) {
        this.lastEditedContributorId = lastEditedContributorId;
    }
}
