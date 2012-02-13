package org.tolweb.btol.dao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.btol.SourceCollection;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.treegrow.main.StringUtils;

public class SpecimenExtractionDAOImpl extends ProjectAssociatedDAOImpl implements
		SpecimenExtractionDAO {
	private SpecimenDAO specimenDAO;
	
	public List getExtractionsMatchingCriteria(Hashtable args, Long projectId) {
		String code = (String) args.get(SpecimenExtractionDAO.COLLECTION_AND_CODE);
		if (StringUtils.notEmpty(code)) {
			List returnList = new ArrayList();
			SpecimenExtraction extractionWithCode = getExtractionWithCollectionAndCode(code, projectId);
			if (extractionWithCode != null) {
				returnList.add(extractionWithCode);
			}
			return returnList; 
		}
		String taxon = (String) args.get(SpecimenExtractionDAO.TAXON);
		if (StringUtils.notEmpty(taxon)) {
			String queryString = getExtractionSelectPrefix(projectId, "join extraction.specimen as specimen ");
			queryString += getTaxonQueryString(taxon);
			return getHibernateTemplate().find(queryString);
		}
		return null;
	}
	/**
	 * Parse out the source collection from the string e.g. DNA0001
	 * BT0002, etc.
	 * 
	 */
	public SpecimenExtraction getExtractionWithCollectionAndCode(String code, Long projectId) {
		if (StringUtils.isEmpty(code)) {
			return null;
		} else if (code.indexOf(",") > 0) {
			// comma separated, so parse out the individuals
		}
		SpecimenExtraction extraction = null;		
		Pattern specimenExtractionPattern = Pattern.compile("\\s*([^\\d]+)(\\d+)\\s*");
		Matcher matcher = specimenExtractionPattern.matcher(code);
		if (matcher.matches()) {
			String dbCode = matcher.group(1);
			String extractionCode = matcher.group(2);
			extractionCode = extractionCode.trim();
			SourceCollection collection = getSourceCollectionWithCode(dbCode);
			if (collection != null) {
				extraction = getExtractionWithCode(extractionCode, collection, projectId);
			}			
		}
		return extraction;
	}
	public List<SpecimenExtraction> getSpecimenExtractionsForSpecimen(Specimen specimen) {
		return getHibernateTemplate().find("select ex from org.tolweb.btol.SpecimenExtraction ex join ex.specimen as specimen where specimen.id=" + specimen.getId());
	}
	public SpecimenExtraction getExtractionWithCode(String code, SourceCollection collection, Long projectId) {
		String queryString = getExtractionSelectPrefix(projectId, "join extraction.sourceCollection as sourceCollection") + "and sourceCollection.id=" + collection.getId() + "and extraction.code=?";
		return (SpecimenExtraction) getFirstObjectFromQuery(queryString, code);
	}
	public List<SpecimenExtraction> getAllExtractionsInProject(Long projectId) {
		String queryString = getExtractionSelectPrefix(projectId);
		return getHibernateTemplate().find(queryString);
	}
	public void saveExtraction(SpecimenExtraction value, Long projectId) {
		doProjectAssociatedSave(value, projectId, getJoinTableName());
	}
	public void saveExtractions(List<SpecimenExtraction> extractions, Long projectId) {
		for (SpecimenExtraction extraction : extractions) {
			saveExtraction(extraction, projectId);
		}
	}
	public List<SourceCollection> getSourceCollections() {
		return getHibernateTemplate().find("from org.tolweb.btol.SourceCollection");
	}
	public SourceCollection getSourceCollectionWithCode(String code) {
		return (SourceCollection) getFirstObjectFromQuery("from org.tolweb.btol.SourceCollection where code=?", code);
	}
	public List<SpecimenExtraction> getExtractionsInCollection(SourceCollection collection, Long projectId) {
		String queryString = getExtractionSelectPrefix(projectId, "join extraction.sourceCollection as sourceCollection") + "and sourceCollection.id=" + collection.getId();
		return getHibernateTemplate().find(queryString);
	}
	protected String getExtractionSelectPrefix(Long projectId) {
		return getSelectPrefix("extraction", "extractionsSet", projectId);
	}	
	protected String getExtractionSelectPrefix(Long projectId, String additionalJoin) {
		return getSelectPrefix("extraction", "extractionsSet", projectId, additionalJoin);
	}	
	public String getForeignKeyColumnName() {
		return "extractionId";
	}
	public String getJoinTableName() {
		return "ExtractionsToProjects";
	}
	public String getTaxonQueryString(String taxon) {
		return getSpecimenDAO().getTaxonQueryString(taxon);
	
	}
	public SpecimenDAO getSpecimenDAO() {
		return specimenDAO;
	}
	public void setSpecimenDAO(SpecimenDAO specimenDAO) {
		this.specimenDAO = specimenDAO;
	}
}
