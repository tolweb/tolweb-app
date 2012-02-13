package org.tolweb.content.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.StringUtils;

/**
 * Defines operations of a general taxon list maker. 
 * 
 * @author lenards
 *
 */
public abstract class TaxonListMaker {
	private MappedNode basalNode;
	
	/**
	 * Constructs an instance of the list maker 
	 * 
	 * @param basalNode the root of the clade
	 * @param nodes the nodes included in the clade
	 */
	public TaxonListMaker(MappedNode basalNode, List<MappedNode> nodes) {
		this.basalNode = basalNode;
		if (!nodes.contains(basalNode)) {
			nodes.add(basalNode);
		}
		initializeInternalStructures(nodes);
	}
	
	/**
	 * Create an internal representation of the clade structure.
	 * 
	 * @param nodes nodes present in the clade 
	 */
	private void initializeInternalStructures(List<MappedNode> nodes) {
		Map<Long, MappedNode> idsToNodes = initializeIdsToNodes(nodes);
		initializeCladeRepresentation(idsToNodes, nodes);
	}

	
	private void initializeCladeRepresentation(Map<Long, MappedNode> idsToNodes, List<MappedNode> nodes) {
        for (Iterator<MappedNode> iter = nodes.iterator(); iter.hasNext();) {
            MappedNode nextNode = iter.next();
            // get our parent and add ourselves to the children of the parent
            MappedNode parent = idsToNodes.get(nextNode.getParentNodeId());
            if (parent != null && (parent.getChildren() != null && !parent.getChildren().contains(nextNode))) {
                parent.addToChildren(nextNode);
            }
        }
	}

	/**
	 * Creates map representing the node ids mapped to node instance
	 * 
	 * @param nodes the nodes to create the map representation. 
	 * @return a map defining a mapping of node ids to node instances
	 */
	private Map<Long, MappedNode> initializeIdsToNodes(List<MappedNode> nodes) {
		Map<Long, MappedNode> idsToNodes = new HashMap<Long, MappedNode>();
        for (Iterator<MappedNode> itr = nodes.iterator(); itr.hasNext(); ) {
        	MappedNode nextNode = itr.next();
        	idsToNodes.put(nextNode.getNodeId(), nextNode);
        }
        return idsToNodes;
	}

	/**
	 * Returns the starting text for a list.
	 * 
	 * @return a string representing the starting text for a list
	 */
	public abstract String listTextStart();
	
	/**
	 * Returns the closing text for a list.
	 * 
	 * @return a string representing the closing text for a list
	 */
	public abstract String listTextClose();
	
	/**
	 * Returns the starting text for an item in the list.
	 * 
	 * @return a string representing the starting text for an item
	 */
	public abstract String itemTextStart();
	
	/**
	 * Returns the closing text for an item in the list.
	 * 
	 * @return a string representing the closing text for an item
	 */
	public abstract String itemTextClose();

	/**
	 * Creates a string representation of the list and returns it 
	 * @return a string representation of the list
	 */
	public String createList() {
		StringBuilder list = new StringBuilder();
		list.append(listTextStart());
		traverseClade(basalNode, list);
		list.append(listTextClose());
		return list.toString();
	}

	/**
	 * Applies processing to transform the node name 
	 * @param mnode the node to process
	 * @return a node name with processing applied  
	 */
	public String processNodeName(MappedNode mnode) {
		return mnode.getName();
	}

	/**
	 * Performs a complete traversal of the represented clade. 
	 * 
	 * @param curr the current mapped node in the traversal
	 * @param buff the string representation built as the clade is traversed
	 */
	@SuppressWarnings("unchecked")
	protected void traverseClade(MappedNode curr, StringBuilder buff) {
		if (curr != null) {
			boolean emptyOrInternal = StringUtils.notEmpty(curr.getName());			
			if (emptyOrInternal) {
				buff.append(itemTextStart());
				buff.append(processNodeName(curr));
			}
			List children = curr.getChildren();
			if (!children.isEmpty()) {
				if (emptyOrInternal) {
					buff.append(listTextStart());
				}
				for (Iterator itr = children.iterator(); itr.hasNext(); ) {
					MappedNode child = (MappedNode)itr.next();
					traverseClade(child, buff);
				}
				if (emptyOrInternal) {
					buff.append(listTextClose());
				}
			}
			if (emptyOrInternal) {
				buff.append(itemTextClose());
			}			
		}
	}
}
