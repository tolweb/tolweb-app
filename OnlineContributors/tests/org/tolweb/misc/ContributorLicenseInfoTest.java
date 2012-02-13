package org.tolweb.misc;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.NodeImage;

/* use_permission
 * -1 public domain, 0  Restricted, 1  ToL use only, 2  Share with ToL partners
 * 3  by/2.0, 4  by-nc/2.0, 5  by-nd/2.0, 6  by-sa/2.0, 7  by-nc-sa/2.0, 8  by-nc-nd/2.0
 * 9  by/2.5, 10  by-nc/2.5, 11  by-nd/2.5, 12  by-sa/2.5, 13  by-nc-sa/2.5, 14  by-nc-nd/2.5 */	


public class ContributorLicenseInfoTest extends ApplicationContextTestAbstract {
	
	public ContributorLicenseInfoTest(String name) {
		super(name);
	}

	public void testCCLicenseSuffixProcessing() {
		String tmp = ContributorLicenseInfo.ccLicenseLinkSuffix(NodeImage.CC_BY_NC20);
		assertEquals(tmp, "by-nc");
		tmp = ContributorLicenseInfo.ccLicenseLinkSuffix(NodeImage.CC_BY_SA20);
		assertEquals(tmp, "by-sa");
	}
	
	public void testShortString() {
		String tmp = new ContributorLicenseInfo(NodeImage.CC_BY_NC20).toShortString();
		System.out.println(tmp);
		assertEquals(tmp, "cc-by-nc 2.0");
		tmp = new ContributorLicenseInfo(NodeImage.CC_BY_SA30).toShortString();
		assertEquals(tmp, "cc-by-sa 3.0");
		tmp = new ContributorLicenseInfo(NodeImage.ALL_USES).toShortString();
		assertEquals(tmp, "public domain");
		tmp = new ContributorLicenseInfo(NodeImage.TOL_USE).toShortString();
		assertEquals(tmp, "all rights reserved");		
	}
	
	public void testNonCCLicenses() {
		tToLUseLicCode();
		tPublicDomainLicCode();
		tRestrictedLicCode();
	}
	
	public void testCCVersion10() {
		tCC10ByLicCode();
		tCC10ByNcLicCode();
		tCC10ByNdLicCode();
		tCC10BySaLicCode();
		tCC10ByNcSaLicCode();
		tCC10ByNcNdLicCode();
	}

	public void testCCVersion20() {
		tCC20ByLicCode();
		tCC20ByNcLicCode();
		tCC20ByNdLicCode();
		tCC20BySaLicCode();
		tCC20ByNcSaLicCode();
		tCC20ByNcNdLicCode();
	}
	
	public void testCCVersion25() {
		tCC25ByLicCode();
		tCC25ByNcLicCode();
		tCC25ByNdLicCode();
		tCC25BySaLicCode();
		tCC25ByNcSaLicCode();
		tCC25ByNcNdLicCode();
	}

	public void testCCVersion30() {
		tCC30ByLicCode();
		tCC30ByNcLicCode();
		tCC30ByNdLicCode();
		tCC30BySaLicCode();
		tCC30ByNcSaLicCode();
		tCC30ByNcNdLicCode();
	}
	
	public void testGenerateNonLicenseCodes() {
		tToLUseGenCode();
		tPubDomainGenCode();
		tToLPartnerUseGenCode();
		tRestrictedGenCode();
		tFailedGenCode();
	}

	public void testGenerateCC10LicenseCodes() {
		tCCByGenCode("Version 1.0", NodeImage.CC_BY10);
		tCCByNcGenCode("Version 1.0", NodeImage.CC_BY_NC10);
		tCCByNdGenCode("Version 1.0", NodeImage.CC_BY_ND10);
		tCCBySaGenCode("Version 1.0", NodeImage.CC_BY_SA10);
		tCCByNcSaGenCode("Version 1.0", NodeImage.CC_BY_NC_SA10);
		tCCByNcNdGenCode("Version 1.0", NodeImage.CC_BY_NC_ND10);
		tCCByFailVersion("Version -1.0", "Attribution License", NodeImage.CC_BY10);
		tCCByFailVersion("Version 1.0", "Invalid license name", NodeImage.CC_BY10);		
	}

	public void testGenerateCC20LicenseCodes() {
		tCCByGenCode("Version 2.0", NodeImage.CC_BY20);
		tCCByNcGenCode("Version 2.0", NodeImage.CC_BY_NC20);
		tCCByNdGenCode("Version 2.0", NodeImage.CC_BY_ND20);
		tCCBySaGenCode("Version 2.0", NodeImage.CC_BY_SA20);
		tCCByNcSaGenCode("Version 2.0", NodeImage.CC_BY_NC_SA20);
		tCCByNcNdGenCode("Version 2.0", NodeImage.CC_BY_NC_ND20);
		tCCByFailVersion("Version -1.0", "Attribution License", NodeImage.CC_BY20);
		tCCByFailVersion("Version 2.0", "Invalid license name", NodeImage.CC_BY20);		
	}

	public void testGenerateCC25LicenseCodes() {
		tCCByGenCode("Version 2.5", NodeImage.CC_BY25);
		tCCByNcGenCode("Version 2.5", NodeImage.CC_BY_NC25);
		tCCByNdGenCode("Version 2.5", NodeImage.CC_BY_ND25);
		tCCBySaGenCode("Version 2.5", NodeImage.CC_BY_SA25);
		tCCByNcSaGenCode("Version 2.5", NodeImage.CC_BY_NC_SA25);
		tCCByNcNdGenCode("Version 2.5", NodeImage.CC_BY_NC_ND25);
		tCCByFailVersion("Version -1.0", "Attribution License", NodeImage.CC_BY25);
		tCCByFailVersion("Version 2.5", "Invalid license name", NodeImage.CC_BY25);		
	}
	
	public void testGenerateCC30LicenseCodes() {
		tCCByGenCode("Version 3.0", NodeImage.CC_BY30);
		tCCByNcGenCode("Version 3.0", NodeImage.CC_BY_NC30);
		tCCByNdGenCode("Version 3.0", NodeImage.CC_BY_ND30);
		tCCBySaGenCode("Version 3.0", NodeImage.CC_BY_SA30);
		tCCByNcSaGenCode("Version 3.0", NodeImage.CC_BY_NC_SA30);
		tCCByNcNdGenCode("Version 3.0", NodeImage.CC_BY_NC_ND30);
		tCCByFailVersion("Version -1.0", "Attribution License", NodeImage.CC_BY30);
		tCCByFailVersion("Version 3.0", "Invalid license name", NodeImage.CC_BY30);
	}
	
	public void tToLUseGenCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		cli.setLicenseName("Tree of Life use only");
		int code = cli.produceNewLicenseCode();
		assertEquals(code, NodeImage.TOL_USE);
	}

	public void tToLPartnerUseGenCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		cli.setLicenseName("Tree of Life & Partners uses only");
		int code = cli.produceNewLicenseCode();
		assertEquals(code, NodeImage.EVERYWHERE_USE);
	}
	
	public void tPubDomainGenCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(1, true);
		cli.setLicenseName("Public Domain");
		int code = cli.produceNewLicenseCode();
		assertEquals(code, NodeImage.ALL_USES);
	}	

	public void tRestrictedGenCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(1, true);
		cli.setLicenseName("Restricted use");
		int code = cli.produceNewLicenseCode();
		assertEquals(code, NodeImage.RESTRICTED_USE);
	}	
	
	public void tFailedGenCode() {
		try {
			ContributorLicenseInfo cli = new ContributorLicenseInfo(1, true);
			cli.setLicenseName("Totally bogus license name....");
			int code = cli.produceNewLicenseCode();
			System.out.println("Code: " + code);
			fail();
		} catch (IllegalArgumentException iae) {
			System.out.println(iae);
		}
	}
	
	public void tCCByGenCode(String version, int ccCode) {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		cli.setLicenseName("Attribution License");
		cli.setLicenseVersion(version);
		int code = cli.produceNewLicenseCode();
		assertEquals(code, ccCode);
	}

	public void tCCByNcGenCode(String version, int ccCode) {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		cli.setLicenseName("Attribution-NonCommercial License");
		cli.setLicenseVersion(version);
		int code = cli.produceNewLicenseCode();
		assertEquals(code, ccCode);
	}
	
	public void tCCByNdGenCode(String version, int ccCode) {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		cli.setLicenseName("Attribution-NoDerivs License");
		cli.setLicenseVersion(version);
		int code = cli.produceNewLicenseCode();
		assertEquals(code, ccCode);
	}
	
	public void tCCBySaGenCode(String version, int ccCode) {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		cli.setLicenseName("Attribution-ShareAlike License");
		cli.setLicenseVersion(version);
		int code = cli.produceNewLicenseCode();
		assertEquals(code, ccCode);
	}

	public void tCCByNcSaGenCode(String version, int ccCode) {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		cli.setLicenseName("Attribution-NonCommercial-ShareAlike License");
		cli.setLicenseVersion(version);
		int code = cli.produceNewLicenseCode();
		assertEquals(code, ccCode);
	}
	
	public void tCCByNcNdGenCode(String version, int ccCode) {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		cli.setLicenseName("Attribution-NonCommercial-NoDerivs License");
		cli.setLicenseVersion(version);
		int code = cli.produceNewLicenseCode();
		assertEquals(code, ccCode);
	}
	
	public void tCCByFailVersion(String version, String name, int ccCode) {
		try {
			ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
			cli.setLicenseName(name);
			cli.setLicenseVersion(version);
			int code = cli.produceNewLicenseCode();
			System.out.println("Code: " + code);
			fail();
		} catch (IllegalArgumentException iae) {
			System.out.println(iae);
		}
	}
	
	public void tToLUseLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(1, true);
		assertEquals(cli.getLicenseName(), "Tree of Life use only");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.TOL_USE);
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "Modification Permitted");
		assertEquals(cli.isModificationPermitted(), true);
		cli.setModificationPermitted(false);
		assertEquals(cli.getModificationChoice(), "Minor Modification Only");
		assertEquals(cli.isEolCompatible(), false);
	}
	
	public void tPublicDomainLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(-1, true);
		assertEquals(cli.getLicenseName(), "Public Domain");
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "Modification Permitted");
		assertEquals(cli.isModificationPermitted(), true);
		assertEquals(cli.isEolCompatible(), true);
	}
	
	public void tRestrictedLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(0);
		assertEquals(cli.getLicenseName(), "Restricted use");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.ONE_TIME_USE);
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "Minor Modification Only");
		assertEquals(cli.isModificationPermitted(), false);
		cli.setModificationPermitted(true);
		assertEquals(cli.getModificationChoice(), "Modification Permitted");
		assertEquals(cli.isEolCompatible(), false);
	}
	
	public void tCC10ByLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(21);
		assertEquals(cli.getLicenseName(), "Attribution License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}

	public void tCC10ByNcLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(22);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}

	public void tCC10ByNdLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(23);
		assertEquals(cli.getLicenseName(), "Attribution-NoDerivs License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), false);
	}

	public void tCC10BySaLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(24);
		assertEquals(cli.getLicenseName(), "Attribution-ShareAlike License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);		
	}


	public void tCC10ByNcSaLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(25);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial-ShareAlike License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);		
	}
	
	public void tCC10ByNcNdLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(26);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial-NoDerivs License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 1.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), false);
	}
	
	public void tCC20ByLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(3);
		assertEquals(cli.getLicenseName(), "Attribution License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}	
	
	public void tCC20ByNcLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(4);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}	
	
	public void tCC20ByNdLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(5);
		assertEquals(cli.getLicenseName(), "Attribution-NoDerivs License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), false);
	}

	public void tCC20BySaLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(6);
		assertEquals(cli.getLicenseName(), "Attribution-ShareAlike License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}	
	
	public void tCC20ByNcSaLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(7);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial-ShareAlike License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);		
	}

	public void tCC20ByNcNdLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(8);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial-NoDerivs License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), false);
	}
	
	public void tCC25ByLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(9);
		assertEquals(cli.getLicenseName(), "Attribution License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.5");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}

	public void tCC25ByNcLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(10);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.5");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}

	public void tCC25ByNdLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(11);
		assertEquals(cli.getLicenseName(), "Attribution-NoDerivs License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.5");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), false);
	}

	public void tCC25BySaLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(12);
		assertEquals(cli.getLicenseName(), "Attribution-ShareAlike License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.5");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}	

	public void tCC25ByNcSaLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(13);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial-ShareAlike License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.5");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);		
	}
	
	public void tCC25ByNcNdLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(14);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial-NoDerivs License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 2.5");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), false);
	}
	
	public void tCC30ByLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(15);
		assertEquals(cli.getLicenseName(), "Attribution License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 3.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);		
		assertEquals(cli.isEolCompatible(), true);
	}	

	public void tCC30ByNcLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(16);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 3.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);		
		assertEquals(cli.isEolCompatible(), true);
	}	

	public void tCC30ByNdLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(17);
		assertEquals(cli.getLicenseName(), "Attribution-NoDerivs License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 3.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);		
		assertEquals(cli.isEolCompatible(), false);
	}

	public void tCC30BySaLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(18);
		assertEquals(cli.getLicenseName(), "Attribution-ShareAlike License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 3.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);
	}		

	public void tCC30ByNcSaLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(19);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial-ShareAlike License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 3.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), true);		
	}
	
	public void tCC30ByNcNdLicCode() {
		ContributorLicenseInfo cli = new ContributorLicenseInfo(20);
		assertEquals(cli.getLicenseName(), "Attribution-NonCommercial-NoDerivs License");
		assertEquals(cli.getLicenseType(), ContributorLicenseInfo.CREATIVE_COMMONS);
		assertEquals(cli.getLicenseVersion(), "Version 3.0");
		assertEquals(cli.getModificationChoice(), "");
		assertEquals(cli.isModificationPermitted(), false);
		assertEquals(cli.isEolCompatible(), false);
	}
}