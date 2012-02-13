package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tolweb.hibernate.MappedNode;

public class NodeDAOFilteredNodeDAOComparisonTest extends ApplicationContextTestAbstract {
	private NodeDAO fdao; 
	private NodeDAO ndao; 
	
	public NodeDAOFilteredNodeDAOComparisonTest(String name) {
		super(name);
		fdao = (NodeDAO)context.getBean("workingFilteredNodeDAO");
		ndao = (NodeDAO)context.getBean("workingNodeDAO");
	}
	
	public void testGetNodeWithId() {
//		Long[] ids = new Long[] {new Long(1), new Long(2), new Long(8221), 
//				new Long(9042), new Long(8870), new Long(8875),  
//				new Long(15721), new Long(2536), new Long(15994) };
		Long[] ids = new Long[] {new Long(9042)};
		for (Long id : ids) {
			compareNodeWithId(id);
		}
	}

	public void compareNodeWithId(Long id) {
		MappedNode nnd = ndao.getNodeWithId(id);
		MappedNode fnd = fdao.getNodeWithId(id);
		assertEquals(nnd, fnd);
	}
	
	public void testGetChildrenNodes() {
		MappedNode nbeetles = ndao.getNodeWithId(new Long(8221));
		MappedNode fbeetles = fdao.getNodeWithId(new Long(8221));
		assertEquals(nbeetles, fbeetles);
		
		List nlist = ndao.getChildrenNodes(nbeetles);
		System.out.println("node-dao: " + nlist);
		List flist = fdao.getChildrenNodes(nbeetles);
		System.out.println("filtered: " + flist);
		assertEquals(nlist, flist);
		
		MappedNode nomma = ndao.getNodeWithId(new Long(9042));
		MappedNode fomma = fdao.getNodeWithId(new Long(9042));
		assertEquals(nomma, fomma);
		compareChildFetchResults(nomma);
	}
	
	public void compareChildFetchResults(MappedNode nd) {
		List nlist = ndao.getChildrenNodes(nd);
		List flist = fdao.getChildrenNodes(nd);
		assertEquals(nlist, flist);
	}
	
	
	public void testDoNodeNameSearches() {
		compareNodeNameSearchResults("Beetles");
		compareNodeNameSearchResults("Spiders");
		compareNodeNameSearchResults("birds");		
		compareNodeNameSearchResults("Omma");
	}
	
	public void compareNodeNameSearchResults(String group) {
		List nl = ndao.findNodesNamed(group);
		List fl = fdao.findNodesNamed(group);
		assertEquals(nl, fl);
	}
	
	public void testNumChildren() {
		compareResults(new Long(8221));
		compareResults(new Long(1));
		compareResults(new Long(2));
		compareResults(new Long(9042));
	}
	
	private void compareResults(Long id) {
		Integer nnum = ndao.getNumChildrenForNodeId(id);
		Integer fnum = fdao.getNumChildrenForNodeId(id);
		System.out.print("id #" + id + " n: " + nnum + " f: " + fnum);
		assertEquals(nnum, fnum);
		nnum = ndao.getNumChildrenForNodeId(id, true);
		fnum = fdao.getNumChildrenForNodeId(id, true);
		System.out.print(", id #" + id + " n: " + nnum + " f: " + fnum);
		assertEquals(nnum, fnum);
		nnum = ndao.getNumChildrenForNodeId(id, false);
		fnum = fdao.getNumChildrenForNodeId(id, false);
		System.out.println(", id #" + id + " n: " + nnum + " f: " + fnum);
		assertEquals(nnum, fnum);		
	}
	
	public void testFetchNodeCollectionIds() {
		Object[] objids = new Object[] {new Long(1), new Long(2), new Long(8221), new Long(9042)};
		List ids = new ArrayList(Arrays.asList(objids));
		List nnodes = ndao.getNodeIdsWithIds(ids);
		List fnodes = fdao.getNodeIdsWithIds(ids);
		checkCollections(nnodes, fnodes);
		
		nnodes = ndao.getNodesWithIds(ids);
		fnodes = fdao.getNodesWithIds(ids);
		checkCollections(nnodes, fnodes);
		
		nnodes = ndao.getNodeNamesWithIds(ids);
		fnodes = fdao.getNodeNamesWithIds(ids);
		checkCollections(nnodes, fnodes);
		
		
		Object nobj = ndao.getDescendantNodesForIndexPage(ids);
		Object fobj = fdao.getDescendantNodesForIndexPage(ids);
		assertNotNull(nobj);
		assertNotNull(fobj);
		assertEquals(nobj, fobj);
		System.out.println("n: " + nobj);
		System.out.println("f: " + fobj);
	}
	
	private void checkCollections(List nnodes, List fnodes) {
		assertNotNull(nnodes);
		assertNotNull(fnodes);
		assertEquals(nnodes, fnodes);
		assertEquals(nnodes.size(), fnodes.size());		
	}
	
	//getMaxChildOrderOnParent
	public void testMaxChildOrderOnParent() {
		Long[] objids = new Long[] {new Long(1), new Long(2), new Long(8221), 
									new Long(9042), new Long(8870), new Long(8875),
									new Long(15721), new Long(2536), new Long(15994) };
		
		for (Long nodeId : objids) {
			Integer ncount = ndao.getMaxChildOrderOnParent(nodeId);
			Integer fcount = fdao.getMaxChildOrderOnParent(nodeId);
			assertEquals(ncount, fcount);
			System.out.print("  [id#(" + nodeId + ") n: " + ncount + " f: " + fcount + "] ");
		}
	}
	
	//getNodeExistsWithId
	public void testNodeExistsWithId() {
		Long[] exists = new Long[] {new Long(1), new Long(2), new Long(8221), 
				new Long(9042), new Long(8870), new Long(8875),  
				new Long(15721), new Long(2536), new Long(15994) };
		// test they agree on nodes that exist
		for (Long nodeId : exists) {
			Boolean nexist = ndao.getNodeExistsWithId(nodeId);
			Boolean fexist = fdao.getNodeExistsWithId(nodeId);
			assertEquals(nexist, fexist);
		}
		
		Long[] doesnt = new Long[] {new Long(6), new Long(11), new Long(21036), new Long(34084), new Long(84566) };
		// test they agree on nodes that don't exist at all 
		for (Long nodeId : doesnt) {
			Boolean nexist = ndao.getNodeExistsWithId(nodeId);
			Boolean fexist = fdao.getNodeExistsWithId(nodeId);
			assertEquals(nexist, fexist);
		}
	}
	
	//getNodeWithName
	public void testNodeWithName() {
		compareNodeNameFetchWithNameAndPageId("Omma", new Long(1309));
		compareNodeNameFetchWithNameAndPageId("Diprotodontia", new Long(1892));
		compareNodeNameFetchWithNameAndPageId("Notoryctemorphia", new Long(1892));
		compareNodeNameFetchWithNameAndPageId("Gyrinidae", new Long(1286));
		compareNodeNameFetchWithNameAndPageId("Cyanobacteria", new Long(274));
		compareNodeNameFetchWithNameAndPageId("Palpigradi", new Long(287));
	}
	
	private void compareNodeNameFetchWithNameAndPageId(String name, Long pageId) {
		MappedNode nnode = ndao.getNodeWithName(name, pageId);
		MappedNode fnode = fdao.getNodeWithName(name, pageId);
		assertNotNull(nnode);
		assertNotNull(fnode);
		assertEquals(nnode, fnode);
	}

	//getNodeWithNameAndParent
	public void testNodeWithNameAndParent() {
		compareNodeNameFetchWithNameAndParentId("Omma jurassicum", new Long(9042));
		compareNodeNameFetchWithNameAndParentId("Diprotodontia", new Long(15994));
		compareNodeNameFetchWithNameAndParentId("Notoryctemorphia", new Long(15994));
		compareNodeNameFetchWithNameAndParentId("Gyrinidae", new Long(8875));
		compareNodeNameFetchWithNameAndParentId("Cyanobacteria", new Long(2));
		compareNodeNameFetchWithNameAndParentId("Palpigradi", new Long(2543));
	}
	
	private void compareNodeNameFetchWithNameAndParentId(String name, Long parentId) {
		MappedNode nnode = ndao.getNodeWithNameAndParent(name, parentId);
		MappedNode fnode = fdao.getNodeWithNameAndParent(name, parentId);
		assertNotNull(nnode);
		assertNotNull(fnode);
		assertEquals(nnode, fnode);
	}	
	
	//getNodesWithNames(List nodeNames, Long parentNodeId)
	public void testNodesWithNames() {
		String[] grp1 = new String[] {"Paucituberculata", "Microbiotheria", "Dasyuromorphia", "Peramelemorphia", "Didelphimorphia"};
		String[] grp2 = new String[] {"Omma jurassicum", "Omma sagitta", "Omma rutherfordi"};
		String[] grp3 = new String[] {"Meru phyllisae", "Haliplidae", "Gyrinidae"};
		
		compareNodesWithNames(new ArrayList(Arrays.asList(grp1)), new Long(15994));
		//Paucituberculata Microbiotheria Dasyuromorphia Peramelemorphia Didelphimorphia 15994
		
		compareNodesWithNames(new ArrayList(Arrays.asList(grp2)), new Long(9042));
		//Omma jurassicum Omma sagitta Omma rutherfordi 9042
		
		compareNodesWithNames(new ArrayList(Arrays.asList(grp3)), new Long(8875));
		//Meru phyllisae Haliplidae Gyrinidae 8875
	}
	
	private void compareNodesWithNames(List names, Long parentNodeId) {
		List nlist = ndao.getNodesWithNames(names, parentNodeId);
		List flist = fdao.getNodesWithNames(names, parentNodeId);
		assertNotNull(nlist);
		assertNotNull(flist);
		assertEquals(nlist.size(), flist.size());
		assertEquals(nlist, flist);
	}
	
	//getMinimalNodeInfoForNodeId
	public void testMinimalNodeInfoForNodeId() {
		Long[] ids = new Long[] {new Long(1), new Long(2), new Long(8221), 
				new Long(9042), new Long(8870), new Long(8875),  
				new Long(15721), new Long(2536), new Long(15994) };
		
		for (Long id : ids) {
			Object[] nminInfo = ndao.getMinimalNodeInfoForNodeId(id);
			Object[] fminInfo = fdao.getMinimalNodeInfoForNodeId(id);
			assertTrue(Arrays.equals(nminInfo, fminInfo));
		}
	}
}
