package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.injections.GeneFragmentInjectable;
import org.tolweb.btol.tapestry.selection.GeneFragmentRequirementSelectionModel;
import org.tolweb.btol.tapestry.selection.GeneSelectionModel;
import org.tolweb.dao.BaseDAO;
import org.tolweb.tapestry.injections.UserInjectable;

public abstract class EditGeneFragment extends AbstractEditPage implements UserInjectable, GeneFragmentInjectable {
	@Persist("session")
	public abstract GeneFragment getGeneFragment();
	public abstract void setGeneFragment(GeneFragment geneFrag);
	
	@InjectPage("btol/EditGene")
	public abstract EditGene getEditGenePage();
	
	public IPage saveGene(IRequestCycle cycle) {
		getGeneFragmentDAO().saveGeneFragment(getGeneFragment(), getProject().getId());
		// if there is no previous page we were called from a popup window so 
		// display a gene saved message instead of going to another page
		return conditionallyGotoPreviousPage();
	}
	public boolean getIsNewGeneFragment() {
		return getGeneFragment().getId() == null;
	}
	public AbstractEditPage editNewObject(IPage prevPage) {
		return editNewObject(prevPage, null);
	}
	public AbstractEditPage editNewObject(IPage prevPage, Gene gene) {
		GeneFragment gFrag = new GeneFragment();
		gFrag.setGene(gene);
		setGeneFragment(gFrag);
		setPreviousPageName(prevPage.getPageName());
		return this;		
	}
	
	public EditGeneFragment editNewGeneFragmentWithName(String geneFragName) {
		GeneFragment gFrag = new GeneFragment();
		gFrag.setName(geneFragName);
		setGeneFragment(gFrag);
		return this;
	}	
	
	public GeneFragmentRequirementSelectionModel getRequiredForTierModel() {
		return new GeneFragmentRequirementSelectionModel();
	}
	
	@SuppressWarnings("unchecked")
	public GeneSelectionModel getGeneModel() {
		List allGenes = getGeneDAO().getAllGenesInProject(getProject().getId());
		return new GeneSelectionModel(allGenes);
	}
	
	public BaseDAO getDAO() {
		return getGeneFragmentDAO();
	}
	
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return GeneFragment.class;
	}
	protected void setObjectToEdit(Object value) {
		setGeneFragment((GeneFragment) value);
	}
	/*
	public IPage savePrimer(IRequestCycle cycle) {
		if (!getAddedOrRemovedSynonym()) {
			String primerName = getPrimer().getName();
			
			// we need to make sure there aren't duplicates in the db!
			List existingPrimers = getPrimerDAO().getPrimersWithNameOrSynonymAndNotId(primerName, getPrimer().getId(), getProject().getId());
			if (existingPrimers != null && existingPrimers.size() > 0) {
				getValidationDelegate().record("There is an existing primer named " + primerName + 
						".  Primer names must be unique.", ValidationConstraint.CONSISTENCY);
				return null;
			}
			if (getIsNewPrimer()) {
				getPrimer().setCreatedContributor(getContributor());
			}
			getPrimerDAO().savePrimer(getPrimer(), getProject().getId());			
			return conditionallyGotoPreviousPage();
		} else {
			return null;
		}
	}*/
	public IPage saveGeneFragment(IRequestCycle cycle) {
		getGeneFragmentDAO().saveGeneFragment(getGeneFragment(), getProject().getId());
		return conditionallyGotoPreviousPage();
	}
	
}
