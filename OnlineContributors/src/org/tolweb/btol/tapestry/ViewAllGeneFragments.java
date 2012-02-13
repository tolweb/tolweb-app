package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Gene;
import org.tolweb.btol.GeneFragment;
import org.tolweb.btol.injections.GeneFragmentInjectable;
import org.tolweb.btol.tapestry.selection.GeneFragmentRequirementSelectionModel;
import org.tolweb.dao.BaseDAO;

public abstract class ViewAllGeneFragments extends AbstractViewAllObjects implements GeneFragmentInjectable {
	@InjectPage("btol/EditGeneFragment")
	public abstract AbstractEditPage getEditPage();
	public abstract GeneFragment getCurrentGeneFragment();
	
	@Persist("session")
	public abstract Gene getSelectedGene();
	
	public GeneFragmentRequirementSelectionModel getRequiredForTierModel() {
		return new GeneFragmentRequirementSelectionModel();
	}
	@SuppressWarnings("unchecked")
	public List getGeneFragments() {
		return getGeneFragmentDAO().getAllGeneFragmentsInProject(getProject().getId());
	}
	public EditGeneFragment editGeneFragment(IRequestCycle cycle) {
		Long geneFragId = (Long) cycle.getListenerParameters()[0];
		GeneFragment geneFrag = getGeneFragmentDAO().getGeneFragmentWithId(geneFragId, getProject().getId());
		EditGeneFragment editPage = (EditGeneFragment) getEditPage();
		editPage.setGeneFragment(geneFrag);
		editPage.setPreviousPageName(getPageName());
		return editPage;
	}   
    public BaseDAO getDAO() {
        return getGeneFragmentDAO();
    }
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
        return GeneFragment.class;
    } 
    public String getColumnsString() {
    	String colsString =  "Name:name, Gene:gene.name, Abbreviated Name:abbreviatedName, !required, ";
    	colsString += "!edit, !delete";
    	return colsString;
    }
    public String getTableId() {
    	return "geneFragmentsTable";
    }
    public String getRequiredForTierText() {
    	int reqTier = getCurrentGeneFragment().getRequiredForTier();
    	return getRequiredForTierModel().getLabel(reqTier);
    }
}
