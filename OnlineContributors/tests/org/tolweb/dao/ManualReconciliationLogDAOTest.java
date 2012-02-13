package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.ManualReconciliationRecord;

public class ManualReconciliationLogDAOTest extends	ApplicationContextTestAbstract {
	private ManualReconciliationLogDAO dao;
	
	public ManualReconciliationLogDAOTest(String name) {
		super(name);
		dao = (ManualReconciliationLogDAO)context.getBean("manualRecLogDAO");
	}
	
	public void testConfig() {
		assertNotNull(dao);
	}
	
	public void test() {
		Long id = create();
		fetch();
		update(id);
		getAll();
		getRecordsForBasalNode();
	}

	
	private void getRecordsForBasalNode() {
		List<ManualReconciliationRecord> recordsForNode = dao.getManualReconciliationRecordWithBasalNodeId(650L);
		assertNotNull(recordsForNode);
		assertFalse(recordsForNode.isEmpty());
		assertNotNull(recordsForNode.get(0));
	}

	private void getAll() {
		List<ManualReconciliationRecord> records = dao.getAllManualReconciliationRecords();
		assertNotNull(records);
		assertFalse(records.isEmpty());
		assertNotNull(records.get(0));
	}

	private void update(Long id) {
		ManualReconciliationRecord mrr = dao.getManualReconciliationRecordWithId(id);
		ManualReconciliationRecord mod = dao.getManualReconciliationRecordWithId(id);
		assertNotNull(mrr);
		assertNotNull(mod);
		
		mod.setBasalNodeId(650L);
		mod.setSourceNodeManifest(".....");
		mod.setTargetNodeManifest("...............");
		
		dao.saveManualReconciliationRecord(mod);
		
		ManualReconciliationRecord update = dao.getManualReconciliationRecordWithId(id);
		assertNotNull(update);
		assertNotSame(mrr.getBasalNodeId(), update.getBasalNodeId());
		assertNotSame(mrr.getSourceNodeManifest(), update.getSourceNodeManifest());
		assertNotSame(mrr.getTargetNodeManifest(), update.getTargetNodeManifest());
		assertNotSame(mrr.getTimestamp(), update.getTimestamp());
	}

	private void fetch() {
		ManualReconciliationRecord record = dao.getManualReconciliationRecordWithId(1L);
		assertNotNull(record);
	}

	private Long create() {
		ManualReconciliationRecord record = new ManualReconciliationRecord();
		record.setBasalNodeId(16246L);
		record.setSourceNodeId(119257L);
		record.setSourceNodeName("blahblah");
		record.setSourceNodeManifest("");
		record.setTargetNodeId(120001L);
		record.setTargetNodeName("neoblahblah");
		record.setTargetNodeManifest("");
		record.setResolvedBy("lenards@tolweb.org");
		dao.createManualReconciliationRecord(record);
		return record.getId();
	}
	
	
}
