package org.tolweb.btol;

import java.util.Set;

import org.tolweb.hibernate.PersistentObject;

/**
 * @hibernate.class table="WorkingGroups"
 * @author dmandel
 */
public class WorkingGroup extends PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4063570285863862257L;
	private String name;
	private Set contributorIds;
	private Set contributors;
	/**
	 * @hibernate.set table="WorkingGroupContributors" lazy="false" sort="natural" cascade="all"
	 * @hibernate.collection-element type="int" column="contributorId"
	 * @hibernate.collection-key column="workingGroupId"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 * @return
	 */
	public Set getContributorIds() {
		return contributorIds;
	}
	public void setContributorIds(Set contributorIds) {
		this.contributorIds = contributorIds;
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
	public Set getContributors() {
		return contributors;
	}
	public void setContributors(Set contributors) {
		this.contributors = contributors;
	}
}
