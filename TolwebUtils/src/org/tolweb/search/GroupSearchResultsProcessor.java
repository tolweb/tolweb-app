package org.tolweb.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.NodeHelper;

public class GroupSearchResultsProcessor {
	private NodeDAO miscNodeDAO;
	private NodeDAO publicNodeDAO;
	private PageDAO pageDAO;
	
	public GroupSearchResultsProcessor(NodeDAO miscNodeDAO,
			NodeDAO publicNodeDAO, PageDAO pageDAO) {
		super();
		this.miscNodeDAO = miscNodeDAO;
		this.publicNodeDAO = publicNodeDAO;
		this.pageDAO = pageDAO;
	}
	
	public void establishContainingGroupRelations(List<GroupSearchResult> results) {
		for (GroupSearchResult result : results) {
			Long ccgrpId = NodeHelper.findClosestContainingGroupWithPage(result.getNodeId(), miscNodeDAO, pageDAO);
			
			MappedNode ccgrpNode = publicNodeDAO.getNodeWithId(ccgrpId);
			result.setContainingGroup(ccgrpNode);
			
			boolean hasPage = pageDAO.getNodeHasPage(result.getNode());
			result.setHasPage(hasPage);
		}
	}
	
	public void establishAncestorCounts(List<GroupSearchResult> results) {
		for (GroupSearchResult result : results) {
			int count = miscNodeDAO.getAncestorCount(result.getNodeId());
			if (count >= 0) {
				result.setAncestorCount(count);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void establishDescendentRelations(List<GroupSearchResult> results) {
		for (GroupSearchResult result : results) {
			Set descIds = miscNodeDAO.getDescendantIdsForNode(result.getNodeId());
			// remove the identity relation
			descIds.remove(result.getNodeId());
			result.setDescendents(createDescendentMap(descIds));
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<Long, Long> createDescendentMap(Set descIds) {
		int size = descIds.size();
		// create a map with size + (size * .35) to give ideal load
		size += size * .35;
		Map<Long, Long> descMap = new HashMap<Long, Long>(size);
		for (Iterator itr = descIds.iterator(); itr.hasNext(); ) {
			Long descId = (Long)itr.next();
			descMap.put(descId, descId);
		}
		return descMap;
	}
	
	public Set<GroupSearchResult> determineRelatedGroups(List<GroupSearchResult> results) {
		Set<GroupSearchResult> toRemove = new HashSet<GroupSearchResult>();
		for (int i = 0; i < results.size(); i++) {
			GroupSearchResult curr = results.get(i);
			if (toRemove.contains(curr)) {
				continue; // skip
			}
			for (int j = i; j < results.size(); j++) {
				GroupSearchResult tmp = results.get(j);
				if (curr.getDescendents().containsKey(tmp.getNodeId())) {
					// the mapped node is related to 'curr' so 
					// we're going to show it as a descendent
					curr.addRelatedGroupToResult(tmp);
					// since it'll be displayed under 'curr', we 
					// want to remove it from the results list
					toRemove.add(tmp);
				}
			}
		}
		return toRemove;
	}	
}
