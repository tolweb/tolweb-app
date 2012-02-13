package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.ValidationConstraint;
import org.tolweb.btol.SourceCollection;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.injections.PCRInjectable;
import org.tolweb.btol.util.MassExtractionParser;
import org.tolweb.btol.util.MassParseException;
import org.tolweb.tapestry.selectionmodels.PersistentObjectSelectionModel;

public abstract class UploadExtractions extends AbstractMassUploadPage implements PCRInjectable {
	@InjectObject("spring:extractionParser")
	public abstract MassExtractionParser getParser();
	public abstract SourceCollection getSelectedCollection();

	@InjectPage("btol/UploadExtractionsResults")
	public abstract UploadExtractionsResults getResultsPage();
	public abstract void setExistingExtractionNames(List<String> value);
	public abstract List<String> getExistingExtractionNames();
	public abstract String getCurrentExistingCode();
	
	public IPage uploadExtractions() {
		String extractionsString = getUploadString();
		getParser().setStringToParse(extractionsString);
		List<SpecimenExtraction> extractions = null;
		try {
			List<String> existingExtractionNames = new ArrayList<String>();
			extractions = getParser().getExtractions(getSelectedCollection(), existingExtractionNames, getProject().getId());
			if (existingExtractionNames.size() > 0) {
				setExistingExtractionNames(existingExtractionNames);
				getDelegate().record("The following extractions are already in the database.  Extraction codes must be unique.", 
						ValidationConstraint.CONSISTENCY);
				return null;
			}
			getSpecimenExtractionDAO().saveExtractions(extractions, getProject().getId());
		} catch (MassParseException e) {
			String message = e.getMessage();
			getDelegate().record(message, ValidationConstraint.CONSISTENCY);
			return null;
		}
		UploadExtractionsResults resultsPage = getResultsPage();
		resultsPage.setExtractions(extractions);
		return resultsPage;
	}
	public IPropertySelectionModel getCollectionSelectionModel() {
		return new PersistentObjectSelectionModel(getSpecimenExtractionDAO().getSourceCollections());
	}
	public boolean getHasExistingExtractions() {
		List<String> existingExtractionNames = getExistingExtractionNames();
		return existingExtractionNames != null && existingExtractionNames.size() > 0;
	}
	public String getExistingCodeString() {
		return getSelectedCollection().getCode() + getCurrentExistingCode();
	}
}
