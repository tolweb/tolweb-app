/*
 * Created on Nov 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.Permission;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PermissionChecker {
	public Document checkPermission(String user, String password);
    /**
     * Checks to see if the password works for the contributor
     * @param user
     * @param password
     * @return An error document if failure, null otherwise
     */
	public Document checkPermission(Contributor user, String password);
	/**
	 * Returns an xml doc specifying if this contributor has permissions for the node
	 * @param user
	 * @param password
	 * @param nodeId
	 * @return Null, if the contributor has permissions, an error document otherwise 
	 */
    public Document checkPermissionForNode(Contributor user, String password, Long nodeId);
	/**
	 * Returns an xml doc specifying if this contributor has permissions for the node
	 * @param email of the contributor to check
	 * @param password
	 * @param nodeId
	 * @return Null, if the contributor has permissions, an error document otherwise 
	 */    
    public Document checkPermissionForNode(String user, String password, Long nodeId);
	/**
	 * Returns a boolean if this contributor has read/write permissions for the node
	 * @param contributor
	 * @param nodeId
	 * @return true, if the contributor has permissions, false otherwise 
	 */        
    public boolean checkHasPermissionForNode(Contributor contributor, Long nodeId);
    /**
     * checks to see whether the given contributor can edit the page.
     * if the page is currently checked out to another contributor,
     * it returns that contributor.  otherwise it returns null
     * 
     * @param contr
     * @param page
     * @return the checked out contributor, if they exist, null otherwise
     */
    public Contributor checkPageIsLocked(Contributor contr, MappedPage page);
    /**
     * Checks whether the page has been submitted for publication
     * @param page the page to check
     * @return true if submitted, false otherwise
     */
    public boolean checkPageIsSubmitted(MappedPage page);
    /**
     * Checks whether the page has been submitted for publication
     * and also whether the contributor is an editor and can edit
     * the submitted page
     * @param contr The contributor to check if they are an editor
     * @param page the page to check
     * @return true if submitted, false otherwise
     */    
    public boolean checkPageIsSubmitted(Contributor contr, MappedPage page);
    public String getLockedMessageForEditHistory(Long editHistoryId);
    public Contributor checkAccessoryPageIsLocked(Contributor contr, MappedAccessoryPage page);
    public boolean checkAccessoryPageIsSubmitted(Contributor contr, MappedAccessoryPage page);
    /**
     * Checks whether the contributor is an EDITOR for a particular
     * node, not just whether they can change node content
     * @param contributor
     * @param nodeId
     * @return
     */
    public boolean checkHasEditingPermissionForNode(Contributor contributor, Long nodeId);
    /**
     * Returns a list of contributor ids that have read/write permissions for the nodeId 
     * parameter or any of its ancestors
     * @param nodeId
     * @return
     */
    public List getContributorIdsWithPermissionsForAncestorNodes(Long nodeId);
    
    /**
     * Returns a list of the scientific editor who have editing permissions over
     * a particular node
     * @param node
     * @return
     */
    public List getScientificEditorsForNodes(Set nodeId);
    public Document returnNoContributorDocument();
    public Document returnWrongPasswordDocument();
    public Document returnNoPermissionsDocument(); 
    /**
     * Removes all node attachments for the contributor, permissions or not
     * @param contr
     */
    public void removeAllNodeAttachmentsForContributor(Contributor contr);
    public void updatePermissionsForContributor(Contributor contr);
    /**
     * @deprecated
     * @param contr
     * @param hasPermissions
     */
    public void updateNodeAttachmentsForContributor(Contributor contr, boolean hasPermissions);
    public void updateNodeAttachmentsForContributor(Contributor contr, int attachmentType, Collection<Permission> permissions);
    public void removeNodeAttachmentForContributor(int contrId, Long nodeId);
    public void addNodeAttachmentForContributor(int contrId, Long nodeId);
    public void addNodeAttachmentForContributor(int contrId, Long nodeId, boolean hasPermissions);
    /**
     * Checks to see if a contributor is *physically* attached to a node.  If
     * they are it returns the boolean true.  If not, but they are attached to 
     * an ancestor, it returns the ancestor name.  If not attached to the node
     * or an ancestor, it returns the boolean false.
     * @param contrId
     * @param nodeId
     * @param hasPermissions
     * @return
     */
    public Object checkIsAttachedToNode(int contrId, Long nodeId, boolean hasPermissions);
    
    public boolean isLearningEditor(Contributor contr);
    /**
     * Whether the contributor is an editor (currently only David, Danny, and Katja) 
     * @param contr
     * @return
     */
    public boolean isEditor(Contributor contr);
    public boolean checkEditingPermissionForPage(Contributor contr, MappedAccessoryPage page);
    public boolean checkEditingPermissionForSubmittedPage(Contributor contr, MappedAccessoryPage page);
    public boolean checkUsePermissionForImageOnPage(Contributor contr, NodeImage img, Long accPageId, boolean isBranchOrLeaf);
    public boolean checkEditingPermissionForImage(Contributor contr, NodeImage img);

    /**
     * Returns a list of node ids that this contributor has editing permission over
     * @param contr
     * @return
     */
    public List getPermissionsForContributor(Contributor contr);
    /**
     * Returns a list of node ids that this contributor is attached to, conditionally
     * also checking whether they have editing permissions
     * @param contr
     * @param checkEditingPermissions Whether to check for editing permissions
     * @return
     */    
    public List<Long> getNodeAttachmentsForContributor(Contributor contr, boolean checkEditingPermissions);
    /**
     * Returns a list of node ids that this contributor is attached to
     * that have the specified node attachment type
     * @param contr
     * @param attachmentType (as of now: interest, permission, or cutoff)
     * @return
     */        
    public List<Long> getNodeAttachmentsForContributor(Contributor contr, int attachmentType);
    
    /**
     * Checks whether the editingContributor has permissions to edit the 
     * editedContributor
     * @param editingContributor
     * @param editedContributor
     * @return
     */
    public boolean checkHasEditingPermissionForContributor(Contributor editingContribuor, Contributor editedContributor);
    
    public Collection getContributorIdsAttachedToNodeIds(Collection nodeIds, boolean hasPermission);
    public List getNodeNamesContributorAttachedTo(Integer contrId, boolean checkPermission);
}
