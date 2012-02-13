package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class ViewObjectManagement extends BasePage implements PageBeginRenderListener, NodeInjectable, BaseInjectable, MetaInjectable {

	public abstract Long getBasalNodeId();
	public abstract void setBasalNodeId(Long nodeId);
	
	public abstract Long getCurrentBasalNodeId();
	public abstract void setCurrentBasalNodeId(Long nodeId);
	
	@InjectPage("taxaimport/SwapNodes")
	public abstract SwapNodes getSwapNodesPage();
	@InjectPage("taxaimport/ViewObjectManagement")
	public abstract ViewObjectManagement getViewObjectManagementPage();
	@InjectPage("taxaimport/SwapNodesConfirmation")
	public abstract SwapNodesConfirmation getSwapNodesConfirmationPage();
	
	public abstract List<Object[]> getRecords();
	public abstract void setRecords(List<Object[]> records);
	
	public abstract Object[] getCurrentRecord();
	public abstract void setCurrentRecord(Object[] currRecord);
	
	public String getCurrentRecordNode() {
		return getNodeName(getCurrentRecordNodeId());
	}

	public Long getCurrentRecordNodeId() {
		return (Long)getCurrentRecord()[0];
	}
	
	public String getCurrentRecordUpload() {
		return getCurrentRecord()[1].toString();
	}
	
	public String getCurrentRecordTimestamp() {
		return getCurrentRecord()[2].toString();
	}
	
	public void pageBeginRender(PageEvent event) {
		List<Object[]> records = getTaxaImportLogDAO().getLatestTaxaImportRecords();
		setRecords(records);
	}
	
	public String getCurrentBasalNodeName() {
		return getNodeName(getCurrentBasalNodeId());
	}	
	
	public String getWorkingURL() {
		if ("[none]".equals(getCurrentBasalNodeName()) || 
			"[unavailable]".equals(getCurrentBasalNodeName())) {
			return "#";
		}
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentBasalNodeId(), true);
		return getUrlBuilder().getWorkingURLForBranchPage(mnode.getName(), getCurrentBasalNodeId());		
	}	

	public String getRecordURL() {
		if ("[none]".equals(getCurrentRecordNode()) || 
			"[unavailable]".equals(getCurrentRecordNode())) {
			return "#";
		}
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getCurrentRecordNodeId(), true);
		return getUrlBuilder().getWorkingURLForBranchPage(mnode.getName(), getCurrentRecordNodeId());		
	}	
	
	public String getNodeName(Long nodeId) {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(nodeId, true);
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[none]";		
	}
	
	public List<Long> getNodeIds() {
		ArrayList<Long> nodeIds = new ArrayList<Long>();
		nodeIds.add(getCurrentBasalNodeId());
		return nodeIds;
	}
	
	public void doSearch(IRequestCycle cycle) {
		setCurrentBasalNodeId(getBasalNodeId());
		cycle.activate(this);
	}	
	
	public SwapNodes manageLink(Long currentBasalNodeId) {
		getSwapNodesPage().setBasalNodeId(currentBasalNodeId);
		getSwapNodesPage().setRefreshTuples(true);
		return getSwapNodesPage();
	}

	public ViewObjectManagement viewLink(Long currentNodeId) {
		getViewObjectManagementPage().setCurrentBasalNodeId(currentNodeId);
		return getViewObjectManagementPage();
	}	
	
	public SwapNodesConfirmation objectRecLink(Long currentNodeId) {
		getSwapNodesConfirmationPage().setBasalNodeId(currentNodeId);
		return getSwapNodesConfirmationPage();
	}
	
    public String getColumnsString() {
    	String colsString =  "!basalNode, !objectManifest, !taxaImport, !autoRecLog, !objectRecLog, !manage";
    	return colsString;
    }
    
    public String getRecentColumnsString() {
    	String colsString =  "!basalNode2, !uploadedBy, !timestamp, !view";
    	return colsString;
    }
    
    public String getTableId() {
    	return "objectManagementTable";
    }	

    public PopupLinkRenderer getRenderer() {
    	int width = 750;
    	int height = 550;
    	return getRendererFactory().getLinkRenderer("", width, height);
    }    
}
