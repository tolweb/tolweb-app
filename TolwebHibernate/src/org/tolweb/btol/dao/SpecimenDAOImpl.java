package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.Specimen;

public class SpecimenDAOImpl extends ProjectAssociatedDAOImpl implements SpecimenDAO {

	public Specimen getSpecimenWithId(Long id) {
		return (Specimen) getObjectWithId(Specimen.class, id);
	}
	public void saveSpecimens(List<Specimen> specimens) {
		getHibernateTemplate().saveOrUpdateAll(specimens);
	}
	public String getForeignKeyColumnName() {
		return "specimenId";
	}
	public String getJoinTableName() {
		return "SpecimensToProjects";
	}	
	public void saveSpecimen(Specimen value, Long projectId) {
		doProjectAssociatedSave(value, projectId, getJoinTableName());
	}
	public void saveSpecimens(List<Specimen> specimens, Long projectId) {
		for (Specimen extraction : specimens) {
			saveSpecimen(extraction, projectId);
		}
	}
	public List<Specimen> getAllSpecimensInProject(Long projectId) {
		return getHibernateTemplate().find(getSpecimenSelectPrefix(projectId));
	}
	public List<Specimen> getSpecimensMatchingTaxon(String taxon, Long projectId) {
		String queryString = getSpecimenSelectPrefix(projectId) + getTaxonQueryString(taxon);
		return getHibernateTemplate().find(queryString);
	}
	public String getTaxonQueryString(String taxon) {
		// the taxon field can match any one of the individual ranks
		taxon = "'%" + taxon + "%'";
		return "and (specimen.family like " + taxon + " or specimen.genus like " + taxon + 
			" or specimen.species like " + taxon + " or specimen.subfamily like " + taxon + 
			" or specimen.tribe like " + taxon + ")";
	}
	protected String getSpecimenSelectPrefix(Long projectId) {
		return getSelectPrefix("specimen", "specimensSet", projectId);
	}	
}
