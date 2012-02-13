package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.SequenceDAO;

public interface SequenceInjectable {
	@InjectObject("spring:sequenceDAO")
	public abstract SequenceDAO getSequenceDAO();
}
