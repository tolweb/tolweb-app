package org.tolweb.tapestry.xml;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.misc.NodeHelper;

public class PageValidityCheckTest extends ApplicationContextTestAbstract {
	private NodeDAO nodeDAO;
	private PageDAO pageDAO;
	
	public PageValidityCheckTest(String name) {
		super(name);
		nodeDAO = (NodeDAO)context.getBean("nodeDAO");
		pageDAO = (PageDAO)context.getBean("workingPageDAO");
	}

	public void testScratch() {
		Long id = NodeHelper.findClosestContainingGroupWithPage(95274L, nodeDAO, pageDAO);
		id = NodeHelper.findClosestContainingGroupWithPage(119145L, nodeDAO, pageDAO);
		id = NodeHelper.findClosestContainingGroupWithPage(119146L, nodeDAO, pageDAO);
		id = NodeHelper.findClosestContainingGroupWithPage(119147L, nodeDAO, pageDAO);
		id = NodeHelper.findClosestContainingGroupWithPage(119148L, nodeDAO, pageDAO);
		id = NodeHelper.findClosestContainingGroupWithPage(119149L, nodeDAO, pageDAO);
	}
}
