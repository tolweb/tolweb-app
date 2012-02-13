package org.tolweb.content.licensing;

import java.util.HashMap;


public class LicenseCriteriaFactory {
	private static HashMap<ContentLicenseClass, LicenseCriteria> licenseClassToCriteria;
	
	static {
		licenseClassToCriteria = new HashMap<ContentLicenseClass, LicenseCriteria>();
		licenseClassToCriteria.put(CommercialWithModificationLicenseCriteria.LicenseClass, new CommercialWithModificationLicenseCriteria());
		licenseClassToCriteria.put(CommercialWithoutModificationLicenseCriteria.LicenseClass, new CommercialWithoutModificationLicenseCriteria());
		licenseClassToCriteria.put(NonCommercialWithModificationLicenseCriteria.LicenseClass, new NonCommercialWithModificationLicenseCriteria());
		licenseClassToCriteria.put(NonCommercialWithoutModificationLicenseCriteria.LicenseClass, new NonCommercialWithoutModificationLicenseCriteria());
	}
	
	public static LicenseCriteria getLicenseCriteria(ContentLicenseClass requestLicenseClass) {
		return licenseClassToCriteria.get(requestLicenseClass);
	}
}
