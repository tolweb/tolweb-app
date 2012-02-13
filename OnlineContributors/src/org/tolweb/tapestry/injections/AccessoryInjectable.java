package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.AccessoryPagePusher;
import org.tolweb.hivemind.AccessoryPageHelper;
import org.tolweb.misc.AccessoryPageSubmitter;

public interface AccessoryInjectable {
	@InjectObject("spring:workingAccessoryPageDAO")
	public AccessoryPageDAO getWorkingAccessoryPageDAO();
	@InjectObject("spring:publicAccessoryPageDAO")
	public AccessoryPageDAO getPublicAccessoryPageDAO();	
	@InjectObject("spring:accessoryPagePusher")
	public AccessoryPagePusher getAccessoryPagePusher();
	@InjectObject("spring:accessoryPageSubmitter")	
	public AccessoryPageSubmitter getAccessoryPageSubmitter();
	@InjectObject("service:org.tolweb.tapestry.AccessoryPageHelper")
	public AccessoryPageHelper getAccessoryPageHelper();
}
