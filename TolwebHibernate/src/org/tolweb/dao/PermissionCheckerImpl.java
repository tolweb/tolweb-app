 /*
 * Created on Nov 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

import ognl.Ognl;
import ognl.OgnlException;

import org.hibernate.Session;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.hibernate.ContributorPermission;
import org.tolweb.hibernate.EditHistory;
import org.tolweb.hibernate.ImagePermission;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MultiContributorObject;
import org.tolweb.hibernate.Student;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.Permission;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrow.page.AccessoryPageContributor;
import org.tolweb.treegrow.page.PageContributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PermissionCheckerImpl extends BaseDAOImpl implements PermissionChecker {
    private PasswordUtils passwordUtils;
    private ContributorDAO contributorDAO;
    private AccessoryPageDAO accessoryPageDAO;
    private PageDAO pageDAO;
    private NodeDAO nodeDAO;
    private EditHistoryDAO editHistoryDAO;
    
    public Document checkPermission(String contributor, String password) {
    	Contributor contr = getContributorDAO().getContributorWithEmail(contributor);
    	return doCommonPermissionChecking(contr, password);
    }
    
    public Document checkPermission(Contributor contributor, String password) {
        return doCommonPermissionChecking(contributor, password);
    }
    
    private Document doCommonPermissionChecking(Contributor contributor, String password) {
        if (contributor == null) {
            return returnNoContributorDocument();
        } else if (!passwordUtils.checkPassword(contributor, password)) {
            return returnWrongPasswordDocument();
        } else {
            return null;
        }
    }
    
    public Document checkPermissionForNode(String email, String password, Long nodeId) {
        Contributor contributor = getContributorDAO().getContributorWithEmail(email);
        return checkPermissionForNode(contributor, password, nodeId);
    }
    
    public Document checkPermissionForNode(Contributor contributor, String password, Long nodeId) {
        Document errorDoc = doCommonPermissionChecking(contributor, password);
        if (errorDoc != null) {
            return errorDoc;
        } else {
    		try {
    		    List nodeIds = getNodeAttachmentsForContributor(contributor, Contributor.NODE_ATTACHMENT_PERMISSION);
    		    if (nodeIds.size() == 0) {
    			    return returnNoPermissionsDocument();    		        
    		    } else {
    		    	Collection<Long> ancestorsWithPermissions = getNodeDAO().getOnlyAncestorIds(nodeId, nodeIds);

	    			// No permission for this node
	    			if (ancestorsWithPermissions == null || ancestorsWithPermissions.size() == 0) {
	    			    return returnNoPermissionsDocument();
	    			} else {
	    				// they have permission for an ancestor but we need to make sure their permissions
	    				// aren't cutoff by another ancestor node that is a child of the ancestor the user
	    				// has permissions for
	    				List cutoffNodeIds = getNodeAttachmentsForContributor(contributor, Contributor.NODE_ATTACHMENT_CUTOFF);
	    				if (cutoffNodeIds != null && cutoffNodeIds.size() > 0) {

	    					Collection<Long> cutoffAncestors = getNodeDAO().getOnlyAncestorIds(nodeId, cutoffNodeIds);
	    					if (cutoffAncestors != null && cutoffAncestors.size() > 0) {
	    						// Some cutoff ids are ancestors of the node id, now check to see if the
	    						// cutoff ids are descendants of the ancestor permission ids.
	    						// Since all of these are ancestors of the same node, figure out how many 
	    						// ancestors each node has.  The one with the most ancestors, is the id
	    						List results = getMaxAncestorsInNodes(cutoffAncestors);
	    						int maxNumCutoffAncestors = (Integer) results.get(1);
	    						results = getMaxAncestorsInNodes(ancestorsWithPermissions);
	    						int maxNumPermissionAncestors = (Integer) results.get(1);
	    						if (maxNumCutoffAncestors > maxNumPermissionAncestors) {
	    							// in this case, the closest ancestor to the actual node
	    							// is a cutoff node, so there are no permissions
	    							return returnNoPermissionsDocument();
	    						}
	    					}
	    				}
	    			}
    		    }
    			return null;
    		} catch (Exception e) {
    			e.printStackTrace();
    			return returnNoPermissionsDocument();
    		}                
        }
    }
    
    /**
     * Returns a 2-element list with the first element being the node id
     * that has the most ancestors and the 2nd-element being the int number of
     * ancestors
     * @param nodeIds
     * @return
     */
    private List getMaxAncestorsInNodes(Collection<Long> nodeIds) {
		int maxAncestors = -1;
		Long maxAncestorsId = null;
		for (Iterator iter = new ArrayList(nodeIds).iterator(); iter
				.hasNext();) {
			Long currentAncestorId = (Long) iter.next();
			int numAncestors = getNodeDAO().getNumAncestorsForNode(currentAncestorId);
			if (numAncestors > maxAncestors) {
				maxAncestors = numAncestors;
				maxAncestorsId = currentAncestorId;
			}
		}    	
		ArrayList returnList = new ArrayList();
		returnList.add(maxAncestorsId);
		returnList.add(maxAncestors);
		return returnList;
    }
    
    public boolean checkHasPermissionForNode(Contributor contributor, Long nodeId) {
        if (contributor != null) {
	        Document doc = checkPermissionForNode(contributor, contributor.getPassword(), nodeId);
	        return doc == null;
        } else {
            return false;
        }
    }  
    public Contributor checkPageIsLocked(Contributor contr, MappedPage page) {
    	return checkLockForObject(contr, page);
    }
    /**
     * Checks to see if a page is submitted, taking into account
     * whether the contributor parameter is an editor and can
     * edit a submitted page
     */
    public boolean checkPageIsSubmitted(Contributor contr, MappedPage page) {
    	if (page != null && contr != null) {
	        boolean submitted = checkPageIsSubmitted(page);
	        if (submitted) {
	            // check to see if the contributor is an editor, in which case we
	            // let them edit it
	            if (checkHasEditingPermissionForNode(contr, page.getMappedNode().getNodeId())) {
	                submitted = false;
	            }
	        }
	        return submitted;
    	} else {
    		return false;
    	}
    }
    
    public boolean checkPageIsSubmitted(MappedPage page) {
    	return page.getMappedNode().getIsSubmitted();
    }
    private Contributor checkLockForObject(Contributor contr, Object editedObject) {
        Long editHistoryId;
		try {
			editHistoryId = (Long) Ognl.getValue("editHistoryId", editedObject);
		} catch (OgnlException e) {
			e.printStackTrace();
			return null;
		}
        EditHistory history = getEditHistoryDAO().getEditHistoryWithId(editHistoryId);
        Long checkoutContrId = history.getLockedContributorId();
        Contributor checkedOutContributor = null;
        if (isLockValid(history.getLockedDate())) {
            if (checkoutContrId != null) {
                if (checkoutContrId.intValue() != contr.getId()) {
                    // if it's been checked out to someone else less than
                    // an hr ago, then it is considered locked
                    checkedOutContributor = getContributorDAO().getContributorWithId(checkoutContrId.toString());
                }
            }
        }
        return checkedOutContributor;
    }
    public Contributor checkAccessoryPageIsLocked(Contributor contr, MappedAccessoryPage page) {
    	return checkLockForObject(contr, page);
    }
    /**
     * Check to see if the page is submitted and if the contributor
     * that is passed-in can edit it.  Returns true if submitted
     * and can't be edited.  False otherwise
     */
    public boolean checkAccessoryPageIsSubmitted(Contributor contr, MappedAccessoryPage page) {
    	if (contr != null && page != null) {
	    	boolean submitted = page.getIsSubmitted();
	    	boolean submittedToTeacher = page.getIsSubmittedToTeacher();
	    	if (submitted) {
	    		// if it's really submitted, only an editor can edit
	    		return contr.getIsLearningEditor();
	    	} else if (submittedToTeacher) {
	    		return getIsTeacherForTreehouse(contr, page);
	    	}
    	}
    	return false;
    }
    public String getLockedMessageForEditHistory(Long editHistoryId) {
        EditHistory history = getEditHistoryDAO().getEditHistoryWithId(editHistoryId);
        Contributor contr = getContributorDAO().getContributorWithId(history.getLockedContributorId().toString());
        String contributorEmail = "<a href=\"mailto:" + contr.getEmail() + "\">" + contr.getDisplayName() + "</a>";
        return "This page has been checked out by " + contributorEmail + " on " + StringUtils.getGMTDateString(history.getLockedDate()) + ".";
    }
    public boolean checkHasEditingPermissionForNode(Contributor contributor, Long nodeId) {
		if (contributor != null) {
		    Long editingRootNodeId = contributor.getEditingRootNodeId();
		    boolean isAncestor = getNodeDAO().getNodeIsAncestor(nodeId, editingRootNodeId); 
		    return isAncestor;
		} else {
		    return false;
		}
    }

    public List getContributorIdsWithPermissionsForAncestorNodes(Long nodeId) {
        String sql = "select ancestor_id from NODEANCESTORS where node_id=" + nodeId;
        List contributorIds = new ArrayList();
        try {
            Session session = getSession();
            Statement selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = selectStatement.executeQuery(sql);
            List nodeIds = new ArrayList();
            while(results.next()) {
                nodeIds.add(Integer.valueOf(results.getInt(1)));
            }
            if (nodeIds.size() > 0) {
	            sql = "select C.contributor_id from Contributors C, Contributors_To_Nodes CTN where C.contributor_id=CTN.contributor_id and CTN.node_id in (" +
	            	StringUtils.returnCommaJoinedString(nodeIds) + ") and CTN.attachmentType=" + Contributor.NODE_ATTACHMENT_PERMISSION;
	            session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	            results = selectStatement.executeQuery(sql);
	            while (results.next()) {
	                contributorIds.add(Integer.valueOf(results.getInt(1)));
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contributorIds;
        //return getContributorDAO().getContributorsWithIds(contributorIds, false);
    }
    
    public boolean isEditor(Contributor contr) {
        return contr.getEditingRootNodeId() != null && contr.getEditingRootNodeId().intValue() > 0;
    }
    
    public boolean isLearningEditor(Contributor contr) {
        return contr.getIsLearningEditor();
    }
    
    public void removeAllNodeAttachmentsForContributor(Contributor contr) {
    	contr.setPermissions(new Vector());
    	updateNodeAttachmentsForContributor(contr, false);
    }
    
    public void updateNodeAttachmentsForContributor(Contributor contr, int attachmentType, Collection<Permission> permissions) {
        Session session = getSession();        
        try {
            String sql = "delete from Contributors_To_Nodes where contributor_id=" + contr.getId() + " and attachmentType=" + attachmentType;
            Statement statement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            statement.executeUpdate(sql);
            PreparedStatement insertStatement = session.connection().prepareStatement("insert into Contributors_To_Nodes (contributor_id, node_id, attachmentType) values(?,?,?)");
            insertStatement.setInt(1, contr.getId());
            for (Iterator iter = permissions.iterator(); iter.hasNext();) {
                Permission permission = (Permission) iter.next();
                insertStatement.setInt(2, permission.getNodeId());
                insertStatement.setInt(3, attachmentType);
                insertStatement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        	
    }
    public void updateNodeAttachmentsForContributor(Contributor contr, boolean hasPermissions) {
        int attachmentType = getAttachmentTypeFromPermissions(hasPermissions);
        updateNodeAttachmentsForContributor(contr, attachmentType, contr.getPermissions());
    }
    
    private int getAttachmentTypeFromPermissions(boolean hasPermissions) {
    	return hasPermissions ? Contributor.NODE_ATTACHMENT_PERMISSION : Contributor.NODE_ATTACHMENT_INTEREST;
    }
    
    public void addNodeAttachmentForContributor(int contrId, Long nodeId, boolean hasPermissions) {
    	String sql = "insert into Contributors_To_Nodes (contributor_id, node_id, attachmentType) values (" + contrId + ", " + nodeId + ", " + 
    		getAttachmentTypeFromPermissions(hasPermissions) + ")";
    	executeRawSQLUpdate(sql);    	
    }
    
    public void addNodeAttachmentForContributor(int contrId, Long nodeId) {
    	addNodeAttachmentForContributor(contrId, nodeId, false);
    }
    
    public void removeNodeAttachmentForContributor(int contrId, Long nodeId) {
    	String sql = "delete from Contributors_To_Nodes where contributor_id=" + contrId + " and node_id=" + nodeId;
    	executeRawSQLUpdate(sql);
    }
    public Object checkIsAttachedToNode(int contrId, Long nodeId, boolean hasPermissions) {
    	List<Long> attachments = getNodeAttachmentsForContributorId(contrId, hasPermissions);
    	// check to see if one of their nodes equals the node id
    	for (Long attachmentId : attachments) {
			if (nodeId.equals(attachmentId)) {
				return true;
			}
		}
    	Collection<Long> ancestorIds = getNodeDAO().getOnlyAncestorIds(nodeId, attachments);
    	if (ancestorIds.size() > 0) {
    		return getNodeDAO().getNameForNodeWithId(ancestorIds.iterator().next());
    	} else {
    		return false;
    	}
    }
    public void updatePermissionsForContributor(Contributor contr) {
    	updateNodeAttachmentsForContributor(contr, true);
    }
    
    public List getPermissionsForContributor(Contributor contr) {
    	return getNodeAttachmentsForContributor(contr, true);
    }
    
    
    public List getNodeNamesContributorAttachedTo(Integer contrId, boolean checkPermission) {
    	List names = doAttachmentsFetch(contrId, getAttachmentTypeFromPermissions(checkPermission), false);
    	Collections.sort(names);
    	return names;
    }
    
    private List doAttachmentsFetch(Integer contrId, int attachmentType, boolean selectId) {
        List returnObjs = new ArrayList();
        Session session = getSession();
        String sql;
        if (selectId) {
	        sql = "select node_id from Contributors_To_Nodes where contributor_id=" + contrId 
	        	+ " and attachmentType=" + attachmentType;
        } else {
        	sql = "select N.node_Name from NODES N, Contributors_To_Nodes CTN where contributor_id=" + contrId
        		+ " and attachmentType=" + attachmentType + " and N.node_id=CTN.node_id";
        }
        try {
            Statement selectStatement = session.connection().createStatement(
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet results = selectStatement.executeQuery(sql);
            while (results.next()) {
            	if (selectId) {
            		returnObjs.add(Long.valueOf(results.getInt(1)));
            	} else {
            		returnObjs.add(results.getString(1));
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnObjs;    	
    }
    
    public List<Long> getNodeAttachmentsForContributor(Contributor contr, boolean checkEditingPermissions) {
    	return getNodeAttachmentsForContributorId(contr.getId(), checkEditingPermissions);
    }
    public List<Long> getNodeAttachmentsForContributor(Contributor contr, int attachmentType) {
    	return getNodeAttachmentsForContributorId(contr.getId(), attachmentType);
    }
    private List<Long> getNodeAttachmentsForContributorId(int contrId, boolean checkEditingPermissions) {
    	return getNodeAttachmentsForContributorId(contrId, getAttachmentTypeFromPermissions(checkEditingPermissions));
    }
    private List<Long> getNodeAttachmentsForContributorId(int contrId, int attachmentType) {
    	return doAttachmentsFetch(contrId, attachmentType, true);    	
    }
    //private List getNodeNamesForContributor()
    
    public PasswordUtils getPasswordUtils() {
        return passwordUtils;
    }
    public void setPasswordUtils(PasswordUtils passwordUtils) {
        this.passwordUtils = passwordUtils;
    }
    
    public Document returnNoContributorDocument() {
        return returnFailureDocument(XMLConstants.NOID);
    }
    
    public Document returnWrongPasswordDocument() {
        return returnFailureDocument(XMLConstants.WRONG_PASSWORD);
    }
    
    public Document returnNoPermissionsDocument() {
        return returnFailureDocument(XMLConstants.PERMISSIONS);
    }
    
    private Document returnFailureDocument(String attributeName) {
        Element failureElement = new Element(XMLConstants.FAILURE);
        if (attributeName.equals(XMLConstants.NOID)) {
            failureElement.setAttribute(XMLConstants.NOID, XMLConstants.ONE);
        } else if (attributeName.equals(XMLConstants.WRONG_PASSWORD)){
            failureElement.setAttribute(XMLConstants.WRONG_PASSWORD, XMLConstants.ONE);
        } else {
            failureElement.setAttribute(XMLConstants.PERMISSIONS, XMLConstants.ZERO);
        }
        Document failureDoc = new Document(failureElement);
        return failureDoc;
    }
    public ContributorDAO getContributorDAO() {
        return contributorDAO;
    }
    public void setContributorDAO(ContributorDAO contributorDAO) {
        this.contributorDAO = contributorDAO;
    }

    /* (non-Javadoc)
     * @see org.tolweb.dao.ContributorDAO#getScientificEditorsForNodeWithId(java.lang.Long)
     */
    public List getScientificEditorsForNodes(Set nodeIds) {
        return getHibernateTemplate().find("from org.tolweb.treegrow.main.Contributor c where c.editingRootNodeId in (" + StringUtils.returnCommaJoinedString(nodeIds) + ")");
    } 
    
    public boolean checkEditingPermissionForPage(Contributor contr, MappedAccessoryPage page) {
        if (page != null) {
            if (page.getContributorId() == contr.getId() || (contr.getEditingRootNodeId() != null && contr.getEditingRootNodeId().intValue() > 0)) {
                return true;
            } else {
                for (Iterator iter = page.getContributors().iterator(); iter.hasNext();) {
                    AccessoryPageContributor pageContr  = (AccessoryPageContributor) iter.next();
                    if (pageContr.getContributorId() == contr.getId()) {
                        return true;
                    }
                }
                // not the author, maybe a teacher?
                return getIsTeacherForTreehouse(contr, page);
            }
        } 
        return false;
    }
    
    public boolean getIsTeacherForTreehouse(Contributor contr, MappedAccessoryPage treehouse) {
        for (Iterator iter = treehouse.getContributors().iterator(); iter.hasNext();) {
            AccessoryPageContributor pageContr  = (AccessoryPageContributor) iter.next();
            if (Student.class.isInstance(pageContr.getContributor())) {
                // Any teacher can automatically edit their student's treehouses
                Student student = (Student) pageContr.getContributor();
                if (student.getTeacher().getId() == contr.getId()) {
                    return true;
                }
            }
        }   
        return false;
    }
    
    public boolean checkEditingPermissionForSubmittedPage(Contributor contr, MappedAccessoryPage page) {
        if (contr == null || page == null) {
            return false;
        }
        if (contr.getEditingRootNodeId() != null && contr.getEditingRootNodeId().intValue() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean checkUsePermissionForImageOnPage(Contributor contributor, NodeImage img, Long accPageId, boolean isBranchOrLeaf) {
        if (contributor == null || img == null) {
            return false;
        }
        MultiContributorObject page;
        if (isBranchOrLeaf) {
            page = getPageDAO().getPageWithId(accPageId);
        } else {
            page = getAccessoryPageDAO().getAccessoryPageWithId(accPageId);
        }

        // If the user is a scientific editor, an image editor, or they contributed the image, they can use it
        if ((contributor.getEditingRootNodeId() != null && contributor.getEditingRootNodeId().intValue() > 0) 
        		|| contributor.getIsImageEditor() || isResponsibleForImage(contributor.getId(), img)) {
            return true;
        } else if (page != null) {
        	SortedSet contributors = page.getContributors();        	
            // Check to see if any of the authors on the page are responsible for the image
            for (Iterator iter = contributors.iterator(); iter.hasNext();) {
                PageContributor pageContr = (PageContributor) iter.next();
                if (isResponsibleForImage(pageContr.getContributorId(), img)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean checkEditingPermissionForImage(Contributor contributor, NodeImage image) {
        if (contributor == null || image == null) {
            return false;
        } else {
            if (contributor.getIsImageEditor()) {
                return true;
            } else {
                boolean checkedOut = image.getCheckedOut();
                boolean validLock = isLockValid(image.getCheckoutDate());
                boolean lockOwner = image.getCheckedOutContributor() != null && 
                	image.getCheckedOutContributor().getId() == contributor.getId();
                // A person can edit an image if they have permission and it's not checked out to someone else
                return isResponsibleForImage(contributor.getId(), image) && (!checkedOut || !validLock || lockOwner);
            }
        }
    }
    
    public boolean checkHasEditingPermissionForContributor(Contributor editingContributor, Contributor editedContributor) {
    	boolean returnValue = false;
    	if (editingContributor.getId() == editedContributor.getId()) {
        	// same contr    		
    		returnValue = true;
    	} else if (editingContributor.getEditingRootNodeId() != null && editingContributor.getEditingRootNodeId().intValue() > 0) {
    		// ToL editor
    		returnValue = true;
    	} else {
    		// check the editing permissions for the editedContributor
    		for (Iterator iter = editedContributor.getContributorPermissions().iterator(); iter.hasNext();) {
				ContributorPermission nextPermission = (ContributorPermission) iter.next();
				if (nextPermission.getContributorId() == editingContributor.getId()) {
					returnValue = true;
					break;
				}
			}
    	}
    	return returnValue;
    }
    
    public Collection getContributorIdsAttachedToNodeIds(Collection nodeIds, boolean hasPermission) {
    	int attachmentType = getAttachmentTypeFromPermissions(hasPermission);
    	long currentTime = System.currentTimeMillis();
     	String nodeIdString = StringUtils.returnSqlCollectionString(nodeIds);
     	System.out.println(" building id string took: " + (System.currentTimeMillis() - currentTime));
    	String queryString = "select distinct contributor_id from Contributors_To_Nodes where node_id " + nodeIdString
    		+ " and attachmentType=" + attachmentType;
    	return executeRawSQLSelectForIntegers(queryString);
    }
    
    private boolean isLockValid(Date lockDate) {
        if (lockDate != null) {
            GregorianCalendar dummyCalendar = new GregorianCalendar();
            dummyCalendar.setTime(lockDate);
            Date checkDate = dummyCalendar.getTime();
	        GregorianCalendar calendar = new GregorianCalendar();
	        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
	        Date oneHourAgo = calendar.getTime();
	        if (checkDate.after(oneHourAgo)) {
	            return true;
	        } else {
	            return false;
	        }
        } else {
            return false;
        }
    }
    
    private boolean isResponsibleForImage(int contributorId, NodeImage img) {
        Contributor imgContributor = img.getContributor();
        Contributor copyContributor = img.getCopyrightOwnerContributor();
        boolean isContributor = imgContributor != null && imgContributor.getId() == contributorId;
        boolean isCopyContributor = copyContributor != null && copyContributor.getId() == contributorId;
        boolean isEditor = false;
        for (Iterator iter = img.getPermissionsSet().iterator(); iter.hasNext();) {
            ImagePermission permission = (ImagePermission) iter.next();
            if (permission.getContributor().getId() == contributorId) {
                isEditor = true;
            }
        }
        return isContributor || isCopyContributor || isEditor;
    }
    
    /**
     * @return Returns the accessoryPageDAO.
     */
    public AccessoryPageDAO getAccessoryPageDAO() {
        return accessoryPageDAO;
    }
    /**
     * @param accessoryPageDAO The accessoryPageDAO to set.
     */
    public void setAccessoryPageDAO(AccessoryPageDAO accessoryPageDAO) {
        this.accessoryPageDAO = accessoryPageDAO;
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
}
