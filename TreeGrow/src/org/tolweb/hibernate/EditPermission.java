/*
 * Created on Apr 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditPermission {
    private int contributorId;
    protected Contributor contributor;
    /**
     * 
     * @return Returns the contributorId.
     */
    public int getContributorId() {
        return contributorId;
    }
    /**
     * @param contributorId The contributorId to set.
     */
    public void setContributorId(int contributorId) {
        this.contributorId = contributorId;
    }
    
    public boolean equals(Object other) {
        if (other == null || !EditPermission.class.isInstance(other)) {
            return false;
        } else {
            return getContributorId() == ((EditPermission) other).getContributorId();
        }
    }
    
    public int hashCode() {
        return Integer.valueOf(contributorId).hashCode();
    }
}
