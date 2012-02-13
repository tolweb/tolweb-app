package org.tolweb.misc;

import org.tolweb.content.licensing.ContentLicenseClass;
import org.tolweb.treegrow.main.NodeImage;

import junit.framework.TestCase;

public class ContributorLicenseInfoTest extends TestCase {

	public void test_license_request_properly_excludes_tol_use() {
		ContributorLicenseInfo licInfo = new ContributorLicenseInfo(NodeImage.TOL_USE);
		ContentLicenseClass nc = ContentLicenseClass.NonCommercialWithoutModification;
		boolean verdict = licInfo.matchesContentLicenseClass(nc);
		assertFalse(verdict);
	}
}
