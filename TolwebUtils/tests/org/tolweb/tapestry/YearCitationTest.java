package org.tolweb.tapestry;

import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class YearCitationTest extends TestCase {
	private YearCitation yc; 
	
	public void setUp() {
		yc = new YearCitation(new Date());
	}
	
	public void test_year_correct() {
		String yearString = yc.getCitationString();
		assertEquals("2008. ", yearString);
	}
	
	public void test_handles_null() {
		yc = new YearCitation(null);
		String yearString = yc.getCitationString();
		assertEquals("2008. ", yearString);
	}
	
	public void test_years_in_past() {
		GregorianCalendar gcal = new GregorianCalendar(1976, 8, 11);
		yc = new YearCitation(gcal.getTime());
		String yearString = yc.getCitationString();
		assertEquals("1976. ", yearString);		
	}
	
	public void test_object_implements_citation_interface() {
		try {
			PageCitationComponent pcc = (PageCitationComponent)yc;
			String yearString = pcc.getCitationString();
			assertEquals("2008. ", yearString);			
		} catch (Exception e) {
			fail();
		}
	}
	
	public void tearDown() {
		yc = null;
	}
}
