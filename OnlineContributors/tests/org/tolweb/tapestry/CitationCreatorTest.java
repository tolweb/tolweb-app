package org.tolweb.tapestry;

import org.tolweb.archive.BranchLeafPageArchiver;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ArchivedPageDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.URLBuilder;

public class CitationCreatorTest extends ApplicationContextTestAbstract {
	private CitationCreator cc; 
	private PageDAO pageDAO;
	private BranchLeafPageArchiver pageArchiver;
	private ArchivedPageDAO archDAO;
	private URLBuilder urlBuilder;
	private MappedPage mpage;
	
	public CitationCreatorTest(String name) {
		super(name);
	}

	public void setUp() {
		pageDAO = (PageDAO) context.getBean("workingPageDAO");
		pageArchiver = (BranchLeafPageArchiver) context.getBean("pageArchiver");
		archDAO = (ArchivedPageDAO) context.getBean("archivedPageDAO");
		urlBuilder = (URLBuilder) context.getBean("urlBuilder");

		cc = new CitationCreator(pageArchiver, urlBuilder, archDAO);
	}

	public void test_citation() {
		mpage = pageDAO.getPageWithId(2311L);
		String expected = "Young, Richard E., Michael Vecchione,  and Katharina M. Mangold (1922-2003).2008. Coleoidea <span class=\"authority\">Bather, 1888</span>.  Octopods, squids, cuttlefishes and their relatives.Version 21 April 2008  (under construction).http://dev.tolweb.org/Coleoidea/19400/2008.04.21 in The Tree of Life Web Project, http://tolweb.org/";
		String actual = cc.createCitation(mpage);
		System.out.println("expected:\n"+expected);
		System.out.println("actual:\n"+actual);
		assertEquals(expected, actual);
	}
	
	public void test_spring_configuration_correct() {
		mpage = pageDAO.getPageWithId(2311L);
		String expected = "Young, Richard E., Michael Vecchione,  and Katharina M. Mangold (1922-2003).2008. Coleoidea <span class=\"authority\">Bather, 1888</span>.  Octopods, squids, cuttlefishes and their relatives.Version 21 April 2008  (under construction).http://dev.tolweb.org/Coleoidea/19400/2008.04.21 in The Tree of Life Web Project, http://tolweb.org/";
		CitationCreator springObj = (CitationCreator) context.getBean("citationCreator");
		assertNotNull(springObj);
		String actual = springObj.createCitation(mpage);
		assertEquals(expected, actual);
		
		mpage = pageDAO.getPageWithId(1285L);
		expected = "Maddison, David R.2000. Coleoptera.  Beetles.Version 11 September 2000  (under construction).http://dev.tolweb.org/Coleoptera/8221/2000.09.11 in The Tree of Life Web Project, http://tolweb.org/";
		actual = springObj.createCitation(mpage);
		System.out.println("expected:\n"+expected);
		System.out.println("actual:\n"+actual);
		assertEquals(expected, actual);
		
		mpage = pageDAO.getPageWithId(1769L);
		expected = "Laurin, Michel and Jacques A. Gauthier.1996. Amniota.  Mammals, reptiles (turtles, lizards, Sphenodon, crocodiles, birds) and their extinct relatives.Version 01 January 1996.http://dev.tolweb.org/Amniota/14990/1996.01.01 in The Tree of Life Web Project, http://tolweb.org/";
		actual = springObj.createCitation(mpage);
		System.out.println("expected:\n"+expected);
		System.out.println("actual:\n"+actual);
		assertEquals(expected, actual);
		
		mpage = pageDAO.getPageWithId(1892L);
		expected = "Tree of Life Web Project.2000. Marsupialia.  Metatheria,   Kangaroos, koalas, gliders, wombats, opossums, bandicoots, bilbies, etc..Version 01 January 2000  (temporary).http://dev.tolweb.org/Marsupialia/15994/2000.01.01 in The Tree of Life Web Project, http://tolweb.org/";
		actual = springObj.createCitation(mpage);
		System.out.println("expected:\n"+expected);
		System.out.println("actual:\n"+actual);
		assertEquals(expected, actual);		
	}

	public void test_citation_cleaned_correctly() {
		mpage = pageDAO.getPageWithId(2311L);
		String expected = "Young, Richard E., Michael Vecchione,  and Katharina M. Mangold (1922-2003).2008. Coleoidea Bather, 1888.  Octopods, squids, cuttlefishes and their relatives.Version 21 April 2008  (under construction).http://dev.tolweb.org/Coleoidea/19400/2008.04.21 in The Tree of Life Web Project, http://tolweb.org/";
		String actual = cc.createCitation(mpage);
		actual = cc.cleanCitation(actual);
		assertEquals(expected, actual);
		
		mpage = pageDAO.getPageWithId(1285L);
		expected = "Maddison, David R.2000. Coleoptera.  Beetles.Version 11 September 2000  (under construction).http://dev.tolweb.org/Coleoptera/8221/2000.09.11 in The Tree of Life Web Project, http://tolweb.org/";
		actual = cc.createCitation(mpage);
		actual = cc.cleanCitation(actual);
		System.out.println("expected:\n"+expected);
		System.out.println("actual:\n"+actual);
		assertEquals(expected, actual);
	}
	
	/**
	 * 
	 * bug: http://bugzilla.tolweb.org/show_bug.cgi?id=2392
	 * 
	 * test-data: http://working.tolweb.org/Riodininae_-_incertae_sedis/105604
	 * 		node-id: 105604
	 * 		page-id: 17138
 	 */
	public void test_author_names_in_citations() {
		mpage = pageDAO.getPageWithId(17138L);
		String actual = cc.createCitation(mpage);
		actual = cc.cleanCitation(actual);
		System.out.println("bug #2392: " + actual);
	}
	
	public void tearDown() {
		cc = null;
		pageDAO = null;
		pageArchiver = null;
		archDAO = null;
		urlBuilder = null;
		mpage = null;
	}
}
