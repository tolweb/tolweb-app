package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.tapestry.injections.BaseInjectable;

public abstract class StandardMenus extends BaseComponent implements BaseInjectable {
	public String getHomeLinkImageUrl() {
		if (CacheAndPublicAwarePage.class.isInstance(getPage())) {
			if (((CacheAndPublicAwarePage) getPage()).getIsWorking()) {
				return getUrlBuilder().getAssetUrlString("img/ToLWebDev.gif");
			}
		}
		return getUrlBuilder().getAssetUrlString("img/ToLWebProject.gif");		
	}
}
