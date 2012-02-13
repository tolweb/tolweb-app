package org.tolweb.btol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolweb.misc.ContributorNameComparator;
import org.tolweb.treegrow.main.Contributor;

/**
 * @hibernate.class table="Projects"
 * @author dmandel
 */
public class Project extends NamedObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8157567174286980399L;
	private static final int ALL_MEMBERSHIP_TYPES = 10000000;
	private Long rootNodeId;
	private Set contributors;
	private Set additionalFieldsSet;
	private Set workingGroups;
	private Set genesSet;
	private Set geneFragmentsSet;
	private Set primersSet;
	private Set protocolsSet;
	private Set extractionsSet;
	private Set specimensSet;
	private Set pcrBatchesSet;
	private Set statusesSet;
	private Set chromatogramBatchesSet;
	private String domain;
	
	/**
	 * @hibernate.set table="ProjectContributors" lazy="false"
	 * @hibernate.collection-composite-element class="org.tolweb.btol.ProjectContributor"
	 * @hibernate.collection-key column="projectId"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
	 * @return Returns the contributors
	 */	
	public Set getContributors() {
		if (contributors == null) {
			contributors = new HashSet();
		}
		return contributors;
	}
	public void setContributors(Set contributorIds) {
		this.contributors = contributorIds;
	}
	/**
	 * @hibernate.set table="ProjectWorkingGroups" lazy="false" cascade="all"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.WorkingGroup" column="workingGroupId"
	 * @hibernate.collection-cache usage="nonstrict-read-write" 
	 * @return
	 */
	public Set getWorkingGroups() {
		if (workingGroups == null) {
			workingGroups = new HashSet();
		}
		return workingGroups;
	}
	public void setWorkingGroups(Set workingGroups) {
		this.workingGroups = workingGroups;
	}
	/**
	 * Adds the contributor to this project and returns
	 * the new ProjectContributor record
	 * @param contr
	 * @return
	 */
	public ProjectContributor addContributorToProject(Contributor contr, int membershipStatus) {
		ProjectContributor contributor = new ProjectContributor();
		contributor.setActualContributor(contr);
		contributor.setMembershipStatus(membershipStatus);
		getContributors().add(contributor);
		return contributor;
	}
	public void removeContributorFromProject(Integer contributorId) {
		for (Iterator iter = new HashSet(getContributors()).iterator(); iter.hasNext();) {
			ProjectContributor contributor = (ProjectContributor) iter.next();
			if (contributor.getContributorId().equals(contributorId)) {
				getContributors().remove(contributor);
			}
		}
	}
	public List getCoreProjectMembers() {
		return returnFilteredContributorByMembershipStatus(ProjectContributor.PROJECT_MEMBER);
	}
	public List getAllProjectMembers() {
		return returnFilteredContributorByMembershipStatus(ProjectContributor.OTHER_AFFILIATE);		
	}
	private List returnFilteredContributorByMembershipStatus(int status) {
		List returnList = new ArrayList();
		for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
			ProjectContributor contributor = (ProjectContributor) iter.next();
			if (contributor.getMembershipStatus() <= status) {
				returnList.add(contributor.getActualContributor());
			}			
		}
		Collections.sort(returnList, new ContributorNameComparator());
		return returnList;		
	}
	public boolean getContributorCanViewAndEditMolecularData(Contributor contr) {
		boolean returnVal = getContributorIsAdministrator(contr);
		if (!returnVal) {
			for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
				ProjectContributor pContr = (ProjectContributor) iter.next();
				if (pContr.getContributorId() == contr.getId()) {
					returnVal = returnVal || pContr.getCanViewMolecular()
						|| pContr.getIsHelper();
				}
			}
		}
		return returnVal;		
	}
	/**
	 * A contributor can edit dna if they are a project administrator
	 * or if they have the 'can edit dna' privilege
	 * @param contr
	 * @return
	 */
	public boolean getContributorCanEditDna(Contributor contr) {
		boolean returnVal = getContributorIsAdministrator(contr);
		if (!returnVal) {
			for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
				ProjectContributor pContr = (ProjectContributor) iter.next();
				if (pContr.getContributorId() == contr.getId()) {
					returnVal = returnVal || pContr.getCanEditDNA();
				}
			}
		}
		return returnVal;
	}
	public boolean getContributorIsAdministrator(Contributor contr) {
		boolean returnVal = false;
		for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
			ProjectContributor pContr = (ProjectContributor) iter.next();
			if (pContr.getContributorId() == contr.getId()) {
				returnVal = returnVal || pContr.getIsAdministrator();
			}
		}
		return returnVal;
	}
	public boolean getContributorCanViewProject(Contributor contr) {
		boolean isMember = false;
		if (contr != null) {
			for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
				ProjectContributor pContr = (ProjectContributor) iter.next();
				if (pContr.getContributorId() == contr.getId() && (pContr.getIsAdministrator()
						|| pContr.getIsMember() || pContr.getIsHelper())) {
					isMember = true;
				}
			}
		}
		return isMember;
	}
	public void ensureContributorIsMember(Contributor contributorToEdit) {
		if (!getContributorCanViewProject(contributorToEdit)) {
			// not currently a member, so make sure they remove any
			// existing status and get added as a member
			removeContributorFromProject(contributorToEdit, ProjectContributor.OTHER_AFFILIATE);
			addContributorToProject(contributorToEdit, ProjectContributor.PROJECT_MEMBER);
		}
	}	
	private void removeContributorFromProject(Contributor contributorToEdit, int contributorType) {
		for (Iterator iter = new ArrayList(getContributors()).iterator(); iter.hasNext();) {
			ProjectContributor contr = (ProjectContributor) iter.next();
			if (contr.getActualContributor().getId() == contributorToEdit.getId() &&
					(contr.getMembershipStatus() == contributorType ||
							contributorType == ALL_MEMBERSHIP_TYPES)) {
				getContributors().remove(contr);
			}
		}
	}
	public void ensureContributorCanEditDna(Contributor contributorToEdit) {
		if (!getContributorCanEditDna(contributorToEdit)) {
			addContributorToProject(contributorToEdit, ProjectContributor.PROJECT_MEMBER_EDIT_DNA);
		}
	}
	public void ensureContributorCanViewMolecular(Contributor contributorToEdit) {
		if (!getContributorCanViewAndEditMolecularData(contributorToEdit)) {
			addContributorToProject(contributorToEdit, ProjectContributor.PROJECT_MEMBER_VIEW_MOLECULAR);
		}
	}
	public void ensureContributorIsntMember(Contributor contributorToEdit) {
		// remove all their current permissions
		removeContributorFromProject(contributorToEdit, ALL_MEMBERSHIP_TYPES);
		// then add them as an affiliate
		addContributorToProject(contributorToEdit, ProjectContributor.OTHER_AFFILIATE);
	}
	public void ensureContributorCantEditDna(Contributor contributorToEdit) {
		removeContributorFromProject(contributorToEdit, ProjectContributor.PROJECT_MEMBER_EDIT_DNA);
	}
	public void ensureContributorCantViewMolecular(Contributor contributorToEdit) {
		removeContributorFromProject(contributorToEdit, ProjectContributor.PROJECT_MEMBER_VIEW_MOLECULAR);
	}	
	/**
	 * @hibernate.property
	 * @return
	 */
	public Long getRootNodeId() {
		return rootNodeId;
	}
	public void setRootNodeId(Long rootNodeId) {
		this.rootNodeId = rootNodeId;
	}
	/**
     * @hibernate.set table="AdditionalFieldsToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.AdditionalFields" column="fieldsId"
     */	
	public Set getAdditionalFieldsSet() {
		if (additionalFieldsSet == null) {
			additionalFieldsSet = new HashSet();
		}
		return additionalFieldsSet;
	}
	public void setAdditionalFieldsSet(Set additionalFields) {
		this.additionalFieldsSet = additionalFields;
	}
	public boolean getInSameWorkingGroup(Contributor contr1, Contributor contr2) {
		if (contr1 != null && contr2 != null) {
			for (Iterator iter = getWorkingGroups().iterator(); iter.hasNext();) {
				WorkingGroup group = (WorkingGroup) iter.next();
				Set ids = group.getContributorIds();
				if (ids != null && ids.contains(contr1.getId()) && ids.contains(contr2.getId())) {
					return true;
				}
			}
		}
		return false;
	}
	/**
     * @hibernate.set table="GenesToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.Gene" column="geneId"
     */
	public Set getGenesSet() {
		return genesSet;
	}
	public void setGenesSet(Set genesSet) {
		this.genesSet = genesSet;
	}
	/**
     * @hibernate.set table="PrimersToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.Primer" column="primerId"
     */	
	public Set getPrimersSet() {
		return primersSet;
	}
	public void setPrimersSet(Set primersSet) {
		this.primersSet = primersSet;
	}
	/**
     * @hibernate.set table="ProtocolsToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.PCRProtocol" column="protocolId"
     */	
	public Set getProtocolsSet() {
		return protocolsSet;
	}
	public void setProtocolsSet(Set protocolsSet) {
		this.protocolsSet = protocolsSet;
	}
	/**
     * @hibernate.set table="ExtractionsToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.SpecimenExtraction" column="extractionId"
     */		
	public Set getExtractionsSet() {
		return extractionsSet;
	}
	public void setExtractionsSet(Set extractionsSet) {
		this.extractionsSet = extractionsSet;
	}
	/**
     * @hibernate.set table="BatchesToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.PCRBatch" column="batchId"
     */			
	public Set getPcrBatchesSet() {
		return pcrBatchesSet;
	}
	public void setPcrBatchesSet(Set pcrBatchesSet) {
		this.pcrBatchesSet = pcrBatchesSet;
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	/**
     * @hibernate.set table="StatusesToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.GeneFragmentNodeStatus" column="statusId"
     */	
	public Set getStatusesSet() {
		return statusesSet;
	}
	public void setStatusesSet(Set statusesSet) {
		this.statusesSet = statusesSet;
	}
	/**
     * @hibernate.set table="ChromatogramBatchesToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.ChromatogramBatch" column="chromatogramBatchId"
     */		
	public Set getChromatogramBatchesSet() {
		return chromatogramBatchesSet;
	}
	public void setChromatogramBatchesSet(Set abiBatchesSet) {
		this.chromatogramBatchesSet = abiBatchesSet;
	}
	/**
     * @hibernate.set table="SpecimensToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.Specimen" column="specimenId"
     */		
	public Set getSpecimensSet() {
		return specimensSet;
	}
	public void setSpecimensSet(Set specimensSet) {
		this.specimensSet = specimensSet;
	}
	/**
     * @hibernate.set table="GeneFragmentsToProjects" lazy="true"
     * @hibernate.collection-key column="projectId"
     * @hibernate.collection-many-to-many class="org.tolweb.btol.GeneFragment" column="geneFragmentId"
     */		
	public Set getGeneFragmentsSet() {
		return geneFragmentsSet;
	}
	public void setGeneFragmentsSet(Set geneFragmentsSet) {
		this.geneFragmentsSet = geneFragmentsSet;
	}
}