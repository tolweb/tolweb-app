package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.MetaNode;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class NodeLoad extends BasePage implements NodeInjectable,
		MetaInjectable {

	public abstract Long getNodeId();
	public abstract void setNodeId(Long nodeId);
	
	public void doLoad() {
		
	}
	
	public String getNodeInfo() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getNodeId(), true);
		return (mnode != null) ? mnode.toString() : "[node is null]";
	}
	
	public String getMetaNodeInfo() {
		List<Long> fauxIds = new ArrayList<Long>();
		fauxIds.add(getNodeId());
		List<MetaNode> mnodes = getMetaNodeDAO().getMetaNodes(fauxIds, true);
		MetaNode mnode = (!mnodes.isEmpty()) ? mnodes.get(0) : null; 
		return (mnode != null) ? mnode.toString() : "[meta node is null]";
	}	
}
