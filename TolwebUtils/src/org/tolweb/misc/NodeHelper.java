package org.tolweb.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.StringUtils;

/**
 * A class for a collection of static methods helpful when dealing with MappedNodes. 
 * 
 * @author lenards
 *
 */
public class NodeHelper {
	
	/**
	 * Determines the node id of the closest containing group with a page attached. 
	 * @param nodeId the id of the node we are looking for the closest containing group with a page for
	 * @param miscNodeDAO misc is the only database with valid NodeAncestor data, this argument must be a NodeDAO pointed at misc
	 * @param pageDAO the page DAO for determining whether the node has a page attached (e.g. working vs. public/live)
	 * @return a long representing the node id of the closest containing group with a page.
	 */
	@SuppressWarnings("unchecked")
	public static Long findClosestContainingGroupWithPage(Long nodeId, NodeDAO miscNodeDAO, PageDAO pageDAO) {
		Set ancestors = miscNodeDAO.getAncestorsForNode(nodeId);
		// in NODEANCESTORS, the node appears as an ancestor of itself - we want to remove this relation
		ancestors.remove(nodeId); 
		Collection nodeIds = pageDAO.getNodeIdsWithPages(ancestors);
		return determineClosestContainingGroupWithPage(nodeIds, miscNodeDAO);
	}

    /*
     * Documentation for the logic behind "Finding Closest Containing Group With A Page": 
     * The idea is that if you take all of the ancestors of a node, and determine a count for them - the 
     * one with the highest count will be the closest containing group.  If you were to filter out the 
     * ancestors without pages, now you're able to determine the "closest containing group with a page." 
     * If you think of it like this, we have a node, id #31 and it has the following ancestors:
     *  
     * 31 -> 31, 29, 27, 22, 21, 13, 9, 2, 1
     *  
     * (A node is an ancestor of itself, so this is an identity quality to the ancestor relation.)
     *  
     * Now, if we filter out those nodes that does not have a page attachment, we get this: 
     * 
     * 31 -> 28, 21, 13, 2, 1
     * 
     * So if we create a histogram like representation of this using the number of ancestors a node has 
     * we'd get something like this: 
     * 
     * 28 **** (4)
     * 21 ***  (3)
     * 13 **   (2)
     *  2 *    (1)
     *  1      (0)
     * 
     * The node with the highest count is the node closest to "us", if we take the view of the node 
     * in question. 
     * 
     * In general - we need a node that satisfies these three conditions: 
     *   1) An ancestor of the argument node
     *   2) The ancestor has a page attached to it
     *   3) The ancestor has the highest ancestor count of those ancestors w/ a page attached
     */	
	@SuppressWarnings("unchecked")
	private static Long determineClosestContainingGroupWithPage(Collection nodeIds, NodeDAO miscNodeDAO) {
		if (nodeIds == null) {
			return null;
		}
		List<NodeIdCountTuple> candidates = new ArrayList<NodeIdCountTuple>();
		// determine the number of appearances in on ancestor-id 
		for (Iterator itr = nodeIds.iterator(); itr.hasNext(); ) {
			Long curr = (Long)itr.next();
			int count = miscNodeDAO.getAncestorCount(curr);
			candidates.add(new NodeIdCountTuple(curr, count));
		}
		Collections.sort(candidates);
		NodeIdCountTuple tuple = null;
		if (candidates != null && !candidates.isEmpty()) {
			tuple = candidates.get(0);
		}
		return (tuple != null) ? tuple.getNodeId() : null;
	}
	
	/**
	 * Fetches all node ids for an entire clade rooted at argument node id.
	 * @param basalNodeId the root of the clade to fetch
	 * @param dao the data source to use when getting node ids 
	 * @return a collection of node ids that compromises all nodes in a given clade
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Long> getCladeIds(Long basalNodeId, NodeDAO dao) {
		Set ancestorIds = dao.getAncestorsForNode(basalNodeId);
		System.out.println("ancestors: " + ancestorIds.toString());
		Set<Long> ids = new HashSet<Long>();
		determineCladeIds(basalNodeId, ids, dao);
		return ids;
	}
	
	/*
	 * Recursive, worker method that accumulates node-ids in 'ids' as it traverses the 
	 * sub-tree specified. 
	 */
	@SuppressWarnings("unchecked")
	private static void determineCladeIds(Long rootNodeId, Set<Long> ids, NodeDAO dao) {
		if (rootNodeId != null) {
			List childIds = dao.getChildrenNodeIds(rootNodeId);
			ids.addAll(childIds);
			for (Iterator itr = childIds.iterator(); itr.hasNext(); ) {
				Long childId = (Long)itr.next();
				determineCladeIds(childId, ids, dao);
			}
		}
	}

	/**
	 * Creates a full clade object representation rooted at the returned node.
	 * 
	 * The returned MappedNode has it's Children collection populated, as well  
	 * as all descendents are fully populated.  It's the full clade with all  
	 * child relations populated. 
	 *  
	 * @param basalNodeId the root of the clade 
	 * @param dao the data source to use when fetching node information
	 * @return a full clade represented in the tol object model
	 */
	public static MappedNode getClade(Long basalNodeId, NodeDAO dao) {
		MappedNode root = dao.getNodeWithId(basalNodeId);
		buildCladeRelations(root, dao);
		return root;
	}
	
	@SuppressWarnings("unchecked")
	private static void buildCladeRelations(MappedNode nd, NodeDAO dao) {
		if (nd != null) {
			List children = dao.getChildrenNodes(nd);
			if (children != null) {
				nd.setChildren(new ArrayList(children));
				for (Iterator itr = children.iterator(); itr.hasNext(); ) {
					MappedNode child = (MappedNode)itr.next();
					buildCladeRelations(child, dao);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setHasPageOnNodes(List nodes, PageDAO dao) {
		for (Iterator i = nodes.iterator(); i.hasNext(); ) {
			MappedNode nd = (MappedNode)i.next();
			nd.setHasPage(dao.getNodeHasPage(nd));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setHasPageOnClade(MappedNode root, PageDAO dao) {
		if (root != null) {
			root.setHasPage(dao.getNodeHasPage(root));
			if (root.getChildren() != null) {
				for (Iterator itr = root.getChildren().iterator(); itr.hasNext(); ) {
					MappedNode child = (MappedNode)itr.next();
					setHasPageOnClade(child, dao);
				}				
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<MappedNode> getInactiveNodesForClade(Long basalNodeId, NodeDAO miscNodeDAO, NodeDAO nodeDAO) {
		List<MappedNode> inactiveNodes = new ArrayList<MappedNode>();
		Set descIds = miscNodeDAO.getDescendantIdsForNode(basalNodeId);
		for (Iterator itr = descIds.iterator(); itr.hasNext(); ) {
			Long descId = (Long)itr.next();
			MappedNode mnode = nodeDAO.getNodeWithId(descId, true);
			if (mnode != null && mnode.getStatus().equals(MappedNode.INACTIVE)) {
				inactiveNodes.add(mnode);
			}
		}
		return inactiveNodes;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Long> getInactiveNodeIdsForClade(Long basalNodeId, NodeDAO miscNodeDAO, NodeDAO nodeDAO) {
		List<Long> inactiveNodeIds = new ArrayList<Long>();
		Set descIds = miscNodeDAO.getInactiveNodeIdsWithAncestors(basalNodeId);
		for (Iterator itr = descIds.iterator(); itr.hasNext(); ) {
			Long descId = (Long)itr.next();
			inactiveNodeIds.add(descId);
		}
		return inactiveNodeIds;
	}
	
	public static List<Long> getAncestorIdsForNode(MappedNode mnode, NodeDAO nodeDAO) {
		List<Long> ancestorIds = new ArrayList<Long>();
		traverseParentialRelations(mnode, ancestorIds, nodeDAO);
		return ancestorIds;
	}
	
	public static void traverseParentialRelations(MappedNode root, List<Long> ids, NodeDAO nodeDAO) {
		if (root != null && !root.getNodeId().equals(Long.valueOf(1))) {
			Long parentNodeId = root.getParentNodeId();
			ids.add(parentNodeId);
			traverseParentialRelations(nodeDAO.getNodeWithId(parentNodeId), ids, nodeDAO);
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static List<String> getNodeNameList(List mnodes) {
		List<String> names = new ArrayList<String>();
		for (Iterator itr = mnodes.iterator(); itr.hasNext(); ) {
			MappedNode mnode = (MappedNode)itr.next();
			String name = mnode.getName();
			if (StringUtils.notEmpty(name)) {
				names.add(name);
			}
		}
		return names;
	}
}
