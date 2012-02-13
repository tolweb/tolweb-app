package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

/**
 * The goal of this page is to perform a check to see if the 
 * nodes in a clade have the right "page relations" - that is, 
 * the page-id that appears in the Node table is the page a 
 * node appears on, even if a node has an attached page - it 
 * will still indicate the page that it appears on as listed 
 * taxa. 
 * 
 * That is what is true.  But we need to make sure that a node 
 * is corrected specifying this.  The above *SHOULD* be true, 
 * but in the case of doing custom taxa imports - this is not 
 * holding true. 
 * 
 * This page will execute this "validity check" - and correct 
 * the nodes that are not appropriately stating their page 
 * appearance. 
 * 
 * @author lenards
 *
 */
public abstract class PageValidityCheck extends BasePage 
	implements BaseInjectable, PageInjectable, NodeInjectable, PageBeginRenderListener {
	
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
		
		TaxaImportCheck.performPageValidityCheck(getCurrentNodeId(), getMiscNodeDAO(), getWorkingNodeDAO(), getWorkingPageDAO(), getNodePusher(), getOutputLog());
	}
}
