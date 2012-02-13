/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadPage {
    private Long pageId;
    private int numAncestors;
    private String groupName;
    private List childPages;
    private boolean shouldBePublished;
    private Long nodeId;
    private Element lockedElement;
    private Long parentPageId;
    
    /**
     * @hibernate.property column="page_id"
     * @return
     */
    public Long getPageId() {
        return pageId;
    }
    
    public void setPageId(Long value) {
        pageId = value;
    }
    
    public boolean equals(Object o) {
    	if (o == null) {
    		return false;
    	}
    	if (!(o instanceof UploadPage)) {
    		return false;
    	}
    	if (getPageId() != null && ((UploadPage) o).getPageId() != null) {
    		return getPageId().equals(((UploadPage) o).getPageId());
    	} else {
    		return false;
    	}    	
    }
    public int hashCode() {
    	if (getPageId() != null) {
    		return getPageId().hashCode();
    	} else {
    		return System.identityHashCode(this);
    	}
    }
    
    public String toString() {
        return "UploadPage with pageId: " + pageId + " and numAncestors: " + numAncestors + " and groupName: " + groupName;
    }

    /**
     * transient property used for sorting to see
     * which pages are closest to root
     * @return Returns the numAncestors.
     */
    public int getNumAncestors() {
        return numAncestors;
    }

    /**
     * @param numAncestors The numAncestors to set.
     */
    public void setNumAncestors(int numAncestors) {
        this.numAncestors = numAncestors;
    }

    /**
     * transient property to remember the name of the
     * page that the id refers to
     * @return Returns the groupName.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName The groupName to set.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return Returns the childPages.
     */
    public List getChildPages() {
        if (childPages == null) {
            childPages = new ArrayList();
        }
        return childPages;
    }

    /**
     * @param childPages The childPages to set.
     */
    public void setChildPages(List childPages) {
        this.childPages = childPages;
    }

    /**
     * transient property to determine if a page will be published.
     * @return Returns the shouldBePublished.
     */
    public boolean getShouldBePublished() {
        return shouldBePublished;
    }

    /**
     * @param shouldBePublished The shouldBePublished to set.
     */
    public void setShouldBePublished(boolean shouldBePublished) {
        this.shouldBePublished = shouldBePublished;
    }

    /**
     * transient property to avoid multiple db queries for this
     * @return Returns the nodeId.
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId The nodeId to set.
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * transient property to remember if this page is downloaded or
     * submitted
     * @return Returns the lockedElement.
     */
    public Element getLockedElement() {
        return lockedElement;
    }

    /**
     * @param lockedElement The lockedElement to set.
     */
    public void setLockedElement(Element lockedElement) {
        this.lockedElement = lockedElement;
    }

    /**
     * @return Returns the parentPageId.
     */
    public Long getParentPageId() {
        return parentPageId;
    }

    /**
     * @param parentPageId The parentPageId to set.
     */
    public void setParentPageId(Long parentPageId) {
        this.parentPageId = parentPageId;
    }
}
