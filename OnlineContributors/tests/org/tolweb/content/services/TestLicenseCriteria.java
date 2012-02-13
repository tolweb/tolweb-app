package org.tolweb.content.services;

import org.tolweb.content.licensing.CommercialWithModificationLicenseCriteria;
import org.tolweb.content.licensing.CommercialWithoutModificationLicenseCriteria;
import org.tolweb.content.licensing.ContentLicenseClass;
import org.tolweb.content.licensing.LicenseCriteria;
import org.tolweb.content.licensing.LicenseCriteriaFactory;
import org.tolweb.content.licensing.NonCommercialWithModificationLicenseCriteria;
import org.tolweb.content.licensing.NonCommercialWithoutModificationLicenseCriteria;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.NodeImage;

public class TestLicenseCriteria extends ApplicationContextTestAbstract {

	public TestLicenseCriteria(String name) {
		super(name);
	}
	
	public void testLicenseCriteriaFactory() {
		LicenseCriteria lc; 
		
		lc = LicenseCriteriaFactory.getLicenseCriteria(ContentLicenseClass.CommercialWithModification);
		assertTrue(CommercialWithModificationLicenseCriteria.class.isInstance(lc));
		lc = LicenseCriteriaFactory.getLicenseCriteria(ContentLicenseClass.CommercialWithoutModification);
		assertTrue(CommercialWithoutModificationLicenseCriteria.class.isInstance(lc));
		lc = LicenseCriteriaFactory.getLicenseCriteria(ContentLicenseClass.NonCommercialWithModification);
		assertTrue(NonCommercialWithModificationLicenseCriteria.class.isInstance(lc));
		lc = LicenseCriteriaFactory.getLicenseCriteria(ContentLicenseClass.NonCommercialWithoutModification);
		assertTrue(NonCommercialWithoutModificationLicenseCriteria.class.isInstance(lc));
	}
	
	public void testCommercialWithModLicenseCriteria() {
		LicenseCriteria criteria = LicenseCriteriaFactory.getLicenseCriteria(ContentLicenseClass.CommercialWithModification);
		assertTrue(criteria.matchesCriteria(NodeImage.ALL_USES));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY30));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA30));
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_ND30));
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_NC30));
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_NC_ND30));
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA30));
		assertFalse(criteria.matchesCriteria(NodeImage.RESTRICTED_USE));
		assertFalse(criteria.matchesCriteria(NodeImage.EVERYWHERE_USE));
		assertFalse(criteria.matchesCriteria(NodeImage.TOL_USE));
	}
	
	public void testCommercialWithoutModLicenseCriteria() {
		LicenseCriteria criteria = LicenseCriteriaFactory.getLicenseCriteria(ContentLicenseClass.CommercialWithoutModification);
		assertTrue(criteria.matchesCriteria(NodeImage.ALL_USES));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY30));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA30));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_ND10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_ND20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_ND25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_ND30));
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_NC30));
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_NC_ND30));
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA30));
		assertFalse(criteria.matchesCriteria(NodeImage.RESTRICTED_USE));
		assertFalse(criteria.matchesCriteria(NodeImage.EVERYWHERE_USE));
		assertFalse(criteria.matchesCriteria(NodeImage.TOL_USE));		
	}
	
	public void testNonCommercialWithModLicenseCriteria() {
		LicenseCriteria criteria = LicenseCriteriaFactory.getLicenseCriteria(ContentLicenseClass.NonCommercialWithModification);
		assertTrue(criteria.matchesCriteria(NodeImage.ALL_USES));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY30));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA30));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC30));		
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA30));		
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_ND30));
		assertFalse(criteria.matchesCriteria(NodeImage.CC_BY_NC_ND30));
		assertFalse(criteria.matchesCriteria(NodeImage.RESTRICTED_USE));
		assertFalse(criteria.matchesCriteria(NodeImage.EVERYWHERE_USE));
		assertFalse(criteria.matchesCriteria(NodeImage.TOL_USE));
	}
	
	public void testNonCommercialWithoutModLicenseCriteria() {
		LicenseCriteria criteria = LicenseCriteriaFactory.getLicenseCriteria(ContentLicenseClass.NonCommercialWithoutModification);
		assertTrue(criteria.matchesCriteria(NodeImage.ALL_USES));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY30));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_SA30));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC30));		
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_SA30));		
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_ND10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_ND20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_ND25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_ND30));		
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_ND10));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_ND20));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_ND25));
		assertTrue(criteria.matchesCriteria(NodeImage.CC_BY_NC_ND30));		
		assertFalse(criteria.matchesCriteria(NodeImage.RESTRICTED_USE));
		assertFalse(criteria.matchesCriteria(NodeImage.EVERYWHERE_USE));
		assertFalse(criteria.matchesCriteria(NodeImage.TOL_USE));
	}	
}
