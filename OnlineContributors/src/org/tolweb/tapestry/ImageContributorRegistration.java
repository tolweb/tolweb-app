/*
 * Home.java
 *
 * Created on April 22, 2004, 10:49 AM
 */

package org.tolweb.tapestry;

import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.Contributor;

/**
 *
 * @author  dmandel
 */
public abstract class ImageContributorRegistration extends AbstractContributorRegistration {
	protected byte getContributorType() {
		return Contributor.IMAGES_CONTRIBUTOR;
	}
	
	protected String getConfirmationPageName() {
		return "Confirmation";
	}
	
	protected Object getExistingContributorPageDestination() {
	    return "ImagesManager";
	}
	protected byte getUnapprovedContributorType() {
		return Contributor.ANY_CONTRIBUTOR;
	}
	
	/**
	 * change the default license for image/media contributors to be the DEFAULT LICENSE defined by 
	 * the ContributorLicenseInfo class. 
	 */
	@Override
	protected void doAdditionalProcessing() {
		Contributor mediaContr = getEditedContributor();
		ContributorLicenseInfo lic = new ContributorLicenseInfo(ContributorLicenseInfo.LICENSE_DEFAULT);
		mediaContr.setImageUseDefault((byte)lic.produceNewLicenseCode());
		super.doAdditionalProcessing();
	}
}
