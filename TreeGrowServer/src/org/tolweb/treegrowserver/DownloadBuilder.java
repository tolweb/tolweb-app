/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.dao.DownloadDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadBuilder {
	private DownloadDAO downloadDAO;
	private NodeDAO nodeDAO;
	private PageDAO pageDAO;
	private ImageDAO imageDAO;
	private ContributorDAO contributorDAO;
	private AccessoryPageDAO accPageDAO;
	private ServerXMLWriter serverXMLWriter;
	private PermissionChecker permissionChecker;
	
	// Used to generate the full xml tree for the weekly data dump
	private int INFINITE_DEPTH = -1000;
	
	public Document buildFullDataDumpDocument() {
		return buildDownload(new Long(1), INFINITE_DEPTH, false, false, null, null, false, false);
	}
	
	public Document buildDownload(Long rootNodeId, int depth, boolean isComplete, 
			boolean checkLocks, Contributor contributor, Download existingDownload,
			boolean dontCheckSubmitted, boolean includeContributors) {
		MappedNode node = nodeDAO.getNodeWithId(rootNodeId);
		if (node == null) {
			return getNoNodeFoundDocument();
		} else {
			return startBuildTree(node, depth, isComplete, checkLocks, contributor, existingDownload, 
					dontCheckSubmitted, includeContributors);			
		}
	}
	
	public Document buildDownload(String rootNodeName, int depth, boolean isComplete, boolean includeContributors) {
		return buildDownload(rootNodeName, depth, isComplete, true, null, null, false,
				includeContributors);
	}
	
	public Document buildDownload(String rootNodeName, int depth, boolean isComplete, 
			boolean checkLocks, Contributor contributor, Download existingDownload,
			boolean dontCheckSubmitted, boolean includeContributors) {
		List matchingNodes = nodeDAO.findNodesExactlyNamed(rootNodeName);
		if (matchingNodes.size() == 0) {
			return getNoNodeFoundDocument();
		} else {
			MappedNode node = (MappedNode) matchingNodes.get(0);
			return startBuildTree(node, depth, isComplete, checkLocks, contributor, existingDownload, 
					dontCheckSubmitted, includeContributors);
		}
	}

	private Document startBuildTree(MappedNode rootNode, int depth, 
			boolean isComplete, boolean checkLocks, Contributor contr,
			Download existingDownload, boolean dontCheckSubmitted, boolean includeContributors) {
		Download download = null;
		// Check to see if the node is locked before starting
		if (checkLocks) {
			Element lockedElement = downloadDAO.getNodeIsLocked(rootNode, contr);
			// Return the lock element if it's locked or it's submitted and it's not 
			// an editor checking out a submitted batch
			if (lockedElement != null) {
				if (lockedElement.getAttributeValue(XMLConstants.TYPE).equals(XMLConstants.SUBMITTED)) {
					if (!dontCheckSubmitted) {
						return getNodeIsLockedDocument(lockedElement);
					}
				} else {
					return getNodeIsLockedDocument(lockedElement);
				}
			}
		}
		if (depth > 0 && checkLocks && existingDownload == null) {
			download = new Download();
			download.setDownloadDate(new Date());
			download.setIsActive(true);
			download.setRootNode(rootNode);
			download.setDownloadedNodes(new HashSet());
			download.setIpAddress("");
			download.setContributor(contr);
		} else if (existingDownload != null) {
			download = existingDownload;
		}
		Element treeElement = new Element(XMLConstants.TREE);
		Element nodesElement = new Element(XMLConstants.NODES);
		Element depthElement = new Element(XMLConstants.DEPTH);
		depthElement.setText("" + depth);
		treeElement.addContent(depthElement);
		treeElement.addContent(nodesElement);
		Set imageIds = new HashSet();
		Set contributorIds = new HashSet();
		StringBuffer treeString = new StringBuffer();
		if (includeContributors) {
	        // Send down all the contributors for the ancestors
	        List otherContributorIds = permissionChecker.getContributorIdsWithPermissionsForAncestorNodes(rootNode.getNodeId());
	        contributorIds.addAll(otherContributorIds);
		}
		if (depth != 0) {
			addToTree(rootNode, depth, nodesElement, isComplete, download, imageIds, contributorIds, checkLocks, treeString, contr,
					includeContributors);
		} else {		    
			Element nodeElement = serverXMLWriter.constructAndFillOutNodeElement(rootNode, isComplete, imageIds, contributorIds, contr, 
					includeContributors);
			nodesElement.addContent(nodeElement);
			treeString.append(rootNode.getNodeId());
		}
		// Remove the last ',' since it's not needed
		if (treeString.charAt(treeString.length() - 1) == ',') {
			treeString.deleteCharAt(treeString.length() - 1);
		}
		Element treeStructureElement = new Element(XMLConstants.TREESTRUCTURE);
		treeElement.addContent(treeStructureElement);
		treeStructureElement.setText(treeString.toString());
		if (download != null) {
			if (existingDownload == null) {
				getDownloadDAO().createNewDownload(download);
			} else {
				getDownloadDAO().saveDownload(download);
			}
			Element downloadIdElement = new Element(XMLConstants.DOWNLOAD_ID);
			downloadIdElement.setText(download.getDownloadId().toString());
			treeElement.addContent(downloadIdElement);
		}
		if (isComplete && imageIds.size() > 0) {
			Element imgsElement = new Element(XMLConstants.IMAGELIST);
			treeElement.addContent(imgsElement);
			List imgs = getImageDAO().getImagesWithIds(imageIds);
			Iterator it = imgs.iterator();
			while (it.hasNext()) {
				NodeImage img = (NodeImage) it.next();
				if (img.getCopyrightOwnerContributor() != null) {
					contributorIds.add(new Integer(img.getCopyrightOwnerContributor().getId()));
				}
				Element imgElement = serverXMLWriter.encodeImage(img);
				imgsElement.addContent(imgElement);
			}
		}
		Element contributorsElement = new Element(XMLConstants.CONTRIBUTORLIST);
		treeElement.addContent(contributorsElement);		
		if (contributorIds.size() > 0) {
			List contrs = getContributorDAO().getContributorsWithIds(contributorIds, false);
			Iterator it = contrs.iterator();
			while (it.hasNext()) {
				Contributor contributor = (Contributor) it.next();
				Element contrElement = serverXMLWriter.encodeContributor(contributor, true);
				contributorsElement.addContent(contrElement);
			}
		}
		return new Document(treeElement);		
	}
	
	private Element addToTree(MappedNode node, int depth, Element currentParentElement, boolean isComplete, 
			Download download, Set imgs, Set contributors, boolean checkLocks, StringBuffer treeString,
			Contributor contributor, boolean includeContributors) {
		Element nodeElement = serverXMLWriter.constructAndFillOutNodeElement(node, isComplete, imgs, contributors, contributor, includeContributors);
		currentParentElement.addContent(nodeElement);
		Element lockedElement = null;
		if (checkLocks) {
			lockedElement = getDownloadDAO().getNodeIsLocked(node, contributor);
			// This is an editor checking out a submitted batch, so don't add the lock
			if (lockedElement != null && lockedElement.getAttributeValue(XMLConstants.BATCHID) != null) {
				lockedElement = null;
			}
		}
		treeString.append(node.getNodeId());
		boolean hasPage = pageDAO.getNodeHasPage(node);
		if ((depth == INFINITE_DEPTH || depth >= 0) && lockedElement == null) {
			List children = nodeDAO.getChildrenNodes(node);
			int checkoutStatus;
			if (depth != 0 || !hasPage) {
				checkoutStatus = DownloadNode.ACTIVE;
				Iterator it = children.iterator();
				Element nodesElement = new Element(XMLConstants.NODES);
				nodeElement.addContent(nodesElement);
				if (hasPage && depth != INFINITE_DEPTH) {
					depth -= 1;
				}
				boolean hadChildren = false;
				if (it.hasNext()) {
					treeString.append("(");
					hadChildren = true;
				} else {
					treeString.append(",");
				}
				while (it.hasNext()) {
					MappedNode nextNode = (MappedNode) it.next();
					addToTree(nextNode, depth, nodesElement, isComplete, 
							download, imgs, contributors, checkLocks, treeString, contributor, includeContributors);
				}
				if (hadChildren) {
					// Make sure the last comma is removed
					if (treeString.charAt(treeString.length() - 1) == ',') {
						treeString.deleteCharAt(treeString.length() - 1);
					}					
					treeString.append("),");
				}
			} else {
				checkoutStatus = DownloadNode.BARRIER_ACTIVE;
				treeString.append(",");
			}
			if (download != null) {
			    addNodeToDownload(download, node, checkoutStatus);
			}
		} else if (lockedElement != null) {
		    if (download != null) {
		        addNodeToDownload(download, node, DownloadNode.BARRIER_ACTIVE);
		    }
			nodeElement.addContent(lockedElement);
			treeString.append(",");
		}
		return nodeElement;
	}
	
	private void addNodeToDownload(Download download, MappedNode node, int checkoutStatus) {
		DownloadNode dn = new DownloadNode();
		dn.setActive(checkoutStatus);
		dn.setNodeId(node.getNodeId());
		if(download.getDownloadedNodes().contains(dn)) {
			// In this case, it's a subtree download so remove the old copy
			// of the node from the download and add the new one (since the
			// status has changed)
			download.removeFromDownloadedNodes(dn);
		}
		download.addToDownloadedNodes(dn);	    
	}
	
	private Document getNoNodeFoundDocument() {
		Document document = new Document();
		Element rootElement = new Element(XMLConstants.TREE);
		rootElement.setText("no tree found");
		document.setRootElement(rootElement);
		return document;
	}
	
	private Document getNodeIsLockedDocument(Element lockElement) {
		Document document = new Document();
		Element errorElement = new Element(XMLConstants.ERROR);
		errorElement.addContent(new Element(XMLConstants.ERRORNUM).setText("10102"));
		errorElement.addContent(new Element(XMLConstants.ERRORTEXT).setText("The tree you have requested has been checked out.  \n\r You may not download this tree until it has been checked back in."));
		errorElement.addContent(lockElement);
		document.setRootElement(errorElement);
		return document;
	}

	/**
	 * @return Returns the accPageDao.
	 */
	public AccessoryPageDAO getAccPageDAO() {
		return accPageDAO;
	}
	/**
	 * @param accPageDao The accPageDao to set.
	 */
	public void setAccPageDAO(AccessoryPageDAO accPageDao) {
		this.accPageDAO = accPageDao;
	}
	/**
	 * @return Returns the downloadDao.
	 */
	public DownloadDAO getDownloadDAO() {
		return downloadDAO;
	}
	/**
	 * @param downloadDao The downloadDao to set.
	 */
	public void setDownloadDAO(DownloadDAO downloadDao) {
		this.downloadDAO = downloadDao;
	}
	/**
	 * @return Returns the imageDao.
	 */
	public ImageDAO getImageDAO() {
		return imageDAO;
	}
	/**
	 * @param imageDao The imageDao to set.
	 */
	public void setImageDAO(ImageDAO imageDao) {
		this.imageDAO = imageDao;
	}
	/**
	 * @return Returns the nodeDao.
	 */
	public NodeDAO getNodeDAO() {
		return nodeDAO;
	}
	/**
	 * @param nodeDao The nodeDao to set.
	 */
	public void setNodeDAO(NodeDAO nodeDao) {
		this.nodeDAO = nodeDao;
	}
	/**
	 * @return Returns the pageDao.
	 */
	public PageDAO getPageDAO() {
		return pageDAO;
	}
	/**
	 * @param pageDao The pageDao to set.
	 */
	public void setPageDAO(PageDAO pageDao) {
		this.pageDAO = pageDao;
	}
	/**
	 * @return Returns the serverXMLWriter.
	 */
	public ServerXMLWriter getServerXMLWriter() {
		return serverXMLWriter;
	}
	/**
	 * @param serverXMLWriter The serverXMLWriter to set.
	 */
	public void setServerXMLWriter(ServerXMLWriter serverXMLWriter) {
		this.serverXMLWriter = serverXMLWriter;
	}
	/**
	 * @return Returns the contributorDAO.
	 */
	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}
	/**
	 * @param contributorDAO The contributorDAO to set.
	 */
	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
	}
    /**
     * @return Returns the permissionChecker.
     */
    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }
    /**
     * @param permissionChecker The permissionChecker to set.
     */
    public void setPermissionChecker(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }
}
