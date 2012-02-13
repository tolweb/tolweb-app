package org.tolweb.hibernate;

import java.io.Serializable;

import org.tolweb.treegrow.main.Contributor;

public class ContributorPermission extends EditPermission implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7424878983626867957L;
	/**
     * @hibernate.many-to-one column="editingContributorId" class="org.tolweb.treegrow.main.Contributor" cascade="none"
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
