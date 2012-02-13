package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.GlossaryDAO;
import org.tolweb.misc.GlossaryLookup;

public interface GlossaryInjectable {
	@InjectObject("spring:glossaryDAO")
	public GlossaryDAO getGlossaryDAO();
	@InjectObject("spring:glossaryLookup")
	public GlossaryLookup getGlossaryLookup();
}
