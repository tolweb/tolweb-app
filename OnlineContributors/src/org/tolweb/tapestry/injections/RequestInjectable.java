package org.tolweb.tapestry.injections;

import javax.servlet.http.HttpServletRequest;

import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.web.WebRequest;

public interface RequestInjectable {
	@InjectObject("service:tapestry.globals.WebRequest")
	public WebRequest getRequest();
	@InjectObject("service:tapestry.globals.HttpServletRequest")
	public HttpServletRequest getServletRequest();
}
