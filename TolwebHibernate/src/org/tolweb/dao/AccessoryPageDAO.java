/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Portfolio;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface AccessoryPageDAO {
	public static final String CONTRIBUTOR_IDS = "contributorids";
	public static final String CONTRIBUTOR = "contributor";
	public static final String COPY_YEAR = "copyyear";
	public static final String GROUP = "group";
	public static final String GROUP_ID = "groupid";
	public static final String TITLE = "title";
	public static final String ID = "id";
	public static final String BONUSPAGES = "bonuspages";
	public static final String TREEHOUSES = "treehouses";
	public static final String BIOGRAPHIES = "biographies";
	public static final String LIMIT = "limit";
	
	/**
	 * Adds an accessory page with the passed-in id to the database (used for copying between dbs)
	 * @param id The id of the page to add
	 * @param discrimiminator TODO
	 */
	public void addAccessoryPageWithId(Long id, int discrimiminator);
	public void addAccessoryPage(MappedAccessoryPage page, Contributor contributor);
	public void saveAccessoryPage(MappedAccessoryPage page);
	public void deleteAccessoryPage(MappedAccessoryPage page);
	public List getAccessoryPagesMatchingCriteria(Map args);
	public List getSubmittedTreehouses();
    public List getSubmittedTreehousesForTeacher(Contributor teacher);
    public List getContributorSubmittedTreehouses(Contributor contr);
	public List getSubmittedArticlesAndNotes();
	public MappedAccessoryPage getAccessoryPageWithId(int id);
	public MappedAccessoryPage getAccessoryPageWithId(Long id);
	public void setContributorDAO(ContributorDAO dao);
	public void setNodeDAO(NodeDAO dao);
    
    public boolean getAccessoryPageExistsWithId(Long id);
	
	/**
	 * Returns a list of all of the accessory pgs (articles, notes, treehouses) attached
	 * to the specified node ids
	 * @param nodeIds
	 * @return
	 */	
	public List getAccessoryPagesForNodes(String nodeIds);
	
	/**
	 * Returns a list of all of the accessory pgs (articles, notes, treehouses) attached
	 * to the specified node
	 * @param nd
	 * @return
	 */
	public List getAccessoryPagesForNode(MappedNode nd);
	
	/**
	 * Returns a list of the articles and notes (actual objects) attached to the specified node
	 * @param nd
	 * @return
	 */
	public List getArticlesAndNotesForNode(MappedNode nd);
	
    /**
     * Returns a List of 2-item arrays for the article bonus pages for
     * a given node.  Index 0 is the menu of the page, Index 1 is
     * the id
     * @param nd
     * @return
     */
	public List getArticlesForNode(MappedNode nd);
    /**
     * Returns a List of 2-item arrays for the note bonus pages for
     * a given node.  Index 0 is the menu of the page, Index 1 is
     * the id
     * @param nd
     * @return
     */	
	public List getNotesForNode(MappedNode nd);
    /**
     * Returns a List of 3-item arrays for the treehouses for
     * a given node.  Index 0 is the menu of the page, Index 1 is
     * the id, Index 3 is the treehouse type
     * @param nd
     * @return
     */	
    public List getTreehousesForNode(MappedNode nd);
    public void clearCacheForPage(MappedAccessoryPage page);
    public void clearCacheForPage(MappedAccessoryPage page, boolean refreshNodes);
    
    /**
     * Takes all accessory pages attached to this node and reattaches them to the
     * specified parent node.
     * @param nodeToBeDeleted The node which will no longer exist
     * @param newParentNode The new place for the accessory pages to live
     */
    public void reattachAccessoryPagesForNode(MappedNode nodeToBeDeleted, MappedNode newParentNode);
    public void reattachAccessoryPagesForNodes(Collection nodeIds, MappedNode newNode);
    public void savePortfolio(Portfolio portfolio);
    public Portfolio getPortfolioWithId(Long id);
    public void addPortfolioWithId(Long id);
    public String getPageTitleForAccessoryPageWithId(int id);
    public String getPageTypeForAccessoryPageWithId(int id);
    public boolean getPageIsArticle(int id);
    public void copyAccessoryPageToDB(MappedAccessoryPage page);
    
    public List getTreehousesForProject(ClassroomProject project);
    public List getMostRecentlyEditedTreehouses(Contributor contributor);
    public List getContributorIdsForTreehousesAttachedToNodeIds(Collection nodeIds);
    public List getContributorIdsForArticleNotesAttachedToNodeIds(Collection nodeIds);    
    public List getArticleNoteTitlesIdsForContributor(Contributor contr);
    public List getTreehouseTitlesIdsForContributor(Contributor contr);
    public List getAccessoryPagesForContributor(Contributor contr);
    
    public List getMostRecentlyEditedTreehouses(int num);
    public Collection getArticleNoteContributorIds();
    public int getNumPagesForContributor(int contributorId);
    
    /**
     * Updates the cross-reference table responsible for managing the attachment of accessory pages to nodes 
     * so that the orphaned accessory page will be reattached to the new node-id
     * 
     * Used by the custom taxa import functionality developed in February 2008
     * @author lenards
     *  
     * @param accPageId the accessory page that is being reattached
     * @param oldNodeId the node-id the accessory page was attached to
     * @param newNodeId the new node-id to attach the accessory page to  
     */
    public void reattachAccessoryPage(Long accPageId, Long oldNodeId, Long newNodeId);
}
