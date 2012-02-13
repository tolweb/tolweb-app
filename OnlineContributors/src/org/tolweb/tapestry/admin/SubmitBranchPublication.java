package org.tolweb.tapestry.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.AbstractScientificEditorPage;
import org.tolweb.tapestry.RevisionTypeModel;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrowserver.PublicationBatch;

public abstract class SubmitBranchPublication extends AbstractScientificEditorPage implements NodeInjectable,
		UserInjectable, PageInjectable, PageBeginRenderListener, TreeGrowServerInjectable {
	@Bean
	public abstract ValidationDelegate getDelegate();
	public abstract String getBranchPublicationRootName();
	public abstract void setBranchPublicationRootName(String value);
	@Persist("session")
	public abstract Long getBranchRootId();
	public abstract void setBranchRootId(Long value);
	@Persist("session")
	public abstract List<CutoffNodeWrapper> getCutoffNodeWrappers();
	public abstract void setCutoffNodeWrappers(List<CutoffNodeWrapper> value);
	public abstract CutoffNodeWrapper getCurrentCutoffNode();
	public abstract void setCurrentCutoffNode(CutoffNodeWrapper value);
	public abstract String getNewCutoffGroupName();
	public abstract void setNewCutoffGroupName(String value);
	public abstract int getRevisionType();
	public abstract void setRevisionType(int value);
	public abstract void setWasPublished(boolean value);
	public abstract boolean getWasPublished();
	@Bean
	public abstract RevisionTypeModel getRevisionTypeModel();
	
	
	public void pageBeginRender(PageEvent event) {
		super.pageBeginRender(event);
		if (getCutoffNodeWrappers() == null) {
			setCutoffNodeWrappers(new ArrayList<CutoffNodeWrapper>());
		}
	}
	
	public boolean getNoRootGroup() {
		return getBranchRootId() == null;
	}

	public void changeRootGroupName() {
		setBranchPublicationRootName(getRootGroupName());
		setBranchRootId(null);
	}
	
	@SuppressWarnings("unchecked")
	public void setRootGroup() {
		String groupName = getBranchPublicationRootName();
		List matchingNodes = getWorkingNodeDAO().findNodesExactlyNamed(groupName, true);
		if (matchingNodes != null && matchingNodes.size() > 0) {
			Long nodeId = (Long) matchingNodes.get(0);
			if (getWorkingPageDAO().getNodeHasPage(nodeId)) {
				setBranchRootId(nodeId);
			} else {
				noPageForNode(groupName);
			}
		} else {
			noNodeFoundWithName(groupName);
		}
	}
	private void noNodeFoundWithName(String groupName) {
		getDelegate().record("No node found named: " + groupName, ValidationConstraint.CONSISTENCY);
	}
	public String getRootGroupName() {
		if (getBranchRootId() != null) {
			return getWorkingNodeDAO().getNodeNameWithId(getBranchRootId());
		} else {
			return null;
		}
	}
	public boolean getHasCutoff() {
		return getCutoffNodeWrappers() != null && getCutoffNodeWrappers().size() > 0;
	}
	public boolean getCanAddAnother() {
		if (getCutoffNodeWrappers() != null) {
			for (CutoffNodeWrapper currentWrapper : getCutoffNodeWrappers()) {
				if (currentWrapper.getIsNew()) {
					return false;
				}
			}
		}
		return true;
	}
	public void addCutoffNode() {
		getCutoffNodeWrappers().add(new CutoffNodeWrapper());
	}
	public void removeCutoffNode(Long nodeId) {
		for (CutoffNodeWrapper currentWrapper : new ArrayList<CutoffNodeWrapper>(getCutoffNodeWrappers())) {
			if (currentWrapper.getNodeId().equals(nodeId)) {
				getCutoffNodeWrappers().remove(currentWrapper);
			}
		}
	}
	public void applyCutoffGroupName() {
		String groupName = getNewCutoffGroupName();
		MappedNode node = getWorkingNodeDAO().getFirstNodeExactlyNamed(groupName);
		if (node != null) {
			// make sure the node has a page
			if (getWorkingPageDAO().getNodeHasPage(node.getNodeId())) {
				// check to see that the node is a descendant of the root group
				if (getMiscNodeDAO().getNodeIsAncestor(node.getNodeId(), getBranchRootId())) {
					// grab the last node wrapper and set it's info
					CutoffNodeWrapper wrapperToSet = getCutoffNodeWrappers().get(getCutoffNodeWrappers().size() - 1);
					wrapperToSet.setNodeId(node.getNodeId());
					wrapperToSet.setNodeName(node.getName());
				} else {
					getDelegate().record(groupName + " is not a descendant of " + getRootGroupName() + ".  Cutoff groups can only be descendants of the root.",
							ValidationConstraint.CONSISTENCY);
				}
			} else {
				noPageForNode(groupName);
			}
		} else {
			noNodeFoundWithName(groupName);
		}
	}
	private void noPageForNode(String groupName) {
		getDelegate().record(groupName + " does not have a page.  Please select a group with a page.", ValidationConstraint.CONSISTENCY);
	}
	public void submitBranch() {
		// first convert node ids into page ids
		Long rootPageId = getWorkingPageDAO().getPageIdForNodeId(getBranchRootId());
		HashSet<Long> pageIdsNotToPublish = new HashSet<Long>();
		for (CutoffNodeWrapper wrapper : getCutoffNodeWrappers()) {
			pageIdsNotToPublish.add(getWorkingPageDAO().getPageIdForNodeId(wrapper.getNodeId()));
		}
		PublicationBatch batch = getBatchSubmitter().submitBranchForPublication
			(rootPageId, getContributor(), pageIdsNotToPublish, getRevisionType());
		try {
			getBatchPusher().pushPublicationBatchToPublic(batch, getContributor());
			setWasPublished(true);
			// clear persistent properties
			setBranchRootId(null);
			setCutoffNodeWrappers(null);
		} catch (Exception e) {
			getDelegate().record("There was an error publishing the branch.  The error is: " + e.getMessage(), ValidationConstraint.CONSISTENCY);
			e.printStackTrace();
		}
	}
	private static class CutoffNodeWrapper {
		private Long nodeId;
		private String nodeName;

		public Long getNodeId() {
			return nodeId;
		}
		public void setNodeId(Long nodeId) {
			this.nodeId = nodeId;
		}
		public boolean getIsNew() {
			return getNodeId() == null;
		}
		public String getNodeName() {
			return nodeName;
		}
		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}
	}
}
