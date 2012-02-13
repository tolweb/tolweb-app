package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.page.PageContributor;

import junit.framework.TestCase;

public class AuthorCitationTest extends TestCase {
	private List<PageContributor> contributors;
	private AuthorCitation ac;
	
	public void setUp() {
		contributors = new ArrayList<PageContributor>();
		contributors.add(createPageContributor("Andrew J.", "Lenards", true));
		contributors.add(createPageContributor("Lisa", "Antkow", true));
		contributors.add(createPageContributor("Simon", "Josep", true));
		ac = new AuthorCitation(contributors);
	}
	
	public void test_simple_no_author_case() {
		ac = new AuthorCitation(new ArrayList<PageContributor>());
		String authorString = ac.getCitationString();
		assertEquals(AuthorCitation.DEFAULT_AUTHOR, authorString);
	}

	/**
	 * bug: http://bugzilla.tolweb.org/show_bug.cgi?id=2392
	 */
	public void test_no_author_but_copyright_owner_case() {
		PageContributor pc = createPageContributor("Andrew", "Lenards", false);
		pc.setIsCopyOwner(true);

		List<PageContributor> contrs = new ArrayList<PageContributor>();
		contrs.add(pc);
		ac = new AuthorCitation(contrs);
		String authorString = ac.getCitationString();
		assertEquals(AuthorCitation.DEFAULT_AUTHOR, authorString);		
	}

	/**
	 * bug: http://bugzilla.tolweb.org/show_bug.cgi?id=2392
	 */	
	public void test_no_author_but_correspondent_case() {
		PageContributor pc = createPageContributor("Andrew", "Lenards", false);
		pc.setIsContact(true);

		List<PageContributor> contrs = new ArrayList<PageContributor>();
		contrs.add(pc);
		ac = new AuthorCitation(contrs);
		String authorString = ac.getCitationString();
		assertEquals(AuthorCitation.DEFAULT_AUTHOR, authorString);		
	}
	
	public void test_no_author_but_copyright_owner_and_correspondent_case() {
		PageContributor pc = createPageContributor("Andrew", "Lenards", false);
		pc.setIsCopyOwner(true);		
		pc.setIsContact(true);

		List<PageContributor> contrs = new ArrayList<PageContributor>();
		contrs.add(pc);
		ac = new AuthorCitation(contrs);
		String authorString = ac.getCitationString();
		assertEquals(AuthorCitation.DEFAULT_AUTHOR, authorString);		
	}
	
	public void test_simple_three_authors_string() {
		String authorString = ac.getCitationString();
		assertEquals("Lenards, Andrew J., Lisa Antkow,  and Simon Josep.", authorString);		
	}
	
	/**
	 * Relates to Bug #2649 - http://bugzilla.tolweb.org/show_bug.cgi?id=2649
	 */
	public void test_single_author_double_period_issue() {
		contributors.clear();
		contributors.add(createPageContributor("Andrew J.  ", "Lenards", true));
		ac = new AuthorCitation(contributors);
		
		String authorString = ac.getCitationString();
		// prior to solving this, the output would have been: 
		//	         "Lenards, Andrew J.  ."
		assertEquals("Lenards, Andrew J.", authorString);
	}
	
	public void test_simple_three_authors_with_nonauthor_first_position() {
		contributors.clear();
		contributors.add(createPageContributor("Andrew J.", "Lenards", false));
		contributors.add(createPageContributor("Lisa", "Antkow", true));
		contributors.add(createPageContributor("Simon", "Josep", true));
		contributors.add(createPageContributor("Daniel", "Mandel", true));

		ac = new AuthorCitation(contributors);
		
		String authorString = ac.getCitationString();
		assertEquals("Antkow, Lisa, Simon Josep,  and Daniel Mandel.", authorString);		
	}
	
	public void test_simple_authors_with_nonauthor_mixed_in() {
		contributors.clear();
		contributors.add(createPageContributor("Andrew J.", "Lenards", true));
		contributors.add(createPageContributor("Lisa", "Antkow", true));
		contributors.add(createPageContributor("Simon", "Josep", false));
		contributors.add(createPageContributor("Daniel", "Mandel", true));

		ac = new AuthorCitation(contributors);
		
		String authorString = ac.getCitationString();
		assertEquals("Lenards, Andrew J., Lisa Antkow,  and Daniel Mandel.", authorString);

		contributors.clear();
		contributors.add(createPageContributor("Andrew J.", "Lenards", true));
		contributors.add(createPageContributor("Lisa", "Antkow", false));
		contributors.add(createPageContributor("Simon", "Josep", false));
		contributors.add(createPageContributor("Daniel", "Mandel", false));

		ac = new AuthorCitation(contributors);
		
		authorString = ac.getCitationString();
		assertEquals("Lenards, Andrew J.", authorString);

		contributors.clear();
		contributors.add(createPageContributor("Andrew J.", "Lenards", true));
		contributors.add(createPageContributor("Lisa", "Antkow", true));
		contributors.add(createPageContributor("Simon", "Josep", true));
		contributors.add(createPageContributor("Daniel", "Mandel", false));

		ac = new AuthorCitation(contributors);
		
		authorString = ac.getCitationString();
		assertEquals("Lenards, Andrew J., Lisa Antkow,  and Simon Josep.", authorString);		

		contributors.clear();
		contributors.add(createPageContributor("Andrew J.", "Lenards", false));
		contributors.add(createPageContributor("Lisa", "Antkow", false));
		contributors.add(createPageContributor("Simon", "Josep", false));
		contributors.add(createPageContributor("Daniel", "Mandel", false));

		ac = new AuthorCitation(contributors);
		
		authorString = ac.getCitationString();
		assertEquals(AuthorCitation.DEFAULT_AUTHOR, authorString);		
	}
	
	public void test_interface_implemented() {
		try {
			PageCitationComponent pcc = new AuthorCitation(new ArrayList<PageContributor>());
			String authorString = pcc.getCitationString();
			assertEquals(AuthorCitation.DEFAULT_AUTHOR, authorString);
		} catch (Exception e) {
			fail();
		}
	}

	public void tearDown() {
		ac = null;
		contributors = null;
	}
	
	private PageContributor createPageContributor(String firstName, String lastName, boolean isAuthor) {
		Contributor contr = new Contributor();
		contr.setFirstName(firstName);
		contr.setLastName(lastName);
		PageContributor pc = new PageContributor();
		pc.setIsAuthor(isAuthor);
		pc.setContributor(contr);
		return pc;
	}
	
}