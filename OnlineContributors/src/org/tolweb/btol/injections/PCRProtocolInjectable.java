package org.tolweb.btol.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.btol.dao.PCRProtocolDAO;

public interface PCRProtocolInjectable {
	@InjectObject("spring:pcrProtocolDAO")
	public abstract PCRProtocolDAO getProtocolDAO();
}
