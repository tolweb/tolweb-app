/*
 * Created on Feb 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.tree.Node;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface UploadBuilder {
	public static final int CREATE_PAGES_INTERNAL_NODES = 0;
	public static final int CREATE_PAGES_SUPERFAMILY = 1;
	public static final int CREATE_PAGES_FAMILY = 2;
	public static final int CREATE_PAGES_SUBFAMILY = 3;
	public static final int CREATE_PAGES_TRIBE = 4;
	//public static final int CREATE
    public Document buildUpload(String xmlString) throws Exception;
    public Document buildUpload(String xmlString, Contributor contr, boolean parsePageAndAdditionalInfo) throws Exception;
	public Document buildUpload(String xmlString, Contributor contr, boolean parsePageAndAdditionalInfo, boolean deleteChildrenIfNotPresent) throws Exception;
    /**
     * Allows users to upload xml and attach it to an existing parentNodeId
     * @param xmlString
     * @param contr
     * @param parsePageAndAdditionalInfo
     * @param rootNodeId
     * @return
     * @throws Exception
     */
    public Document buildUpload(String xmlString, Contributor contr, boolean parsePageAndAdditionalInfo, 
    		Long rootNodeId) throws Exception;
	public void readNode(Element nodeElement, Long parentNodeId, ParentPageInfo parentPageInfo, Download download,
			Upload upload, Hashtable oldIdsToNewIds, Set childIds, Set seenIds,
			Set nodeAncestorIds, Set pageAncestorIds, boolean isNewVersion) throws Exception;
    /**
     * Uploads children of new nodes to the parent page
     * @param parentPage the parent page the existing child nodes live on
     * @param parentNodes the existing child nodes
     * @param createPagesAllNamed assorted booleans for page creation criteria
     * @param createPagesInternalNodes
     * @param createPagesSuperfamily
     * @param createPagesFamily
     * @param createPagesSubfamily
     * @param createPagesTribe
     * @param contributor the user who is creating these pages
     * @param nodesString the string used to generate the import
     * @param useTaxonLists whether new pages should have taxon lists or trees
     * @param createPagesForParents whether to create pages for the existing children
     */
	public void uploadNewNodes(MappedPage parentPage, List parentNodes, boolean createPagesAllNamed,
    		boolean createPagesInternalNodes, 
			boolean createPagesSuperfamily, boolean createPagesFamily, boolean createPagesSubfamily, 
			boolean createPagesTribe, Contributor contributor, String nodesString,
			boolean useTaxonLists, boolean createPagesForParents);	
	public void uploadNewNodes(MappedNode parentNode, List childNodes, boolean createPagesAllNamed,
			boolean createPagesInternalNodes, 
			boolean createPagesSuperfamily, boolean createPagesFamily, boolean createPagesSubfamily, 
			boolean createPagesTribe, Contributor contributor, String nodesString, boolean useTaxonLists);
	/**
	 * Checks to see whether it's legal for the possible parent to be the new parent
	 * of the node to move.  Reasons it wouldn't be legal are:
	 * 1) the new parent is a child of the node to move
	 * 2) the new parent is downloaded in TreeGrow
	 * @param nodeToMove
	 * @param possibleParent
	 * @return An error message explaning why this isn't legal, or null
	 * if it is
	 */
	public String getIsLegalNewParent(MappedNode nodeToMove, MappedNode possibleParent);
	/**
	 * Moves branchRoot and all of its children and attachments to newParent
	 * @param branchRoot
	 * @param newParent
	 */
	public void moveBranch(MappedNode branchRoot, MappedNode newParent, Contributor contr);
    public MappedNode checkForTwoChildren(MappedNode parentNode);
    public void copyValuesForMappedNode(MappedNode node, Node originalNode, ForeignDatabase sourceDatabase);
}
