package org.tolweb.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.tolweb.dao.EditedPageDAO;
import org.tolweb.hibernate.EditedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.BatchPusher;
import org.tolweb.treegrowserver.UploadBatch;
import org.tolweb.treegrowserver.UploadPage;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;

public class EditedPagesBuilder {
    private EditedPageDAO editedPageDAO;
    private UploadBatchDAO uploadBatchDAO;
    private BatchPusher batchPusher;
    
    /**
     * Returns a list of lists of upload pages grouped together according
     * to their hierarchies (contiguous blocks of pages form one list)
     * @param contr
     * @return
     */
    @SuppressWarnings("unchecked")
    public List buildPageBlocksForContributor(Contributor contr) {
        // do the upload batches first
        //List batches = buildUploadBatchesFromIdsAndPages(contr);
        // grab the page ids off the end of the list
        /*Set ubPageIds = (Set) batches.remove(batches.size() - 1);
        for (Iterator iter = new ArrayList(batches).iterator(); iter.hasNext();) {
            UploadBatch nextBatch = (UploadBatch) iter.next();
            List uploadedPages = getBatchPusher().getSortedUploadPages(nextBatch.getUploadedPagesSet(), contr, true, true);
            if (uploadedPages.size() == 0) {
                batches.remove(nextBatch);
            } else {
                nextBatch.setSortedUploadedPages(uploadedPages);
            }
        } */       
        List batches = new ArrayList();
        List pageIds = getEditedPageDAO().getEditedPageIdsForContributor(contr, EditedPage.BRANCH_LEAF_TYPE);
        List uploadPages = new ArrayList();
        // convert these to upload pages so we can use the existing code for
        // working with those
        for (Iterator iter = pageIds.iterator(); iter.hasNext();) {
            Long pageId = (Long) iter.next();
            /*if (ubPageIds.contains(pageId)) {
                // if it's already contained in the set of UploadBatches,
                // then no need to duplicate it.
                continue;
            }*/
            UploadPage nextUploadPage = new UploadPage();
            nextUploadPage.setPageId(pageId);
            uploadPages.add(nextUploadPage);
        }
        // ok we have all of the current work, now we will sort them according to numAncestors
        uploadPages = batchPusher.getSortedUploadPages(uploadPages, null, false, true);
        List returnLists = new ArrayList();
        // need to break these up into lists of pages, so:
        // loop through the list of returned pages and for each page seen:
        // get all of its children and add them to a list for that page and
        // remove the children from the main list
        for (Iterator iter = new ArrayList(uploadPages).listIterator(); iter.hasNext();) {
            UploadPage page = (UploadPage) iter.next();                
            // if it's not in the main list, don't create a special element for it
            if (!uploadPages.contains(page)) {
                continue;
            }
            ArrayList currentList = new ArrayList();
            returnLists.add(currentList);
            currentList.add(page);
            for (Iterator iterator = page.getChildPages().iterator(); iterator.hasNext();) {
                UploadPage nextChild = (UploadPage) iterator.next();
                currentList.add(nextChild);
                uploadPages.remove(nextChild);
            }
        }
        // at this point loop over all of the remaining pages and see if they can
        // be "glued" on to an existing batch of pages
        Hashtable pageIdsToUploadPages = new Hashtable();
        /*for (Iterator iter = batches.iterator(); iter.hasNext();) {
            UploadBatch nextBatch = (UploadBatch) iter.next();
            addUploadPagesToHashtable(nextBatch.getSortedUploadedPages(), pageIdsToUploadPages);
        }*/
        // Loop over this collection and create an upload batch wrapper for each one
        for (Iterator iter = returnLists.iterator(); iter.hasNext();) {
            List nextList = (List) iter.next();
            addUploadPagesToHashtable(nextList, pageIdsToUploadPages);
            // grab the root of this list and check to see if it's parent page 
            // has been seen thus far.  if it has, add this on to the end of the
            // parent's child pages
            UploadPage currentRoot = (UploadPage) nextList.get(0);
            if (currentRoot != null) {
                UploadPage parentPage = null;
                if (currentRoot.getParentPageId() != null) {
                	parentPage = (UploadPage) pageIdsToUploadPages.get(currentRoot.getParentPageId());
                }
                if (parentPage != null) {
                    UploadBatch parentPageBatch = null;
                    int parentPageIndex = -1;
                    // find the upload batch that contains the parent, and insert this
                    // list of pages right after the parent
                    for (Iterator iterator = batches.iterator(); iterator
                            .hasNext();) {
                        UploadBatch batch = (UploadBatch) iterator.next();
                        parentPageIndex = batch.getSortedUploadedPages().indexOf(parentPage); 
                        if (parentPageIndex != -1) {
                            parentPageBatch = batch;
                            break;
                        }
                    }
                    if (parentPageBatch != null) {
						parentPageBatch.getSortedUploadedPages().addAll(
								parentPageIndex + 1, nextList);
					}
                } else {
                    UploadBatch batch = new UploadBatch();
                    batch.setSortedUploadedPages(nextList);
                    batches.add(batch);
                }
            }
        }        
        Collections.sort(batches, getRootNameComparator());
        // remove any batches that have an empty root group name
        for (Iterator iter = new ArrayList(batches).iterator(); iter.hasNext();) {
			UploadBatch batch = (UploadBatch) iter.next();
			UploadPage firstUploadPage = (UploadPage) batch.getSortedUploadedPages().get(0);
			if (StringUtils.isEmpty(firstUploadPage.getGroupName())) {
				batches.remove(batch);
			}
		}
        return batches;
    }
    
    @SuppressWarnings("unchecked")
    private void addUploadPagesToHashtable(Collection uploadPages, Hashtable table) {
        for (Iterator iterator = uploadPages.iterator(); iterator.hasNext();) {
            UploadPage nextPage = (UploadPage) iterator.next();
            table.put(nextPage.getPageId(), nextPage);
        }        
    }
    
    /**
     * Builds a list of UploadBatches for a contributor.  Returns the list.
     * The last item of the list is not an UploadBatch, but a set of all the page ids
     * that this user has uploaded in TreeGrow.  This is used later when
     * de-duplicating EditedPages from UploadBatches.
     * @param contr
     * @return
     /
    private List buildUploadBatchesFromIdsAndPages(Contributor contr) {
        Set allPageIds = new HashSet();
        List idsAndPageIds = getUploadBatchDAO().getActiveBatchesForContributor(contr, true);
        List batches = new ArrayList();
        UploadBatch currentBatch = null;
        Set currentUploadPages = null;
        Long lastBatchId = null;
        for (Iterator iter = idsAndPageIds.iterator(); iter.hasNext();) {
            Object[] nextPairs = (Object[]) iter.next();
            Long currentBatchId = (Long) nextPairs[0];
            Long nextPageId = (Long) nextPairs[1];
            allPageIds.add(nextPageId);
            if (lastBatchId == null || !currentBatchId.equals(lastBatchId)) {
                if (currentBatch != null) {
                    currentBatch.setUploadedPagesSet(currentUploadPages);
                    // store it in the list
                    batches.add(currentBatch);
                }
                currentBatch = new UploadBatch();
                currentBatch.setBatchId(currentBatchId);
                currentUploadPages = new HashSet();
            }
            UploadPage up = new UploadPage();
            up.setPageId(nextPageId);
            currentUploadPages.add(up);
            lastBatchId = currentBatchId;
        }
        // need to do this once we've broken out of the loop as well -- fencepost problem
        if (currentBatch != null) {
            currentBatch.setUploadedPagesSet(currentUploadPages);
            batches.add(currentBatch);
        }        
        batches.add(allPageIds);
        //List batches = getUploadBatchDAO().getActiveBatchesForContributor(contr, false);
        //batches.add(allPageIds);
        return batches;
    }*/
    @SuppressWarnings("unchecked")
    private Comparator getRootNameComparator() {
        return new Comparator() {
            public int compare(Object o1, Object o2) {
                if (UploadBatch.class.isInstance(o1) && UploadBatch.class.isInstance(o2)) {
                    String batch1Root = ((UploadPage) ((UploadBatch) o1).getSortedUploadedPages().get(0)).getGroupName();
                    String batch2Root = ((UploadPage) ((UploadBatch) o2).getSortedUploadedPages().get(0)).getGroupName();
                    return batch1Root.compareTo(batch2Root);
                } else {
                    return 0;
                }
            }
        };
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
     * @return Returns the batchPusher.
     */
    public BatchPusher getBatchPusher() {
        return batchPusher;
    }

    /**
     * @param batchPusher The batchPusher to set.
     */
    public void setBatchPusher(BatchPusher batchPusher) {
        this.batchPusher = batchPusher;
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
}
