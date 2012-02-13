package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.BtolMetaNode;
import org.tolweb.misc.NodeHelper;

public class BtolMetaNodeDAOTest extends ApplicationContextTestAbstract {
	private static final Long BEETLES_NODE_ID = 8221L;
	
	private BtolMetaNodeDAO dao;
	private NodeDAO workingNodeDAO;
	private NodeDAO miscNodeDAO;
	
	public BtolMetaNodeDAOTest(String name) {
		super(name);
		dao = (BtolMetaNodeDAO)context.getBean("btolMetaNodeDAO");
		workingNodeDAO = (NodeDAO)context.getBean("workingNodeDAO");
		miscNodeDAO = (NodeDAO)context.getBean("nodeDAO");
	}
	
	public void testGeneralDataFetch() {
		BtolMetaNode node = dao.getMetaNodeForBtol(Long.valueOf(169));
		assertNotNull(node);
		assertNotNull(node.getNode());
		assertNotNull(node.getNode().getAdditionalFields());
		System.out.println(node.getNode().getAdditionalFields());
	}
	
	public void test() {
		MappedNode beetleNode = workingNodeDAO.getNodeWithId(BEETLES_NODE_ID);
		assertNotNull(beetleNode);
		List<Long> inactiveBeetleNodes = getInactiveBeetleNodes(beetleNode);
		System.out.println(inactiveBeetleNodes);
		assertNotNull(inactiveBeetleNodes);
		assertTrue(!inactiveBeetleNodes.isEmpty());
		List<BtolMetaNode> metaNodes = dao.getMetaNodesForBtolWithIds(inactiveBeetleNodes, true);
		assertNotNull(metaNodes);
		System.out.println(metaNodes);
/*
			List<MappedNode> inactiveBeetleNodes = NodeHelper.getInactiveNodesForClade(getBasalNode().getNodeId(), getMiscNodeDAO(), getWorkingNodeDAO());
			List<BtolMetaNode> metaNodes = getBtolMetaNodeDAO().getMetaNodesForBtol(inactiveBeetleNodes, true);
			List<BtolTuple> tuples = new ArrayList<BtolTuple>();
			for (BtolMetaNode meta : metaNodes) {
				BtolTuple tuple = new BtolTuple(meta);
				tuples.add(tuple);
			}

 */		
	}
	
	public List<Long> getInactiveBeetleNodes(MappedNode rootNode) {
		return NodeHelper.getInactiveNodeIdsForClade(rootNode.getNodeId(), miscNodeDAO, workingNodeDAO);
	}
}
