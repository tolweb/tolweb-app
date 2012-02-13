package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.AutoReconciliationRecord;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.TaxaImportRecord;
import org.tolweb.misc.MetaNode;
import org.tolweb.misc.NodeHelper;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

public abstract class ViewAutoReconciliationRecord extends BasePage implements IExternalPage, MetaInjectable, NodeInjectable {

	@InjectPage("taxaimport/SwapNodes")
	public abstract SwapNodes getSwapNodesPage();
	
	public abstract List<AutoReconciliationRecord> getRecords(); 
	public abstract void setRecords(List<AutoReconciliationRecord> records);

	public abstract List<AutoReconciliationRecord> getMatchedRecords(); 
	public abstract void setMatchedRecords(List<AutoReconciliationRecord> records);

	public abstract List<AutoReconciliationRecord> getNewRecords(); 
	public abstract void setNewRecords(List<AutoReconciliationRecord> records);
		
	public abstract AutoReconciliationRecord getCurrentRecord();
	public abstract void setCurrentRecord(AutoReconciliationRecord record);
	
	public abstract AutoReconciliationRecord getCurrentMatchedRecord();
	public abstract void setCurrentMatchedRecord(AutoReconciliationRecord record);

	public abstract AutoReconciliationRecord getCurrentNewRecord();
	public abstract void setCurrentNewRecord(AutoReconciliationRecord record);

	public abstract List<SwapTuple> getSwapTuples();
	public abstract void setSwapTuples(List<SwapTuple> tuples);
	
	public abstract SwapTuple getCurrentTuple();
	public abstract void setCurrentTuple(SwapTuple tuple);	
	
	public abstract Long getCurrentBasalNodeId();
	public abstract void setCurrentBasalNodeId(Long id);
	
	public String getCurrentBasalNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentBasalNodeId());
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[unavailable]";
	}	
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		if (args.length == 2) {
			Long basalNodeId = (Long)args[0];
			setCurrentBasalNodeId(basalNodeId);
			TaxaImportRecord record = getTaxaImportLogDAO().getLatestTaxaImportRecordWithNodeId(basalNodeId);
			initializeRecord(record);
			initialOrphanStructures();
		}		
	}

	private void initializeRecord(TaxaImportRecord record) {
		if (record != null) {
			List<AutoReconciliationRecord> records = getAutoReconciliationLogDAO().getLatestAutoReconciliationRecordsWithId(record.getId());
			setRecords(records);
			if (records != null && !records.isEmpty()) {
				setCurrentRecord(getRecords().get(0));
			}
			
			List<AutoReconciliationRecord> matchedRecords = getAutoReconciliationLogDAO().getLatestMatchedAutoReconciliationRecordsWithId(record.getId());
			setMatchedRecords(matchedRecords);
			List<AutoReconciliationRecord> newRecords = getAutoReconciliationLogDAO().getLatestNewAutoReconciliationRecordsWithId(record.getId());
			setNewRecords(newRecords);

		}
	}
	
	private void initialOrphanStructures() {
		if (getCurrentBasalNodeId() != null) {
			List<MappedNode> inactiveNodes = NodeHelper.getInactiveNodesForClade(getCurrentBasalNodeId(), getMiscNodeDAO(), getWorkingNodeDAO());
			List<Long> inactiveNodeIds = new ArrayList<Long>();
			for (MappedNode mnode : inactiveNodes) {
				inactiveNodeIds.add(mnode.getNodeId());
			}
			List<MetaNode> metaNodes = getMetaNodeDAO().getMetaNodes(inactiveNodeIds, true);
			createTupleStructure(metaNodes);
		}		
	}
	
	private void createTupleStructure(List<MetaNode> metaNodes) {
		List<SwapTuple> tuples = new ArrayList<SwapTuple>();
		for (MetaNode mnode : metaNodes) {
			tuples.add(new SwapTuple(mnode));
		}
		setSwapTuples(tuples);
	}	

	public SwapNodes manageLink(Long currentBasalNodeId) {
		getSwapNodesPage().setBasalNodeId(currentBasalNodeId);
		return getSwapNodesPage();
	}
	
	public abstract NodeImage getCurrentMediaFile();
	public abstract void setCurrentMediaFile(NodeImage mediaFile);

	public abstract Contributor getCurrentContributor(); 
	public abstract void setCurrentContributor(Contributor contr);
	
	public abstract MappedAccessoryPage getCurrentAccessoryPage();
	public abstract void setCurrentAccessoryPage(MappedAccessoryPage accPage);
	
	public abstract Long getCurrentOtherNameId();
	public abstract void setCurrentOtherNameId(Long id);
	
	public String getCurrentOtherNameText() {
		return " - " + getCurrentTuple().getMetaNode().getOtherNameIds().get(getCurrentOtherNameId());
	}	
}
