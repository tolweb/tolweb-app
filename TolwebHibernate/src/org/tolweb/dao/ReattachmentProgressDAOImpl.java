package org.tolweb.dao;

import java.util.Date;
import java.util.List;

import org.tolweb.hibernate.ReattachmentProgressRecord;

public class ReattachmentProgressDAOImpl extends BaseDAOImpl implements ReattachmentProgressDAO {

	public ReattachmentProgressRecord getReattachmentProgressWithId(Long id) {
        try {
            return (ReattachmentProgressRecord) getHibernateTemplate().load(org.tolweb.hibernate.ReattachmentProgressRecord.class, id);
        } catch (Exception e) {
            return null;
        }		
	}
	
	public ReattachmentProgressRecord getReattachmentProgress(String key) {
		try {
			String queryString = "from org.tolweb.hibernate.ReattachmentProgressRecord r where r.keyValue=?";
			Object[] args = new Object[1];
			args[0] = key;
			List results = getHibernateTemplate().find(queryString, args);
			if (results != null) {
				return (ReattachmentProgressRecord)results.get(0);
			} 
			return null;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}		
	}
	
	public void createReattachmentProgressRecord(ReattachmentProgressRecord record) {
		Date now = new Date();
		record.setTimestamp(now);
		getHibernateTemplate().save(record);
	}
	
	public void saveReattachmentProgressRecord(ReattachmentProgressRecord record) {
		Date now = new Date();
		record.setTimestamp(now);
		getHibernateTemplate().saveOrUpdate(record);
	}	
}
