package org.tolweb.treegrow.main;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

/**
 * 
 * @author lenards
 *
 */
public class ContributorTest extends TestCase {
	private Contributor ctor; 
	
	protected void setUp() throws Exception {
		super.setUp();
		ctor = new Contributor();
	}
	
	// test the expected values for a newly constructed object 
	public void test_expected_values_for_new_instance() {
		assertTrue(ctor.getId() == -1);
		assertTrue("".equals(ctor.getPassword()));
		
		assertNull(ctor.getAdditionalInfo());
		assertNull(ctor.getAddress());
		assertNull(ctor.getAssignmentApproval());
		assertNull(ctor.getBio());
		assertNull(ctor.getCategory());
		assertNull(ctor.getCheckedOutContributorId());
		assertNull(ctor.getCheckoutDate());
		assertNull(ctor.getCheckoutDownloadId());
		assertNull(ctor.getCity());
		assertNull(ctor.getCategory());
		assertNull(ctor.getConfirmationDate());
		assertNull(ctor.getConfirmerContributorId());
		assertNull(ctor.getCoordinationComments());
		assertNull(ctor.getCountry());
		assertNull(ctor.getEditHistoryId());
		assertNull(ctor.getEditingRootNodeId());
		assertNull(ctor.getEmail());
		assertNull(ctor.getFax());
		assertNull(ctor.getFirstName());
		assertNull(ctor.getGeographicAreaInterest());
		assertNull(ctor.getHomepage());
		assertNull(ctor.getImageFilename());
		assertNull(ctor.getImageModificationDefault());
		assertNull(ctor.getImageUseDefault());
		assertNull(ctor.getImageUseLastUpdated());
		assertNull(ctor.getInitials());
		assertNull(ctor.getInstitution());
		assertNull(ctor.getLastName());
		assertNull(ctor.getLicenseReviewerContributorId());
		assertNull(ctor.getNameOrInstitution());
		assertNull(ctor.getNoteModificationDefault());
		assertNull(ctor.getNoteUseDefault());
		assertNull(ctor.getNoteUseLastUpdated());
		assertNull(ctor.getNotes());
		assertNull(ctor.getOtherInterests());
		assertNull(ctor.getPhone());
		assertNull(ctor.getPlans());
		assertNull(ctor.getPublications());
		assertNull(ctor.getQualifications());
		assertNull(ctor.getState());
		assertNull(ctor.getSurname());
		
		assertFalse(ctor.getImageUseLastChanged());
		assertFalse(ctor.getNoteUseLastChanged());
		assertFalse(ctor.getIsAccessoryContributor());
		assertFalse(ctor.getIsImageEditor());
		assertFalse(ctor.getIsLearningEditor());
		assertFalse(ctor.getIsReviewer());

		// This is highly confused
		assertTrue(ctor.getIsApproved());		
		// So, this method compares the contributors "unapproved type" to see if 
		// it's less than zero - which is true when you create a new instance. 
		// You'd expect this:
		// assertFalseTrue(ctor.getIsApproved()); 
		// However, it seems that every contributor is "automatically" thought 
		// to be "approved" in the sense that they're "media contributors", 
		// which doesn't make sense - but the default for a contributor type 
		// is Scientific Core.
		//
		// DEVN - Personally, I don't think this makes any sense at all.  It's a 
		// bad code smell and really should be investigated - but we don't have 
		// the time to make things "right" and such at the moment.  I leave this 
		// comment for someone in the future to know that, lenards, the 3rd ToL 
		// developer did not like or agree with the implementation for the method 
		// getIsApproved() on Contributor.  I believe you need to look at the 
		// CONTRIBUTORS database table to see if there is a default listed and 
		// perhaps this is, thus, a non-issue since Hibernate creates this objects 
		// w/ the defaults of the mapped database table's schema.  A simple Eclipse 
		// CTRL+SHIFT G search may not show all of the uses of getIsApproved() since 
		// the code resides in the TreeGrow project - which for some reason Eclipse 
		// has not indexed properly (as of this comment, Dec 9, 2008) so it does not 
		// find matches in the TreeGrow project all of the time. 
		//
		// Good luck. 

		// first & last names are empty - so this instance would be thought as of an institution 
		assertTrue(ctor.getIsInstitution());
		
		// the contributor type byte is 0, so Scientific Core is the default
		assertEquals("Scientific Core", ctor.getContributorTypeString());
		assertTrue(ctor.getContributorType() == (byte)0);
		assertTrue(ctor.getContributorType() == Contributor.SCIENTIFIC_CONTRIBUTOR);
		// if that's true - then we expect this to be true too
		assertTrue(ctor.getIsCoreScientificContributor());
		// an unapproved contributor is -1, or any contributor
		assertTrue(ctor.getUnapprovedContributorType() == (byte)-1);
		assertTrue(ctor.getUnapprovedContributorType() == Contributor.ANY_CONTRIBUTOR);
		// therefore, the type string for that will be the default, "Media"
		assertEquals("Media", ctor.getUnapprovedContributorTypeString());
		// getName() returns a space when the first & last name are full 
		assertTrue(" ".equals(ctor.getName()));
		// a new instance should have no interests, ergo - all bools for that are false 
		assertTrue("".equals(ctor.getInterestsString()));
		// so, that would mean all of the below are false (and not null because they're primitives)
		assertFalse(ctor.getInterestedInBehavior());
		assertFalse(ctor.getInterestedInBiogeography());
		assertFalse(ctor.getInterestedInCytogenetics());
		assertFalse(ctor.getInterestedInEcology());
		assertFalse(ctor.getInterestedInImmatureStages());
		assertFalse(ctor.getInterestedInMorphology());
		assertFalse(ctor.getInterestedInPhylogenetics());
		assertFalse(ctor.getInterestedInProteins());
		assertFalse(ctor.getInterestedInTaxonomy());
	}
	
	// test the mutators to ensure functionality 
	public void test_mutators_change_values() {
		ctor.setAdditionalInfo(".... blah");
		assertEquals(".... blah", ctor.getAdditionalInfo());
		ctor.setAdditionalInfo("...");
		assertEquals("...", ctor.getAdditionalInfo());
		
		ctor.setAddress("650 E. Cull Way");
		assertEquals("650 E. Cull Way", ctor.getAddress());
		ctor.setAddress("650");
		assertEquals("650", ctor.getAddress());
		
		ctor.setAssignmentApproval("Let Them Eat NULL!!!");
		assertEquals("Let Them Eat NULL!!!", ctor.getAssignmentApproval());
		ctor.setAssignmentApproval("null");
		assertEquals("null", ctor.getAssignmentApproval());
		
		ctor.setBio("Stuck Behind a Minivan!");
		assertEquals("Stuck Behind a Minivan!", ctor.getBio());
		ctor.setBio("it was a cold day");
		assertEquals("it was a cold day", ctor.getBio());
		
		ctor.setCategory("Electro-dance");
		assertEquals("Electro-dance", ctor.getCategory());
		ctor.setCategory("astro-funk");
		assertEquals("astro-funk", ctor.getCategory());

		ctor.setCheckedOut(true);
		assertTrue(ctor.getCheckedOut());
		ctor.setCheckedOut(false);
		assertFalse(ctor.getCheckedOut());
		
		ctor.setCheckedOutContributorId(Long.valueOf(650));
		assertNotNull(ctor.getCheckedOutContributorId());
		assertEquals(Long.valueOf(650), ctor.getCheckedOutContributorId());
		ctor.setCheckedOutContributorId(Long.valueOf(1980));
		assertEquals(Long.valueOf(1980), ctor.getCheckedOutContributorId());
		
		Date now = new Date();
		ctor.setCheckoutDate(now);
		assertNotNull(ctor.getCheckoutDate());
		assertEquals(now, ctor.getCheckoutDate());
		now = new Date();
		ctor.setCheckoutDate(now);
		assertEquals(now, ctor.getCheckoutDate());
		
		ctor.setCheckoutDownloadId(Long.valueOf(2323));
		assertEquals(Long.valueOf(2323), ctor.getCheckoutDownloadId());
		ctor.setCheckoutDownloadId(Long.valueOf(2383));
		assertEquals(Long.valueOf(2383), ctor.getCheckoutDownloadId());
		
		ctor.setCity("Tucson");
		assertEquals("Tucson", ctor.getCity());
		ctor.setCity("Three Points");
		assertEquals("Three Points", ctor.getCity());
		
		ctor.setCoAuthors("Simon, Danny, Jeff");
		assertEquals("Simon, Danny, Jeff", ctor.getCoAuthors());
		ctor.setCoAuthors("NOONE!");
		assertEquals("NOONE!", ctor.getCoAuthors());
		
		Date then = (Date) now.clone();
		ctor.setConfirmationDate(then);
		assertEquals(then, ctor.getConfirmationDate());
		now = new Date();
		ctor.setConfirmationDate(now);
		assertEquals(now, ctor.getConfirmationDate());
		
		ctor.setConfirmerContributorId(Long.valueOf(3374));
		assertEquals(Long.valueOf(3374), ctor.getConfirmerContributorId());
		ctor.setConfirmerContributorId(Long.valueOf(666));
		assertEquals(Long.valueOf(666), ctor.getConfirmerContributorId());
		
		ctor.setContributorType((byte)64);
		assertEquals(64, ctor.getContributorType());
		ctor.setContributorType((byte)252);
		assertEquals(-4, ctor.getContributorType());
		ctor.setContributorType(Contributor.ANY_CONTRIBUTOR);
		assertEquals(-1, ctor.getContributorType());
		
		ctor.setCoordinationComments("Nunc faucibus odio ac risus.");
		assertEquals("Nunc faucibus odio ac risus.", ctor.getCoordinationComments());
		ctor.setCoordinationComments("Cras aliquet volutpat est.");
		assertEquals("Cras aliquet volutpat est.", ctor.getCoordinationComments());
		
		ctor.setCountry("Australia");
		assertEquals("Australia", ctor.getCountry());
		ctor.setCountry("Canada");
		assertEquals("Canada", ctor.getCountry());
		
		ctor.setDontShowAddress(true);
		assertTrue(ctor.getDontShowAddress());
		ctor.setDontShowAddress(false);
		assertFalse(ctor.getDontShowAddress());
		
		ctor.setDontShowEmail(true);
		assertTrue(ctor.getDontShowEmail());
		ctor.setDontShowEmail(false);
		assertFalse(ctor.getDontShowEmail());
		
		ctor.setDontShowLocation(true);
		assertTrue(ctor.getDontShowLocation());
		ctor.setDontShowLocation(false);
		assertFalse(ctor.getDontShowLocation());
		
		ctor.setDontUseEditor(true);
		assertTrue(ctor.getDontUseEditor());
		ctor.setDontUseEditor(false);
		assertFalse(ctor.getDontUseEditor());
		
		ctor.setEditHistoryId(Long.valueOf(33));
		assertEquals(Long.valueOf(33), ctor.getEditHistoryId());
		ctor.setEditHistoryId(Long.valueOf(44));
		assertEquals(Long.valueOf(44), ctor.getEditHistoryId());
		
		ctor.setEditingRootNodeId(Long.valueOf(1));
		assertEquals(Long.valueOf(1), ctor.getEditingRootNodeId());
		ctor.setEditingRootNodeId(Long.valueOf(350));
		assertEquals(Long.valueOf(350), ctor.getEditingRootNodeId());
		
		ctor.setEmail("foo@bar.nu");
		assertEquals("foo@bar.nu", ctor.getEmail());
		ctor.setEmail("dev@null.unix");
		assertEquals("dev@null.unix", ctor.getEmail());
		
		ctor.setFax("650.650.4444");
		assertEquals("650.650.4444", ctor.getFax());
		ctor.setFax("404.650.1980");
		assertEquals("404.650.1980", ctor.getFax());
	
		ctor.setFirstName("Andrew");
		assertEquals("Andrew", ctor.getFirstName());
		ctor.setFirstName("Lisa");
		assertEquals("Lisa", ctor.getFirstName());
		
		ctor.setGeographicAreaInterest("SPACE!");
		assertEquals("SPACE!", ctor.getGeographicAreaInterest());
		ctor.setGeographicAreaInterest("THE FINAL FRONTIER!");
		assertEquals("THE FINAL FRONTIER!", ctor.getGeographicAreaInterest());
		
		ctor.setHomepage("http://foo.org/");
		assertEquals("http://foo.org/", ctor.getHomepage());
		ctor.setHomepage("http://kung-foo.org/");
		assertEquals("http://kung-foo.org/", ctor.getHomepage());
		
		ctor.setId(650);
		assertEquals(650, ctor.getId());
		ctor.setId(999);
		assertEquals(999, ctor.getId());
		
		ctor.setImageFilename("andy_sm.jpg");
		assertEquals("andy_sm.jpg", ctor.getImageFilename());
		ctor.setImageFilename("lisa_sm.jpg");
		assertEquals("lisa_sm.jpg", ctor.getImageFilename());
		
		ctor.setImageModificationDefault(Boolean.TRUE);
		assertTrue(ctor.getImageModificationDefault());
		ctor.setImageModificationDefault(Boolean.FALSE);
		assertFalse(ctor.getImageModificationDefault());
		ctor.setImageModificationDefault(Boolean.valueOf(true));
		assertTrue(ctor.getImageModificationDefault());
		
		ctor.setImageUseDefault(Byte.valueOf((byte)44));
		assertEquals(Byte.valueOf((byte)44), ctor.getImageUseDefault());
		ctor.setImageUseDefault(Byte.valueOf((byte)4));
		assertEquals(Byte.valueOf((byte)4), ctor.getImageUseDefault());
		
		ctor.setImageUseLastChanged(true);
		assertTrue(ctor.getImageUseLastChanged());
		ctor.setImageUseLastChanged(false);
		assertFalse(ctor.getImageUseLastChanged());
		
		ctor.setImageUseLastUpdated(then);
		assertEquals(then, ctor.getImageUseLastUpdated());
		ctor.setImageUseLastUpdated(now);
		assertEquals(now, ctor.getImageUseLastUpdated());
		
		ctor.setInitials("A.J.L.");
		assertEquals("A.J.L.", ctor.getInitials());
		ctor.setInitials("AJL");
		assertEquals("AJL", ctor.getInitials());
		
		ctor.setInstitution("Department of Redundency Department");
		assertEquals("Department of Redundency Department", ctor.getInstitution());
		ctor.setInstitution("The Grail");
		assertEquals("The Grail", ctor.getInstitution());
		
		ctor.setInterestedInBehavior(true);
		assertTrue(ctor.getInterestedInBehavior());
		ctor.setInterestedInBehavior(false);
		assertFalse(ctor.getInterestedInBehavior());
		
		ctor.setInterestedInBiogeography(true);
		assertTrue(ctor.getInterestedInBiogeography());
		ctor.setInterestedInBiogeography(false);
		assertFalse(ctor.getInterestedInBiogeography());
		
		ctor.setInterestedInCytogenetics(true);
		assertTrue(ctor.getInterestedInCytogenetics());
		ctor.setInterestedInCytogenetics(false);
		assertFalse(ctor.getInterestedInCytogenetics());
		
		ctor.setInterestedInEcology(true);
		assertTrue(ctor.getInterestedInEcology());
		ctor.setInterestedInEcology(false);
		assertFalse(ctor.getInterestedInEcology());
		
		ctor.setInterestedInImmatureStages(true);
		assertTrue(ctor.getInterestedInImmatureStages());
		ctor.setInterestedInImmatureStages(false);
		assertFalse(ctor.getInterestedInImmatureStages());
		
		ctor.setInterestedInMorphology(true);
		assertTrue(ctor.getInterestedInMorphology());
		ctor.setInterestedInMorphology(false);
		assertFalse(ctor.getInterestedInMorphology());
		
		ctor.setInterestedInPhylogenetics(true);
		assertTrue(ctor.getInterestedInPhylogenetics());
		ctor.setInterestedInPhylogenetics(false);
		assertFalse(ctor.getInterestedInPhylogenetics());
		
		ctor.setInterestedInProteins(true);
		assertTrue(ctor.getInterestedInProteins());
		ctor.setInterestedInProteins(false);
		assertFalse(ctor.getInterestedInProteins());
	
		ctor.setInterestedInTaxonomy(true);
		assertTrue(ctor.getInterestedInTaxonomy());
		ctor.setInterestedInTaxonomy(false);
		assertFalse(ctor.getInterestedInTaxonomy());
		
		ctor.setIsImageEditor(true);
		assertTrue(ctor.getIsImageEditor());
		ctor.setIsImageEditor(false);
		assertFalse(ctor.getIsImageEditor());
		
		ctor.setIsLearningEditor(true);
		assertTrue(ctor.getIsLearningEditor());
		ctor.setIsLearningEditor(false);
		assertFalse(ctor.getIsLearningEditor());
		
		ctor.setLastName("Lenards");
		assertEquals("Lenards", ctor.getLastName());
		ctor.setLastName("Antkow");
		assertEquals("Antkow", ctor.getLastName());
		
		ctor.setLicenseReviewerContributorId(Long.valueOf(664));
		assertEquals(Long.valueOf(664), ctor.getLicenseReviewerContributorId());
		ctor.setLicenseReviewerContributorId(Long.valueOf(344));
		assertEquals(Long.valueOf(344), ctor.getLicenseReviewerContributorId());
		
		ctor.setNoteModificationDefault(Boolean.TRUE);
		assertTrue(ctor.getNoteModificationDefault());
		ctor.setNoteModificationDefault(false);
		assertFalse(ctor.getNoteModificationDefault());
		ctor.setNoteModificationDefault(Boolean.valueOf(true));
		assertTrue(ctor.getNoteModificationDefault());
		
		String note = "See publication #54 - Sed odio libero, tempor nec, aliquet eu, vulputate elementum, magna.";
		ctor.setNotes(note);
		assertEquals(note, ctor.getNotes());
		note = "Vestibulum ornare pharetra tellus.";
		ctor.setNotes(note);
		assertEquals(note, ctor.getNotes());
		
		ctor.setNoteUseDefault(Byte.valueOf((byte)6));
		assertEquals(Byte.valueOf((byte)6), ctor.getNoteUseDefault());
		ctor.setNoteUseDefault(null);
		assertNull(ctor.getNoteUseDefault());
		ctor.setNoteUseDefault(Byte.valueOf((byte)28));
		assertEquals(Byte.valueOf((byte)28), ctor.getNoteUseDefault());
		
		ctor.setNoteUseLastChanged(true);
		assertTrue(ctor.getNoteUseLastChanged());
		ctor.setNoteUseLastChanged(false);
		assertFalse(ctor.getNoteUseLastChanged());

		// we're using "now" and "then" Date instances, 
		// make sure they're not the same 
		assertNotSame(now, then);
		
		ctor.setNoteUseLastUpdated(then);
		assertEquals(then, ctor.getNoteUseLastUpdated());
		ctor.setNoteUseLastUpdated(now);
		assertEquals(now, ctor.getNoteUseLastUpdated());
		
		ctor.setOtherInterests("javelin");
		assertEquals("javelin", ctor.getOtherInterests());
		ctor.setOtherInterests("death");
		assertEquals("death", ctor.getOtherInterests());
		
		ctor.setPassword("funkmonkzao");
		assertEquals("funkmonkzao", ctor.getPassword());
		ctor.setPassword("rockitliketomjones");
		assertEquals("rockitliketomjones", ctor.getPassword());
		
		ctor.setPhone("650.404.8686");
		assertEquals("650.404.8686", ctor.getPhone());
		ctor.setPhone("650.404.8689");
		assertEquals("650.404.8689", ctor.getPhone());
		
		ctor.setPlans("total domination!");
		assertEquals("total domination!", ctor.getPlans());
		ctor.setPlans("bfe");
		assertEquals("bfe", ctor.getPlans());
		
		String pubs = "Donec elit nibh, aliquam ac, sollicitudin vitae, porta at, nulla.";
		ctor.setPublications(pubs);
		assertEquals(pubs, ctor.getPublications());
		pubs = "Suspendisse lectus sem, commodo non, dapibus ut, ultrices quis, erat.";
		ctor.setPublications(pubs);
		assertEquals(pubs, ctor.getPublications());
		
		ctor.setQualifications("Lead Developer");
		assertEquals("Lead Developer", ctor.getQualifications());
		ctor.setQualifications("Subcommandante");
		assertEquals("Subcommandante", ctor.getQualifications());
		
		ctor.setShowAddress(true);
		assertTrue(ctor.getShowAddress());
		ctor.setShowAddress(false);
		assertFalse(ctor.getShowAddress());
		
		ctor.setShowEmail(true);
		assertTrue(ctor.getShowEmail()); 
		ctor.setShowEmail(false);
		assertFalse(ctor.getShowEmail());
		
		ctor.setState("Bless");
		assertEquals("Bless", ctor.getState());
		ctor.setState("melancholy");
		assertEquals("melancholy", ctor.getState());
		
		ctor.setSurname("Lenards");
		assertEquals("Lenards", ctor.getSurname());
		ctor.setSurname("Antkow");
		assertEquals("Antkow", ctor.getSurname());
		
		ctor.setUnapprovedContributorType((byte)4);
		assertEquals(4, ctor.getUnapprovedContributorType());
		ctor.setUnapprovedContributorType((byte)9);
		assertEquals(9, ctor.getUnapprovedContributorType());
		
		ctor.setWillingToCoordinate(true);
		assertTrue(ctor.getWillingToCoordinate());
		ctor.setWillingToCoordinate(false);
		assertFalse(ctor.getWillingToCoordinate());
	}
	
	// test the setValues(Contributor) method 
	/**
	 * Ensure that setValues(Contributor) is working. 
	 * 
	 * The fields that are modified in setValues() are:
	 * 
     *  id
     *  firstName 
     *  lastName 
     *  email 
     *  address 
     *  homepage 
     *  permissions 
     *  institution 
     *  phone 
     *  fax 
     *  notes
     *  
     * DEVN - No reason is given in the implementation  
     * why components of a contributor's "address", like 
     * city & state, are not included.  Their inclusion 
     * would seem to be natural here.  The likely reason 
     * is that these fields are the ones defined as part 
     * of the generic setValue(byte, String) method that 
     * is used for undo operations. According to comments 
     * there is a connection with this method and the 
     * NodeImage object.    
	 */
	public void test_set_values_for_contributor() {
		ctor.setId(650);
		ctor.setFirstName("Andrew");
		ctor.setLastName("Lenards");
		ctor.setAddress("650 E. Rockout Way");
		ctor.setHomepage("http://futureme.org/");
		ctor.setInstitution("650 Software");
		ctor.setPhone("650.404.8686");
		ctor.setFax("650.404.4411");
		ctor.setNotes("{important stuff}");
		
		// I skipped permissions because I think it 
		// is no longer used, deprecated in favor of 
		// ContributorPermissions. 
		
		Contributor lisa = new Contributor();
		lisa.setId(1030);
		lisa.setFirstName("Lisa");
		lisa.setLastName("Antkow");
		lisa.setAddress("6267 S Bosworth Way");
		lisa.setHomepage("http://pimaco.gov");
		lisa.setInstitution("Pima County");
		lisa.setPhone("520.411.0555");
		lisa.setFax("520.411.5656");
		lisa.setNotes("{details}");
		
		assertEquals("Andrew Lenards", ctor.getName());
		assertEquals("650 E. Rockout Way", ctor.getAddress());
		assertEquals(650, ctor.getId());
		
		ctor.setValues(lisa);
		
		assertEquals(1030, ctor.getId());
		assertEquals("Lisa", ctor.getFirstName());
		assertEquals("Antkow", ctor.getLastName());
		assertEquals("6267 S Bosworth Way", ctor.getAddress());
		assertEquals("http://pimaco.gov", ctor.getHomepage());
		assertEquals("Pima County", ctor.getInstitution());
		assertEquals("520.411.0555", ctor.getPhone());
		assertEquals("520.411.5656", ctor.getFax());
		assertEquals("{details}", ctor.getNotes());
	}
	
	// test the generic setter setValue(key, value)
	/**
	 * 
	 * FIRST_NAME_KEY
	 * LAST_NAME_KEY
	 * EMAIL_KEY
	 * ADDRESS_KEY
	 * HOMEPAGE_KEY
	 * INSTITUTION_KEY
	 * PHONE_KEY
	 * FAX_KEY
	 * NOTES_KEY
	 */
	public void test_generic_setter() {
		ctor.setId(1030);
		ctor.setFirstName("Lisa");
		ctor.setLastName("Antkow");
		ctor.setEmail("lisa@antkow.us");
		ctor.setAddress("6267 S Bosworth Way");
		ctor.setHomepage("http://pimaco.gov");
		ctor.setInstitution("Pima County");
		ctor.setPhone("520.411.0555");
		ctor.setFax("520.411.5656");
		ctor.setNotes("{details}");

		assertEquals(1030, ctor.getId());
		assertEquals("Lisa", ctor.getFirstName());
		assertEquals("Antkow", ctor.getLastName());
		assertEquals("lisa@antkow.us", ctor.getEmail());
		assertEquals("6267 S Bosworth Way", ctor.getAddress());
		assertEquals("http://pimaco.gov", ctor.getHomepage());
		assertEquals("Pima County", ctor.getInstitution());
		assertEquals("520.411.0555", ctor.getPhone());
		assertEquals("520.411.5656", ctor.getFax());
		assertEquals("{details}", ctor.getNotes());
		
		ctor.setValue(Contributor.FIRST_NAME_KEY, "Andrew");
		ctor.setValue(Contributor.LAST_NAME_KEY, "Lenards");
		ctor.setValue(Contributor.EMAIL_KEY, "andrew@futureme.org");
		ctor.setValue(Contributor.ADDRESS_KEY, "Andrew");
		ctor.setValue(Contributor.HOMEPAGE_KEY, "Andrew");
		ctor.setValue(Contributor.INSTITUTION_KEY, "Andrew");
		ctor.setValue(Contributor.PHONE_KEY, "Andrew");
		ctor.setValue(Contributor.FAX_KEY, "Andrew");
		ctor.setValue(Contributor.NOTES_KEY, "Andrew");

		assertEquals("Andrew", ctor.getFirstName());
		assertEquals("Lenards", ctor.getLastName());
		assertEquals("andrew@futureme.org", ctor.getEmail());
		assertEquals("Andrew", ctor.getAddress());
		assertEquals("Andrew", ctor.getHomepage());
		assertEquals("Andrew", ctor.getInstitution());
		assertEquals("Andrew", ctor.getPhone());
		assertEquals("Andrew", ctor.getFax());
		assertEquals("Andrew", ctor.getNotes());
		
	}
	
	// test that display name functions 
	public void test_display_name_correct() {
		ctor.setId(1030);
		ctor.setFirstName("Lisa");
		ctor.setLastName("Antkow");
		ctor.setInstitution("Pima County");

		assertEquals("Lisa Antkow", ctor.getName());
		assertEquals("Lisa Antkow", ctor.getDisplayName());
		
		ctor.setFirstName(null);
		ctor.setLastName(null);
		assertEquals("Pima County", ctor.getDisplayName());
	}
	
	// test that is institutions functions
	// test that name or institution functions	
	/**
	 * Any contributor without a first & last name is  
	 * construed as being an institution.  Therefore, 
	 * if getFirstName() & getLastName() are empty or 
	 * null, the Contributor represents an institution. 
	 */
	public void test_institution_methods_correct() {
		ctor.setId(1030);
		ctor.setInstitution("Pima County");
		// externally verify the conditions that the getIsInstituion uses 
		assertTrue(StringUtils.isEmpty(ctor.getFirstName()));
		assertTrue(StringUtils.isEmpty(ctor.getLastName()));
		// use the method 
		assertTrue(ctor.getIsInstitution());
		
		assertEquals("Pima County", ctor.getNameOrInstitution());
		assertEquals("Pima County", ctor.getDisplayName());
		assertEquals(" ", ctor.getName());
		
		ctor.setFirstName("Lisa");
		ctor.setLastName("Antkow");		
		
		// externally verify the conditions that the getIsInstituion uses 
		assertFalse(StringUtils.isEmpty(ctor.getFirstName()));
		assertFalse(StringUtils.isEmpty(ctor.getLastName()));
		// use the method 
		assertFalse(ctor.getIsInstitution());
		
		assertEquals("Antkow, Lisa", ctor.getNameOrInstitution());
		assertEquals("Lisa Antkow", ctor.getDisplayName());
		assertEquals("Lisa Antkow", ctor.getName());
		
		ctor.setFirstName(null);
		
		// externally verify the conditions that the getIsInstituion uses 
		assertTrue(StringUtils.isEmpty(ctor.getFirstName()));
		assertFalse(StringUtils.isEmpty(ctor.getLastName()));
		// use the method 
		assertFalse(ctor.getIsInstitution());
		
		assertEquals("Antkow", ctor.getNameOrInstitution());
		// leading space has to do w/ how the names are joined 
		assertEquals(" Antkow", ctor.getDisplayName());
		assertEquals(" Antkow", ctor.getName());		
		
		ctor.setFirstName("Lisa");
		ctor.setLastName(null);

		// externally verify the conditions that the getIsInstituion uses 
		assertFalse(StringUtils.isEmpty(ctor.getFirstName()));
		assertTrue(StringUtils.isEmpty(ctor.getLastName()));
		// use the method 
		assertFalse(ctor.getIsInstitution());

		// String var = getLastName() + firstNameString apparently 
		// produces "null, Lisa" - I think that's interesting that null 
		// is in there like that.  I found an instance where this 
		// happened in Groovy and that it had to do w/ their usage of 
		// GroovyStrings - but it's apparently present in Java too. 
		assertEquals("null, Lisa", ctor.getNameOrInstitution());
		// leading space has to do w/ how the names are joined 
		assertEquals("Lisa ", ctor.getDisplayName());
		assertEquals("Lisa ", ctor.getName());		
	}

	// test get interests string
	
	// test that you can add & remove permissions
	@SuppressWarnings("unchecked")
	public void test_add_remove_permissions() {
		ctor.setFirstName("Andrew");
		ctor.setLastName("Lenards");
		
		Permission perm1 = new Permission(650, "Six-fifty", ctor);
		ctor.addToPermissions(perm1);
		Permission perm2 = new Permission(87, "Rahr", ctor);
		ctor.addToPermissions(perm2);
		
		ArrayList perms = new ArrayList();
		perms.add(perm1);
		perms.add(perm2);
		assertEquals(perms, ctor.getPermissions());
		
		ctor.removeFromPermissions(perm2);
		perms = new ArrayList();
		perms.add(perm1);
		assertEquals(perms, ctor.getPermissions());
		
		((Permission)ctor.getPermissions().get(0)).setNodeName("Foo");
		assertEquals(perms, ctor.getPermissions());
		
		Permission permX = new Permission(650, "Six-fifty", ctor);
		perms = new ArrayList();
		perms.add(permX);
		assertNotSame(perms, ctor.getPermissions());
		
		// Permission does not have a definition for equals(), so 
		// further testing is not really worth it.  
	}
	
	// test funky set use permission default (imageuse / noteuse), will require testing use permission last updated too
	/**
	 * This tests the logic used for update the bookkeeping 
	 * variables associated with a contributor changing their 
	 * default use permission, which is ToL-speak for the 
	 * default license with which their content is "under."  
	 * We made a big move over to Creative Commons licenses 
	 * when implementing the content aggregation services used 
	 * by the EOL.  We wanted to know if a contributor had 
	 * updated their license, and when they did.  This allowed 
	 * us to query contributors and then approach them about 
	 * changing the license for their content (or future content). 
	 * 
	 * The algorithm looked something like this: 
	 * 
	 *  // if the default is "set", e.g. not null
	 * 	if (imageUseDefault != null) {
	 * 		// verify that it has changed from the previous
	 *  	boolean change = !imageUseDefault.equals(this.imageUseDefault);
	 *  	// update the property indicating that it changed
	 *  	setImageUseLastChanged(change);
	 *  	// set the property indicating when it was changed
	 *  	setImageUseLastUpdated(new Date());
	 *  }
	 *  this.imageUseDefault = imageUseDefault;
	 *   
	 */
	public void test_set_use_permission_side_effects_correct() {
		assertNull(ctor.getImageUseDefault());
		assertFalse(ctor.getImageUseLastChanged());
		assertNull(ctor.getImageUseLastUpdated());

		Date now = new Date();

		ctor.setImageUseDefault(NodeImage.CC_BY30);
		assertEquals(Byte.valueOf(NodeImage.CC_BY30), ctor.getImageUseDefault());
		assertTrue(ctor.getImageUseLastChanged());
		assertNotNull(ctor.getImageUseLastUpdated());
		// now should come before the updated-date (or, on very fast machine, 
		// they might actually be equal) 
		assertTrue(now.compareTo(ctor.getImageUseLastUpdated()) <= 0);
	}
	
	// test get Contributor Type String
	public void test_contributor_type_string_correct() {
		assertEquals(0, ctor.getContributorType());
		assertEquals(Contributor.SCIENTIFIC_CONTRIBUTOR, ctor.getContributorType());
		
		// if the contributor type is SCIENTIFIC_CONTRIBUTOR, we expect 
		assertEquals("Scientific Core", ctor.getContributorTypeString());
		
		// set it to be -1
		ctor.setContributorType(Contributor.ANY_CONTRIBUTOR);
		// so we expect 
		assertEquals("Media", ctor.getContributorTypeString());
		
		ctor.setContributorType(Contributor.ACCESSORY_CONTRIBUTOR);
		assertEquals("General Scientific", ctor.getContributorTypeString());
		
		ctor.setContributorType(Contributor.TREEHOUSE_CONTRIBUTOR);
		assertEquals("Treehouse", ctor.getContributorTypeString());
		
		ctor.setContributorType(Contributor.OTHER_SCIENTIST);
		assertEquals("Other Scientist", ctor.getContributorTypeString());
		
		// any value not defined by a constant defaults to Media
		ctor.setContributorType((byte)100);
		// so we expect 
		assertEquals("Media", ctor.getContributorTypeString());

		ctor.setContributorType((byte)-40);
		assertEquals("Media", ctor.getContributorTypeString());		
	}
	
	// test the add & remove contributor-permissions functionality 
	// (wait until later - this isn't as important to test) 
 
	
	// test compare to functionality
	public void test_comparable_definition() {
		ctor.setId(650);
		ctor.setFirstName("Andrew");
		ctor.setLastName("Lenards");
		
		Contributor contr1 = new Contributor();
		contr1.setId(650);
		contr1.setInstitution("Center for Research of Unexpected Events");
		
		Contributor contr2 = new Contributor();
		contr2.setId(650);
		contr2.setInstitution("University of Arizona");
		
		Contributor.ContributorComparator cmp = new Contributor.ContributorComparator();
 
		assertTrue(cmp.compare(ctor, ctor) == 0);
		
		assertTrue(cmp.compare(ctor, contr1) > 0);
		assertTrue(cmp.compare(contr1, contr2) < 0);
		assertTrue(cmp.compare(contr2, ctor) > 0);
 
		ctor.setFirstName(null);
		ctor.setLastName(null);
		
		// This is WRONG - any object compared with itself should be 0! 
		// The problem is, the first condition is hit - the first comparison 
		// string is equal to null, so -1 is returned when there should be 
		// a case for when the first & second comparison strings are both 
		// null - thus, 0 should be returned.  Unfortunately, correcting 
		// this might alter expected behavior so I, lenards, won't bother 
		// to add the case.  But I want it noted that it's incorrect.		
		assertTrue(cmp.compare(ctor, ctor) != 0);
		// since lhs's comparison string is null, expect less than 0 
		assertTrue(cmp.compare(ctor, contr1) < 0);
		// since rhs's comparison string is null, expect greater than 0
		assertTrue(cmp.compare(contr1, ctor) > 0);
	}
	
	// test equality functionality
	public void test_equality_definition() {
		ctor.setId(650);
		
		Contributor contr1 = new Contributor();
		contr1.setId(650);
		
		Contributor contr2 = new Contributor();
		contr2.setId(650);
		
		// x == x
		assertEquals(ctor, ctor);
		
		// if x == y, then y == x
		assertEquals(ctor, contr1);
		assertEquals(contr1, ctor);
		
		// if x == y, y == z then x == z
		assertEquals(ctor, contr1);
		assertEquals(contr1, contr2);
		assertEquals(ctor, contr2);
		
		contr1.setId(86);
		contr2.setId(99);
		
		assertNotSame(ctor, contr1);
		assertNotSame(contr1, contr2);
		assertNotSame(contr2, ctor);
		
		// check that we handle other types 
		NodeImage img = new NodeImage();
		assertFalse(ctor.equals(img));
		// check that we handle null 
		assertFalse(ctor.equals(null));
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		ctor = null;
	}
}
