/*
 * Created on Sep 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;

/**
 * @author dmandel
 *
 * Used for speedy querying of ancestor pages in order to assemble the list
 * of containing groups.  Doesn't have hibernate tags inside of it because
 * xdoclet is lame and doesn't understand composite ids.
 * 
 */
public class PageAncestor implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8373269621365568141L;
	private Long pageId, ancestorId;

    public Long getAncestorId() {
        return ancestorId;
    }
    public void setAncestorId(Long ancestorId) {
        this.ancestorId = ancestorId;
    }
       
    public Long getPageId() {
        return pageId;
    }
    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public boolean equals(Object otherAncestor) {
        if (this == otherAncestor) {
            return true;
        }
        if (otherAncestor == null) {
            return false;
        }
        if (!(otherAncestor instanceof PageAncestor)) {
            return false;
        }
        PageAncestor o = (PageAncestor) otherAncestor;
        if (!(o.getPageId().equals(getPageId()))) {
            return false;
        } else if (!(o.getAncestorId().equals(getAncestorId()))) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        return pageId.hashCode();
    }
}
