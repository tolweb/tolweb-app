package org.tolweb.content.helpers;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.treegrow.main.StringUtils;

public class EmbeddedMediaHandlerTest extends ApplicationContextTestAbstract {
	private ImageDAO imageDAO;
	private PageDAO pageDAO;
	
	public EmbeddedMediaHandlerTest(String name) {
		super(name);
		imageDAO = (ImageDAO)context.getBean("imageDAO");
		pageDAO = (PageDAO)context.getBean("publicPageDAO");
	}
	
	public void setUp() {

	}
	
	public void test_dependency_create() {
		assertNotNull(imageDAO);
		assertNotNull(pageDAO);
	}
	
	public void test_thumbnail_URL_absolute() {
		// As of: 2/12/2009
		// media id 35092 appears in http://tolweb.org/webservices/pagecontent/nc/28759
		// with a relative URL
		MappedPage atlantaFusca = pageDAO.getPageWithId(17534L);
		assertNotNull(atlantaFusca);
		String pageUrl = getPageNameUrl(atlantaFusca.getMappedNode());
		assertTrue(!StringUtils.isEmpty(pageUrl));
		SortedSet sections = atlantaFusca.getTextSections();
		for (Iterator itr = sections.iterator(); itr.hasNext(); ) {
			MappedTextSection mtxt = (MappedTextSection)itr.next();
			if (mtxt != null && mtxt.getHeading().equals("Characteristics")) {
				EmbeddedMediaHandler hndlr = new EmbeddedMediaHandler(mtxt.getText(), pageUrl, imageDAO);
				HashMap<Integer,String> embeddedMedia = hndlr.getEmbeddedMedia();
				for (String thumb : embeddedMedia.values()) {
					System.out.println(thumb);
					assertTrue(thumb.startsWith(EmbeddedMediaHandler.TOLWEB_HOSTNAME));
				}
					
			}
		}
	}
	
	public void test_simple() {
		MappedPage beetles = pageDAO.getPageWithId(1285L);
		String pageUrl = getPageNameUrl(beetles.getMappedNode());
		assertNotNull(beetles);
		SortedSet sections = beetles.getTextSections();
		for (Iterator itr = sections.iterator(); itr.hasNext(); ) {
			MappedTextSection mtxt = (MappedTextSection)itr.next();
			EmbeddedMediaHandler emh = new EmbeddedMediaHandler(mtxt.getText(), pageUrl, imageDAO);
			String text = emh.getText();
			assertTrue(text.indexOf("a href=\"/tree?") < 0);
			assertTrue(text.indexOf("ToLimg") < 0);
		}
	}
	
	public void test_embedded_media_found() {
		// text section id: 48503
		MappedPage beetles = pageDAO.getPageWithId(1285L);
		String pageUrl = getPageNameUrl(beetles.getMappedNode());
		assertNotNull(beetles);
		
		boolean wasFound = false;
		SortedSet sections = beetles.getTextSections();
		for (Iterator itr = sections.iterator(); itr.hasNext(); ) {
			MappedTextSection mtxt = (MappedTextSection)itr.next();

			if (mtxt != null && mtxt.getTextSectionId().equals(new Long(48503))) {
				EmbeddedMediaHandler emh = new EmbeddedMediaHandler(mtxt.getText(), pageUrl, imageDAO);
				assertTrue(!emh.getEmbeddedMedia().isEmpty());
				assertTrue(emh.getEmbeddedMedia().size() == 1);
				String value = emh.getEmbeddedMedia().get(new Integer(4748));
				assertTrue(StringUtils.notEmpty(value));
				wasFound = true;
			} 
		}
		
		if (!wasFound) {
			fail();
		}
	}

	private MappedTextSection findTextSection(String title, MappedPage mpage) {
		SortedSet sections = mpage.getTextSections();
		for (Iterator itr = sections.iterator(); itr.hasNext(); ) {
			MappedTextSection mtxt = (MappedTextSection)itr.next();

			if (mtxt != null && StringUtils.notEmpty(mtxt.getHeading()) && mtxt.getHeading().equals(title)) {
				return mtxt;
			} 
		}		
		return null;
	}
	
	public void test_helper_method_for_finding_text_sections() {
		MappedPage atlantaF = pageDAO.getPageWithId(1285L);
		assertNotNull(atlantaF);
		
		MappedTextSection mtxt = findTextSection("Characteristics", atlantaF);
		assertNotNull(mtxt);		
	}
	
	public void test_css_attributes_stripping() {
		String test = "<p class=\"MsoNormal\">";
		String result = "";
		test = "<div style=\"center\">";
		result = test.replaceAll(EmbeddedMediaHandler.CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		System.out.println(test + " | " + result);		

		test = "<div id=\"center\">";
		result = test.replaceAll(EmbeddedMediaHandler.CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		System.out.println(test + " | " + result);		

		test = "<img class=\"center\">";
		result = test.replaceAll(EmbeddedMediaHandler.CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		System.out.println(test + " | " + result);

		test = "<div class=\"imgcenterbb\">";
		result = test.replaceAll(EmbeddedMediaHandler.CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		System.out.println(test + " | " + result);

		test = "<    div            		 class=\"imgcenterbb\"    			>";
		result = test.replaceAll(EmbeddedMediaHandler.CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		System.out.println(test + " | " + result);		

		test = "<span class=\"Apple-style-span\"    >";
		result = test.replaceAll(EmbeddedMediaHandler.CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		System.out.println(test + " | " + result);		

		test = "<div\nclass=\"imgfr\" >";
		result = test.replaceAll(EmbeddedMediaHandler.CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		System.out.println(test + " | " + result);		

		test = "<span class=\"Apple-style-span\" >";
		result = test.replaceAll(EmbeddedMediaHandler.CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		result = result.replaceAll("\\s", "");
		System.out.println(test + " | " + result);		
	}	

	public void test_css_attributes_stripped_from_text_section_object() {
		MappedPage atlantaF = pageDAO.getPageWithId(17534L);
		assertNotNull(atlantaF);
		
		String pageUrl = getPageNameUrl(atlantaF.getMappedNode());
		assertTrue(StringUtils.notEmpty(pageUrl));
		
		MappedTextSection mtxt = findTextSection("Characteristics", atlantaF);
		assertNotNull(mtxt);
		
		EmbeddedMediaHandler emh = new EmbeddedMediaHandler(mtxt.getText(), pageUrl, imageDAO);
		String text = emh.getText();
		assertTrue(text.indexOf("class=\"") < 0);
		assertTrue(text.indexOf("style=\"") < 0);
		assertTrue(text.indexOf("id=\"") < 0);
	}	
	
	private String getPageNameUrl(MappedNode mnode) {
		String pageNodeNameUrl = mnode.getName();
		try {
			pageNodeNameUrl = URLEncoder.encode(pageNodeNameUrl, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		pageNodeNameUrl = (StringUtils.notEmpty(pageNodeNameUrl) ? pageNodeNameUrl
				+ "/"
				: "")
				+ mnode.getId();
		return "http://tolweb.org/" + pageNodeNameUrl;
	}	

	public void tearDown() {
		
	}
}
