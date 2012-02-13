package org.tolweb.tapestry.xml.taxaimport.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the root of an XML Taxa Ingestion document. 
 * The document is rooted by a 'tree-of-life-web' element.  It's first 
 * single is either a single root node element or a nodes element 
 * containing multiple node elements.  The context is when a basal 
 * node is being updated, the single root node will be used and the 
 * data associated with the single root will be used to update the 
 * basal node.  However, when a nodes collection is provide, the "taxa" 
 * represented will be attached to the basal node without touching the 
 * clade-root's data.
 * 
 * @author lenards
 */
public class XTRoot {
	private XTNode node;
	private List<XTNode> nodes; 
	private String version; 
	
	/**
	 * Constructs a new instance of a XML Taxa Root (XTRoot) object
	 */
	public XTRoot() {
		nodes = new ArrayList<XTNode>();
	}
	
	/**
	 * Indicates if the root object has a single root node element 
	 * or a collection of nodes containing node elements.
	 * @return true if there is a single root node element; otherwise false.
	 */
	public boolean hasSingleRootNode() {
		return getNode() != null;
	}
	
	/**
	 * Returns a string representation of the root's state. 
	 */
	public String toString() {
		return "[root: " + getNodeString() + " version: " + version + " child-nodes: " + getChildNodesString() +"]"; 
	}
	
	private String getNodeString() {
		return (node != null) ? node.toString() : "<null>";
	}
	
	private String getChildNodesString() {
		return (nodes != null) ? "" + nodes.size() : "<null>";
	}
	
	/**
	 * Adds a node to the Nodes collection
	 * (used by the Betwixt collection-mapping functionality)
	 * @param node
	 */
	public void addNode(XTNode node) {
		getNodes().add(node);
	}
	
	/**
	 * @return the node
	 */
	public XTNode getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(XTNode node) {
		this.node = node;
	}

	/**
	 * @return the nodes
	 */
	public List<XTNode> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(List<XTNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
}