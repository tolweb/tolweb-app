package org.tolweb.tapestry.xml.taxaimport.preparers;

import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;

import org.tolweb.treegrow.main.Contributor;

public class ContributorsPreparer extends XmlPreparerImpl {
	private Set<Contributor> contributors;

	public ContributorsPreparer(Set<Contributor> contributors) {
		this(contributors, NS);
	}	
	
	public ContributorsPreparer(Set<Contributor> contributors, String namespace) {
		super(namespace);
		setContributors(contributors);
	}
	
	@Override
	public Element toElement() {
		Element parent = createElement("contributors");
		processContributors(parent);
		return parent;
	}

	private void processContributors(Element parent) {
		for (Contributor contr : getContributors()) {
			Element child = createElement("contributor");
			addAttributes(child, contr);
			parent.appendChild(child);
		}
	}
	
	public void addAttributes(Element element, Contributor contr) {
		element.addAttribute(new Attribute("id", ""+contr.getId()));
		element.addAttribute(new Attribute("type", "other"));
		element.addAttribute(new Attribute("data-table", "CONTRIBUTORS"));
	}	
	
	/**
	 * @return the contributors
	 */
	public Set<Contributor> getContributors() {
		return contributors;
	}

	/**
	 * @param contributors the contributors to set
	 */
	public void setContributors(Set<Contributor> contributors) {
		this.contributors = contributors;
	}

}
