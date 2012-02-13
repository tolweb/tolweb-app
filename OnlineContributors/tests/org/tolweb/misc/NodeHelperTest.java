package org.tolweb.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;

public class NodeHelperTest extends ApplicationContextTestAbstract {
	private NodeDAO ndao;
	private Collection<Long> nodeIds;

	public NodeHelperTest(String name) {
		super(name);
		ndao = (NodeDAO)context.getBean("nodeDAO");
		nodeIds = new ArrayList<Long>();
	}

	public void testSingleNodeCase() {
		nodeIds.add(Long.valueOf(1L));
		List mnodes = ndao.getNodesWithIds(nodeIds);
		List<String> names = NodeHelper.getNodeNameList(mnodes);
		assertNotNull(names);
		assertTrue(!names.isEmpty());
		assertTrue(names.size() == 1);
		assertEquals("Life on Earth", names.get(0));
	}
	
	public void testEmptyCase() {
		List<String> names = NodeHelper.getNodeNameList(new ArrayList());
		assertNotNull(names);
		assertTrue(names.isEmpty());
		assertTrue(names.size() == 0);
	}
	
	public void testMultiNodeCase() {
		List<String> expected = new ArrayList<String>();
		expected.add("Bembidiina");
		expected.add("Philipis vicina");
		expected.add("Omma");
		expected.add("Marsupialia");
		
		nodeIds.add(Long.valueOf(9042L)); // Omma
		nodeIds.add(Long.valueOf(15994L)); // Marsupialia
		nodeIds.add(Long.valueOf(200L)); // Bembidiina
		nodeIds.add(Long.valueOf(650L)); // Philipis vicina or Philipis
		
		List mnodes = ndao.getNodesWithIds(nodeIds);
		List<String> names = NodeHelper.getNodeNameList(mnodes);
		
		assertNotNull(names);
		assertTrue(!names.isEmpty());
		assertTrue(names.size() == 4);
		assertTrue(names.contains("Omma"));
		assertTrue(names.contains("Marsupialia"));
		assertTrue(names.contains("Bembidiina"));
		assertTrue(names.contains("Philipis vicina"));
		System.out.println(names);
		System.out.println(expected);
		assertTrue(names.equals(expected));
	}
}
