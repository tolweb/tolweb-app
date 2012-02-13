package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.NodeRetirementRecord;

public class NodeRetirementLogDAOTest extends ApplicationContextTestAbstract {
	private NodeRetirementLogDAO dao; 
	
	public NodeRetirementLogDAOTest(String name) {
		super(name);
		dao = (NodeRetirementLogDAO)context.getBean("nodeRetirementLogDAO");
	}
	
	public void testConfig() {
		assertNotNull(dao);
	}
	
	public void test() { 
		Long id = create();
		fetch(id);
		getAll();
	}

	private void getAll() {
		List<NodeRetirementRecord> records = dao.getAllNodeRetirementRecords();
		assertNotNull(records);
		assertFalse(records.isEmpty());
		assertNotNull(records.get(0));
	}
	private void fetch(Long id) {
		NodeRetirementRecord record = dao.getNodeRetirementRecordWithId(id);
		assertNotNull(record);
	}

	private Long create() {
		NodeRetirementRecord record = new NodeRetirementRecord();
		record.setNodeId(666L);
		record.setNodeName("Beetlis Diabolica");
		record.setRetiredBy("dmandel@tolweb.org");
		record.setRetiredFromClade(650L);
		dao.createNodeRetirementRecord(record);
		return record.getId();
	}
}
