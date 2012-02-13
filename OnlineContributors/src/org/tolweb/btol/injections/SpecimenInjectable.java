package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.SpecimenDAO;

public interface SpecimenInjectable {
	@InjectObject("spring:specimenDAO")
	public SpecimenDAO getSpecimenDAO();
}
