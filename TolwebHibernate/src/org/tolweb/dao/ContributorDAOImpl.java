/*
 * ContributorDAO.java
 *
 * Created on April 28, 2004, 9:55 AM
 */

package org.tolweb.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.Student;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 *
 * @author  dmandel
 */
public class ContributorDAOImpl extends BaseDAOImpl implements ContributorDAO {
	
    /** Creates a new instance of ContributorDAO */
    public ContributorDAOImpl() {
    }
    
    public void addContributor(Contributor contr, EditHistoryDAO historyDAO) {
        addContributor(contr, null, historyDAO);
    }
    
    public void addContributor(Contributor contr) {
    	addContributor(contr, null, null);
    }
    
    /**
     * having the history dao is a bit of a hack but having it as a class
     * member causes circular bean reference errors!
     * @param contr
     * @param creatingContributor
     * @param historyDao
     */
    public void addContributor(Contributor contr, Contributor creatingContributor, EditHistoryDAO historyDAO) {
    	getHibernateTemplate().save(contr);
		if (creatingContributor != null) {
			contr.addToContributorPermissions(creatingContributor);
		}
    	if (historyDAO != null) { 
	    	Long newHistoryId = historyDAO.createNewHistory(contr);
	    	contr.setEditHistoryId(newHistoryId);
    	}
    	saveContributor(contr);
    }
    
    public Contributor getContributorWithEmail(String value) {
        return getContributorWithEmail(value, Contributor.ANY_CONTRIBUTOR);
    }
    
    public List getAllContributors() {
    	return getHibernateTemplate().find("from org.tolweb.treegrow.main.Contributor");
    }
    
    public Contributor getContributorWithEmail(String value, byte type) {
        String queryString;
        Object[] args;
        if (type != Contributor.ANY_CONTRIBUTOR) {
            queryString = getEmailFetchString() + " and type<=?";
            args = new Object[2];
            args[0] = value;
            args[1] = Integer.valueOf(type);
        } else {
            queryString = getEmailFetchString();
            args = new Object[1];
            args[0] = value;
        }
        List list = getHibernateTemplate().find(queryString, args);
        return returnFirstItem(list);
    }
    
    public Contributor getContributorWithEmail(String value, Collection<Byte> types) {
    	String queryString = getEmailFetchString();
    	queryString += " and type " + StringUtils.returnSqlCollectionString(types);
    	List list = getHibernateTemplate().find(queryString, value);
    	return returnFirstItem(list);
    }
    
    private String getEmailFetchString() {
    	return "from Contributor c where c.email=?";
    }
    
    public Contributor getContributorWithId(Number value) {
    	if (value != null) {
    		return getContributorWithId(value.toString());
    	} else {
    		return null;
    	}
    }
    
    public Contributor getContributorWithId(String value) {
        try {
            Contributor contr = (Contributor) getHibernateTemplate().load(org.tolweb.treegrow.main.Contributor.class, Integer.valueOf(value));
            return contr;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Returns the lastName, firstName, institution, and id for every
     * contributor contained in the collection of ids
     */
    public List getContributorIdsNamesInstitutionsWithIds(Collection ids) {
    	return getHibernateTemplate().find("select lastName, firstName, institution, id from org.tolweb.treegrow.main.Contributor where id " + 
    			StringUtils.returnSqlCollectionString(ids));
    }
    
    public List getContributorsWithIds(final Collection value, final boolean onlyNames) {
    	if (value.size() == 0) {
    		return new ArrayList();
    	}
    	HibernateCallback callback = new HibernateCallback() {
    		public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria contrsCriteria = session.createCriteria(Contributor.class);
				contrsCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
				if (onlyNames) {
					contrsCriteria.addOrder(Order.asc("lastName"));
				}
				contrsCriteria.add(Restrictions.in("id", value));
				return contrsCriteria.list();    			
    		}
    	};
		List returnList = (List) getHibernateTemplate().execute(callback);
		if (onlyNames) {
			Iterator it = returnList.iterator();
			List newList = new ArrayList();
			while (it.hasNext()) {
				Contributor contr = (Contributor) it.next();
				if (StringUtils.notEmpty(contr.getName()) || StringUtils.notEmpty(contr.getInstitution())) {
					newList.add(contr);    	
				}
			}
			return newList;
		} else {
			return returnList;
		}
    }
    
    public List getNewCoreContributors() {
    	return getNewContributors(Contributor.SCIENTIFIC_CONTRIBUTOR);
    }
    
    public List getNewGeneralScientificContributors() {
    	return getNewContributors(Contributor.ACCESSORY_CONTRIBUTOR);
    }    
    
    public List getNewTreehouseContributors() {
    	return getNewContributors(Contributor.TREEHOUSE_CONTRIBUTOR);
    }
    
    public List getAllNewScientificContributors() {
    	return getNewContributors(Contributor.SCIENTIFIC_CONTRIBUTOR, Contributor.ACCESSORY_CONTRIBUTOR);
    }
    
    private List getNewContributors(byte contributorType) {
    	return getNewContributors(contributorType, ((byte) -1));
    }
    
    private List getNewContributors(byte type1, byte type2) {
    	String queryString = "from Contributor c where c.unapprovedContributorType=" + type1;
    	if (type2 > 0) {
    		queryString += " or c.unapprovedContributorType=" + type2;
    	}
    	return getHibernateTemplate().find(queryString);    	
    }
    
    private Contributor returnFirstItem(List list) {
        if (list != null && list.size() > 0) {
            return (Contributor) list.get(0);
        } else {
            return null;
        }                
    }
    
    /**
     * Finds the Contributor according to the values passed-in.
     * @param params A Hashtable of parameters to use.  Looks for keys firstName,
     * surname, email, institution, and address
     * @return A List of Contributors matching the parameters
     */
    public List findContributors(Hashtable params) {
        final String firstName = (String) params.get(ContributorDAO.FIRSTNAME);
        final String surname = (String) params.get(ContributorDAO.SURNAME);
        final String email = (String) params.get(ContributorDAO.EMAIL);
        final String institution = (String) params.get(ContributorDAO.INSTITUTION);
        final String address = (String) params.get(ContributorDAO.ADDRESS);
        final String name = (String) params.get(ContributorDAO.NAME);
        final String alias = (String) params.get(ContributorDAO.ALIAS);
        final String type = (String) params.get(ContributorDAO.TYPE);
        final Boolean checkUnapproved = (Boolean) params.get(ContributorDAO.CHECKUNAPPROVEDTYPE);
        
		HibernateCallback callback = new HibernateCallback() {
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
			     Class searchClass = Contributor.class;
			     if (StringUtils.notEmpty(alias)) {
			         searchClass = Student.class;
			     }
				Criteria contrsCriteria = session.createCriteria(searchClass);
				contrsCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
				if (StringUtils.notEmpty(name)) {
					// We want to split the name on the last space if there is one.  If there isn't one,
					// just match either firstName or lastName.  In all cases we do want institutional
				    // contributors' Institution field to be treated like a name for people contributors,
				    // so search that, too
					int lastSpace = name.lastIndexOf(" ");
					String firstNameMatch, surnameMatch;
					if (lastSpace != -1) {
						firstNameMatch = name.substring(0, lastSpace);
						surnameMatch = name.substring(lastSpace + 1);
						contrsCriteria.add(
						    Restrictions.or(
						            Restrictions.and(
						                    Restrictions.like("firstName", firstNameMatch, MatchMode.ANYWHERE),
						                    Restrictions.like("lastName", surnameMatch, MatchMode.ANYWHERE)),
						            getInstitutionNameExpression(name)));						
					} else {
						firstNameMatch = name;
						surnameMatch = name;
						contrsCriteria.add(Restrictions.disjunction()
							.add(Restrictions.like("firstName", firstNameMatch, MatchMode.ANYWHERE)) 
							.add(Restrictions.like("lastName", surnameMatch, MatchMode.ANYWHERE))
							.add(getInstitutionNameExpression(name)));						        		
					}					
				} else {
					addToQuery("firstName", firstName, contrsCriteria);
					addToQuery("lastName", surname, contrsCriteria);			
				}
				addToQuery("email", email, contrsCriteria);
				addToQuery("institution", institution, contrsCriteria);
				addToQuery("address", address, contrsCriteria);
				addToQuery("alias", alias, contrsCriteria);
                if (StringUtils.notEmpty(type)) {
                	SimpleExpression typeExpression = Restrictions.le("contributorType", Byte.valueOf(type)); 
                	if (checkUnapproved != null && checkUnapproved.booleanValue()) {
                		SimpleExpression unapprovedTypeExpression = Restrictions.le("unapprovedContributorType", Byte.valueOf(type));
                		contrsCriteria.add(Restrictions.or(typeExpression, unapprovedTypeExpression));
                	} else {
                		contrsCriteria.add(typeExpression);	
                	}
                    
                }
				return contrsCriteria.list();
			 }
		};
        
		return (List) getHibernateTemplate().execute(callback);
    }
    
    public List getNodeIdsForContributor(Contributor contr) {
        ResultSet results = null;
        Statement selectStatement = null;
        Connection conn = null;
        Session session = null;
		try {
		    session = getSession();
			selectStatement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            String sqlString = "select distinct node_id from Contributors_To_Nodes where contributor_id=" + contr.getId();
			results = selectStatement.executeQuery(sqlString);
			ArrayList nodeIds = new ArrayList();
			while (results.next()) {
			    nodeIds.add(Long.valueOf(results.getLong(1)));
			}
			return nodeIds;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.flush();
				session.close();
			}			
		}
        return new ArrayList();
    }
    
    public Student getStudentWithAlias(String alias) {
    	List list = getHibernateTemplate().find("from org.tolweb.hibernate.Student st where st.alias=?", alias);
    	if (list != null && list.size() > 0) {
    		return (Student) list.get(0);
    	} else {
    		return null;
    	}
    }
    
    /**
     * Returns a Criterion to be used that causes the Institution to be treated like
     * a name when both the firstName and lastName are null or empty
     * @param institutionName
     * @return
     */
    private Criterion getInstitutionNameExpression(String institutionName) {
        return Restrictions.and(Restrictions.like("institution", institutionName, MatchMode.ANYWHERE),
                Restrictions.and(
                        Restrictions.or(Restrictions.isNull("firstName"), Restrictions.eq("firstName", "")), 
                        Restrictions.or(Restrictions.isNull("lastName"), Restrictions.eq("lastName", ""))));
    }

    
    /**
     *  Adds stuff to the query to look for a contributor
     * @param fieldName The db fieldname to query against
     * @param fieldValue The actual value to test against
     * @param criteria The criteria object to add to
     */
    private void addToQuery(String fieldName, String fieldValue, Criteria criteria) {
        if (StringUtils.notEmpty(fieldValue)) {
			criteria.add(Restrictions.like(fieldName, fieldValue, MatchMode.ANYWHERE));
        }
    }
    
    public void saveContributor(Contributor contr) {
    	saveContributor(contr, null, null);
    }
    
    public void saveContributor(Contributor contr, EditHistoryDAO historyDAO, Contributor editingContr) {
        if (historyDAO != null) {
        	historyDAO.updateLastEdited(contr.getEditHistoryId(), editingContr);
        }
        // ensure that URLs associated with a contributor are valid
        ensureContributorUrlsValid(contr);
    	getHibernateTemplate().update(contr);    	
    }
    
    /**
     * Verifies that the URL data associated with a Contributor are properly formed. 
     * 
     * Users in the past have provided URLs that do not have the protocol prefix and, 
     * therefore, are not valid.  This ensures that the prefix is in place. 
     * 
     * At the first of implementation, there is only one URL associated with Contributor 
     * data: Contributor home page.  As the system grows, this may change, which means 
     * more clean up and verification may be added here. 
     * 
     * @param contr contributor to verify URL data for
     */
    private void ensureContributorUrlsValid(Contributor contr) {
    	if (contr != null) {
    		if (StringUtils.notEmpty(contr.getHomepage()) && !hasHttpPrefix(contr.getHomepage())) {
    			contr.setHomepage(HTTP_PREFIX + contr.getHomepage());
    		}
    	}
	}
    
	public void deleteContributor(Contributor contr, PermissionChecker checker) {
        // also need to delete node attachments, since those aren't managed by
        // hibernate
        if (checker != null) {
        	checker.removeAllNodeAttachmentsForContributor(contr);
        }
    	getHibernateTemplate().delete(contr);
    }
    
    public void addProject(ClassroomProject project, EditHistoryDAO historyDAO) {
    	getHibernateTemplate().save(project);    	
    	for (Iterator iter = project.getStudents().iterator(); iter.hasNext();) {
			Student nextStudent = (Student) iter.next();
			nextStudent.setEditHistoryId(historyDAO.createNewHistory(nextStudent));
			addContributor(nextStudent, project.getTeacher(), historyDAO);
		}
    }
    
    public void deleteProject(ClassroomProject project) {
        getHibernateTemplate().delete(project);
    }
    
    public List getProjectsForContributor(Contributor teacher) {
    	return doProjectFetch(teacher, "select distinct project");
    }
    
    public int getNumProjectsForContributor(Contributor teacher) {
        List results = doProjectFetch(teacher, "select count(*)");
        if (results != null && results.size() > 0) {
            return ((Number) results.get(0)).intValue();
        } else {
            return 0;
        }
    }
    
    private List doProjectFetch(Contributor teacher, String selectString) {
        return getHibernateTemplate().find(selectString + " from org.tolweb.hibernate.ClassroomProject project left join project.additionalEditors as editor where project.teacher.id=" + teacher.getId() + " or editor.id=" + teacher.getId());        
    }
    
    public ClassroomProject getProjectWithPseudonym(String pseudonym) {
        List results = getHibernateTemplate().find("from org.tolweb.hibernate.ClassroomProject project where project.pseudonym=?", pseudonym);
        if (results != null && results.size() > 0) {
            return (ClassroomProject) results.get(0);
        } else {
            return null;
        }
    }
    
    public List getUnapprovedProjects() {
        return getHibernateTemplate().find("from org.tolweb.hibernate.ClassroomProject project where project.isApproved = 0");
    }
    
    public void saveProject(ClassroomProject project) {
        getHibernateTemplate().update(project);
    }
    
    public void clearCacheForContributor(Contributor contr) {
	    try {
	        SessionFactory factory = getSessionFactory();
	        int id = contr.getId();
	        factory.evict(Contributor.class, Integer.valueOf(id));
	    } catch (Exception e) {
	        logger.info("Problem saving contributor", e);
	    }
    }

    public ClassroomProject getProjectWithId(Long id) {
        List results = getHibernateTemplate().find("from org.tolweb.hibernate.ClassroomProject project where project.projectId=" + id);
        if (results != null && results.size() > 0) {
            return (ClassroomProject) results.get(0);
        } else {
            return null;
        }
    }
    
    public List getAllActiveProjects() {
        return getHibernateTemplate().find("from org.tolweb.hibernate.ClassroomProject where isApproved=1 and isClosed=0");
    }

    public int getNumStudentsInProject(ClassroomProject project) {
        List results = getHibernateTemplate().find("select count(*) from org.tolweb.hibernate.Student student where student.project.id=" + project.getProjectId());
        if (results != null && results.size() > 0) {
            return ((Number) results.get(0)).intValue();
        } else {
            return 0;
        }
    }
    
    public List getStudentIdsForProject(ClassroomProject project) {
        return getHibernateTemplate().find("select c.id from org.tolweb.treegrow.main.Contributor c where c.project.id=" + project.getProjectId());
    }
    
    public List getProjectsMatchingCriteria(Hashtable args) {
    	final String name = (String) args.get(ContributorDAO.NAME);
    	final Long id = (Long) args.get(ContributorDAO.ID);
    	final Contributor contr = (Contributor) args.get(ContributorDAO.CONTRIBUTOR);
		HibernateCallback callback = new HibernateCallback() {
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
				 Criteria criteria = session.createCriteria(ClassroomProject.class);
				 if (name != null) {
					 criteria.add(Restrictions.like("name", "%" + name + "%"));
				 }
				 if (id != null) {
					 criteria.add(Restrictions.eq("projectId", id));
				 }
				 if (contr != null) {
					 criteria.add(getCriteriaForTeacher(contr));
				 }
				 List results = criteria.list(); 
				 return results;
			 }
		};
    	return (List) getHibernateTemplate().execute(callback);
    }
    
    public List getContributorIdsWithLastNameOrAlias(String lastNameOrAlias) {
    	return getContributorIdsWithLastNameOrAlias(Contributor.class, lastNameOrAlias, null, null);
    }
    
    public List getStudentIdsWithLastNameOrAlias(final String lastNameOrAlias, final Contributor teacher, 
    		final Long projectId) {
    	return getContributorIdsWithLastNameOrAlias(Student.class, lastNameOrAlias, teacher, projectId);
    }
    
    private List getContributorIdsWithLastNameOrAlias(final Class contributorClass, final String lastNameOrAlias,
    		final Contributor teacher, final Long projectId) {
    	final boolean hasName = StringUtils.notEmpty(lastNameOrAlias);
    	final boolean hasProjectId = projectId != null;
    	final boolean hasTeacher = teacher != null;
    	if (hasName || hasProjectId || hasTeacher) {
	    	HibernateCallback callback = new HibernateCallback() {
	    		public Object doInHibernate(Session session) throws HibernateException, SQLException {
	    			Criteria criteria = session.createCriteria(contributorClass);
	    			if (hasName) {
	    				criteria.add(getCriteriaForLastNameOrAlias(lastNameOrAlias));
	    			}
	    			if (hasTeacher) {
	    				criteria.add(getCriteriaForTeacher(teacher));
	    			}
	    			if (hasProjectId) {
	    				criteria.createCriteria("project").add(Restrictions.eq("projectId", projectId));
	    			}
	    			criteria.setProjection(Projections.property("id"));
	    			List results = criteria.list();
	    			return results;
	    		}
	    	};
	    	return (List) getHibernateTemplate().execute(callback);
    	} else {
    		return new ArrayList();
    	}    	
    }
    
    public void setContributorPassword(int contrId, String password) {
    	executeUpdateQuery("update org.tolweb.treegrow.main.Contributor set password='" + password+ "' where id=" + contrId);
    }
    
    private SimpleExpression getCriteriaForTeacher(Contributor teacher) {
    	return Restrictions.eq("teacher", teacher);
    }
    
    private LogicalExpression getCriteriaForLastNameOrAlias(String name) {
    	name = "%" + name + "%";
    	return Restrictions.or(Restrictions.like("lastName", name), Restrictions.like("alias", name));
    }
    
    public String getNameForContributorWithId(int contrId) {
    	String nameString = "";
    	List results = getHibernateTemplate().find("select firstName, lastName from org.tolweb.treegrow.main.Contributor where id=" + contrId);
    	if (results.size() > 0) {
    		Object[] firstResult = (Object[]) results.get(0);
    		nameString = firstResult[0] + " " + firstResult[1];
    	}
    	return nameString;
    }
    
    public List getContributorsAttachedToNodeIds(Collection nodeIds, PermissionChecker checker, 
    		boolean hasPermissions) {
    	Collection contributorIds = checker.getContributorIdsAttachedToNodeIds(nodeIds, hasPermissions);
    	if (contributorIds.size() > 0) {
    		return getContributorsWithIds(contributorIds, true);
    	} else {
    		return new ArrayList();
    	}
    }
    public List getContributorsWithReadWritePermissionForNode(Long nodeId, PermissionChecker checker) {
		List contrIds = checker.getContributorIdsWithPermissionsForAncestorNodes(nodeId);
		return getContributorsWithIds(contrIds, true);
    }
    
    public void reattachContributor(Long contrId, Long oldNodeId, Long newNodeId) {
		Object[] args = new Object[] {newNodeId, contrId, oldNodeId};
		String fmt = "UPDATE Contributors_To_Nodes SET node_id = %1$d WHERE contributor_id = %2$d AND node_id = %3$d";
		executeRawSQLUpdate(String.format(fmt, args));    	
    }
}
