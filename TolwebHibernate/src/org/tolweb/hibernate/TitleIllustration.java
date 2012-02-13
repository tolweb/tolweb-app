/*
 * Created on Oct 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;

import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.OrderedObject;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="GRAPHICS"
 */
public class TitleIllustration extends OrderedObject implements Comparable, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1220317690510993861L;
	public static final int TOL_DEFAULT_HEIGHT = 0;
    public static final int BRANCH_DEFAULT_HEIGHT = 1;
    public static final int CUSTOM_HEIGHT = 2;
    public static final int BRANCH_TILLUS_DEFAULT_HEIGHT = 250;
    public static final int LEAF_TILLUS_DEFAULT_HEIGHT = 300;    
    
    private Long id;
    private Long pageId;
    private Long versionId;
    private ImageVersion version;
    
    /**
     * @hibernate.id generator-class="native" column="graphic_id"
     * @return
     */
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @hibernate.property column="version_id"
     * @return
     */
    public Long getVersionId() {
        return versionId;
    }
    public void setVersionId(Long value) {
        this.versionId = value;
    }
    /**
     * Overridden so the hibernate mapping can be here
     * @hibernate.property column="page_order"
     * @return
     */
    public int getOrder() {
        return super.getOrder();
    }

    public NodeImage getImage() {
        if (getVersion() != null) {
            return getVersion().getImage();
        } else {
            return null;
        }
    }

	public boolean equals(Object o) {
	    return super.doEquals(o);
	}
	
	public int hashCode() {
		return super.getHashCode();
	}
    /**
     * @return Returns the version.
     */
    public ImageVersion getVersion() {
        return version;
    }
    /**
     * @param version The version to set.
     */
    public void setVersion(ImageVersion version) {
        this.version = version;
        try {
        	if (this.version != null) {
        		setVersionId(version.getVersionId());
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String toString() {
        return "Title Illustration id: " + id + " pointing at image version: " + versionId;
    }
}
