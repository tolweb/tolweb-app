package org.tolweb.hibernate;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import org.tolweb.treegrow.main.NodeImage;

import junit.framework.TestCase;

public class MappedNodeTest extends TestCase {
	private MappedNode mnode; 
	
	@SuppressWarnings("unchecked")
	public void setUp() {
		mnode = new MappedNode();
		mnode.setName("Vombatidae");
		mnode.setNodeId(new Long(650));
		mnode.setIsLeaf(false);
		mnode.setStatus(MappedNode.ACTIVE);
		mnode.setSynonyms(new TreeSet());
	}
	
	/**
	 * For some reason many of the Hibernate mapped 
	 * objects rely simpling on the default inherited 
	 * constructor (well, the reason for this goes back 
	 * to the practice of hibernate mapping POJOs, or 
	 * Plain Old Java Objects, and one of the assumed 
	 * things is that you're following something close 
	 * to the old Java Beans spec, where you have getters 
	 * & setters for all the properties - and you have 
	 * a paren-paren, default constructor).  Anyway, 
	 * this means at times things might not be configured 
	 * correctly outside of the Spring container or when 
	 * you're not using hibernate to get the instances - 
	 * BUT, when Hibernate & Spring are involved, things 
	 * are all fine and rosy.  These Unit Tests are not 
	 * meant as "integration" tests just yet - which is 
	 * to say, the tests do not involve Hibernate & Spring 
	 * - and, time-permitting, integration tests will be 
	 * put in place to test the configuration and 
	 * functionality of these object under the influence 
	 * of those two APIs. 
	 */
	public void test_initial_object_state() {
		MappedNode nd = new MappedNode();
		
		// MappedNode has some issues with the Identity 
		// property.  The super class has an old id 
		// property that isn't really used anymore, which 
		// I guess explains by the super.getId() isn't used 
		// for seeding getNodeId(), you see that getId() is 
		// overrode in MappedNode to return the int value of 
		// the Long representing the node-id. This is far 
		// from clean - but changing this way one or another 
		// might land you in some quicksand (perhaps).  This 
		// is merely documentation - and a polite "good luck" 
		// if you decide to clean it up, it's likely not 
		// going to cause much issue (and, as Danny Mandel 
		// used to say, if you're not breaking stuff - you're 
		// not working). 
		assertEquals(null, nd.getNodeId());
		try {
			assertEquals(0, nd.getId());
			fail();
		} catch(NullPointerException npe) { 
			/* constructor allows the id to be null */ 
		}
		
		assertEquals("", nd.getName());
		assertEquals(MappedNode.NOT_EXTINCT, nd.getExtinct());
		assertEquals(MappedNode.ACTIVE, nd.getStatus());
		assertEquals(Long.valueOf(0), nd.getTreeOrder());
	}
	
	@SuppressWarnings("unchecked")
	public void test_othername_added() {
		MappedOtherName othername = createOtherName("Wombats", null, null, 0);
		boolean retVal = mnode.addSynonym(othername);
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(retVal);
		Iterator itr = mnode.getSynonyms().iterator(); 
		assertEquals(othername.getName(), ((MappedOtherName)itr.next()).getName());
	}
	
	@SuppressWarnings("unchecked")
	public void test_add_bunch_of_othernames() {
		boolean retVal = mnode.addSynonym(createOtherName("Wombats", null, null, 0));
		assertTrue(retVal);
		retVal = mnode.addSynonym(createOtherName("Monkeybone-finders", null, null, 1));
		assertTrue(retVal);
		retVal = mnode.addSynonym(createOtherName("Warbats", null, null, 2));
		assertTrue(retVal);
		retVal = mnode.addSynonym(createOtherName("Deathmongers", null, null, 3));
		assertTrue(retVal);
		// same name - no new info 
		retVal = mnode.addSynonym(createOtherName("Warbats", null, null, 4));
		assertTrue(!retVal);
		// same name & same order
		retVal = mnode.addSynonym(createOtherName("Warbats", null, null, 2));
		assertTrue(!retVal);
		retVal = mnode.addSynonym(createOtherName("DeathMongers", null, null, 4));
		assertTrue(retVal);
	}
	
	@SuppressWarnings("unchecked")
	public void test_cannot_add_duplicate_othernames_via_new_method() {
		MappedOtherName othername1 = createOtherName("Wombats", null, null, 0);
		MappedOtherName othername2 = createOtherName("Wombats", null, null, 1);
		
		boolean retVal = mnode.addSynonym(othername1);
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		assertTrue(retVal);
		
		retVal = mnode.addSynonym(othername2);
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		assertTrue(!retVal);
	}	
	
	public void test_merge_authority_info_for_names() {
		// create an element w/o auth info 
		MappedOtherName othername1 = createOtherName("Wombats", null, null, 0);
		boolean retVal = mnode.addSynonym(othername1);
		// verify it made it into synonyms
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		assertTrue(retVal);
		
		// create a 2nd element with auth info 
		MappedOtherName othername2 = createOtherName("Wombats", "Burnett", 1839, 1);		
		retVal = mnode.addSynonym(othername2);
		// verify the 2nd element wasn't added
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		// True is returned because the element in synonyms is "modified" 
		// but not is not added to the synonyms.  The thought is that if 
		// you're using the return value to decide whether to do a save 
		// of the object, if it changed you want to do a save - the true 
		// indicates that a change happened.  Not sure how I totally feel 
		// about this... sticking with it at the moment. 
		assertTrue(retVal);
		
		// get the actual element to do some checking on
		Iterator itr = mnode.getSynonyms().iterator(); 
		MappedOtherName actual = (MappedOtherName)itr.next();
		// sanity check 
		assertEquals(othername2.getName(), actual.getName());
		assertEquals(othername1.getName(), actual.getName());
		// THE MEAT - verify the auth info was merged into element in synonyms
		assertEquals(othername2.getAuthorityYear(), actual.getAuthorityYear());
		assertEquals(othername2.getAuthority(), actual.getAuthority());
		// make sure it's really "othername1" and not a direct copy
		assertEquals(othername1.getOrder(), actual.getOrder());
	}
	
	public void test_merge_partial_authority_info_for_authority() {
		MappedOtherName othername1 = createOtherName("Wombats", null, null, 0);
		boolean retVal = mnode.addSynonym(othername1);
		// verify it made it into synonyms
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		assertTrue(retVal);
		
		MappedOtherName othername2 = createOtherName("Wombats", "Burnett", null, 1);		
		mnode.addSynonym(othername2);
		// verify the 2nd element wasn't added
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);

		Iterator itr = mnode.getSynonyms().iterator(); 
		MappedOtherName actual = (MappedOtherName)itr.next();
		assertEquals(othername1.getName(), actual.getName());
		assertEquals(othername2.getAuthority(), actual.getAuthority());
		assertTrue(actual.getAuthorityYear() == null);
		assertEquals(othername1.getOrder(), actual.getOrder());		
	}
	
	public void test_merge_partial_authority_info_for_authYear() {
		MappedOtherName othername1 = createOtherName("Wombats", null, null, 0);
		boolean retVal = mnode.addSynonym(othername1);
		// verify it made it into synonyms
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		assertTrue(retVal);
		
		MappedOtherName othername2 = createOtherName("Wombats", null, 1839, 1);		
		mnode.addSynonym(othername2);
		// verify the 2nd element wasn't added
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);

		Iterator itr = mnode.getSynonyms().iterator(); 
		MappedOtherName actual = (MappedOtherName)itr.next();
		assertEquals(othername1.getName(), actual.getName());
		assertEquals(othername2.getAuthorityYear(), actual.getAuthorityYear());
		assertTrue(actual.getAuthority() == null);
		assertEquals(othername1.getOrder(), actual.getOrder());		
	}	
	
	public void test_names_with_differing_authority_info_are_not_changed() {
		MappedOtherName othername1 = createOtherName("Wombats", "Shaw", 1844, 0);
		boolean retVal = mnode.addSynonym(othername1);
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		assertTrue(retVal);
		
		MappedOtherName othername2 = createOtherName("Wombats", "Burnett", 1839, 1);		
		retVal = mnode.addSynonym(othername2);
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 2);
		assertTrue(retVal);
		
		Iterator itr = mnode.getSynonyms().iterator(); 
		MappedOtherName actual = (MappedOtherName)itr.next();
		
		assertEquals(othername1.getName(), actual.getName());
		assertEquals(othername1.getAuthority(), actual.getAuthority());
		assertEquals(othername1.getAuthorityYear(), actual.getAuthorityYear());
		assertEquals(othername1.getOrder(), actual.getOrder());
		
		actual = (MappedOtherName)itr.next();
		assertEquals(othername2.getName(), actual.getName());
		assertEquals(othername2.getAuthority(), actual.getAuthority());
		assertEquals(othername2.getAuthorityYear(), actual.getAuthorityYear());
		assertEquals(othername2.getOrder(), actual.getOrder());
	}

	public void test_new_name_with_incomplete_authority_info_ignored() {
		MappedOtherName othername1 = createOtherName("Wombats", "Burnett", 1839, 1);
		boolean retVal = mnode.addSynonym(othername1);
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		assertTrue(retVal);
		
		// create a 2nd element w/o auth info 
		MappedOtherName othername2 = createOtherName("Wombats", null, null, 2);
		retVal = mnode.addSynonym(othername2);
		// verify the 2nd element wasn't added
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		assertTrue(!retVal);
	}
	
	/**
	 * Verifies the old way of defining equality by their getOrder() value 
	 * still holds true. 
	 */
	@SuppressWarnings("unchecked")
	public void test_cannot_add_duplicate_othernames_based_on_order_for_equality() {
		MappedOtherName othername1 = createOtherName("Wombats", null, null, 0);
		MappedOtherName othername2 = createOtherName("Wombats", null, null, 0);
		mnode.getSynonyms().add(othername1);
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);
		mnode.getSynonyms().add(othername2);
		assertTrue(mnode.getSynonyms() != null);
		assertTrue(!mnode.getSynonyms().isEmpty());
		assertTrue(mnode.getSynonyms().size() == 1);		
	}

	// test the copyValues(MappedNode) & copyValues(MappedNode, boolean) to verify correctness
	
	/* fields/properties involved w/ copy values operations  
	    setPageId(other.getPageId());
	    setParentNodeId(other.getParentNodeId());
	    setAuthorityDate(other.getAuthorityDate());
	    setOrderOnParent(other.getOrderOnParent());
	    setNodeRankInteger(other.getNodeRankInteger());
	    setIsSubmitted(other.getIsSubmitted());
	    setExtinct(other.getExtinct());
	    setPhylesis(other.getPhylesis());
	    setConfidence(other.getConfidence());
	    setIsLeaf(other.getIsLeaf());
	    setName(other.getName());
	    setNameAuthority(other.getNameAuthority());
	    setShowPreferredAuthority(other.getShowPreferredAuthority());
	    setShowNameAuthority(other.getShowNameAuthority());
	    setShowImportantAuthority(other.getShowImportantAuthority());
		(and the merging of the synonyms collection)
	    setDescription(other.getDescription());
	    setDontPublish(other.getDontPublish());
        setOrderOnPage(other.getOrderOnPage());
        setShowAuthorityInContainingGroup(other.getShowAuthorityInContainingGroup());
        setItalicizeName(other.getItalicizeName());
        setNameComment(other.getNameComment());
        setIsNewCombination(other.getIsNewCombination());
        setCombinationAuthor(other.getCombinationAuthor());
        setCombinationDate(other.getCombinationDate());
        setHasIncompleteSubgroups(other.getHasIncompleteSubgroups());
        setRankName(other.getRankName());
        setStatus(other.getStatus()); 
	 */
	
	// test the equality implementation 
	public void test_equality_implementation() {
		MappedNode ndX = new MappedNode();
		ndX.setNodeId(Long.valueOf(650));
		
		MappedNode ndY = new MappedNode();
		ndY.setNodeId(Long.valueOf(650));
		
		MappedNode ndZ = new MappedNode();
		ndZ.setNodeId(Long.valueOf(650));
		
		assertFalse(ndX.equals(null));
		assertFalse(ndX.equals(new NodeImage()));
		
		// if x == x
		assertTrue(ndX.equals(ndX));
		
		// if x == y then y == x
		assertTrue(ndX.equals(ndY));
		assertTrue(ndY.equals(ndX));
		
		// if x == y, y == z then x == z
		assertTrue(ndX.equals(ndY));
		assertTrue(ndY.equals(ndZ));
		assertTrue(ndX.equals(ndZ));
		
		ndY.setNodeId(Long.valueOf(88));
		ndZ.setNodeId(Long.valueOf(99));
		
		// now, none of them are equal
		
		assertFalse(ndX.equals(ndY));
		assertFalse(ndY.equals(ndX));
		
		assertFalse(ndX.equals(ndY));
		assertFalse(ndY.equals(ndZ));
		assertFalse(ndX.equals(ndZ));		
	}

	public void test_activation_bookkeeping_properties() {
		mnode = new MappedNode();
		mnode.setName("Vombatidae");
		mnode.setNodeId(new Long(650));
		mnode.setIsLeaf(false);
		mnode.setStatus(MappedNode.ACTIVE);
		mnode.setSynonyms(new TreeSet<MappedOtherName>());

		// mnode was set to be ACTIVE when constructed, so it should not be null
		assertNotNull(mnode.getLastActivated());
		slightPause();
		Date now = new Date();
		assertTrue(mnode.getLastActivated().before(now));

		assertNull(mnode.getLastInactivated());
		assertNull(mnode.getLastRetired());

		slightPause();
		mnode.setStatus(MappedNode.INACTIVE);

		
		assertNotNull(mnode.getLastInactivated());
		assertTrue(mnode.getLastInactivated().after(mnode.getLastActivated()));
		slightPause();
		now = new Date();
		assertTrue(mnode.getLastInactivated().before(now));
		
		assertNull(mnode.getLastRetired());

		slightPause();
		mnode.setStatus(MappedNode.RETIRED);
		
		assertNotNull(mnode.getLastRetired());
		assertTrue(mnode.getLastRetired().after(mnode.getLastActivated()));
		assertTrue(mnode.getLastRetired().after(mnode.getLastInactivated()));
		slightPause();
		now = new Date();
		assertTrue(mnode.getLastRetired().before(now));
	}

	private void slightPause() {
		try {
			Thread.sleep(1050);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private MappedOtherName createOtherName(String name, String authority, Integer year, int order) {
		MappedOtherName moname = new MappedOtherName();
		moname.setName(name);
		moname.setAuthority(authority);
		moname.setAuthorityYear(year);
		moname.setOrder(order);
		return moname;
	}

	public void tearDown() {
		mnode = null;
	}
}
