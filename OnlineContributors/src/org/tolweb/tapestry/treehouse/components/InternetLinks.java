package org.tolweb.tapestry.treehouse.components;

import java.util.Collection;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.InternetLink;

public abstract class InternetLinks extends BaseComponent {
	public abstract InternetLink getCurrentLink();
	@Parameter(required = true)
	public abstract Collection<InternetLink> getLinks();
	
	public boolean getShowCurrentLink() {
		return StringUtils.notEmpty(getCurrentLink().getUrl());
	}
	public String getCurrentLinkUrl() {
		return StringUtils.getProperHttpUrl(getCurrentLink().getUrl());
	}
	public String getCurrentLinkText() {
		if (StringUtils.notEmpty(getCurrentLink().getSiteName())) {
			return getCurrentLink().getSiteName();
		} else {
			return getCurrentLinkUrl();
		}
	}
}
