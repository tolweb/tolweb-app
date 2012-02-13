package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.AutoReconciliationRecord;

public class AutoReconciliationLogDAOTest extends ApplicationContextTestAbstract {
	private AutoReconciliationLogDAO dao;
	
	public AutoReconciliationLogDAOTest(String name) {
		super(name);
		dao = (AutoReconciliationLogDAO)context.getBean("autoRecLogDAO");
	}

	public void testConfigured() {
		assertNotNull(dao);
	}
	
	public void test() {
		Long id = create();
		fetch();
		update(id);
		getAll();
	}
	
	public Long create() {
		AutoReconciliationRecord record = new AutoReconciliationRecord();
		record.setTaxaImportId(1L);
		record.setCladeName("testValue");
		record.setNodeId(666L);
		dao.createAutoReconciliationRecord(record);
		assertNotNull(record);
		assertNotSame(-1L, record.getId());
		return record.getId(); 
	}
	
	public void fetch() {
		AutoReconciliationRecord record = dao.getAutoReconciliationRecordWithId(1L);
		assertNotNull(record);
	}
	
	public void update(Long id) {
		AutoReconciliationRecord arr = dao.getAutoReconciliationRecordWithId(id);
		assertNotNull(arr);
		AutoReconciliationRecord mod = dao.getAutoReconciliationRecordWithId(id);
		assertNotNull(mod);
		
		mod.setCladeName("bogusName");
		mod.setNodeId(650L);
		dao.saveAutoReconciliationRecord(mod);
		
		AutoReconciliationRecord updated = dao.getAutoReconciliationRecordWithId(id);
		assertNotNull(updated);
		assertFalse(arr.getCladeName().equals(updated.getCladeName()));
		assertFalse(arr.getNodeId().equals(updated.getNodeId()));
		assertNotSame(arr.getTimestamp(),updated.getTimestamp());
	}
	
	public void getAll() {
		List<AutoReconciliationRecord> records = dao.getAllAutoReconciliationRecords();
		assertNotNull(records);
		assertTrue(!records.isEmpty());
		assertNotNull(records.get(0));
	}
}
