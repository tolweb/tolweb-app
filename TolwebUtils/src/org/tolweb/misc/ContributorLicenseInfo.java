package org.tolweb.misc;

import org.tolweb.content.licensing.ContentLicenseClass;
import org.tolweb.treegrow.main.NodeImage;
import java.util.Arrays;
import java.util.List;

/*
 * Developer Notes: 
 */

/**
 * 
 * @author lenards
 */
public class ContributorLicenseInfo {

	public static final String[] LICENSE_NAMES = {
		"Public Domain", "Tree of Life use only", "Tree of Life & Partners uses only", "Restricted use"
	};
	
	public static final String[] CREATIVE_COMMONS_LICENSE_NAMES = {
		/* 0 */ "Attribution License", 
		/* 1 */ "Attribution-NonCommercial License", 
		/* 2 */ "Attribution-NoDerivs License", 
		/* 3 */ "Attribution-ShareAlike License", 
		/* 4 */ "Attribution-NonCommercial-ShareAlike License", 
		/* 5 */ "Attribution-NonCommercial-NoDerivs License"
	};

	public static final String[] CREATIVE_COMMONS_LICENSE_VERSIONS = {
		"Version 1.0", "Version 2.0", "Version 2.5", "Version 3.0"
	};
	
	public static final String[] MEDIA_MODIFICATION_CHOICES = {	
		"Minor Modification Only", "Modification Permitted"		
	};
	
	
	public static final int[] CREATIVE_COMMONS_LICENSE_BY = {
		// *NOTE* The order is defined by the NodeImage constants values such that this array is in sorted order
		NodeImage.CC_BY20, NodeImage.CC_BY25, NodeImage.CC_BY30, NodeImage.CC_BY10
	};

	public static final int[] CREATIVE_COMMONS_LICENSE_BY_NC = {
		// *NOTE* The order is defined by the NodeImage constants values such that this array is in sorted order		
		NodeImage.CC_BY_NC20, NodeImage.CC_BY_NC25, NodeImage.CC_BY_NC30, NodeImage.CC_BY_NC10
	};	
	
	public static final int[] CREATIVE_COMMONS_LICENSE_BY_ND = {
		// *NOTE* The order is defined by the NodeImage constants values such that this array is in sorted order		
		NodeImage.CC_BY_ND20, NodeImage.CC_BY_ND25, NodeImage.CC_BY_ND30, NodeImage.CC_BY_ND10
	};
	
	public static final int[] CREATIVE_COMMONS_LICENSE_BY_SA = {
		// *NOTE* The order is defined by the NodeImage constants values such that this array is in sorted order		
		NodeImage.CC_BY_SA20, NodeImage.CC_BY_SA25, NodeImage.CC_BY_SA30, NodeImage.CC_BY_SA10
	};
	
	public static final int[] CREATIVE_COMMONS_LICENSE_BY_NC_ND = {
		// *NOTE* The order is defined by the NodeImage constants values such that this array is in sorted order		
		NodeImage.CC_BY_NC_ND20, NodeImage.CC_BY_NC_ND25, NodeImage.CC_BY_NC_ND30, NodeImage.CC_BY_NC_ND10
	};
	
	public static final int[] CREATIVE_COMMONS_LICENSE_BY_NC_SA = {
		// *NOTE* The order is defined by the NodeImage constants values such that this array is in sorted order		
		NodeImage.CC_BY_NC_SA20, NodeImage.CC_BY_NC_SA25, NodeImage.CC_BY_NC_SA30, NodeImage.CC_BY_NC_SA10
	};
	
	public static final int[] PUBLIC_DOMAIN_LICENSES = {
		NodeImage.ALL_USES
	};
	/**
	 * Defines the license default - it is an index into the CREATIVE_COMMONS_LICENSE_NAMES array
	 */
	public static final int CREATIVE_COMMONS_LICENSE_NAME_DEFAULT = 1;
	
	/**
	 * Defines the version default - it is an index into the CREATIVE_COMMONS_LICENSE_VERSIONS array
	 */
	public static final int CREATIVE_COMMONS_LICENSE_VERSION_DEFAULT = 3;

	public static final byte LICENSE_DEFAULT = NodeImage.CC_BY_NC30;
	public static final byte TREE_IMAGE_LICENSE = NodeImage.CC_BY30;
	
	public static final int CC_FIRST_LIC_CODE = NodeImage.CC_BY20;
	public static final int CC_LAST_LIC_CODE = NodeImage.CC_BY_NC_ND10;
	
	/**
	 * This value is used by UI and is not a valid Public Domain license code
	 */
	public static final byte PUBLIC_DOMAIN = 0;
	public static final byte TOL_USE = 1;
	public static final byte CREATIVE_COMMONS = 2; 
	public static final byte TOL_PARTNER_USE = 3;
	public static final byte ONE_TIME_USE = 4;
	
	public static final String CC_DISPLAY = "Creative Commons ";
	public static final String CC_URL_PREFIX = "http://creativecommons.org/licenses/";
	public static final String CC_PUB_DOMAIN = "http://creativecommons.org/licenses/publicdomain";
	public static final String TOL_URL = "/tree/home.pages/toluse.html";
	private int licenseCode;
	private byte licenseType; 
	private String licenseName;
	private String licenseVersion;
	private String modificationChoice;
	private boolean modificationPermitted;
	private boolean eolCompatible;
	
	public ContributorLicenseInfo(int licenseCode) {
		this(licenseCode, false);
	}
	
	public ContributorLicenseInfo(int licenseCode, boolean modPermitted) {
		setLicenseCode(licenseCode);
		setLicenseType(determineLicenseType());
		setLicenseName(determineLicenseName());
		setLicenseVersion(determineLicenseVersion());
		setEolCompatible(determineEolCompatibility());
		setModificationPermitted(modPermitted);
		setModificationChoice(determineModificationChoice());	
	}
	// "Public Domain", "Tree of Life use only", "Tree of Life & Partners uses only", "Restricted use"
	public int produceNewLicenseCode() {
		List<String> ccNames = Arrays.asList(CREATIVE_COMMONS_LICENSE_NAMES);
		if (ccNames.contains(getLicenseName())) {
			return produceCreativeCommonsLicenseCode();
		} else if (getLicenseName().equals(LICENSE_NAMES[0])) {
			return NodeImage.ALL_USES;
		} else if (getLicenseName().equals(LICENSE_NAMES[1])) {
			return NodeImage.TOL_USE;
		} else if (getLicenseName().equals(LICENSE_NAMES[2])) {
			return NodeImage.EVERYWHERE_USE;
		} else if (getLicenseName().equals(LICENSE_NAMES[3])) {
			return NodeImage.RESTRICTED_USE;
		} else {
			throw new IllegalArgumentException("the license name, license version, etc. values are such that they do not correspond to a valid license code");
		}
	}
	
	public boolean matchesContentLicenseClass(ContentLicenseClass licClass) {
		switch(licClass) {
			case NonCommercialWithModification:
				return matchesNoncommercialWithModContentLicenseClass();
			case NonCommercialWithoutModification:
				return matchesNoncommercialWithoutModContentLicenseClass();
			case CommercialWithModification:
				return matchesCommercialWithModContentLicenseClass();
			case CommercialWithoutModification:
				return matchesCommercialWithoutModContentLicenseClass();
			default: 
				return false;
		}
		
	}
	
	public ContentLicenseClass getContentLicenseClass() {
		if (matchesNoncommercialWithModContentLicenseClass()) {
			return ContentLicenseClass.NonCommercialWithModification;
		} else if (matchesNoncommercialWithoutModContentLicenseClass()) {
			return ContentLicenseClass.NonCommercialWithoutModification;
		} else if (matchesCommercialWithModContentLicenseClass()) {
			return ContentLicenseClass.CommercialWithModification;
		} else if (matchesCommercialWithModContentLicenseClass()) {
			return ContentLicenseClass.CommercialWithoutModification;
		} else {
			return null;
		}
	}
	
	private boolean matchesNoncommercialWithModContentLicenseClass() {
		return isPublicDomain() || 
				isCreativeCommonsBy(getLicenseCode()) ||
				isCreativeCommonsBySa(getLicenseCode()) || 
				isCreativeCommonsByNc(getLicenseCode()) || 
				isCreativeCommonsByNcSa(getLicenseCode());
	}

	private boolean matchesNoncommercialWithoutModContentLicenseClass() {
		return matchesNoncommercialWithModContentLicenseClass() || 
				isCreativeCommonsByNd(getLicenseCode()) ||
				isCreativeCommonsByNcNd(getLicenseCode());
	}	
	
	private boolean matchesCommercialWithModContentLicenseClass() {
		return isPublicDomain() || 
				isCreativeCommonsBy(getLicenseCode()) ||
				isCreativeCommonsBySa(getLicenseCode());
	}

	private boolean matchesCommercialWithoutModContentLicenseClass() {
		return matchesCommercialWithModContentLicenseClass() || 
				isCreativeCommonsByNd(getLicenseCode());
	}
	
	public static String getNonCCLicenseName(byte licType) {
		if (licType == PUBLIC_DOMAIN) {
			return LICENSE_NAMES[0];
		} else if (licType == TOL_USE) {
			return LICENSE_NAMES[1];
		} else if (licType == TOL_PARTNER_USE) {
			return LICENSE_NAMES[2];
		} else if (licType == ONE_TIME_USE) {
			return LICENSE_NAMES[3];
		} else {
			return "";
		}
	}
	
	private int produceCreativeCommonsLicenseCode() {
		List<String> names = Arrays.asList(CREATIVE_COMMONS_LICENSE_NAMES);
		int index = names.indexOf(getLicenseName());
		int firstLic;
		if (getLicenseVersion().equals(CREATIVE_COMMONS_LICENSE_VERSIONS[0])) {
			firstLic = NodeImage.CC_BY10;
		} else if (getLicenseVersion().equals(CREATIVE_COMMONS_LICENSE_VERSIONS[1])) {
			firstLic = NodeImage.CC_BY20;
		} else if (getLicenseVersion().equals(CREATIVE_COMMONS_LICENSE_VERSIONS[2])) {
			firstLic = NodeImage.CC_BY25;
		} else if (getLicenseVersion().equals(CREATIVE_COMMONS_LICENSE_VERSIONS[3])) {
			firstLic = NodeImage.CC_BY30;
		} else {
			throw new IllegalArgumentException("value for getLicenseVersion() is not defined and therefore not valid");
		}
		return firstLic + index;
	}
	private String determineModificationChoice() {
		if (isToLRelatedLicenseType(getLicenseType())) {
			if (isModificationPermitted()) {
				return MEDIA_MODIFICATION_CHOICES[1];
			} else {
				return MEDIA_MODIFICATION_CHOICES[0];
			}
		} else if (isPublicDomain()) {
			return MEDIA_MODIFICATION_CHOICES[1];
		} else {
			return "";
		}
	}
	
	private boolean determineEolCompatibility() {
		//public domain, CC-BY, CC-BY-NC, CC-BY-SA, or CC-BY-NC-SA
/*		boolean isCCBy = isCreativeCommonsBy(licenseCode);
		boolean isCCByNc = isCreativeCommonsByNc(licenseCode);
		boolean isCCBySa = isCreativeCommonsBySa(licenseCode);
		boolean isCCByNcSa = isCreativeCommonsByNcSa(licenseCode);
		return isCCBy || isCCByNc || isCCBySa || isCCByNcSa || isPublicDomain(); */
		return isCreativeCommonsBy(licenseCode) || isCreativeCommonsByNc(licenseCode) || 
			isCreativeCommonsBySa(licenseCode) || isCreativeCommonsByNcSa(licenseCode) || 
			isPublicDomain();
	}
	
	private String determineLicenseName() {
		if (licenseCode >= CC_FIRST_LIC_CODE && licenseCode <= CC_LAST_LIC_CODE) {
			if (isCreativeCommonsByNc(licenseCode)) {
				return CREATIVE_COMMONS_LICENSE_NAMES[1];
			} else if (isCreativeCommonsByNd(licenseCode)) {
				return CREATIVE_COMMONS_LICENSE_NAMES[2];
			} else if (isCreativeCommonsBySa(licenseCode)) { 
				return CREATIVE_COMMONS_LICENSE_NAMES[3];
			} else if (isCreativeCommonsByNcSa(licenseCode)) { 
				return CREATIVE_COMMONS_LICENSE_NAMES[4];
			} else if (isCreativeCommonsByNcNd(licenseCode)) { 
				return CREATIVE_COMMONS_LICENSE_NAMES[5];
			} else {
				return CREATIVE_COMMONS_LICENSE_NAMES[0];
			}
		} else if (licenseCode == NodeImage.TOL_USE) {
				return LICENSE_NAMES[1];
		} else if (licenseCode == NodeImage.EVERYWHERE_USE) {
				return LICENSE_NAMES[2];
		} else if (licenseCode == NodeImage.RESTRICTED_USE) {
				return LICENSE_NAMES[3];
		} else if (licenseCode == NodeImage.ALL_USES) {
			return LICENSE_NAMES[0];
		} else {
			throw new IllegalArgumentException("license code is not defined");
		}
	}
	
	private String determineLicenseVersion() {
		if (licenseCode >= NodeImage.CC_BY20 && licenseCode <= NodeImage.CC_BY_NC_ND20) {
			return CREATIVE_COMMONS_LICENSE_VERSIONS[1];
		} else if(licenseCode >= NodeImage.CC_BY25 && licenseCode <= NodeImage.CC_BY_NC_ND25) {
			return CREATIVE_COMMONS_LICENSE_VERSIONS[2];
		} else if(licenseCode >= NodeImage.CC_BY30 && licenseCode <= NodeImage.CC_BY_NC_ND30) {
			return CREATIVE_COMMONS_LICENSE_VERSIONS[3];
		} else {
			return CREATIVE_COMMONS_LICENSE_VERSIONS[0];
		}
	}
	
	public boolean isPublicDomain() {
		return licenseType == PUBLIC_DOMAIN;
	}
	
	public boolean isToLRelated() {
		return licenseType == TOL_USE || licenseType == TOL_PARTNER_USE || licenseType == ONE_TIME_USE;
	}
	
	public static boolean isToLRelatedLicenseType(int licType) {
		return licType == TOL_USE || licType == TOL_PARTNER_USE || licType == ONE_TIME_USE;
	}
	
	public static boolean isToLRelatedLicenseCode(int licCode) {
		return licCode == NodeImage.TOL_USE || licCode == NodeImage.RESTRICTED_USE || licCode == NodeImage.EVERYWHERE_USE;
	}	

	public static boolean isPublicDomainCode(int licCode) {
		return licCode == NodeImage.ALL_USES;
	}
	
	public static boolean isCreativeCommons(int licCode) {
		return !isToLRelatedLicenseCode(licCode) && !isPublicDomainCode(licCode);
	}
	
	public boolean isCreativeCommons() {
		return licenseType == CREATIVE_COMMONS;
	}
	
	public static boolean isCreativeCommonsBy(int licCode) {
		return Arrays.binarySearch(CREATIVE_COMMONS_LICENSE_BY, licCode) >= 0;
	}
	
	public static boolean isCreativeCommonsByNc(int licCode) {
		return Arrays.binarySearch(CREATIVE_COMMONS_LICENSE_BY_NC, licCode) >= 0;
	}

	public static boolean isCreativeCommonsByNd(int licCode) {
		return Arrays.binarySearch(CREATIVE_COMMONS_LICENSE_BY_ND, licCode) >= 0;
	}	

	public static boolean isCreativeCommonsBySa(int licCode) {
		return Arrays.binarySearch(CREATIVE_COMMONS_LICENSE_BY_SA, licCode) >= 0;
	}

	public static boolean isCreativeCommonsByNcNd(int licCode) {
		return Arrays.binarySearch(CREATIVE_COMMONS_LICENSE_BY_NC_ND, licCode) >= 0;
	}
	
	public static boolean isCreativeCommonsByNcSa(int licCode) {
		return Arrays.binarySearch(CREATIVE_COMMONS_LICENSE_BY_NC_SA, licCode) >= 0;
	}
	
	private byte determineLicenseType() {
		if (licenseCode < 0) {
			return PUBLIC_DOMAIN; 
		} else if (licenseCode < CC_FIRST_LIC_CODE) {
			if (licenseCode == NodeImage.TOL_USE) {
				return TOL_USE;
			} else if (licenseCode == NodeImage.RESTRICTED_USE) {
				return ONE_TIME_USE;
			} else {
				return TOL_PARTNER_USE;
			} 
		} else {
			return CREATIVE_COMMONS;
		} 
	}
	
	public static String ccLicenseLinkSuffix(int licCode) {
		if (isCreativeCommonsByNc(licCode)) {
			return "by-nc";
		} else if (isCreativeCommonsBySa(licCode)) {
			return "by-sa";
		} else if (isCreativeCommonsByNd(licCode)) {
			return "by-nd";
		} else if (isCreativeCommonsByNcSa(licCode)) {
			return "by-nc-sa";
		} else if (isCreativeCommonsByNcNd(licCode)) {
			return "by-nc-nd";
		} else {
			return "by";
		}
	}
	
	public static String linkString(ContributorLicenseInfo licInfo) {
		if (licInfo.isCreativeCommons()) {
			String ccLicenseLink = ccLicenseLinkSuffix(licInfo.getLicenseCode());
			String[] pieces = licInfo.getLicenseVersion().split(" ");
			return CC_URL_PREFIX + ccLicenseLink + "/" + pieces[1] + "/";
		} else if (licInfo.isToLRelated() || licInfo.isPublicDomain()) {
			return TOL_URL;
		}
		return "";
	}
	
	public String toString() {
		String tmp = getLicenseName();
		if (isCreativeCommons()) {
			tmp = CC_DISPLAY + tmp;
		}
		return tmp;
	}
	
	private String getShortVersionString(String version) {
		String[] pieces = version.split(" ");
		return pieces.length == 2 ? pieces[1] : "";
	}
	
	public String toShortString() {
		if (isCreativeCommons()) {
			return "cc-" + ccLicenseLinkSuffix(getLicenseCode()) + " " + getShortVersionString(getLicenseVersion());
		} else if (isPublicDomain()) {
			return "public domain";
		}
		return "all rights reserved";
	}
	
	public boolean isEolCompatible() {
		return eolCompatible;
	}
	public void setEolCompatible(boolean eolCompatible) {
		this.eolCompatible = eolCompatible;
	}
	public int getLicenseCode() {
		return licenseCode;
	}
	public void setLicenseCode(int licenseCode) {
		this.licenseCode = licenseCode;
	}
	public String getLicenseName() {
		return licenseName;
	}
	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}
	public byte getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(byte licenseType) {
		this.licenseType = licenseType;
	}
	public String getLicenseVersion() {
		return licenseVersion;
	}
	public void setLicenseVersion(String licenseVersion) {
		this.licenseVersion = licenseVersion;
	}
	public String getModificationChoice() {
		return modificationChoice;
	}
	public void setModificationChoice(String modificationChoice) {
		this.modificationChoice = modificationChoice;
	}

	public boolean isModificationPermitted() {
		return modificationPermitted;
	}

	public void setModificationPermitted(boolean modificationPermitted) {
		this.modificationPermitted = modificationPermitted;
		setModificationChoice(determineModificationChoice());
	}
}