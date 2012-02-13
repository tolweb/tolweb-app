package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.NodeObjectManagementRecord;

public class NodeObjectManagementLogDAOTest extends ApplicationContextTestAbstract {
	private NodeObjectManagementLogDAO dao;
	
	public NodeObjectManagementLogDAOTest(String name) {
		super(name);
		dao = (NodeObjectManagementLogDAO)context.getBean("nodeObjectManagementLogDAO");
	}

	public void testBeanConfigured() {
		assertNotNull(dao);
	}
	
	public void test() {
		Long id = create();
		
		assertNotNull(id);
		assert(id.longValue() > 0);
		
		List<NodeObjectManagementRecord> records = getAllRecords();
		fetch(records, id);
		updateAll();
		allRecordsDescending();
	}
	
	
	private void allRecordsDescending() {
		List<NodeObjectManagementRecord> records = dao.getAllRecords(true);
		assertNotNull(records);
		Long prevId = 0L;
		for (NodeObjectManagementRecord record : records) {
			Long currId = record.getId();
			System.out.println("curr id compared to prev id: " + currId.compareTo(prevId));
			assertTrue(currId.compareTo(prevId) > 0);
		}
	}

	private void updateAll() {
		List<NodeObjectManagementRecord> records = getAllRecords();
		for (NodeObjectManagementRecord record : records) {
			record.setModifiedBy("lenards@email.arizona.edu");
			dao.save(record);
		}
		records = getAllRecords();
		for (NodeObjectManagementRecord record : records) {
			assertEquals(record.getModifiedBy(), "lenards@email.arizona.edu");
		}
	}

	private void fetch(List<NodeObjectManagementRecord> records, Long id) {
		NodeObjectManagementRecord record = dao.getRecordWithId(id);
		assertNotNull(record);
		assertEquals(record.getModifiedBy(), "lenards@tolweb.org");
		assertEquals(record.getLogEntry(), "test input.... ");
		assertEquals(record.getSourceNodeId(), new Long(9042));
		assertEquals(record.getDestNodeId(), new Long(8875));
		
		assertNotNull(records);
		for (NodeObjectManagementRecord rec : records) {
			assertNotNull(rec.getSourceNodeId());
			assertNotNull(rec.getDestNodeId());
			assertNotNull(rec.getLogEntry());
			assertNotNull(rec.getModifiedBy());
			assertNotNull(rec.getTimestamp());
		}
		
		List<NodeObjectManagementRecord> recForNode = dao.getRecordsWithNodeId(9042L);
		assertNotNull(recForNode);
		assertTrue(recForNode.size() > 1);
	}

	public List<NodeObjectManagementRecord> getAllRecords() {
		List<NodeObjectManagementRecord> records = dao.getAllRecords();
		assertNotNull(records);
		assertTrue(!records.isEmpty());
		return records;
	}
	
	public Long create() {
		NodeObjectManagementRecord record = new NodeObjectManagementRecord();
		record.setSourceNodeId(9042L);
		record.setDestNodeId(8875L);
		record.setLogEntry("test input.... ");
		record.setModifiedBy("lenards@tolweb.org");
		dao.create(record);
		return record.getId();
	}
}
