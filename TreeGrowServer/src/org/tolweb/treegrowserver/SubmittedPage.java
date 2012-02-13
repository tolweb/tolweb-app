package org.tolweb.treegrowserver;

public class SubmittedPage extends UploadPage {
    private boolean wasPublished;
    private int revisionType;
    
    public static final int MINOR_REVISION = 0;
    public static final int REGULAR_REVISION = 1;
    public static final int MAJOR_REVISION = 2;
    public static final int NEW_PAGE = 3;
    
    /**
     * overridden for xdoclet
     * @hibernate.property column="pageId"
     * @return
     */
    public Long getPageId() {
        return super.getPageId();
    }
    
    /**
     * @hibernate.property
     * @return Returns the wasPublished.
     */
    public boolean getWasPublished() {
        return wasPublished;
    }

    /**
     * @param wasPublished The wasPublished to set.
     */
    public void setWasPublished(boolean wasPublished) {
        this.wasPublished = wasPublished;
    }
    /**
     * @hibernate.property
     * @return
     */
	public int getRevisionType() {
		return revisionType;
	}

	public void setRevisionType(int revisionType) {
		this.revisionType = revisionType;
	}
	
	/**
	 * Determines if the revision value is a regular or major revision 
	 * @param revision the revision type of the page being submitted. 
	 * @return true, indicating it is a regular or major revision; otherwise, false. 
	 */
	public static boolean isRegularOrMajorRevision(int revision) {
		return revision == REGULAR_REVISION || revision == MAJOR_REVISION; 
	}
}
