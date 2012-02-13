package org.tolweb.misc;

import java.io.Serializable;
import java.util.List;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.GeneFragmentNodeStatus;
import org.tolweb.btol.Specimen;

public class BtolMetaNode implements Serializable {
	/** */
	private static final long serialVersionUID = 7816596145087054958L;
	private static final int MINIMIUM_TIER = 0;
	// additional fields are part of the node - getAdditionalFields()
	private MappedNode node;
	private List<GeneFragmentNodeStatus> statuses;
	
	public boolean hasAttachments() {
		return node != null && ((node.getAdditionalFields() != null && 
				node.getAdditionalFields().getTier() >= MINIMIUM_TIER) || 
				(statuses != null && !statuses.isEmpty())); 
	}
	
	public boolean getHasAttachments() {
		return hasAttachments(); 
	}	
	
	public String toString() {
		return "{" + node.toString() + " has-attachments? " + hasAttachments() + "}";
	}
	
	/**
	 * @return the node
	 */
	public MappedNode getNode() {
		return node;
	}
	/**
	 * @param node the node to set
	 */
	public void setNode(MappedNode node) {
		this.node = node;
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
