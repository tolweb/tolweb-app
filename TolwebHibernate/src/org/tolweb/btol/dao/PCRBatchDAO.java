package org.tolweb.btol.dao;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import org.tolweb.btol.PCRBatch;
import org.tolweb.dao.BaseDAO;
import org.tolweb.treegrow.main.Contributor;

public interface PCRBatchDAO  extends BaseDAO {
	public static final int START_YEAR = 0;
	public static final int START_MONTH = 1;
	public static final int START_DAY = 2;
	public static final int END_YEAR = 3;
	public static final int END_MONTH = 4;
	public static final int END_DAY = 5;
	public static final int CONTRIBUTOR = 6;
	public static final int GENE = 7;
	public static final int REACTION_NUMBER = 8;
	public static final int EXTRACTION = 9;
	public static final int TAXON = 10;
	public static final int PROTOCOL = 11;
	public static final int PROJECT_ID = 12;
	
	public PCRBatch getBatchWithId(Long batchId);
	public void saveBatch(PCRBatch batch, Long projectId);
	public void saveBatches(Collection<PCRBatch> batches, Long project);
	public List<PCRBatch> getAllBatchesInProject(Long projectId);
	public List<PCRBatch> getBatchesWithoutImageForContributor(Contributor contr, Long projectId);
	public List<PCRBatch> getBatchesMatchingCriteria(Hashtable<Integer, Object> args);
	public List<String> getChromatogramFilenamesForExtractionGeneAndTaxon(Collection<Long> extractionIds, 
			Collection<Long> geneIds, String taxon, Long projectId);	
}
