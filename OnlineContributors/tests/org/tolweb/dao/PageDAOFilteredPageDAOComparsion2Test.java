package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;

public class PageDAOFilteredPageDAOComparsion2Test extends ApplicationContextTestAbstract {
	private PageDAO pdao;
	private PageDAO fdao;
	private NodeDAO ndao; 

	public PageDAOFilteredPageDAOComparsion2Test(String name) {
		super(name);
		ndao = (NodeDAO)context.getBean("workingNodeDAO");
		pdao = (PageDAO)context.getBean("workingUnfilteredPageDAO");
		fdao = (PageDAO)context.getBean("workingFilteredPageDAO");
	}

/*  public boolean getHasIncompleteSubgroups() {
    	boolean isPublic = ((ViewBranchOrLeaf) getPage()).getIsPublic();
    	// check the cache if it's the public page
    	if (isPublic) {
    		Boolean isIncomplete = getCacheAccess().getPageIsIncomplete(getTolPage());
    		if (isIncomplete != null) {
    			return isIncomplete;
    		}
    	}
    	boolean childOnPageWithoutPageHasIncomplete = false;
    	boolean rootHasIncomplete = ((ViewBranchOrLeaf) getPage()).getNode().getHasIncompleteSubgroups(); 
    	if (!rootHasIncomplete) {
    		// get all of the nodes on that page that are marked incomplete
    		List<Long> nodeIdsWithIncSubgroups = ((ViewBranchOrLeaf) getPage()).getPageDAO().getNodeIdsOnPageWithIncompleteSubgroups(getTolPage().getPageId());
    		if (nodeIdsWithIncSubgroups != null && nodeIdsWithIncSubgroups.size() > 0) {
    			// check to see if any don't have pages
    			Collection<Long> nodeIdsWithPages = ((ViewBranchOrLeaf) getPage()).getPageDAO().getNodeIdsWithPages(nodeIdsWithIncSubgroups);
    			// show the message if there is at least one descendant
    			// node on the page that doesn't have its own page
    			childOnPageWithoutPageHasIncomplete = nodeIdsWithPages != null &&
    				nodeIdsWithPages.size() != nodeIdsWithIncSubgroups.size();
    		}
    	}
    	boolean returnValue = rootHasIncomplete || childOnPageWithoutPageHasIncomplete;
    	if (isPublic) {
    		getCacheAccess().setPageIsIncomplete(getTolPage(), returnValue);
    	}
    	return returnValue;
    }  */
	
	public void testBooleanValues() {
		Boolean b = new Boolean("1");
		assertEquals(false, b.booleanValue());
		Boolean b2 = new Boolean("0");
		assertEquals(false, b2.booleanValue());
	}
	
	public void testIncompleteSubgroupsComputation() {
		MappedNode diprotodon = ndao.getNodeWithId(122963L, true);
		MappedPage mpage = pdao.getPageForNode(diprotodon);
		if (mpage != null) {
			List<Long> nodeIdsWithIncSubgroupsA = pdao.getNodeIdsOnPageWithIncompleteSubgroups(mpage.getPageId());
			List<Long> nodeIdsWithIncSubgroupsB = fdao.getNodeIdsOnPageWithIncompleteSubgroups(mpage.getPageId());
			System.out.println("nodeids w/ incomplete subgroups count(A) = " + nodeIdsWithIncSubgroupsA.size());
			System.out.println("nodeids w/ incomplete subgroups count(B) = " + nodeIdsWithIncSubgroupsB.size());
			Collection<Long> nodeIdsWithPagesA = createNodeIdsWithPages(nodeIdsWithIncSubgroupsA, pdao);
			Collection<Long> nodeIdsWithPagesB = createNodeIdsWithPages(nodeIdsWithIncSubgroupsB, fdao);
			System.out.println("nodeids w/ pages count(A) = " + nodeIdsWithPagesA.size());
			System.out.println("nodeids w/ pages count(B) = " + nodeIdsWithPagesB.size());
		} else {
			fail();
		}
	}
	
	private Collection<Long> createNodeIdsWithPages(List<Long> list, PageDAO dao) {
		Collection<Long> collection = new ArrayList<Long>();
		if (list != null && list.size() > 0) {
			collection = dao.getNodeIdsWithPages(list);
		}
		return collection; 
	}
}
