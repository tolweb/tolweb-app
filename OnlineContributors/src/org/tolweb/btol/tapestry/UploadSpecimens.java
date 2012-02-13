package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.valid.ValidationConstraint;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.injections.SpecimenInjectable;
import org.tolweb.btol.util.MassParseException;
import org.tolweb.btol.util.MassSpecimenParser;

public abstract class UploadSpecimens extends AbstractMassUploadPage implements SpecimenInjectable {
	@InjectObject("spring:specimenParser")
	public abstract MassSpecimenParser getParser();

	@InjectPage("btol/UploadSpecimensResults")
	public abstract UploadSpecimensResults getResultsPage();
	
	public IPage uploadSpecimens() {
		String extractionsString = getUploadString();
		getParser().setStringToParse(extractionsString);
		List<Specimen> specimens = null;
		try {
			specimens = getParser().getSpecimens();
			getSpecimenDAO().saveSpecimens(specimens, getProject().getId());
		} catch (MassParseException e) {
			getDelegate().record(e.getMessage(), ValidationConstraint.CONSISTENCY);
			return null;
		}
		UploadSpecimensResults resultsPage = getResultsPage();
		resultsPage.setSpecimens(specimens);
		return resultsPage;
	}
}
