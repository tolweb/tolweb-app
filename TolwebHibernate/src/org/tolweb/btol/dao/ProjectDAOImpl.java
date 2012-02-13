package org.tolweb.btol.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.tolweb.btol.AdditionalFields;
import org.tolweb.btol.Project;
import org.tolweb.btol.ProjectContributor;
import org.tolweb.btol.WorkingGroup;
import org.tolweb.dao.BaseDAOImpl;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class ProjectDAOImpl extends BaseDAOImpl implements ProjectDAO {
	private AdditionalFieldsDAO additionalFieldsDAO;
	private ContributorDAO contributorDAO;
	public List getProjectsForContributor(Contributor contr) {
		return getHibernateTemplate().find("select p.id, p.name from org.tolweb.btol.Project p join " +
				" p.contributors as contributor where contributor.contributorId=" + contr.getId());
	}

	public AdditionalFields getAdditionalFieldsForNodeInProject(
			MappedNode node, Long projectId) {
		AdditionalFields fields = (AdditionalFields) getFirstObjectFromQuery("select f " + getFieldsQueryString() + " where p.id=" + 
				projectId + " and f.nodeId=" + node.getNodeId());
		if (fields != null) {
			getAdditionalFieldsDAO().updateInMemoryFieldsForFields(fields);
		}
		return fields;
	}

	public List getAdditionalFieldsForNodeIdsInProject(Collection nodeIds,
			Long projectId) {
		Query query = getSession().createQuery("select f " + getFieldsQueryString() + " where p.id=" +
				projectId + " and f.nodeId " + StringUtils.returnSqlCollectionString(nodeIds));
		query.setCacheable(true);
		List fields = query.list();
		getAdditionalFieldsDAO().updateInMemoryFieldsForAll(fields);
		return fields;
	}

	public void createFieldsForNodeIdsInProject(Collection nodeIds, Long projectId) {
		if (nodeIds == null || nodeIds.size() == 0) {
			return;
		}
		List<Long> nodeIdsToCreate = new ArrayList<Long>(nodeIds);
		String queryString = "select f.nodeId " + getFieldsQueryString() + 
			" where p.id=" + projectId + " and f.nodeId " +
				StringUtils.returnSqlCollectionString(nodeIds);
		List nodeIdsWithAdditionalFields = getHibernateTemplate().find(queryString);
		// remove the ids who already exist in the db
		for (Iterator iter = nodeIdsWithAdditionalFields.iterator(); iter
				.hasNext();) {
			Long nodeId = (Long) iter.next();
			nodeIdsToCreate.remove(nodeId);
		}
		List<AdditionalFields> newFieldsList = new ArrayList<AdditionalFields>();
		// and create the rest of the fields
		for (Iterator iter = nodeIdsToCreate.iterator(); iter
				.hasNext();) {
			Long nodeId = (Long) iter.next();
			AdditionalFields newFields = new AdditionalFields();
			newFields.setNodeId(nodeId);
			newFields.setTier(AdditionalFields.NO_TIER_SET);
			newFields.setMtGenomeState(AdditionalFields.MT_GENOME_NOT_NEEDED);
			newFieldsList.add(newFields);
		}
		getAdditionalFieldsDAO().saveAdditionalFields(newFieldsList);
		Project project = getProjectWithId(projectId);
		project.getAdditionalFieldsSet().addAll(newFieldsList);
		getHibernateTemplate().saveOrUpdate(project);
	}
	
	public void reattachAdditionalFields(Long additionalFieldsId, Long newNodeId, Long oldNodeId) {
		Object[] args = new Object[] {newNodeId, additionalFieldsId, oldNodeId};
		String fmt = "UPDATE AdditionalFields SET nodeId = %1$d WHERE id = %2$d and nodeId = %3$d";
		executeRawSQLUpdate(String.format(fmt, args));
	}

	
	public Project getProjectWithId(Long id) {
		Project project = (Project) getObjectWithId(Project.class, id);
		fillOutInMemoryFields(project);
		return project;
	}
	public void fillOutInMemoryFields(Project project) {
		for (Iterator iter = project.getContributors().iterator(); iter.hasNext();) {
			ProjectContributor contributor = (ProjectContributor) iter.next();
			contributor.setActualContributor(getContributorDAO().getContributorWithId(contributor.getContributorId()));			
		}
		for (Iterator iter = project.getWorkingGroups().iterator(); iter.hasNext();) {
			WorkingGroup nextGroup = (WorkingGroup) iter.next();
			List contributors = getContributorDAO().getContributorsWithIds(nextGroup.getContributorIds(), true);
			nextGroup.setContributors(new HashSet<Contributor>(contributors));
		}
	}
	public List<Integer> getContributorIdsForProject(Long projectId) {
		return getContributorIdsForProject(projectId, null);
	}
	public List<Integer> getContributorIdsForProject(Long projectId, Integer membershipStatus) {
		String queryString = "select contributor.contributorId from org.tolweb.btol.Project p join p.contributors " +
		" as contributor where p.id=" + projectId;
		if (membershipStatus != null) {
			queryString += " and contributor.membershipStatus<=" + membershipStatus;
		}
		return getHibernateTemplate().find(queryString);		
	}
	public Long getProjectIdWithDomain(String domainName) {
		return (Long) doProjectFetchFromDomainName("p.id", domainName);
	}
	public String getProjectNameWithDomain(String domainName) {
		return (String) doProjectFetchFromDomainName("p.name", domainName);
	}
	public Project getProjectWithDomain(String domainName) {
		return (Project) doProjectFetchFromDomainName("p", domainName);
	}			
	private Object doProjectFetchFromDomainName(String selectString, String domainName) {
		return getFirstObjectFromQuery("select " + selectString + " from org.tolweb.btol.Project p where p.domain=?", domainName);
	}
	public Project getBtolProject() {
		return getProjectWithId(1L);
	}
	private String getFieldsQueryString() {
		return "from org.tolweb.btol.Project as p join p.additionalFieldsSet as f";
	}
	public AdditionalFieldsDAO getAdditionalFieldsDAO() {
		return additionalFieldsDAO;
	}
	public void setAdditionalFieldsDAO(AdditionalFieldsDAO additionalFieldsDAO) {
		this.additionalFieldsDAO = additionalFieldsDAO;
	}
	public void saveProject(Project project) {
		getHibernateTemplate().saveOrUpdate(project);
	}

	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}

	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
	}
	
	public Set<Long> getNodeIdsWithRevelantTierData(Collection nodeIds) {
		Set<Long> resultNodeIds = new HashSet<Long>();
		ResultSet results;
		Session session = null;
		try {
			session = getSession();
			Statement stmt = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			String sql = "SELECT AF.nodeId FROM AdditionalFields AF " + 
						"INNER JOIN AdditionalFieldsToProjects AFTP on AF.id = AFTP.fieldsId " +
						"WHERE AFTP.projectId = 1 AND AF.tier >= 0 AND AF.nodeId " + 
						StringUtils.returnSqlCollectionString(nodeIds);
			results = stmt.executeQuery(sql);
			while(results.next()) {
				resultNodeIds.add(Long.valueOf(results.getLong(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultNodeIds;
	}
}
