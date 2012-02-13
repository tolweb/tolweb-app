package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.ManualReconciliationRecord;


public interface ManualReconciliationLogDAO extends BaseDAO {
	public ManualReconciliationRecord getManualReconciliationRecordWithId(Long id);
	public List<ManualReconciliationRecord> getManualReconciliationRecordWithBasalNodeId(Long nodeId);
	public void createManualReconciliationRecord(ManualReconciliationRecord record);
	public void saveManualReconciliationRecord(ManualReconciliationRecord record);
	public List<ManualReconciliationRecord> getAllManualReconciliationRecords();
}
