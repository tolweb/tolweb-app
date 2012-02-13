package org.tolweb.btol.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneNodeStatus;
import org.tolweb.btol.util.GeneNodeStatusLookup;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @deprecated As of July 2007, replaced GeneFragmentNodeStatusDAOImpl 
 * @author dmandel
 */
public class GeneNodeStatusDAOImpl extends ProjectAssociatedDAOImpl implements
		GeneNodeStatusDAO {
	@Override
	public String getForeignKeyColumnName() {
		return "statusId";
	}

	@Override
	public String getJoinTableName() {
		return "StatusesToProjects";
	}
	protected String getStatusSelectPrefix(Long projectId) {
		return getSelectPrefix("status", "statusesSet", projectId);		
	}
	public GeneNodeStatus getStatusForGeneAndNodeIdInProject(Gene gene,
			Long nodeId, Long projectId) {
		return (GeneNodeStatus) getFirstObjectFromQuery(getStatusSelectPrefix(projectId) + " and status.gene=? and status.nodeId=" + nodeId, gene);
	}

	public List getStatusesForNodeIdsInProject(Collection nodeIds,
			Long projectId) {
		String hqlString = getStatusSelectPrefix(projectId) + " and status.nodeId " + StringUtils.returnSqlCollectionString(nodeIds);
		return getHibernateTemplate().find(hqlString);
	}

	public GeneNodeStatus createStatusForGeneAndNodeIdInProject(Gene gene, Long nodeId, Long projectId) {
		GeneNodeStatus status = new GeneNodeStatus();
		status.setSource(GeneNodeStatus.NONE_SELECTED);
		status.setGene(gene);
		status.setNodeId(nodeId);
		doProjectAssociatedSave(status, projectId, getJoinTableName());
		return status;
	}
	public List<GeneNodeStatus> assignInitialStatusForNodeIdsAndGenesInProject(Collection<Long> nodeIds, Collection<Gene> genes, 
			int initialStatus, boolean overwriteExisting, Long projectId) {
		GeneNodeStatusLookup lookup = new GeneNodeStatusLookup();
		List<GeneNodeStatus> statuses = getStatusesForNodeIdsInProject(nodeIds, projectId);
		lookup.setStatuses(statuses);		
		List<GeneNodeStatus> statusesToSave = new ArrayList<GeneNodeStatus>();
		for (Gene gene : genes) {
			for (Long nodeId : nodeIds) {
				GeneNodeStatus status = lookup.getStatusForNodeIdAndGene(nodeId, gene);
				if (status == null || overwriteExisting) {
					if (status == null) {
						status = new GeneNodeStatus();
						status.setGene(gene);
						status.setNodeId(nodeId);
					}
					status.setStatus(initialStatus);						
					statusesToSave.add(status);
				}
			}
		}
		doProjectAssociatedSave(statusesToSave, projectId, getJoinTableName());
		return statusesToSave;
	}
	public void updateSourceDbForStatusWithId(Long statusId, int newSourceDb) {
		doStatusUpdate(statusId, "source", newSourceDb);
	}
	public void updateSourceDbIdForStatusWithId(Long statusId, String newSourceDbId) {
		doStatusUpdate(statusId, "sourceDbId", newSourceDbId);		
	}
	public void updateStatusForStatusWithId(Long statusId, int newStatus) {
		doStatusUpdate(statusId, "status", newStatus);
	}
	private void doStatusUpdate(Long statusId, String propertyName, Object value) {
		Hashtable<String, Object> args = new Hashtable<String, Object>();
		args.put("id", statusId);		
		args.put(propertyName, value);
		executeUpdateQuery("update org.tolweb.btol.GeneNodeStatus set " + propertyName + "=:" + propertyName + " where id=:id", args);
	}


}
