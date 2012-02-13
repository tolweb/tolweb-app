package org.tolweb.btol.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.GeneFragmentNodeStatus;

/**
 * 
 * @author lenards
 */
public interface GeneFragmentNodeStatusDAO {
	public GeneFragmentNodeStatus getStatusForGeneFragmentAndNodeIdInProject(GeneFragment gene, Long nodeId, Long projectId);
	public List getStatusesForNodeIdsInProject(Collection nodeIds, Long projectId);
	public GeneFragmentNodeStatus createStatusForGeneFragmentAndNodeIdInProject(GeneFragment gene, Long nodeId, Long projectId);
	public void updateStatusForStatusWithId(Long statusId, int newStatus);
	public void updateSourceDbForStatusWithId(Long statusId, int newSourceDb);
	public void updateSourceDbIdForStatusWithId(Long statusId, String newSourceDbId);
	public void updateStatusNotesForStatusWithId(Long statusId, String newStatusNotes);
	public List<GeneFragmentNodeStatus> assignInitialStatusForNodeIdsAndGenesInProject(Collection<Long> nodeIds, Collection<GeneFragment> geneFragments, 
			int initialStatus, boolean overwriteExisting, Long projectId);
	public void removeStatus(GeneFragmentNodeStatus status);
	public void reattachStatus(Long statusId, Long newNodeId, Long oldNodeId);
	public Set<Long> getNodeIdsWithStatuses(Collection nodeIds);
}
