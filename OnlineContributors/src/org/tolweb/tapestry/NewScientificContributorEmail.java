package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class NewScientificContributorEmail extends BaseComponent implements BaseInjectable {
	@InjectObject("spring:passwordUtils")
	public abstract PasswordUtils getPasswordUtils();
	public abstract Contributor getContributor();
	
	public String getPlaintextPassword() {
		String password = getPasswordUtils().resetPassword(getContributor());
		return password;
	}
}
