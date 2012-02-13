/*
 * PageContributor.java
 *
 * Created on December 22, 2003, 1:21 PM
 */

package org.tolweb.treegrow.page;

import java.io.Serializable;

import org.tolweb.treegrow.main.*;

/**
 *
 * @author  dmandel
 */
public class PageContributor extends OrderedObject implements AuxiliaryChangedFromServerProvider, Cloneable, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6867818882486636690L;
	private int contributorId;
    private Contributor contributor;
    private boolean isAuthor = true;
    private boolean isContact = true;
    private boolean isCopyOwner = true;    
    private boolean changed = false;
    private Page page;    
    
    /** Creates a new instance of PageContributor */
    public PageContributor(Page p, int contId, boolean author, boolean corr, boolean copy, int order) {
        page = p;
        contributorId = contId;
        isAuthor = author;
        isContact = corr;
        isCopyOwner = copy;
        setOrder(order);
    }

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
	
	public PageContributor() {
		super();
	}
    
	public String toString() {
		return "" + getContributorId();
	}
	
    /**
     * @hibernate.property column="contributor_id"
     * @return
     */
    public int getContributorId() {
        return contributorId;
    }
    
    public void setContributorId(int value) {
        contributorId = value;
    }
    
    /**
     * The duplication of this and the contributor id field is due to Contributors living in
     * a separate database at the moment.
     * @return
     */
    public Contributor getContributor() {
    	return contributor;
    }
    
    public void setContributor(Contributor value) {
    	contributor = value;
        if (value != null) {
            setContributorId(contributor.getId());
        }
    }
    
    /**
     * @hibernate.property column="is_author"
     * @return
     */
    public boolean getIsAuthor() {
    	return isAuthor();
    }
    
    public boolean isAuthor() {
        return isAuthor;
    }
    
    public void setIsAuthor(boolean value) {
        isAuthor = value;
    }
    
    /**
     * @hibernate.property column="is_contact"
     * @return
     */
    public boolean getIsContact() {
    	return isContact();
    }
    
    public boolean isContact() {
        return isContact;
    }
    
    public void setIsContact(boolean value) {
        isContact = value;
    }
    
    public void setIsCopyOwner(boolean value) {
        isCopyOwner = value;
    }
    
    public boolean isCopyOwner() {
        return isCopyOwner;
    }
    
    /**
     * @hibernate.property column="is_copy_owner"
     * @return
     */
    public boolean getIsCopyOwner() {
    	return isCopyOwner();
    }
    
    /**
     * Returns whether the page this author is attached to thinks that 
     * its authors have changed
     * 
     * @return Whether the page thinks its authors have changed
     */
    public boolean auxiliaryChangedFromServer() {   
        return page.getContributorsChanged();
    }

    public void setAuxiliaryChangedFromServer(boolean value) {
        page.setContributorsChanged(value);
    }

    public boolean changedFromServer() {
        return changed;
    }

    public void setChangedFromServer(boolean value) {
        changed = value;
    }
    
	/**
	 * overridden in order to add hibernate property mapping
	 * @hibernate.property column="page_order"
	 * @return
	 */
	public int getOrder() {
		return super.getOrder();
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null) {
			return false;
		} else if (!(o instanceof PageContributor)) {
			return false;
		} else {
		    PageContributor other = (PageContributor) o;
			return getOrder() == other.getOrder();
		}
	}
	
	public int hashCode() {
		return Integer.valueOf(getOrder()).hashCode();
	}    
}
