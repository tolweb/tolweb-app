package org.tolweb.content.licensing;

import java.util.HashMap;

import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.UsePermissable;

public abstract class AbstractLicenseCriteria implements LicenseCriteria {

	protected HashMap<Byte, Byte> validLicenses;
	
	public AbstractLicenseCriteria() {
		validLicenses = new HashMap<Byte, Byte>();
	}
	
	protected void addToValidLicenses(int[] licenses) {
		for (int i = 0; i < licenses.length; i++) {
			Byte licCode = Byte.valueOf((byte)licenses[i]);
			validLicenses.put(licCode, licCode);
		}
	}	
	
	public boolean matchesCriteria(byte licenseCode) {
		Byte licCode = Byte.valueOf(licenseCode);
		return validLicenses.containsKey(licCode);
	}
	public boolean matchesCriteria(ContributorLicenseInfo contrLicInfo) {
		return matchesCriteria((byte)contrLicInfo.getLicenseCode());
	}
	public boolean matchesCriteria(UsePermissable content) {
		return matchesCriteria(content.getUsePermission());
	}
}
