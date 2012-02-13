package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ContributorNameOrHomepageLink extends BaseComponent {
	public abstract Contributor getContributor();
	public boolean getHasHomepage() {
		return StringUtils.notEmpty(getContributor().getHomepage());
	}
	public String getContributorHomepageUrl() {
		String homepage = getContributor().getHomepage();
		return StringUtils.getProperHttpUrl(homepage);
	}
}
