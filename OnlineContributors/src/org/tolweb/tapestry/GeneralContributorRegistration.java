package org.tolweb.tapestry;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.ContributorPermission;
import org.tolweb.treegrow.main.Contributor;

public abstract class GeneralContributorRegistration extends
		AbstractContributorRegistration {
	public abstract boolean getAddEditorSelected();
	public abstract void setAddEditorSelected(boolean value);
	public abstract ContributorPermission getEditorToDelete();
	public abstract void setEditorToDelete(ContributorPermission value);	
	
	/**
	 * Overridden to see if the editor stuff was selected
	 */
	protected boolean checkForRedirectOrErrors(IRequestCycle cycle) {
		if (getAddEditorSelected()) {
			ContributorSearchPage page = (ContributorSearchPage) getRequestCycle().getPage("ContributorSearchPage");
			page.doActivate(cycle, AbstractWrappablePage.DEFAULT_WRAPPER, getPageName(), AbstractContributorSearchOrResultsPage.EDIT_CONTRIBUTOR_PERMISSIONS, null);
			return false;
		} else if (getEditorToDelete() != null) {
			getEditedContributor().removeFromContributorPermissions(getEditorToDelete());
			// don't do a save just yet because they aren't done at this point
			return false;
		} else {
			return super.checkForRedirectOrErrors(cycle);
		}
	}
	
	public byte getContributorType() {
		return Contributor.IMAGES_CONTRIBUTOR;
	}

	protected Object getExistingContributorPageDestination() {
		return "ScientificMaterialsManager";
	}

	protected String getConfirmationPageName() {
		return "ViewContributorConfirmation";
	}
	protected byte getUnapprovedContributorType() {
		return Contributor.ACCESSORY_CONTRIBUTOR;
	}
	protected void setAdditionalConfirmationPageProperties(IPage page) {
		PropertyUtils.write(page, "isFromRegistration", true);
	}	
}
