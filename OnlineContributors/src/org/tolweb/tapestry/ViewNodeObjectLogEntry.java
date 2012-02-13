package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.NodeObjectManagementRecord;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class ViewNodeObjectLogEntry extends BasePage implements IExternalPage, MetaInjectable, NodeInjectable {
	
	public abstract MappedNode getSourceNode();
	public abstract void setSourceNode(MappedNode node);
	
	public abstract MappedNode getDestNode();
	public abstract void setDestNode(MappedNode node);
	
	public abstract NodeObjectManagementRecord getCurrentRecord();
	public abstract void setCurrentRecord(NodeObjectManagementRecord record);
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		if (parameters.length == 1) {
			Long id = (Long)parameters[0];
			NodeObjectManagementRecord record = getNodeObjectManagementLogDAO().getRecordWithId(id);
			setCurrentRecord(record);
			setSourceNode(getWorkingNodeDAO().getNodeWithId(record.getSourceNodeId(), true));
			setDestNode(getWorkingNodeDAO().getNodeWithId(record.getDestNodeId(), true));
		}
	}
}