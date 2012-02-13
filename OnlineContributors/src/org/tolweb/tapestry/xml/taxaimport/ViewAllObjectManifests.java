package org.tolweb.tapestry.xml.taxaimport;

import java.util.List;

import org.tolweb.dao.BaseDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.ObjectManifestRecord;
import org.tolweb.tapestry.AbstractViewAllObjects;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class ViewAllObjectManifests extends AbstractViewAllObjects implements MetaInjectable, NodeInjectable {

	public abstract ObjectManifestRecord getCurrentObjectManifestRecord();
	public abstract void setCurrentObjectManifestRecord(ObjectManifestRecord record);
	
	public String getCurrentBasalNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentObjectManifestRecord().getBasalNodeId());
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[none]";
	}
	
	public boolean getShowProgressLink() {
		return getCurrentObjectManifestRecord() != null &&
				getReattachmentProgressDAO().getReattachmentProgress(getCurrentObjectManifestRecord().getKeyValue()) != null;
	}
	
	public String getBasalNodeText() {
		String xml = (getCurrentObjectManifestRecord() != null) ? getCurrentObjectManifestRecord().getManifest() : "";
		
		return null;
	}
	@Override
	public BaseDAO getDAO() {
		return getObjectManifestLogDAO();
	}

	@Override @SuppressWarnings("unchecked")
	public Class getObjectClass() {
		return ObjectManifestRecord.class;
	}

	public List<ObjectManifestRecord> getObjectManifestRecords() {
		return getObjectManifestLogDAO().getAllObjectManifestRecords();
	}
	
    public String getColumnsString() {
    	String colsString =  "!view, ID:id, !basalNode, key:keyValue, updated by:updatedBy, timestamp:timestamp";
    	return colsString;
    }
    
    public String getTableId() {
    	return "objectManifestRecordsTable";
    }
}
