package org.tolweb.dao;

import java.util.Date;
import java.util.List;

import org.tolweb.hibernate.ManualReconciliationRecord;

public class ManualReconciliationLogDAOImpl extends BaseDAOImpl implements ManualReconciliationLogDAO {
	public ManualReconciliationRecord getManualReconciliationRecordWithId(Long id) {
        try {
            return (ManualReconciliationRecord) getHibernateTemplate().load(org.tolweb.hibernate.ManualReconciliationRecord.class, id);
        } catch (Exception e) {
            return null;
        }
	}
	
	public List<ManualReconciliationRecord> getManualReconciliationRecordWithBasalNodeId(Long nodeId) {
		try {
			String queryString = "from org.tolweb.hibernate.ManualReconciliationRecord r where r.basalNodeId=? order by r.timestamp desc";
			Object[] args = new Object[1];
			args[0] = nodeId;
			List results = getHibernateTemplate().find(queryString, args);
			return (List<ManualReconciliationRecord>)results;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}
	}
	
	public void createManualReconciliationRecord(ManualReconciliationRecord record) {
		Date now = new Date();
		record.setTimestamp(now);		
		getHibernateTemplate().save(record);
	}
	
	public void saveManualReconciliationRecord(ManualReconciliationRecord record) {
		Date now = new Date();
		record.setTimestamp(now);		
		getHibernateTemplate().update(record);
	}
	
	public List<ManualReconciliationRecord> getAllManualReconciliationRecords() {
		String hql = "from org.tolweb.hibernate.ManualReconciliationRecord";
		return (List<ManualReconciliationRecord>)getHibernateTemplate().find(hql);
	}
}
