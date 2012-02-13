package org.tolweb.btol.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tolweb.btol.Gene;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.dao.ChromatogramBatchDAO;
import org.tolweb.btol.dao.GeneDAO;
import org.tolweb.btol.dao.PCRBatchDAO;
import org.tolweb.btol.dao.SpecimenExtractionDAO;
import org.tolweb.treegrow.main.StringUtils;

/**
 * chromatogram searching class that coordinates multiple database queries
 * and returns the results as a list of filenames
 * @author dmandel
 *
 */
public class ChromatogramSearcher {
	private SpecimenExtractionDAO specimenExtractionDAO;
	private GeneDAO geneDAO;
	private ChromatogramBatchDAO chromatogramBatchDAO;
	private PCRBatchDAO pcrBatchDAO;
	
	public Collection<String> getChromatogramFilenames(String extractionName, String geneName, 
			String batchName, String taxon, Long projectId) {
		String commaRegex = ",\\s*";
		SpecimenExtraction extraction = null;
		Collection<Long> extractionIds = new ArrayList<Long>();		
		if (StringUtils.notEmpty(extractionName)) {
			String[] pieces = extractionName.split(commaRegex);
			for (String exName : pieces) {
				extraction = getSpecimenExtractionDAO().getExtractionWithCollectionAndCode(exName, projectId);
				if (extraction != null) {
					extractionIds.add(extraction.getId());
				}
			}
			if (extractionIds.size() == 0) {
				outputExtractionError(extractionName);
			}
		}
		Gene gene = null;
		Collection<Long> geneIds = new ArrayList<Long>();		
		if (StringUtils.notEmpty(geneName)) {
			String[] pieces = geneName.split(commaRegex);
			for (String string : pieces) {
				gene = getGeneDAO().getGeneWithName(string, projectId);
				if (gene != null) {
					geneIds.add(gene.getId());
				}
			}
			if (geneIds.size() == 0) {
				outputGeneError(geneName);
			}
		}
		Collection<String> filenamesInBatch = new ArrayList<String>();
		boolean hasBatch = StringUtils.notEmpty(batchName);
		if (hasBatch) {
			filenamesInBatch = getChromatogramBatchDAO().getChromatogramFilenamesInBatchWithName(batchName);
			if (filenamesInBatch.size() == 0) {
				outputBatchError(batchName);
			}
		}
		List<String> returnVals = new ArrayList<String>();
		boolean hasPcrSearchCriteria = StringUtils.notEmpty(extractionName) || StringUtils.notEmpty(taxon) ||
			StringUtils.notEmpty(geneName);
		if (hasPcrSearchCriteria) {
			returnVals = getPcrBatchDAO().getChromatogramFilenamesForExtractionGeneAndTaxon(extractionIds, 
					geneIds, taxon, projectId);
		}
		// make two sets with query results and return their intersection
		Set<String> chroBatchResults = new HashSet<String>(filenamesInBatch);
		Set<String> pcrBatchResults = new HashSet<String>(returnVals);
		Set<String> results = null;
		if (hasBatch) {
			// don't do the intersection if they only want all chromatograms for 
			// a given chromatogram batch
			if (hasPcrSearchCriteria) {
				chroBatchResults.retainAll(pcrBatchResults);
			}
			results = chroBatchResults;
		} else {
			results = pcrBatchResults;
		}
		return results;
	}
	private void outputGeneError(String geneName) {
		outputNotFoundError("gene", geneName);
	}
	private void outputExtractionError(String extractionName) {
		outputNotFoundError("extraction", extractionName);
	}
	private void outputBatchError(String batchName) {
		outputNotFoundError("chromatogram batch", batchName);
	}
	private void outputNotFoundError(String elementType, String elementName) {
		throw new RuntimeException("There was no " + elementType + " found named '" + elementName + "'");
	}	
	public ChromatogramBatchDAO getChromatogramBatchDAO() {
		return chromatogramBatchDAO;
	}
	public void setChromatogramBatchDAO(ChromatogramBatchDAO chromatogramBatchDAO) {
		this.chromatogramBatchDAO = chromatogramBatchDAO;
	}
	public GeneDAO getGeneDAO() {
		return geneDAO;
	}
	public void setGeneDAO(GeneDAO geneDAO) {
		this.geneDAO = geneDAO;
	}
	public PCRBatchDAO getPcrBatchDAO() {
		return pcrBatchDAO;
	}
	public void setPcrBatchDAO(PCRBatchDAO pcrBatchDAO) {
		this.pcrBatchDAO = pcrBatchDAO;
	}
	public SpecimenExtractionDAO getSpecimenExtractionDAO() {
		return specimenExtractionDAO;
	}
	public void setSpecimenExtractionDAO(SpecimenExtractionDAO specimenExtractionDAO) {
		this.specimenExtractionDAO = specimenExtractionDAO;
	}
}
