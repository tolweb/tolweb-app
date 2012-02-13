package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.GeneDAO;

public interface GeneInjectable {
	@InjectObject("spring:geneDAO")
	public abstract GeneDAO getGeneDAO();
}
