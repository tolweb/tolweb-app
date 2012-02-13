package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ExistingSciContributorTopForm extends BaseComponent implements UserInjectable {
	public boolean getIsCoreContributor() {
		Contributor contr = ((AbstractContributorRegistration) getPage()).getEditedContributor();
		return contr.getContributorType() == Contributor.SCIENTIFIC_CONTRIBUTOR;
	}
	public boolean getShowPasswordFields() {
		Contributor editingContributor = getContributor();
		Contributor beingEdited = ((AbstractContributorRegistration) getPage()).getEditedContributor();
		return editingContributor.getId() == beingEdited.getId();
	}
}
