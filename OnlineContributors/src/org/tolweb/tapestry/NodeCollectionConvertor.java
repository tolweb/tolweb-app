package org.tolweb.tapestry;

import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.hibernate.MappedNode;

public class NodeCollectionConvertor implements IPrimaryKeyConvertor {
	@SuppressWarnings("unchecked")
	private Collection nodes;
	
	@SuppressWarnings("unchecked")
	public NodeCollectionConvertor(Collection nodesToSearch) {
		this.nodes = nodesToSearch;
	}
	
	public Object getPrimaryKey(Object objValue) {
		return ((MappedNode) objValue).getNodeId();
	}

	@SuppressWarnings("unchecked")
	public Object getValue(Object objPrimaryKey) {
	    Long nodeId = (Long) objPrimaryKey;
	    for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            MappedNode node = (MappedNode) iter.next();
            Long nextNodeId = node.getNodeId();
            if (nextNodeId.equals(nodeId)) {
                return node;
            }
        }
	    return null;
	}
}
