package org.tolweb.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;


/**
 * This test is a 'workbench' or 'play-area' for figuring a concept 
 * regarding how to find the closest containing group with a page. This 
 * will be used in a number of different place.  But one case would be 
 * where you have an ImageData block (which you see when you view an image) 
 * and you want to link to the "Attached Group."  This works fine for nodes 
 * that are published (e.g. they exist in Live [aka Public] & Working).  
 * But when you're dealing with something that existing only in Working, the 
 * link is not valid when browsing through the "live" site.  So you need to 
 * link to the closest containing group with a page (specifically, the  
 * closest containing group with a page in Live/Public).  
 * 
 * Another case where this comes into play is 
 * @author lenards
 *
 */
public class ClosestContainingGroupWithPageTest extends ApplicationContextTestAbstract {
	private PageDAO workingPageDAO;
	private NodeDAO workingNodeDAO;
	private NodeDAO miscNodeDAO;
	
	private Long danainiId = 27571L;
	private Long libytheinaeId = 12193L;
	
	public ClosestContainingGroupWithPageTest(String name) {
		super(name);
		workingNodeDAO = (NodeDAO)context.getBean("workingNodeDAO");
		workingPageDAO = (PageDAO)context.getBean("workingPageDAO");
		miscNodeDAO = (NodeDAO)context.getBean("nodeDAO");
	}

	public void testMappedNodeFetch() {
		MappedNode danaini = workingNodeDAO.getNodeWithId(danainiId);
		MappedNode liby = workingNodeDAO.getNodeWithId(libytheinaeId);
		assertNotNull(danaini);
		assertNotNull(liby);
	}
	
	public void testNodeAncestorsFetch() {
		// move out into a helper class... 
		// method parameters: nodeId, miscNodeDAO, pageDAO
		Set danainiAncestors = miscNodeDAO.getAncestorsForNode(danainiId);
		System.out.println("danaini: " + danainiAncestors);
		Set libyAncestors = miscNodeDAO.getAncestorsForNode(libytheinaeId);
		System.out.println("libytheinae: " + libyAncestors);
		
		Collection dnodes = workingPageDAO.getNodeIdsWithPages(danainiAncestors);
		Collection lnodes = workingPageDAO.getNodeIdsWithPages(libyAncestors);
		dnodes.remove(danainiId);
		lnodes.remove(libytheinaeId);
		
		// anything contained in dnodes or lnodes is "ancestor of the node" and "has a page" (in working)
		Long dId = determineClosestContainingGroupWithPage(dnodes);
		Long lId = determineClosestContainingGroupWithPage(lnodes);
		Long hdId = NodeHelper.findClosestContainingGroupWithPage(danainiId, miscNodeDAO, workingPageDAO);
		Long hlId = NodeHelper.findClosestContainingGroupWithPage(libytheinaeId, miscNodeDAO, workingPageDAO);
		assertEquals(dId, hdId);
		assertEquals(lId, hlId);
	}

	private Long determineClosestContainingGroupWithPage(Collection dnodes) {
		List<NodeIdCountTuple> candidates = new ArrayList<NodeIdCountTuple>();
		
		for (Iterator itr = dnodes.iterator(); itr.hasNext(); ) {
			Long curr = (Long)itr.next();
			int count = miscNodeDAO.getAncestorCount(curr);
			NodeIdCountTuple tuple = new NodeIdCountTuple(curr, count);
			candidates.add(tuple);
		}
		Collections.sort(candidates);
		System.out.println(candidates.get(0));
		return candidates.get(0).getNodeId();
	}
	
	public class NodeIdCountTuple implements Comparable<NodeIdCountTuple> {
		private Long nodeId;
		private int count;
		
		public NodeIdCountTuple() {}
		
		public NodeIdCountTuple(Long nodeId, int count) {
			this.nodeId = nodeId;
			this.count = count;
		}
		
		/**
		 * @return the nodeId
		 */
		public Long getNodeId() {
			return nodeId;
		}
		/**
		 * @param nodeId the nodeId to set
		 */
		public void setNodeId(Long nodeId) {
			this.nodeId = nodeId;
		}
		/**
		 * @return the count
		 */
		public int getCount() {
			return count;
		}
		/**
		 * @param count the count to set
		 */
		public void setCount(int count) {
			this.count = count;
		}

		public int compareTo(NodeIdCountTuple o) {
			return o.getCount() - count;
		}
		
		@Override
		public String toString() {
			return "node-id: " + nodeId + " count: " + count;
		}
	}
}
