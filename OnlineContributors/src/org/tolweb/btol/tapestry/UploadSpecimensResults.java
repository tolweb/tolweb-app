package org.tolweb.btol.tapestry;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.injections.SpecimenInjectable;
import org.tolweb.dao.BaseDAO;

public abstract class UploadSpecimensResults extends AbstractViewAllObjects implements SpecimenInjectable {
	public abstract void setSpecimens(List<Specimen> value);
	@Persist("session")
	public abstract List<Specimen> getSpecimens();
	public abstract Specimen getCurrentSpecimen();
	
	@InjectPage("btol/EditSpecimen")
	public abstract AbstractEditPage getEditPage();

    public BaseDAO getDAO() {
    	return getSpecimenDAO();
    }
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
    	return Specimen.class;
    }
	
	public void deleteAll() {
		if (getSpecimens() != null && getSpecimens().size() > 0) {
			getSpecimenDAO().deleteAll(getSpecimens());
			getSpecimens().clear();
		}
	}	
	@SuppressWarnings("unchecked")
	public void deleteObject(Number id) {
		Specimen specimenToDelete = null;
		for (Iterator iter = getSpecimens().iterator(); iter.hasNext();) {
			Specimen specimen = (Specimen) iter.next();
			if (specimen.getId().equals(id)) {
				specimenToDelete = specimen;
				break;
			}
		}
		if (specimenToDelete != null) {
			super.deleteObject(id);
			getSpecimens().remove(specimenToDelete);
		}
	}
	public IPage editObject(Number id) {
		getEditPage().setObjectToEdit(getObjectFromRequestCycle(getRequestCycle()));
		getEditPage().setPreviousPageName(getPageName());
		return getEditPage();
	}	
}
