package org.tolweb.content.licensing;

import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.UsePermissable;

public interface LicenseCriteria {
	public boolean matchesCriteria(byte licenseCode);
	public boolean matchesCriteria(ContributorLicenseInfo contrLicInfo);
	public boolean matchesCriteria(UsePermissable content);
}
