/*
 * Created on May 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="Portfolios"
 */
public class Portfolio implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6923404007041280735L;
	private Long id; 
    private SortedSet sections;
    /**
     * @hibernate.id generator-class="native"
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**	 
     * @hibernate.set table="PortfoliosToSections" lazy="false" sort="natural" cascade="all-delete-orphan"
	 * @hibernate.collection-many-to-many class="org.tolweb.hibernate.PortfolioSection" column="section_id" outer-join="true"
	 * @hibernate.collection-key column="portfolio_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
     * @return Returns the sections.
     */
    public SortedSet getSections() {
        if (sections == null) {
            sections = new TreeSet();
        }
        return sections;
    }
    /**
     * @param sections The sections to set.
     */
    public void setSections(SortedSet sections) {
        this.sections = sections;
    }
    
    public boolean getIsEmpty() {
        if (getSections().size() == 0) {
            return true;
        } else {
            // Check and see if any of the sections have pages in them
            for (Iterator iter = getSections().iterator(); iter.hasNext();) {
                PortfolioSection section = (PortfolioSection) iter.next();
                for (Iterator iterator = section.getPages().iterator(); iterator
                        .hasNext();) {
                    PortfolioPage page = (PortfolioPage) iterator.next();
                    if (page.getDestinationId() > 0 || StringUtils.notEmpty(page.getExternalPageUrl())) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
