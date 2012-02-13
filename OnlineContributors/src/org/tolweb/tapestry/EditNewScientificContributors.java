package org.tolweb.tapestry;

import java.util.List;

import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

public abstract class EditNewScientificContributors extends
		EditNewTreehouseContributors implements UserInjectable, BaseInjectable {
	protected String getApproveRejectPageName() {
		return "ApproveRejectScientificContributorPage";
	}
	@SuppressWarnings("unchecked")
	protected List fetchNewContributors() {
		return getContributorDAO().getAllNewScientificContributors();
	}
	public String getNewPassword() {
		return getPasswordUtils().resetPassword(getApprovedContributor()) + "\n";
	}
}
