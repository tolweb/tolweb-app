package org.tolweb.btol.dao;

import java.util.Hashtable;
import java.util.List;

import org.tolweb.btol.SourceCollection;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.dao.BaseDAO;

public interface SpecimenExtractionDAO extends BaseDAO {
	public static final int COLLECTION_AND_CODE = 0;
	public static final int TAXON = 1;
	
	public List getExtractionsMatchingCriteria(Hashtable args, Long projectId);
	public SpecimenExtraction getExtractionWithCollectionAndCode(String code, Long projectId);
	public SpecimenExtraction getExtractionWithCode(String code, SourceCollection collection, Long projectId);
	public List<SpecimenExtraction> getAllExtractionsInProject(Long projectId);
	public List<SpecimenExtraction> getExtractionsInCollection(SourceCollection collection, Long projectId);
	public void saveExtraction(SpecimenExtraction extraction, Long projectId);
	public void saveExtractions(List<SpecimenExtraction> extractions, Long projectId);
	public List<SourceCollection> getSourceCollections();
	public SourceCollection getSourceCollectionWithCode(String code);
	
	public List<SpecimenExtraction> getSpecimenExtractionsForSpecimen(Specimen specimen);
	
	public String getTaxonQueryString(String taxon);
}
