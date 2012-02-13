package org.tolweb.tapestry.injections;

import org.tolweb.tapestry.injections.UserInjectable;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.annotations.InjectStateFlag;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.treegrow.main.Contributor;

public interface UserInjectable {
	@InjectObject("spring:contributorDAO")
	public ContributorDAO getContributorDAO();
	@InjectObject("spring:passwordUtils")
	public PasswordUtils getPasswordUtils();
    @InjectObject("spring:permissionChecker")
    public PermissionChecker getPermissionChecker();
	@InjectState("contributor")
	public Contributor getContributor();
	public void setContributor(Contributor value);
	@InjectStateFlag("contributor")
	public boolean getContributorExists();
}
