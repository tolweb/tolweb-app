package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nu.xom.Document;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.tapestry.AbstractScientificContributorPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.Download;

/**
 * Visiting this page will remove the clade rooted at the node-id identified
 * @author lenards
 */
public abstract class RemoveClade extends AbstractScientificContributorPage implements 
	BaseInjectable, NodeInjectable, PageInjectable, MiscInjectable, 
	MetaInjectable, CookieInjectable, TreeGrowServerInjectable {
	
	public abstract boolean getWarned(); 
	public abstract void setWarned(boolean value);
	
	public abstract boolean getWasDeleted();
	public abstract void setWasDeleted(boolean value);
	
	public abstract Long getBasalNodeId();
	public abstract void setBasalNodeId(Long id);
	
	public abstract String getBasalNodeName();
	public abstract void setBasalNodeName(String nodeName);

	@InitialValue("true") 
	public abstract boolean getCreateObjectManifest();
	public abstract void setCreateObjectManifest(boolean value);
	
	public abstract boolean getClearOtherNames();
	public abstract void setClearOtherNames(boolean value);
	
	public abstract boolean getContinue();
	public abstract void setContinue(boolean value);
	
	public abstract List<Download> getDownloadedNodes();
	public abstract void setDownloadedNodes(List<Download> nodes);
	
	public abstract Download getCurrentDownloadedNode();
	public abstract void setCurrentDownloadedNode(Download dl);
	
	public String getContributorForCurrentDownloadedNode() {
		return (getCurrentDownloadedNode().getContributor() != null) ? 
					getCurrentDownloadedNode().getContributor().getName() + " " +
					getCurrentDownloadedNode().getContributor().getEmail() : "[unavailable]";
	}
	
	public boolean getShowDownloadedNodes() { 
		return getDownloadedNodes() != null && !getDownloadedNodes().isEmpty();
	}
	
	public abstract List<String> getNodeNames();
	public abstract void setNodeNames(List<String> names);
	
	public List<String> getDuplicateNames() {
		return new ArrayList<String>(getDuplicateNamesSet());
	}
	
	public abstract Set<String> getDuplicateNamesSet();
	public abstract void setDuplicateNamesSet(Set<String> dupes);	
	
	public abstract String getCurrentDuplicateName();
	public abstract void setCurrentDuplicateName(String name);
	
	public void issueWarning(IRequestCycle cycle) {
		if (!getWarned()) {
			setWarned(true);
			MappedNode nd = getWorkingNodeDAO().getNodeWithId(getBasalNodeId());
			if (nd != null) {
				setBasalNodeName(nd.getName());
			} else {
				setBasalNodeName("<unnamed basal node - id: " + getBasalNodeId() + ">");
			}
			cycle.activate(this);
		} 
	}
	
	public void removeClade(IRequestCycle cycle) {
		System.out.println("Clade removal start... " + (new Date()));
		if (getCreateObjectManifest()) {
			Document doc = ObjectManifestHelper.createObjectManifest(getBasalNodeId(), 
								getWorkingNodeDAO(), getMetaNodeDAO());
			Contributor user = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
			ObjectManifestHelper.saveObjectManifest(doc, getBasalNodeId(), user, getObjectManifestLogDAO());
		}
		
		setNodeNames(new ArrayList<String>());
		setDuplicateNamesSet(new HashSet<String>());
		
		setWasDeleted(true);
		TaxaImportCheck.filterOtherNames(getBasalNodeId(), getMiscNodeDAO(), getWorkingNodeDAO(), getNodePusher(), new ArrayList<String>());
		doRemoveClade(getBasalNodeId());
		TaxaImportCheck.performHomonymyCheck(getNodeNames(), getDuplicateNamesSet());
		System.out.println("Clade removal complete... " + (new Date()));
		cycle.activate(this);
	}
	
	public void returnToManager(IRequestCycle cycle) {
		System.out.println("I should redirect you... ");
		TaxaImportHome home = (TaxaImportHome)cycle.getPage("xml/TaxaImportHome");
		cycle.activate(home);		
	}
	
	@SuppressWarnings("unchecked")
	private void doRemoveClade(Long rootNodeId) {
		Set<Long> idsToDelete = new HashSet<Long>();
		
		determineChildIdsToDelete(getWorkingNodeDAO(), rootNodeId, idsToDelete);
		determineChildIdsToDelete(getMiscNodeDAO(), rootNodeId, idsToDelete);
		System.out.println("size: " + idsToDelete.size());
		System.out.println("ids: " + idsToDelete.toString());
		
		if (getClearOtherNames()) {
			clearBasalNodeOtherNames(rootNodeId);
		}
		
		List downloadedNodeIds = null;
		if (idsToDelete != null && !idsToDelete.isEmpty()) {
			downloadedNodeIds = getDownloadDAO().getNodesAreDownloaded(new ArrayList(idsToDelete));
		}
		
		if (downloadedNodeIds != null && !downloadedNodeIds.isEmpty()) {
			Map<Long, Download> downloads = new HashMap<Long, Download>();
			for (Iterator itr = downloadedNodeIds.iterator(); itr.hasNext(); ) {
				Long nodeId = (Long)itr.next();
				Download dl = getDownloadDAO().getOpenDownloadForNode(nodeId);
				if (dl != null) {
					downloads.put(dl.getDownloadId(), dl);
				}
			}
			setDownloadedNodes(new ArrayList<Download>(downloads.values()));
			System.out.println("\tAbort Clade Removal! Clade contains downloaded nodes and cannot be removed...");
			for (Download dl : getDownloadedNodes()) {
				System.out.println("\tdownload-id: " + dl.getDownloadId() + " for node: " + dl.getRootNode());
			}
			setWasDeleted(false); 
			return; // abort the delete - someone has nodes checked out in TreeGrow
		}
				
		for (Long deleteId : idsToDelete) {
			executeNodeDeletion(deleteId);
			System.out.println("\tlogically deleting node identified as node-id=" + deleteId);
		}
		
		System.out.println("\tI hope this is what you wanted to do... please consider this action may have orphaned objects attached to those nodes");
		getMiscNodeDAO().flushQueryCache();
		getWorkingNodeDAO().flushQueryCache();
	}

	private void clearBasalNodeOtherNames(Long rootNodeId) {
		MappedNode basalNode = getWorkingNodeDAO().getNodeWithId(rootNodeId);
		if (basalNode != null && basalNode.getSynonyms() != null) {
			basalNode.getSynonyms().clear();
			getWorkingNodeDAO().saveNode(basalNode);
		}
	}
	
	/**
	 * Node data is stored both in the Misc and Working databases - we need to execution deletion 
	 * in both databases to ensure that all information about the clade has been removed. 
	 * @param deleteId the id of the node to be deleted
	 */
	private void executeNodeDeletion(Long deleteId) {
		MappedNode mnodeDelete = getWorkingNodeDAO().getNodeWithId(deleteId);
		if (mnodeDelete != null) {
			getNodeNames().add(mnodeDelete.getName());
			if (getClearOtherNames()) {
				if (mnodeDelete.getSynonyms() != null) {
					System.out
							.println("\tWhoa...I just cleared out the othernames... hope they meant to do that...");
					mnodeDelete.getSynonyms().clear();
				}
			}
			cleanseOtherNameData(mnodeDelete);
			getWorkingNodeDAO().deleteNode(mnodeDelete, false);

			mnodeDelete = getMiscNodeDAO().getNodeWithId(deleteId);
			getMiscNodeDAO().deleteNode(mnodeDelete, false);
		}
	}
	
	/**
	 * Cleanses data for OtherNames in both places they exist: 1) 
	 * in the database and 2) objects alive in memory in the  
	 * second-level hibernate cache
	 * 
	 * @param mnodeDelete node to cleanse othername data for
	 */
	private void cleanseOtherNameData(MappedNode mnodeDelete) {
		// cleanse data in the database
		getWorkingOtherNameDAO().cleanseOtherNameDataForNode(mnodeDelete.getNodeId());
		getMiscOtherNameDAO().cleanseOtherNameDataForNode(mnodeDelete.getNodeId());
		// cleanse data in memory
		cleanseOtherNameValuesInMemory(mnodeDelete); // <--- HERE BE EVIL! DAS BUG!  
	}

	@SuppressWarnings("unchecked")
	private void cleanseOtherNameValuesInMemory(MappedNode mnode) {
		// DEVN: modifying the values here causes Hibernate to behave like an 
		// old grandfather... it makes it create two version of the name - 
		// because these two properties changed... 
		for (Iterator itr = mnode.getSynonyms().iterator(); itr.hasNext(); ) {
			MappedOtherName moname = (MappedOtherName) itr.next();
			moname.setIsImportant(false);
			moname.setIsPreferred(false);
		}
		mnode.setSynonyms(new TreeSet(mnode.getSynonyms()));
	}
	
	@SuppressWarnings("unchecked")
	private void determineChildIdsToDelete(NodeDAO nodeDAO, Long rootNodeId, Set<Long> idsToDelete) {
		if (rootNodeId != null) {
			List childIds = nodeDAO.getChildrenNodeIds(rootNodeId);
			idsToDelete.addAll(childIds);
			for (Iterator itr = childIds.iterator(); itr.hasNext(); ) {
				Long childId = (Long)itr.next();
				determineChildIdsToDelete(nodeDAO, childId, idsToDelete);
			}
		}
	}
	
    public PopupLinkRenderer getRenderer() {
    	int width = 750;
    	int height = 350;
    	return getRendererFactory().getLinkRenderer("Find Basal Node Id", width, height);
    }		
}