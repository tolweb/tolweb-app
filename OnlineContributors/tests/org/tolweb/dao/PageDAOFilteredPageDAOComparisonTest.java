package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;

public class PageDAOFilteredPageDAOComparisonTest extends ApplicationContextTestAbstract {
	private PageDAO pdao;
	private PageDAO fdao;
	private NodeDAO ndao; 
	
	public PageDAOFilteredPageDAOComparisonTest(String name) {
		super(name);
		ndao = (NodeDAO)context.getBean("workingNodeDAO");
		pdao = (PageDAO)context.getBean("workingPageDAO");
		fdao = (PageDAO)context.getBean("workingFilteredPageDAO");
	}
	
	public void testInitialState() {
		assertNotNull(pdao);
		assertNotNull(fdao);
	}
	
	// 1 getNodeHasPage(Long nodeId)
	public void testGetNodeHasPage() {
		Long[] ids = new Long[]{new Long(8221), new Long(15994), new Long(9042), new Long(8884)};
		for (Long id : ids) {
			checkNodeHasPageResults(id);
		}
	}
	
	private void checkNodeHasPageResults(Long nodeId) {
		Boolean phaspage = pdao.getNodeHasPage(nodeId);
		Boolean fhaspage = fdao.getNodeHasPage(nodeId);
		assertNotNull(phaspage);
		assertNotNull(fhaspage);
		assertEquals(phaspage, fhaspage);
	}
	
	// 2 getNodeIdsWithPages(Collection nodeIds)
	public void testGetNodeIdsThatHavePages() {
		Long[] ids = new Long[]{new Long(9042), new Long(9043), new Long(9044), new Long(9045),
								new Long(29714), new Long(29715), new Long(29716), new Long(29717),
								new Long(29718), new Long(29719), new Long(29720), new Long(29721),
								new Long(29722), new Long(29723), new Long(29724), new Long(29725)};
		Collection nodeIds = new ArrayList(Arrays.asList(ids));
		Collection presult = pdao.getNodeIdsWithPages(nodeIds);
		Collection fresult = fdao.getNodeIdsWithPages(nodeIds);
		assertNotNull(presult);
		assertNotNull(fresult);
		assertEquals(presult.size(), fresult.size());
		assertEquals(presult, fresult);
	}
	
	// 3 getPageIdNodeIdIsOn(Long nodeId)
	public void testGetPageIdNodeIdIsOn() {
		Long[] ids = new Long[]{new Long(8221), new Long(15994), new Long(8884), new Long(9042), 
				new Long(29714), new Long(29715), new Long(29716), new Long(29717), new Long(29718), 
				new Long(29719), new Long(29720), new Long(29721), new Long(29722), new Long(29723), 
				new Long(29724), new Long(29725), new Long(9043), new Long(9044), new Long(9045)};
		for (Long nodeId : ids) {
			checkPageIdNodeIdIsOnResult(nodeId);
		}
	}
	
	private void checkPageIdNodeIdIsOnResult(Long nodeId) {
		Long ppageId = pdao.getPageIdNodeIdIsOn(nodeId);
		Long fpageId = fdao.getPageIdNodeIdIsOn(nodeId);
		assertNotNull(ppageId);
		assertNotNull(fpageId);
		assertEquals(ppageId, fpageId);
	}
	
	// 4 getNodeForPageNodeIsOn(MappedNode nd)
	public void testGetNodeForPageNodeIsOn() {
		Long[] ids = new Long[]{new Long(8221), new Long(15994), new Long(8884), new Long(9042), 
				new Long(29714), new Long(29715), new Long(29716), new Long(29717), new Long(29718), 
				new Long(29719), new Long(29720), new Long(29721), new Long(29722), new Long(29723), 
				new Long(29724), new Long(29725), new Long(9043), new Long(9044), new Long(9045)};
		for (Long nodeId : ids) {
			MappedNode mnode = ndao.getNodeWithId(nodeId);
			checkNodeForPageNodeIsOnResults(mnode);
		}
	}
	
	private void checkNodeForPageNodeIsOnResults(MappedNode nd) {
		System.out.println("test4: " + nd.toString());
		//MappedNode pnd = pdao.getNodeForPageNodeIsOn(nd);
		MappedNode fnd = fdao.getNodeForPageNodeIsOn(nd);
		//assertNotNull(pnd);
		assertNotNull(fnd);
		//assertEquals(pnd, fnd);
	}
	
	// 5 getPageForNode(Long nodeId, boolean onlyId)
	public void testGetPageForNode() {
		Long[] ids = new Long[]{new Long(8221), new Long(15994), new Long(8884), new Long(9042),
				new Long(2508), new Long(8876), new Long(8875), new Long(9034), new Long(9048)};
		
		for (Long nodeId : ids) {
			checkPageForNodeResults(nodeId);
		}
	}
	
	private void checkPageForNodeResults(Long nodeId) {
		System.out.println("test5: " + nodeId);
		MappedNode nd = ndao.getNodeWithId(nodeId);
		MappedPage ppg = pdao.getPageForNode(nd);
		MappedPage fpg = fdao.getPageForNode(nd);
		doRobustAssert(ppg.getPageId(), fpg.getPageId());
		Long ppgId = pdao.getPageIdForNode(nd);
		Long fpgId = fdao.getPageIdForNode(nd);
		doRobustAssert(ppgId, fpgId);
		ppgId = pdao.getPageIdForNodeId(nodeId);
		fpgId = fdao.getPageIdForNodeId(nodeId);
		doRobustAssert(ppgId, fpgId);
	}
	
	private void doRobustAssert(Object lhs, Object rhs) {
		assertNotNull(lhs);
		assertNotNull(rhs);
		assertEquals(lhs, rhs);
	}
	
	// 6 getChildPages(Long pgId, boolean onlyNamesIds)
	public void testGetChildPage() {
		Long[] ids = new Long[]{new Long(2849), new Long(1285), new Long(1286), new Long(1303),
				new Long(1292), new Long(1307), new Long(3660), new Long(1892), new Long(1312)};
		for (Long pageId : ids) {
			checkGetChildPagesResults(pageId);
		}
	}
	
	private void checkGetChildPagesResults(Long pageId) {
		MappedPage ppg = pdao.getPageWithId(pageId);
		MappedPage fpg = fdao.getPageWithId(pageId);
		if (ppg != null && fpg != null) {
			List plst = pdao.getChildPages(ppg);
			List flst = fdao.getChildPages(fpg);
			assertEquals(plst.size(), flst.size());
			for (int i = 0; i < plst.size(); i++) {
				adhocMappedPageEquals((MappedPage)plst.get(i), (MappedPage)flst.get(i));
			}
		} else { 
			System.out.println("test6: page-id: " + pageId + " was skipped... what's up with that yo?");
		}
		List plst = pdao.getChildPageNamesAndIds(pageId);
		List flst = fdao.getChildPageNamesAndIds(pageId);
		assertEquals(plst.size(), flst.size());
		for (int i = 0; i < plst.size(); i++) {
			adhocPageNodeArrayEquals((Object[])plst.get(i), (Object[])flst.get(i));
		}
	}

	private void adhocMappedPageEquals(MappedPage lhs, MappedPage rhs) {
		assertNotNull(lhs);
		assertNotNull(rhs);
		assertEquals(lhs.getPageId(), rhs.getPageId());
		assertEquals(lhs.getMappedNode(), rhs.getMappedNode());
	}
	
	private void adhocPageNodeArrayEquals(Object[] lhs, Object[] rhs) {
		assertNotNull(lhs);
		assertNotNull(rhs);
		assertEquals(lhs.length, rhs.length);
		assertEquals(lhs[0], rhs[0]);
		assertEquals(lhs[1], rhs[1]);
		assertEquals(lhs[2], rhs[2]);
	}
	
	// 7 getNodesOnPage(Long pageId, boolean onlyNamed, boolean onlyIds, boolean orderByOrderOnParent, String additionalQuery)
	public void testGetNodesOnPage() {
		Long[] ids = new Long[]{new Long(8221), new Long(15994), new Long(8884), new Long(9042)};
		List<Long> pageIds = new ArrayList<Long>(); 
		for (Long id : ids) {
			MappedNode nd = ndao.getNodeWithId(id);
			MappedPage ppg = pdao.getPageForNode(nd);
			MappedPage fpg = fdao.getPageForNode(nd);
			assertEquals(ppg.getPageId(), fpg.getPageId());
			pageIds.add(ppg.getPageId());
			
			List plst = pdao.getNodesOnPage(ppg);
			List flst = fdao.getNodesOnPage(fpg);
			assertEquals(plst.size(), flst.size());
			plst = pdao.getNodesOnPage(ppg, true);
			flst = fdao.getNodesOnPage(fpg, true);
			assertEquals(plst.size(), flst.size());
			plst = pdao.getNodesOnPage(ppg, false);
			flst = fdao.getNodesOnPage(fpg, false);
			assertEquals(plst.size(), flst.size());
		}
		
		PageDAO pdaoLocal = pdao;
		PageDAO fdaoLocal = fdao;
		for (Long pageId : pageIds) {
			MappedPage ppg = pdao.getPageWithId(pageId);
			MappedPage fpg = fdao.getPageWithId(pageId);
			assertEquals(ppg.getPageId(), fpg.getPageId());
			List plst = pdaoLocal.getNodesOnPage(ppg, false, false, false);
			List flst = fdaoLocal.getNodesOnPage(fpg, false, false, false);
			assertEquals(plst.size(), flst.size());
			plst = pdaoLocal.getNodesOnPage(ppg, true, false, false);
			flst = fdaoLocal.getNodesOnPage(fpg, true, false, false);
			assertEquals(plst.size(), flst.size());
			plst = pdaoLocal.getNodesOnPage(ppg, true, true, false);
			flst = fdaoLocal.getNodesOnPage(fpg, true, true, false);
			assertEquals(plst.size(), flst.size());			
			plst = pdaoLocal.getNodesOnPage(ppg, true, true, true);
			flst = fdaoLocal.getNodesOnPage(fpg, true, true, true);
			assertEquals(plst.size(), flst.size());			
			plst = pdaoLocal.getNodesOnPage(ppg, false, true, false);
			flst = fdaoLocal.getNodesOnPage(fpg, false, true, false);
			assertEquals(plst.size(), flst.size());			
			plst = pdaoLocal.getNodesOnPage(ppg, false, true, true);
			flst = fdaoLocal.getNodesOnPage(fpg, false, true, true);
			assertEquals(plst.size(), flst.size());			
			plst = pdaoLocal.getNodesOnPage(ppg, false, false, true);
			flst = fdaoLocal.getNodesOnPage(fpg, false, false, true);
			assertEquals(plst.size(), flst.size());			
			plst = pdaoLocal.getNodesOnPage(ppg, true, false, true);
			flst = fdaoLocal.getNodesOnPage(fpg, true, false, true);
			assertEquals(plst.size(), flst.size());	
//			relocated to new test
//			List nplst = pdaoLocal.getNodeIdsOnPageWithIncompleteSubgroups(pageId);
//			List nflst = fdaoLocal.getNodeIdsOnPageWithIncompleteSubgroups(pageId);
//			assertEquals(nplst.size(), nflst.size());
		}
		
	}
	
	public void testNodeIsOnPageWithIncompleteSubgroupsIsEmpty() {
		Long[] pageIds = new Long[] {new Long(1285), new Long(1892), new Long(1292), new Long(3660)};
		for (Long pageId : pageIds) {
			List nplst = pdao.getNodeIdsOnPageWithIncompleteSubgroups(pageId);
			List nflst = fdao.getNodeIdsOnPageWithIncompleteSubgroups(pageId);
			assertEquals(nplst.size(), nflst.size());	
			System.out.println("page-dao: " + nplst);
			System.out.println("filtered: " + nflst);
		}
	}
    
	public void testNodeIsOnPageWithIncompleteSubgroups() {
		//43 3346 5393 5916 5917 5918 5919 6068 7055 7056 7057 7058 7318 7319 7597 7598 7599 7600 7601		
		Long[] pageIds = new Long[] {new Long(43), new Long(3346), new Long(5393), new Long(5916), new Long(5917), 
									new Long(5918), new Long(5919), new Long(6068), new Long(7055), new Long(7056), 
									new Long(7057), new Long(7058), new Long(7318), new Long(7319), new Long(7597), 
									new Long(7598), new Long(7599), new Long(7600), new Long(7601)};
		for (Long pageId : pageIds) {
			List nplst = pdao.getNodeIdsOnPageWithIncompleteSubgroups(pageId);
			List nflst = fdao.getNodeIdsOnPageWithIncompleteSubgroups(pageId);
			assertEquals(nplst.size(), nflst.size());	
			System.out.println("page-dao: " + nplst);
			System.out.println("filtered: " + nflst);
		}
	}

	
	// 8 getPageTypeForPageWithId(int id)
	public void testGetPageTypeForPageWithId() {
		Long[] pageIds = new Long[] {new Long(1285), new Long(1892), new Long(1852), new Long(1292), new Long(3660)};
		for (Long pageId : pageIds) {
			String ptype = pdao.getPageTypeForPageWithId(pageId.intValue());
			String ftype = fdao.getPageTypeForPageWithId(pageId.intValue());
			assertEquals(ptype, ftype);
		}
	}
	
	// 9 getNodeIdsAndEditHistoryIds()
	public void testGetNodeIdsAndEditHistoryIds() {
		List<Object[]> plst = pdao.getNodeIdsAndEditHistoryIds();
		List<Object[]> flst = fdao.getNodeIdsAndEditHistoryIds();
		assertEquals(plst.size(), flst.size());
		adhocNodeIdsEditHistoryIdsComparison(plst, flst);
	}
	
	private void adhocNodeIdsEditHistoryIdsComparison(List<Object[]> lhs, List<Object[]> rhs) {
		HashMap<Long, Object[]> lhsm = new HashMap<Long, Object[]>();
		HashMap<Long, Object[]> rhsm = new HashMap<Long, Object[]>();
		for (Object[] record : lhs) {
			lhsm.put((Long)record[0], record);
		}
		for (Object[] record : rhs) {
			rhsm.put((Long)record[0], record);
		}
		for (Long key : lhsm.keySet()) {
			Object[] lhst = lhsm.get(key);
			Object[] rhst = rhsm.get(key);
			assertEquals(lhst[0], rhst[0]);
			assertEquals(lhst[1], rhst[1]);
		}
	}
	
	//10 getMaxOrderOnPage(Long pageId)
	public void testGetMaxOrderOnPage() {
		Long[] pageIds = new Long[] {new Long(1285), new Long(1892), new Long(1852), new Long(1292), new Long(3660)};
		for (Long pageId : pageIds) {
			int pmax = pdao.getMaxOrderOnPage(pageId);
			int fmax = fdao.getMaxOrderOnPage(pageId);
			assertEquals(pmax, fmax);
		}
	}
	
	//11 getPageIdsAndNodeIdsForPages(Collection ancestorIds)
	public void testGetPageIdsAndNodeIdsForPages() {
		Long[] pageIds = new Long[] {new Long(1285), new Long(1892), new Long(1852), new Long(1292), new Long(3660)};
		List<Object[]> plst = pdao.getPageIdsAndNodeIdsForPages(Arrays.asList(pageIds));
		List<Object[]> flst = fdao.getPageIdsAndNodeIdsForPages(Arrays.asList(pageIds));
		assertEquals(plst.size(), flst.size());
		adhocNodeIdsEditHistoryIdsComparison(plst, flst);
	}
}
