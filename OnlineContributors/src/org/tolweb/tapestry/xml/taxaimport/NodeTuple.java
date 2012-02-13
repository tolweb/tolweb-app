package org.tolweb.tapestry.xml.taxaimport;

import java.io.Serializable;

public class NodeTuple<T> implements Serializable {
	/** */
	private static final long serialVersionUID = 35172110758981950L;
	private T node;
	private Long targetNodeId;
	private boolean shouldRetireNode;
	
	public NodeTuple(T node) {
		this.node = node;
	}
	
	/**
	 * @return the node
	 */
	public T getNode() {
		return node;
	}
	/**
	 * @param node the node to set
	 */
	public void setNode(T node) {
		this.node = node;
	}
	/**
	 * @return the targetNodeId
	 */
	public Long getTargetNodeId() {
		return targetNodeId;
	}
	/**
	 * @param targetNodeId the targetNodeId to set
	 */
	public void setTargetNodeId(Long targetNodeId) {
		this.targetNodeId = targetNodeId;
	}

	/**
	 * @return the retireNode
	 */
	public boolean getShouldRetireNode() {
		return shouldRetireNode;
	}

	/**
	 * Set whether a node should be permanently retired
	 * @param retireNode the retireNode to set
	 */
	public void setShouldRetireNode(boolean retireNode) {
		this.shouldRetireNode = retireNode;
	}
}
