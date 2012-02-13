/*
 * Created on Dec 8, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.treegrow.tree;

public class NodeHorizontalLine {
	private Node node;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	public NodeHorizontalLine(Node node, int x, int y, int endX) {
		this(node, x, y, endX, y);
	}
	
	public NodeHorizontalLine(Node node, int x, int y, int endX, int endY) {
		this.node = node;
		this.startX = x;
		this.startY = y;
		this.endX = endX;
		this.endY = endY;
	}
	
	/**
	 * @return Returns the length.
	 */
	public int getEndX() {
		return endX;
	}
	/**
	 * @param length The length to set.
	 */
	public void setEndX(int length) {
		this.endX = length;
	}
	/**
	 * @return Returns the x.
	 */
	public int getStartX() {
		return startX;
	}
	/**
	 * @param x The x to set.
	 */
	public void setStartX(int x) {
		this.startX = x;
	}
	/**
	 * @return Returns the startY.
	 */
	public int getStartY() {
		return startY;
	}
	/**
	 * @param startY The startY to set.
	 */
	public void setStartY(int y) {
		this.startY = y;
	}
	/**
	 * @return Returns the endY.
	 */
	public int getEndY() {
		return endY;
	}
	/**
	 * @param endY The endY to set.
	 */
	public void setEndY(int endY) {
		this.endY = endY;
	}

	/**
	 * @return Returns the node.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @param node The node to set.
	 */
	public void setNode(Node node) {
		this.node = node;
	}
}
