package org.tolweb.tapestry.xml.taxaimport.preparers;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;

import org.tolweb.btol.GeneFragmentNodeStatus;
import org.tolweb.treegrow.main.StringUtils;

public class BtolStatusesPreparer extends XmlPreparerImpl {
	private List<GeneFragmentNodeStatus> statuses;
	
	public BtolStatusesPreparer(List<GeneFragmentNodeStatus> statuses) {
		this(statuses, NS);
	}
	
	public BtolStatusesPreparer(List<GeneFragmentNodeStatus> statuses, String namespace) {
		super(namespace);
		setStatuses(statuses);
	}
	
	@Override
	public Element toElement() {
		Element parent = createElement("statuses");
		addAttributes(parent);
		processStatuses(parent);
		return parent;
	}

	private void processStatuses(Element parent) {
		Element child = createElement("ids");
		ArrayList<String> statIds = new ArrayList<String>();
		for (GeneFragmentNodeStatus status : getStatuses()) {
			statIds.add(status.getId().toString());
		}
		child.appendChild(new Text(StringUtils.returnCommaJoinedString(statIds)));
		parent.appendChild(child);
	}
	
	private void addAttributes(Element node) {
		node.addAttribute(new Attribute("data-table", "btol.GeneFragmentNodeStatuses"));
	}
	
	/**
	 * @return the statuses
	 */
	public List<GeneFragmentNodeStatus> getStatuses() {
		return statuses;
	}

	/**
	 * @param statuses the statuses to set
	 */
	public void setStatuses(List<GeneFragmentNodeStatus> statuses) {
		this.statuses = statuses;
	}

}
