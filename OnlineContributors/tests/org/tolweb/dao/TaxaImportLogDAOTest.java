package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.TaxaImportRecord;

public class TaxaImportLogDAOTest extends ApplicationContextTestAbstract {
	private TaxaImportLogDAO dao;
	
	public TaxaImportLogDAOTest(String name) {
		super(name);
		dao = (TaxaImportLogDAO)context.getBean("taxaImportLogDAO");
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
	
	public void testLatestFetch() {
		List<Object[]> records = dao.getLatestTaxaImportRecords();
		assertNotNull(records);
		Object[] record = records.get(0);
		assertNotNull(record);
	}
	
	public Long create() {
		TaxaImportRecord record = new TaxaImportRecord();
		record.setBasalNodeId(650L);
		record.setIngest("<tree-of-life-web/>");
		record.setReconcileWithPrevious(true);
		record.setPreserveNodeName(false);
		record.setPreserveNodeProperties(false);
		record.setUploadedBy("dmandel@tolweb.org");
		dao.createTaxaImportRecord(record);
		return record.getId();
	}
	
	public void fetch() {
		TaxaImportRecord record = dao.getTaxaImportRecordWithId(1L);
		assertNotNull(record);
	}
	
	public void update(Long id) { 
		TaxaImportRecord og = dao.getTaxaImportRecordWithId(id);
		TaxaImportRecord mod = dao.getTaxaImportRecordWithId(id);
		assertNotNull(og);
		assertNotNull(mod);
		
		mod.setUploadedBy("lenards@tolweb.org");
		mod.setPreserveNodeName(true);
		dao.saveTaxaImportRecord(mod);
		
		TaxaImportRecord updated = dao.getTaxaImportRecordWithId(id);
		assertNotNull(updated);
		assertFalse(og.getUploadedBy().equals(updated.getUploadedBy()));
		assertFalse(og.isPreserveNodeName() == updated.isPreserveNodeName());
		assertNotSame(og.getTimestamp(), updated.getTimestamp());
	}
	
	public void getAll() {
		List<TaxaImportRecord> records = dao.getAllTaxaImportRecords();
		assertNotNull(records);
		assertTrue(!records.isEmpty());
		assertNotNull(records.get(0));
	}
}
