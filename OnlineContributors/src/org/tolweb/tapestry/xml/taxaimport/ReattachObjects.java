package org.tolweb.tapestry.xml.taxaimport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Document;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.ObjectManifestRecord;
import org.tolweb.misc.NodeHelper;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.xml.taxaimport.beans.OMAccessoryPage;
import org.tolweb.tapestry.xml.taxaimport.beans.OMContributor;
import org.tolweb.tapestry.xml.taxaimport.beans.OMMediaFile;
import org.tolweb.tapestry.xml.taxaimport.beans.OMNode;
import org.tolweb.tapestry.xml.taxaimport.beans.OMOtherName;

public abstract class ReattachObjects extends ViewObjectManifest implements PageBeginRenderListener, MetaInjectable, 
	BaseInjectable, NodeInjectable, PageInjectable, ImageInjectable, UserInjectable, AccessoryInjectable,
	CookieInjectable {
	
	@Persist("session")
	public abstract String getKey();
	public abstract void setKey(String key);
	
	@Persist("session")
	public abstract List<OMNode> getNodes();
	public abstract void setNodes(List<OMNode> nodes);
	
	public abstract Long getBasalNodeId();
	public abstract void setBasalNodeId(Long id);
	
	public String getCurrentBasalNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getBasalNodeId());
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[unavailable]";
	}
	
//	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
//		String idParam = cycle.getParameter("id");
//		Long id = null;
//		if (StringUtils.isEmpty(idParam) && args.length >= 1) {
//			id = (Long)args[0];
//		} else {
//			id = Long.parseLong(idParam);
//		}
//		setBasalNodeId(id);
//		
//		initializeOrphanObjects(id);
//	}
	
	public void pageBeginRender(PageEvent event) {
		if (getBasalNodeId() != null) {
			initializeOrphanObjects(getBasalNodeId());
		}
	}
	
	private void initializeOrphanObjects(Long id) {
		List<MappedNode> inactiveNodes = NodeHelper.getInactiveNodesForClade(id, getMiscNodeDAO(), getWorkingNodeDAO());
		Set<Long> inactiveNodeIds = new HashSet<Long>();
		for (MappedNode mnode : inactiveNodes) {
			inactiveNodeIds.add(mnode.getNodeId());
		}
		Document doc = ObjectManifestHelper.createObjectManifest(id, inactiveNodeIds, true, getWorkingNodeDAO(), getMetaNodeDAO());
		ObjectManifestRecord record = new ObjectManifestRecord();
		record.setBasalNodeId(id);
		record.setManifest(doc.toXML());
		
		extractNodesFromManifest(record);
	}

	@SuppressWarnings("unchecked")
	protected void suggestReattachmentNodes(List<OMNode> nodes, NodeDAO nodeDAO) {
		for (OMNode node : nodes) {
			List<Long> idMatches = nodeDAO.findNodesExactlyNamed(node.getName(), true);
			if (idMatches != null && idMatches.size() == 1) {
				node.setNewId(idMatches.get(0));
			}
		}
	}
	
	public void processManifest(IRequestCycle cycle) {
		System.out.println("....");
		System.out.println("\tProcessing manifest...");
//		List<OMNode> toRemove = new ArrayList<OMNode>();
		List<OMNode> nodes = getNodes();
		for (OMNode node : nodes) {
			if (node != null && node.getNewId() != null) {
				System.out.println("\t\tdoing reattachments for node-id: " + node.getId());
				doObjectReattachments(node);
				//toRemove.add(node);
			}
		}
		
		TaxaImportHome homePage = (TaxaImportHome)cycle.getPage("xml/TaxaImportHome");
		cycle.activate(homePage);
	}
	
	protected void doObjectReattachments(OMNode node) {
		Long newNodeId = node.getNewId();
		Long oldNodeId = node.getId();
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(newNodeId);
		reattachPage(node, newNodeId, oldNodeId);
		reattachMediaFiles(node, newNodeId, oldNodeId);
		reattachAccessoryPages(node, newNodeId, oldNodeId);
		int sequence = 0;
		if (mnode != null && mnode.getSynonyms() != null) {
			sequence = mnode.getSynonyms().size();
		}
		reattachOtherNames(node, newNodeId, oldNodeId, sequence);
		reattachContributors(node, newNodeId, oldNodeId);
	}
	
	private void reattachContributors(OMNode node, Long newNodeId,
			Long oldNodeId) {
		if (node.getContributors() != null && !node.getContributors().isEmpty()) {
			for (OMContributor contr : node.getContributors()) {
				Long contrId = contr.getId();
				System.out.println("\tReattaching contributor-id: "+contrId+" to node-id: " + newNodeId);
				getContributorDAO().reattachContributor(contrId, oldNodeId, newNodeId);
			}
		}
	}
	
	private void reattachOtherNames(OMNode node, Long newNodeId, Long oldNodeId, int sequence) {
		if (node.getOthernames() != null && !node.getOthernames().isEmpty()) {
			for (OMOtherName otherName : node.getOthernames()) {
				Long otherNameId = otherName.getId();
				System.out.println("\tReattaching other-name-id: "+otherNameId+" to node-id: " + newNodeId);
				getWorkingOtherNameDAO().reattachOtherName(otherNameId, oldNodeId, newNodeId, sequence++);
			}
			getWorkingOtherNameDAO().flushQueryCache();
		}
	}
	
	private void reattachAccessoryPages(OMNode node, Long newNodeId, Long oldNodeId) {
		if (node.getAccessorypages() != null && !node.getAccessorypages().isEmpty()) {
			for (OMAccessoryPage accPage : node.getAccessorypages()) {
				Long accPageId = accPage.getId();
				System.out.println("\tReattaching acc-page-id: "+accPageId+" to node-id: " + newNodeId);
				getWorkingAccessoryPageDAO().reattachAccessoryPage(accPageId, oldNodeId, newNodeId);
			}
		}
	}
	
	private void reattachMediaFiles(OMNode node, Long newNodeId, Long oldNodeId) {
		if (node.getMediafiles() != null && !node.getMediafiles().isEmpty()) {
			for (OMMediaFile mediafile : node.getMediafiles()) {
				Long mediafileId = mediafile.getId();
				System.out.println("\tReattaching media-id: "+mediafileId+" to node-id: " + newNodeId);
				getImageDAO().reattachImage(mediafileId, oldNodeId, newNodeId);
			}
		}
	}
	
	private void reattachPage(OMNode node, Long newNodeId, Long oldNodeId) {
		if (node.getPage() != null) {
			Long pageId = node.getPage().getId();
			System.out.println("\tReattaching page-id: "+pageId+" to node-id: " + newNodeId);
			getWorkingPageDAO().reattachPage(pageId, oldNodeId, newNodeId);
		}
	}
}
