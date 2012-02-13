/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface BatchPusher {
    public static final int NO_PERMISSIONS = 0;
    public static final int PUSH_PUBLIC_ERROR = 1;
    public static final int PUSH_PUBLIC_SUCCESSFUL = 2;
    /**
     * Pushes all nodes and pages contained in the batch
     * to the public database
     * @param batchId
     * @throws Exception
     */
    //public void pushBatchToPublic(Long batchId) throws Exception;
    
    /**
     * Pushes the pages in this submission batch to the public site
     * @param batchId
     * @param editingContributor
     * @return TODO
     * @throws Exception
     */
    public String pushPublicationBatchToPublic(PublicationBatch batch, Contributor editingContributor) throws Exception;
    
    /**
     * Sorts the uploaded pages according to tree structure, also notes
     * whether some pages are not allowed to be submitted for publication
     * (due to them being downloaded, or already submitted, or a given
     * contributor not having permission to submit them)
     * @param contr
     * @param checkPermissions TODO
     * @param initPublishParameter TODO
     * @param batch
     * @return
     */
    public List getSortedUploadPages(Collection uploadedPagesSet, Contributor contr, boolean checkPermissions, boolean initPublishParameter);
    /**
     * Returns a 2-element array with the first element being the ancestor
     * page scheduled to not be published and the second being the descendant
     * page that is scheduled to be published, or null if no such scenario
     * exists
     * @param pages
     * @return
     */
    public UploadPage[] getIncompatiblePublishScenario(Collection pages); 
    public Comparator getClosestToRootComparator();
    /**
     * Loops through a collection of uploaded pages
     * and returns the page(s) that are the root-most in the collection
     * @param pages
     * @return
     */    
    public List getRootMostPages(Collection pages);
    /**
     * Checks to see if the contributor can push this page to public.  If they can,
     * then push it.  Return whether they could push, and if they did, whether it
     * was successful
     * @param batch
     * @param contr
     * @return
     */
    public int conditionallyPushBatchToPublic(PublicationBatch batch, Contributor contr);
    /**
     * Checks to see if this page can be published (i.e. does the necessary tree structure exist),
     * and is the node currently downloaded in TreeGrow
     * @param page
     * @return null if it can be published, the download if it's downloaded in TreeGrow, and the name
     * of an offending containing group if the necessary tree structure doesn't exist
     */
    public Object getCanPublishPage(MappedPage page);
}
