/*
 * Created on Aug 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.hibernate.TitleIllustration;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PageDAO {
    public List getPagesForContributor(Contributor contr);
    
    public List getPagesForContributor(int contrId);
    
	public boolean getPageExistsWithId(Long id);
	/**
	 * Executes the sql to insert a page with the given id into the
	 * database this dao connects to
	 * @param id
	 */
	public void addPageWithId(Long id, Long nodeId);
    /**
     * Returns the page with the given id, or null if no such page exists
     * @param id The id of the page to look for
     * @return the page with the given id, or null if no such page exists
     */
    public MappedPage getPageWithId(Long id);
    /**
     * Returns whether a node has a page on which it is the root node
     * @param nd The node to check
     * @return true if the node has a page on which it is the root node, false otherwise
     */
    public boolean getNodeHasPage(MappedNode nd);
    public boolean getNodeHasPage(Long nodeId);
    
    public Collection getNodeIdsWithPages(Collection nodeIds);
    
    /**
     * Returns the page that a given node sits on.  This is not the same
     * as the page for a given node.  i.e, for Eukaryotes, this would return the
     * Life page
     * @param nd
     * @return the page a node sits on
     */
    public MappedPage getPageNodeIsOn(MappedNode nd);
    
    /**
     * Returns the page id that a given node sits on.  This is not the same
     * as the page for a given node.  i.e, for Eukaryotes, this would return the
     * id of the Life page
     * @param nd
     * @return the page id a node sits on
     */    
    public Long getPageIdNodeIsOn(MappedNode nd);
    public Long getPageIdNodeIdIsOn(Long ndId);
    
    /**
     * Returns the node for the page that a given node sits on.  For Eukaryotes,
     * it would return the Life node.
     * @param nd
     * @return the node id for the page that the node sits on
     */
    public MappedNode getNodeForPageNodeIsOn(MappedNode nd);
    
    /**
     * Returns the page for a given node.  i.e, for Eukaryotes, this
     * would return the Eukaryotes page
     * @param nd
     * @return The page for a given node, if one exists.  Null otherwise
     */
    public MappedPage getPageForNode(MappedNode nd);
    
    /**
     * Method that checks to see if this node has a page.  If it does,
     * return that page, else return the page that this node sits on.
     * @param nd
     * @return
     */
    public MappedPage getPage(MappedNode nd);
    /**
     * Method that checks to see if this node has a page.  If it does,
     * return that page id, else return the page id that this node sits on.
     * @param nd
     * @return
     */    
    public Long getPageId(Long nodeId);
    
    /**
     * Returns the id of the root node for the page that a node is on.
     * If the node has a page, then the node id itself is returned, if it
     * doesn't then the nodeId of the page root id is returned
     * @param nodeId
     * @return
     */
    public Long getRootNodeIdOnPage(Long nodeId);
    
    /**
     * Returns the page for a given node.  i.e, for Eukaryotes, this
     * would return the id of the Eukaryotes page
     * @param nd
     * @return The page id for a given node, if one exists.  Null otherwise
     */    
    public Long getPageIdForNode(MappedNode nd);
    
    public Long getPageIdForNodeId(Long nodeId);
    
    /**
     * Returns the node id for the page (the id of the basal node for that page)
     * @param pgId
     * @return
     */
    public Long getNodeIdForPage(Long pgId);
    
    /**
     * Returns a list of child pages that have links from the passed-in page.
     * i.e. For Life, this would return Eubacteria, Eukaryotes, Archaea, and
     * Viruses
     * @param page The page to find the children pages for
     * @return A list of child pages accessible from a page
     */
    public List getChildPages(MappedPage page);
    
    /**
     * Returns a list of object arrays where index 0 is the child page name,
     * index 1 is the child page id, and index 2 is the node id
     * @param pg
     * @return
     */
    public List getChildPageNamesAndIds(Long pgId);
    
    /**
     * Returns a list of parent page names that are ancestors of the page on the
     * path back to life
     * @param page
     * @return A List of parent page names
     */
    public List getAncestorPageNames(Long pgId);
    
    /**
     * Return a set of ancestor page ids that are ancestors of the page all the way
     * to life
     * @param pg
     * @return
     */
    public Set getAncestorPageIds(Long pgId);
    /**
     * Wipes out the current ancestors and replaces them with the collection passed-in
     * @param pgId
     * @param ancestors
     */
    public void resetAncestorsForPage(Long pgId, Collection ancestors);
    
    public List getNodeIdsOnPage(MappedPage pg);
    public List getNodeIdsOnPage(MappedPage pg, boolean onlyWithoutPages);
    public List getNodeIdsOnPage(Long pgId);
    public List getNodeIdsOnPageWithIncompleteSubgroups(Long pgId);    
    public List getOrderedByParentNodesOnPage(MappedPage pg, boolean fetchHasPage);
    public List getNodesOnPage(MappedPage pg);
    public List getNodesOnPage(MappedPage pg, boolean onlyWithNames);
    
    // temporary additions
    public List getNodesOnPage(MappedPage page, boolean onlyNamed, boolean onlyIds, boolean orderByOrderOnParent);
    public List getNodesOnPage(Long pageId, boolean onlyNamed, boolean onlyIds, boolean orderByOrderOnParent, String additionalQuery);
    
    public List getRandomPicsForPage(MappedPage pg, boolean includeCurrentPage);
    /**
     * Returns a list of object arrays with the first element being the img loc, the
     * 2nd element being the group name of the page to link to and the 3rd element
     * being the node id of the page to link to
     */    
    public List getRandomPicsForPage(MappedPage pg);   
    
    /**
	 * Returns a list of object arrays that has the following objects 
	 * in indices:
	 * {image, tillusPageGroupName, tillusPageNodeId}
	 * @param page
	 * @param numImages
	 * @return
	 */
	public List<Object[]> getRandomPicsForImageGallery(MappedPage page, int numImages);
    /**
     * Returns a list of object arrays that has the following objects 
     * in indices:
     * {image, tillusPageGroupName, tillusPageNodeId}
     * @param page
     * @param numImages
     * @return
     */    
    public List<Object[]> getPicsForImageGallery(MappedPage page, int numImages, int startIndex);
    /**
     * Returns a count of the number of images shown for a title illustration 
     * @param page
     * @return
     */
    public int getNumPicsForImageGallery(MappedPage page);
    
    /**
     * returns all version ids for all title illustrations
     * @return
     */
    public List<Long> getTillusVersionIds();
    
    public String getGroupNameForPage(Long pgId);
    public Object[] getGroupNameAndNodeIdForPage(Long pgId);
    public void setContributorDAO(ContributorDAO dao);
    public void setImageDAO(ImageDAO dao);
    public void addPage(MappedPage pg, Contributor contr);
    public void savePage(MappedPage pg);
    public void deletePage(MappedPage pd);
    public void deletePageAndReassignNodes(MappedPage pg);
    public void clearCacheForPage(MappedPage pg);
    
    public int getTitleIllustrationBranchDefaultHeight(MappedPage page);
    public List getTitleIllustrationsPointingAtVersion(Long versionId);
    public void saveTitleIllustration(TitleIllustration illustration);
    public void deleteTitleIllustrationsPointingAtVersionIds(List versionIds);
    public void setFirstOnlineDateForPageWithId(Long pageId, Date date);
   
    public String getPageTypeForPageWithId(int id);
    public int getNumAncestorsForPage(Long pageId);
    
    public Long getParentPageIdForPage(Long pageId);
    public int getNumPagesWithIds(Collection ids);

    public boolean getTextSectionExistsWithId(Long id);
    public MappedTextSection getTextSectionWithId(Long id);
    public void saveTextSection(MappedTextSection textSection);
    public void deleteTextSection(MappedTextSection textSection);
    public void addTextSectionWithId(Long id);    
    
    public void copyPageToDB(MappedPage page);
    /**
     * return all the node ids and edit history ids
     * @return
     */
    public List<Object[]> getNodeIdsAndEditHistoryIds();
    public List getEditHistoryIdsForPageIds(Collection pageIds);
    public MappedPage addPageForNode(MappedNode node, Contributor contributor);
    public MappedPage addPageForNode(MappedNode node, Contributor contributor, boolean writeAsList);
    public void insertNewAncestorForPages(MappedPage parentPage, MappedPage newPage);
    public void insertNewAncestorForPages(Long parentPageId, Long pageId);
    public List getDescendantPageIds(Collection rootPageIds);
    public List getDescendantPageIds(Long pageId);
    public void deleteAncestorPagesNotInBranch(Collection pageIds);
    
    /**
     * Returns a list of object arrays that contain:
     * (1) cladename
     * (2) page status
     * (3) author ids
     * (4) last revision date
     * (5) initial publication date
     * (6) num title illustrations
     * @param rootPageId
     * @return
     */
    public List<Object[]> getDescendantPageInfo(Long rootPageId);
    public List<MappedPage> getDescendantPagesWithInfo(Long rootPageId);
    
    public void updateHasIncompleteSubgroupsForPage(Long pageId, boolean value);
    public boolean getPageLeadsToCompletePage(Long pageId);
    /**
     * Returns a list of the branch or leaf page names that a contributor
     * either owns the copyright for or is an author
     * @param contr The contributor to look for
     * @return
     */
    public List getGroupNamesContributorOwns(Contributor contr);
    /**
     * Returns the max order on page of all the nodes that live on this page
     * @param pageId
     * @return
     */
    public int getMaxOrderOnPage(Long pageId);
    public void updateParentPageIdForPage(Long pageId, Long newParentPageId);
	public void addNewAncestorsForPages(List descendantPageIds, Set pageAncestorIds);
	public void updateContentChangedDateForPage(Long pageId, Date date);
	
	public Collection getPageContributorIds();
	public int getNumPagesForContributor(int contributorId);
	/**
	 * return a list of page and node ids for the pages in the id list
	 * @param ancestorIds
	 * @return
	 */
	public List<Object[]> getPageIdsAndNodeIdsForPages(Collection pageIds);
	/**
	 * Returns a list of page ids for the ancestor node ids in the argument collection.
	 * @param nodeAncestorIds a collection of node-ids represented as Longs 
	 * @return a list of page ids for the node ids passed.
	 */
	public List<Long> getPageIdsForNodeIds(Collection nodeAncestorIds);
    /**
     * Returns a list of 4-element object array with the 
     * the 1st element being the contributor's name,
     * the 2nd element being the date of the edit, 
     * and the 3rd being the node name of the edited page
     * and the 4th being page id of the edited page
     * @param pageId
     * @param lastChangedDate
     * @return
     */
    public List<Object[]> getContributorsDatesAndPagesRecentlyChanged(Long pageId, Date lastChangedDate);

    /**
     * Updates the cross-reference table responsible for managing the attachment of pages to nodes 
     * so that the orphaned page will be reattached to the new node-id
     * 
     * Used by the custom taxa import functionality developed in February 2008
     * @author lenards
     * 
     * @param pageId the page that is being reattached
     * @param oldNodeId the node-id the page was attached to
     * @param newNodeId the new node-id to attach the page to
     */
    public void reattachPage(Long pageId, Long oldNodeId, Long newNodeId);    
    
    // Methods for getting branch/leaf pages and their statuses.
    
	public int getBranchPagesLeadToCompletePages(Long pageId);
    public int getBranchPagesLeadToUnderConstructionPages(Long pageId);    
    public int getBranchPagesLeadToTemporaryPages(Long pageId);
    public int getLeafPagesLeadToCompletePages(Long pageId);
    public int getLeafPagesLeadToUnderConstructionPages(Long pageId);
    public int getLeafPagesLeadToTemporaryPages(Long pageId);    
    
    /**
     * Retrieve a page at random. 
     * 
     * Used the rand() function to get a random page from Pages.
     * 
     * @return an instance of MappedPage selected at random from the table. 
     */
    public MappedPage getRandomPage();
}
