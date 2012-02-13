package org.tolweb.tapestry.xml.taxaimport.preparers;

import nu.xom.Element;

public abstract class XmlPreparerImpl implements XmlPreparer {
	public static final String NS = "http://tolweb.org/xml/2008";
	
	private String namespace;
	
	public XmlPreparerImpl () { }
	
	protected XmlPreparerImpl(String namespace) {
		setNamespace(namespace);
	}
	
	public abstract Element toElement();
	
	public String toString() {
		return (toElement() != null) ? toElement().toXML() : "{null-element}";
	}

	protected Element createElement(String elementName) {
		return new Element(elementName, getNamespace());
	}
	
	/**
	 * @return the namespace
	 */
	protected String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	protected void setNamespace(String namespace) {
		this.namespace = namespace;
	}	
}
