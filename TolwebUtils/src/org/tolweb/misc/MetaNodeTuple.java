package org.tolweb.misc;

public class MetaNodeTuple {
	private MetaNode metaNode;
	private BtolMetaNode btolMetaNode;

	public MetaNodeTuple() { 
		setBtolMetaNode(new BtolMetaNode());
	}
	
	public MetaNodeTuple(MetaNode mnode, BtolMetaNode bnode) {
		setMetaNode(mnode);
		setBtolMetaNode(bnode);
	}
	
	/**
	 * @return the metaNode
	 */
	public MetaNode getMetaNode() {
		return metaNode;
	}
	/**
	 * @param metaNode the metaNode to set
	 */
	public void setMetaNode(MetaNode metaNode) {
		this.metaNode = metaNode;
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
