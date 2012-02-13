package org.tolweb.hibernate;

import junit.framework.TestCase;

/**
 * MappedOtherName is an embedded mapped class representing an
 * "other name" or synonym for a group/organism.  In ToL-speak, 
 * it is a name attached to a node.  It's accessible via the 
 * getSynonyms() method of the MappedNode mapped entity, which is 
 * also the owning, or parent, class for MappedOtherName. 
 * 
 * It should be noted that the getOtherNames() method inherited 
 * by MappedNode from Node is no longer used and dates back to the 
 * time when TreeGrow was used for all editing and manipulation 
 * of the data on the website. 
 * 
 * Being a child or subordinate class gives MappedOtherName some 
 * characteristics that are worth mentioning.  First off, this 
 * fact (the "embedded" or child status) means that, in Hibernate 
 * terms, it will never exist on its' own in the Hibernate landscape. 
 * This means that you'll never be able to get the otherNameId of a 
 * given instance - because to have an identity would mean you exist 
 * on your own. You could think of it as MappedNode putting 
 * MappedOtherName in it's place - all the time, like some oppressive 
 * government (or character in a Lifetime original).
 * 
 * The consequences of this embedded role mean that are worth noting. 
 * Any change to the fields of an instance will cause Hibernate to 
 * remove the previous instance and create a new one.  Why?  Because 
 * Hibernate defines the identity of the instance by hashcoding all 
 * the fields.  So a minor change has the major side-effect of object 
 * removal.  However, nullable fields make this *way* more interesting 
 * then you'd ever expect. You can in SQL something like "... where 
 * comments = null" - it's valid, but it's always false.  So what this 
 * means is that when you make a change - Hibernate attempts to delete 
 * an instance, but it fails (but it doesn't realize that) and then 
 * inserts a brand new instance. This issue is chronicled in several 
 * write-ups in the TolwebDocumation project under "incidents".  
 *  
 * TreeGrow came long before "TolwebHibernate", this means that most 
 * domain objects have a TreeGrow version which are extended in order 
 * to make them "mapped" - or handled by Hibernate.  
 *  
 * @author lenards
 *
 */
public class MappedOtherNameTest extends TestCase {
	private MappedOtherName moname; 
	
	protected void setUp() throws Exception {
		super.setUp();
		moname = new MappedOtherName();
	}
	
	/**
	 * The major fields that matter for class
	 * 
	 * Defined in OtherName: name, authority, date, isImportant, isPreferred, changedFromServer, id
	 * Defined in MappedOtherName: otherNameId, authorityYear, italicize, comment, isCommonName, isDontList 
	 */
	public void test_all_setters_operate_correctly() {
		moname.setAuthority("Dejean");
		assertEquals("Dejean", moname.getAuthority());
		
		moname.setAuthorityYear(Integer.valueOf(1999));
		assertEquals(Integer.valueOf(1999), moname.getAuthorityYear());
		
		moname.setComment("comments are neat");
		assertEquals("comments are neat", moname.getComment());
		
		moname.setDate(1999);
		assertTrue(1999 == moname.getDate());
		
		moname.setId(650);
		assertTrue(moname.getId() == 650);
		
		moname.setIsCommonName(true);
		assertTrue(moname.getIsCommonName());
		moname.setIsCommonName(false);
		assertFalse(moname.getIsCommonName());
		
		moname.setIsDontList(true);
		assertTrue(moname.getIsDontList());
		moname.setIsDontList(false);
		assertFalse(moname.getIsDontList());
		
		moname.setIsImportant(true);
		assertTrue(moname.getIsImportant());
		moname.setIsImportant(false);
		assertFalse(moname.getIsImportant());
		
		moname.setIsPreferred(false);
		assertFalse(moname.getIsPreferred());
		moname.setIsPreferred(true);
		assertTrue(moname.getIsPreferred());
		
		moname.setItalicize(false);
		assertFalse(moname.getItalicize());
		moname.setItalicize(true);
		assertTrue(moname.getItalicize());

		moname.setName(null);
		assertTrue(moname.getName() == null);
		moname.setName("");
		assertTrue(moname.getName().length() == 0);
		moname.setName("Common Monkeybat");
		assertEquals("Common Monkeybat", moname.getName());

		moname.setOrder(0);
		assertTrue(moname.getOrder() == 0);
		
		moname.setOtherNameId(Long.valueOf(650));
		assertEquals(Long.valueOf(650), moname.getOtherNameId());
	}

	/**
	 * An oddity about MappedOtherName is that equality is not defined 
	 * by what you'd consider a "proper business defintion."  One might 
	 * expect a simple name comparison defining equality.  But that is 
	 * not really the whole story because more than a name matters. 
	 * Authority information is part of an other name, so you might 
	 * expect a proper definition to include all that. But it is not 
	 * defined by the "authority" information (so you'd expect 
	 * "Vombatus ursinus Shaw 1800" to not be equal to 
	 * "Vombatus ursinus" because the included authority information 
	 * makes the first example different.  Thus, it would follow that: 
	 * "Vombatus ursinus Shaw" != "Vombatus ursinus Shaw 1800"). Again, 
	 * this is not the case.  Order actually defined equality because 
	 * of the technical UI details of how OtherNames are displayed & 
	 * edited.  This is obviously not ideal, but it's how it is - and 
	 * changing it would have been a serious pain (so it has not happen) 
	 * 
	 * That leads us to the need for a nameEquals() method. 
	 * 
	 * Note: there is also a proper definition that uses name, authority, 
	 * and date defined by authorityInfoEquals()
	 */
	public void test_name_equality() {
		moname.setName("Wombats");
		moname.setIsCommonName(true);
		
		MappedOtherName rhs = new MappedOtherName();
		rhs.setName("wombats");
		
		assertFalse(moname.nameEquals(rhs));
		
		rhs.setName("Wombats");
		
		assertTrue(moname.nameEquals(rhs));
		
		rhs.setName("Wombats ");
		
		assertFalse(moname.nameEquals(rhs));
		
		rhs.setName("");
		
		assertFalse(moname.nameEquals(rhs));
		
		rhs.setName(null);
		
		assertFalse(moname.nameEquals(rhs));
		
		rhs = null;
		
		assertFalse(moname.nameEquals(rhs));
	}
	
	/**
	 * See comment for test_name_equality for background
	 * 
	 * Remember, equality should have the following three 
	 * attributes: 
	 * 
	 * - identity (x == x)
	 * - symmetric (if x == y then y == x)
	 * - transitive (if x == y & y == z, then x == z) 
	 * 
	 */
	public void test_authority_info_equality() {
		moname.setName("Wombats");
		
		// identity - it should be equal to itself
		assertTrue(moname.authorityInfoEquals(moname));
		
		// it should handle null politely 
		assertFalse(moname.authorityInfoEquals(null));
		
		MappedOtherName rhs = new MappedOtherName();
		rhs.setName("Wombats");
		
		// it should always be symmetric 
		assertTrue(moname.authorityInfoEquals(rhs));
		assertTrue(rhs.authorityInfoEquals(moname));
		
		rhs.setAuthority("Shaw");
		
		assertFalse(moname.authorityInfoEquals(rhs));
		assertFalse(rhs.authorityInfoEquals(moname));
		
		moname.setAuthority("Shaw");
		
		assertTrue(moname.authorityInfoEquals(rhs));
		assertTrue(rhs.authorityInfoEquals(moname));
		
		moname.setAuthorityYear(Integer.valueOf(1800));

		assertFalse(moname.authorityInfoEquals(rhs));
		assertFalse(rhs.authorityInfoEquals(moname));

		rhs.setAuthorityYear(Integer.valueOf(1800));

		assertTrue(moname.authorityInfoEquals(rhs));
		assertTrue(rhs.authorityInfoEquals(moname));

		moname.setAuthority(null);
		
		assertFalse(moname.authorityInfoEquals(rhs));
		assertFalse(rhs.authorityInfoEquals(moname));
		
		rhs.setAuthority(null);

		assertTrue(moname.authorityInfoEquals(rhs));
		assertTrue(rhs.authorityInfoEquals(moname));
		
		// set the authority back to the correct one
		moname.setAuthority("Shaw");
		rhs.setAuthority("Shaw");
		// create a three instance, that's should be equal 
		MappedOtherName z = new MappedOtherName();
		z.setName("Wombats");
		z.setAuthority("Shaw");
		z.setAuthorityYear(Integer.valueOf(1800));
	
		// if moname == rhs
		assertTrue(moname.authorityInfoEquals(rhs));
		// and rhs == z 
		assertTrue(rhs.authorityInfoEquals(z));
		// then moname == z
		assertTrue(moname.authorityInfoEquals(z));

		// make sure they're not always equal to z 
		
		z.setAuthority(null);
		
		assertFalse(rhs.authorityInfoEquals(z));
		assertFalse(moname.authorityInfoEquals(z));
		
		z.setAuthority("Shaw");
		z.setAuthorityYear(null);

		assertFalse(rhs.authorityInfoEquals(z));
		assertFalse(moname.authorityInfoEquals(z));
	}

	/**
	 * Equality is defined by the superclass of OtherName, 
	 * OrderedObject.  OtherNames are edited in an ordered 
	 * manner in the UI - this is the technical detail that 
	 * was allowed to seep into the lower level.  Thus, the 
	 * oddness or complexity behind the manner of defining 
	 * equality has nothing to do w/ the business logic of 
	 * the domain object. 
	 */
	public void test_equality_comparable_functionality() {
		moname.setName("Vombatidae");
		moname.setAuthority("Burnett");
		moname.setAuthorityYear(1829);
		moname.setOrder(-1);
	
		// should be equal to itself
		assertTrue(moname.equals(moname));
		// should elegantly handle null
		assertFalse(moname.equals(null));
		// hashCode() override should be same as inherited getHashCode()
		assertTrue(moname.hashCode() == moname.getHashCode());
		
		MappedOtherName wombat = new MappedOtherName();
		wombat.setName("Common Wombat");
		wombat.setAuthority("Shaw");
		wombat.setAuthorityYear(1800);
		// hashCode() override should be same as inherited getHashCode()
		assertTrue(wombat.hashCode() == wombat.getHashCode());
		
		MappedOtherName vombatus = new MappedOtherName();
		vombatus.setName("Vombatus");
		vombatus.setAuthority("Geoffroy");
		vombatus.setAuthorityYear(1803);
		
		MappedOtherName lasiorhinus = new MappedOtherName();
		lasiorhinus.setName("Lasiorhinus");
		lasiorhinus.setAuthority("Owen");
		lasiorhinus.setAuthorityYear(1845);
		
		// identity (x == x)
		assertTrue(vombatus.equals(vombatus));
		
		// symmetric (if x == y then y == x)   
		assertTrue(vombatus.equals(lasiorhinus));
		assertTrue(lasiorhinus.equals(vombatus));
		
		// transitive (if x == y, y == z then x == z)
		assertTrue(vombatus.equals(lasiorhinus));
		assertTrue(lasiorhinus.equals(wombat));
		assertTrue(vombatus.equals(wombat));

		assertTrue(vombatus.hashCode() == lasiorhinus.hashCode());
		assertTrue(vombatus.getHashCode() == lasiorhinus.getHashCode());
		assertTrue(lasiorhinus.hashCode() == wombat.hashCode());
		assertTrue(lasiorhinus.getHashCode() == wombat.getHashCode());
		assertTrue(vombatus.hashCode() == wombat.hashCode());
		assertTrue(vombatus.getHashCode() == wombat.getHashCode());
		
		assertTrue(vombatus.compareTo(lasiorhinus) == 0);
		assertTrue(lasiorhinus.compareTo(wombat) == 0);
		assertTrue(vombatus.compareTo(wombat) == 0);
		
		assertFalse(moname.equals(wombat));
		assertFalse(moname.equals(lasiorhinus));
		assertFalse(moname.equals(vombatus));

		assertFalse(moname.doEquals(wombat));
		assertFalse(moname.doEquals(lasiorhinus));
		assertFalse(moname.doEquals(vombatus));		

		assertTrue(vombatus.doEquals(vombatus));
		
		assertTrue(vombatus.doEquals(lasiorhinus));
		assertTrue(lasiorhinus.doEquals(vombatus));
		
		assertTrue(wombat.doEquals(lasiorhinus));
		assertTrue(lasiorhinus.doEquals(vombatus));
		assertTrue(wombat.doEquals(vombatus));
		
		assertFalse(moname.compareTo(wombat) == 0);
		assertFalse(moname.compareTo(lasiorhinus) == 0);
		assertFalse(moname.compareTo(vombatus) == 0);
	}
	
	/**
	 * MappedOtherName contains several methods that are 
	 * intended to provide logical information about the 
	 * status of the instance's authority information 
	 * (moreover, they're helper methods to quickly know 
	 * if stuff is missing or not) 
	 */
	public void test_authority_info_checks() {
		// Lasiorhinus Owen 1845
		moname.setName("Lasiorhinus");
		
		assertFalse(moname.hasAuthorityInfo());
		assertTrue(moname.hasIncompleteAuthorityInfo());
		assertTrue(moname.hasNoAuthorityInfo());
		
		moname.setAuthority("Owen");

		assertTrue(moname.getAuthority() != null);
		assertTrue(moname.getAuthorityYear() == null);
		assertTrue(moname.hasIncompleteAuthorityInfo());
		
		assertFalse(moname.hasAuthorityInfo());
		assertFalse(moname.hasNoAuthorityInfo());
		
		moname.setAuthorityYear(1845);

		assertTrue(moname.hasAuthorityInfo());
		assertFalse(moname.hasIncompleteAuthorityInfo());
		assertFalse(moname.hasNoAuthorityInfo());
	}
	
	/**
	 * The Taxa Import process needed to be able to "merge" 
	 * other names authority info.  The idea being, you may 
	 * get an other name without any authority info and one 
	 * that has full authority info.  You'd like to merge 
	 * them together.  Afterwards, they should be equal. 
	 * 
	 * The example is, you have a taxonomic catalog that 
	 * you're important.  We may have a good number of 
	 * "other names" in a clade - but they are not likely 
	 * to be complete (where the taxonomic catalog 
	 * should have a greater amount of complete authority 
	 * info - otherwise, it wouldn't be that good and we'd  
	 * not be as likely to import it). 
	 */
	public void test_merge_authority_info() {
		// lhs: left hand side
		// rhs: right hand side
		moname.setName("Lasiorhinus");

		// moname shouldn't have any authority info. 
		assertTrue(moname.hasIncompleteAuthorityInfo());
		assertTrue(moname.hasNoAuthorityInfo());
		assertFalse(moname.hasAuthorityInfo());
		
		// create an other name w/ complete authority info
		MappedOtherName wombats = new MappedOtherName();
		wombats.setName("Lasiorhinus");
		wombats.setAuthority("Owen");
		wombats.setAuthorityYear(1845);
		
		// merge the lhs & rhs instance together. 
		moname.mergeAuthorityInfo(wombats);
		
		// now, they should represent equal by authority info
		assertTrue(moname.authorityInfoEquals(wombats));
		
		// moname should have any authority info now
		assertTrue(moname.hasAuthorityInfo());
		assertFalse(moname.hasIncompleteAuthorityInfo());
		assertFalse(moname.hasNoAuthorityInfo());

		MappedOtherName anti = new MappedOtherName();
		anti.setName("Lasiorhinus");
		
		// merging a rhs without authority info should never 
		// clobber the existing authority info in lhs. 
		moname.mergeAuthorityInfo(anti);
		assertFalse(moname.authorityInfoEquals(anti));
		assertTrue(moname.authorityInfoEquals(wombats));
	}

	/**
	 * Expected format should be something like: 
	 * 
	 * OtherName with name: {name} order: {order} authority: {authority} date: {authorityYear} important: {isImportant} preferred: {isPreferred}
	 * 
	 */
	public void test_toString_format_check() {
		// authority="" vs. name=null&date=null - that's because authority is set to empty in OtherName
		String expected = "OtherName with name: null order: 0 authority:  date: null important: false preferred: false";
		System.out.println(moname.toString());
		assertEquals(expected, moname.toString());
		
		moname.setName("Lasiorhinus");
		moname.setIsPreferred(true);
		moname.setIsImportant(true);
		
		expected = "OtherName with name: Lasiorhinus order: 0 authority:  date: null important: true preferred: true";
		
		assertEquals(expected, moname.toString());
		
		moname.setAuthority("Owen");
		moname.setAuthorityYear(1845);
		moname.setIsPreferred(false);
		
		expected = "OtherName with name: Lasiorhinus order: 0 authority: Owen date: 1845 important: true preferred: false";
		
		assertEquals(expected, moname.toString());
		
		moname.setOrder(9);
		moname.setIsPreferred(true);
		moname.setIsImportant(false);
		
		expected = "OtherName with name: Lasiorhinus order: 9 authority: Owen date: 1845 important: false preferred: true";
		
		assertEquals(expected, moname.toString());
	}
	
	/**
	 * The shouldDisplay() method is used when creating the "Other Names" 
	 * text section for pages.  The intent is this, contributors may not 
	 * want the public to see names they've associated with a group - so 
	 * they can get that behavior by toggling the "don't list" property 
	 * for OtherNames in the editing tools.  Also, we want to collect 
	 * all "common names" to display them in a single, comma-separated 
	 * list - so shouldDisplay() indicates the name should be included 
	 * in the listing. 
	 */
	public void test_should_display() {
		moname.setName("Monkeybats");
		moname.setIsCommonName(true);
		moname.setIsDontList(false);

		// common - true, dont-list - false
		assertFalse(moname.shouldDisplay());

		moname.setIsDontList(true);
		
		// common - true, dont-list - true
		assertFalse(moname.shouldDisplay());
		
		moname.setIsCommonName(false);
		
		// common - false, dont-list - true
		assertFalse(moname.shouldDisplay());
		
		moname.setIsDontList(false);
		
		// common - false, dont-list - false
		assertTrue(moname.shouldDisplay());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		moname = null;
	}
}
