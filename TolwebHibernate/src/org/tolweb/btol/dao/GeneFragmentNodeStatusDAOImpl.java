package org.tolweb.btol.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.GeneFragmentNodeStatus;
import org.tolweb.btol.util.GeneFragmentNodeStatusLookup;
import org.tolweb.treegrow.main.StringUtils;

public class GeneFragmentNodeStatusDAOImpl extends ProjectAssociatedDAOImpl implements GeneFragmentNodeStatusDAO {
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
	public GeneFragmentNodeStatus getStatusForGeneFragmentAndNodeIdInProject(GeneFragment gene,
			Long nodeId, Long projectId) {

		return (GeneFragmentNodeStatus) getFirstObjectFromQuery(getStatusSelectPrefix(projectId) + " and status.geneFragment=? and status.nodeId=" + nodeId, gene);
	}

	public List getStatusesForNodeIdsInProject(Collection nodeIds,
			Long projectId) {
		String hqlString = getStatusSelectPrefix(projectId) + " and status.nodeId " + StringUtils.returnSqlCollectionString(nodeIds);
		return getHibernateTemplate().find(hqlString);
	}

	public GeneFragmentNodeStatus createStatusForGeneFragmentAndNodeIdInProject(GeneFragment gene, Long nodeId, Long projectId) {
		GeneFragmentNodeStatus status = new GeneFragmentNodeStatus();
		status.setSource(GeneFragmentNodeStatus.NONE_SELECTED);
		status.setGeneFragment(gene);
		status.setNodeId(nodeId);
		doProjectAssociatedSave(status, projectId, getJoinTableName());
		return status;
	}
	
	public void removeStatus(GeneFragmentNodeStatus status) {
		deleteObject(status);
	}
	
	public List<GeneFragmentNodeStatus> assignInitialStatusForNodeIdsAndGenesInProject(Collection<Long> nodeIds, Collection<GeneFragment> genes, 
			int initialStatus, boolean overwriteExisting, Long projectId) {
		GeneFragmentNodeStatusLookup lookup = new GeneFragmentNodeStatusLookup();
		List<GeneFragmentNodeStatus> statuses = getStatusesForNodeIdsInProject(nodeIds, projectId);
		lookup.setStatuses(statuses);		
		List<GeneFragmentNodeStatus> statusesToSave = new ArrayList<GeneFragmentNodeStatus>();
		for (GeneFragment gene : genes) {
			for (Long nodeId : nodeIds) {
				GeneFragmentNodeStatus status = lookup.getStatusForNodeIdAndGeneFragment(nodeId, gene);
				if (status == null || overwriteExisting) {
					if (status == null) {
						status = new GeneFragmentNodeStatus();
						status.setGeneFragment(gene);
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
	public void updateStatusNotesForStatusWithId(Long statusId, String newStatusNotes) {
		doStatusUpdate(statusId, "statusNotes", newStatusNotes);
	}
	private void doStatusUpdate(Long statusId, String propertyName, Object value) {
		Hashtable<String, Object> args = new Hashtable<String, Object>();
		args.put("id", statusId);		
		args.put(propertyName, value);
		executeUpdateQuery("update org.tolweb.btol.GeneFragmentNodeStatus set " + propertyName + "=:" + propertyName + " where id=:id", args);
	}
	
	public Set<Long> getNodeIdsWithStatuses(Collection nodeIds) {
		Set<Long> resultNodeIds = new HashSet<Long>();
		ResultSet results; 
		Session session = null;
		try {
			session = getSession();
			Statement stmt = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT GF.nodeId as `nodeId` FROM GeneFragmentNodeStatuses GF WHERE GF.nodeId " + 
							StringUtils.returnSqlCollectionString(nodeIds) + " GROUP BY GF.nodeId";
			results = stmt.executeQuery(sql);
			while(results.next()) {
				resultNodeIds.add(Long.valueOf(results.getLong(1)));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultNodeIds;
	}
	
	public void reattachStatus(Long statusId, Long newNodeId, Long oldNodeId) {
		Object[] args = new Object[] {newNodeId, statusId, oldNodeId};
		String fmt = "UPDATE GeneFragmentNodeStatuses SET nodeId = %1$d WHERE id = %2$d and nodeId = %3$d";
		executeRawSQLUpdate(String.format(fmt, args));		
	}
}
