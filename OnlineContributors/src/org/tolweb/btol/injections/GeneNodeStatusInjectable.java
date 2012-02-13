package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.GeneNodeStatusDAO;

/**
 * @deprecated As of July 2007, replaced by GeneFragmentNodeStatusInjectable
 * @author dmandel
 */
public interface GeneNodeStatusInjectable {
	@InjectObject("spring:geneNodeStatusDAO")
	public GeneNodeStatusDAO getGeneNodeStatusDAO();
}
