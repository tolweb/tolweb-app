package org.tolweb.btol.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.Project;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;

public interface ProjectDAO {
	/**
	 * Returns a list of object arrays that contain project ids and names 
	 * that this contributor is a part of
	 * @param contr
	 * @return
	 */
	public List getProjectsForContributor(Contributor contr);
	public AdditionalFields getAdditionalFieldsForNodeInProject(MappedNode node, Long projectId);
	public List getAdditionalFieldsForNodeIdsInProject(Collection nodeIds, Long projectId);
	public void createFieldsForNodeIdsInProject(Collection nodeIds, Long projectId);
	public Project getProjectWithId(Long id);
	public List<Integer> getContributorIdsForProject(Long projectId);
	public List<Integer> getContributorIdsForProject(Long projectId, Integer membershipStatus);
	public void saveProject(Project project);
	public Project getBtolProject();
	public Long getProjectIdWithDomain(String domain);
	public String getProjectNameWithDomain(String domainName);
	public Project getProjectWithDomain(String domain);
	public void reattachAdditionalFields(Long additionalFieldsId, Long newNodeId, Long oldNodeId);
	public Set<Long> getNodeIdsWithRevelantTierData(Collection nodeIds);
}
