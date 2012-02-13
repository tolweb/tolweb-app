package org.tolweb.btol.dao;

import java.util.Collection;
import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneNodeStatus;

/**
 * @deprecated As of July 2007, replaced by GeneFragmentNodeStatusDAO 
 * @author dmandel
 * 
 */
public interface GeneNodeStatusDAO {
	public GeneNodeStatus getStatusForGeneAndNodeIdInProject(Gene gene, Long nodeId, Long projectId);
	public List getStatusesForNodeIdsInProject(Collection nodeIds, Long projectId);
	public GeneNodeStatus createStatusForGeneAndNodeIdInProject(Gene gene, Long nodeId, Long projectId);
	public void updateStatusForStatusWithId(Long statusId, int newStatus);
	public void updateSourceDbForStatusWithId(Long statusId, int newSourceDb);
	public void updateSourceDbIdForStatusWithId(Long statusId, String newSourceDbId);
	public List<GeneNodeStatus> assignInitialStatusForNodeIdsAndGenesInProject(Collection<Long> nodeIds, Collection<Gene> genes, 
			int initialStatus, boolean overwriteExisting, Long projectId);
}
