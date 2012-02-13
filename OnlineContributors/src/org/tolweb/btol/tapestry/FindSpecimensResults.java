package org.tolweb.btol.tapestry;

import java.util.List;

import org.apache.tapestry.annotations.Persist;
import org.tolweb.btol.Specimen;

public abstract class FindSpecimensResults extends ProjectPage {
	@Persist
	public abstract void setSpecimens(List<Specimen> value);
}
