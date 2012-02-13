package org.tolweb.content.licensing;

import org.tolweb.misc.ContributorLicenseInfo;

/*
 * The following are valid license classes for this criteria implementation:
 *   > Public Domain (no restrictions on use or redistribution)
 *   > Creative Commons Attribution (or a previous version of this license)
 *   > Creative Commons Attribution-ShareAlike (or a previous version of this license)
 *   > Creative Commons Attribution-NonCommercial (or a previous version of this license)
 *   > Creative Commons Attribution-NonCommercial-ShareAlike (or a previous version of this license)
 */


public class NonCommercialWithModificationLicenseCriteria extends AbstractLicenseCriteria implements LicenseCriteria {
	public static final ContentLicenseClass LicenseClass = ContentLicenseClass.NonCommercialWithModification;
	
	public NonCommercialWithModificationLicenseCriteria() {
		super();
		addToValidLicenses(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_BY);
		addToValidLicenses(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_BY_SA);
		addToValidLicenses(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_BY_NC);
		addToValidLicenses(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_BY_NC_SA);
		addToValidLicenses(ContributorLicenseInfo.PUBLIC_DOMAIN_LICENSES);		
	}
}
