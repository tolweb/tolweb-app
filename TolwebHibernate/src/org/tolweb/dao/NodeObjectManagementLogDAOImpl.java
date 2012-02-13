package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.NodeObjectManagementRecord;

public class NodeObjectManagementLogDAOImpl extends BaseDAOImpl implements NodeObjectManagementLogDAO {
	public NodeObjectManagementRecord getRecordWithId(Long id) {
        try {
            return (NodeObjectManagementRecord) getHibernateTemplate().load(org.tolweb.hibernate.NodeObjectManagementRecord.class, id);
        } catch (Exception e) {
            return null;
        }
	}
	
	public List<NodeObjectManagementRecord> getRecordsWithNodeId(Long nodeId) {
		try {
			String queryString = "from org.tolweb.hibernate.NodeObjectManagementRecord r where r.sourceNodeId=? or r.destNodeId=? order by r.timestamp desc";
			List results = getHibernateTemplate().find(queryString, new Object[] { nodeId, nodeId });
			return (List<NodeObjectManagementRecord>)results;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void create(NodeObjectManagementRecord record) {
		getHibernateTemplate().save(record);
	}
	
	public void save(NodeObjectManagementRecord record) {
		getHibernateTemplate().update(record);
	}
	
	public List<NodeObjectManagementRecord> getAllRecords() {
		return getAllRecords(false);
	}
	
	public List<NodeObjectManagementRecord> getAllRecords(boolean descOrderBy) {
		String hql = "from org.tolweb.hibernate.NodeObjectManagementRecord";
		if (descOrderBy) {
			hql += " order by id desc";
		}	
		return (List<NodeObjectManagementRecord>)getHibernateTemplate().find(hql);
	}
}
