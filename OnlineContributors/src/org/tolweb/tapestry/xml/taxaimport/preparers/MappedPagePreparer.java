package org.tolweb.tapestry.xml.taxaimport.preparers;

import nu.xom.Attribute;
import nu.xom.Element;

import org.tolweb.hibernate.MappedPage;

public class MappedPagePreparer extends XmlPreparerImpl {
	private MappedPage mpage;
	
	public MappedPagePreparer(MappedPage mpage) {
		this(mpage, NS);
		
	}
	
	public MappedPagePreparer(MappedPage mpage, String namespace) {
		super(namespace);
		this.mpage = mpage;
	}
	
	@Override
	public Element toElement() {
		Element page = createElement("page");
		addAttributes(page);
		return page;
	}

	private void addAttributes(Element element) {
		MappedPage tmp = getMappedPage();
		element.addAttribute(new Attribute("id", tmp.getPageId().toString()));
		element.addAttribute(new Attribute("type", "content"));
		element.addAttribute(new Attribute("data-table", "PAGES"));
	}
	
	/**
	 * @return the mpage
	 */
	public MappedPage getMappedPage() {
		return mpage;
	}

	/**
	 * @param mpage the mpage to set
	 */
	public void setMappedPage(MappedPage mpage) {
		this.mpage = mpage;
	}
}
