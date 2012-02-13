package org.tolweb.content.helpers;

import junit.framework.TestCase;

public class TextLinkMakerTest extends TestCase {
	private TextLinkMaker linkMaker; 
	
	public void setUp() {
		linkMaker = new TextLinkMaker("http://tolweb.org/Marsupialia/15994", "15994", "Marsupialia Tree");
	}
	
	public void test_link_created() {
		/* Example: 
		 *  <a href="javascript: w = window.open('http://tolweb.org/media/4748', 
		 *  	'4748', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();">
		 *  	<img src="http://tolweb.org/tree/ToLimages/beetleflying.100a.gif"/></a>
		 */		
		String expected = "<a href=\"javascript: w = window.open('http://tolweb.org/Marsupialia/15994'," +
			"'15994', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();\">Marsupialia Tree</a>";
		String actual = linkMaker.makeLink();
		assertEquals(expected, actual);
	}
	
	public void tearDown() {
		linkMaker = null;
	}
}
