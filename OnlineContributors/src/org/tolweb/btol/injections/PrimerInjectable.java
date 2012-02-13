package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.PrimerDAO;

public interface PrimerInjectable {
	@InjectObject("spring:primerDAO")
	public abstract PrimerDAO getPrimerDAO();
}
