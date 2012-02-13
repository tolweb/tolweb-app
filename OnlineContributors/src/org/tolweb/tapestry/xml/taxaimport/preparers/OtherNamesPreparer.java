package org.tolweb.tapestry.xml.taxaimport.preparers;

import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;

public class OtherNamesPreparer extends XmlPreparerImpl {
	private Map<Long, String> otherNames;
	
	public OtherNamesPreparer(Map<Long, String> otherNames) {
		this(otherNames, NS);
	}	
	
	public OtherNamesPreparer(Map<Long, String> otherNames, String namespace) {
		super(namespace);
		this.otherNames = otherNames;
	}
	
	@Override
	public Element toElement() {
		Element parent = createElement("othernames");
		processOtherNames(parent);
		return parent;
	}

	public void processOtherNames(Element parent) {
		for (Long othernameId : getOtherNames().keySet()) {
			Element child = createElement("othername");
			String name = getOtherNames().get(othernameId);
			addAttributes(child, othernameId, name);
			parent.appendChild(child);
		}
	}
	
	public void addAttributes(Element element, Long othernameId, String othername) {
		element.addAttribute(new Attribute("id", ""+othernameId));
		element.addAttribute(new Attribute("name", othername));
		element.addAttribute(new Attribute("type", "content"));
		element.addAttribute(new Attribute("data-table", "OTHERNAMES"));
	}
	
	/**
	 * @return the otherNames
	 */
	public Map<Long, String> getOtherNames() {
		return otherNames;
	}

	/**
	 * @param otherNames the otherNames to set
	 */
	public void setOtherNames(Map<Long, String> otherNames) {
		this.otherNames = otherNames;
	}

}
