package org.tolweb.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;


import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;

public class GroupSearchAgent {
	private NodeDAO nodeDAO;

	public GroupSearchAgent(NodeDAO nodeDAO) {
		super();
		this.nodeDAO = nodeDAO;
	} 
	
	public List<GroupSearchResult> performSearch(String groupName, String option) {
		if (GroupSearchOptions.EXACT_OPTION.equals(option)) {
			return createResults(nodeDAO.findNodesExactlyNamed(groupName));
		} else if (GroupSearchOptions.START_OPTION.equals(option)) {
			return createResults(nodeDAO.findNodesNamedStartingWith(groupName));
		} else if (GroupSearchOptions.END_OPTION.equals(option)) {
			return createResults(nodeDAO.findNodesNamedEndingWith(groupName));
		} else { // implicit match to GroupSearchOptions.PARTIAL_OPTION
			return createResults(nodeDAO.findNodesNamed(groupName));
		}
	}
	
	// a method to convert from results of Object[] from the DAO to GroupSearchResult instances
	@SuppressWarnings("unchecked")
	private List<GroupSearchResult> createResults(List nodes) {
		List<GroupSearchResult> results = new ArrayList<GroupSearchResult>();
		
		// if all four cases, the element returned is the node - so it should be a MappedNode instance
		for(Iterator itr = nodes.iterator(); itr.hasNext(); ) {
			GroupSearchResult result = new GroupSearchResult((MappedNode)itr.next());
			results.add(result);
		}
		return results;
	}
	
	/*
	 * get this getting the write dependencies injected as method parameters
	 *  
	public void executeSearch() {
		GroupSearchAgent agent = new GroupSearchAgent(getPublicNodeDAO());
		List<GroupSearchResult> results = agent.performSearch(getSearchTaxon(), getOptionSelection());
		ViewGroupSearchResults resultsPage = getSearchResultsPage();
		resultsPage.setSearchTaxon(getSearchTaxon());
		resultsPage.setGroupSearchResults(results);
		cycle.activate(resultsPage);
	}
		 */
}
