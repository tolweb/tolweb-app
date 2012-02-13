package org.tolweb.btol.dao;

import java.util.List;

import org.tolweb.btol.Specimen;
import org.tolweb.dao.BaseDAO;

public interface SpecimenDAO extends BaseDAO {
	public void saveSpecimens(List<Specimen> specimens, Long projectId);
	public void saveSpecimen(Specimen specimen, Long projectId);
	public Specimen getSpecimenWithId(Long id);
	public List<Specimen> getAllSpecimensInProject(Long projectId);
	public List<Specimen> getSpecimensMatchingTaxon(String taxon, Long projectId);
	public String getTaxonQueryString(String taxon);
}
