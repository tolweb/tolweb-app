package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.web.WebRequest;

public interface RequestInjectable {
	@InjectObject("service:tapestry.globals.WebRequest")
	public WebRequest getRequest();
}
