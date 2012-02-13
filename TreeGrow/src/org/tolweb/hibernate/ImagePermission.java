/*
 * Created on Apr 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;

import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ImagePermission extends EditPermission implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6886502205669839014L;
	/**
     * @hibernate.many-to-one column="contributor_id" class="org.tolweb.treegrow.main.Contributor" cascade="none"
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
        if (contributor != null) {
        	setContributorId(contributor.getId());
        }
    }
}
