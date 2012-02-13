package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class AboutPageLicense extends BaseComponent implements PageBeginRenderListener, 
	PageInjectable, BaseInjectable {
	
	@Parameter
	public abstract String getPageName();
	public abstract void setPageName(String pageName);
	@Parameter
	public abstract String getTolPageUrl();
	public abstract void setTolPageUrl(String tolPageUrl);
	@Parameter
	public abstract String getContributorName();
	public abstract void setContributorName(String contrName);
	@Parameter
	public abstract Integer getUsePermission();
	public abstract void setUsePermission(Integer usePermission);

	public abstract String getLicenseUrl();
	public abstract void setLicenseUrl(String licenseUrl);

	public abstract String getLicenseName();
	public abstract void setLicenseName(String licenseName);
	
	public boolean getShowContributorName() {
		return getContributorName() != null && StringUtils.notEmpty(getContributorName());
	}
	
	public String getContributorElements() {
		return "<span xmlns:cc=\"http://creativecommons.org/ns#\" href=\"" + 
				getTolPageUrl() + "\" property=\"cc:attributionName\" rel=\"cc:attributionURL\">" +
				getContributorName() + "</span>";		
	}
	
	public void pageBeginRender(PageEvent event) {
		// DEVN - don't try to switch these two conditions, the call to getUsePermission() has a side-effect that 
		// causes an exception during rewind
		if (!getPage().getRequestCycle().isRewinding() && getUsePermission() != null) {
			ContributorLicenseInfo licInfo = new ContributorLicenseInfo(getUsePermission().intValue());
			setLicenseUrl(ContributorLicenseInfo.linkString(licInfo));
			setLicenseName(licInfo.toString() + " - " + licInfo.getLicenseVersion());
		}
	}
}
