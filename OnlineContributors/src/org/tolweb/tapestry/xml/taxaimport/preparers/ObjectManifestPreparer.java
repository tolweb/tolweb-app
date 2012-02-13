package org.tolweb.tapestry.xml.taxaimport.preparers;

import java.util.HashMap;

import nu.xom.Attribute;
import nu.xom.Element;

import org.tolweb.misc.MetaNodeTuple;

public class ObjectManifestPreparer extends XmlPreparerImpl {
	private Long basalNodeId;
	private HashMap<Long, MetaNodeTuple> metaNodeMap;
	private boolean includeNodesWithoutAttachments;
	
	public ObjectManifestPreparer(Long basalNodeId, HashMap<Long, MetaNodeTuple> map) {
		this(basalNodeId, map, NS);
	}
	
	public ObjectManifestPreparer(Long basalNodeId, HashMap<Long, MetaNodeTuple> map, String namespace) {
		super(namespace);
		this.metaNodeMap = map;
		this.basalNodeId = basalNodeId;
	}
	
	@Override
	public Element toElement() {
		Element root = createElement("tree-of-life-web");
		root.addAttribute(new Attribute("basal-node-id", getBasalNodeId().toString()));
		Element nodes = createElement("nodes");
		root.appendChild(nodes);
		processMetaNodes(nodes);
		return root;
	}

	private void processMetaNodes(Element root) {
		for (MetaNodeTuple tuple : getMetaNodeMap().values()) {
			boolean metaNodeHasAttachments = tuple.getMetaNode() != null && tuple.getMetaNode().getHasAttachments();
			boolean btolHasAttachments = tuple.getBtolMetaNode() != null && tuple.getBtolMetaNode().hasAttachments(); 
			if (metaNodeHasAttachments || btolHasAttachments || getIncludeNodesWithoutAttachments()) {
				MetaNodePreparer metaNodePrep = new MetaNodePreparer(tuple.getMetaNode());
				long currentTime = System.currentTimeMillis();
				Element node = metaNodePrep.toElement();
				System.out.println("\t building meta node element took: " + (System.currentTimeMillis() - currentTime));
//				BtolMetaNodePreparer btolMetaPrep = new BtolMetaNodePreparer(tuple.getBtolMetaNode());
//				if (btolHasAttachments) {
//					node.appendChild(btolMetaPrep.toElement());
//				}
				// todo - get btol node preparer to inject the btol meta data as last child of meta node
				root.appendChild(node);
			}
		}
	}
	
	/**
	 * @return the metaNodeMap
	 */
	public HashMap<Long, MetaNodeTuple> getMetaNodeMap() {
		return metaNodeMap;
	}

	/**
	 * @param metaNodeMap the metaNodeMap to set
	 */
	public void setMetaNodeMap(HashMap<Long, MetaNodeTuple> metaNodeMap) {
		this.metaNodeMap = metaNodeMap;
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
	 * @return the includeNodesWithoutAttachments
	 */
	public boolean getIncludeNodesWithoutAttachments() {
		return includeNodesWithoutAttachments;
	}

	/**
	 * @param includeNodesWithoutAttachments the includeNodesWithoutAttachments to set
	 */
	public void setIncludeNodesWithoutAttachments(boolean includeNodesWithoutAttachments) {
		this.includeNodesWithoutAttachments = includeNodesWithoutAttachments;
	}

}
