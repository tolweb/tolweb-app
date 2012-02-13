package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.Gene;
import org.tolweb.btol.NamedObject;

public class GeneDAOImpl extends ProjectAssociatedDAOImpl implements GeneDAO {
	public void saveGene(NamedObject gene, Long projectId) {
		doProjectAssociatedSave(gene, projectId, getJoinTableName());
	}
	public List getAllGenesInProject(Long projectId) {
		return getAllGenesInProject(projectId, false);
	}
	public Gene getGeneWithId(Long id, Long projectId) {
		return (Gene) getFirstObjectFromQuery(getGeneSelectPrefix(projectId) + " and gene.id=" + id);
	}
	public Gene getGeneWithName(String name, Long projectId) {
		return (Gene) getFirstObjectFromQuery(getGeneSelectPrefix(projectId) + " and gene.name=?", name);
	}
	protected String getGeneSelectPrefix(Long projectId) {
		return getSelectPrefix("gene", "genesSet", projectId);
	}
	public String getJoinTableName() {
		return "GenesToProjects";
	}
	public String getForeignKeyColumnName() {
		return "geneId";
	}
	public List getAllGenesInProject(Long projectId, boolean showHidden) {
		String hiddenRestriction = "";
		String orderByHql = " order by ";
		if (showHidden) {
			orderByHql += " gene.hidden desc, ";
		} else {
			hiddenRestriction = " and gene.hidden=0";
		}
		orderByHql += " gene.name asc ";		
		return getHibernateTemplate().find(getGeneSelectPrefix(projectId) + hiddenRestriction + orderByHql);
	}
}
