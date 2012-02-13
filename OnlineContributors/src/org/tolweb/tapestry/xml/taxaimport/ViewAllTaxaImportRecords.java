package org.tolweb.tapestry.xml.taxaimport;

import java.util.List;

import org.tolweb.dao.BaseDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.TaxaImportRecord;
import org.tolweb.tapestry.AbstractViewAllObjects;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class ViewAllTaxaImportRecords extends AbstractViewAllObjects implements MetaInjectable, NodeInjectable {

	public abstract TaxaImportRecord getCurrentTaxaImportRecord();
	public abstract void setCurrentTaxaImportRecord(TaxaImportRecord record);
	
	public String getCurrentBasalNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentTaxaImportRecord().getBasalNodeId());
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
		return TaxaImportRecord.class;
	}

	public List<TaxaImportRecord> getTaxaImportRecords() {
		return getTaxaImportLogDAO().getAllTaxaImportRecords(true);
	}
	
    public String getColumnsString() {
    	String colsString =  "!view, ID:id, !basalNode, uploaded by:uploadedBy, timestamp:timestamp";
    	return colsString;
    }
    
    public String getTableId() {
    	return "taxaImportRecordsTable";
    }
}
