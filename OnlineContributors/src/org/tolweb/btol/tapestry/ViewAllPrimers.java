package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Gene;
import org.tolweb.btol.Primer;
import org.tolweb.btol.injections.GeneInjectable;
import org.tolweb.btol.injections.PrimerInjectable;
import org.tolweb.btol.tapestry.selection.GeneFilterSelectionModel;
import org.tolweb.dao.BaseDAO;

public abstract class ViewAllPrimers extends AbstractViewAllObjects implements PrimerInjectable, GeneInjectable {
	@InjectPage("btol/EditPrimer")
	public abstract AbstractEditPage getEditPage();
    @InjectPage("btol/ViewFullPrimerInfo")
    public abstract ViewFullPrimerInfo getViewFullInfoPage();
	public abstract Primer getCurrentPrimer();
	public abstract boolean getShowNotes();
	@Persist("session")
	public abstract Gene getSelectedGene();

	@SuppressWarnings("unchecked")
	public GeneFilterSelectionModel getGeneSelectionModel() {
		GeneFilterSelectionModel model = new GeneFilterSelectionModel(getGeneDAO().getAllGenesInProject(getProject().getId()));
		return model;
	}
	
	@SuppressWarnings("unchecked")
	public List getPrimers() {
		return filterPrimersToVisible(getPrimerDAO().getPrimersForGene(getSelectedGene(), getProject().getId()));
	}
	public EditPrimer editPrimer(IRequestCycle cycle) {
		Long primerId = (Long) cycle.getListenerParameters()[0];
		Primer primer = getPrimerDAO().getPrimerWithId(primerId, getProject().getId());
		EditPrimer editPage = (EditPrimer) getEditPage();
		editPage.setPrimer(primer);
		editPage.setPreviousPageName(getPageName());
		return editPage;
	}   
    public ViewFullPrimerInfo viewFullPrimerInfo() {
        ViewFullPrimerInfo viewPage = getViewFullInfoPage();
        Number id = (Number) getRequestCycle().getListenerParameters()[0];
        Primer primer = getPrimerDAO().getPrimerWithId(Long.valueOf(id.longValue()), getProject().getId());
        viewPage.setPrimer(primer);
        return viewPage;
    }
    public BaseDAO getDAO() {
        return getPrimerDAO();
    }
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
        return Primer.class;
    }    
    public String getColumnsString() {
    	String colsString =  "Gene:gene.name, Name:name, Code:code, Sequence (length):sequenceString,";
    	colsString += "direction: :directionString, Synonyms:synonymsString, !details, !edit, !delete, !notes";
    	return colsString;
    }
    public String getTableId() {
    	return "primersTable";
    }
    public AbstractEditPage editNewObject() {
    	if (getSelectedGene() == null) {
    		return super.editNewObject();
    	} else {
    		return ((EditPrimer) getEditPage()).editNewObject(this, getSelectedGene());
    	}
    }
    @SuppressWarnings("unchecked")
    private List filterPrimersToVisible(List<Primer> primers) {
    	List visiblePrimers = new ArrayList<Primer>();
    	for (Primer primer : primers) {
    		// add it to the visible list if it's not private
    		// or the person who's looking at it is the creator
			if (getContributorCanViewPrimer(primer)) {
				visiblePrimers.add(primer);
			}
		}
    	return getFilteredByDefunctList(visiblePrimers);
    }
    
    private boolean getContributorCanViewPrimer(Primer primer) {
    	boolean isPrivate = primer.getPrivateFlag();
    	boolean privateButCanView = primer.getCreatedContributorId() != null &&
			(primer.getCreatedContributorId().equals(getContributor().getId()) ||
				getProject().getInSameWorkingGroup(primer.getCreatedContributor(), getContributor()));
		return !isPrivate || privateButCanView;
    }
}
