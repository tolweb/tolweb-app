/*
 * Created on Dec 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Element;
import org.tolweb.archive.BranchLeafPageArchiver;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.EditHistoryDAO;
import org.tolweb.dao.EditedPageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.NodePusher;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PagePusher;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.EditedPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.HttpRequestMaker;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.dao.DownloadDAO;
import org.tolweb.treegrowserver.dao.PublicationBatchDAO;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;
import org.tolweb.treegrowserver.dao.UploadDAO;

/**
 * @author dmandel
 *
 * Class used to push the results of one upload batch to the public database
 */
public class BatchPusherImpl extends AbstractObjectReattacher implements BatchPusher {
    private UploadDAO uploadDAO;
    private UploadBatchDAO uploadBatchDAO;
    private PublicationBatchDAO publicationBatchDAO;
    private PageDAO workingPageDAO, publicPageDAO;
    private PagePusher pagePusher;
    private NodePusher nodePusher;
    private AccessoryPageDAO publicAccessoryPageDAO;
    private DownloadDAO downloadDAO;
    private PermissionChecker permissionChecker;
    private BatchSubmitter batchSubmitter;
    private EditedPageDAO editedPageDAO;
    private EditHistoryDAO editHistoryDAO;
    private BranchLeafPageArchiver branchLeafPageArchiver;
    private NodeDAO miscNodeDAO;
       
    /*public void pushBatchToPublic(Long batchId) throws Exception {
        UploadBatch batch = uploadBatchDAO.getUploadBatchWithId(batchId);
        Long rootNodeId = uploadBatchDAO.getRootNodeIdForBatch(batch);
        MappedNode publicRootNode = publicNodeDAO.getNodeWithId(rootNodeId);
        MappedNode rootNode = workingNodeDAO.getNodeWithId(rootNodeId);
        Set uploadedAndDownloadedNodes = batch.getUploadedAndDownloadedNodesSet();
        Set affectedPageIds = new HashSet();
        boolean hasWithheldPages = pushValuesForNode(rootNode, uploadedAndDownloadedNodes, affectedPageIds);
        // at this point all of the affected pages are in the affectedPageIds set.
        // delete all work in progress for these pages
        getEditedPageDAO().deleteEditedPagesWithIds(affectedPageIds, EditedPage.BRANCH_LEAF_TYPE);
        resetValuesForRootNode(rootNode, publicRootNode);
        // Now deal with all deleted nodes
        Iterator it = batch.getDeletedNodeIdsSet().iterator();
        while (it.hasNext()) {
            Long nextId = (Long) it.next();
            performDeletionForNode(nextId, batch);
        }
        if (!hasWithheldPages) {
            performCleanupForBatch(batch);
        }
        Long containingPageId = publicPageDAO.getPageIdNodeIsOn(publicRootNode);
        if (containingPageId != null && containingPageId.intValue() > 0) {
            affectedPageIds.add(containingPageId);
        }
        clearCacheForPages(affectedPageIds);
        batch.setIsSubmitted(false);
        uploadBatchDAO.saveBatch(batch);
    }*/
    
    private void clearCacheForPages(Set affectedPageIds) {
    	String pageIdsCommaSep = StringUtils.returnCommaJoinedStringNoSpaces(affectedPageIds);
        try {   
            HttpRequestMaker.flushPageCacheOnServer(pageIdsCommaSep);
        } catch (Exception e) {
        }    
    }
    
    private void performDeletionForNode(Long nodeId) {
    	// only bother doing the deletion if it exists
    	if (nodeId != null && getPublicNodeDAO().getNodeExistsWithId(nodeId)) {

	        MappedNode nodeToDelete = publicNodeDAO.getNodeWithId(nodeId);
	        /*if (newParent != null) {
	        	publicAccessoryPageDAO.reattachAccessoryPagesForNode(nodeToDelete, newParent);
	        } else {
	        	System.err.println("\n\n***************\n\nNO NEW NODE FOUND FOR ACCESSORY PAGES ATTACHED TO DELETED NODE: " + nodeToDelete);
	        }*/
	        // get the descendants and recursively call with all of them
	        Collection descendantIds = getMiscNodeDAO().getDeletedDescendantIdsForNode(nodeId);	
	        if (nodeToDelete != null) {
	            // Now we can actually delete the node and its associated page (if it has one)
	            deleteNodeAndAssociatedPage(getPublicNodeDAO(), getPublicPageDAO(), nodeToDelete, false);
	            // Clean up the ancestors table, too
	            getMiscNodeDAO().resetAncestorsForNode(nodeToDelete.getNodeId(), new ArrayList());
	        }

	        if (descendantIds != null && descendantIds.size() > 0) {
		        descendantIds = getMiscNodeDAO().getOrderedByNumAncestorsNodes(descendantIds);
		        // walk out the deletion path starting with the closest nodes
		        for (Iterator iter = descendantIds.iterator(); iter.hasNext();) {
					Number nextNodeId = (Number) iter.next();
					// why is this not an int?!?!?!
					if (nextNodeId.intValue() == nodeId.intValue()) {
						// don't call ourselves again!
						continue;
					}
					performDeletionForNode(new Long(nextNodeId.longValue()));
				}
	        }
    	}
    }
    
    public String pushPublicationBatchToPublic(PublicationBatch batch, Contributor editingContributor) throws Exception {
        String unpublishedNodes = null;
        Set publishedPageIds = new HashSet();

        // determine what pageIds to archive
        Hashtable<Long, Integer> pageIdsToArchive = new Hashtable<Long, Integer>();
        for (Iterator iter = batch.getSubmittedPages().iterator(); iter.hasNext();) {
            UploadPage page = (UploadPage) iter.next();
            publishedPageIds.add(page.getPageId());
            if (SubmittedPage.class.isInstance(page)) {
            	int revisionType = ((SubmittedPage) page).getRevisionType(); 
            	if (revisionType == SubmittedPage.REGULAR_REVISION || revisionType == SubmittedPage.MAJOR_REVISION) {
            		pageIdsToArchive.put(page.getPageId(), revisionType);
            	} else {
            		// check to see if it's a new page
            		boolean pageExists = getPublicPageDAO().getPageExistsWithId(page.getPageId());
            		if (!pageExists) {
            			// new page so archive it as a regular revision, even though it wasn't marked as such
            			pageIdsToArchive.put(page.getPageId(), SubmittedPage.NEW_PAGE);
            		}
            	}
            }
        }
        
        // for all the root pages, push the values for the nodes to Public and 
        // determined what's checked out and not publishable 
        List roots = getRootMostPages(batch.getSubmittedPages());
        for (Iterator iter = roots.iterator(); iter.hasNext();) {
        	UploadPage upage = (UploadPage) iter.next();
            Long nextPageId = upage.getPageId();
            MappedPage nextPage = getWorkingPageDAO().getPageWithId(nextPageId);
            // get the parent of this next root and add it to the set of containing pages
            // so that cache flushing occurs properly
            Long parentPageId = getWorkingPageDAO().getParentPageIdForPage(nextPageId);
            if (parentPageId != null) {
                publishedPageIds.add(parentPageId);
            }
            int revType = -1;
            if (SubmittedPage.class.isInstance(upage)) {
            	revType = ((SubmittedPage)upage).getRevisionType(); 
            }
            MappedNode publicRootNode = getPublicNodeDAO().getNodeWithId(nextPage.getMappedNode().getNodeId());
            Set checkedOutNodes = new HashSet();
            pushValuesForNode(nextPage.getMappedNode(), null, publishedPageIds, true, nextPage.getPageId(), checkedOutNodes, revType);
            if (checkedOutNodes.size() > 0) {
                unpublishedNodes = "";
                for (Iterator iterator = checkedOutNodes.iterator(); iterator
                        .hasNext();) {
                    MappedNode nextNode = (MappedNode) iterator.next();
                    unpublishedNodes += nextNode.getName();
                    if (iterator.hasNext()) {
                        unpublishedNodes += ", ";
                    }
                }
            }
            // did the pushing, now do cleanup for the root
            resetValuesForRootNode(nextPage.getMappedNode(), publicRootNode);
        }
        
        // at this point all of the affected pages are in the affectedPageIds set.
        // delete all work in progress for these pages
        getEditedPageDAO().deleteEditedPagesWithIds(publishedPageIds, EditedPage.BRANCH_LEAF_TYPE);
        getEditHistoryDAO().clearAllLocksForPages(publishedPageIds);
        
        // close out the batch, save it, and clear it's submission
        batch.setDecisionDate(new Date());
        batch.setEditingContributor(editingContributor);
        batch.setIsClosed(true);
        batch.setWasPublished(true);
        getPublicationBatchDAO().saveBatch(batch);
        getBatchSubmitter().unsubmitNodesForPublicationBatch(batch);

        clearCacheForPages(publishedPageIds);
        // now that things have been cleared from cache, go ahead and snapshot things
        // take care of the pageIds we need to archive. 
        for (Iterator iter = pageIdsToArchive.keySet().iterator(); iter.hasNext();) {
			Long nextPageId = (Long) iter.next();
			int revisionType = pageIdsToArchive.get(nextPageId);
			boolean majorRevision = revisionType == SubmittedPage.MAJOR_REVISION;
			boolean newPage = revisionType == SubmittedPage.NEW_PAGE;
			MappedPage pageToArchive =  getPublicPageDAO().getPageWithId(nextPageId);
			if (pageToArchive == null) {
				System.out.println("bad page id is: " + nextPageId);
			} else {
				getBranchLeafPageArchiver().archivePage(pageToArchive, majorRevision,
						newPage);
			}
		}
        return unpublishedNodes;
    }
    
    private void performCleanupForBatch(UploadBatch batch) {
        batch.setIsClosed(true);        
    }
    
    /**
     * Loops through a collection of uploaded pages
     * and returns the page(s) that are the root-most in the collection
     * @param pages
     * @return A list of UploadPages that are the rootmost in the collection
     */
    public List getRootMostPages(Collection pages) {
        List sortedPages = getSortedByRootPages(pages, true);
        int firstNumAncestors = ((UploadPage) sortedPages.get(0)).getNumAncestors();
        ArrayList rootMostPages = new ArrayList();
        for (Iterator iter = sortedPages.iterator(); iter.hasNext();) {
            UploadPage nextPage = (UploadPage) iter.next();
            if (nextPage.getNumAncestors() > firstNumAncestors) {
                break;
            }
            rootMostPages.add(nextPage);
        }        
        return rootMostPages;
    }
    
    /**
     * during the course of batch pushing, some root node values may
     * get overwritten when they shouldn't.  this sets those values
     * back to their appropriate values.
     * @param rootNode the value in memory after pushing
     * @param previousRootNode a previous value in memory
     */
    private void resetValuesForRootNode(MappedNode rootNode, MappedNode previousRootNode) {
        if (previousRootNode != null) {
            // Fetch root from the public db (so we have the latest copy) 
            rootNode = publicNodeDAO.getNodeWithId(rootNode.getNodeId());
            // Then reset its parent to the old value (since that really shouldn't have been overwritten)
            rootNode.setParentNodeId(previousRootNode.getParentNodeId());
            // And its order on parent
            rootNode.setOrderOnParent(previousRootNode.getOrderOnParent());
            // And its node rank
            rootNode.setNodeRankInteger(previousRootNode.getNodeRankInteger());
            
            // check to see if the working node's page exists in public
        	boolean workingPageExistsInPublic = getPublicPageDAO().getPageExistsWithId(rootNode.getPageId());
        	Long previousPublicPageId = getPublicPageDAO().getPageIdForNodeId(previousRootNode.getNodeId());
        	if (!workingPageExistsInPublic && previousPublicPageId != null) {
        		rootNode.setPageId(previousPublicPageId);
        	}
            
            // And save those changes to the db
            publicNodeDAO.saveNode(rootNode);        
        }
    }
    
    private boolean pushValuesForNode(MappedNode node, Set uploadedAndDownloadedNodes, Set affectedPageIds) 
            throws Exception {
        return pushValuesForNode(node, uploadedAndDownloadedNodes, affectedPageIds, false, null, null);
    }
    
    /**
     * Pushes the values for this node and its children out to some boundary in the tree
     * @param node The node to push values for
     * @param uploadedAndDownloadedNodes The set of nodes uploaded and downloaded (if this is part of a treegrow batch)
     * @param affectedPageIds The pages seen, if part of a treegrow batch -- the list of pages to publish if part of online tools
     * @param isPublicationBatch Whether it's part of an online tools batch
     * @param containingPageId The id of the containing page -- needed to flush the cache
     * @param checkedOutNodes TODO
     * @param containingPage The containing page that this node is on
     * @return
     * @throws Exception
     */
    private boolean pushValuesForNode(MappedNode node, Set uploadedAndDownloadedNodes, Set affectedPageIds, 
            boolean isPublicationBatch, Long containingPageId, Set checkedOutNodes) throws Exception {
    	return pushValuesForNode(node, uploadedAndDownloadedNodes, 
    			affectedPageIds, isPublicationBatch, containingPageId, checkedOutNodes, -1);
    }
    
    /**
     * Pushes the values for this node and its children out to some boundary in the tree
     * @param node The node to push values for
     * @param uploadedAndDownloadedNodes The set of nodes uploaded and downloaded (if this is part of a treegrow batch)
     * @param affectedPageIds The pages seen, if part of a treegrow batch -- the list of pages to publish if part of online tools
     * @param isPublicationBatch Whether it's part of an online tools batch
     * @param containingPageId The id of the containing page -- needed to flush the cache
     * @param checkedOutNodes TODO
     * @param containingPage The containing page that this node is on
     * @param revisionType the type of revision being made 
     * @return a boolean indicating if nodes were withheld during the push 
     * @throws Exception
     */
    private boolean pushValuesForNode(MappedNode node, Set uploadedAndDownloadedNodes, Set affectedPageIds, 
            boolean isPublicationBatch, Long containingPageId, Set checkedOutNodes, int revisionType) throws Exception {
        boolean hasWithheldNodes = false;
        boolean pushFull;
        if (!isPublicationBatch) {
            // Determine if we are pushing everything or just the parent association
            pushFull = getUploadDownloadStatusForNode(node, uploadedAndDownloadedNodes);
        } else {
            pushFull = getPushFullNodeInformation(node, containingPageId);
            // if the page that this node is on is in the set of pages to publish,
            // then continue along with the publishing
            if (!pushFull) {
                Long pageId = getWorkingPageDAO().getPageIdForNode(node);
                if (affectedPageIds.contains(pageId)) {
                    pushFull = true;
                    containingPageId = pageId;
                }
            }
        }

        boolean isDownloaded = downloadDAO.getNodeIsDownloaded(node.getNodeId()); 
        if (pushFull && !isDownloaded) {

            // Push the node
            MappedNode publicNode = nodePusher.pushNodeToDB(node, getPublicNodeDAO());
            
            // Check for a page.  If it exists, push it
            MappedPage workingPage = workingPageDAO.getPageForNode(node);
            if (workingPage != null) {
            	if(SubmittedPage.isRegularOrMajorRevision(revisionType)) {
           			workingPage.setCopyrightDate(""+new GregorianCalendar().get(Calendar.YEAR));
            	}
                MappedPage publicPage = pagePusher.pushPageToDB(workingPage, publicNode, getPublicPageDAO(), getWorkingPageDAO());
                publicPageDAO.resetAncestorsForPage(publicPage.getPageId(), workingPageDAO.getAncestorPageIds(workingPage.getPageId()));
                affectedPageIds.add(workingPage.getPageId());
                // here, we want to check and see if any of the nodes on the page have
                // been marked as deleted
                Collection nodeIdsOnPage = publicPageDAO.getNodeIdsOnPage(publicPage);

                if (nodeIdsOnPage != null && nodeIdsOnPage.size() > 0) {
                	Collection deletedNodeIds = downloadDAO.getDeletedNodeIds(nodeIdsOnPage);
                	if (deletedNodeIds != null && deletedNodeIds.size() > 0) {
		                // we need to order these so that we start with the nodes closest
		                // to the root of the tree so that object re-attachment proceeds
		                // correctly -- otherwise we could attach things to nodes that will
		                // also be deleted
		                List orderedDeletedNodes = miscNodeDAO.getOrderedByNumAncestorsNodes(deletedNodeIds);
		                Set alreadyDeletedNodes = new HashSet();
		                for (Iterator iter = orderedDeletedNodes.iterator(); iter.hasNext();) {
							Long nextNodeId = (Long) iter.next();
							if (!alreadyDeletedNodes.contains(nextNodeId)) {
								reattachAllAccPagesForNodeAndDescendants(nextNodeId);
							}
							performDeletionForNode(nextNodeId);
							alreadyDeletedNodes.add(nextNodeId);
						}
                	}
                }
            } else {
                // In this case there is no page in working, check to see if there's one 
                // in public.  If there is, delete it since the one in working has disappeared.
                MappedPage publicPage = publicPageDAO.getPageForNode(publicNode);
                if (publicPage != null) {
                    publicPageDAO.deletePage(publicPage);
                }	
            }
            
            // Call it with the children - all children: active, inactive, retired, whatever, all of them 
            List children = workingNodeDAO.getAllChildrenNodes(node);
            
            for (Iterator iter = children.iterator(); iter.hasNext();) {
                MappedNode nextChild = (MappedNode) iter.next();
                boolean childHasWithheldNodes = pushValuesForNode(nextChild, uploadedAndDownloadedNodes, affectedPageIds,
                        isPublicationBatch, containingPageId, checkedOutNodes, revisionType);
                hasWithheldNodes = hasWithheldNodes || childHasWithheldNodes;
            }
        } else {
            if (pushFull && isDownloaded) {
                checkedOutNodes.add(node);
            }
            // Just reset the parent, page id, and order on parent
            MappedNode publicNode = publicNodeDAO.getNodeWithId(node.getNodeId());
            if (publicNode == null) {
                nodePusher.pushNodeToDB(node, getPublicNodeDAO());
                publicNode = publicNodeDAO.getNodeWithId(node.getNodeId());
            } else {
	            publicNode.setParentNodeId(node.getParentNodeId());
	            publicNode.setPageId(node.getPageId());
	            publicNode.setOrderOnParent(node.getOrderOnParent());
	            publicNode.setNodeRankInteger(node.getNodeRankInteger());
	            publicNodeDAO.saveNode(publicNode);	            
            }
            // Check to see if it has a page.  If it does, we need to also reset the parent page
            // and all of the page ancestors for that page
            MappedPage workingPage = workingPageDAO.getPageForNode(node);
            if (workingPage != null) {
                if (publicNode != null) {
                    MappedPage publicPage = publicPageDAO.getPageForNode(publicNode);
                    if (publicPage != null) {
                    	if(SubmittedPage.isRegularOrMajorRevision(revisionType)) {
                   			publicPage.setCopyrightDate(""+new GregorianCalendar().get(Calendar.YEAR));
                    	}
                        publicPage.setParentPageId(workingPage.getParentPageId());
                        publicPageDAO.savePage(publicPage);
                        publicPageDAO.resetAncestorsForPage(publicPage.getPageId(), workingPageDAO.getAncestorPageIds(workingPage.getPageId()));
                    }
                }
            }
        }
        return hasWithheldNodes;
    }
    
    
    /**
     * Get all of the descendants and then get all acc pages and re-attach them to
     * the parent of this node
     * @param nextNodeId
     */
    private void reattachAllAccPagesForNodeAndDescendants(Long nextNodeId) {
    	boolean nodeHasName = false;
    	MappedNode parentNode = null;
    	Long currentChildId = nextNodeId;
    	while (!nodeHasName) {    		
    		parentNode = getPublicNodeDAO().getParentNodeForNode(getPublicNodeDAO().getNodeWithId(currentChildId));
    		if (parentNode == null) {
    			break;
    		}
    		currentChildId = parentNode.getNodeId();
    		nodeHasName = StringUtils.notEmpty(parentNode.getName());
    	}
		Collection descendantIds = getMiscNodeDAO().getDeletedDescendantIdsForNode(nextNodeId);
		//if (!descendantIds.contains(nextNodeId)) {
			// we should be in the collection, but just in case add ourselves back in
			//descendantIds.add(nextNodeId);
		//}
		if (parentNode != null) {
			getPublicAccessoryPageDAO().reattachAccessoryPagesForNodes(descendantIds, parentNode);
		}
	}

	/**
     * @param node The node to check
     * @param uploadedAndDownloadedNodes The set of all uploaded and downloaded nodes in this batch
     * @return Whether or not the full node value should be copied over
     */
    private boolean getUploadDownloadStatusForNode(MappedNode node, Set uploadedAndDownloadedNodes) {
        for (Iterator iter = uploadedAndDownloadedNodes.iterator(); iter.hasNext();) {
            AbstractNodeWrapper nodeWrapper = (AbstractNodeWrapper) iter.next();
            if (nodeWrapper.getNodeId().equals(node.getNodeId())) {
                // It was uploaded, so it definitely gets its values pushed
                if (nodeWrapper instanceof UploadNode) {
                    return true;
                } else {
                    // it's a DownloadNode
                    DownloadNode dn = (DownloadNode) nodeWrapper;
                    // It was part of a true download, so return true
                    if (dn.getActive() == DownloadNode.NOT_ACTIVE) {
                        return true;
                    }
                }
            }
        }
        // Not part of an upload, not part of a true download, must be barrier
        return false;
    }
    
    /**
     * Checks to see whether we should push all information for a particular node
     * on a given page.  If a node has a page and it is not the page we are examining,
     * then only set the parent, order on parent, etc.  Don't actually modify any
     * node properties.
     * @param node The node to check
     * @param containingPage The page which is currently being examined
     * @return Whether or not all info should be pushed
     */
    private boolean getPushFullNodeInformation(MappedNode node, Long containingPageId) {
        boolean isPageForNode = getWorkingPageDAO().getNodeIdForPage(containingPageId).equals(node.getNodeId());
        boolean hasPage = false;
        if (!isPageForNode) {
            // does this node have a page
            hasPage = getWorkingPageDAO().getNodeHasPage(node);
        }
        return isPageForNode || !hasPage;
    }
    
    /**
     * Iterates over a collection of uploaded pages and build a hierarchical structure
     * according to which pages are children of which...
     * @param uploadedPages The pages to sort
     */    
    public List getSortedUploadPages(Collection uploadedPages, Contributor contributor, boolean checkPermissions, boolean initPublishParameter) {
        List pages = getSortedByRootPages(uploadedPages, initPublishParameter);
        // Need to make a pass through the list and remove any pages the user
        // may not have access to
        for (Iterator iter = new ArrayList(pages).iterator(); iter.hasNext();) {
            UploadPage nextPage = (UploadPage) iter.next();
            // check to make sure they are able to do so
            Long nodeId = getWorkingPageDAO().getNodeIdForPage(nextPage.getPageId());
            nextPage.setNodeId(nodeId);
            if (checkPermissions) {
                if (!getPermissionChecker().checkHasPermissionForNode(contributor, nodeId)) {
                    pages.remove(nextPage);
                }
            }
            // Check to see if this page is downloaded or already submitted
            Element lockedElement = getDownloadDAO().getNodeIsLocked(nodeId);
            nextPage.setLockedElement(lockedElement);
        }
        // make sure there are still pages, otherwise return
        if (pages.size() == 0) {
            return pages;
        }
        int lastNumAncestors = 0;
        boolean isFirst = true;
        // the parent pages in the list (one level above us)
        ArrayList lastPages = null;
        // the current pages in the list -- sibling pages
        ArrayList currentPages = null;
        // the list to store the results of the sorting
        ArrayList sortedPages = new ArrayList();
        int firstNumAncestors = ((UploadPage) pages.get(0)).getNumAncestors();
        for (Iterator iter = new ArrayList(pages).iterator(); iter.hasNext();) {
            UploadPage page = (UploadPage) iter.next();
            
            page.setGroupName(getWorkingPageDAO().getGroupNameForPage(page.getPageId()));
            if (isFirst || page.getNumAncestors() > lastNumAncestors) {
                lastPages = currentPages;
                currentPages = new ArrayList();
                // If this is the first pass through, add the root to the list 
                // of previous level pages since there is no list to speak of
                // yet
                if (isFirst) {
                    lastPages = new ArrayList();
                    lastPages.add(pages.get(0));
                }
            } else if (!isFirst && page.getNumAncestors() == firstNumAncestors) {
                // here for the special case of multiple root nodes
                lastPages.add(page);
            }
            // find our parent and add ourselves to the child pages
            Long parentId = getWorkingPageDAO().getParentPageIdForPage(page.getPageId());
            page.setParentPageId(parentId);
            for (Iterator iterator = lastPages.iterator(); iterator.hasNext();) {
                UploadPage lastParent = (UploadPage) iterator.next();
                if (lastParent.getPageId().equals(parentId)) {
                    lastParent.getChildPages().add(page);
                    pages.remove(page);
                    break;
                }
            }
            lastNumAncestors = page.getNumAncestors();
            currentPages.add(page);
            isFirst = false;
        }
        // Here we've built-everything up, now just add it into a list
        // Add root(s) first, then go on to the children and start the recursion
        lastNumAncestors = ((UploadPage) pages.get(0)).getNumAncestors();
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
            UploadPage nextPage = (UploadPage) iter.next();
            addChildrenPages(sortedPages, nextPage);
        }
        return sortedPages;
    }
    
    /**
     * Iterates over a list of uploaded pages, fetches their num ancestors
     * and sorts acoording to that info.
     * @param uploadedPages
     * @param initPublish Whether to initialize pages to be published or not
     * @return
     */
    public List getSortedByRootPages(Collection uploadedPages, boolean initPublish) {
        ArrayList pages = new ArrayList();
        for (Iterator iter = uploadedPages.iterator(); iter.hasNext();) {
            UploadPage page = (UploadPage) iter.next();
            int numAncestors = getWorkingPageDAO().getNumAncestorsForPage(page.getPageId());
            page.setNumAncestors(numAncestors);
            if (initPublish) {
                // Initialize things so that they will always be published
                page.setShouldBePublished(true);
            }
            pages.add(page);
        }
        Collections.sort(pages, getClosestToRootComparator()); 
        return pages;
    }

    /**
     * Goes through the page and makes sure all of its children get added to 
     * the return list
     * @param sortedPages
     * @param page
     */
    private void addChildrenPages(List sortedPages, UploadPage page) {
        sortedPages.add(page);
        for (Iterator iter = page.getChildPages().iterator(); iter.hasNext();) {
            UploadPage nextChild = (UploadPage) iter.next();
            addChildrenPages(sortedPages, nextChild);
        }
    }  
    
    public Comparator getClosestToRootComparator() {
        return new Comparator() {
            public int compare(Object o1, Object o2) {
                if (UploadPage.class.isInstance(o1) && UploadPage.class.isInstance(o2)) {
                    return new Integer(((UploadPage) o1).getNumAncestors()).compareTo(new Integer(((UploadPage) o2).getNumAncestors()));
                } else {
                    return 0;
                }
            }
        };
    }
    
    /**
     * Returns a 2-element array with the first element being the ancestor
     * page scheduled to not be published and the second being the descendant
     * page that is scheduled to be published, or null if no such scenario
     * exists
     * @param pages
     * @return
     */
    public UploadPage[] getIncompatiblePublishScenario(Collection pages) {
        // loop through the pages and find the ones who weren't selected
        // to be published.  then check if there is a scenario where the descendant
        // of a page (actually, the page's node) flagged to not be published 
        // that doesn't already exist in public is scheduled 
        // to be published.  if this is the case, we can't allow for
        // this publication scenario as the necessary tree structure does not yet
        // exist.
        Set notPublishedPages = new HashSet();
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
            UploadPage page = (UploadPage) iter.next();
            if (!page.getShouldBePublished()) {
                notPublishedPages.add(page);
            }
        }
        // now we have the set, so loop over it and check for inconsistencies
        for (Iterator iter = notPublishedPages.iterator(); iter.hasNext();) {
            UploadPage page = (UploadPage) iter.next();
            // get the node id for this page
            Long nodeId = getWorkingPageDAO().getNodeIdForPage(page.getPageId());
            boolean nodeExists = getPublicNodeDAO().getNodeExistsWithId(nodeId);
            // if the node doesn't exist, make sure that none of the page's
            // descendants are scheduled to be published
            if (!nodeExists) {
                UploadPage descendantPage = getDescendantPageToBePublished(page);
                if (descendantPage != null) {
                    UploadPage[] badPages = new UploadPage[2];
                    badPages[0] = page;
                    badPages[1] = descendantPage;
                    return badPages;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Loop through all descendants and make sure none are scheduled to be 
     * published
     * @param page The page whose descendants will be examined
     * @return The first offending page found, or null if one couldn't be found
     */
    private UploadPage getDescendantPageToBePublished(UploadPage page) {
        for (Iterator iter = page.getChildPages().iterator(); iter.hasNext();) {
            UploadPage nextChild = (UploadPage) iter.next();
            if (nextChild.getShouldBePublished()) {
                return nextChild;
            } else {
                UploadPage descendantPublishedPage = getDescendantPageToBePublished(nextChild);
                if (descendantPublishedPage != null) {
                    return descendantPublishedPage;
                }
            }
        }
        // all children examined, and none are withheld, so return null
        return null;
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
     * @return Returns the pagePusher.
     */
    public PagePusher getPagePusher() {
        return pagePusher;
    }
    /**
     * @param pagePusher The pagePusher to set.
     */
    public void setPagePusher(PagePusher pagePusher) {
        this.pagePusher = pagePusher;
    }
    /**
     * @return Returns the publicPageDAO.
     */
    public PageDAO getPublicPageDAO() {
        return publicPageDAO;
    }
    /**
     * @param publicPageDAO The publicPageDAO to set.
     */
    public void setPublicPageDAO(PageDAO publicPageDAO) {
        this.publicPageDAO = publicPageDAO;
    }
    /**
     * @return Returns the workingPageDAO.
     */
    public PageDAO getWorkingPageDAO() {
        return workingPageDAO;
    }
    /**
     * @param workingPageDAO The workingPageDAO to set.
     */
    public void setWorkingPageDAO(PageDAO workingPageDAO) {
        this.workingPageDAO = workingPageDAO;
    }
    /**
     * @return Returns the publicAccessoryPageDAO.
     */
    public AccessoryPageDAO getPublicAccessoryPageDAO() {
        return publicAccessoryPageDAO;
    }
    /**
     * @param publicAccessoryPageDAO The publicAccessoryPageDAO to set.
     */
    public void setPublicAccessoryPageDAO(
            AccessoryPageDAO publicAccessoryPageDAO) {
        this.publicAccessoryPageDAO = publicAccessoryPageDAO;
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
     * @return Returns the publicationBatchDAO.
     */
    public PublicationBatchDAO getPublicationBatchDAO() {
        return publicationBatchDAO;
    }

    /**
     * @param publicationBatchDAO The publicationBatchDAO to set.
     */
    public void setPublicationBatchDAO(PublicationBatchDAO publicationBatchDAO) {
        this.publicationBatchDAO = publicationBatchDAO;
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
     * @return Returns the batchSubmitter.
     */
    public BatchSubmitter getBatchSubmitter() {
        return batchSubmitter;
    }

    /**
     * @param batchSubmitter The batchSubmitter to set.
     */
    public void setBatchSubmitter(BatchSubmitter batchSumitter) {
        this.batchSubmitter = batchSumitter;
    }

    /**
     * @return Returns the editedPageDAO.
     */
    public EditedPageDAO getEditedPageDAO() {
        return editedPageDAO;
    }

    /**
     * @param editedPageDAO The editedPageDAO to set.
     */
    public void setEditedPageDAO(EditedPageDAO editedPageDAO) {
        this.editedPageDAO = editedPageDAO;
    }

    /**
     * @return Returns the editHistoryDAO.
     */
    public EditHistoryDAO getEditHistoryDAO() {
        return editHistoryDAO;
    }

    /**
     * @param editHistoryDAO The editHistoryDAO to set.
     */
    public void setEditHistoryDAO(EditHistoryDAO editHistoryDAO) {
        this.editHistoryDAO = editHistoryDAO;
    }

    public int conditionallyPushBatchToPublic(PublicationBatch batch, Contributor contr) {
        if (getPublicationBatchDAO().getContributorCanPublishBatch(contr, batch)) {
            try {
                pushPublicationBatchToPublic(batch, contr);
                return BatchPusher.PUSH_PUBLIC_SUCCESSFUL;
            } catch (Exception e) {
                e.printStackTrace();
                return BatchPusher.PUSH_PUBLIC_ERROR;
            }
        } else {
            return BatchPusher.NO_PERMISSIONS;
        }
    }
    /**
     * Checks to see if this single page can be published (i.e. does 
     * the necessary tree structure exist and is the node 
     * currently downloaded in TreeGrow)
     * @param page
     * @return null if it can be published, The download if it's downloaded in TreeGrow, and the name(s)
     * of offending containing group(s) if the necessary tree structure doesn't exist
     */
    public Object getCanPublishPage(MappedPage page) {
        Long pageRootNodeId = page.getMappedNode().getNodeId();
		// first check to see if it's downloaded
        Download openDownload = getDownloadDAO().getOpenDownloadForNode(pageRootNodeId);
        if (openDownload != null) {
            return openDownload;
        }
        boolean pageRootExistsInPublic = getPublicNodeDAO().getNodeExistsWithId(pageRootNodeId);
        // if the page's root already exists, we shouldn't prevent it from being published
        // since there is no possibility for harm
        if (pageRootExistsInPublic) {
        	return null;
        }
        // made it here, means they can publish
        // check that all nodes on the parent page exist in public
        List nodeIdsOnParentPage = getWorkingPageDAO().getNodeIdsOnPage(page.getParentPageId());
        nodeIdsOnParentPage.add(getWorkingPageDAO().getNodeIdForPage(page.getParentPageId()));
        Collection<Long> ancestorIdsOnParentPage = getMiscNodeDAO().getOnlyAncestorIds
        	(pageRootNodeId, nodeIdsOnParentPage);
        List publicNodeIds = getPublicNodeDAO().getNodeIdsWithIds(ancestorIdsOnParentPage);
        
        if (publicNodeIds.size() == ancestorIdsOnParentPage.size()) {
        	// all exist in public, so it's ok
        	return null;
        } else {
        	List<Long> missingNodeIds = new ArrayList<Long>(); 
        	// find the group that doesn't exist in public yet, and return its name
        	for (Iterator iter = ancestorIdsOnParentPage.iterator(); iter.hasNext();) {
				Long nodeId = (Long) iter.next();
				if (!publicNodeIds.contains(nodeId)) {
					missingNodeIds.add(nodeId);
				}
			}
        	List names = getWorkingNodeDAO().getNodeNamesWithIds(missingNodeIds);
        	return StringUtils.returnCommaJoinedString(names);
        }
    }

	public BranchLeafPageArchiver getBranchLeafPageArchiver() {
		return branchLeafPageArchiver;
	}

	public void setBranchLeafPageArchiver(
			BranchLeafPageArchiver branchLeafPageArchiver) {
		this.branchLeafPageArchiver = branchLeafPageArchiver;
	}

	public NodeDAO getMiscNodeDAO() {
		return miscNodeDAO;
	}

	public void setMiscNodeDAO(NodeDAO miscNodeDAO) {
		this.miscNodeDAO = miscNodeDAO;
	}
}
