/*
 * Created on Apr 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Iterator;

import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.tolweb.hibernate.MappedNode;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MediaNodeAttachmentsBox extends EditMediaComponent { 
    public IPrimaryKeyConvertor getConvertor() {
        return new NodeConvertor();
    }
    
	private class NodeConvertor implements IPrimaryKeyConvertor {
		public Object getPrimaryKey(Object objValue) {
			return ((MappedNode) objValue).getNodeId();
		}
		@SuppressWarnings("unchecked")
		public Object getValue(Object objPrimaryKey) {
		    Long nodeId = (Long) objPrimaryKey;
		    for (Iterator iter = getMedia().getNodesSet().iterator(); iter.hasNext();) {
	            MappedNode node = (MappedNode) iter.next();
	            Long nextNodeId = node.getNodeId();
	            if (nextNodeId.equals(nodeId)) {
	                return node;
	            }
	        }
		    return null;
		}
	}
}
