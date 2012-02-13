package org.tolweb.content.services;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedTextSection;

public class PageCoPreparerTest extends ApplicationContextTestAbstract {
	private PageDAO publicPageDAO;
	//private NodeDAO publicNodeDAO;
	
	public PageCoPreparerTest(String name) {
		super(name);
		
		publicPageDAO = (PageDAO) context.getBean("publicPageDAO");
		//publicNodeDAO = (NodeDAO) context.getBean("publicNodeDAO");
	}
/*
href="(.*)#(.*)"

href="(\.\.)?/(.*)"

<\\s*([^>]*)\\s*([^>]*)((class|id|style)="[^>]*")\\s*([^>]*)>

<(.*) (class=".*")>
<(.*) (.*)(class=".*")(.*)>

<(.*) (id=".*")>
<(.*) (id="*") (.*)>

<(.*) (style=".*")>
<(.*) (.*)(style=".*")(.*)>

\<ToLimg (id=\".*\" )(.*)\>
better --v
<ToLimg (id=\"[0-9]*\" )([^<]*)>

(<ToLimg (id=\".*\" )(.*)>)\n((.*)\n)(<p( class="caption")?>(((.*)\n)*)</p>)?

<ToLimg (id=\"[0-9]*\" )([^<]*)>(\n(.*)\n)?(<p( class="caption")?>(((.*)\n)*)</p>)?
<ToLimg (id=\"[0-9]*\" )([^<]*)>(\n(.*)\n)?(<p( class=\"caption\")>[^/]*)?
 */	
	public void testRegexOnTextSections() {
		//MappedTextSection mtxt = publicPageDAO.getTextSectionWithId(new Long(101894));
		//System.out.println(mtxt.getText());
		//String s = mtxt.getText().replaceAll("href=\"#(.*)\"", "href=\"http://tolweb.org/Eukaryotes/3#$1\"");
		//s = s.replaceAll("href=\"(\\.\\.)?/(.*)\"", "href=\"http://tolweb.org/tree/$2\"");
		//s = s.replaceAll("<ToLimg (id=\"(.*)\" )(.*)>", "<ToLimg $1>");
		//s = s.replaceAll("<ToLimg (id=\"(.*)\" )(.*)>", "<!-- media_$1-->");
		
		System.out.println(processHrefs(new Long(101894)));
		System.out.println(processHrefs(new Long(101896)));
		System.out.println(processHrefs(new Long(152471)));
		System.out.println(processHrefs(new Long(7225)));
		System.out.println(processHrefs(new Long(7226)));
	}
	
	private String processHrefs(Long id) {
		MappedTextSection mtxt = publicPageDAO.getTextSectionWithId(id);
		//System.out.println(mtxt.getText());
		String s = mtxt.getText();
		String pageUrl = "http://tolweb.org/Eukaryotes/3";
		s = s.replaceAll("href=\"#(.*)\"", "href=\"" + pageUrl + "#$1\"");
		s = s.replaceAll("href=\"(\\.\\.)?/([^>]*)\"([^<])*>", "href=\"http://tolweb.org/$2\">");
		
		s = s.replaceAll("<\\s*ToLimg\\s*([^>]*)\\s*id=\"?(\\d+)\"?\\s*([^>]*)>", 
		"<a href=\"javascript: w = window.open('http://tolweb.org/media/$2', '$2', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();\">Tree of Life Image</a>");
		s = s.replaceAll("<\\s*([^>]*)\\s*([^>]*)\\s*((class|[^_]id|style)=\"?[^>]*\"?)\\s*([^>]*)>", "<$1>");
		return s;
	}
}
