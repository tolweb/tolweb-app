package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.annotations.InjectPage;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.injections.SpecimenInjectable;
import org.tolweb.dao.BaseDAO;

public abstract class ViewAllSpecimens extends AbstractViewAllObjects implements SpecimenInjectable {
	@InjectPage("btol/EditSpecimen")
	public abstract AbstractEditPage getEditPage();	

    public BaseDAO getDAO() {
    	return getSpecimenDAO();
    }
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
    	return Specimen.class;
    }
    public List<Specimen> getSpecimens() {
   		return getSpecimenDAO().getAllSpecimensInProject(getProject().getId());
    }
}
