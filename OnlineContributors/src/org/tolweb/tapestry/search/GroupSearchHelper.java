package org.tolweb.tapestry.search;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.NodeDAO;
import org.tolweb.search.GroupSearchAgent;
import org.tolweb.search.GroupSearchResult;

public class GroupSearchHelper {
	
	public static void performSearch(IRequestCycle cycle,
			NodeDAO publicNodeDAO, ViewGroupSearchResults resultsPage,
			String searchTerm, String optionValue) {
		GroupSearchAgent agent = new GroupSearchAgent(publicNodeDAO);
		List<GroupSearchResult> results = agent.performSearch(searchTerm,
				optionValue);
		resultsPage.setSearchTaxon(searchTerm);
		resultsPage.setGroupSearchResults(results);
		cycle.activate(resultsPage);
	}
}
