package org.tolweb.dao;

import java.util.Date;
import java.util.List;

import org.tolweb.hibernate.NodeRetirementRecord;

public class NodeRetirementLogDAOImpl extends BaseDAOImpl implements NodeRetirementLogDAO {
	public NodeRetirementRecord getNodeRetirementRecordWithId(Long id) {
        try {
            return (NodeRetirementRecord) getHibernateTemplate().load(org.tolweb.hibernate.NodeRetirementRecord.class, id);
        } catch (Exception e) {
            return null;
        }
	}
	
	public List<NodeRetirementRecord> getRetiredNodesForClade(Long basalNodeId) {
		try {
			String queryString = "from org.tolweb.hibernate.NodeRetirementRecord r where r.retiredFromClade=? order by r.timestamp desc";
			Object[] args = new Object[1];
			args[0] = basalNodeId;
			List results = getHibernateTemplate().find(queryString, args);
			return (List<NodeRetirementRecord>)results;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}
	}
	
	public List<NodeRetirementRecord> getAllNodeRetirementRecords() {
		String hql = "from org.tolweb.hibernate.NodeRetirementRecord";
		return (List<NodeRetirementRecord>)getHibernateTemplate().find(hql);
	}
	
	public void createNodeRetirementRecord(NodeRetirementRecord record) {
		Date now = new Date();
		record.setTimestamp(now);
		getHibernateTemplate().save(record);
	}
}
