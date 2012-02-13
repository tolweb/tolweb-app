package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.NodeObjectManagementRecord;

public interface NodeObjectManagementLogDAO extends BaseDAO {
	public NodeObjectManagementRecord getRecordWithId(Long id);
	public List<NodeObjectManagementRecord> getRecordsWithNodeId(Long nodeId);
	public void create(NodeObjectManagementRecord record);
	public void save(NodeObjectManagementRecord record);
	public List<NodeObjectManagementRecord> getAllRecords();
	public List<NodeObjectManagementRecord> getAllRecords(boolean descOrderBy);
}
