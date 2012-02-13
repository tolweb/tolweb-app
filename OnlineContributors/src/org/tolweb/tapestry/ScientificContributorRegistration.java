package org.tolweb.tapestry;

import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.treegrow.main.Contributor;

public abstract class ScientificContributorRegistration extends
		GeneralContributorRegistration implements PageBeginRenderListener{
	public abstract void setFromProfilePage(boolean value);
	@Persist("session")
	public abstract boolean getFromProfilePage();
	
	@InitialValue("true")
	public abstract boolean getReviewDefaultLicenses();
	public abstract void setReviewDefaultLicenses(boolean value);
	
	/**
	 * Clears all persistent properties.  Currently needs to be
	 * manually called.  Eventually this should get reworked into
	 * the activate() method so it gets called automatically
	 */
	public void clearPersistentProperties() {
		super.clearPersistentProperties();
		setFromProfilePage(false);
	}		
	public void pageBeginRender(PageEvent event) {
		super.pageBeginRender(event);
		if (getEditedContributor() != null && getIsNewContributor()) {
			getEditedContributor().setWillingToCoordinate(true);
		}
	}
	public PermissionConvertor getPermissionConvertor() {
		return new PermissionConvertor(getEditedContributor().getContributorPermissions());
	}
	protected byte getUnapprovedContributorType() {
		return Contributor.SCIENTIFIC_CONTRIBUTOR;
	}
	protected Object getExistingContributorPageDestination() {
		if (getFromProfilePage()) {
			ContributorDetailPage detailPage = (ContributorDetailPage) getRequestCycle().getPage("ContributorDetailPage");
			detailPage.setViewedContributor(getEditedContributor());
			return detailPage;
		} else {
			return super.getExistingContributorPageDestination();
		}
	}
}
