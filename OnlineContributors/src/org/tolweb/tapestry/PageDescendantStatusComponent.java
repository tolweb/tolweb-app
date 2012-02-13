package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.dao.PageDAO;
import org.tolweb.presentation.helper.PageStatusInfo;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

public abstract class PageDescendantStatusComponent extends BaseComponent implements PageInjectable, NodeInjectable {

    @Parameter
	public abstract Long getPageId();
    @Parameter
    public abstract Long getNodeId();
    
    public abstract int getBranchPageComplete();
    public abstract void setBranchPageComplete(int val);
    public abstract int getBranchPageUnderConstruction();
    public abstract void setBranchPageUnderConstruction(int val);
    public abstract int getBranchPageTemporary();
    public abstract void setBranchPageTemporary(int val);
    
    public abstract int getBranchPageTotal();
    public abstract void setBranchPageTotal(int total);
    
    public abstract int getLeafPageComplete();
    public abstract void setLeafPageComplete(int val);
    public abstract int getLeafPageUnderConstruction();
    public abstract void setLeafPageUnderConstruction(int val);
    public abstract int getLeafPageTemporary();
    public abstract void setLeafPageTemporary(int val);
    
    public abstract int getLeafPageTotal();
    public abstract void setLeafPageTotal(int total);
    
    public abstract PageStatusInfo getPageStatusInfo();
    public abstract void setPageStatusInfo(PageStatusInfo info);
   
	@Override
	protected void renderComponent(IMarkupWriter arg0, IRequestCycle arg1) {
		if (getPageId() != null && getNodeId() != null) {
			setPageStatusInfo(new PageStatusInfo());
			computeStatusInfo();
		}
		super.renderComponent(arg0, arg1);
	}    
	
	private void computeStatusInfo() {
		computePublicStatusStats();
		computeWorkingStatusStats();
		// get all the descendants nodes from this point up in the tree - this includes unnamed nodes
	}
	
	private void computePublicStatusStats() {
		computeBranchStatusStats(getPublicPageDAO(), getPageStatusInfo().getPublicScope());
		computeLeafStatusStats(getPublicPageDAO(), getPageStatusInfo().getPublicScope());
	}
	
	private void computeWorkingStatusStats() {
		computeBranchStatusStats(getWorkingPageDAO(), getPageStatusInfo().getWorkingScope());
		computeLeafStatusStats(getWorkingPageDAO(), getPageStatusInfo().getWorkingScope());
	}	
	
	private void computeLeafStatusStats(PageDAO currDAO, PageStatusInfo.PageScope currScope) {
		int leafComplete = currDAO.getLeafPagesLeadToCompletePages(getPageId());
		currScope.getLeafPage().setPageComplete(leafComplete); 
		
		int leafUnderConst = currDAO.getLeafPagesLeadToUnderConstructionPages(getPageId());
		currScope.getLeafPage().setPageUnderConstruction(leafUnderConst);
		
		int leafTemp = currDAO.getLeafPagesLeadToTemporaryPages(getPageId());
		currScope.getLeafPage().setPageTemporary(leafTemp);
	}
	
	private void computeBranchStatusStats(PageDAO currDAO, PageStatusInfo.PageScope currScope) {
		int branchComplete = currDAO.getBranchPagesLeadToCompletePages(getPageId());
		currScope.getBranchPage().setPageComplete(branchComplete);
		
		int branchUnderConst = currDAO.getBranchPagesLeadToUnderConstructionPages(getPageId());
		currScope.getBranchPage().setPageUnderConstruction(branchUnderConst);
		
		int branchTemp = currDAO.getBranchPagesLeadToTemporaryPages(getPageId());
		currScope.getBranchPage().setPageTemporary(branchTemp);
	}
}
