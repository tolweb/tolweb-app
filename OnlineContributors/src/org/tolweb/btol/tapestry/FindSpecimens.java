package org.tolweb.btol.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectPage;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.injections.SpecimenInjectable;

public abstract class FindSpecimens extends ProjectPage implements SpecimenInjectable {
	public abstract String getTaxon();
	public abstract Number getSpecimenId();
	
	@InjectPage("btol/FindSpecimensResults")
	public abstract FindSpecimensResults getSearchResultsPage();
	
	public IPage findSpecimens() {
		List<Specimen> specimens = null;
		if (getSpecimenId() != null) {
			Specimen resultSpecimen = getSpecimenDAO().getSpecimenWithId(getSpecimenId().longValue());
			if (resultSpecimen != null) {
				specimens = new ArrayList<Specimen>();
				specimens.add(resultSpecimen);
			}
		} else {
			specimens = getSpecimenDAO().getSpecimensMatchingTaxon(getTaxon(), getProject().getId());
		}
		if (specimens == null || specimens.size() == 0) {
			return null;
		} else {
			getSearchResultsPage().setSpecimens(specimens);
			return getSearchResultsPage();
		}
	}
}
