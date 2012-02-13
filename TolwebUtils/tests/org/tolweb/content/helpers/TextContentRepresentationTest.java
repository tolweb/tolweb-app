package org.tolweb.content.helpers;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.page.PageContributor;

import junit.framework.TestCase;

public class TextContentRepresentationTest extends TestCase {
	private MappedPage mpage; 
	private MappedTextSection mtxt;
	private Date sampleDate;
	
	public void setUp() {
		sampleDate = new Date();
		mpage = new MappedPage();
		mpage.setContentChangedDate(sampleDate);
		mpage.setFirstOnlineDate(sampleDate);
		mpage.setUsePermission(ContributorLicenseInfo.LICENSE_DEFAULT);
		PageContributor pc1 = new PageContributor();
		pc1.setIsAuthor(true);
		pc1.setContributor(new Contributor());
		pc1.setContributorId(650);
		pc1.setOrder(0);
		pc1.getContributor().setFirstName("Andrew");
		pc1.getContributor().setLastName("Lenards");
		pc1.getContributor().setHomepage("http://tolweb.org/");
		PageContributor pc2 = new PageContributor();
		pc2.setIsAuthor(true);
		pc2.setContributor(new Contributor());
		pc2.setContributorId(1030);
		pc2.setOrder(1);
		pc2.getContributor().setFirstName("Lisa");
		pc2.getContributor().setLastName("Antkow");
		pc2.getContributor().setHomepage("http://pima.gov");
		PageContributor pc3 = new PageContributor();
		pc3.setIsAuthor(true);
		pc3.setContributor(new Contributor());
		pc3.setContributorId(1030);
		pc3.setOrder(2);
		pc3.getContributor().setFirstName("Aaron");
		pc3.getContributor().setLastName("Marsh");
		pc3.getContributor().setHomepage("http://southwestambulance.com");
		TreeSet<PageContributor> authors = new TreeSet<PageContributor>();
		authors.add(pc1);
		authors.add(pc2);
		authors.add(pc3);
		System.out.println("pc1 cmp pc3: " + pc1.compareTo(pc3));
		System.out.println("pc1 cmp pc2: " + pc1.compareTo(pc2));
		System.out.println("pc1 cmp pc1: " + pc1.compareTo(pc1));
		System.out.println("pc2 cmp pc1: " + pc2.compareTo(pc1));
		System.out.println("pc2 cmp pc3: " + pc2.compareTo(pc3));
		System.out.println("pc3 cmp pc1: " + pc3.compareTo(pc1));
		System.out.println("pc3 cmp pc2: " + pc3.compareTo(pc2));
		mpage.setContributors(authors);
		mtxt = new MappedTextSection();
		mtxt.setTextSectionId(new Long(650));
		mtxt.setHeading("Bogus Text Section");
	}
	
	public void test_create_with_simple_arguments() {
		TextContentRepresentation textRep = new TextContentRepresentation(new MappedPage(), new MappedTextSection());
		assertNotNull(textRep);
	}

	public void test_create() {
		TextContentRepresentation textRep = new TextContentRepresentation(mpage, mtxt);
		assertNotNull(textRep);		
		
		assertEquals(textRep.getCreatedDate(), sampleDate);
		assertEquals(textRep.getModifiedDate(), sampleDate);
		
		assertEquals(textRep.getLanguage(), "en");
		
		Date now = new Date();
		assertNotSame(textRep.getCreatedDate(), now);
		assertNotSame(textRep.getModifiedDate(), now);
		
		assertEquals(textRep.getTitle(), "Bogus Text Section");
		
		assertEquals(textRep.getLicenseLink(), "http://creativecommons.org/licenses/by-nc/3.0/");
		System.out.println(textRep.getAuthorNames());
		assertNotNull(textRep.getAuthors().get("Andrew Lenards"));
		assertNotNull(textRep.getAuthors().get("Lisa Antkow"));
		assertTrue(textRep.getAuthors().containsKey("Andrew Lenards"));
		assertFalse(textRep.getAuthors().containsKey("Michael Lenards"));
		assertTrue(textRep.getAuthors().containsValue("http://tolweb.org/"));
		assertFalse(textRep.getAuthors().containsValue("http://gigism.com/"));
		
		Iterator itr = textRep.getAuthors().keySet().iterator();
		assertEquals(itr.next(), "Aaron Marsh");
		
		assertEquals(textRep.getMimeType(), "text/html");
		assertEquals(textRep.getDataType(), "http://purl.org/dc/dcmitype/Text");
		assertEquals(textRep.getIdentifier(), "tol-text-id-650");
		
		assertEquals(3, textRep.getCopyrightHolders().size());
	}

	public void test_construct_with_null_references() {
		mpage.setReferences(null);
		TextContentRepresentation textRep = new TextContentRepresentation(mpage, mtxt);
		assertNotNull(textRep);
	}
	
	public void tearDown() {
		mpage = null;
		mtxt = null;
		sampleDate = null;
	}
}
