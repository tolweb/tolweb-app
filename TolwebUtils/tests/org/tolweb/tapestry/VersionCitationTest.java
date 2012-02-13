package org.tolweb.tapestry;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class VersionCitationTest extends TestCase {
	private VersionCitation vc; 
	private SimpleDateFormat versionDate;
	private Date now;
	
	public void setUp() {
		now = new Date();
		vc = new VersionCitation(now, true);
		versionDate = new SimpleDateFormat("dd MMMM yyyy");
	}
	
	public void test_simple_case_version_now() {
		String versionString = vc.getCitationString();
		assertEquals("Version " + versionDate.format(now), versionString);
	}

	public void test_version_now_without_status() {
		vc = new VersionCitation(now, false);
		String versionString = vc.getCitationString();
		assertEquals("Version " + versionDate.format(now) + ".", versionString);
	}	
	
	public void test_handles_null() {
		vc = new VersionCitation(null, false);
		String versionString = vc.getCitationString();
		assertEquals("Version " + versionDate.format(now) + ".", versionString);		
	}
	
	public void test_simple_case_but_calling_interface() {
		try {
			PageCitationComponent pcc = (PageCitationComponent) vc;
			String versionString = pcc.getCitationString();
			assertEquals("Version " + versionDate.format(now), versionString);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void tearDown() {
		vc = null;
		versionDate = null;
	}
}
