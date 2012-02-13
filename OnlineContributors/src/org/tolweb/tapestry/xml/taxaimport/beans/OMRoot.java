package org.tolweb.tapestry.xml.taxaimport.beans;

import java.util.ArrayList;
import java.util.List;

/* document structure will look something like this: 
tree-of-life-web
    nodes
	    node
		page	
		contributors -> contributor
		accessorypages -> accessorypage
		mediafiles -> mediafile
		othernames -> othername */
/**
 * @author lenards
 */
public class OMRoot {
	private String xmlns;
	private String id;
	private Long basalNodeId;
	private List<OMNode> nodes;
	
	public OMRoot() {
		this.nodes = new ArrayList<OMNode>(); 
	}
	
	public void addNode(OMNode node) {
		getNodes().add(node);
	}
	
	/**
	 * @return the node
	 */
	public List<OMNode> getNodes() {
		return nodes;
	}

	/**
	 * @param node the node to set
	 */
	public void setNodes(List<OMNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the basalNodeId
	 */
	public Long getBasalNodeId() {
		return basalNodeId;
	}

	/**
	 * @param basalNodeId the basalNodeId to set
	 */
	public void setBasalNodeId(Long basalNodeId) {
		this.basalNodeId = basalNodeId;
	}

	/**
	 * @return the xmlns
	 */
	public String getXmlNamespace() {
		return xmlns;
	}

	/**
	 * @param xmlns the xmlns to set
	 */
	public void setXmlNamespace(String ns) {
		this.xmlns = ns;
	}
}
