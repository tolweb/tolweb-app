package org.tolweb.dao;

import java.util.Map;
import java.util.SortedSet;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;

public interface OtherNameDAO extends BaseDAO {
	public Map<Long, String> getOtherNamesInfoForNode(Long nodeId);
	public Map<Long, String> getOtherNamesInfoForNode(MappedNode nd);
	
	/**
     * Updates the cross-reference table responsible for managing the attachment of other-names to nodes 
     * so that the orphaned other-name will be reattached to the new node-id
     * 
     * Used by the custom taxa import functionality developed in February 2008
     * @author lenards
     *  
	 * @param otherNameId the other-name this is being reattached
     * @param oldNodeId the node-id the other-name was attached to
     * @param newNodeId the new node-id to attach the other-name to 
	 */
	public void reattachOtherName(Long otherNameId, Long oldNodeId, Long newNodeId, int sequence);
	public void flushQueryCache();
	
	public void cleanseOtherNameDataForNode(Long nodeId);
}
