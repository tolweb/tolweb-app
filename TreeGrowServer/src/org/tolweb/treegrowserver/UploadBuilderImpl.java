/*
 * Created on Dec 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.base.xml.BaseXMLReader;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.EditedPageDAO;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.NodePusher;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.names.NameRankMapper;
import org.tolweb.names.Rank;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.HttpRequestMaker;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.main.XMLWriter;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.OtherName;
import org.tolweb.treegrowserver.dao.DownloadDAO;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;
import org.tolweb.treegrowserver.dao.UploadDAO;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadBuilderImpl extends AbstractObjectReattacher implements UploadBuilder {
	private UploadDAO uploadDAO;
	private ServerXMLReader serverXMLReader;
	private ServerXMLWriter serverXMLWriter;
	private UploadBatchDAO uploadBatchDAO;
	private DownloadDAO downloadDAO;
	private NodeDAO nodeDAO, miscNodeDAO;
	private NodePusher nodePusher;
	private PageDAO pageDAO;
	private ImageDAO imageDAO;
	private AccessoryPageDAO accessoryPageDAO;
	private NameRankMapper nameRankMapper;
	private EditedPageDAO editedPageDAO;
	
	public Document buildUpload(String xmlString) throws Exception {
		return buildUpload(xmlString, null, false, true);
	}
	
	public Document buildUpload(String xmlString, Contributor contr, boolean parsePageAndAdditionalInfo, Long parentNodeId) throws Exception {
		Document doc = serverXMLReader.getDocumentFromString(xmlString);
		// insert a node as the root node
		MappedNode rootNode = getWorkingNodeDAO().getNodeWithId(parentNodeId);
		if (rootNode == null) {
			return null;
		}
		// create new node element and paste nodes element
		Element newRootNodeElement = getServerXMLWriter().constructAndFillOutNodeElement(rootNode, false, null, null, contr, false);
		Element newRootNodesElement = new Element(XMLConstants.NODES);
		newRootNodeElement.addContent(newRootNodesElement);
		Element rootNodesElement = doc.getRootElement().getChild(XMLConstants.NODES);
		Element oldRootNodeElement = rootNodesElement.getChild(XMLConstants.NODE);
		// clear the document's root nodes element
		rootNodesElement.getChildren().clear();		
		// paste the old root node onto the new root's NODES element
		newRootNodesElement.addContent(oldRootNodeElement);
		// paste the new root node element onto the document's NODES element 
		rootNodesElement.addContent(newRootNodeElement);
		// upload as usual but don't do deletions
		return buildUpload(XMLWriter.getDocumentAsString(doc), contr, parsePageAndAdditionalInfo, false);
	}
	
	public Document buildUpload(String xmlString, Contributor contr, boolean parsePageAndAdditionalInfo) throws Exception {
		return buildUpload(xmlString, contr, parsePageAndAdditionalInfo, true);
	}

	public Document buildUpload(String xmlString, Contributor contr, boolean parsePageAndAdditionalInfo, 
			boolean deleteChildrenIfNotPresent) throws Exception {
		Document doc = serverXMLReader.getDocumentFromString(xmlString);		
		Element rootNodeElement = serverXMLReader.getRootNodeElement(doc);
		// check to see if this upload has any tree structure -- changes how we 
		// interpret things
		boolean hasTreeStructure = checkForTreeStructure(rootNodeElement, doc.getRootElement());		
		if (!hasTreeStructure) {
			return readFlatXMLDoc(doc.getRootElement(), contr, xmlString);
		}
		Long downloadId = new Long(serverXMLReader.fetchDownloadId(doc.getRootElement()));
		Download download = null;
		try {
			download = downloadDAO.getDownloadWithId(downloadId);
		} catch (Exception e) {
			// download doesn't exist, so create it like we do with other node uploads
			MappedNode rootNode = getRootNodeFromRootNodeElement(rootNodeElement);
			download = createNewDownloadForRootNode(rootNode, contr);
		}

		//Long rootNodeId = new Long(serverXMLReader.getRootNodeId(doc));
		Hashtable oldToNewIds = new Hashtable();
		Upload upload = createNewUpload(xmlString, download, null);
		Set childIds = new HashSet();
		Set seenIds = new HashSet();
		int rootNodeId = serverXMLReader.getNodeIdFromNodeElement(rootNodeElement);
        boolean isNewVersion = serverXMLReader.getIsNewVersion(doc.getRootElement());
        if (parsePageAndAdditionalInfo) {
        	// make it behave like the old version of things since we are doing
        	// a different sort of upload
        	isNewVersion = false;
        }
		MappedNode rootNode = getNodeDAO().getNodeWithId(new Long(rootNodeId));
		// Save the root node's order on parent so it doesn't get overwritten.
		Integer currentOrderOnParent = rootNode.getOrderOnParent();
		// Same with the current node rank
		Integer currentNodeRank = rootNode.getNodeRankInteger();
		// Initialize the node and page ancestor sets to those of the root node
		// (we know it has a page since all roots of batches must have a page)
	    Set nodeAncestorIds = getMiscNodeDAO().getAncestorsForNode(rootNode.getNodeId());
	    // And do the same with the page ancestors -- we know it's
	    // not null because the root node must always have a page
	    Set pageAncestorIds = getPageDAO().getAncestorPageIds(getPageDAO().getPageId(rootNode.getNodeId()));
		readNode(rootNodeElement, null, null, download, upload, oldToNewIds, 
		        childIds, seenIds, nodeAncestorIds, pageAncestorIds, isNewVersion);
		// Reset the root's order on parent
		rootNode.setOrderOnParent(currentOrderOnParent);
		rootNode.setNodeRankInteger(currentNodeRank);
		getNodeDAO().saveNode(rootNode);
		// Remove any residual missing nodes from the childIds
		childIds.removeAll(seenIds);
		if (deleteChildrenIfNotPresent) {
	        // before we do the deletions, make sure to add all descendants
	        // of the nodes to delete because the entire tree structure 
	        // needs to disappear
	        addAllDescendantNodeIds(childIds, new ArrayList(childIds));
			if (!childIds.isEmpty()) {
			    doNodeDeletions(childIds, download);
			}
		}
		getDownloadDAO().saveDownload(download);
		saveUpload(upload, childIds);
		return buildResultsDocument(upload.getUploadId(), upload.getUploadId(), false, oldToNewIds);
	}
	
	private Document readFlatXMLDoc(Element rootElement, Contributor contributor, String nodesString) throws Exception {
		List<Element> nodes = rootElement.getChildren(XMLConstants.NODE);
		
    	Download download = null;
    	Upload upload = null;
    	boolean isFirst = true;
		for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			Element nextNodeElement = (Element) iter.next();
			if (isFirst) {
				MappedNode rootNode = getRootNodeFromRootNodeElement(nextNodeElement);
				download = createNewDownloadForRootNode(rootNode, contributor);
				upload = createNewUpload(nodesString, download, download.getDownloadDate());
				isFirst = false;
			}
			readNode(nextNodeElement, null, null, download, upload, new Hashtable(), new HashSet(),
					new HashSet(), new HashSet(), new HashSet(), false);
		}
		saveUpload(upload, new HashSet());
		return buildResultsDocument(upload.getUploadId(), upload.getUploadId(), false, null);
	}

	private MappedNode getRootNodeFromRootNodeElement(Element rootNodeElement) {
		int rootNodeId = serverXMLReader.getNodeIdFromNodeElement(rootNodeElement);
		MappedNode rootNode = getWorkingNodeDAO().getNodeWithId(new Long(rootNodeId));
		return rootNode;
	}

	private boolean checkForTreeStructure(Element rootNodeElement, Element docRootElement) {
		Element childNodesElement = rootNodeElement.getChild(XMLConstants.NODES);
		// has tree structure if it has a child node element or if it
		// is from treegrow and is a single node upload
		return childNodesElement != null || docRootElement.getAttributeValue(XMLConstants.NEW_VERSION) != null;
	}

	private void saveUpload(Upload upload, Collection childIds) {
		getUploadDAO().saveUpload(upload);
		/*batch.addToUploads(upload);
		getUploadBatchDAO().saveBatch(batch);*/
		
		// get the upload's page ids and make sure there are EditedPage objects
		for (Iterator iter = upload.getUploadedPages().iterator(); iter.hasNext();) {
			UploadPage nextPage = (UploadPage) iter.next();
			getEditedPageDAO().addEditedPageForContributor(nextPage.getPageId(), upload.getDownload().getContributor());
		}
		
		if (childIds != null && !childIds.isEmpty()) {
		    getDownloadDAO().reassignDownloadRootNodesForDeletedNodes(childIds, upload.getDownload().getRootNode().getNodeId());
		}
        /*Get the editors
        List editors = getUploadBatchDAO().getEditorsForBatch(batch);
        batch.setEditors(new HashSet(editors));
        getUploadBatchDAO().saveBatch(batch);*/
	}
	
	private Upload createNewUpload(String xmlString, Download download, Date date) {
		if (date == null) {
			date = new Date();
		}
		Upload upload = new Upload();
		upload.setXmlDoc(xmlString);
		upload.setDownload(download);
		upload.setIsClosed(false);
		upload.setUploadDate(date);
		return upload;
	}
	
	/*private UploadBatch getUploadBatchForNodeIds(Set nodeIds) {
	    // Check to see if there is an active upload batch for this set of nodes
	    UploadBatch batch = uploadBatchDAO.getActiveUploadBatchForNodes(nodeIds);
	    if (batch == null) {
	        batch = new UploadBatch();    
	    }
	    return batch;
	}*/
	
	private void initalizeNewPage(MappedPage page, MappedNode node, Long parentPageId, Contributor contr, Set pageAncestorIds) {
	    // Add ourselves to the list and reset our page ancestors
        page.setMappedNode(node);
        page.setParentPageId(parentPageId);
        getPageDAO().addPage(page, contr);                
	    pageAncestorIds.add(page.getPageId());
	    getPageDAO().resetAncestorsForPage(page.getPageId(), pageAncestorIds);		
	}
    
    private void addAllDescendantNodeIds(Set nodeIdsToDelete, Collection currentNodeIds) {
        ArrayList nextIds = new ArrayList();
        for (Iterator iter = currentNodeIds.iterator(); iter.hasNext();) {
            Long nextNodeId = (Long) iter.next();
            List childNodeIds = getNodeDAO().getChildrenNodeIds(nextNodeId); 
            nextIds.addAll(childNodeIds);
            nodeIdsToDelete.addAll(childNodeIds);
        }
        if (nextIds.size() > 0) {
            addAllDescendantNodeIds(nodeIdsToDelete, nextIds);
        }
    }
       
    private Download createNewDownloadForRootNode(MappedNode parentNode, Contributor contributor) {
    	// need to create a download, then an upload, then an upload batch if necessary
    	Date now = new Date();
    	Download download = new Download();
    	download.setContributor(contributor);
    	download.setDownloadDate(now);
    	download.setIpAddress("");
    	Set nodes = new HashSet();
    	DownloadNode downloadNode = new DownloadNode();
    	downloadNode.setNodeId(parentNode.getNodeId());
    	downloadNode.setWasDeleted(false);
    	downloadNode.setActive(DownloadNode.ACTIVE);
    	nodes.add(downloadNode);
    	download.setDownloadedNodes(nodes);
    	// since the download never actually "happened", it's checked in
    	download.setIsActive(false);
    	download.setRootNode(parentNode);
    	getDownloadDAO().createNewDownload(download);    
    	return download;
    }

	/**
	 * Adds the new nodes to be tree.  The list of nodes is assumed to be
	 * only those that are immediate children of the parent.  The individual
	 * nodes will be walked and their children list iterated. 
	 */
    public void uploadNewNodes(MappedNode parentNode, List childNodes, boolean createPagesAllNamed,
    		boolean createPagesInternalNodes, 
			boolean createPagesSuperfamily, boolean createPagesFamily, boolean createPagesSubfamily, 
			boolean createPagesTribe, Contributor contributor, String nodesString,
			boolean useTaxonLists) {
    	Download download = createNewDownloadForRootNode(parentNode, contributor);
    	Set nodeIds = new HashSet();
    	nodeIds.add(parentNode.getNodeId());
        
        // we can't allow any node to have one child, so check this before we save
        checkForTwoChildren(parentNode);               
        if (childNodes.size() == 1) {
            MappedNode newEmptyNode = new MappedNode();
            newEmptyNode.setName("");
            childNodes.add(newEmptyNode);
        }
    	// check to see if there is an existing batch for the root node, no need
    	// to check for the others since they don't exist yet

    	// create the Upload object
    	Upload upload = createNewUpload(nodesString, download, download.getDownloadDate());
    	Long parentNodeId = parentNode.getNodeId();
    	Long pageId = getPageDAO().getPageIdForNode(parentNode);
        Set nodeAncestorIds = getMiscNodeDAO().getAncestorsForNode(parentNode.getNodeId());
        Set pageAncestorIds = getPageDAO().getAncestorPageIds(pageId);
        ParentPageInfo parentPageInfo = new ParentPageInfo();        
    	if (pageId == null) {
            // adjust parent page id to be the containing page
    		pageId = getPageDAO().getPageIdNodeIsOn(parentNode);
            // we want to create a page for the node so, set up the ancestors and pages properly
            pageAncestorIds = getPageDAO().getAncestorPageIds(pageId);
            parentPageInfo.setPageId(pageId);
            parentPageInfo = createPageForNodeInMassUpload(parentNode, parentPageInfo, 
                    pageAncestorIds, contributor, upload, useTaxonLists);
            // add the new page id to the ancestors
    	} else {
            parentPageInfo.setPageId(pageId);
        }
		// initialize the current root's order on parent to be at the end
		// of the parent list    	
    	Integer counter = getNodeDAO().getMaxChildOrderOnParent(parentNodeId);
    	if (counter == null) {
    		counter = 0;
    	}
    	for (Iterator iter = childNodes.iterator(); iter.hasNext();) {
			MappedNode nextChildNode = (MappedNode) iter.next();
            checkForTwoChildren(nextChildNode);
			nextChildNode.setOrderOnParent(new Integer(++counter));
			saveAndRecurseNode(parentNodeId, nextChildNode, parentPageInfo, nodeAncestorIds, createPagesAllNamed,
					createPagesInternalNodes,
					createPagesSuperfamily, createPagesFamily, createPagesSubfamily, createPagesTribe,
					pageAncestorIds, contributor, upload, useTaxonLists);
		}
    	saveUpload(upload, null);
    	getMiscNodeDAO().flushQueryCache();
    	getNodeDAO().flushQueryCache();
	}
    /**
     * Adds the new nodes to be tree.  The list of nodes is assumed to be existing 
     * terminal nodes on page parentPage.  The existing nodes
     * nodes will be walked and their children list iterated. 
     */    
    public void uploadNewNodes(MappedPage parentPage, List parentNodes, boolean createPagesAllNamed,
            boolean createPagesInternalNodes, 
            boolean createPagesSuperfamily, boolean createPagesFamily, boolean createPagesSubfamily, 
            boolean createPagesTribe, Contributor contributor, String nodesString,
            boolean useTaxonLists, boolean createPagesForParents) {
        // TODO: need to create pages for nodes of attachment 
        
        Download download = createNewDownloadForRootNode(parentPage.getMappedNode(), contributor);
        Set nodeIds = new HashSet();
        for (Iterator iter = parentNodes.iterator(); iter.hasNext();) {
            MappedNode parentNode = (MappedNode) iter.next();
            nodeIds.add(parentNode.getNodeId());
        }
        // check to see if there is an existing batch for the root node, no need
        // to check for the others since they don't exist yet
        // create the Upload object
        Upload upload = createNewUpload(nodesString, download, download.getDownloadDate());
        Set pageAncestorIds = getPageDAO().getAncestorPageIds(parentPage.getPageId());
        ParentPageInfo pageInfo = new ParentPageInfo();
        pageInfo.setPageId(parentPage.getPageId());
        for (Iterator iter = parentNodes.iterator(); iter.hasNext();) {
            MappedNode nextParentNode = (MappedNode) iter.next();
            // don't want any single children nodes
            checkForTwoChildren(nextParentNode);
            Long parentPageId = parentPage.getPageId();
            if (createPagesForParents) {
                ParentPageInfo parentPageInfo = createPageForNodeInMassUpload(nextParentNode, pageInfo, 
                        pageAncestorIds, contributor, upload, useTaxonLists);
                parentPageId = parentPageInfo.getPageId();
                pageAncestorIds.add(parentPageId);
            }           
            int counter = 0;
            Set nodeAncestorIds = getMiscNodeDAO().getAncestorsForNode(nextParentNode.getNodeId());
            for (Iterator iterator = nextParentNode.getChildren().iterator(); iterator.hasNext();) {
                MappedNode nextChild = (MappedNode) iterator.next();
                nextChild.setOrderOnParent(new Integer(counter++));
                ParentPageInfo parentPageInfo = new ParentPageInfo();
                parentPageInfo.setPageId(parentPageId);
                saveAndRecurseNode(nextParentNode.getNodeId(), nextChild, parentPageInfo, nodeAncestorIds, createPagesAllNamed,
                        createPagesInternalNodes,
                        createPagesSuperfamily, createPagesFamily, createPagesSubfamily, createPagesTribe,
                        pageAncestorIds, contributor, upload, useTaxonLists);               
            }
            if (createPagesForParents) {
                // remove our page id from the ancestor set since we are all done
                pageAncestorIds.remove(parentPageId);
            }
        }
        saveUpload(upload, null);        
    }    
    
    /**
     * Used to walk a node for mass import initial save
     * @param parentNodeId
     * @param nodeToSave
     * @param parentPageId
     * @param nodeAncestorIds
     * @param createPagesInternalNodes
     * @param createPagesSuperfamily
     * @param createPagesFamily
     * @param createPagesSubfamily
     * @param createPagesTribe
     * @param pageAncestorIds
     * @param contributor
     * @param useTaxonLists 
     */
    private void saveAndRecurseNode(Long parentNodeId, MappedNode nodeToSave, ParentPageInfo parentPageInfo, 
    		Set nodeAncestorIds, boolean createPagesAllNamed, boolean createPagesInternalNodes, 
    		boolean createPagesSuperfamily,
    		boolean createPagesFamily, boolean createPagesSubfamily, boolean createPagesTribe,
    		Set pageAncestorIds, Contributor contributor, Upload upload, boolean useTaxonLists) {
    	nodeToSave.setParentNodeId(parentNodeId);
    	nodeToSave.setPageId(parentPageInfo.getPageId());  
    	getNodeDAO().saveNode(nodeToSave);
    	try {
			getNodePusher().pushNodeToDB(nodeToSave, getMiscNodeDAO());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//getMiscNodeDAO().addAdditionalFieldsForNode(nodeToSave);
    	
    	// add this node to the upload
    	addNodeToUploadNodes(upload, nodeToSave);

		// Add ourselves to the list of the ancestors (for querying purposes a node
		// is considered to be an ancestor of itself)
    	nodeAncestorIds.add(nodeToSave.getNodeId());
		getMiscNodeDAO().resetAncestorsForNode(nodeToSave.getNodeId(), nodeAncestorIds);
		boolean createPage = shouldCreatePage(nodeToSave, createPagesAllNamed, createPagesInternalNodes,
				createPagesSuperfamily, createPagesFamily, createPagesSubfamily, createPagesTribe);
		if (createPage) {
            parentPageInfo = createPageForNodeInMassUpload(nodeToSave, parentPageInfo, 
                    pageAncestorIds, contributor, upload, useTaxonLists);
		}
		for (Iterator iter = nodeToSave.getChildren().iterator(); iter.hasNext();) {
			MappedNode nextChild = (MappedNode) iter.next();
			saveAndRecurseNode(nodeToSave.getNodeId(), nextChild, parentPageInfo, nodeAncestorIds, createPagesAllNamed,
					createPagesInternalNodes,
					createPagesSuperfamily, createPagesFamily, createPagesSubfamily, createPagesTribe,
					pageAncestorIds, contributor, upload, useTaxonLists);
		}
		// remove ourself from the node ancestors set
		nodeAncestorIds.remove(nodeToSave.getNodeId());
		// if we added the page, then remove its id from the page ancestors set
		if (createPage) {
			pageAncestorIds.remove(parentPageInfo.getPageId());
		}
    }
    /*
     Utility method to initialize a new page
     */
    private ParentPageInfo createPageForNodeInMassUpload(MappedNode nodeToSave, ParentPageInfo parentPageInfo,
            Set pageAncestorIds, Contributor contributor, Upload upload, boolean useTaxonLists) {
        MappedPage page = new MappedPage();
        page.setWriteAsList(useTaxonLists);
        initalizeNewPage(page, nodeToSave, parentPageInfo.getPageId(), contributor, pageAncestorIds);
        nodeToSave.setOrderOnPage(new Integer(parentPageInfo.incrementPageCount()));
        // need to save again to update order on page
        getNodeDAO().saveNode(nodeToSave);
        parentPageInfo = new ParentPageInfo();
        parentPageInfo.setPageId(page.getPageId());
        // add this page to the upload
        addPageToUploadPages(upload, page.getPageId());
        return parentPageInfo;
    }
    
    private boolean shouldCreatePage(MappedNode node, boolean createPagesAllNamed,
    		boolean createPagesInternalNodes, boolean createPagesSuperfamily, 
    		boolean createPagesFamily, boolean createPagesSubfamily,
    		boolean createPagesTribe) {
    	boolean createPage = false;
    	NameRankMapper mapper = getNameRankMapper();
    	if (createPagesInternalNodes) {
    		if (node.getChildren().size() > 0) {
    			createPage = true;
    		}
    	}  else if (createPagesAllNamed) {
    		createPage = StringUtils.notEmpty(node.getName());
    	} else {
    		Rank rank = mapper.getRankForName(node.getName());
    		switch(rank) {
    			case SUPERFAMILY: 
    				createPage = createPagesSuperfamily;
    				break;
    			case FAMILY:
    				createPage = createPagesFamily;
    				break;
    			case SUBFAMILY:
    				createPage = createPagesSubfamily;
    				break;
    			case TRIBE:
    				createPage = createPagesTribe;
    				break;
    		}
    	}
    	return createPage;
    }
	
	public void readNode(Element nodeElement, Long parentNodeId, ParentPageInfo parentPageInfo, Download download,
			Upload upload, Hashtable oldIdsToNewIds, Set childIds, Set seenIds,
			Set nodeAncestorIds, Set pageAncestorIds, boolean isNewVersion) throws Exception {
		Long nodeId = new Long(serverXMLReader.getNodeIdFromNodeElement(nodeElement));
		MappedNode node;
		boolean wasNewNode = false;
		Long oldId = null;
		if (nodeId.intValue() > 0) {
			node = getNodeDAO().getNodeWithId(nodeId);
			if (node == null) {
			    getNodeDAO().addNodeWithId(nodeId);
			    node = new MappedNode();
			    node.setNodeId(nodeId);
			}
		} else {
			node = new MappedNode();
			oldId = nodeId;
			wasNewNode = true;
		}
		if (parentNodeId != null) {
			node.setParentNodeId(parentNodeId);
		}
		if (parentPageInfo != null) {
			node.setPageId(parentPageInfo.getPageId());
		}
		// Do this to verify that all nodes that were previously children
		// of this node were uploaded 
		if (!wasNewNode) {
			childIds.addAll(getNodeDAO().getChildrenNodeIds(node));
		}
		// Make sure this node isn't marked as deleted 
		// (it's possible it could have gotten moved from an uploaded subtree,
		// which would have caused it to be marked as deleted)
		DownloadNode dn = download.getDownloadNodeWithNodeId(nodeId);
		if (dn != null) {
		    dn.setWasDeleted(false);
		} 
        boolean wasLeaf = node.getIsLeaf();
		serverXMLReader.readNodeProperties(node, nodeElement, isNewVersion);
        // check to see if it is a new node marked as a leaf.  if it is,
        // set the default italicized printing to true
        if (!wasLeaf && node.getIsLeaf()) {
            node.setItalicizeName(true);
        }
        // if it has a page, assign its order on parent page property
        boolean hasPage = serverXMLReader.getNodeHasPage(nodeElement);
        if (hasPage && parentPageInfo != null) {
            if (node.getConfidence() != Node.INCERT_UNSPECIFIED) {
                node.setOrderOnPage(new Integer(parentPageInfo.incrementPageCount()));
            } else {
                // it's an ISPU node, so it should be added to the list of
                // ISPU nodes in order to ensure that they show up on the end
                // of child and sibling lists (to correspond to their tree placement)
                parentPageInfo.getIspuNodes().add(node);
            }
        }
        
		getNodeDAO().saveNode(node);
        
		// Save it to misc and rebuild the ancestors -- don't do the copy to misc
		// it takes too long.  Replace with a script that does the copy every X min.
		getNodePusher().pushNodeToDB(node, getMiscNodeDAO());
		// If the node was new, we need to manually mark it as downloaded
		if (wasNewNode) {
		    dn = new DownloadNode();
		    dn.setNodeId(node.getNodeId());
		    dn.setActive(DownloadNode.ACTIVE);
		    dn.setWasDeleted(false);
		    download.addToDownloadedNodes(dn);
		}
		//
        
		MappedPage page = getPageDAO().getPageForNode(node);		
		
		// create an additional info object if one doesn't exist
		/*AdditionalFields fields = getMiscNodeDAO().getAdditionalFieldsForNode(node);
		if (fields == null) {
			getMiscNodeDAO().addAdditionalFieldsForNode(node);
		}*/
		
		Set differentNodeAncestorIds = nodeAncestorIds;
		Set differentPageAncestorIds = pageAncestorIds;
		// As long as it isn't root, reset node and page ancestors
		if (parentNodeId != null) {
		    // Create a copy so that the different ancestors don't affect one another
		    differentNodeAncestorIds = new HashSet(nodeAncestorIds);
		    differentPageAncestorIds = new HashSet(pageAncestorIds);
			// Add ourselves to the list of the ancestors (for querying purposes a node
			// is considered to be an ancestor of itself)
		    differentNodeAncestorIds.add(node.getNodeId());
			getMiscNodeDAO().resetAncestorsForNode(node.getNodeId(), differentNodeAncestorIds);
			if (page != null) {
			    // Add ourselves to the list and reset our page ancestors
			    differentPageAncestorIds.add(page.getPageId());
			    getPageDAO().resetAncestorsForPage(page.getPageId(), differentPageAncestorIds);
			}
		}
		addNodeToUploadNodes(upload, node);
		// Remember the old id and it's replacement since the client needs this information
		if (oldId != null) {
			oldIdsToNewIds.put(oldId, node.getNodeId());
		}
		parentNodeId = node.getNodeId();

		String pageAdded = nodeElement.getAttributeValue(XMLConstants.PAGEADDED);
        boolean pageWasAdded = StringUtils.notEmpty(pageAdded) && pageAdded.equals(XMLConstants.ONE);
		if (hasPage) {
            // only worry about this if it's the old version of TreeGrow
            // or if the page was newly added in the client or if the page still exists on the server. 
            // Otherwise we risk the possibility of recreating a page that was deleted online
            if (!isNewVersion || pageWasAdded || page != null) {
    		    boolean wasNewPage = false;
    			if (page == null) {
    				page = new MappedPage();
                    wasNewPage = true;
    			}
                // here for legacy files provided by the old TreeGrow -- new files don't 
                // contain page stuff
                if (!isNewVersion) {
                    Element pageElement = serverXMLReader.getPageElementFromNodeElement(nodeElement);
                    if (pageElement != null) {
	                    serverXMLReader.readPageProperties(page, pageElement);
	                    page.setMappedNode(node);
	                    getPageDAO().savePage(page);	                    
                    }                    
                }
                
    			if (parentPageInfo != null) {
    				page.setParentPageId(parentPageInfo.getPageId());	
    			}    			
    			
    			if (wasNewPage) {
    				Long parentPageId = parentPageInfo != null ? parentPageInfo.getPageId() : null;
    				initalizeNewPage(page, node, parentPageId, download.getContributor(), differentPageAncestorIds);
                    //getPageDAO().insertNewAncestorForPages(parentPageInfo.getPageId(), page.getPageId());
    			}
                parentPageInfo = new ParentPageInfo();
    			parentPageInfo.setPageId(page.getPageId());
    			addPageToUploadPages(upload, parentPageInfo.getPageId());
                
                // this may be updated later if an internal node on that page
                // is found to have incomplete subgroups, but re-initialize it
                // to the root node in order to reset things in case of deleted or
                // moved nodes
                //getPageDAO().updateHasIncompleteSubgroupsForPage(page.getPageId(), node.getHasIncompleteSubgroups());                
            }
		} else if (page != null) {
			// Also remove the page id from the ancestor page ids set since it really 
			// shouldn't be there
			differentPageAncestorIds.remove(page.getPageId());
		    // Delete an existing page since there was a page but it isn't present in the upload
			getPageDAO().deletePage(page);
		}
		List children = serverXMLReader.getChildNodeElements(nodeElement);
		Iterator it = children.iterator();
		while (it.hasNext()) {
			Element nextChildElement = (Element) it.next();
			Long childNodeId = new Long(serverXMLReader.getNodeIdFromNodeElement(nextChildElement));
			if (childIds.contains(childNodeId)) {
				// Mark this node as having been seen
				childIds.remove(childNodeId);
			} else {
			    // Wasn't previously seen, so add it to the set of seen ids.  At the end of
			    // walking through all nodes, anything in here will get removed from the child
			    // ids in order to account for nodes that have been shifted around
			    seenIds.add(childNodeId);
			}
			// Check to see if it was checked out or it's a new node, or it's a previously
			// new node that was uploaded as part of this UploadBatch
			// -- Still possible to break if the node is newly uploaded, subtree checked in, then
			//    another upload occurs
			if (download.getIsNodePartOfDownload(childNodeId, true) || childNodeId.intValue() < 0) {
				// Call recursively to read the next node's info
				readNode(nextChildElement, parentNodeId, parentPageInfo, download, upload, 
				        oldIdsToNewIds, childIds, seenIds, 
				        differentNodeAncestorIds, differentPageAncestorIds, isNewVersion);
			} else {
				// Not an active part of the download, so update it's parent node and parent page 
				MappedNode notCheckedOutNode = getNodeDAO().getNodeWithId(childNodeId);
				notCheckedOutNode.setParentNodeId(parentNodeId);
				notCheckedOutNode.setPageId(parentPageInfo.getPageId());
				// Also need to update its order on parent
				serverXMLReader.fetchSequence(nextChildElement, notCheckedOutNode);
				serverXMLReader.fetchNodeRank(nextChildElement, notCheckedOutNode);
				getNodeDAO().saveNode(notCheckedOutNode);
				// Need to make a copy of the nodeAncestorIds set and add this node to it
				// (don't add it to the main set because it's not a parent of anything in the
				// upload)
				differentNodeAncestorIds.add(notCheckedOutNode.getNodeId());
				getMiscNodeDAO().resetAncestorsForNode(notCheckedOutNode.getNodeId(), differentNodeAncestorIds);
				differentNodeAncestorIds.remove(notCheckedOutNode.getNodeId());
				// Also need to check if it has a page.  If it does, then its page ancestors
				// need to be reset for the same reason the node ancestors need to be reset
				MappedPage notCheckedOutPage = pageDAO.getPageForNode(notCheckedOutNode);
				if (notCheckedOutPage != null) {
				    notCheckedOutPage.setParentPageId(parentPageInfo.getPageId());
				    getPageDAO().savePage(notCheckedOutPage);
				    differentPageAncestorIds.add(notCheckedOutPage.getPageId());
				    getPageDAO().resetAncestorsForPage(notCheckedOutPage.getPageId(), differentPageAncestorIds);
				    differentPageAncestorIds.remove(notCheckedOutPage.getPageId());
                    if (notCheckedOutNode.getConfidence() != Node.INCERT_UNSPECIFIED) {
                        // Save the node again
                        notCheckedOutNode.setOrderOnPage(new Integer(parentPageInfo.incrementPageCount()));
                        getNodeDAO().saveNode(notCheckedOutNode);
                    } else {
                        parentPageInfo.getIspuNodes().add(notCheckedOutNode);
                    }
				}
			}
		}
		if (parentPageInfo != null) {
	        // Iterate over the ispu nodes to make sure they show up at the end of the list
	        for (Iterator iter = parentPageInfo.getIspuNodes().iterator(); iter.hasNext();) {
	            MappedNode nextIspuNode = (MappedNode) iter.next();
	            nextIspuNode.setOrderOnPage(new Integer(parentPageInfo.incrementPageCount()));
	            getNodeDAO().saveNode(nextIspuNode);
	        }
		}
	}
	
	private void addPageToUploadPages(Upload upload, Long pageId) {
		UploadPage up = new UploadPage();
		up.setPageId(pageId);
		upload.addToUploadedPages(up);		
	}
	
	private void addNodeToUploadNodes(Upload upload, MappedNode node) {
		UploadNode un = new UploadNode();
		un.setNodeId(node.getNodeId());
		upload.addToUploadedNodes(un);		
	}
	
	private Document buildResultsDocument(Long uploadId, Long batchId, boolean isEditor, Hashtable oldIdsToNewIds) {
		Document doc = new Document();
		Element resultsElement = new Element(XMLConstants.UPLOADRESULTS);
		doc.setRootElement(resultsElement);
		resultsElement.setAttribute(XMLConstants.ID, uploadId.toString());
		resultsElement.setAttribute(XMLConstants.BATCHID, batchId.toString());
		resultsElement.setAttribute(XMLConstants.NODEID, buildOldIdsToNewIdsString(oldIdsToNewIds));
		return doc;
	}
	
	private String buildOldIdsToNewIdsString(Hashtable oldToNewIds) {
		if (oldToNewIds == null) {
			return "";
		}
		Iterator it = oldToNewIds.keySet().iterator();
		StringBuffer resultStringBuffer = new StringBuffer();
		while (it.hasNext()) {
			Number nextKey = (Number) it.next();
			resultStringBuffer.append(nextKey);
			resultStringBuffer.append('=');
			resultStringBuffer.append(oldToNewIds.get(nextKey));
			resultStringBuffer.append('&');
		}
		return resultStringBuffer.toString();
	}
	
	private void doNodeDeletions(Set nodeIds, Download download) {
	    for (Iterator iter = nodeIds.iterator(); iter.hasNext();) {
            Long nextNodeId = (Long) iter.next();
            List returnList = getNewParentForDeletedNode(nextNodeId, nodeDAO, nodeIds);
            MappedNode workingParentNode = null;
            MappedNode workingNode = getNodeDAO().getNodeWithId(nextNodeId);            
            MappedNode miscNode = getMiscNodeDAO().getNodeWithId(nextNodeId);            
            if (returnList != null) {
                workingParentNode = (MappedNode) returnList.get(1);
            }
            List imagesAttachedToDeletedNode = getImageDAO().getImagesAttachedToNode(nextNodeId);
            for (Iterator iterator = imagesAttachedToDeletedNode.iterator(); iterator
                    .hasNext();) {
                NodeImage image = (NodeImage) iterator.next();
                image.removeFromNodesSet(workingNode);
                if (workingParentNode != null) {
                    image.addToNodesSet(workingParentNode);
                }
                imageDAO.saveImage(image);
                try {
                    HttpRequestMaker.flushImageCacheOnServer(image.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (workingParentNode != null) {
                accessoryPageDAO.reattachAccessoryPagesForNode(workingNode, workingParentNode);
            }
            // Now we can actually delete the node and it's associated page (if it has one)
            deleteNodeAndAssociatedPage(getNodeDAO(), getPageDAO(), workingNode, false);
            Set ancestorIds = deleteNodeAndAssociatedPage(getMiscNodeDAO(), null, miscNode, true);
            if (ancestorIds != null && miscNode != null) {
            	getMiscNodeDAO().resetAncestorsForNode(miscNode.getNodeId(), ancestorIds);
            }
			// Now look for the downloaded node and mark it as deleted
			DownloadNode dn = download.getDownloadNodeWithNodeId((Long) nextNodeId);
			if (dn != null) {
			    dn.setWasDeleted(true);
			}
        }	    
	}
	
	/**
	 * @return Returns the serverXMLReader.
	 */
	public ServerXMLReader getServerXMLReader() {
		return serverXMLReader;
	}
	/**
	 * @param serverXMLReader The serverXMLReader to set.
	 */
	public void setServerXMLReader(ServerXMLReader serverXMLReader) {
		this.serverXMLReader = serverXMLReader;
	}
	/**
	 * @return Returns the uploadBatchDAO.
	 */
	public UploadBatchDAO getUploadBatchDAO() {
		return uploadBatchDAO;
	}
	/**
	 * @param uploadBatchDAO The uploadBatchDAO to set.
	 */
	public void setUploadBatchDAO(UploadBatchDAO uploadBatchDAO) {
		this.uploadBatchDAO = uploadBatchDAO;
	}
	/**
	 * @return Returns the uploadDAO.
	 */
	public UploadDAO getUploadDAO() {
		return uploadDAO;
	}
	/**
	 * @param uploadDAO The uploadDAO to set.
	 */
	public void setUploadDAO(UploadDAO uploadDAO) {
		this.uploadDAO = uploadDAO;
	}
	/**
	 * @return Returns the downloadDAO.
	 */
	public DownloadDAO getDownloadDAO() {
		return downloadDAO;
	}
	/**
	 * @param downloadDAO The downloadDAO to set.
	 */
	public void setDownloadDAO(DownloadDAO downloadDAO) {
		this.downloadDAO = downloadDAO;
	}
	/**
	 * @return Returns the nodeDAO.
	 */
	public NodeDAO getNodeDAO() {
		return nodeDAO;
	}
	/**
	 * @param nodeDAO The nodeDAO to set.
	 */
	public void setNodeDAO(NodeDAO nodeDAO) {
		this.nodeDAO = nodeDAO;
	}
	/**
	 * @return Returns the pageDAO.
	 */
	public PageDAO getPageDAO() {
		return pageDAO;
	}
	/**
	 * @param pageDAO The pageDAO to set.
	 */
	public void setPageDAO(PageDAO pageDAO) {
		this.pageDAO = pageDAO;
	}
    /**
     * @return Returns the miscNodeDAO.
     */
    public NodeDAO getMiscNodeDAO() {
        return miscNodeDAO;
    }
    /**
     * @param miscNodeDAO The miscNodeDAO to set.
     */
    public void setMiscNodeDAO(NodeDAO miscNodeDAO) {
        this.miscNodeDAO = miscNodeDAO;
    }
    /**
     * @return Returns the nodePusher.
     */
    public NodePusher getNodePusher() {
        return nodePusher;
    }
    /**
     * @param nodePusher The nodePusher to set.
     */
    public void setNodePusher(NodePusher nodePusher) {
        this.nodePusher = nodePusher;
    }
    /**
     * @return Returns the imageDAO.
     */
    public ImageDAO getImageDAO() {
        return imageDAO;
    }
    /**
     * @param imageDAO The imageDAO to set.
     */
    public void setImageDAO(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }
    /**
     * @return Returns the workingAccessoryPageDAO.
     */
    public AccessoryPageDAO getAccessoryPageDAO() {
        return accessoryPageDAO;
    }
    /**
     * @param workingAccessoryPageDAO The workingAccessoryPageDAO to set.
     */
    public void setAccessoryPageDAO(
            AccessoryPageDAO workingAccessoryPageDAO) {
        this.accessoryPageDAO = workingAccessoryPageDAO;
    }

	public NameRankMapper getNameRankMapper() {
		return nameRankMapper;
	}

	public void setNameRankMapper(NameRankMapper nameRankMapper) {
		this.nameRankMapper = nameRankMapper;
	}

	public String getIsLegalNewParent(MappedNode nodeToMove, MappedNode possibleParent) {
		// make sure the new parent is not a child of the node to move
		if (getMiscNodeDAO().getNodeIsAncestor(possibleParent.getNodeId(), nodeToMove.getNodeId())) {
			return possibleParent.getName() + " is a descendant of " + nodeToMove.getName() + ".  This sort of move is not allowed";
		} else if (getDownloadDAO().getNodeIsDownloaded(possibleParent.getNodeId())) {
			return possibleParent.getName() + " is downloaded in TreeGrow.  You will not be able to modify its children until it is checked back in.";
		}
		return null;
	}
	public void moveBranch(MappedNode branchRoot, MappedNode newParent, Contributor contr) {
		Long branchRootOldPageId = branchRoot.getPageId();
		Long branchRootId = branchRoot.getNodeId();
		NodeDAO workingNodeDAO = getWorkingNodeDAO();
		NodeDAO miscNodeDAO = getMiscNodeDAO();
		// things to do:
		// change branch root's parent (add it on to the end of the children) and page
		branchRoot.setParentNodeId(newParent.getNodeId());
		int numChildren = workingNodeDAO.getNumChildrenForNode(newParent);
		branchRoot.setOrderOnParent(numChildren);
		PageDAO pageDAO = getPageDAO();
		Long newParentPageId;
		if (pageDAO.getNodeHasPage(newParent.getNodeId())) {
			newParentPageId = pageDAO.getPageIdForNode(newParent);
		} else {
			newParentPageId = pageDAO.getPageIdNodeIdIsOn(newParent.getNodeId());
		}
		branchRoot.setPageId(newParentPageId);
		int maxOrderOnPage = pageDAO.getMaxOrderOnPage(newParentPageId);
		boolean branchRootHasPage = pageDAO.getNodeHasPage(branchRoot);
		
		// need to determine the root most pages of the branch move 
		// before we muck about with tree structure
		Set nodeDescendantIds = miscNodeDAO.getDescendantIdsForNode(branchRootId);
		nodeDescendantIds.add(branchRootId);
		Set rootPageIds = new HashSet();		
		if (!branchRootHasPage) {			
			// if branchRoot has no page, also change any descendants of branchRoot 
			// that were previously	on the same page to be on the new page branchRoot is on
			List nodeIdsOnPreviousPage = pageDAO.getNodeIdsOnPage(branchRootOldPageId);			
			List nodeIdsToResetPageId = new ArrayList();
			List orderOnPageNodeIds = new ArrayList();			
			for (Iterator iter = nodeIdsOnPreviousPage.iterator(); iter.hasNext();) {
				Long nextNodeId = (Long) iter.next();
				if (nodeDescendantIds.contains(nextNodeId)) {
					Long pageIdForNode = pageDAO.getPageIdForNodeId(nextNodeId);
					nodeIdsToResetPageId.add(nextNodeId);					
					// this is a node on the same page as branch root that has a page attached to it
					if (pageIdForNode != null) {
						rootPageIds.add(pageIdForNode);
						// add it to the list to reset the order on page
						orderOnPageNodeIds.add(nextNodeId);						
					}
				}
			}
			if (nodeIdsToResetPageId.size() > 0) {
				workingNodeDAO.resetPageIdForNodeIds(newParentPageId, nodeIdsToResetPageId);
			}
			// iterate over these and set their order on page
			for (Iterator iter = orderOnPageNodeIds.iterator(); iter.hasNext();) {
				Long nodeId = (Long) iter.next();
				workingNodeDAO.updateOrderOnPageForNode(nodeId, ++maxOrderOnPage);
			}			
		} else {
			// just add the page for the branch root
			Long branchRootPageId = pageDAO.getPageIdForNode(branchRoot);
			rootPageIds.add(branchRootPageId);
			// if branchRoot has a page, change its order on parent to be 1 + the highest node
			branchRoot.setOrderOnPage(++maxOrderOnPage);			
		}		
		// save the root node at this point
		workingNodeDAO.saveNode(branchRoot);

		// set the parent page of all the root pages
		for (Iterator iter = rootPageIds.iterator(); iter.hasNext();) {
			Long nextPageId = (Long) iter.next();
			pageDAO.updateParentPageIdForPage(nextPageId, newParentPageId);			
		}
		
		// get rid of any ancestor ids currently not contained in this branch
		miscNodeDAO.deleteAncestorsNotInBranch(nodeDescendantIds);
		Set newParentAncestorIds = miscNodeDAO.getAncestorsForNode(newParent.getNodeId());
		// remove the branch root from here because it's already in all of the
		// descendant ancestor lists
		nodeDescendantIds.remove(branchRootId);
		miscNodeDAO.addAncestorsForNodes(nodeDescendantIds, newParentAncestorIds);
		// then reset the branch root's ancestors
		newParentAncestorIds.add(branchRootId);
		miscNodeDAO.resetAncestorsForNode(branchRootId, newParentAncestorIds);
			
		List descendantPageIds = getPageDAO().getDescendantPageIds(rootPageIds);
		getPageDAO().deleteAncestorPagesNotInBranch(descendantPageIds);
		Set pageAncestorIds = getPageDAO().getAncestorPageIds(newParentPageId);
		getPageDAO().addNewAncestorsForPages(descendantPageIds, pageAncestorIds);
		
		getEditedPageDAO().addEditedPageForContributor(newParentPageId, contr);
		getEditedPageDAO().addEditedPageForContributor(branchRootOldPageId, contr);
	}
    
    public MappedNode checkForTwoChildren(MappedNode parentNode) {
        MappedNode newChildNode = null;
        if (parentNode.getChildren() != null && parentNode.getChildren().size() == 1) {
            newChildNode = new MappedNode();
            newChildNode.setName("");
            parentNode.addToChildren(newChildNode);
        }
        for (Iterator iter = parentNode.getChildren().iterator(); iter.hasNext();) {
            MappedNode nextNode = (MappedNode) iter.next();
            checkForTwoChildren(nextNode);
        }        
        return newChildNode;
    }
    public void copyValuesForMappedNode(MappedNode node, Node originalNode, ForeignDatabase sourceDatabase) {
        node.setName(originalNode.getName());
        node.setNameAuthority(originalNode.getNameAuthority());
        node.setAuthorityDate(originalNode.getNameDate());
        node.setOrderOnParent(originalNode.getSequence());
        node.setExtinct(originalNode.getExtinct());
        node.setSourceDbNodeId(originalNode.getSourceDbNodeId());
        if (sourceDatabase != null) {
            node.setSourceDbId(sourceDatabase.getId());
        }
        Vector otherNames = originalNode.getOtherNames();
        if (otherNames != null) {
            TreeSet synonyms = new TreeSet();
            int i = 0;
            for (Iterator iter = otherNames.iterator(); iter.hasNext();) {
                OtherName nextOtherName = (OtherName) iter.next();
                MappedOtherName newSynonym = new MappedOtherName();
                newSynonym.setOrder(i++);
                newSynonym.setName(nextOtherName.getName());
                newSynonym.setAuthority(nextOtherName.getAuthority());
                newSynonym.setAuthorityYear(nextOtherName.getDate());
                synonyms.add(newSynonym);
            }
            node.setSynonyms(synonyms);
        }
        for (Iterator iter = originalNode.getChildren().iterator(); iter.hasNext();) {
            Node nextChild = (Node) iter.next();
            MappedNode newChildNode = new MappedNode();
            newChildNode.setName("");
            node.addToChildren(newChildNode);
            copyValuesForMappedNode(newChildNode, nextChild, sourceDatabase);
        }        
    }

	public EditedPageDAO getEditedPageDAO() {
		return editedPageDAO;
	}

	public void setEditedPageDAO(EditedPageDAO editedPageDAO) {
		this.editedPageDAO = editedPageDAO;
	}

	public ServerXMLWriter getServerXMLWriter() {
		return serverXMLWriter;
	}
	public void setServerXMLWriter(ServerXMLWriter serverXMLWriter) {
		this.serverXMLWriter = serverXMLWriter;
	}
}