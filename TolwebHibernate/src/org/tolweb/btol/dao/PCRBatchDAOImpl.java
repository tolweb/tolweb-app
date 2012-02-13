package org.tolweb.btol.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.tolweb.btol.Gene;
import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.PCRProtocol;
import org.tolweb.btol.Project;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public class PCRBatchDAOImpl extends ProjectAssociatedDAOImpl implements PCRBatchDAO {
	private SpecimenExtractionDAO specimenExtractionDAO;

	public PCRBatch getBatchWithId(Long batchId) {
		return (PCRBatch) getObjectWithId(PCRBatch.class, batchId);
	}
	
	public void saveBatch(PCRBatch batch, Long projectId) {
		doProjectAssociatedSave(batch, projectId, getJoinTableName());
	}

	public void saveBatches(Collection<PCRBatch> batches, Long projectId) {
		for (PCRBatch batch : batches) {
			saveBatch(batch, projectId);
		}
	}
	public List<PCRBatch> getAllBatchesInProject(Long project) {
		return getHibernateTemplate().find(getBatchSelectPrefix(project));
	}

	public List<PCRBatch> getBatchesWithoutImageForContributor(Contributor contr, Long projectId) {
		String queryString = getBatchSelectPrefix(projectId);
		queryString += " and batch.contributorId=" + contr.getId() + " and (batch.imageFilename1=null or batch.imageFilename1='')";
		return getHibernateTemplate().find(queryString);
	}

	/**
	 * no longer used due to hibernate's crippled criteria api
	 * keeping around in case they ever get their act together
	 * @param projectId
	 * @return
	 */
	private Criteria createBatchCriteria(Long projectId) {
		Criteria crit = getSession().createCriteria(Project.class);
		crit.add(Restrictions.eq("id", projectId));
		crit.createAlias("pcrBatchesSet", "batch");
		//crit.setProjection(Projections.property("c.name"));		
		crit.setProjection(Projections.property("pcrBatchesSet"));
		//crit = crit.createCriteria("pcrBatchesSet");
		//crit.setProjection(Projections.property("pcrBatchesSet"));
		//crit.setProjection(Projections.property("name"));
		return crit;
	}
	
	/*public List<PCRBatch> getAllBatches() {
		Criteria crit = createBatchCriteria();
		return crit.list();
	}*/

	public List<PCRBatch> getBatchesMatchingCriteria(Hashtable<Integer, Object> args) {
		Long projectId = (Long) args.get(PROJECT_ID);	
		//"select abi from org.tolweb.btol.Project p join p.pcrBatchesSet as b "
		String queryString = "select distinct batch from org.tolweb.btol.Project p join p.pcrBatchesSet as batch ";
		//Criteria crit = createBatchCriteria(projectId);
		//crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		String extractionString = (String) args.get(EXTRACTION);
		String reactionNumber = (String) args.get(REACTION_NUMBER);
		String taxon = (String) args.get(TAXON);
		//crit.add(Restrictions.eq("id", projectId));
		boolean shouldCreateReactionsAlias = StringUtils.notEmpty(extractionString) || 
			StringUtils.notEmpty(reactionNumber) || StringUtils.notEmpty(taxon);
		boolean shouldCreateExtractionAlias = StringUtils.notEmpty(taxon);
 		Gene gene = (Gene) args.get(GENE);
 		boolean shouldCreateGeneAlias = gene != null;
		PCRProtocol protocol = (PCRProtocol) args.get(PROTOCOL);
		boolean shouldCreateProtocolAlias = protocol != null;
		if (shouldCreateReactionsAlias) {
			queryString += " join batch.reactions as reaction ";			
		}
		if (shouldCreateGeneAlias) {
			queryString += " join batch.forwardPrimer as forwardPrimer join batch.reversePrimer as reversePrimer " + 
				"join forwardPrimer.gene as fpGene join reversePrimer.gene as rpGene";
		}
		if (shouldCreateExtractionAlias) {
			queryString += " join reaction.extraction as extraction join extraction.specimen as specimen ";
		}
		if (shouldCreateProtocolAlias) {
			queryString += " join batch.protocol as protocol ";
		}
		queryString += " where p.id=" + projectId;
		if (StringUtils.notEmpty(extractionString)) {
			System.out.println("extraction dao is: " + getSpecimenExtractionDAO());
			SpecimenExtraction extraction = getSpecimenExtractionDAO().getExtractionWithCollectionAndCode(extractionString, projectId);
			// didn't locate the extraction, so there's no way we'll find anything else
			if (extraction == null) {
				return new ArrayList<PCRBatch>();
			} else {
				queryString += "and reaction.extraction.id=" + extraction.getId(); 
				//crit.add(Restrictions.eq("reaction.extraction", extraction));
			}
		}
		if (shouldCreateExtractionAlias) {
			// the taxon field can match any one of the individual ranks
			queryString += getSpecimenExtractionDAO().getTaxonQueryString(taxon);
		}
		Integer startYear = (Integer) args.get(START_YEAR);
		if (startYear != null) {
			queryString += " and batch.creationYear >= " + startYear;
		}
		Integer startMonth = (Integer) args.get(START_MONTH);
		if (startMonth != null) {
			queryString += " and batch.creationMonth >= " + startMonth;
		}
		Integer startDay = (Integer) args.get(START_DAY);
		if (startDay != null) {
			queryString += " and batch.creationDay >= " + startDay;
		}
		Integer endYear = (Integer) args.get(END_YEAR);
		if (endYear != null) {
			queryString += " and batch.creationYear <= " + endYear;
		}
		Integer endMonth = (Integer) args.get(END_MONTH);
		if (endMonth != null) {
			queryString += " and batch.creationMonth <= " + endMonth;
		}
		Integer endDay = (Integer) args.get(END_DAY);
		if (endDay != null) {
			queryString += " and batch.creationDay <= " + endDay;
		}
		Contributor person = (Contributor) args.get(CONTRIBUTOR);
		if (person != null) {
			queryString += " and batch.contributorId=" + person.getId();
		}
		if (protocol != null) {
			queryString += " and batch.protocol.id=" + protocol.getId();
		}
		if (shouldCreateGeneAlias) {
			queryString += " and (fpGene.id=" + gene.getId() + " or rpGene.id=" + gene.getId() + ")";
		}
		if (StringUtils.notEmpty(reactionNumber)) {
			queryString += "and reaction.btolCode=" + reactionNumber;
		}
		System.out.println("query string is: " + queryString);
		return getHibernateTemplate().find(queryString);
	}

	public List<String> getChromatogramFilenamesForExtractionGeneAndTaxon(Collection<Long> extractionIds, 
			Collection<Long> geneIds, String taxon, Long projectId) {
		boolean hasExtractionIds = (extractionIds != null && extractionIds.size() > 0);
		boolean hasTaxon = StringUtils.notEmpty(taxon);
		boolean includeExtraction = hasExtractionIds || hasTaxon;
		boolean includeGene = geneIds != null && geneIds.size() > 0;
		String queryString = "select chro.filename from org.tolweb.btol.Project p join p.pcrBatchesSet as b ";
		if (includeGene) {
			queryString += " join b.forwardPrimer as fp join b.reversePrimer as rp join fp.gene as fgene join rp.gene as rgene ";
		} 
		queryString += " join b.reactions as r join r.chromatograms as chro ";

		if (includeExtraction) {
			queryString += " join r.extraction as extraction join extraction.specimen as specimen ";
		}
		queryString += " where p.id=" + projectId;	
		if (hasExtractionIds) {
			queryString += "and extraction.id " + StringUtils.returnSqlCollectionString(extractionIds);
		}
		if (hasTaxon) {
			queryString += getSpecimenExtractionDAO().getTaxonQueryString(taxon);
		}
		if (includeGene) {
			String idsString = StringUtils.returnSqlCollectionString(geneIds);
			queryString += " and (fgene.id " + idsString + " or rgene.id " + idsString + ")";
		}
		System.out.println("queryString is: \n\n\n" + queryString);
		return getHibernateTemplate().find(queryString);
	}
	
	public SpecimenExtractionDAO getSpecimenExtractionDAO() {
		return specimenExtractionDAO;
	}
	public void setSpecimenExtractionDAO(SpecimenExtractionDAO specimenExtractionDAO) {
		this.specimenExtractionDAO = specimenExtractionDAO;
	}
	protected String getBatchSelectPrefix(Long projectId) {
		return getSelectPrefix("batch", "pcrBatchesSet", projectId);
	}	
	protected String getBatchSelectPrefix(Long projectId, String additionalJoin) {
		return getSelectPrefix("batch", "pcrBatchesSet", projectId, additionalJoin);
	}	
	public String getForeignKeyColumnName() {
		return "batchId";
	}
	public String getJoinTableName() {
		return "BatchesToProjects";
	}
}
