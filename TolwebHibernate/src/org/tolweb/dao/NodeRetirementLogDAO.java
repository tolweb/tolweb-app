package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.NodeRetirementRecord;

public interface NodeRetirementLogDAO extends BaseDAO {
	public NodeRetirementRecord getNodeRetirementRecordWithId(Long id);
	public List<NodeRetirementRecord> getRetiredNodesForClade(Long basalNodeId);
	public List<NodeRetirementRecord> getAllNodeRetirementRecords();
	public void createNodeRetirementRecord(NodeRetirementRecord record);
}
