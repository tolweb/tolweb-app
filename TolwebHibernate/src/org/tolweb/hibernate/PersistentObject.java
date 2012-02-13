package org.tolweb.hibernate;

import java.io.Serializable;

import org.tolweb.treegrow.main.Contributor;

public abstract class PersistentObject implements Serializable {
    private Long id;

    /**
     * @hibernate.id generator-class="native" column="id" unsaved-value="null"
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
    
    public int hashCode() {
    	return id.hashCode();
    }
    
    public boolean equals(Object other) {
    	if (other == null) {
    		return false;
    	}
    	boolean sameClass = other.getClass() == getClass();
    	boolean idsNotNull = getId() != null && ((PersistentObject) other).getId() != null;
    	return sameClass && idsNotNull && getId().equals(((PersistentObject) other).getId());
    } 
    public int doCompareBasedOnId(PersistentObject other) {
    	if (other == null) {
    		return 1;
    	} else {
    		Long id = getId();
    		Long otherId = other.getId();
    		if (id == null && otherId == null) {
    			return 1;
    		} else if (id != null && otherId == null) {
    			return 1;
    		} else if (otherId != null && id == null) {
    			return -1;
    		} else {
    			return id.compareTo(otherId);
    		}
    	}
    }
    /**
     * Used for selecting things from popup lists
     * @return
     */
    public String getDisplayName() {
        return toString();
    }
	protected Integer getSafeId(Contributor contr) {
		return contr != null ? Integer.valueOf(contr.getId()) : null;
	}    
}
