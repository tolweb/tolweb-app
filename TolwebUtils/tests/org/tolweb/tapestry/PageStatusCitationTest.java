package org.tolweb.tapestry;

import junit.framework.TestCase;

public class PageStatusCitationTest extends TestCase {
	private PageStatusCitation ps;
	
	public void setUp() {
		ps = new PageStatusCitation("temporary");
	}
	
	public void test_simple_case() {
		String statusString = ps.getStatus();
		assertEquals("  (temporary).", statusString);
	}
	
	public void test_handles_null() {
		ps = new PageStatusCitation(null);
		String statusString = ps.getStatus();
		assertEquals("", statusString);		
	}

	public void test_handles_empty_string() {
		ps = new PageStatusCitation("");
		String statusString = ps.getStatus();
		assertEquals("", statusString);		
	}
	
	public void tearDown() {
		ps = null;
	}
}
