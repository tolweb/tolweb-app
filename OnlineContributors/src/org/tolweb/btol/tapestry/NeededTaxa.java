package org.tolweb.btol.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.btol.tapestry.selection.NeededFilterSelectionModel;
import org.tolweb.btol.tapestry.selection.TierFilterSelectionModel;
import org.tolweb.exporter.TabbedNameExporter;
import org.tolweb.tapestry.TaxaIndex;
import org.tolweb.tapestry.TaxonList;

public abstract class NeededTaxa extends TaxaIndex implements IExternalPage {
	@InjectObject("spring:tabbedNameExporter")
	public abstract TabbedNameExporter getExporter();
	
	public abstract void setDisplayString(String value);	

	public void activateExternalPage(Object[] args, IRequestCycle arg1) {
		Long projectId = Long.valueOf(((Number) args[0]).longValue());		
		Long nodeId = Long.valueOf(((Number) args[1]).longValue());
		Integer type = Integer.valueOf(((Number) args[2]).intValue());
		setupNodeIdAndProjectId(nodeId, projectId);		
		doDatabaseFetchingAndInit(getNode(), projectId);
		setTierSelection(TierFilterSelectionModel.TIER_3);
		setNeededSelection(type);
		setShowAllTaxa(false);
		filterNodes();
		// add the root to the 
		// now that the nodes are filtered, get the tabbed export
		String tabbedExportString = getExporter().getTabSeparatedTreeStringForBtol(nodeId, getDisplayedNodes(), getNode());
		setDisplayString(tabbedExportString);
	}
	
	public String getPageTitle() {
		return "BTOL: Specimens Needed for " + getTypeString();
	}
	
	private String getTypeString() {
		int filterType = getNeededSelection();
		if (filterType == NeededFilterSelectionModel.SHOW_NEEDED_ADULTS) {
			return "Adult Morphology";
		} else if (filterType == NeededFilterSelectionModel.SHOW_NEEDED_LARVAE) {
			return "Larval Morphology";
		} else {
			return "DNA";
		}
	}
	public boolean getIsDNA() {
		return getNeededSelection() == NeededFilterSelectionModel.SHOW_NEEDED_DNA;
	}

	public TaxonList getTaxonList() {
		return null;
	}
	/**
	 * overridden since these pages aren't secure
	 */
	public void pageAttached(PageEvent event) {
	}
	public void pageDetached(PageEvent event) {
	}
}
