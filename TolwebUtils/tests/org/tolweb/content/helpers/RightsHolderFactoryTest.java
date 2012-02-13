package org.tolweb.content.helpers;

import junit.framework.TestCase;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.page.PageContributor;

public class RightsHolderFactoryTest extends TestCase {
	
	public void test_create_page_contributor() {
		PageContributor author = createPageContributor();
		RightsHolder h = RightsHolderFactory.createRightsHolderFor(author);
		assertNotNull(h);
		assertEquals("Andrew Lenards", h.getName());
		assertEquals("http://tolweb.org/", h.getHomepage());
		assertEquals("lenards@tolweb.org", h.getEmail());
		assertEquals("<a href=\"http://tolweb.org/\">Andrew Lenards</a>", h.toString());
		h.setHomepage(null);
		assertEquals("<a href=\"http://tolweb.org/people/650\">Andrew Lenards</a>", h.toString());
		h.setNonContributor(true);
		assertEquals("Andrew Lenards", h.toString());
		h.setNonContributor(false);
		assertEquals("<a href=\"http://tolweb.org/people/650\">Andrew Lenards</a>", h.toString());

	}
	
	private PageContributor createPageContributor() {
		PageContributor pc1 = new PageContributor();
		pc1.setIsAuthor(true);

		Contributor c = new Contributor();
		c.setId(650);
		c.setFirstName("Andrew");
		c.setLastName("Lenards");
		c.setHomepage("http://tolweb.org/");
		c.setEmail("lenards@tolweb.org");
	
		pc1.setContributor(c);
		pc1.setContributorId(650);
		pc1.setOrder(0);

		return pc1;
	}
}
