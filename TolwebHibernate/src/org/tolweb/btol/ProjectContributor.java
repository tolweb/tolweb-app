package org.tolweb.btol;

import org.tolweb.treegrow.main.Contributor;

/**
 * Wrapper class for a contributor and their attachment to a project
 * @author dmandel
 */
public class ProjectContributor {
	public static final int PROJECT_ADMINISTRATOR = -1;	
	public static final int PROJECT_MEMBER = 0;
	public static final int OTHER_AFFILIATE = 1;
	public static final int PROJECT_HELPER = 2;
	public static final int PROJECT_MEMBER_EDIT_DNA = 3;
	public static final int PROJECT_MEMBER_VIEW_MOLECULAR = 4;
	private Integer contributorId;
	private int membershipStatus;
	private Contributor actualContributor;
	/**
	 * @hibernate.property
	 * @return
	 */
	public Integer getContributorId() {
		return contributorId;
	}
	public void setContributorId(Integer contributorId) {
		this.contributorId = contributorId;
	}
	/**
	 * @hibernate.property
	 * @return
	 */	
	public int getMembershipStatus() {
		return membershipStatus;
	}
	public void setMembershipStatus(int membershipStatus) {
		this.membershipStatus = membershipStatus;
	}
	public Contributor getActualContributor() {
		return actualContributor;
	}
	public void setActualContributor(Contributor actualContributor) {
		this.actualContributor = actualContributor;
		if (actualContributor != null) {
			setContributorId(actualContributor.getId());
		} else {
			setContributorId(null);
		}
	}
	public int hashCode() {
		if (contributorId != null) {
			return contributorId.hashCode();
		} else {
			return super.hashCode();
		}
	}
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		} else if (!ProjectContributor.class.isInstance(other)) {
			return false;
		} else {
			ProjectContributor otherPc = (ProjectContributor) other;
			return getContributorId() != null && otherPc.getContributorId() != null &&
				((ProjectContributor) other).getContributorId().equals(getContributorId()) && 
				getMembershipStatus() == ((ProjectContributor) other).getMembershipStatus();
		}
	}
	public boolean getIsMember() {
		return getMembershipStatus() == PROJECT_MEMBER;
	}
	public boolean getIsAdministrator() {
		return getMembershipStatus() == PROJECT_ADMINISTRATOR;
	}
	public boolean getCanEditDNA() {
		return getMembershipStatus() == PROJECT_MEMBER_EDIT_DNA;
	}
	public boolean getCanViewMolecular() {
		return getMembershipStatus() == PROJECT_MEMBER_VIEW_MOLECULAR;
	}
	public boolean getIsHelper() {
		return getMembershipStatus() == PROJECT_HELPER;
	}
}
