package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationConstraint;
import org.tolweb.btol.SourceCollection;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.btol.injections.SpecimenInjectable;
import org.tolweb.dao.BaseDAO;
import org.tolweb.tapestry.selectionmodels.PersistentObjectSelectionModel;
import org.tolweb.treegrow.main.StringUtils;

public abstract class EditSpecimenExtraction extends AbstractEditPage implements PCRInjectable, SpecimenInjectable {
	public abstract void setExtraction(SpecimenExtraction value);
	@Persist
	public abstract SpecimenExtraction getExtraction();
	
	public IPage saveExtraction(IRequestCycle cycle) {
		if (StringUtils.isEmpty(getError()) && !getValidationDelegate().getHasErrors()) {
			getSpecimenExtractionDAO().saveExtraction(getExtraction(), getProject().getId());
			// if there is no previous page we were called from a popup window so 
			// display a gene saved message instead of going to another page
			return conditionallyGotoPreviousPage();
		} else {
			return null;
		}
	}
	public BaseDAO getDAO() {
		return getSpecimenExtractionDAO();
	}
	@SuppressWarnings("unchecked")
	public Class getEditObjectClass() {
		return SpecimenExtraction.class;
	}
	protected void setObjectToEdit(Object value) {
		setExtraction((SpecimenExtraction) value);
	}
	public AbstractEditPage editNewObject(IPage prevPage) {
		setExtraction(new SpecimenExtraction());
		if (prevPage != null) {
			setPreviousPageName(prevPage.getPageName());
		}		
		return this;
	}
	public String getPropertiesString() {
		return "specimenId{required},sourceDatabase,code{required},extractionDate,person{required},method,target{required},dnaCollections,notes";
	}
	public Number getSpecimenId() {
		return getExtraction().getSpecimen().getId();
	}
	public void setSpecimenId(Number value) {
		Specimen spec = getSpecimenDAO().getSpecimenWithId(Long.valueOf(value.longValue()));
		if (spec != null) {
			getExtraction().setSpecimen(spec);
		} else {
			getValidationDelegate().record("There is no specimen with id " + value, ValidationConstraint.CONSISTENCY);
		}
	}
	public IPropertySelectionModel getSourceDatabaseSelectionModel() {
		List<SourceCollection> sourceCollections = getSpecimenExtractionDAO().getSourceCollections();
		return new PersistentObjectSelectionModel(sourceCollections);
	}
}
