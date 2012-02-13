package org.tolweb.btol.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.tolweb.btol.NamedObject;
import org.tolweb.btol.Primer;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.treegrow.main.Contributor;

public class PrimerDAOImpl extends ProjectAssociatedDAOImpl implements PrimerDAO {
	private ContributorDAO contributorDAO;
	
	public void savePrimer(Primer value, Long projectId) {
		doProjectAssociatedSave(value, projectId, getJoinTableName());
	}
	public List getAllPrimersInProject(Long projectId) {
		String queryString = getPrimerSelectPrefix(projectId, "join primer.gene as g") + " order by g.name asc";
		List primers = getHibernateTemplate().find(queryString);
		fillOutInMemoryFieldsForAll(primers);
		return primers;
	}
	public List getPrimersForGene(NamedObject gene, Long projectId) {
		if (gene == null) {
			return getAllPrimersInProject(projectId);
		} else {
			String queryString = getPrimerSelectPrefix(projectId, "join primer.gene as g") + " and g.id=" + gene.getId();
			List primers = getHibernateTemplate().find(queryString);
			fillOutInMemoryFieldsForAll(primers);
			return primers;
		}
	}
	public Primer getPrimerWithId(Long value, Long projectId) {
		Primer primer = (Primer) getObjectWithId(Primer.class, value);
		fillOutInMemoryFields(primer);
		return primer;
	}
	public void fillOutInMemoryFieldsForAll(Collection value) {
		for (Iterator iter = value.iterator(); iter.hasNext();) {
			Primer primer = (Primer) iter.next();
			fillOutInMemoryFields(primer);
		}
	}
	public void fillOutInMemoryFields(Primer primer) {
		Integer contrId = primer.getDeveloperId();
		if (contrId != null) {
			Contributor contr = getContributorDAO().getContributorWithId(contrId);
			primer.setDeveloper(contr);
		}
		contrId = primer.getCreatedContributorId();
		if (contrId != null) {
			primer.setCreatedContributor(getContributorDAO().getContributorWithId(contrId));
		}
	}
	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}
	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
	}
	public List getPrimersWithNameOrSynonymAndNotId(String primerName, Long notId, Long projectId) {
		String queryString = getPrimerSelectPrefix(projectId, "left join primer.synonyms as syn")  + " and (primer.name=? or syn=?) ";
		if (notId != null) {
			queryString += " and primer.id!=" + notId;
		}
		return getHibernateTemplate().find(queryString, new Object[] {primerName, primerName});
	}
	public Primer getPrimerWithName(String primerName, Long projectId) {
		return doStringBasedFetch(primerName, "name", projectId);
	}
	public Primer getPrimerWithCode(String primerCode, Long projectId) {
		return doStringBasedFetch(primerCode, "code", projectId);		
	}
	public Primer getPrimerWithSynonym(String synonym, Long projectId) {
		String queryString = getPrimerSelectPrefix(projectId, "join primer.synonyms as syn") + "and syn=?";
		return doQueryStringBasedFetch(queryString, synonym);
	}
	private Primer doStringBasedFetch(String value, String column, Long projectId) {
		String queryString = getPrimerSelectPrefix(projectId) + " and primer." + column + "=?";
		return doQueryStringBasedFetch(queryString, value);
	}
	private Primer doQueryStringBasedFetch(String queryString, String value) {
		Query query = getSession().createQuery(queryString);
		query.setParameter(0, value);
		return (Primer) query.uniqueResult();		
	}
	public Primer getPrimerWithName(String primerName, boolean isForward, Long projectId) {
		String queryString = getPrimerSelectPrefix(projectId) + " and primer.name=? and primer.isForward=" + isForward;
		return (Primer) getFirstObjectFromQuery(queryString, primerName);
	}
	protected String getPrimerSelectPrefix(Long projectId) {
		return getSelectPrefix("primer", "primersSet", projectId);
	}	
	protected String getPrimerSelectPrefix(Long projectId, String additionalJoin) {
		return getSelectPrefix("primer", "primersSet", projectId, additionalJoin);
	}
	public String getForeignKeyColumnName() {
		return "primerId";
	}
	public String getJoinTableName() {
		return "PrimersToProjects";
	}
}
