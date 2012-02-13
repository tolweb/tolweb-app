/*
 * Created on Jun 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.treegrow.main.OrderedObject;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="PortfolioSections"
 */
public class PortfolioSection extends OrderedObject implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1429133513138047313L;
	private String title;
    private Long id;
    private SortedSet pages;
    private String instructions;
    
    /**
     * @hibernate.property column="section_order"
     */
    public int getOrder() {
        return super.getOrder();
    }
    /**
     * @hibernate.property
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @hibernate.id generator-class="native" column="section_id"
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long sectionId) {
        this.id = sectionId;
    }
    /**
     * @hibernate.set table="PortfolioPages" lazy="false" order-by="page_order asc" sort="natural" cascade="all-delete-orphan"
	 * @hibernate.collection-composite-element class="org.tolweb.hibernate.PortfolioPage"
	 * @hibernate.collection-key column="section_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
     * @return Returns the pages.
     */
    public SortedSet getPages() {
        if (pages == null) {
            pages = new TreeSet();
        }
        return pages;
    }
    /**
     * @param pages The pages to set.
     */
    public void setPages(SortedSet pages) {
        this.pages = pages;
    }
	public boolean equals(Object o) {
	    return super.doEquals(o);
	}
	public int hashCode() {
		return super.getHashCode();
	}    
    /**
     * @hibernate.property
     * @return Returns the instructions.
     */
    public String getInstructions() {
        return instructions;
    }
    /**
     * @param instructions The instructions to set.
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String toString() {
        return "PortfolioSection w/ order " + getOrder() + " and title " + getTitle();
    }
}
