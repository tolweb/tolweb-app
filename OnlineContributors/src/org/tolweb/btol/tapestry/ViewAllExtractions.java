package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.List;

import net.sf.tacos.ajax.AjaxUtils;

import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.tolweb.btol.SourceCollection;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.dao.BaseDAO;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.selectionmodels.PersistentObjectSelectionModelWithDefault;

public abstract class ViewAllExtractions extends AbstractViewAllObjects implements PCRInjectable, 
		RequestInjectable, MiscInjectable {
	public abstract SpecimenExtraction getCurrentExtraction();
	@Persist("session")
	public abstract SourceCollection getSelectedCollection();
	public abstract String getExtractionName();
	public abstract boolean getAjaxSubmit();
	public abstract void setAjaxSubmit(boolean value);
	public abstract SpecimenExtraction getSelectedExtraction();
	public abstract void setSelectedExtraction(SpecimenExtraction value);
	public abstract void setNoMatchingExtraction(boolean value);
	
	public IPropertySelectionModel getCollectionSelectionModel() {
		if (!AjaxUtils.isAjaxCycle(getRequestCycle())) {
			return new PersistentObjectSelectionModelWithDefault(getSpecimenExtractionDAO().getSourceCollections(), "All Collections");
		} else {
			return new StringPropertySelectionModel(new String[] {});
		}
	}
	
	@InjectPage("btol/EditSpecimenExtraction")
	public abstract AbstractEditPage getEditPage();	
	
	public boolean getIsSafari() {
		return getTapestryHelper().getIsSafari();
	}
    public BaseDAO getDAO() {
    	return getSpecimenExtractionDAO();
    }
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
    	return SpecimenExtraction.class;
    }
    public List<SpecimenExtraction> getExtractions() {
    	if (!AjaxUtils.isAjaxCycle(getRequestCycle())) {
	    	if (getSelectedCollection() == null) {
	    		return getSpecimenExtractionDAO().getAllExtractionsInProject(getProject().getId());
	    	} else {
	    		return getSpecimenExtractionDAO().getExtractionsInCollection(getSelectedCollection(), getProject().getId());
	    	}
    	} else {
    		return new ArrayList<SpecimenExtraction>();
    	}
    }
    public void findExtraction() {
    	System.out.println("\n\najax listener firing");
    	setAjaxSubmit(true);
    	SpecimenExtraction extraction = getSpecimenExtractionDAO().getExtractionWithCollectionAndCode(getExtractionName(), getProject().getId());
    	if (extraction != null) {
    		setSelectedExtraction(extraction);
    	} else {
    		setNoMatchingExtraction(true);
    	}
    }
}
