package org.tolweb.btol.tapestry;

import java.util.Collection;
import java.util.List;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.btol.Specimen;
import org.tolweb.btol.SpecimenExtraction;
import org.tolweb.btol.injections.PCRInjectable;

public abstract class SpecimensTable extends BaseComponent implements PCRInjectable {
	@Parameter
	public abstract Collection<Specimen> getSpecimens();
	@Parameter(required = false, defaultValue = "false")
	public abstract boolean getShowExtractions();
	
	public abstract Specimen getCurrentSpecimen();
	public abstract SpecimenExtraction getCurrentExtraction();
	@SuppressWarnings("unchecked")
	public List getExtractions() {
		return getSpecimenExtractionDAO().getSpecimenExtractionsForSpecimen(getCurrentSpecimen());
	}
}
