package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ContributorImage extends BaseComponent implements ImageInjectable {
	public abstract Contributor getContributor();
	public abstract boolean getIncludeFullURL();
	
	public String getImageUrl() {
		return getImageUtils().getContributorImageUrl(getContributor(), getIncludeFullURL());
	}
	public boolean getHasImage() {
		return StringUtils.notEmpty(getImageUrl());
	}
}
