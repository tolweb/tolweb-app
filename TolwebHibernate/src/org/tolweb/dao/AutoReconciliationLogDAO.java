package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.AutoReconciliationRecord;

public interface AutoReconciliationLogDAO extends BaseDAO {
	public AutoReconciliationRecord getAutoReconciliationRecordWithId(Long id);
	public List<AutoReconciliationRecord> getLatestAutoReconciliationRecordsWithId(Long taxaImportRecordId);
	public List<AutoReconciliationRecord> getLatestMatchedAutoReconciliationRecordsWithId(Long taxaImportRecordId);
	public List<AutoReconciliationRecord> getLatestNewAutoReconciliationRecordsWithId(Long taxaImportRecordId);
	public void createAutoReconciliationRecord(AutoReconciliationRecord record);
	public void saveAutoReconciliationRecord(AutoReconciliationRecord record);
	public List<AutoReconciliationRecord> getAllAutoReconciliationRecords();
	public List<AutoReconciliationRecord> getAllAutoReconciliationRecords(boolean descOrderBy);
}
