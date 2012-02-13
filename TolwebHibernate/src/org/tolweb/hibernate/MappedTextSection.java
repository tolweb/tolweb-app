/*
 * Created on Aug 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;

import org.tolweb.treegrow.page.TextSection;

/**
 * Represents an individual section of text for a page. 
 * 
 * @author dmandel
 *
 * @hibernate.class table="SECTIONS"
 * @hibernate.cache usage="nonstrict-read-write"
 */
public class MappedTextSection extends TextSection implements Serializable {
    /** use for serialization of the object */
	private static final long serialVersionUID = 6852392679517724514L;
	private Long textSectionId;
    private String notes;

	/**
	 * @hibernate.id generator-class="native" column="section_id" unsaved-value="null"
	 */
    public Long getTextSectionId() {
        return textSectionId;
    }
    
    public void setTextSectionId(Long value) {
        textSectionId = value;
    }

    /**
     * @hibernate.property column="page_order"
     */
    public int getOrder() {
        return super.getOrder();
    }
    
    public int hashCode() {
        return super.getHashCode();
    }
    
    public boolean equals(Object o) {
        return super.doEquals(o);
    }
    
    public String getHeadingNoSpaces() {
        return getHeading().replaceAll("\\s", "");
    }

    /**
     * @hibernate.property
     * @return Returns the notes.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes The notes to set.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
