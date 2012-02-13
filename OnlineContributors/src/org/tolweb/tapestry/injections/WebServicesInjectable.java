package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.WebServicesKeyDAO;

public interface WebServicesInjectable {
	@InjectObject("spring:webServicesKeyDAO")
	public WebServicesKeyDAO getWebServicesKeyDAO();
}
