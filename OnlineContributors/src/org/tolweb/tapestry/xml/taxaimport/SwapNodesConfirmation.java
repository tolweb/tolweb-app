package org.tolweb.tapestry.xml.taxaimport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.ManualReconciliationRecord;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.NodeRetirementRecord;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class SwapNodesConfirmation extends BasePage implements PageBeginRenderListener, NodeInjectable, PageInjectable, MetaInjectable {

	public abstract int getIndex();
	
	@Persist("session")
	public abstract Long getBasalNodeId();
	public abstract void setBasalNodeId(Long id);
	
	@InjectPage("taxaimport/SwapNodes")
	public abstract SwapNodes getSwapNodesPage();	

	public abstract List<String> getOutputLog();
	public abstract void setOutputLog(List<String> log);	
	
	public String getCurrentBasalNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getBasalNodeId(), true);
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[unavailable]";
	}
	
	public String getCurrentRetiredCladeName() {
		if (getCurrentNodeRetirementRecord() != null) {
			MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentNodeRetirementRecord().getRetiredFromClade(), true);
			if (mnode != null) {
				return mnode.getName() + " (" + mnode.getNodeId() + ")";
			}
		}
		return "[unavailable]";
	}
	
	public SwapNodes manageLink(Long currentBasalNodeId) {
		getSwapNodesPage().setBasalNodeId(currentBasalNodeId);
		return getSwapNodesPage();
	}	
	
	public abstract List<ManualReconciliationRecord> getManualReconcileRecords();
	public abstract void setManualReconcileRecords(List<ManualReconciliationRecord> records);
	
	public abstract ManualReconciliationRecord getCurrentManualReconcileRecord();
	public abstract void setCurrentManualReconcileRecord(ManualReconciliationRecord record);
	
	public abstract String getCurrentSourceManifestOutput();
	public abstract void setCurrentSourceManifestOutput(String out);
	
	public String getCurrentSourceManifestXml() {
		if (StringUtils.isEmpty(getCurrentSourceManifestOutput())) {
	        try {
	    		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();            
	        	Builder parser = new Builder();
	            Document doc = parser.build(new StringReader(getCurrentManualReconcileRecord().getSourceNodeManifest()));
	            Serializer serializer = new Serializer(fileOut, "ISO-8859-1");
	            serializer.setIndent(4);
	            serializer.setMaxLength(64);
	            serializer.setPreserveBaseURI(false);
	            serializer.write(doc);
	            serializer.flush();
	            setCurrentSourceManifestOutput(fileOut.toString());
	        } catch (ParsingException ex) {
	            System.out.println("source manifest is not well-formed: \n" + getCurrentManualReconcileRecord().getSourceNodeManifest());
	            System.out.println(ex.getMessage());
	        } catch (IOException ioe) { }
		}
        return getCurrentSourceManifestOutput();
	}
	
	public abstract String getCurrentTargetManifestOutput();
	public abstract void setCurrentTargetManifestOutput(String out);
	
	public boolean getTargetHasInfo() {
		return StringUtils.notEmpty(getCurrentManualReconcileRecord().getTargetNodeManifest()) &&
			getCurrentManualReconcileRecord().getTargetNodeId() != null && 
			StringUtils.notEmpty(getCurrentManualReconcileRecord().getTargetNodeName());
	}
	
	public String getCurrentTargetManifestXml() {
		if (StringUtils.isEmpty(getCurrentTargetManifestOutput())) {
	        try {
	    		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();            
	        	Builder parser = new Builder();
	            Document doc = parser.build(new StringReader(getCurrentManualReconcileRecord().getTargetNodeManifest()));
	            Serializer serializer = new Serializer(fileOut, "ISO-8859-1");
	            serializer.setIndent(4);
	            serializer.setMaxLength(64);
	            serializer.setPreserveBaseURI(false);
	            serializer.write(doc);
	            serializer.flush();
	            setCurrentTargetManifestOutput(fileOut.toString());
	        } catch (ParsingException ex) {
	            System.out.println("source manifest is not well-formed: \n" + getCurrentManualReconcileRecord().getTargetNodeManifest());
	            System.out.println(ex.getMessage());
	        } catch (IOException ioe) { }
		}
        return getCurrentTargetManifestOutput();
	}
	
	
	
	public abstract List<NodeRetirementRecord> getNodeRetirementRecords();
	public abstract void setNodeRetirementRecords(List<NodeRetirementRecord> records);

	public abstract NodeRetirementRecord getCurrentNodeRetirementRecord();
	public abstract void setCurrentNodeRetirementRecord(NodeRetirementRecord record);
	
	public void pageBeginRender(PageEvent event) {
		if (areRecordsEmpty()) {
			// use basal node id to fetch records for display
			List<NodeRetirementRecord> nodeRetirementRecords = getNodeRetirementLogDAO().getRetiredNodesForClade(getBasalNodeId());
			setNodeRetirementRecords(nodeRetirementRecords);
			List<ManualReconciliationRecord> manualRecRecords = getManualReconciliationLogDAO().getManualReconciliationRecordWithBasalNodeId(getBasalNodeId());
			setManualReconcileRecords(manualRecRecords);
		}
		doPageValidityCheck();		
		filterOutInternalNodes();
	}

	public String getOutputLogString() {
		String output = StringUtils.returnNewLineJoinedString(getOutputLog());
		return StringUtils.notEmpty(output) ? output: "[no results]";
	}
	
	private void doPageValidityCheck() {
		List<String> outputLog = new ArrayList<String>();
		TaxaImportCheck.performPageValidityCheck(getBasalNodeId(), getMiscNodeDAO(), getWorkingNodeDAO(), 
												 getWorkingPageDAO(), getNodePusher(), outputLog);
		setOutputLog(outputLog);
	}
	
	private void filterOutInternalNodes() {
		List<NodeRetirementRecord> toRemove = new ArrayList<NodeRetirementRecord>();
		for (NodeRetirementRecord nrr : getNodeRetirementRecords()) {
			if (StringUtils.isEmpty(nrr.getNodeName())) {
				toRemove.add(nrr);
			}
		}
		getNodeRetirementRecords().removeAll(toRemove);
	}
	
	private boolean areRecordsEmpty() {
		return areManualReconcileRecordsEmpty() && areNodeRetirementRecordsEmpty();
	}

	private boolean areManualReconcileRecordsEmpty() {
		return getManualReconcileRecords() == null || getManualReconcileRecords().isEmpty();
	}
	
	private boolean areNodeRetirementRecordsEmpty() {
		return getNodeRetirementRecords() == null || getNodeRetirementRecords().isEmpty();
	}
}
