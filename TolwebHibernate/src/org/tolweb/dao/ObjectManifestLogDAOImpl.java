package org.tolweb.dao;

import java.util.List;
import java.util.Date;

import org.tolweb.hibernate.ObjectManifestRecord;

public class ObjectManifestLogDAOImpl extends BaseDAOImpl implements ObjectManifestLogDAO {
	public ObjectManifestRecord getObjectManifestRecordWithId(Long id) {
        try {
            return (ObjectManifestRecord) getHibernateTemplate().load(org.tolweb.hibernate.ObjectManifestRecord.class, id);
        } catch (Exception e) {
            return null;
        }
	}
	
	public ObjectManifestRecord getLatestObjectManifestRecordWithNodeId(Long basalNodeId) {
		try {
			String queryString = "from org.tolweb.hibernate.ObjectManifestRecord r where r.basalNodeId=? order by r.timestamp";
			Object[] args = new Object[1];
			args[0] = basalNodeId;
			List results = getHibernateTemplate().find(queryString, args);
			if (results != null) {
				return (ObjectManifestRecord)results.get(0);
			} 
			return null;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}		
	}
	
	public ObjectManifestRecord getObjectManifestRecord(String key) {
		try {
			String queryString = "from org.tolweb.hibernate.ObjectManifestRecord r where r.keyValue=?";
			Object[] args = new Object[1];
			args[0] = key;
			List results = getHibernateTemplate().find(queryString, args);
			if (results != null) {
				return (ObjectManifestRecord)results.get(0);
			} 
			return null;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}	
	}
	
	public void createObjectManifestRecord(ObjectManifestRecord record) {
		Date now = new Date();
		record.setTimestamp(now);
		getHibernateTemplate().save(record);
	}
	
	public void saveObjectManifestRecord(ObjectManifestRecord record) {
		Date now = new Date();
		record.setTimestamp(now);		
		getHibernateTemplate().update(record);
	}

	public List<ObjectManifestRecord> getAllObjectManifestRecords() {
		return (List<ObjectManifestRecord>)getHibernateTemplate().find("from org.tolweb.hibernate.ObjectManifestRecord");
	}	
}
