package org.tolweb.tapestry.xml.taxaimport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.NodePusher;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.NodeHelper;
import org.tolweb.treegrow.main.StringUtils;


public class TaxaImportCheck {
	
	
	public static void filterOtherNames(Long basalNodeId, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, NodePusher nodePusher, List<String> outputLog) {
		MappedNode root = NodeHelper.getClade(basalNodeId, workingNodeDAO);
		OtherNamesFilter.traverseClade(root, miscNodeDAO, workingNodeDAO, nodePusher, outputLog);
	}
	
	public static void performPageValidityCheck(Long basalNodeId, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, PageDAO pageDAO, NodePusher nodePusher, List<String> outputLog) {
		MappedNode root = NodeHelper.getClade(basalNodeId, workingNodeDAO);
		if (root != null) {
			MappedPage pg = pageDAO.getPage(root);
			PageValidityCheck.traverseClade(root, pg, miscNodeDAO, workingNodeDAO, pageDAO, nodePusher, outputLog);
		}
	}
	
	public static void performExtinctCheck(Long basalNodeId, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, NodePusher nodePusher, List<String> outputLog) {
		MappedNode root = NodeHelper.getClade(basalNodeId, workingNodeDAO);
		ExtinctCheck.traverseClade(root, miscNodeDAO, workingNodeDAO, nodePusher, outputLog);
	}
	
	public static boolean performHomonymyCheck(List<String> nodeNames, Set<String> duplicateNames) {
		Set<String> nodeNamesSet = new HashSet<String>();
		findDuplicates(nodeNames, nodeNamesSet, duplicateNames);
		return duplicateNames.isEmpty();
	}
	
	private static void findDuplicates(List<String> names, Set<String> namesSet, Set<String> duplicateNames) {
		for (String name : names) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}
			boolean added = namesSet.add(name);
			if (!added) {
				duplicateNames.add(name);
			}
		}
	}
	
	private static class OtherNamesFilter {
		public static void traverseClade(MappedNode node, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, NodePusher nodePusher, List<String> outputLog) {
			if (node != null) {
				filterDuplicateNames(node, miscNodeDAO, workingNodeDAO, nodePusher, outputLog);
				if (node.getChildren() != null) {
					for (Iterator itr = node.getChildren().iterator(); itr.hasNext(); ) {
						MappedNode child = (MappedNode)itr.next();
						traverseClade(child, miscNodeDAO, workingNodeDAO, nodePusher, outputLog);
					}
				}				
			}
		}
		
		@SuppressWarnings("unchecked")
		private static void filterDuplicateNames(MappedNode node, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, NodePusher nodePusher, List<String> outputLog) {
			SortedSet otherNames = node.getSynonyms();
			Set<String> filterSet = new HashSet<String>();
			for (Iterator itr = new ArrayList(otherNames).iterator(); itr.hasNext(); ) {
				MappedOtherName moname = (MappedOtherName)itr.next();
				String comboKey = moname.getName() + moname.getAuthority() + moname.getAuthorityYear();
				boolean added = filterSet.add(comboKey);
				if (!added) {
					otherNames.remove(moname);
				}
			}
			saveChanges(node, miscNodeDAO, workingNodeDAO, nodePusher);
		}

		private static void saveChanges(MappedNode node, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, NodePusher nodePusher) {
			workingNodeDAO.saveNode(node);
			try {
				nodePusher.pushNodeToDB(node, miscNodeDAO);
				nodePusher.pushNodeToDB(node, workingNodeDAO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class ExtinctCheck {

		public static void traverseClade(MappedNode node, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, NodePusher nodePusher, List<String> outputLog) {
			if (node != null) {
				determineExtinctChange(node, miscNodeDAO, workingNodeDAO, nodePusher, outputLog);
				if (node.getChildren() != null) {
					for (Iterator itr = node.getChildren().iterator(); itr.hasNext(); ) {
						MappedNode child = (MappedNode)itr.next();
						traverseClade(child, miscNodeDAO, workingNodeDAO, nodePusher, outputLog);
					}
				}
			}
		}

		private static void determineExtinctChange(MappedNode node,	NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, NodePusher nodePusher, List<String> outputLog) {
			if (node != null && node.getParentNodeId() != null) {
				MappedNode parent = workingNodeDAO.getNodeWithId(node.getNodeId());
				if (parent != null) {
					if (parent.getExtinct() == MappedNode.EXTINCT) {
						node.setExtinct(MappedNode.EXTINCT);
						workingNodeDAO.saveNode(node);
						handlePushToMisc(node, miscNodeDAO, nodePusher);
						logChange(node, outputLog);
					}
				}
			}
			
		}

		private static void logChange(MappedNode node, List<String> outputLog) {
			StringBuilder msg = new StringBuilder();
			msg.append(node.getName() + "(" + node.getNodeId() + ") ");
			msg.append("- parent is extinct, so node was marked as extinct");
			outputLog.add(msg.toString());
		}

		private static void handlePushToMisc(MappedNode node, NodeDAO miscNodeDAO, NodePusher nodePusher) {
			try {
				nodePusher.pushNodeToDB(node, miscNodeDAO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static class PageValidityCheck {
		
		@SuppressWarnings("unchecked")
		public static void traverseClade(MappedNode nd, MappedPage pg, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, PageDAO pageDAO, NodePusher nodePusher, List<String> outputLog) {
			if (nd != null) {
				determinePageIdChange(nd, pg, miscNodeDAO, workingNodeDAO, pageDAO, nodePusher, outputLog);
				if (nd.getChildren() != null) {
					for (Iterator itr = nd.getChildren().iterator(); itr.hasNext(); ) {
						MappedNode child = (MappedNode)itr.next();
						pg = pageDAO.getPageForNode(child);
						traverseClade(child, pg, miscNodeDAO, workingNodeDAO, pageDAO, nodePusher, outputLog);
					}
				}
				
				Set ancestors = miscNodeDAO.getAncestorsForNode(nd.getNodeId());
				List<Long> pageIds = pageDAO.getPageIdsForNodeIds(ancestors);
				if (pageIds != null && !pageIds.isEmpty()) {
					Long pageId = pageDAO.getPageIdForNode(nd);
					if (pageId != null) {
						pageDAO.resetAncestorsForPage(pageId, pageIds);
					}
				} else {
					logNodeAncestorIssue(nd, outputLog);
				}
			}
		}

		private static void logNodeAncestorIssue(MappedNode nd,
				List<String> outputLog) {
			StringBuilder errMsg = new StringBuilder();
			errMsg.append("For node: " + nd.toString());
			errMsg.append(", it appears that node has no entries in the NodeAncestors table. ");
			errMsg.append("This means that it may not be considered 'reachable' by it's parent ");
			errMsg.append("in some situations (like media galleries) and may lead to issues. ");
			outputLog.add(errMsg.toString());
			System.out.println("ERROR - NODEANCESTORS CONSTRAINT ISSUE: " + errMsg.toString());
		}
		
		@SuppressWarnings("unchecked")
		private static void determinePageIdChange(MappedNode mnode, MappedPage mpage, NodeDAO miscNodeDAO, NodeDAO workingNodeDAO, PageDAO pageDAO, 
												  NodePusher nodePusher, List<String> outputLog) {
			Long ccgrpId = NodeHelper.findClosestContainingGroupWithPage(mnode.getNodeId(), miscNodeDAO, pageDAO);
			Long ccgrpPageId = pageDAO.getPageId(ccgrpId);
			boolean hasPage = pageDAO.getNodeHasPage(mnode);
			
			if (!mnode.getPageId().equals(ccgrpPageId) && 
					hasValidPageInfo(mnode, ccgrpPageId)) {
				logChange(mnode, ccgrpPageId, outputLog);
				
				mnode.setPageId(ccgrpPageId);
				workingNodeDAO.saveNode(mnode);
				try {
					nodePusher.pushNodeToDB(mnode, miscNodeDAO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (hasPage && (mpage != null && mpage.getParentPageId() != null && !mpage.getParentPageId().equals(ccgrpPageId))) {
				mpage.setParentPageId(ccgrpPageId);
				pageDAO.savePage(mpage);
			}
		}
		
		private static boolean hasValidPageInfo(MappedNode mnode,
				Long ccgrpPageId) {
			Long invalidId = Long.valueOf(0);
			return (mnode != null && mnode.getPageId() != null && 
					!mnode.getPageId().equals(invalidId)) && 
					(ccgrpPageId != null && !ccgrpPageId.equals(invalidId));
		}

		private static String internalRewritePageLink(Long pageId) {
			return "" + pageId; //"<a href=\"/tol-pages/?tol_page_id=" + pageId + "\">" + pageId + "</a>";
		}
		
		private static void logChange(MappedNode mnode, Long ccgrpPageId, List<String> outputLog) {
			StringBuilder msg = new StringBuilder();
			msg.append(mnode.getName() + "(" + mnode.getNodeId() + ")");
			msg.append(" - node originally indicated as appearing on page (page id: " + internalRewritePageLink(mnode.getPageId()) + ")");
			msg.append(" - moved to closest containing group with page (page id: " + internalRewritePageLink(ccgrpPageId) + ")");
			outputLog.add(msg.toString());
		}
	}
}
