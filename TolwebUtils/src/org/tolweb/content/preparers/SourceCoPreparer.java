package org.tolweb.content.preparers;

import org.tolweb.content.helpers.DaoBundle;
import org.tolweb.content.helpers.PageContentAttributes;
import org.tolweb.content.helpers.PageContentElements;

import nu.xom.Attribute;
import nu.xom.Element;

public class SourceCoPreparer extends AbstractCoPreparer {
	
	public void setContentSource(Object payload, DaoBundle daos, Element doc) {
		// don't currently need to do anything w/ the payload
		setDaoBundle(daos);
		setParentElement(doc);
		setPreparedElement(doc);
	}
	
	public void processContent() {
		Element src = new Element(PageContentElements.SOURCE, ContentPreparer.NS);
		src.addAttribute(new Attribute(PageContentAttributes.ID, "0"));
		src.addAttribute(new Attribute(PageContentAttributes.NAME, "Tree of Life Web Project"));
		src.addAttribute(new Attribute(PageContentAttributes.URL, "http://tolweb.org"));
		src.addAttribute(new Attribute(PageContentAttributes.LOGO, "http://tolweb.org/tree/img/ToLBrand.gif"));
		
		getParentElement().appendChild(src);
	}
}
