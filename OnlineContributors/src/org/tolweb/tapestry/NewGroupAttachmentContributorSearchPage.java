package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class NewGroupAttachmentContributorSearchPage extends
		ContributorSearchPage implements NodeInjectable {
	public void addNewContributor(IRequestCycle cycle) {
		Long nodeId = (Long) cycle.getListenerParameters()[0];
		ScientificContributorRegistrationOther otherPage = (ScientificContributorRegistrationOther) cycle.getPage("ScientificContributorRegistrationOther");
		otherPage.addNewContributor(true, nodeId, cycle, true);
	}
	public String getNodeName() {
		return getWorkingNodeDAO().getNameForNodeWithId(getEditedObjectId());
	}
}
