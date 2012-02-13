/*
 * Created on Jun 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;

import org.tolweb.treegrow.main.OrderedObject;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PortfolioPage extends OrderedObject implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2866006022903787784L;
	public static final int ACC_PAGE_DESTINATION = 0;
    public static final int PAGE_DESTINATION = 1;
    
    private int destinationId;
    private int destinationType;
    private String comments;
    private boolean includeReferences;
    private boolean includeInternetLinks;
    private boolean includeLearningInfo;
    private boolean isExternal;
    private String pageTitle;
    private String pageType;
    private byte treehouseType;
    private String externalPageName = "";
    private String externalPageUrl = "";
    private Integer imageId;
    /**
     * @hibernate.property
     * @return Returns the comments.
     */
    public String getComments() {
        return comments;
    }
    /**
     * @param comments The comments to set.
     */
    public void setComments(String comments) {
        this.comments = comments;
    }
    /**
     * @hibernate.property column="include_internet_links"
     * @return Returns the includeInternetLinks.
     */
    public boolean getIncludeInternetLinks() {
        return includeInternetLinks;
    }
    /**
     * @param includeInternetLinks The includeInternetLinks to set.
     */
    public void setIncludeInternetLinks(boolean includeInternetLinks) {
        this.includeInternetLinks = includeInternetLinks;
    }
    /**
     * @hibernate.property column="include_learning_info"
     * @return Returns the includeLearningInfo.
     */
    public boolean getIncludeLearningInfo() {
        return includeLearningInfo;
    }
    /**
     * @param includeLearningInfo The includeLearningInfo to set.
     */
    public void setIncludeLearningInfo(boolean includeLearningInfo) {
        this.includeLearningInfo = includeLearningInfo;
    }
    /**
     * @hibernate.property column="include_references"
     * @return Returns the includeReferences.
     */
    public boolean getIncludeReferences() {
        return includeReferences;
    }
    /**
     * @param includeReferences The includeReferences to set.
     */
    public void setIncludeReferences(boolean includeReferences) {
        this.includeReferences = includeReferences;
    }
    /**
     * @hibernate.property column="destination_id"
     * @return Returns the url.
     */
    public int getDestinationId() {
        return destinationId;
    }
    /**
     * @param url The url to set.
     */
    public void setDestinationId(int destinationId) {
        this.destinationId = destinationId;
    }
    /**
     * @hibernate.property column="page_order"
     */
    public int getOrder() {
        return super.getOrder();
    }
	public boolean equals(Object o) {
	    return super.doEquals(o);
	}
	public int hashCode() {
		return super.getHashCode();
	}    
    /**
     * @hibernate.property column="destination_type"
     * @return Returns the destinationType.
     */
    public int getDestinationType() {
        return destinationType;
    }
    /**
     * @param destinationType The destinationType to set.
     */
    public void setDestinationType(int destinationType) {
        this.destinationType = destinationType;
    }
    
    public String getPageTitle() {
        return pageTitle;
    }
    
    public void setPageTitle(String value) {
        pageTitle = value;
    }
    
    public String getPageSummaryString() {
        if (getDestinationType() == PAGE_DESTINATION) {
            return "ToL " + getPageType() + " Page ID#" + getDestinationId();
        } else {
            if (pageType != null && (pageType.equals(MappedAccessoryPage.ARTICLE) || pageType.equals(MappedAccessoryPage.NOTE))) {
                return "ToL " + pageType + " ID#" + getDestinationId();
            } else {
                return "Treehouse " + pageType + " ID#" + getDestinationId();                
            }
        }
    }
    
    
    /**
     * @hibernate.property column="external_page_name"
     * @return Returns the externalPageName.
     */
    public String getExternalPageName() {
        return externalPageName;
    }
    /**
     * @param externalPageName The externalPageName to set.
     */
    public void setExternalPageName(String externalPageName) {
        this.externalPageName = externalPageName;
    }
    /**
     * @hibernate.property column="external_page_url"
     * @return Returns the externalPageUrl.
     */
    public String getExternalPageUrl() {
        return externalPageUrl;
    }
    /**
     * @param externalPageUrl The externalPageUrl to set.
     */
    public void setExternalPageUrl(String externalPageUrl) {
        this.externalPageUrl = externalPageUrl;
    }
    /**
     * @hibernate.property column="is_external"
     * @return Returns the isExternal.
     */
    public boolean getIsExternal() {
        return isExternal;
    }
    /**
     * @param isExternal The isExternal to set.
     */
    public void setIsExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }
    /**
     * @return Returns the pageType.
     */
    public String getPageType() {
        return pageType;
    }
    /**
     * @param pageType The pageType to set.
     */
    public void setPageType(String pageType) {
        this.pageType = pageType;
    }
    /**
     * @return Returns the treehouseType.
     */
    public byte getTreehouseType() {
        return treehouseType;
    }
    /**
     * @param treehouseType The treehouseType to set.
     */
    public void setTreehouseType(byte treehouseType) {
        this.treehouseType = treehouseType;
    }
    /**
     * @hibernate.property column="image_id"
     * An optional image id that can be associated with the portfolio page
     * that will show up next to the link in a presentation
     * @return Returns the imageId.
     */
    public Integer getImageId() {
        return imageId;
    }
    /**
     * @param imageId The imageId to set.
     */
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
    
    public boolean getHasImage() {
        return getImageId() != null && getImageId().intValue() > 0;        
    }
}
