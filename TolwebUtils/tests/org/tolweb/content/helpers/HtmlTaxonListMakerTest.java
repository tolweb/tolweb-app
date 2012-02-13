package org.tolweb.content.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.MappedNode;

import junit.framework.TestCase;

public class HtmlTaxonListMakerTest extends TestCase {
	private HtmlTaxonListMaker listMaker;
	private List<MappedNode> nodes; 
	private MappedNode basalNode;
	
	public void setUp() {
		basalNode = new MappedNode();
		nodes = new ArrayList<MappedNode>();
		basalNode.setNodeId(new Long(650));
		basalNode.setName("Wombats");
		addNodesNTimes(nodes, basalNode, 3);
		listMaker = new HtmlTaxonListMaker(basalNode, nodes);
	}

	public void test_taxon_list_create() {
		String htmlList = listMaker.createList();
		assertNotNull(htmlList);
		assertTrue(htmlList.length() > 0);
		System.out.println(htmlList);
	}

	public void test_taxon_list_create_subLists() {
		addNodesNTimes(nodes, nodes.get(0), 2);
		addNodesNTimes(nodes, nodes.get(1), 3);
		addNodesNTimes(nodes, nodes.get(2), 1);
		addNodesNTimes(nodes, nodes.get(nodes.size()-1), 2);
		listMaker = new HtmlTaxonListMaker(basalNode, nodes);
		String htmlList = listMaker.createList();
		assertNotNull(htmlList);
		assertTrue(htmlList.length() > 0);
		System.out.println(htmlList);		
	}
	
	public void test_extinct_dagger_shown() {
		basalNode.setExtinct(MappedNode.EXTINCT);
		listMaker = new HtmlTaxonListMaker(basalNode, nodes);
		String htmlList = listMaker.createList();
		assertNotNull(htmlList);
		assertTrue(htmlList.length() > 0);
		System.out.println(htmlList);
		assertTrue(htmlList.contains("&dagger;"));
	}
	
	public void tearDown() {
		listMaker = null;
		basalNode = null;
		nodes = null;
	}
	
	private void addNodesNTimes(List<MappedNode> nodes, MappedNode basalNode, int times) {
		for (int i = 0; i < times; i++) {
			MappedNode nd = new MappedNode();
			nd.setName(basalNode.getName() + " " + i);
			nd.setNodeId(new Long(basalNode.getNodeId().intValue() + i + nodes.size() + 1));
			nd.setParentNodeId(basalNode.getNodeId());
			nd.setParent(basalNode);
			nodes.add(nd);
		}
	}	
}
