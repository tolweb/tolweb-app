/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="TreeGifs"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class TreeGif {
    private Integer height, width;
    private String name;
    private String mapString;
    private Long pageId;
    private Long id;
    

    /**
     * @hibernate.id generator-class="native" 
     * @return
     */
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @hibernate.property
     * @return
     */
    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }
    /**
     * @hibernate.property
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFilenameNoPath() {
        return name.substring(name.lastIndexOf('/') + 1);
    }
    
    /**
     * @hibernate.property column="page_id"
     * @return
     */
    public Long getPageId() {
        return pageId;
    }
    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }
    /**
     * @hibernate.property
     * @return
     */
    public Integer getWidth() {
        return width;
    }
    public void setWidth(Integer width) {
        this.width = width;
    }
    /**
     * @hibernate.property column="mapstring"
     * @return
     */
    public String getMapString() {
        return mapString;
    }
    public void setMapString(String mapString) {
        this.mapString = mapString;
    }
}
