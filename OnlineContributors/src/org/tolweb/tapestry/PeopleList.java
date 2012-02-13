package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.ContributorNameComparator;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.PageContributor;

public abstract class PeopleList extends CacheAndPublicAwarePage implements IExternalPage, PageBeginRenderListener, UserInjectable, NodeInjectable, ImageInjectable {
	public abstract void setNode(MappedNode node);
	public abstract MappedNode getNode();
	public abstract void setTolPage(MappedPage page);
	@SuppressWarnings("unchecked")
	public abstract void setCoreContributors(List value);
	@SuppressWarnings("unchecked")
	public abstract List getCoreContributors();
	@SuppressWarnings("unchecked")
	public abstract void setMediaContributors(List value);
	@SuppressWarnings("unchecked")
	public abstract List getMediaContributors();
	@SuppressWarnings("unchecked")
	public abstract void setOtherContributors(List value);
	@SuppressWarnings("unchecked")
	public abstract List getOtherContributors();
	@SuppressWarnings("unchecked")
	public abstract void setTreehouseContributors(List value);
	public abstract Contributor getCurrentContributor();
	public abstract void setCurrentContributor(Contributor value);
	@SuppressWarnings("unchecked")
	public abstract void setContributorIds(Set value);
	@SuppressWarnings("unchecked")
	public abstract Set getContributorIds();
	public abstract void setHasError(boolean value);

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		if (parameters != null && parameters.length > 0) {
			Number nodeId = (Number) parameters[0];
			MappedNode node = getPublicNodeDAO().getNodeWithId(Long.valueOf(nodeId.intValue()));
			if (node != null) {
				setNode(node);			
			} else {
				setHasError(true);
			}
		} else {
			setHasError(true);
		}
	}

	@SuppressWarnings("unchecked")
	public void pageBeginRender(PageEvent event) {
		super.pageBeginRender(event);
		MappedNode node = getNode();
		if (node != null) {
			PageDAO pageDAO = getPageDAO();
			MappedPage page = pageDAO.getPageForNode(node);
			setTolPage(page);			
			//Collection nodeIds = pageDAO.getNodeIdsOnPage(page, true);
			ContributorDAO contrDAO = getContributorDAO();
			ArrayList nodeIds = new ArrayList();
			Long nodeId = getNode().getNodeId(); 
			nodeIds.add(nodeId);
			nodeIds.addAll(getMiscNodeDAO().getDescendantIdsForNode(nodeId));
			List coreContributors = contrDAO.getContributorsAttachedToNodeIds(nodeIds, getPermissionChecker(), true);						
			if (page != null) {
				for (Iterator iter = page.getContributors().iterator(); iter.hasNext();) {
					PageContributor contr = (PageContributor) iter.next();
					coreContributors.add(contr.getContributor());
				}
			}
			// remove the contributors who have the editingRootNodeId which is equal 
			// to the node we are looking at
			for (Iterator iter = new ArrayList(coreContributors).iterator(); iter.hasNext();) {
				Contributor nextContr = (Contributor) iter.next();
				if (nextContr.getEditingRootNodeId() != null && nextContr.getEditingRootNodeId().equals(nodeId)) {
					coreContributors.remove(nextContr);
				}
			}
			Set seenIds = new HashSet();
			List articleNoteContributors = getAccessoryPageDAO().getContributorIdsForArticleNotesAttachedToNodeIds(nodeIds);
			articleNoteContributors = contrDAO.getContributorsWithIds(articleNoteContributors, true);
			//iterateContributors(seenIds, articleNoteContributors);
			coreContributors.addAll(articleNoteContributors);
			// then the treehouse contributors	
			List treehouseContributors = getAccessoryPageDAO().getContributorIdsForTreehousesAttachedToNodeIds(nodeIds);
			treehouseContributors = contrDAO.getContributorsWithIds(treehouseContributors, true);
			List otherContributors = contrDAO.getContributorsAttachedToNodeIds(nodeIds, 
					getPermissionChecker(), false);
			List mediaContributors = getImageDAO().getContributorsWithImagesAttachedToNodeIds(nodeIds);
			iterateContributors(seenIds, coreContributors, true);
			iterateContributors(seenIds, treehouseContributors, false);
			iterateContributors(seenIds, mediaContributors, false);
			iterateContributors(seenIds, otherContributors, false);
			setTreehouseContributors(treehouseContributors);
			setMediaContributors(mediaContributors);
			setCoreContributors(coreContributors);
			setOtherContributors(otherContributors);
			setContributorIds(seenIds);
		}
	}
	
	/**
	 * Removes contributors from the list if their id is in the
	 * set of already seen ids
	 * @param seenIds
	 * @param contributors
	 */
	@SuppressWarnings("unchecked")
	private void iterateContributors(Set seenIds, List contributors, boolean removeDuplicates) {
		for (Iterator iter = new ArrayList(contributors).iterator(); iter.hasNext();) {
			Contributor nextContr = (Contributor) iter.next();
			if (seenIds.contains(nextContr.getId())) {
				if (removeDuplicates) {
					contributors.remove(nextContr);
				}
			} else {
				seenIds.add(nextContr.getId());
			}
		}		
		Collections.sort(contributors, new ContributorNameComparator());
	}
	public String getCurrentNonSciContrNodesString() {
		return getCurrentContributorsNodesString(false);
	}
	public String getCurrentSciContrNodesString() {
		return getCurrentContributorsNodesString(true);
	}
	@SuppressWarnings("unchecked")
	private String getCurrentContributorsNodesString(boolean checkPermissions) {
		List names = getPermissionChecker().getNodeNamesContributorAttachedTo(getCurrentContributor().getId(), checkPermissions);
		if (names.size() > 0) {
			return "(" + StringUtils.returnCommaJoinedString(names) + ")";
		} else {
			return null;
		}
	}
	public boolean getCanAddScientificContributorToGroup() {
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		return contr != null && (getPermissionChecker().checkHasPermissionForNode(contr, getNode().getNodeId()));
	}
	private void addContributorToGroup(boolean isScientific, Long nodeId, IRequestCycle cycle) {
		ScientificContributorRegistrationOther otherPage = (ScientificContributorRegistrationOther) cycle.getPage("ScientificContributorRegistrationOther");
		otherPage.addNewContributor(isScientific, nodeId, cycle, false);
	}
	public void addScientificContributorToGroup(IRequestCycle cycle) {
		setupRequestCycleAttributes();
		Long nodeId = (Long) cycle.getListenerParameters()[0];
		addContributorToGroup(true, nodeId, cycle);
	}
	public void addUserToGroup(IRequestCycle cycle) {
		setupRequestCycleAttributes();
		Long nodeId = (Long) cycle.getListenerParameters()[0];
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		if (contr == null) {
			addContributorToGroup(false, nodeId, cycle);
		} else {
			MappedNode node = getNodeDAO().getNodeWithId(nodeId);			
			getPermissionChecker().addNodeAttachmentForContributor(contr.getId(), nodeId);
			// add the group to those that the contributor is interested in
			setNode(node);
		}
	}
	public void removeNode(IRequestCycle cycle) {
		setupRequestCycleAttributes();
		Long nodeId = (Long) cycle.getListenerParameters()[0];
		Contributor visitContributor = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		getPermissionChecker().removeNodeAttachmentForContributor(visitContributor.getId(), nodeId);
		setNode(getNodeDAO().getNodeWithId(nodeId));
	}
	public boolean getCanRemoveNode() {
		boolean returnVal = false;
		Contributor visitContributor = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		if (visitContributor != null) {
			returnVal = visitContributor.getId() == getCurrentContributor().getId();
		}
		return returnVal;
	}
	public boolean getCanAddToList() {
		Contributor visitContributor = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		return visitContributor == null || !getContributorIds().contains(visitContributor.getId());
	}
}
