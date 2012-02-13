package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.IFieldTracking;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.hibernate.ManualReconciliationRecord;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.NodeRetirementRecord;
import org.tolweb.misc.MetaNode;
import org.tolweb.misc.MetaNodeTuple;
import org.tolweb.misc.NodeHelper;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.xml.taxaimport.preparers.ObjectManifestPreparer;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

public abstract class SwapNodes extends BasePage implements
		PageBeginRenderListener, MetaInjectable, BaseInjectable,
		NodeInjectable, PageInjectable, UserInjectable, AccessoryInjectable,
		ImageInjectable, CookieInjectable {

	private static final String DEFAULT_RETIRED_BY = "[unavailable]";

	@Persist("session")
	public abstract Long getBasalNodeId();
	public abstract void setBasalNodeId(Long id);

	public abstract boolean getRefreshTuples();
	public abstract void setRefreshTuples(boolean value);
	
	public String getCurrentBasalNodeName() {
		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(getBasalNodeId(), true);
		if (mnode != null) {
			return mnode.getName() + " (" + mnode.getNodeId() + ")";
		}
		return "[unavailable]";
	}
	
	public String getWorkingURL() {
		if ("[unavailable]".equals(getCurrentBasalNodeName())) {
			return "#";
		}
		return getUrlBuilder().getWorkingURLForBranchPage(getCurrentBasalNodeName(), getBasalNodeId());		
	}
	
	public abstract MappedAccessoryPage getCurrentAccessoryPage();

	public abstract void setCurrentAccessoryPage(MappedAccessoryPage accPage);

	public abstract Long getCurrentOtherNameId();

	public abstract void setCurrentOtherNameId(Long id);

	public String getCurrentOtherNameText() {
		return " - "
				+ getCurrentTuple().getMetaNode().getOtherNameIds().get(
						getCurrentOtherNameId());
	}

	public abstract NodeImage getCurrentMediaFile();

	public abstract void setCurrentMediaFile(NodeImage mediaFile);

	public abstract Contributor getCurrentContributor();

	public abstract void setCurrentContributor(Contributor contr);

	@Persist("session")
	public abstract List<SwapTuple> getSwapTuples();

	public abstract void setSwapTuples(List<SwapTuple> tuples);

	public abstract SwapTuple getCurrentTuple();

	public abstract void setCurrentTuple(SwapTuple tuple);

	public abstract List<MappedNode> getRetiredNodes();

	public abstract void setRetiredNodes(List<MappedNode> nodes);

	public abstract List<ManualReconciliationRecord> getManualReconcileRecords();

	public abstract void setManualReconcileRecords(
			List<ManualReconciliationRecord> records);

	public abstract List<NodeRetirementRecord> getNodeRetirementRecords();

	public abstract void setNodeRetirementRecords(
			List<NodeRetirementRecord> records);

	@Bean
	public abstract ValidationDelegate getValidationDelegate();	
	
	public abstract IFieldTracking getCurrentFieldTracking();
	public abstract void setCurrentFieldTracking(IFieldTracking ift);	
	
	public void pageBeginRender(PageEvent event) {
		initializeStructures();
	}

	public String getNodeBackground() {
		if (shouldUseDifferentBackground()) {
			return "background: #FFCCBB;";
		}
		return "";
	}

	public boolean shouldUseDifferentBackground() {
		return getCurrentTuple() != null
				&& getCurrentTuple().getMetaNode() != null
				&& getCurrentTuple().getMetaNode().getHasAttachments();
	}

	public void checkAllRetireNode(IRequestCycle cycle) {
		for (SwapTuple tuple : getSwapTuples()) {
			if (!tuple.getMetaNode().getHasAttachments()) {
				tuple.setShouldRetireNode(true);
			}
		}
	}
	
	public void checkAllReplaceNode(IRequestCycle cycle) {
		for (SwapTuple tuple : getSwapTuples()) {
			tuple.setReplaceTargetNode(true);
		}
		cycle.activate(this);
	}

	public void uncheckAllReplaceNode(IRequestCycle cycle) {
		for (SwapTuple tuple : getSwapTuples()) {
			tuple.setReplaceTargetNode(false);
		}
		cycle.activate(this);
	}
	
	public void processOperations(IRequestCycle cycle) {
		Contributor resolver = getCookieAndContributorSource()
				.getContributorFromSessionOrAuthCookie();
		boolean isValid = validateInput();
		if (isValid) {
			initializeLoggingRecords();
			processAllTuples();
			retireNodes(resolver);
			finalizeLogging(resolver);
			prepareAndRedirect(cycle);
			setSwapTuples(null); // clear out all tuples
		}
	}

	private boolean validateInput() {
		Set<Long> targetNodeIds = new HashSet<Long>();
		boolean noDupes = true;
		for (SwapTuple tuple : getSwapTuples()) {
			if (tuple.getTargetNodeId() != null && tuple.getReplaceTargetNode()) {
				boolean added = targetNodeIds.add(tuple.getTargetNodeId());
				if (!added) {
					String valMsg = "" + tuple.getTargetNodeId() + 
						" appears more than once as a Target Node Id";
					getValidationDelegate().record(valMsg, 
						ValidationConstraint.CONSISTENCY);
					noDupes = false;
				}
			}
		}
		return noDupes;
	}

	private void initializeLoggingRecords() {
		setManualReconcileRecords(new ArrayList<ManualReconciliationRecord>());
		setNodeRetirementRecords(new ArrayList<NodeRetirementRecord>());
	}

	private void processAllTuples() {
		System.out.println("Operations being processed... ");
		for (SwapTuple tuple : getSwapTuples()) {
			performAnyRemoveDetachOperations(tuple);
			processTargetNodeOperations(tuple);
			processRetireOperations(tuple);
			processReactivateOperations(tuple);
		}
	}
	private void processReactivateOperations(SwapTuple tuple) {
		if (tuple.getParentNodeId() != null
				&& tuple.getTargetNodeId() == null) {
			System.out.println("Executing reactivate operation - node-id: "
					+ tuple.getMetaNode().getNode().getNodeId()
					+ " parent node-id: " + tuple.getParentNodeId());
			executeReactivate(tuple);
		}
	}
	private void processRetireOperations(SwapTuple tuple) {
		if (tuple.getShouldRetireNode()) {
			MappedNode mnode = tuple.getMetaNode().getNode();
			getRetiredNodes().add(mnode);
			System.out.println("Retiring node: " + mnode.getNodeId());
		}
	}
	private void processTargetNodeOperations(SwapTuple tuple) {
		if (tuple.getTargetNodeId() != null) {
			if (tuple.getReplaceTargetNode()) {
				System.out.println("Executing swap operation - node-id: "
						+ tuple.getMetaNode().getNode().getNodeId()
						+ " target node-id: " + tuple.getTargetNodeId());
				if (!tuple.getTargetNodeId().equals(getBasalNodeId())) {
					executeSwap(tuple);
				} else {
					System.out.println("WHOA - just dodged a problem! " + 
							"They tried to replace the basal node, " + getBasalNodeId() + 
							" with node id: " + tuple.getTargetNodeId() + 
							" that would have been BAD!");
				}
			} else {
				executeObjectRelocate(tuple);
			}
		}
	}

	private void performAnyRemoveDetachOperations(SwapTuple tuple) {
		if (tuple.getRemoveAttachedPage()) {
			executeAttachedPageRemoval(tuple);
		}
		if (tuple.getRemoveAttachedOtherNames()) {
			executeAttachedOtherNamesRemoval(tuple);
		}
		if (tuple.getDetachAllMedia()) {
			executeMediaDetach(tuple);
		}
	}

	private void retireNodes(Contributor resolver) {
		for (MappedNode mnode : getRetiredNodes()) {
			if (mnode != null) {
				logRetirement(mnode, resolver);
				mnode.setStatus(MappedNode.RETIRED);
				getWorkingNodeDAO().saveNode(mnode);
				pushNode(mnode);
			}
		}
	}

	private void prepareAndRedirect(IRequestCycle cycle) {
		SwapNodesConfirmation confirmPage = (SwapNodesConfirmation) cycle
				.getPage("taxaimport/SwapNodesConfirmation");
		confirmPage.setBasalNodeId(getBasalNodeId());
		confirmPage.setManualReconcileRecords(getManualReconcileRecords());
		confirmPage.setNodeRetirementRecords(getNodeRetirementRecords());
		cycle.activate(confirmPage);
	}

	private void finalizeLogging(Contributor resolver) {
		finalizeLoggingForManualRecRecords(resolver);
		finalizeLoggingForNodeRetirementRecords(resolver);
	}

	private void finalizeLoggingForNodeRetirementRecords(Contributor resolver) {
		for (NodeRetirementRecord rec : getNodeRetirementRecords()) {
			if (StringUtils.isEmpty(rec.getRetiredBy())
					|| DEFAULT_RETIRED_BY.equals(rec.getRetiredBy())) {
				rec.setRetiredBy((resolver != null) ? resolver.getEmail()
						: DEFAULT_RETIRED_BY);
			}
			getNodeRetirementLogDAO().createNodeRetirementRecord(rec);
		}
	}

	private void finalizeLoggingForManualRecRecords(Contributor resolver) {
		for (ManualReconciliationRecord rec : getManualReconcileRecords()) {
			rec.setResolvedBy((resolver != null) ? resolver.getEmail()
					: DEFAULT_RETIRED_BY);
			getManualReconciliationLogDAO().createManualReconciliationRecord(
					rec);
		}
	}

	private void executeMediaDetach(SwapTuple tuple) {
		if (tuple != null && tuple.getMetaNode() != null) {
			if (tuple.getMetaNode().getNode() != null) {
				List<NodeImage> media = tuple.getMetaNode().getMedia();
				MappedNode nd = tuple.getMetaNode().getNode();
				for (NodeImage mediaFile : media) {
					mediaFile.removeFromNodesSet(nd);
					getImageDAO().saveImage(mediaFile);
				}
				logRemoval(tuple.getMetaNode());				
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void executeAttachedOtherNamesRemoval(SwapTuple tuple) {
		if (tuple != null && tuple.getMetaNode() != null) {
			MappedNode mnode = tuple.getMetaNode().getNode();
			mnode.setSynonyms(new TreeSet());
			getWorkingNodeDAO().saveNode(mnode);
			pushNode(mnode);
			logRemoval(tuple.getMetaNode());			
		}
	}

	private void executeAttachedPageRemoval(SwapTuple tuple) {
		if (tuple != null && tuple.getMetaNode() != null) {
			if (tuple.getMetaNode().getPage() != null) {
				logRemoval(tuple.getMetaNode());
				MappedPage mpage = tuple.getMetaNode().getPage();
				getWorkingPageDAO().deletePage(mpage);
			}
		}
	}

	private void executeObjectRelocate(SwapTuple tuple) {
		MappedNode source = tuple.getMetaNode().getNode();
		MappedNode target = getWorkingNodeDAO().getNodeWithId(
				tuple.getTargetNodeId(), true);

		if (source != null && target != null) {
			MetaNode targetMetaInfo = getMetaNodeDAO().getMetaNode(target);

			logManualReconcile(targetMetaInfo, tuple.getMetaNode());

			moveNameToTarget(tuple, source, target);
			
			reattachObjects(targetMetaInfo, tuple.getMetaNode());
			moveOtherNames(source, target);

			getWorkingNodeDAO().saveNode(source);
			pushNode(source);
			getWorkingNodeDAO().saveNode(target);
			pushNode(target);
		}
	}

	@SuppressWarnings("unchecked")
	private void moveOtherNames(MappedNode source, MappedNode target) {
		for (Iterator itr = source.getSynonyms().iterator(); itr.hasNext();) {
			MappedOtherName othername = (MappedOtherName) itr.next();
			int curr = target.getSynonyms().size() + 100;
			othername.setOrder(curr);
			target.addSynonym(othername);
		}
		source.getSynonyms().clear();
		resetOtherNamesOrdering(target);
	}

	@SuppressWarnings("unchecked")
	private void resetOtherNamesOrdering(MappedNode target) {
		int i = 0;
		for (Iterator itr = target.getSynonyms().iterator(); itr.hasNext(); i++) {
			MappedOtherName othername = (MappedOtherName) itr.next();
			othername.setOrder(i);
		}
	}

	private void executeReactivate(SwapTuple tuple) {
		MappedNode child = tuple.getMetaNode().getNode();
		MappedNode parent = getWorkingNodeDAO().getNodeWithId(
				tuple.getParentNodeId());
		if (child != null && parent != null) {
			// get the child's current node ancestry
			Set nodeAncestorIds = getMiscNodeDAO().getAncestorsForNode(
					child.getNodeId());

			MappedPage mpage = getWorkingPageDAO().getPage(parent);

			// change parental relationship
			child.setParent(parent);
			child.setParentNodeId(parent.getNodeId());
			child.setPageId(mpage.getPageId());

			// activate the child node
			child.setStatus(MappedNode.ACTIVE);

			// determine order on parent
			Integer max = getWorkingNodeDAO().getMaxChildOrderOnParent(
					parent.getNodeId());
			if (max != null) {
				int order = max + 1;
				child.setOrderOnParent(order);
			}

			if (getWorkingPageDAO().getNodeHasPage(child)) {
				// tweak parent page setting
				MappedPage childPage = getWorkingPageDAO()
						.getPageForNode(child);
				childPage.setParentPageId(mpage.getPageId());
				getWorkingPageDAO().savePage(childPage);
				// tweak page ancestry
				List<Long> pageIds = getWorkingPageDAO().getPageIdsForNodeIds(
						nodeAncestorIds);
				if (pageIds != null && !pageIds.isEmpty()) {
					Long pageId = getWorkingPageDAO().getPageIdForNode(child);
					getWorkingPageDAO().resetAncestorsForPage(pageId, pageIds);
				}
			}

			// save node to working
			getWorkingNodeDAO().saveNode(child);
			// push changes to misc
			pushNode(child);

			// a node is an ancestor of itself - maintain this identity relation
			nodeAncestorIds.add(child.getNodeId());
			// reset node ancestry
			getMiscNodeDAO().resetAncestorsForNode(child.getNodeId(),
					nodeAncestorIds);

			// log the action performed
			MetaNode targetMetaInfo = getMetaNodeDAO().getMetaNode(parent);
			logManualReconcile(tuple.getMetaNode(), targetMetaInfo);
		}
	}

	/*
	 * We're attempting to preserve the node-id - so the source has the node-id
	 * we want, but we need to resolve some things before getting down to
	 * business:
	 *  - get other names, add them to source - copy values from target into
	 * source (only really want to preserve id) - clear out things like
	 * source-db, source-db-node-id - reattach target's objects to source
	 */
	private void executeSwap(SwapTuple tuple) {
		MappedNode source = tuple.getMetaNode().getNode();
		MappedNode target = getWorkingNodeDAO().getNodeWithId(
				tuple.getTargetNodeId(), true);

		moveNameToTarget(tuple, source, target);

		if (source != null && target != null) {
			MetaNode targetMetaInfo = getMetaNodeDAO().getMetaNode(target);

			logManualReconcile(tuple.getMetaNode(), targetMetaInfo);
			logRetirement(target, null);
			// updates parent and ancestor relations
			getMiscNodeDAO().swapNodeAncestry(source.getNodeId(),
					target.getNodeId());
			source.copyValues(target, true); // calling copyValues(other, 
											 // mergeOtherNames) will merge
											 // the othernames of both nodes
			moveChildren(source, target);
			clearTransientProperties(source);
			reattachObjects(tuple.getMetaNode(), targetMetaInfo);
			getWorkingNodeDAO().saveNode(source);
			pushNode(source);

			updateCladename(source, target.getName());

			target.setStatus(MappedNode.RETIRED);
			getWorkingNodeDAO().saveNode(target);
			pushNode(target);
		}
	}

	@SuppressWarnings("unchecked")
	private void moveNameToTarget(SwapTuple tuple, MappedNode source,
			MappedNode target) {
		if (tuple.getMoveNameToTarget()) {
			MappedOtherName moname = new MappedOtherName();
			moname.setName(source.getName());
			moname.setAuthority(source.getNameAuthority());
			moname.setAuthorityYear(source.getAuthorityDate());
			moname.setItalicize(source.getItalicizeName());
			moname.setComment(source.getNameComment());
			moname.setIsPreferred(false);
			moname.setIsImportant(false);
			if (target.getSynonyms() != null) {
				target.addSynonym(moname);
			}
		}
	}

	private void logRemoval(MetaNode metaNode) {
		ManualReconciliationRecord record = new ManualReconciliationRecord();
		String manifest = createManifestForMetaNode(metaNode);
		record.setBasalNodeId(getBasalNodeId());
		record.setSourceNodeId(metaNode.getNode().getNodeId());
		record.setSourceNodeName(metaNode.getNode().getName());
		record.setSourceNodeManifest(manifest);
		record.setTargetNodeManifest("");
		record.setTargetNodeName("");
		getManualReconcileRecords().add(record);
	}

	private void logManualReconcile(MetaNode sourceMetaNode,
			MetaNode targetMetaNode) {
		ManualReconciliationRecord record = new ManualReconciliationRecord();
		String sourceManifest = createManifestForMetaNode(sourceMetaNode);
		String targetManifest = createManifestForMetaNode(targetMetaNode);

		record.setBasalNodeId(getBasalNodeId());
		record.setSourceNodeId(sourceMetaNode.getNode().getNodeId());
		record.setSourceNodeName(sourceMetaNode.getNode().getName());
		record.setSourceNodeManifest(sourceManifest);
		record.setTargetNodeId(targetMetaNode.getNode().getNodeId());
		record.setTargetNodeName(targetMetaNode.getNode().getName());
		record.setTargetNodeManifest(targetManifest);
		getManualReconcileRecords().add(record);
	}

	private String createManifestForMetaNode(MetaNode metaNode) {
		MetaNodeTuple tuple = new MetaNodeTuple();
		tuple.setMetaNode(metaNode);
		HashMap<Long, MetaNodeTuple> map = new HashMap<Long, MetaNodeTuple>();
		map.put(metaNode.getNode().getNodeId(), tuple);
		ObjectManifestPreparer omPrep = new ObjectManifestPreparer(
				getBasalNodeId(), map);

		return omPrep.toElement().toXML();
	}

	/**
	 * Make target's children become source's children
	 * 
	 * @param source
	 *            new parent node
	 * @param target
	 *            old parent node
	 */
	private void moveChildren(MappedNode source, MappedNode target) {
		// make the change in the database
		getWorkingNodeDAO().swapNodeParenthood(source.getNodeId(),
				target.getNodeId());
		getMiscNodeDAO().swapNodeParenthood(source.getNodeId(),
				target.getNodeId());
		// make the change in memory
		ArrayList tmpKids = new ArrayList(target.getChildren());
		for (Iterator itr = tmpKids.iterator(); itr.hasNext();) {
			MappedNode child = (MappedNode) itr.next();
			target.removeChild(child);
			source.addChild(child);
		}
	}

	private void updateCladename(MappedNode source, String name) {
		MappedPage mpage = getWorkingPageDAO()
				.getPageWithId(source.getPageId());
		if (mpage != null) {
			mpage.setGroupName(name);
			getWorkingPageDAO().savePage(mpage);
		}
	}

	private void reattachObjects(MetaNode sourceMetaInfo,
			MetaNode targetMetaInfo) {
		// this is sort of confusing, but the target is the "source" of objects
		// to reattach
		// and the source is our "target" for where they'll be attached when
		// complete.
		Long sourceNodeId = sourceMetaInfo.getNode().getNodeId();
		Long targetNodeId = targetMetaInfo.getNode().getNodeId();
		// my most abject apologies for this abuse of naming - but I think it's
		// best when
		// you wrap your head around it ... hopefully

		// if the target has a page, and source doesn't - move it over to the
		// source.
		if (targetMetaInfo.getHasPageAttached()
				&& !sourceMetaInfo.getHasPageAttached()) {
			Long pageId = targetMetaInfo.getPage().getPageId();
			Long oldNodeId = targetMetaInfo.getNode().getNodeId();
			Long newNodeId = sourceMetaInfo.getNode().getNodeId();
			getWorkingPageDAO().reattachPage(pageId, oldNodeId, newNodeId);
		}

		// we're replacing the target node with source - so anything attached to
		// target
		// must be moved over to the source - cool? good...
		reattachContributors(sourceNodeId, targetNodeId, targetMetaInfo);
		reattachAccessoryPages(sourceNodeId, targetNodeId, targetMetaInfo);
		reattachMediaFiles(sourceNodeId, targetNodeId, targetMetaInfo);
	}

	private void reattachContributors(Long newNodeId, Long oldNodeId,
			MetaNode mnode) {
		for (Contributor c : mnode.getContributors()) {
			getContributorDAO().reattachContributor(Long.valueOf(c.getId()),
					oldNodeId, newNodeId);
		}
	}

	private void reattachAccessoryPages(Long newNodeId, Long oldNodeId,
			MetaNode mnode) {
		for (MappedAccessoryPage accPage : mnode.getAccessoryPages()) {
			getWorkingAccessoryPageDAO().reattachAccessoryPage(
					Long.valueOf(accPage.getId()), oldNodeId, newNodeId);
		}
	}

	private void reattachMediaFiles(Long newNodeId, Long oldNodeId,
			MetaNode mnode) {
		for (NodeImage mediafile : mnode.getMedia()) {
			// reattach image in the database
			getImageDAO().reattachImage(Long.valueOf(mediafile.getId()), oldNodeId,
					newNodeId);
			// detach image relation 
			mediafile.removeFromNodesSet(mnode.getNode());
			getImageDAO().saveImage(mediafile);
		}
	}

	private void clearTransientProperties(MappedNode mnode) {
		mnode.setSourceDbId(null);
		mnode.setSourceDbNodeId(null);
	}

	private void pushNode(MappedNode mnode) {
		try {
			getNodePusher().pushNodeToDB(mnode, getMiscNodeDAO());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeStructures() {
		if (getBasalNodeId() != null) {
			setRetiredNodes(new ArrayList<MappedNode>());
			// this seems to be throwing org.hibernate.ObjectNotFoundExceptions
			List<MappedNode> inactiveNodes = NodeHelper
					.getInactiveNodesForClade(getBasalNodeId(),
							getMiscNodeDAO(), getWorkingNodeDAO());
			List<Long> inactiveNodeIds = new ArrayList<Long>();
			for (MappedNode mnode : inactiveNodes) {
				// filter out unnamed, internal nodes and retire them when the
				// others.
				if (mnode != null && StringUtils.notEmpty(mnode.getName())) {
					inactiveNodeIds.add(mnode.getNodeId());
				} else {
					getRetiredNodes().add(mnode);
				}
			}
			if  (getSwapTuples() == null || getRefreshTuples()) {
				List<MetaNode> metaNodes = getMetaNodeDAO().getMetaNodes(
						inactiveNodeIds, true);
				createTupleStructure(metaNodes);
			}
		}
	}

	private void createTupleStructure(List<MetaNode> metaNodes) {
		List<SwapTuple> tuples = new ArrayList<SwapTuple>();
		for (MetaNode mnode : metaNodes) {
			tuples.add(new SwapTuple(mnode));
		}
		setSwapTuples(tuples);
	}

	private void logRetirement(MappedNode mnode, Contributor resolver) {
		NodeRetirementRecord record = new NodeRetirementRecord();
		record.setNodeId(mnode.getNodeId());
		record.setNodeName(mnode.getName());
		record.setRetiredFromClade(getBasalNodeId());
		record.setRetiredBy((resolver != null) ? resolver.getEmail()
				: DEFAULT_RETIRED_BY);
		getNodeRetirementRecords().add(record);
	}

	protected String getPopupLinkText(String relativeLink, Long id) {
		return "<a href=\"#\" onclick=\"javascript:window.open('"
				+ relativeLink
				+ "/"
				+ id
				+ "', 'width=650,height=450,scrollbars=no,menubar=no,location=no,status=no,toolbar=no'); return false;\">"
				+ id + "</a>";
	}

	public PopupLinkRenderer getRenderer() {
		int width = 750;
		int height = 550;
		return getRendererFactory().getLinkRenderer("", width, height);
	}

	public String getCurrentMediaFileLink() {
		return (getCurrentMediaFile() != null) ? getPopupLinkText("/media",
				Long.valueOf(getCurrentMediaFile().getId())) : "[id-null]";
	}

	public String getCurrentContributorLink() {
		return (getCurrentContributor() != null) ? getPopupLinkText("/people",
				Long.valueOf(getCurrentContributor().getId())) : "[id-null]";
	}

	public String getCurrentAccessoryPageLink() {
		return (getCurrentAccessoryPage() != null) ? "notes/?note_id="
				+ getCurrentAccessoryPage().getId() : "#";
	}

}