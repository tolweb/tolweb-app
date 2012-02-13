package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.GeneFragmentNodeStatusDAO;

public interface GeneFragmentNodeStatusInjectable {
	@InjectObject("spring:geneFragmentNodeStatusDAO")
	public GeneFragmentNodeStatusDAO getGeneFragmentNodeStatusDAO();

}
