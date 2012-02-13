package org.tolweb.tapestry.xml.taxaimport.preparers;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.tolweb.hibernate.MappedAccessoryPage;

public class MappedAccessoryPagesPreparer extends XmlPreparerImpl {
	private List<MappedAccessoryPage> accessoryPages;
	
	public MappedAccessoryPagesPreparer(List<MappedAccessoryPage> accessoryPages) {
		this(accessoryPages, NS);
	}
	
	public MappedAccessoryPagesPreparer(List<MappedAccessoryPage> accessoryPages, String namespace) {
		super(namespace);
		this.accessoryPages = accessoryPages;
	}
	
	@Override
	public Element toElement() {
		Element accessorypages = createElement("accessorypages");
		processAccessoryPages(accessorypages);
		return accessorypages;
	}
	
	private void processAccessoryPages(Element accessorypages) {
		for (MappedAccessoryPage accPage : getAccessoryPages()) {
			Element pageElement = createElement("accessorypage");
			addAttributes(pageElement, accPage);
			accessorypages.appendChild(pageElement);
		}
	}

	private void addAttributes(Element accessorypage, MappedAccessoryPage accPage) {
		accessorypage.addAttribute(new Attribute("id", ""+accPage.getId()));
		accessorypage.addAttribute(new Attribute("type", "content"));
		accessorypage.addAttribute(new Attribute("data-table", "ACCESSORY_PAGES"));
	}

	/**
	 * @return the accessoryPages
	 */
	public List<MappedAccessoryPage> getAccessoryPages() {
		return accessoryPages;
	}

	/**
	 * @param accessoryPages the accessoryPages to set
	 */
	public void setAccessoryPages(List<MappedAccessoryPage> accessoryPages) {
		this.accessoryPages = accessoryPages;
	}
	
}
