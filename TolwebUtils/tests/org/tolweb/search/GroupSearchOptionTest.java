package org.tolweb.search;

import java.util.Iterator;

import junit.framework.TestCase;

public class GroupSearchOptionTest extends TestCase {

	public void test_constants_are_expected_values() {
		assertEquals("partially", GroupSearchOptions.PARTIAL_OPTION);
		assertEquals("exactly", GroupSearchOptions.EXACT_OPTION);
		assertEquals("beginning of name", GroupSearchOptions.START_OPTION);
		assertEquals("end of name", GroupSearchOptions.END_OPTION);
	}
	
	public void test_options_list_constructed() {
		assertNotNull(GroupSearchOptions.OPTIONS);
		assertTrue(!GroupSearchOptions.OPTIONS.isEmpty());
		assertEquals(GroupSearchOptions.OPTIONS, GroupSearchOptions.getOptionsList());
	}
	
	/**
	 * Order will matter when the list is used by a UI component
	 */
	public void test_options_list_ordering() {
		Iterator<String> itr = GroupSearchOptions.OPTIONS.iterator();

		String option = itr.next();
		assertEquals(GroupSearchOptions.PARTIAL_OPTION, option);

		option = itr.next();
		assertEquals(GroupSearchOptions.EXACT_OPTION, option);
		
		option = itr.next();
		assertEquals(GroupSearchOptions.START_OPTION, option);
		
		option = itr.next();
		assertEquals(GroupSearchOptions.END_OPTION, option);
	}
}
