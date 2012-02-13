package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;

public abstract class EditBranchLeafMedia extends AbstractNodeEditingPage {
    /**
     * Don't actually do anything because we aren't saving anything.
     * This page is subclassed for navigation purposes.
     */
    protected void savePage() {}
    
    public void goToRegistration(IRequestCycle cycle) {
        ImageContributorRegistration registrationPage = (ImageContributorRegistration) cycle.getPage("ImageContributorRegistration");
        registrationPage.setEditedContributor(getContributor());
        cycle.activate(registrationPage);
    }
    public String getInstructions() {
    	return "<p>When you click on the <em>edit</em> link next a thumbnail, a new window will open that lets you edit the data for this image.</p> " + 
			"<p>Please note that you can edit only one image at a time.  If you would like to edit several images in a row, make sure to submit your image data before clicking on a new <em>edit</em> link.</p>";
    }
    public String getNodeName() {
    	return getNode() != null ? getNode().getName() : "";
    }
    /**
     * tapestry expects this here because the parameter value gets pushed back out
     * @param value
     */
    public void setNodeName(String value) {}
}
