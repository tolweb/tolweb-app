package org.tolweb.content.licensing;

import org.tolweb.misc.ContributorLicenseInfo;

/*
 * The following are valid license classes for this criteria implementation:
 *   > Public Domain (no restrictions on use or redistribution)
 *   > Creative Commons Attribution (or a previous version of this license)
 *   > Creative Commons Attribution-ShareAlike (or a previous version of this license)
 *   > Creative Commons Attribution-NoDerivs (or a previous version of this license)
 */

public class CommercialWithoutModificationLicenseCriteria extends CommercialWithModificationLicenseCriteria  implements LicenseCriteria {
	public static final ContentLicenseClass LicenseClass = ContentLicenseClass.CommercialWithoutModification;
	
	public CommercialWithoutModificationLicenseCriteria() {
		super();
		addToValidLicenses(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_BY_ND);
	}
}
