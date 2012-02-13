/*
 * Created on Jan 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.main;

import java.io.Serializable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class  table="Image_Versions"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class ImageVersion implements Comparable, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2888538986204007151L;
	private Long versionId;
    private NodeImage image;
    private String versionName;
    private String fileName;
    private boolean isMaster;
    private boolean isMaxSize;
    private Integer width;
    private Integer height;
    private String filesize;
    private Contributor contributor;
    
    /**
     * @hibernate.many-to-one column="contributor_id" class="org.tolweb.treegrow.main.Contributor"
     * @return Returns the contributor.
     */
    public Contributor getContributor() {
        return contributor;
    }
    /**
     * @param contributor The contributor to set.
     */
    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }
    /**
     * @hibernate.property column="filename"
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /**
     * @hibernate.many-to-one column="image_id" class="org.tolweb.treegrow.main.NodeImage"
     * @return Returns the image.
     */
    public NodeImage getImage() {
        return image;
    }
    /**
     * @param image The image to set.
     */
    public void setImage(NodeImage image) {
        this.image = image;
    }
    /**
     * @hibernate.property column="is_master"
     * @return Returns the isMaster.
     */
    public boolean getIsMaster() {
        return isMaster;
    }
    /**
     * @param isMaster The isMaster to set.
     */
    public void setIsMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }
    /**
     * @hibernate.property column="is_maxsize"
     * @return Returns the isMaxSize.
     */
    public boolean getIsMaxSize() {
        return isMaxSize;
    }
    /**
     * @param isMaxSize The isMaxSize to set.
     */
    public void setIsMaxSize(boolean isMaxSize) {
        this.isMaxSize = isMaxSize;
    }
    /**
     * @hibernate.id generator-class="native" column="version_id"
     * @return Returns the versionId.
     */
    public Long getVersionId() {
        return versionId;
    }
    /**
     * @param versionId The versionId to set.
     */
    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }
    /**
     * @hibernate.property column="name"
     * @return Returns the versionName.
     */
    public String getVersionName() {
        return versionName;
    }
    /**
     * @param versionName The versionName to set.
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    /**
     * @hibernate.property column="filesize"
     * @return Returns the filesize.
     */
    public String getFileSize() {
        return filesize;
    }
    /**
     * @param filesize The filesize to set.
     */
    public void setFileSize(String filesize) {
        this.filesize = filesize;
    }
    /**
     * @hibernate.property
     * @return Returns the height.
     */
    public Integer getHeight() {
        return height;
    }
    /**
     * @param height The height to set.
     */
    public void setHeight(Integer height) {
        this.height = height;
    }
    /**
     * @hibernate.property
     * @return Returns the width.
     */
    public Integer getWidth() {
        return width;
    }
    /**
     * @param width The width to set.
     */
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    /**
     * Returns whether this version was automatically generated.
     * @return
     */
    public boolean getIsGenerated() {
        return getContributor() == null;
    }
    
    /**
     * Sort image versions according to the following criteria:
     * 1) If one version is taller than another, it is greater
     * 2) If two versions have the same height and one is autogenerated
     *    and one is manually uploaded, the manually uploaded one is greater
     * 3) Have the logic inverted so that the versions sort in descending order
     */
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        } else if (!ImageVersion.class.isInstance(o)) {
            return -1;
        } else {
            ImageVersion other = (ImageVersion) o;
            if (getHeight() == null) {
                return 1;
            } else if (other.getHeight() == null) {
                return -1;
            }
            int myHeight = getHeight().intValue();
            int otherHeight = other.getHeight().intValue();
            if (myHeight != otherHeight) {
                if (myHeight > otherHeight) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                if (getIsGenerated() && !other.getIsGenerated()) {
                    return 1;
                } else if (other.getIsGenerated() && !getIsGenerated()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }
    
    public String toString() {
        return "Image version with id: " + getVersionId() + " and name: " + getVersionName();
    }
}