package org.tolweb.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.helpers.OtherGroupsHelper;

public class OtherGroupsHelperTest extends ApplicationContextTestAbstract {
	private OtherGroupsHelper otherGroupsHelper;
	private NodeDAO workingNodeDAO;
	private NodeDAO miscNodeDAO;
	private PageDAO workingPageDAO;
	private PageDAO pageDAO;
	
	public OtherGroupsHelperTest(String name) {
		super(name);
		otherGroupsHelper = (OtherGroupsHelper)context.getBean("otherGroupsHelper");
		workingNodeDAO = (NodeDAO)context.getBean("workingNodeDAO");
		workingPageDAO = (PageDAO)context.getBean("workingPageDAO");
		pageDAO = workingPageDAO;
		miscNodeDAO = (NodeDAO)context.getBean("nodeDAO");
		Long rootNodeId = 119145L;
		MappedNode node = workingNodeDAO.getNodeWithId(rootNodeId);
		otherGroupsHelper = otherGroupsHelper.constructHelperForNode(node, workingPageDAO, rootNodeId);
		assertNotNull(otherGroupsHelper);
	}

	public void test() {
		List ancestors = otherGroupsHelper.getAncestorPages();
		List children = otherGroupsHelper.getChildPages();
		List siblings = otherGroupsHelper.getSiblingPages();
		
		boolean nextSib = otherGroupsHelper.getHasNextSibling();
		boolean prevSib = otherGroupsHelper.getHasPreviousSibling();
		System.out.println("next sibling: " + nextSib);
		System.out.println("previous sibling: " + prevSib);
	}
	
	/*
        Long pageIdNodeIsOn = pageDao.getPageIdNodeIsOn(node);
        Long pageIdForNode = pageDao.getPageIdForNode(node);		
		helper.setChildPages(pageDao.getChildPageNamesAndIds(pageIdForNode));
		helper.setSiblingPages(pageDao.getChildPageNamesAndIds(pageIdNodeIsOn));
	 */
	public void testDAOMethods() {
		Long rootNodeId = 119155L;
		MappedNode node = workingNodeDAO.getNodeWithId(rootNodeId);
        Long pageIdNodeIsOn = workingPageDAO.getPageIdNodeIsOn(node);
        Long pageIdForNode = workingPageDAO.getPageIdForNode(node);		
        workingPageDAO.getChildPageNamesAndIds(pageIdForNode);
        workingPageDAO.getChildPageNamesAndIds(pageIdNodeIsOn);
	}
	
	public void testContainingGroupCode() {
		List ids = new ArrayList(Arrays.asList(new Object[] {119155L, 119145L}));
		for (Iterator itr = ids.iterator(); itr.hasNext(); ) {
			Long rootNodeId = (Long)itr.next();
			MappedNode node = workingNodeDAO.getNodeWithId(rootNodeId);		
			Long ccgrpId = NodeHelper.findClosestContainingGroupWithPage(node.getNodeId(), miscNodeDAO, pageDAO);
			System.out.println("grp-id: " + ccgrpId);
		}
	}
}
