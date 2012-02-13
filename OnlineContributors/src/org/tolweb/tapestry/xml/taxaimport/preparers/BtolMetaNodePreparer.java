package org.tolweb.tapestry.xml.taxaimport.preparers;

import nu.xom.Element;

import org.tolweb.misc.BtolMetaNode;

public class BtolMetaNodePreparer extends XmlPreparerImpl {
	private BtolMetaNode btolMetaNode;

	public BtolMetaNodePreparer(BtolMetaNode btolMetaNode) {
		this(btolMetaNode, NS);
	}	
	
	public BtolMetaNodePreparer(BtolMetaNode btolMetaNode, String namespace) {
		super(namespace);
		setBtolMetaNode(btolMetaNode);
	}
	
	@Override
	public Element toElement() {
		Element data = createElement("btol-data");
		addAttachedData(data);
		return data;
	}

	private void addAttachedData(Element parent) {
		addAttachedAdditionalFields(parent);
		addAttachedStatuses(parent);
	}
	
	private void addAttachedStatuses(Element parent) {
		if (btolMetaNode.getStatuses() != null && !btolMetaNode.getStatuses().isEmpty()) {
			BtolStatusesPreparer statPrep = new BtolStatusesPreparer(btolMetaNode.getStatuses());
			parent.appendChild(statPrep.toElement());
		}
	}
	
	private void addAttachedAdditionalFields(Element parent) {
		if(getBtolMetaNode().getNode() != null && getBtolMetaNode().getNode().getAdditionalFields() != null) {
			BtolAdditionalFieldsPreparer fieldsPrep = new BtolAdditionalFieldsPreparer(getBtolMetaNode().getNode().getAdditionalFields());
			parent.appendChild(fieldsPrep.toElement());
		}
	}
	
	/**
	 * @return the btolMetaNode
	 */
	public BtolMetaNode getBtolMetaNode() {
		return btolMetaNode;
	}

	/**
	 * @param btolMetaNode the btolMetaNode to set
	 */
	public void setBtolMetaNode(BtolMetaNode btolMetaNode) {
		this.btolMetaNode = btolMetaNode;
	}

}
