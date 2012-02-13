package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Lifecycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.selectionmodels.BranchLeafSelectionModel;
import org.tolweb.tapestry.selectionmodels.ConfidenceSelectionModel;
import org.tolweb.tapestry.selectionmodels.ExtinctSelectionModel;
import org.tolweb.tapestry.selectionmodels.PhylesisSelectionModel;
import org.tolweb.tapestry.selectionmodels.SubgroupsSelectionModel;
import org.tolweb.tapestry.selectionmodels.TrunkSelectionModel;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.Download;

public abstract class EditGroupProperties extends AbstractNodeEditingPage
		implements NodeInjectable, PageBeginRenderListener, TreeGrowServerInjectable {

	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract ExtinctSelectionModel getExtinctModel();
	
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract TrunkSelectionModel getTrunkModel();
	
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract PhylesisSelectionModel getPhylesisModel();
	
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract ConfidenceSelectionModel getConfidenceModel();

	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract SubgroupsSelectionModel getSubgroupsModel();
	
	@Bean(lifecycle = Lifecycle.PAGE)
	public abstract BranchLeafSelectionModel getBranchLeafModel();	
	
	@Persist("session")
	public abstract boolean getIsDownloaded();
	public abstract void setIsDownloaded(boolean locked);
	
	@Persist("session")
	public abstract Contributor getDownloadContributor();
	public abstract void setDownloadContributor(Contributor contr);
	
	@Persist("session")
	public abstract Date getDownloadDate();
	public abstract void setDownloadDate(Date date);
	
	@Persist("session")
	public abstract boolean getDisableEdit();
	public abstract void setDisableEdit(boolean canEdit);
	
	@Persist("session")
	public abstract boolean getIsLeafEligible();
	public abstract void setIsLeafEligible(boolean couldBeLeaf);
	
	
	public String getShowDownloadMessage() {
		return !getDisableEdit() ? "hide" : "show";
	}
	
	@SuppressWarnings("unchecked")
	public void pageBeginRender(PageEvent event) {
		setDisableEdit(false);
		if (getNode() != null) {
			determineLeafEligibility();
			
			boolean isActive = determineCheckoutStatus();
			
			if (isActive) {
				disableEditingComponents();
			}
		}
	}

	private void disableEditingComponents() {
		// okay, it's active, let's figure out who's got it downloaded.  
		Download dl = getDownloadDAO().getOpenDownloadForNode(getNode().getNodeId());
		setDownloadContributor(dl.getContributor());
		setDownloadDate(dl.getDownloadDate());
		// let's disable the editing controls
		setDisableEdit(true);
	}

	@SuppressWarnings("unchecked")
	private boolean determineCheckoutStatus() {
		// is the node being actively edited? 
		List currNode = new ArrayList();
		currNode.add(getNode().getNodeId());
		List downloadedNodeIds = getDownloadDAO().getNodesAreDownloaded(currNode);
		boolean isActive = downloadedNodeIds != null && !downloadedNodeIds.isEmpty();
		setIsDownloaded(isActive);
		return isActive;
	}

	private void determineLeafEligibility() {
		// does the node have children? if not, it's leaf eligible

		// remember - a node is always a descendants of itself, so we need 
		// to remove the current node from what's returned so avoid the 
		// leaf eligible checks from failing
		Set descendants = getMiscNodeDAO().getDescendantIdsForNode(getNode().getNodeId());
		if (descendants != null) {
			descendants.remove(getNode().getNodeId());
		}
		boolean leafEligible = descendants == null || descendants.isEmpty();
		setIsLeafEligible(leafEligible);
	}
	
	public String getDownloadedMessage() {
		if (getIsDownloaded() && getDownloadContributor() != null) {
			Contributor contr = getDownloadContributor();
			String contributorEmail = "<a href=\"mailto:" + contr.getEmail() + "\">" + contr.getDisplayName() + "</a>";
			return "This node has been checked out by " + contributorEmail + " on " + StringUtils.getGMTDateString(getDownloadDate()) + ".";
		}
		return "";

	}
	
	public int getExtinct() {
		if (getNode().getExtinct() == MappedNode.EXTINCT) {
			return ExtinctSelectionModel.EXTINCT;
		} else {
			return ExtinctSelectionModel.NOT_EXTINCT;
		}
	}
	
	public void setExtinct(int val) {
		if (val == ExtinctSelectionModel.EXTINCT) {
			getNode().setExtinct(MappedNode.EXTINCT);
		} else {
			getNode().setExtinct(MappedNode.NOT_EXTINCT);
		}
	}
	
	public int getSubgroups() {
		return getNode().getHasIncompleteSubgroups() ? 
				SubgroupsSelectionModel.INCOMPLETE_SUBGROUPS : 
				SubgroupsSelectionModel.ALL_SUBGROUPS;
	}
	
	public void setSubgroups(int val) {
		getNode().setHasIncompleteSubgroups(val == SubgroupsSelectionModel.INCOMPLETE_SUBGROUPS);
	}

	public int getBranchLeaf() {
		return getNode().getIsLeaf() ? BranchLeafSelectionModel.LEAF : BranchLeafSelectionModel.BRANCH;
	}
	
	public void setBranchLeaf(int val) {
		getNode().setIsLeaf(val == BranchLeafSelectionModel.LEAF);
	}	
	
	public int getTrunkNode() {
		return getNode().getTrunkNode();
	}
	
	public void setTrunkNode(int val) {
		getNode().setTrunkNode(val);
	}
	
    public void doSave(IRequestCycle cycle) {
        super.doSave(cycle);
        if (getValidationDelegate().getHasErrors()) {
            return;
        }
        savePage();
    }	
	
    protected void savePage() {
        getNodeDAO().saveNode(getNode());        
    } 
}
