package org.tolweb.tapestry;

import junit.framework.TestCase;

public class TitleCitationTest extends TestCase {
	private TitleCitation tc; 
	
	public void setUp() {
		tc = new TitleCitation(null, 
				"Marsupialia", 
				"Metatheria, Kangaroos, koalas, gliders, wombats, opossums, bandicoots, bilbies, etc.");	
	}
	
	public void test_simple_case_marsupialia() {
		String titleString = tc.getCitationString();
		assertEquals(
				"Marsupialia. Metatheria, Kangaroos, koalas, gliders, wombats, opossums, bandicoots, bilbies, etc..", 
				titleString);
	}

	public void test_all_constructor_params_null() {
		tc = new TitleCitation(null, null, null);
		String titleString = tc.getCitationString();
		assertEquals("", titleString);
	}
	
	public void test_all_constructor_params_empty() {
		tc = new TitleCitation("", "", "");
		String titleString = tc.getCitationString();
		assertEquals("", titleString);
	}
	
	public void test_pagetitle_only() {
		tc = new TitleCitation("", "Marsupialia", "");
		String titleString = tc.getCitationString();
		assertEquals("Marsupialia. ", titleString);		
	}

	public void test_supertitle_only() {
		tc = new TitleCitation("Marsupials", "", "");
		String titleString = tc.getCitationString();
		assertEquals("Marsupials. ", titleString);		
	}	

	public void test_subtitle_only() {
		tc = new TitleCitation("", "", "And Other Crazy Australian Animals");
		String titleString = tc.getCitationString();
		assertEquals("And Other Crazy Australian Animals.", titleString);		
	}
	
	public void test_supertitle_pagetitle_only() {
		tc = new TitleCitation("Marsupials", "Marsupialia", "");
		String titleString = tc.getCitationString();
		assertEquals("Marsupials. Marsupialia. ", titleString);	
	}	

	public void test_pagetitle_subtitle_only() {
		tc = new TitleCitation("Marsupials", "", "And Other Crazy Australian Animals");
		String titleString = tc.getCitationString();
		assertEquals("Marsupials. And Other Crazy Australian Animals.", titleString);	
	}	
	
	public void test_interface_call() {
		try {
			PageCitationComponent pcc = (PageCitationComponent) tc;
			String titleString = pcc.getCitationString();
		} catch (Exception e) {
			fail();
		}
	}
	
	public void tearDown() {
		tc = null;
	}
}
