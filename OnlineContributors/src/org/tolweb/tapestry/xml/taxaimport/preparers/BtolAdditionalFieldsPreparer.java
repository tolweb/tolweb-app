package org.tolweb.tapestry.xml.taxaimport.preparers;

import nu.xom.Attribute;
import nu.xom.Element;

import org.tolweb.btol.AdditionalFields;

public class BtolAdditionalFieldsPreparer extends XmlPreparerImpl {
	private AdditionalFields fields;
	
	public BtolAdditionalFieldsPreparer(AdditionalFields fields) {
		this(fields, NS);
	}	
	
	public BtolAdditionalFieldsPreparer(AdditionalFields fields, String namespace) {
		super(namespace);
		setFields(fields);
	}
	
	@Override
	public Element toElement() {
		Element child = createElement("additional-fields");
		addAttributes(child);
		return child;
	}

	private void addAttributes(Element node) {
		node.addAttribute(new Attribute("id", getFields().getId().toString()));
		node.addAttribute(new Attribute("data-table", "btol.AdditionalFields"));
	}	
	
	/**
	 * @return the fields
	 */
	public AdditionalFields getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(AdditionalFields fields) {
		this.fields = fields;
	}

}
