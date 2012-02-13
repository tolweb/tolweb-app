package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class ExtinctCheck extends BasePage implements PageBeginRenderListener, BaseInjectable, NodeInjectable {
	public abstract Long getCurrentNodeId();
	public abstract void setCurrentNodeId(Long id);
	
	public abstract List<String> getOutputLog();
	public abstract void setOutputLog(List<String> log);
	
	public abstract String getCurrentEntry();
	public abstract void setCurrentEntry(String entry);
	
	public String getGroupName() {
		MappedNode grp = getWorkingNodeDAO().getNodeWithId(getCurrentNodeId());
		return (grp != null) ? grp.getName() : ""; 
	}
	
	public String getWorkingURL() {
		return getUrlBuilder().getWorkingURLForBranchPage(getGroupName(), getCurrentNodeId());		
	}	
	
	public void pageBeginRender(PageEvent event) {
		setOutputLog(new ArrayList<String>());
		TaxaImportCheck.performExtinctCheck(getCurrentNodeId(), getMiscNodeDAO(), getWorkingNodeDAO(), getNodePusher(), getOutputLog());
	}
}
