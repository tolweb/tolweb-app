/*
 * NodeDAO.java
 *
 * Created on May 4, 2004, 2:44 PM
 */

package org.tolweb.dao;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.tolweb.btol.AdditionalFields;
import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;

/**
 *
 * @author  dmandel
 */
public interface NodeDAO {
	public void addNodeWithId(Long id);
    public Integer getNumNodesNamed(String name);
    public Integer getNumNodesExactlyNamed(String name);
    public MappedNode getFirstNodeExactlyNamed(String name);
    public List findNodesNamed(String name);
    public List findNodesNamed(String name, boolean idOnly);
    public List findNodesNamedStartingWith(String name);
    public List findNodesNamedEndingWith(String name);
    /**
     * Returns a list of 3 element object arrays with the
     * first element the node name (String) and the 2nd the 
     * node id (Long), and the 3rd the parentnodeid 
     * for all nodes that exactly match the name  
     * @param name
     * @return
     */
    public List<Object[]> findNodeNamesParentIdsAndIdsNamed(String name);
    /**
     * Returns a list of 3 element object arrays with the
     * first element the node name (String), the 2nd the 
     * node id (Long), and the 3rd the parentnodeid 
     * for all nodes that partially match the name  
     * @param name
     * @return
     */    
    public List<Object[]> findNodeNamesParentIdsAndIdsExactlyNamed(String name);    
    public List findNodesExactlyNamed(String name);
    public List findNodesExactlyNamed(String name, boolean idOnly);
    public List getChildrenNodeIds(MappedNode parent);
    public List getChildrenNodeIds(Long id);
    public List getChildrenNodes(MappedNode parent);
    public List getChildrenNodes(MappedNode parent, boolean samePage, boolean idOnly);
    public Integer getNumChildrenForNode(MappedNode node);
    public Integer getNumChildrenForNodeId(Long nodeId);
    public Integer getNumChildrenForNodeId(Long nodeId, boolean checkIncertaeSedis);
    public List getAllChildrenNodes(MappedNode parent);
    /**
     * Return the max value of any child node's order on parent for the node
     * with the specified id
     * @param nodeId
     * @return
     */
    public Integer getMaxChildOrderOnParent(Long nodeId);
    public MappedNode getNodeWithId(Long id);
    public MappedNode getNodeWithId(Long id, boolean includeInactiveNodes);
    public List getNodesWithIds(Collection ids);
    public List getNodesWithIds(Collection ids, boolean includeInactiveNodes);
    public List getNodeNamesWithIds(Collection ids);
    public String getNodeNameWithId(Long id);
    /**
     * Returns a list of node ids that exist in the db.  Used
     * to check en masse whether all node ids are present
     * @param ids
     * @return
     */
    public List getNodeIdsWithIds(Collection ids);
    public MappedNode getParentNodeForNode(MappedNode nd);   
    public void clearCacheForNode(MappedNode nd);
    public void clearCacheForNodeIds(Collection<Long> nodeIds);    
    public void saveNode(MappedNode nd);
    public void deleteNode(MappedNode nd, boolean immediateFlush);
    public String getNameForNodeWithId(Long id);
    /**
     * Checks to see whether potentialAncestorId is an ancestor of nodeId
     * @param nodeId The node id to check (the potential descendant)
     * @param potentialAncestorId The ancestor id to check
     * @return
     */
    public boolean getNodeIsAncestor(Long nodeId, Long potentialAncestorId);
    public Integer getNumAncestorsForNode(Long nodeId);    
    public Set getAncestorsForNode(Long nodeId);
    /**
     * Determines the number of ancestors that exists for a given node-id.
     * @param nodeId The node id to check ancestor count for
     * @return an integer representing the number of entries in the 
     * NODEANCESTORS table for the node-id
     */
    public int getAncestorCount(Long nodeId);
    /**
     * Returns a set of descendant node ids for the given ancestor ids
     * @param ancestorIds collection of ancestor ids 
     * @return set of descendant node ids 
     */
    public Set getNodeIdsWithAncestors(Collection ancestorIds);
    public Set getInactiveNodeIdsWithAncestors(Long nodeId);
    /**
     * Takes a collection of node ids and returns only those ids
     * that are ancestors of the node id
     * @param nodeId The id of the node to check
     * @param potentialAncestorIds The node ids to filter
     * @return
     */
    public Collection<Long> getOnlyAncestorIds(Long nodeId, Collection potentialAncestorIds);
    public void resetAncestorsForNode(Long nodeId, Collection ancestors);
    public void deleteAncestorsForNode(Long nodeId);
    /**
     * Order a collection of node ids by their closeness to root --
     * the closest node id to root is the first element in the list,
     * furthest away from root is the last
     * @param nodeIds
     * @return
     */
    public List getOrderedByNumAncestorsNodes(Collection nodeIds);    
    public boolean getNodeExistsWithId(Long id);
    public boolean getNodeExistsWithIdUnfiltered(Long id);
    public boolean getNodeIsSubmitted(Long id);
    public AdditionalFields getAdditionalFieldsForNode(MappedNode node);
    public void addAdditionalFieldsForNode(MappedNode node);
    public void saveAdditionalFields(AdditionalFields fields);
    public Set getDescendantIdsForNode(Long nodeId);
    public Set getDescendantIdsForNode(Long nodeId, boolean filterInactive);
    public Set getDeletedDescendantIdsForNode(Long nodeId);
    public MappedNode getRootNode();
    public MappedNode getNodeWithName(String nodeName, Long pageId);
    public MappedNode getNodeWithNameAndParent(String nodeName, Long parentNodeId);
    /**
     * Get existing nodes with the same name and parent
     * @param nodeNames
     * @param parentNodeId
     * @return
     */
    public List getNodesWithNames(List nodeNames, Long parentNodeId);
    public void resetPageIdForNodeIds(Long pageId, Collection nodeIds);
    public void updateOrderOnPageForNode(Long nodeId, int order);
    public void updateHasIncompleteSubgroupsForNode(Long nodeId, boolean value);
    public void deleteAncestorsNotInBranch(Collection nodeIds);
    public void addAncestorsForNodes(Collection nodeIds, Collection ancestorIds);
    public void replaceTextInTaxa(Long rootNodeId, String toReplace, String replacementText, boolean caseSensitive, boolean wholeWords);
    /**
     * Returns a list of object arrays that have the following info:
     * node_id, node_Name, authority, auth_year, node_Extinct, parentnode_id
     * @param nodeId
     * @return
     */
    public List getDescendantsForTabbedExport(Long nodeId, Collection descendantIds);
    /**
     * Returns a hashtable that has node ids as keys, and a list of nodes
     * that have that node id as a parent as values -- for life on earth one deep it would be
     * 1 -> (Eubacteria, Eukaryotes, Archaea, Viruses)
     * @param descendantIds The node ids to build parent info about
     * @return
     */
    public Hashtable<Long, List<MappedNode>> getDescendantNodesForIndexPage(Collection descendantIds);
    public List getAllForeignDatabases();
    public void saveForeignDatabase(ForeignDatabase db);
    public ForeignDatabase getForeignDatabaseWithId(Long id);
    public ForeignDatabase getForeignDatabaseWithName(String name);
    public void flushQueryCache();
    
    /**
     * Returns an object array with minimal node info for xml export
     * @param nodeId 
     * @return An object array with the values: node_Name (string), 
     * 	node_Extinct (int), node_Confidence (int), node_Phylesis (int), node_Leaf (boolean),
     *  node_Description (string)
     */    
    public Object[] getMinimalNodeInfoForNodeId(Long nodeId);
	public void updatePageIdForNode(MappedNode node, Long pageId);
	/**
	 * Takes a source node and move it into the destination. 
	 * 
	 * The purpose of this is to take a node inactivated during a "taxa import" operation 
	 * and move it back into the clade in place of the target/destination.  The idea being 
	 * that a node may not be reconciled with a previous during the "taxa import" because 
	 * the names are not equal - but structurally (or taxonomically), they are the same 
	 * node (determined by an expect user). 
	 *  
	 * @param srcNodeId the old, inactive node that we want to "swap" into the clade to replace the destination
	 * @param destNodeId the target the source node will occupy once the "swap" operation is complete 
	 */
	public void swapNodeAncestry(Long srcNodeId, Long destNodeId);
	/**
	 * Takes child of destination and associates them with the source
	 * 
	 * The purpose of this is related to swapNodeAncestory(), we're attempt to 
	 * move a formerly 'inactive' node back into the tree.  Or, in other words,  
	 * the 'inactive' node is our source and the new, active node is the 
	 * destination of the source. We need to make the nodes that believe they 
	 * are children of the destination know that they are *now* children of 
	 * the source. 
	 * 
	 * @param srcNodeId
	 * @param destNodeId
	 */
	public void swapNodeParenthood(Long newParentNodeId, Long oldParentNodeId);
	public boolean getNodeIsInactive(Long nodeId);
}
