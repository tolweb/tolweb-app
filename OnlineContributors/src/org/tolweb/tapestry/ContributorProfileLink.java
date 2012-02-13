package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.treegrow.main.Contributor;

public abstract class ContributorProfileLink extends BaseComponent {
	public abstract Contributor getContributor();
	public String getImgUrl() {
		if (getContributor().getIsInstitution()) {
			return "/tree/img/institutionprofile.gif";
		} else {
			return "/tree/img/contributorprofile.gif";
		}
	}
}
