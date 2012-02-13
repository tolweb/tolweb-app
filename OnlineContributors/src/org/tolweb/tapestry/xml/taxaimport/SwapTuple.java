package org.tolweb.tapestry.xml.taxaimport;

import java.io.Serializable;

import org.tolweb.misc.MetaNode;

/**
 * A convenience class that bundles a meta-node and 
 * various settings to be used when processing swap 
 * operations.  
 * 
 * Is used by the SwapNode page - was originally a private 
 * inner class of that page but that TOTALLY causes trouble.  
 * The hivemind runtime tries to serialize the entire page, 
 * and causes a ton of trouble - so don't try to do that. 
 * 
 * @author lenards
 *
 */
public class SwapTuple extends NodeTuple<MetaNode> implements Serializable {
	/** */
	private static final long serialVersionUID = -3551126606439591193L;
	private boolean moveNameToTarget;
	private boolean replaceTargetNode;
	private boolean removeAttachedPage;
	private boolean removeAttachedOtherNames;
	private boolean detachAllMedia;
	private Long parentNodeId; 
	
	public SwapTuple(MetaNode node) {
		super(node);
		moveNameToTarget = true;
		replaceTargetNode = true;
	}
	
	public MetaNode getMetaNode() {
		return getNode();
	}
	
	public void setMetaNode(MetaNode node) {
		setNode(node);
	}

	/**
	 * @return the moveNameToTarget
	 */
	public boolean getMoveNameToTarget() {
		return moveNameToTarget;
	}

	/**
	 * @param moveNameToTarget the moveNameToTarget to set
	 */
	public void setMoveNameToTarget(boolean moveNameToTarget) {
		this.moveNameToTarget = moveNameToTarget;
	}

	/**
	 * Gets the Parent Node Id for which this node will become a child. 
	 * @return the parentNodeId that will be the new parent of this node.
	 */
	public Long getParentNodeId() {
		return parentNodeId;
	}

	/**
	 * Sets the Parent Node Id for which this node will become a child. 
	 * @param parentNodeId the parentNodeId to set
	 */
	public void setParentNodeId(Long parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public boolean getReplaceTargetNode() {
		return replaceTargetNode;
	}

	public void setReplaceTargetNode(boolean replaceTargetNode) {
		this.replaceTargetNode = replaceTargetNode;
	}

	public boolean getRemoveAttachedPage() {
		return removeAttachedPage;
	}

	public void setRemoveAttachedPage(boolean removeAttachedPage) {
		this.removeAttachedPage = removeAttachedPage;
	}

	public boolean getRemoveAttachedOtherNames() {
		return removeAttachedOtherNames;
	}

	public void setRemoveAttachedOtherNames(boolean removeAttachedOtherNames) {
		this.removeAttachedOtherNames = removeAttachedOtherNames;
	}

	public boolean getDetachAllMedia() {
		return detachAllMedia;
	}

	public void setDetachAllMedia(boolean detachAllMedia) {
		this.detachAllMedia = detachAllMedia;
	}
}
