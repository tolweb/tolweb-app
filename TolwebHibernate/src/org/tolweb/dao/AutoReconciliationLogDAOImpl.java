package org.tolweb.dao;

import java.util.Date;
import java.util.List;

import org.tolweb.hibernate.AutoReconciliationRecord;

public class AutoReconciliationLogDAOImpl extends BaseDAOImpl implements AutoReconciliationLogDAO {
	public AutoReconciliationRecord getAutoReconciliationRecordWithId(Long id) {
        try {
            return (AutoReconciliationRecord) getHibernateTemplate().load(org.tolweb.hibernate.AutoReconciliationRecord.class, id);
        } catch (Exception e) {
            return null;
        }
	}
	
	public List<AutoReconciliationRecord> getLatestAutoReconciliationRecordsWithId(Long taxaImportRecordId) {
		try {
			String queryString = "from org.tolweb.hibernate.AutoReconciliationRecord r where r.taxaImportId=? order by r.timestamp";
			Object[] args = new Object[1];
			args[0] = taxaImportRecordId;
			List results = getHibernateTemplate().find(queryString, args);
			return (List<AutoReconciliationRecord>)results;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}	
	}

	public List<AutoReconciliationRecord> getLatestMatchedAutoReconciliationRecordsWithId(Long taxaImportRecordId) {
		try {
			String queryString = "from org.tolweb.hibernate.AutoReconciliationRecord r where r.taxaImportId=? and r.reconcileType=? order by r.timestamp";
			Object[] args = new Object[2];
			args[0] = taxaImportRecordId;
			args[1] = AutoReconciliationRecord.MATCHED_NODE;
			List results = getHibernateTemplate().find(queryString, args);
			return (List<AutoReconciliationRecord>)results;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}
	}

	public List<AutoReconciliationRecord> getLatestNewAutoReconciliationRecordsWithId(Long taxaImportRecordId) {
		try {
			String queryString = "from org.tolweb.hibernate.AutoReconciliationRecord r where r.taxaImportId=? and r.reconcileType=? order by r.timestamp";
			Object[] args = new Object[2];
			args[0] = taxaImportRecordId;
			args[1] = AutoReconciliationRecord.NEW_NODE;
			List results = getHibernateTemplate().find(queryString, args);
			return (List<AutoReconciliationRecord>)results;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}
	}	
	
	public void createAutoReconciliationRecord(AutoReconciliationRecord record) {
		Date now = new Date();
		record.setTimestamp(now);
		getHibernateTemplate().save(record);
	}
	
	public void saveAutoReconciliationRecord(AutoReconciliationRecord record) {
		Date now = new Date();
		record.setTimestamp(now);		
		getHibernateTemplate().update(record);
	}
	
	public List<AutoReconciliationRecord> getAllAutoReconciliationRecords() {
		return getAllAutoReconciliationRecords(false);
	}
	
	public List<AutoReconciliationRecord> getAllAutoReconciliationRecords(boolean descOrderBy) {
		String hql = "from org.tolweb.hibernate.AutoReconciliationRecord";
		if (descOrderBy) {
			hql += " order by id desc";
		}
		return (List<AutoReconciliationRecord>)getHibernateTemplate().find(hql);
	}
}
