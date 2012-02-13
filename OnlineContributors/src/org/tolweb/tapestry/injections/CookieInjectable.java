package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.hivemind.CookieAndContributorSource;

public interface CookieInjectable {
	@InjectObject("service:org.tolweb.tapestry.TolwebCookieSource")
	public CookieAndContributorSource getCookieAndContributorSource();
}
