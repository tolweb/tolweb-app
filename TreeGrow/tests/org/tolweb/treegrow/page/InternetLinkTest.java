import org.tolweb.treegrow.page.InternetLink;

import junit.framework.TestCase;

/*
 * Created on Jul 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InternetLinkTest extends TestCase {
	public void testEquals() {
		InternetLink link1 = new InternetLink();
		InternetLink link2 = new InternetLink();
		assertFalse(link1.equals(link2));
		link1.setLinkId(new Long(1));
		assertFalse(link1.equals(link2));
		link2.setLinkId(new Long(2));
		assertFalse(link1.equals(link2));
		link2.setLinkId(new Long(1));
		assertTrue(link1.equals(link2));
		assertTrue(link1.equals(link1));
		assertTrue(link2.equals(link2));
	}
}
