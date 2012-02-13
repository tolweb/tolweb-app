/*
 * ContributorDAO.java
 *
 * Created on April 28, 2004, 10:52 AM
 */

package org.tolweb.dao;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.Student;
import org.tolweb.treegrow.main.Contributor;

/**
 *
 * @author  dmandel
 */
public interface ContributorDAO extends BaseDAO {
	public static final String FIRSTNAME = "firstname";
	public static final String SURNAME = "surname";
	public static final String NAME = "name";
	public static final String EMAIL = "email";
	public static final String INSTITUTION = "institution";
	public static final String ADDRESS = "address";
	public static final String ALIAS = "alias";
    public static final String TYPE = "type";
    public static final String CHECKUNAPPROVEDTYPE = "unapproved";
    public static final String TEACHER = "teacher";
    public static final String ID = "id";
    public static final String CONTRIBUTOR = "contributor";
	
    public List getAllContributors();
    public void addContributor(Contributor contr);
    public void addContributor(Contributor contr, EditHistoryDAO historyDAO);
    public void addContributor(Contributor contr, Contributor creatingContributor, EditHistoryDAO historyDAO);
    public Contributor getContributorWithEmail(String value);
    public Contributor getContributorWithEmail(String value, byte type);
    public Contributor getContributorWithEmail(String value, Collection<Byte> type);
    public void saveContributor(Contributor contr);
    public void saveContributor(Contributor contr, EditHistoryDAO historyDAO, Contributor editingContr);
    public List findContributors(Hashtable params);
    public Contributor getContributorWithId(String value);
    public Contributor getContributorWithId(Number value);    
    public List getContributorsWithIds(Collection ids, boolean onlyWithNames);
    public List getContributorIdsNamesInstitutionsWithIds(Collection ids);
    public List getNewTreehouseContributors();
    public List getNewCoreContributors();
    public List getNewGeneralScientificContributors();
    public List getAllNewScientificContributors();
    public void deleteContributor(Contributor contr, PermissionChecker checker);
    public List getNodeIdsForContributor(Contributor contr);
    public Student getStudentWithAlias(String alias);
    public void addProject(ClassroomProject project, EditHistoryDAO historyDAO);
    public int getNumProjectsForContributor(Contributor teacher);
    public List getProjectsForContributor(Contributor teacher);
    public ClassroomProject getProjectWithPseudonym(String name);
    public List getUnapprovedProjects();
    public ClassroomProject getProjectWithId(Long id);
    public void saveProject(ClassroomProject project);
    public void deleteProject(ClassroomProject project);
    public int getNumStudentsInProject(ClassroomProject project);
    public void clearCacheForContributor(Contributor contr);
    public List getStudentIdsForProject(ClassroomProject project);
    public List getAllActiveProjects();
    public List getProjectsMatchingCriteria(Hashtable args);
    public List getContributorIdsWithLastNameOrAlias(String lastNameOrAlias);
    public List getStudentIdsWithLastNameOrAlias(String lastNameOrAlias, Contributor teacher, Long projectId);
    public void setContributorPassword(int contrId, String password);
    public String getNameForContributorWithId(int contrId);
    public List getContributorsAttachedToNodeIds(Collection nodeIds, PermissionChecker checker, 
    		boolean hasPermissions);
    public List getContributorsWithReadWritePermissionForNode(Long nodeId, PermissionChecker checker);
    /**
     * Updates the cross-reference table responsible for managing the attachment of contributors to nodes 
     * so that the orphaned contributor will be reattached to the new node-id
     * 
     * Used by the custom taxa import functionality developed in February 2008
     * @author lenards 
     * 
     * @param contrId the contributor that is being reattached
     * @param oldNodeId the node-id the contributor was attached to
     * @param newNodeId the new node-id to attach the contributor to 
     */
    public void reattachContributor(Long contrId, Long oldNodeId, Long newNodeId);
}