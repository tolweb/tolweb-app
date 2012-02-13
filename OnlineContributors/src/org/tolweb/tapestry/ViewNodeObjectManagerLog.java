package org.tolweb.tapestry;

import java.util.List;

import org.tolweb.dao.BaseDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.NodeObjectManagementRecord;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class ViewNodeObjectManagerLog extends AbstractViewAllObjects implements
		MetaInjectable, NodeInjectable {

	public abstract NodeObjectManagementRecord getCurrentRecord();
	public abstract void setCurrentRecord(NodeObjectManagementRecord record);
	
	public String getCurrentSourceNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentRecord().getSourceNodeId(), true);
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[unavailable]";
	}

	public String getCurrentDestNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentRecord().getDestNodeId(), true);
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[unavailable]";
	}
	
	@Override
	public BaseDAO getDAO() {
		return getTaxaImportLogDAO();
	}

	@Override @SuppressWarnings("unchecked")
	public Class getObjectClass() {
		return NodeObjectManagementRecord.class;
	}

	public List<NodeObjectManagementRecord> getRecords() {
		return getNodeObjectManagementLogDAO().getAllRecords(true);
	}
	
    public String getColumnsString() {
    	String colsString =  "ID:id, !sourceNodeId, !destNodeId, modified By:modifiedBy, timestamp:timestamp, !view";
    	return colsString;
    }
    
    public String getTableId() {
    	return "nodeObjectManagementRecordsTable";
    }	
}
