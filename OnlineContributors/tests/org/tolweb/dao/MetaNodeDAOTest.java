package org.tolweb.dao;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.MetaNode;

public class MetaNodeDAOTest extends ApplicationContextTestAbstract {
	private NodeDAO workingNodeDAO;
	private MetaNodeDAO metaNodeDAO;
	private PageDAO pageDAO;
	
	public MetaNodeDAOTest(String name) {
		super(name);
		metaNodeDAO = (MetaNodeDAO)context.getBean("workingMetaNodeDAO");
		workingNodeDAO = (NodeDAO)context.getBean("workingNodeDAO");
		pageDAO = (PageDAO)context.getBean("workingPageDAO");
	}
	
	//9164
	public void testMetaNodeLoad() {
		MappedNode nd = workingNodeDAO.getNodeWithId(119410L, true);
		MetaNode mnode = metaNodeDAO.getMetaNode(nd);
		System.out.println(mnode.getOtherNameIds());
		for (Long othernameId : mnode.getOtherNameIds().keySet()) {
			System.out.println("id: " + othernameId);
		}
		MappedPage mpage = pageDAO.getPageForNode(nd);
		assertNotNull(mpage);
		assertNotNull(mnode.getPage());
	}
}
