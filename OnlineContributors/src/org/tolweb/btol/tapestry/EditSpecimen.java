package org.tolweb.btol.tapestry;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.injections.SpecimenInjectable;
import org.tolweb.dao.BaseDAO;

public abstract class EditSpecimen extends AbstractEditPage implements SpecimenInjectable {
	public abstract void setSpecimen(Specimen value);
	@Persist
	public abstract Specimen getSpecimen();
	
	public IPage saveSpecimen(IRequestCycle cycle) {
		getSpecimenDAO().saveSpecimen(getSpecimen(), getProject().getId());
		// if there is no previous page we were called from a popup window so 
		// display a gene saved message instead of going to another page
		return conditionallyGotoPreviousPage();
	}
	public BaseDAO getDAO() {
		return getSpecimenDAO();
	}
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return Specimen.class;
	}
	protected void setObjectToEdit(Object value) {
		setSpecimen((Specimen) value);
	}
	public AbstractEditPage editNewObject(IPage prevPage) {
		setSpecimen(new Specimen());
		if (prevPage != null) {
			setPreviousPageName(prevPage.getPageName());
		}		
		return this;
	}
	public String getPropertiesString() {
		String props = "family,subfamily,tribe,genus,species," + 
		"identificationStatus,identificationNotes,nodeId,collectionDataSource{required}," + 
		"collectionDataSourceId{required},collectionDate,collectionTime,country{required}," + 
		"adminDivision1{required},adminDivision2,location1{required},location2,latitude,longitude," + 
		"elevation,habitat,collectionMethod," + 
		"preservationMethod,currentCondition," + 
		"lifeStage{required},sex{required},collector,credit,collection{required},notes";
		return props;
	}
}
