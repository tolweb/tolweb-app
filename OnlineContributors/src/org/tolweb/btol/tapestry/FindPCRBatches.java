package org.tolweb.btol.tapestry;

import java.util.Hashtable;
import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.tolweb.btol.Gene;
import org.tolweb.btol.PCRBatch;
import org.tolweb.btol.PCRProtocol;
import org.tolweb.btol.dao.PCRBatchDAO;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.tapestry.ContributorSelectionModel;
import org.tolweb.tapestry.selectionmodels.PersistentObjectSelectionModelWithDefault;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;


public abstract class FindPCRBatches extends ProjectPage implements PCRInjectable, GeneInjectable {
	public abstract void setErrorMessage(String value);
	public abstract Integer getStartYear();
	public abstract Integer getStartMonth();
	public abstract Integer getStartDay();
	public abstract Integer getEndYear();
	public abstract Integer getEndMonth();
	public abstract Integer getEndDay();
	public abstract Contributor getPerson();
	public abstract Gene getGene();
	public abstract String getReactionNumber();
	public abstract String getExtraction();
	public abstract String getTaxon();
	public abstract PCRProtocol getProtocol();
	@InjectPage("btol/FindPCRBatchesResults")
	public abstract FindPCRBatchesResults getResultsPage();
	
	public IPropertySelectionModel getPersonSelectionModel() {
		return new ContributorSelectionModel(getProject().getCoreProjectMembers(), true, true, getContributor());
	}
	
	public IPropertySelectionModel getGeneSelectionModel() {
		return new PersistentObjectSelectionModelWithDefault(getGeneDAO().getAllGenesInProject(getProject().getId()), "");
	}
	
	public IPropertySelectionModel getProtocolSelectionModel() {
		return new PersistentObjectSelectionModelWithDefault(getProtocolDAO().getAllProtocolsInProject(getProject().getId()), "");
	}
	
	public IPage findBatches() {
		Hashtable<Integer, Object> args = new Hashtable<Integer, Object>();
		if (getStartYear() != null) {
			args.put(PCRBatchDAO.START_YEAR, getStartYear());
		}
		if (getStartMonth() != null) {
			args.put(PCRBatchDAO.START_MONTH, getStartMonth());
		}
		if (getStartDay() != null) {
			args.put(PCRBatchDAO.START_DAY, getStartDay());
		}	
		if (getEndYear() != null) {
			args.put(PCRBatchDAO.END_YEAR, getEndYear());
		}
		if (getEndMonth() != null) {
			args.put(PCRBatchDAO.END_MONTH, getEndMonth());
		}
		if (getEndDay() != null) {
			args.put(PCRBatchDAO.END_DAY, getEndDay());
		}
		if (getPerson() != null) {
			args.put(PCRBatchDAO.CONTRIBUTOR, getPerson());
		}
		if (getGene() != null) {
			args.put(PCRBatchDAO.GENE, getGene());
		}
		if (getProtocol() != null) {
			args.put(PCRBatchDAO.PROTOCOL, getProtocol());
		}
		if (StringUtils.notEmpty(getExtraction())) {
			args.put(PCRBatchDAO.EXTRACTION, getExtraction());
		}
		if (StringUtils.notEmpty(getReactionNumber())) {
			args.put(PCRBatchDAO.REACTION_NUMBER, getReactionNumber());
		}
		if (StringUtils.notEmpty(getTaxon())) {
			args.put(PCRBatchDAO.TAXON, getTaxon());
		}
		if (args.size() == 0) {
			setErrorMessage("Please specify one or more values to search on");
			return null;
		} else {
			args.put(PCRBatchDAO.PROJECT_ID, getProject().getId());
			List<PCRBatch> results = getPCRBatchDAO().getBatchesMatchingCriteria(args);
			if (results == null || results.size() == 0) {
				setErrorMessage("No results found matching the specified criteria");
				return null;
			} else {
				getResultsPage().setBatches(results);
				return getResultsPage();
			}
		}
	}
}