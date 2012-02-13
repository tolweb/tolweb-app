package org.tolweb.dao;

public class BranchLeafScoreboardTest extends ApplicationContextTestAbstract {
	private PageDAO pageDao;
	//private NodeDAO miscNodeDao;
	
	public BranchLeafScoreboardTest(String name) {
		super(name);
		pageDao = (PageDAO)context.getBean("publicPageDAO");
		//miscNodeDao = (NodeDAO)context.getBean("nodeDAO");
	}
	
	public void testNumberOfNodes() {

	}
	
	public void testBranchPageDescendantsComplete() {
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(2720L), 2);
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(285L), 3);
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(2635L), 5);
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(2661L), 2);
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(2675L), 4);
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(2678L), 0);
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(2449L), 0);
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(2448L), 3);
		assertEquals(pageDao.getBranchPagesLeadToCompletePages(1286L), 7);
	}

	public void testBranchPageDescendantsUnderConstruction() {
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(2720L), 2);
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(285L), 0);
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(2635L), 2);
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(2661L), 22);
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(2675L), 14);
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(2678L), 2);
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(2449L), 1);
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(2448L), 2);
		assertEquals(pageDao.getBranchPagesLeadToUnderConstructionPages(1286L), 80);
	}

	public void testBranchPageDescendantsTemporary() {
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(2720L), 1);
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(285L), 1);
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(2635L), 12);
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(2661L), 2);
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(2675L), 0);
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(2678L), 2);
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(2449L), 0);
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(2448L), 0);
		assertEquals(pageDao.getBranchPagesLeadToTemporaryPages(1286L), 300);
	}
	
	public void testLeafPageDescendantsComplete() {
		assertEquals(pageDao.getLeafPagesLeadToCompletePages(2720L), 0);
		assertEquals(pageDao.getLeafPagesLeadToCompletePages(285L), 1);
		assertEquals(pageDao.getLeafPagesLeadToCompletePages(2635L), 0);
		assertEquals(pageDao.getLeafPagesLeadToCompletePages(2661L), 0);
		assertEquals(pageDao.getLeafPagesLeadToCompletePages(2675L), 0);
		assertEquals(pageDao.getLeafPagesLeadToCompletePages(2678L), 0);
		assertEquals(pageDao.getLeafPagesLeadToCompletePages(2449L), 10);
		assertEquals(pageDao.getLeafPagesLeadToCompletePages(2448L), 16);
	}

	public void testLeafPageDescendantsUnderConstruction() {
		assertEquals(pageDao.getLeafPagesLeadToUnderConstructionPages(2720L), 0);
		assertEquals(pageDao.getLeafPagesLeadToUnderConstructionPages(285L), 0);
		assertEquals(pageDao.getLeafPagesLeadToUnderConstructionPages(2635L), 0);
		assertEquals(pageDao.getLeafPagesLeadToUnderConstructionPages(2661L), 3);
		assertEquals(pageDao.getLeafPagesLeadToUnderConstructionPages(2675L), 0);
		assertEquals(pageDao.getLeafPagesLeadToUnderConstructionPages(2678L), 1);
		assertEquals(pageDao.getLeafPagesLeadToUnderConstructionPages(2449L), 1);
		assertEquals(pageDao.getLeafPagesLeadToUnderConstructionPages(2448L), 3);				
	}
	
	public void testLeafPageDescendantsTemporary() {
		assertEquals(pageDao.getLeafPagesLeadToTemporaryPages(2720L), 0);
		assertEquals(pageDao.getLeafPagesLeadToTemporaryPages(285L), 0);
		assertEquals(pageDao.getLeafPagesLeadToTemporaryPages(2635L), 2);
		assertEquals(pageDao.getLeafPagesLeadToTemporaryPages(2661L), 0);
		assertEquals(pageDao.getLeafPagesLeadToTemporaryPages(2675L), 0);
		assertEquals(pageDao.getLeafPagesLeadToTemporaryPages(2678L), 0);
		assertEquals(pageDao.getLeafPagesLeadToTemporaryPages(2449L), 0);
		assertEquals(pageDao.getLeafPagesLeadToTemporaryPages(2448L), 1);				
	}	
}
