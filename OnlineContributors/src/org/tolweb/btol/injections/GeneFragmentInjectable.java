package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.GeneFragmentDAO;

public interface GeneFragmentInjectable extends GeneInjectable {
	@InjectObject("spring:geneFragmentDAO")
	public abstract GeneFragmentDAO getGeneFragmentDAO();
}
