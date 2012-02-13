package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.Permission;

/**
 * Registration form for both other people editing scientific contributors
 * as well as people who are classified as enthusiasts (due to the similarities
 * of node attachments)
 * @author dmandel
 *
 */
public abstract class ScientificContributorRegistrationOther extends
		ScientificContributorRegistration implements NodesSetPage, UserInjectable, NodeInjectable {
	@Persist("session") @SuppressWarnings("unchecked")
	public abstract Set getNodes();
	@SuppressWarnings("unchecked")
	public abstract void setNodes(Set value);
	@Persist("session") @SuppressWarnings("unchecked")
	public abstract Set getCutoffNodes();
	@SuppressWarnings("unchecked")
	public abstract void setCutoffNodes(Set value);
	public abstract void setIsNonCoreContributor(boolean value);
	@Persist("session")
	public abstract boolean getIsNonCoreContributor();
	public abstract Long getNodeIdToDelete();
	public abstract Long getCutoffNodeIdToDelete();
	public abstract void setFromWorkingNode(MappedNode node);
	@Persist("session")
	public abstract MappedNode getFromWorkingNode();

	public abstract boolean getAddNodeSelected();
	public abstract void setAddNodeSelected(boolean value);
	public abstract boolean getAddCutoffNodeSelected();
	public abstract void setAddCutoffNodeSelected(boolean value);
	
	protected boolean checkForRedirectOrErrors(IRequestCycle cycle) {
		if (getAddNodeSelected() || getAddCutoffNodeSelected()) {
			FindNodes searchPage = (FindNodes) cycle.getPage("FindNodes");
			int callbackType = getAddCutoffNodeSelected() ? FindNodesResults.ADD_CONTRIBUTOR_PERMISSIONS_CUTOFF_CALLBACK :
				FindNodesResults.ADD_CONTRIBUTOR_PERMISSIONS_CALLBACK;
			searchPage.doActivate(cycle, AbstractWrappablePage.DEFAULT_WRAPPER, getPageName(), false, null, callbackType, true);
			return false;
		} else if (getRemoveNodeSelected()) {
			removeSelectedNode();
			return false;
		} else if (getRemoveCutoffNodeSelected()) {
			removeSelectedCutoffNode();			
			return false;
		} else {
			return super.checkForRedirectOrErrors(cycle);
		}
	}
	
	/**
	 * Override this since someone who is registered by another contributor
	 * should automatically be approved, or if it's not a core contributor
	 * indicate that as well
	 */
	public byte getContributorType() {
		if (getIsNonCoreContributor()) {
			return Contributor.OTHER_SCIENTIST;
		} else {
			return Contributor.SCIENTIFIC_CONTRIBUTOR;
		}
	}
	/**
	 * Overridden to add the contributor who created this contributor to 
	 * the permissions to edit the newly created contributor
	 */
	protected void doAdditionalProcessing() {
		if (getIsNewContributor()) {
			Contributor editingContributor = getContributor();
			if (editingContributor != null) {
				getEditedContributor().addToContributorPermissions(editingContributor);
			}
			if (!getIsNonCoreContributor()) {
				// this approves the new contributor that another user is editing
				// (since we assume that our existing scientific contributors know
				//  what they are doing)
				getEditedContributor().setUnapprovedContributorType(Contributor.ANY_CONTRIBUTOR);
			} else {
				// want to log them in so they can add themselves to other groups
				// they might be interested in
				setContributor(getEditedContributor());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
    public Set getNodesSet() {
    	return getNodes();
    }
    public void setSelectedAnchor(String value) {}
    
    @SuppressWarnings("unchecked")
    public void pageBeginRender(PageEvent event) {
    	super.pageBeginRender(event);
    	// if the contributor is not a core contributor, redirect to the general form
    	if (getEditedContributor().getContributorType() == Contributor.ACCESSORY_CONTRIBUTOR) {
    		redirectToOtherEditPage("GeneralContributorRegistration");
    	}
    	
    	// check to make sure that the contributor isn't trying to edit themself 
    	// (don't want them to be able to edit their node permissions)
    	// unless it's the case that they are an enthusiast, in which case their node
    	// permissions don't do anything, so it's ok
    	Contributor visitContributor = getContributor();
    	if (visitContributor != null && !getIsNonCoreContributor()) {
    		if (visitContributor.getId() == getEditedContributor().getId()) {
    			redirectToOtherEditPage("ScientificContributorRegistration");
    		}
    	}
    	setAgreeToTerms(true);
    	// init nodes
    	if (!getRequestCycle().isRewinding()) {
	    	if (getIsNewContributor()) {
	    		if (getNodes() == null) {
		    		// if they are new, set a new set for the person
		    		setNodes(new HashSet());
	    		}
	    		if (getCutoffNodes() == null) {
		    		setCutoffNodes(new HashSet());	    			
	    		}
	    	} else {
	    		if (getNodes() == null) {
		    		// an existing contributor, so fetch their nodes from the db
		    		List nodeIds = getPermissionChecker().getNodeAttachmentsForContributor(getEditedContributor(), 
		    				!getIsNonCoreContributor());
		    		if (nodeIds != null && nodeIds.size() > 0) {
			    		List nodes = getWorkingNodeDAO().getNodesWithIds(nodeIds);
			    		setNodes(new HashSet(nodes));
		    		} else {
		    			setNodes(new HashSet<MappedNode>());
		    		}
	    		}
	    		if (getCutoffNodes() == null) {
	    			List nodeIds = getPermissionChecker().getNodeAttachmentsForContributor(getEditedContributor(), Contributor.NODE_ATTACHMENT_CUTOFF);
		    		if (nodeIds != null && nodeIds.size() > 0) {
			    		List nodes = getWorkingNodeDAO().getNodesWithIds(nodeIds);
			    		setCutoffNodes(new HashSet(nodes));
		    		} else {
		    			setCutoffNodes(new HashSet<MappedNode>());
		    		}	    			
	    		}
	    	}
    	}
    }
    
    private void redirectToOtherEditPage(String page) {
    	AbstractContributorRegistration editPage = (AbstractContributorRegistration) getRequestCycle().getPage(page);
    	editPage.setEditedContributor(getEditedContributor());
    	throw new PageRedirectException(editPage);
    }
	public void addGroup(IRequestCycle cycle) {
		setAddNodeSelected(true);
	}
	public void addCutoffGroup(IRequestCycle cycle) {
		setAddCutoffNodeSelected(true);
	}
	public String getConfirmationPageName() {
		if (getIsNonCoreContributor()) {
			return "NonCoreContributorConfirmation";
		} else {
			return super.getConfirmationPageName();
		}
	}
	protected Object getExistingContributorPageDestination() {
		if (!getIsNonCoreContributor()) {
			return super.getExistingContributorPageDestination();
		} else {
			// here, redirect to the first people list page that appears
			// in the contributor's list of node attachments
			PeopleList listPage = (PeopleList) getRequestCycle().getPage("PeopleList");
			MappedNode node = getPublicNodeDAO().getRootNode();
			try {
				node = (MappedNode) getNodes().iterator().next();
			} catch (Exception e) { e.printStackTrace(); }
			listPage.setNode(node);
			return listPage;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void doPostSaveAdditionalProcessing() {
		PermissionChecker checker = getPermissionChecker();
		Contributor contr = getEditedContributor();
		if (getNodes() != null) {
			int contributorType = getIsNonCoreContributor() ? Contributor.NODE_ATTACHMENT_INTEREST : Contributor.NODE_ATTACHMENT_PERMISSION;
			checker.updateNodeAttachmentsForContributor(contr, contributorType, getPermissionsFromNodes(getNodes()));
		}
		if (getCutoffNodes() != null) {
			checker.updateNodeAttachmentsForContributor(contr, Contributor.NODE_ATTACHMENT_CUTOFF, getPermissionsFromNodes(getCutoffNodes()));
		}
	}
	private Collection<Permission> getPermissionsFromNodes(Set<MappedNode> nodes) {
		ArrayList<Permission> permissions = new ArrayList<Permission>();
		for (MappedNode node : nodes) {
			Permission newPermission = new Permission(node.getNodeId().intValue(), node.getName(), null);
			permissions.add(newPermission);
		}
		return permissions;
	}
	protected void setAdditionalConfirmationPageProperties(IPage page) {		
		if (getIsNonCoreContributor()) {
			super.setAdditionalConfirmationPageProperties(page);
			Long firstNodeId = ((MappedNode) getNodes().iterator().next()).getNodeId();
			PropertyUtils.write(page, "nodeId", firstNodeId);
		} else {
			super.setAdditionalConfirmationPageProperties(page);			
			PropertyUtils.write(page, "nodesSet", getNodes());
			PropertyUtils.write(page, "fromWorkingNode", getFromWorkingNode());
		}
		// at the end of this save, so clear the nodes set
		setNodes(null);
	}
	public boolean getRemoveNodeSelected() {
		return getNodeIdToDelete() != null;
	}
	public boolean getRemoveCutoffNodeSelected() {
		return getCutoffNodeIdToDelete() != null;
	}
	private void removeSelectedCutoffNode() {
		removeNode(getCutoffNodes(), getCutoffNodeIdToDelete());
	}	
	protected void removeSelectedNode() {
		removeNode(getNodes(), getNodeIdToDelete());
	}
	@SuppressWarnings("unchecked")
	private void removeNode(Set nodes, Long selectedId) {
		for (Iterator iter = new ArrayList(nodes).iterator(); iter.hasNext();) {
			MappedNode nextNode = (MappedNode) iter.next();
			if (nextNode.getNodeId().equals(selectedId)) {
				nodes.remove(nextNode);
				break;
			}
		}		
	}
	public void clearPersistentProperties() {
		super.clearPersistentProperties();
		setNodes(null);
		setCutoffNodes(null);
		setFromWorkingNode(null);
		setIsNonCoreContributor(false);
	}
	public void addNewContributor(boolean isCore, Long nodeId, IRequestCycle cycle, boolean fromWorkingPage) {
		clearPersistentProperties();
		setIsNonCoreContributor(!isCore);
		Set<MappedNode> nodes = new HashSet<MappedNode>();
		MappedNode node = getMiscNodeDAO().getNodeWithId(nodeId); 
		nodes.add(node);
		setNodes(nodes);
		setCutoffNodes(new HashSet<MappedNode>());
		if (fromWorkingPage) {
			setFromWorkingNode(node);
		}
		cycle.activate(this);		
	}
}
