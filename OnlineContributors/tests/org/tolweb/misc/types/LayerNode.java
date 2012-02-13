package org.tolweb.misc.types;

import java.util.ArrayList;
import java.util.List;

public class LayerNode {
	private List<FieldNode> nodes = new ArrayList<FieldNode>();
	private String name; 
	
	public void addFieldNode(FieldNode fnode) {
		nodes.add(fnode);
	}

	/**
	 * @return the nodes
	 */
	public List<FieldNode> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(List<FieldNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
