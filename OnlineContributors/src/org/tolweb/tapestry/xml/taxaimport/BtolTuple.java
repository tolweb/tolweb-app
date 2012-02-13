package org.tolweb.tapestry.xml.taxaimport;

import java.io.Serializable;

import org.tolweb.hibernate.MappedNode;

public class BtolTuple extends NodeTuple<MappedNode> implements Serializable {

	/** */
	private static final long serialVersionUID = -6491308810176326347L;
	private boolean hasAdditionalFields;
	private boolean hasStatuses;
	
	public BtolTuple(MappedNode node) {
		super(node);
	}

	/**
	 * @return the hasAdditionalFields
	 */
	public boolean getHasAdditionalFields() {
		return hasAdditionalFields;
	}

	/**
	 * @param hasAdditionalFields the hasAdditionalFields to set
	 */
	public void setHasAdditionalFields(boolean hasAdditionalFields) {
		this.hasAdditionalFields = hasAdditionalFields;
	}

	/**
	 * @return the hasStatuses
	 */
	public boolean getHasStatuses() {
		return hasStatuses;
	}

	/**
	 * @param hasStatuses the hasStatuses to set
	 */
	public void setHasStatuses(boolean hasStatuses) {
		this.hasStatuses = hasStatuses;
	}
}
