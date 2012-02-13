package org.tolweb.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.tolweb.hibernate.TaxaImportRecord;

public class TaxaImportLogDAOImpl extends BaseDAOImpl implements
		TaxaImportLogDAO {
	private static final int MAX_FETCH_SIZE = 25; 
	
	public void createTaxaImportRecord(TaxaImportRecord record) {
		Date now = new Date();
		record.setTimestamp(now);
		getHibernateTemplate().save(record);
	}

	public List<TaxaImportRecord> getAllTaxaImportRecords() {
		return getAllTaxaImportRecords(false);
	}
	public List<TaxaImportRecord> getAllTaxaImportRecords(boolean descendentOrderBy) {
		String hql = "from org.tolweb.hibernate.TaxaImportRecord";
		if (descendentOrderBy) {
			hql += " order by id desc";
		}
		return (List<TaxaImportRecord>)getHibernateTemplate().find(hql);
	}

	public TaxaImportRecord getTaxaImportRecordWithId(Long id) {
        try {
            return (TaxaImportRecord) getHibernateTemplate().load(org.tolweb.hibernate.TaxaImportRecord.class, id);
        } catch (Exception e) {
            return null;
        }
	}

	public List<Object[]> getLatestTaxaImportRecords() {
		String queryString = "select r.basalNodeId, r.uploadedBy, max(r.timestamp) " + 
							"from org.tolweb.hibernate.TaxaImportRecord as r " + 
							"group by r.basalNodeId order by r.timestamp desc";
		Query query = getSession().createQuery(queryString);
		query.setMaxResults(MAX_FETCH_SIZE);
		return query.list();
	}
	
	public TaxaImportRecord getLatestTaxaImportRecordWithNodeId(Long basalNodeId) {
		try {
			String queryString = "from org.tolweb.hibernate.TaxaImportRecord r where r.basalNodeId=? order by r.timestamp desc";
			Object[] args = new Object[1];
			args[0] = basalNodeId;
			List results = getHibernateTemplate().find(queryString, args);
			if (results != null) {
				return (TaxaImportRecord)results.get(0);
			} 
			return null;
		} catch (Exception e) {
			e.printStackTrace();
//			String s = e.toString();
			return null;
		}		
	}	
	
	public void saveTaxaImportRecord(TaxaImportRecord record) {
		Date now = new Date();
		record.setTimestamp(now);		
		getHibernateTemplate().update(record);
	}

}
