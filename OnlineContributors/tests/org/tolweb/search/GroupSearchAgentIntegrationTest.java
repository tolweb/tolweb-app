package org.tolweb.search;

import java.util.List;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;

public class GroupSearchAgentIntegrationTest extends
		ApplicationContextTestAbstract {

	private NodeDAO nodeDAO; 
	private GroupSearchAgent agent; 
	
	public GroupSearchAgentIntegrationTest(String name) {
		super(name);
		nodeDAO = (NodeDAO) context.getBean("publicNodeDAO");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		agent = new GroupSearchAgent(nodeDAO);
	}
	
	public void test_object_construction() {
		assertNotNull(agent);
	}
	
	public void test_simple_find_search() {
		List<GroupSearchResult> results = agent.performSearch("Marsupialia", GroupSearchOptions.EXACT_OPTION);
		assertNotNull(results);
		assertTrue(!results.isEmpty());
		assertTrue(results.toString().indexOf("Marsupialia") != -1);
	}
	
	public void test_partial_match_search() {
		List<GroupSearchResult> results = agent.performSearch("dania", GroupSearchOptions.PARTIAL_OPTION);
		assertNotNull(results);
		assertTrue(!results.isEmpty());
		System.out.println(results);
		
		// see if a partial match works for other names
		List<GroupSearchResult> resultsA = agent.performSearch("Wombats", GroupSearchOptions.PARTIAL_OPTION);
		assertNotNull(resultsA);
		assertTrue(!resultsA.isEmpty());

		// see if the search is case-sensitive
		List<GroupSearchResult> resultsB = agent.performSearch("WOMBATS", GroupSearchOptions.PARTIAL_OPTION);
		assertTrue(resultsA.equals(resultsB));
		
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		agent = null;
	}
}
