/*
 * Created on Jul 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import junit.framework.TestCase;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AboutPageTest extends TestCase {
	/*public void testCorrespondenceString() {
		String returnString = AboutPage.getCorrespondenceString(getContributors());
		assertEquals("Correspondence regarding this page should be directed to Katja Schulz at <a href=\"mailto:treegrow@ag.arizona.edu\">treegrow@ag.arizona.edu</a> and David Maddison at <a href=\"mailto:beetle@ag.arizona.edu\">beetle@ag.arizona.edu</a><br>", returnString);
	}
	
	public void testCopyrightString() {
		String returnString = AboutPage.getCopyrightString(getContributors(), "2004", null);
		System.out.println("return string is: " + returnString);
		// If printing URLs is turned on
		//String regexString = "Page\\s*copyright\\s*&copy;\\s*2004\\s*Danny Mandel,\\s*<a href=\"http://www.tolweb.org\">Katja Schulz</a>,\\s*and\\s*<a href=\"mailto:beetle@ag.arizona.edu\">David Maddison</a>";
		String regexString = "Page\\s*copyright\\s*&copy;\\s*2004\\s*Danny Mandel,\\s*Katja Schulz,\\s*and\\s*David Maddison";
		assertTrue(returnString.matches(regexString));
		returnString = AboutPage.getCopyrightString(getContributors(), "2004", "Bob");	
		//regexString = "Page\\s*copyright\\s*&copy;\\s*2004\\s*Danny Mandel,\\s*<a href=\"http://www.tolweb.org\">Katja Schulz</a>,\\s*<a href=\"mailto:beetle@ag.arizona.edu\">David Maddison</a>,\\s*and Bob";		
		regexString = "Page\\s*copyright\\s*&copy;\\s*2004\\s*Danny Mandel,\\s*Katja Schulz,\\s*David Maddison,\\s*and Bob";
		assertTrue(returnString.matches(regexString));		
	}
	
	private Collection getContributors() {
		Collection list = new ArrayList();
		Contributor contributor1  = new Contributor();
		contributor1.setFirstName("Danny");
		contributor1.setLastName("Mandel");
		contributor1.setEmail("dmandel@tolweb.org");
		contributor1.setDontShowEmail(true);
		AccessoryPageContributor contr1 = new AccessoryPageContributor();
		contr1.setIsCopyOwner(true);
		contr1.setIsContact(false);
		contr1.setOrder(0); 
		contr1.setContributor(contributor1);
		list.add(contr1);
		
		Contributor contributor2 = new Contributor();
		contributor2.setFirstName("Katja");
		contributor2.setLastName("Schulz");
		contributor2.setEmail("treegrow@ag.arizona.edu");
		contributor2.setDontShowEmail(false);
		contributor2.setHomepage("http://www.tolweb.org");
		AccessoryPageContributor contr2 = new AccessoryPageContributor();
		contr2.setIsCopyOwner(true);
		contr2.setIsContact(true);
		contr2.setOrder(1);
		contr2.setContributor(contributor2);
		list.add(contr2);
		
		Contributor contributor3 = new Contributor();
		contributor3.setFirstName("David");
		contributor3.setLastName("Maddison");
		contributor3.setEmail("beetle@ag.arizona.edu");
		contributor3.setDontShowEmail(false);
		AccessoryPageContributor contr3 = new AccessoryPageContributor();
		contr3.setIsCopyOwner(true);
		contr3.setIsContact(true);
		contr3.setOrder(2);
		contr3.setContributor(contributor3);
		list.add(contr3);
		
		return list; 
	}*/
}
