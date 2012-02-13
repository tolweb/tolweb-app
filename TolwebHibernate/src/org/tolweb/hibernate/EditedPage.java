package org.tolweb.hibernate;

/**
 * class used to represent that someone has edited a page
 * @hibernate.class table="EditedPages"
 */
public class EditedPage extends PersistentObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4523390239381249614L;
	private Long contributorId;
    private Long pageId;
    private int pageType;
    public static final int BRANCH_LEAF_TYPE = 0;
    public static final int ARTICLE_NOTE_TYPE = 1;
    
    /**
     * @hibernate.property
     * @return Returns the contributorId.
     */
    public Long getContributorId() {
        return contributorId;
    }
    /**
     * @param contributorId The contributorId to set.
     */
    public void setContributorId(Long contributorId) {
        this.contributorId = contributorId;
    }
    /**
     * @hibernate.property
     * @return Returns the pageId.
     */
    public Long getPageId() {
        return pageId;
    }
    /**
     * @param pageId The pageId to set.
     */
    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }
    /**
     * @hibernate.property
     * @return Returns the pageType.
     */    
	public int getPageType() {
		return pageType;
	}
	public void setPageType(int pageType) {
		this.pageType = pageType;
	}
}
