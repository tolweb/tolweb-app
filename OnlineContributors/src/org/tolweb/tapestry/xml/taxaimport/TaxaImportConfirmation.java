package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.AutoReconciliationRecord;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.TaxaImportRecord;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

public abstract class TaxaImportConfirmation extends BasePage implements PageBeginRenderListener, BaseInjectable, NodeInjectable, PageInjectable, MetaInjectable {
	public abstract String getHeading();
	public abstract void setHeading(String heading);
	
	public abstract String getMessage();
	public abstract void setMessage(String msg);
	
	public abstract Boolean getHasErrors();
	public abstract void setHasErrors(Boolean b);

	public abstract Long getBasalNodeId();
	public abstract void setBasalNodeId(Long id);
	
	public abstract List<MappedNode> getReactivatedNodes();
	public abstract void setReactivatedNodes(List<MappedNode> nodes);

	public abstract List<MappedNode> getNewNodes();
	public abstract void setNewNodes(List<MappedNode> nodes);
	
	public abstract TaxaImportRecord getTaxaImportRecord();
	public abstract void setTaxaImportRecord(TaxaImportRecord record);
	
	public abstract List<String> getOutputLog();
	public abstract void setOutputLog(List<String> log);
	
	public abstract String getCurrentEntry();
	public abstract void setCurrentEntry(String entry);
	
	public void pageBeginRender(PageEvent event) {
		// for performance testing - to estimate the run time of the import because we're having timeout issues. 
		long startTime = System.currentTimeMillis();
		System.out.println("#TAXA-IMPORT-CONFIRMATION start: " + startTime + " Date:" + (new Date()));
		
		if (getBasalNodeId() != null) {
			List<String> outputLog = new ArrayList<String>();
			outputLog.add("<h5>Page Validity Check:</h5>");
			TaxaImportCheck.performPageValidityCheck(getBasalNodeId(), getMiscNodeDAO(), getWorkingNodeDAO(), 
													 getWorkingPageDAO(), getNodePusher(), outputLog);
			outputLog.add("<h5>Extinct Check:</h5>");
			TaxaImportCheck.performExtinctCheck(getBasalNodeId(), getMiscNodeDAO(), getWorkingNodeDAO(), 
													 getNodePusher(), outputLog);
			setOutputLog(outputLog);
		}
		completeLogging();

		long stopTime = System.currentTimeMillis();
		System.out.println("#TAXA-IMPORT-CONFIRMATION stop: " + stopTime + " Date:" + (new Date()));
		System.out.println("#TAXA-IMPORT-CONFIRMATION duration: " + (stopTime - startTime));		
	}
	
	public String getGroupName() {
		MappedNode grp = getWorkingNodeDAO().getNodeWithId(getBasalNodeId());
		return (grp != null) ? grp.getName() : ""; 
	}
	
	public String getWorkingURL() {
		return getUrlBuilder().getWorkingURLForBranchPage(getGroupName(), getBasalNodeId());		
	}
	
	public void doPageValidityCheckRedirect(IRequestCycle cycle) {
		PageValidityCheck redirPage = (PageValidityCheck)cycle.getPage("taxaimport/PageValidityCheck");
		redirPage.setCurrentNodeId(getBasalNodeId());
		cycle.activate(redirPage);
	}
	
	private void completeLogging() {
		if (getTaxaImportRecord() != null) {
			Long taxaImportId = getTaxaImportRecord().getId();
			for (MappedNode nd : getReactivatedNodes()) {
				AutoReconciliationRecord record = new AutoReconciliationRecord(taxaImportId, nd, AutoReconciliationRecord.MATCHED_NODE);
				getAutoReconciliationLogDAO().createAutoReconciliationRecord(record);
			}
			for (MappedNode nd : getNewNodes()) {
				AutoReconciliationRecord record = new AutoReconciliationRecord(taxaImportId, nd, AutoReconciliationRecord.NEW_NODE);
				if (nd != null && nd.getNodeId() != null) {
					getAutoReconciliationLogDAO().createAutoReconciliationRecord(record);
				}
			}
		}
	}
}
