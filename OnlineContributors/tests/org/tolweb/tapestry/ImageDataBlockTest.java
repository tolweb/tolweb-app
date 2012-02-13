/*
 * Created on Jun 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import junit.framework.TestCase;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImageDataBlockTest extends TestCase {
	public void testCopyrightOwnerString() {
		Contributor contr = new Contributor();
		contr.setFirstName("Danny");
		contr.setLastName("Mandel");
		contr.setEmail("dmandel@tolweb.org");
		
		
		NodeImage img = new NodeImage();
		img.setCopyrightOwnerContributor(contr);
		img.setCopyrightDate("2004");
		
		String expectedCopyright = "&copy; 2004 <a href=\"mailto:dmandel@tolweb.org\">Danny Mandel</a>";
		System.out.println("expected is: " + expectedCopyright);
		//System.out.println("actual is: " + ImageDataBlock.getCopyrightOwnerString(img, true, false, false));
		//assertEquals(expectedCopyright, ImageDataBlock.getCopyrightOwnerString(img, true));
		contr.setDontShowEmail(true);
		expectedCopyright = "&copy; 2004 Danny Mandel";
		
		contr.setHomepage("http://tolweb.org");
		expectedCopyright = "&copy; 2004 <a href=\"http://tolweb.org\">Danny Mandel</a>";
		//assertEquals(expectedCopyright, ImageDataBlock.getCopyrightOwnerString(img, true));
		
		img.setCopyrightOwnerContributor(null);
		img.setCopyrightOwner("Michael Moore");
		expectedCopyright = "&copy; 2004 Michael Moore";
		//assertEquals(expectedCopyright, ImageDataBlock.getCopyrightOwnerString(img, true));
		
		img.setCopyrightEmail("mike@michaelmoore.com");
		expectedCopyright = "&copy; 2004 <a href=\"mailto:mike@michaelmoore.com\">Michael Moore</a>";
		//assertEquals(expectedCopyright, ImageDataBlock.getCopyrightOwnerString(img, true));
		
		img.setCopyrightUrl("http://michaelmoore.com");
		expectedCopyright = "&copy; 2004 <a href=\"http://michaelmoore.com\">Michael Moore</a>";
		//assertEquals(expectedCopyright, ImageDataBlock.getCopyrightOwnerString(img, true));
	}
}
