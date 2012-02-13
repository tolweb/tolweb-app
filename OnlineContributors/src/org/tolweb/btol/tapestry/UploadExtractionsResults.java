package org.tolweb.btol.tapestry;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.dao.BaseDAO;

public abstract class UploadExtractionsResults extends AbstractViewAllObjects implements PCRInjectable {
	@Persist("session")
	public abstract List<SpecimenExtraction> getExtractions();
	public abstract void setExtractions(List<SpecimenExtraction> extractions);
	public abstract SpecimenExtraction getCurrentExtraction();
	@InjectPage("btol/EditSpecimenExtraction")
	public abstract AbstractEditPage getEditPage();	
	
    public BaseDAO getDAO() {
    	return getSpecimenExtractionDAO();
    }
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
    	return SpecimenExtraction.class;
    }
	
	public void deleteAll() {
		getSpecimenExtractionDAO().deleteAll(getExtractions());
		getExtractions().clear();
	}
	@SuppressWarnings("unchecked")
	public void deleteObject(Number id) {
		SpecimenExtraction extractionToDelete = null;
		for (Iterator iter = getExtractions().iterator(); iter.hasNext();) {
			SpecimenExtraction extraction = (SpecimenExtraction) iter.next();
			if (extraction.getId().equals(id)) {
				extractionToDelete = extraction;
				break;
			}
		}
		if (extractionToDelete != null) {
			super.deleteObject(id);
			getExtractions().remove(extractionToDelete);
		}
	}
	public IPage editObject(Number id) {
		getEditPage().setObjectToEdit(getObjectFromRequestCycle(getRequestCycle()));
		getEditPage().setPreviousPageName(getPageName());
		return getEditPage();
	}
}
