package org.tolweb.tapestry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.link.PopupLinkRenderer;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.NodeObjectManagementRecord;
import org.tolweb.hibernate.NodeRetirementRecord;
import org.tolweb.misc.MetaNode;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.xml.taxaimport.SwapTuple;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

public abstract class NodeObjectManager extends BasePage implements
		BaseInjectable, MetaInjectable, NodeInjectable, PageInjectable, 
		ImageInjectable, AccessoryInjectable, CookieInjectable {

	public static final String PAGE_ID = "pageIdCtrl";
	public static final String ACC_PAGES_ID = "accPagesIdCtrl";
	public static final String MEDIA_ID = "mediaIdCtrl";
	public static final String OTHERNAMES_ID = "othernamesIdCtrl";

	public SimpleDateFormat dateInfoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	
	@Bean
	public abstract ValidationDelegate getDelegate();

	@Persist("session")
	public abstract Long getSelectedNodeId();
	public abstract void setSelectedNodeId(Long nodeId);

	public abstract MappedNode getSelectedNode();
	public abstract void setSelectedNode(MappedNode mnode);

	public abstract Long getDestNodeId();

	public abstract void setDestNodeId(Long nodeId);

	@InitialValue("false")
	public abstract boolean getShouldPermanentlyInactivate();

	public abstract void setShouldPermanentlyInactivate(boolean value);

	public abstract boolean isCurrentPageSelected();

	public abstract void setCurrentPageSelected(boolean value);

	@Persist("session")
	public abstract SwapTuple getCurrentTuple();

	public abstract void setCurrentTuple(SwapTuple tuple);

	public abstract MappedAccessoryPage getCurrentAccessoryPage();

	public abstract void setCurrentAccessoryPage(MappedAccessoryPage accPage);

	@Persist("session")
	public abstract List<MappedAccessoryPage> getAllAccessoryPages();

	public abstract void setAllAccessoryPages(List<MappedAccessoryPage> accPages);

	@Persist("session")
	public abstract List<MappedAccessoryPage> getSelectedAccessoryPages();

	public abstract void setSelectedAccessoryPages(
			List<MappedAccessoryPage> accPages);

	public abstract Long getCurrentOtherNameId();

	public abstract void setCurrentOtherNameId(Long monameId);

	@Persist("session")
	public abstract List<String> getLogEntries();
	public abstract void setLogEntries(List<String> entries);
	
	public abstract List<String> getErrorMessages();
	public abstract void setErrorMessages(List<String> messages);
	
	public abstract String getCurrentErrorMessage();
	public abstract void setCurrentErrorMessage(String msg);
	
	public String getCurrentOtherNameText() {
		if (getCurrentTuple() != null
				&& getCurrentTuple().getMetaNode() != null
				&& getCurrentTuple().getMetaNode().getOtherNameIds() != null) {
			return getCurrentTuple().getMetaNode().getOtherNameIds().get(
					getCurrentOtherNameId());
		}
		return "[other name text unavailable]";
	}

	@Persist("session")
	public abstract List<Long> getAllOtherNameIds();

	public abstract void setAllOtherNameIds(List<Long> otherNameIds);

	@Persist("session")
	public abstract List<Long> getSelectedOtherNameIds();

	public abstract void setSelectedOtherNameIds(List<Long> otherNameIds);

	public abstract NodeImage getCurrentMediaFile();

	public abstract void setCurrentMediaFile(NodeImage mediaFile);

	@Persist("session")
	public abstract List<NodeImage> getAllMediaFiles();

	public abstract void setAllMediaFiles(List<NodeImage> mediaFiles);

	@Persist("session")
	public abstract List<NodeImage> getSelectedMediaFiles();

	public abstract void setSelectedMediaFiles(List<NodeImage> mediaFiles);

	@Persist("session")
	public abstract String getNodeName();
	public abstract void setNodeName(String nodeName);
	
	@Persist("session")
	public abstract Long getNodeId();
	public abstract void setNodeId(Long nodeId);
	
	public void selectOperation(IRequestCycle cycle) {
		setSelectedNode(getWorkingNodeDAO().getNodeWithId(getSelectedNodeId(), true));
		setSelectedNodeId(null);
		if (getSelectedNode() != null) {
			setNodeName(getSelectedNode().getName());
			setNodeId(getSelectedNode().getNodeId());
			
			MetaNode meta = getMetaNodeDAO().getMetaNode(getSelectedNode());
			SwapTuple currentTuple = new SwapTuple(meta);
			setCurrentTuple(currentTuple);

			setAllAccessoryPages(meta.getAccessoryPages());
			setSelectedAccessoryPages(new ArrayList<MappedAccessoryPage>());

			setAllMediaFiles(meta.getMedia());
			setSelectedMediaFiles(new ArrayList<NodeImage>());

			setAllOtherNameIds(new ArrayList<Long>(meta.getOtherNameIds().keySet()));
			setSelectedOtherNameIds(new ArrayList<Long>());
		}
	}

	public boolean isCurrentAccessoryPageSelected() {
		return getSelectedAccessoryPages().contains(getCurrentAccessoryPage());
	}

	public void setCurrentAccessoryPageSelected(boolean value) {
		if (getSelectedAccessoryPages() != null) {
			if (value) {
				getSelectedAccessoryPages().add(getCurrentAccessoryPage());
			} else {
				getSelectedAccessoryPages().remove(getCurrentAccessoryPage());
			}
		}
	}

	public boolean isCurrentOtherNameSelected() {
		return getSelectedOtherNameIds().contains(getCurrentOtherNameId());
	}

	public void setCurrentOtherNameSelected(boolean value) {
		if (value) {
			getSelectedOtherNameIds().add(getCurrentOtherNameId());
		} else {
			getSelectedOtherNameIds().remove(getCurrentOtherNameId());
		}
	}

	public boolean isCurrentMediaFileSelected() {
		return getSelectedMediaFiles().contains(getCurrentMediaFile());
	}

	public void setCurrentMediaFileSelected(boolean value) {
		if (value) {
			getSelectedMediaFiles().add(getCurrentMediaFile());
		} else {
			getSelectedMediaFiles().remove(getCurrentMediaFile());
		}
	}

	public void moveOperation(IRequestCycle cycle) {
		if (getDestNodeId() != null) {
			setErrorMessages(new ArrayList<String>());
			setLogEntries(new ArrayList<String>());
			Date now = new Date();
			String logStartFmt = "[%1$s] Performing Object Reattachment from node id: %2$s to node id: %3$s";
			String dateFmt = dateInfoFormat.format(now);
			String logStart = String.format(logStartFmt, dateFmt, getNodeId(), getDestNodeId());
			getLogEntries().add(logStart);
			System.out.println(logStart);
			
			Long oldNodeId = getNodeId();
			MappedNode oldNode = getWorkingNodeDAO().getNodeWithId(oldNodeId, true);
			Long newNodeId = getDestNodeId();
			
			movePage(oldNodeId, newNodeId);
			moveMediaFiles(oldNodeId, newNodeId);
			moveOtherNames(oldNode, oldNodeId, newNodeId);
			moveAccessoryPages(oldNodeId, newNodeId);
			
			retireNode(oldNode);
			
			logOperation(oldNodeId, newNodeId);
		} else {
			setSelectedNodeId(getNodeId());
			return;			
		}
	}

	private void logOperation(Long oldNodeId, Long newNodeId) {
		Contributor resolver = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		NodeObjectManagementRecord record = new NodeObjectManagementRecord();
		record.setSourceNodeId(oldNodeId);
		record.setDestNodeId(newNodeId);
		record.setLogEntry(createLogEntryHtml());
		record.setModifiedBy((resolver != null) ? resolver.getEmail() : "[unavailable]");
		getNodeObjectManagementLogDAO().create(record);
	}

	private String createLogEntryHtml() {
		StringBuilder entriesHtml = new StringBuilder();
		entriesHtml.append("<ul>");
		for (String entry : getLogEntries()) {
			String fmt = "<li>%1$s</li>";
			entriesHtml.append(String.format(fmt, entry));
		}
		entriesHtml.append("</ul>");
		return entriesHtml.toString();
	}

	private void movePage(Long oldNodeId, Long newNodeId) {
		if (isCurrentPageSelected()) {
			Date now = new Date();
			boolean newNodeHasPage = getWorkingPageDAO().getNodeHasPage(newNodeId);
			if (!newNodeHasPage) {
				String logPageMoveFmt = "[%1$s] \tReattaching page id: %2$s to node id: %3$s";
				Long pageId = getCurrentTuple().getMetaNode().getPage().getPageId();
				getWorkingPageDAO().reattachPage(pageId, oldNodeId, newNodeId);
				getLogEntries().add(String.format(logPageMoveFmt, dateInfoFormat.format(now), pageId, newNodeId));
			} else {
				String logPageMoveFmt = "[%1$s] Error - cannot attach page to node id: %2$s because it already has a page attached.";
				getErrorMessages().add(String.format(logPageMoveFmt, dateInfoFormat.format(now), newNodeId));
			}
		}
	}

	private void moveMediaFiles(Long oldNodeId, Long newNodeId) {
		if (getSelectedMediaFiles() != null && !getSelectedMediaFiles().isEmpty()) {
			Date now = new Date();
			for (NodeImage img : getSelectedMediaFiles()) {
				String logMediaMoveFmt = "[%1$s] \tReattaching media id: %2$s to node id: %3$s";
				Long imageId = Long.valueOf(img.getId());
				getImageDAO().reattachImage(imageId, oldNodeId, newNodeId);
				getLogEntries().add(String.format(logMediaMoveFmt, dateInfoFormat.format(now), imageId, newNodeId));
			}
		}
	}

	private void moveOtherNames(MappedNode oldNode, Long oldNodeId, Long newNodeId) {
		if (getSelectedOtherNameIds() != null && !getSelectedOtherNameIds().isEmpty()) {
			Date now = new Date();
			int sequence = 0;
			if (oldNode != null && oldNode.getSynonyms() != null) {
				sequence = oldNode.getSynonyms().size();
			}
			for (Long otherNameId : getSelectedOtherNameIds()) {
				String logOtherNameMoveFmt = "[%1$s] \tReattaching other name id: %2$s to node id: %3$s";
				getWorkingOtherNameDAO().reattachOtherName(otherNameId, oldNodeId, newNodeId, sequence);
				getLogEntries().add(String.format(logOtherNameMoveFmt, dateInfoFormat.format(now), otherNameId, newNodeId));
				sequence++;
			}
		}
	}

	private void moveAccessoryPages(Long oldNodeId, Long newNodeId) {
		if (getSelectedAccessoryPages() != null	&& !getSelectedAccessoryPages().isEmpty()) {
			Date now = new Date();
			for (MappedAccessoryPage accPage : getSelectedAccessoryPages()) {
				String logAccPageMoveFmt = "[%1$s] \tReattaching accessory page id: %2$s to node id: %3$s";
				Long accPageId = accPage.getAccessoryPageId();
				getWorkingAccessoryPageDAO().reattachAccessoryPage(accPageId, oldNodeId, newNodeId);
				getLogEntries().add(String.format(logAccPageMoveFmt, dateInfoFormat.format(now), accPageId, newNodeId));
			}
		}
	}

	private void retireNode(MappedNode oldNode) {
		if (getShouldPermanentlyInactivate()) {
			logNodeRetirement(oldNode);
			oldNode.setStatus(MappedNode.RETIRED);
			getWorkingNodeDAO().saveNode(oldNode);
			pushNode(oldNode);
		}
	}

	private void logNodeRetirement(MappedNode oldNode) {
		Date now = new Date();
		String logAccPageMoveFmt = "[%1$s] \tRetired node id: %2$s";
		getLogEntries().add(String.format(logAccPageMoveFmt, dateInfoFormat.format(now), oldNode.getNodeId()));
		Contributor resolver = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		NodeRetirementRecord record = new NodeRetirementRecord();
		record.setNodeId(oldNode.getNodeId());
		record.setNodeName(oldNode.getName());
		record.setRetiredFromClade(oldNode.getNodeId());
		record.setRetiredBy((resolver != null) ? resolver.getEmail() : "[unavailable]");
		getNodeRetirementLogDAO().createNodeRetirementRecord(record);
	}

	public PopupLinkRenderer getRenderer() {
		int width = 750;
		int height = 550;
		return getRendererFactory().getLinkRenderer("Find Basal Node Id",
				width, height);
	}
	
	private void pushNode(MappedNode mnode) {
		try {
			getNodePusher().pushNodeToDB(mnode, getMiscNodeDAO());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
