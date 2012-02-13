package org.tolweb.tapestry;

import junit.framework.TestCase;

public class CitationUrlTest extends TestCase {
	private CitationUrl cu; 
	
	public void setUp() {
		cu = new CitationUrl("http://tolweb.org/Marsupialia/15994/2000.01.01");
	}
	
	public void test_simple_case() {
		String expected = "http://tolweb.org/Marsupialia/15994/2000.01.01 in The Tree of Life Web Project, http://tolweb.org/";
		String actual = cu.getCitationString();
		assertEquals(expected, actual);
	}
	
	public void test_different_host_url() {
		cu = new CitationUrl("http://tolweb.org/Marsupialia/15994/2000.01.01", "http://foo.org");
		String expected = "http://tolweb.org/Marsupialia/15994/2000.01.01 in The Tree of Life Web Project, http://foo.org";
		String actual = cu.getCitationString();
		assertEquals(expected, actual);		
	}
	
	public void test_project_name_changes() {
		cu.setProjectName("Lenards Phylogenetics Web Project");
		String expected = "http://tolweb.org/Marsupialia/15994/2000.01.01 in Lenards Phylogenetics Web Project, http://tolweb.org/";
		String actual = cu.getCitationString();
		assertEquals(expected, actual);		
	}
	
	public void test_empty_values() {
		cu = new CitationUrl("", "");
		cu.setProjectName("");
		String expected = " in , ";
		String actual = cu.getCitationString();
		assertEquals(expected, actual);		
	}

	public void test_null_values() {
		cu = new CitationUrl(null, null);
		cu.setProjectName(null);
		String expected = " in , ";
		String actual = cu.getCitationString();
		assertEquals(expected, actual);		
	}
	
	public void test_interface_implemented() {
		
		try {
			PageCitationComponent pcc = (PageCitationComponent)cu;
			pcc.getCitationString();
		} catch (Exception e) {
			fail();
		}
	}
	
	public void tearDown() {
		cu = null;
	}
}
