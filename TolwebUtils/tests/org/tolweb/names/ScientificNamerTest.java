package org.tolweb.names;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;

import junit.framework.TestCase;

public class ScientificNamerTest extends TestCase {
	private MappedNode mnode; 
	private MappedOtherName moname;
	
	public void setUp() {
		mnode = new MappedNode();
		mnode.setName("Vombatidae");
		mnode.setNameAuthority("Burns");
		mnode.setAuthorityDate(new Integer(1788));
		mnode.setIsNewCombination(false);
		
		moname = new MappedOtherName();
		moname.setName("Wombats");
		moname.setAuthority("Burns");
		moname.setAuthorityYear(new Integer(1787));
	}
	
	public void test_easy_object_state() {
		ScientificNamer namer1 = new ScientificNamer(mnode);
		assertNotNull(namer1);
		assertEquals(namer1.getName(), "Vombatidae Burns 1788");
		
		ScientificNamer namer2 = new ScientificNamer(moname);
		assertNotNull(namer2);
		assertEquals(namer2.getName(), "Wombats Burns 1787");
	}
	
	public void test_null_authority() {
		mnode.setNameAuthority(null);
		
		ScientificNamer namer1 = new ScientificNamer(mnode);
		assertNotNull(namer1);
		assertEquals(namer1.getName(), "Vombatidae 1788");
		
		moname.setAuthority(null);
		ScientificNamer namer2 = new ScientificNamer(moname);
		assertNotNull(namer2);
		assertEquals(namer2.getName(), "Wombats 1787");
		
		mnode.setIsNewCombination(true);
		ScientificNamer namer3 = new ScientificNamer(mnode);
		assertNotNull(namer3);
		assertEquals(namer3.getName(), "Vombatidae (1788)");
	}
	
	public void test_null_year() {
		mnode.setAuthorityDate(null);
		assertNotNull(mnode);
		ScientificNamer namer1 = new ScientificNamer(mnode);
		assertNotNull(namer1);
		assertEquals(namer1.getName(), "Vombatidae Burns");
		
		moname.setAuthorityYear(null);
		ScientificNamer namer2 = new ScientificNamer(moname);
		assertNotNull(namer2);
		assertEquals(namer2.getName(), "Wombats Burns");
		
		mnode.setIsNewCombination(true);
		ScientificNamer namer3 = new ScientificNamer(mnode);
		assertNotNull(namer3);
		assertEquals(namer3.getName(), "Vombatidae (Burns)");		
	}
	
	public void test_null_authority_and_year() {
		mnode.setNameAuthority(null);
		mnode.setAuthorityDate(null);

		ScientificNamer namer1 = new ScientificNamer(mnode);
		assertNotNull(namer1);
		assertEquals(namer1.getName(), "Vombatidae");
		
		moname.setAuthority(null);
		moname.setAuthorityYear(null);
		ScientificNamer namer2 = new ScientificNamer(moname);
		assertNotNull(namer2);
		assertEquals(namer2.getName(), "Wombats");
		
		mnode.setIsNewCombination(true);
		ScientificNamer namer3 = new ScientificNamer(mnode);
		assertNotNull(namer3);
		assertEquals(namer3.getName(), "Vombatidae");		
	}
	
	public void test_null_everything() {
		mnode.setName(null);
		mnode.setNameAuthority(null);
		mnode.setAuthorityDate(null);

		ScientificNamer namer1 = new ScientificNamer(mnode);
		assertNotNull(namer1);
		assertEquals("null", namer1.getName());
		
		moname.setName(null);
		moname.setAuthority(null);
		moname.setAuthorityYear(null);
		ScientificNamer namer2 = new ScientificNamer(moname);
		assertNotNull(namer2);
		assertEquals("null", namer2.getName());
		
		mnode.setIsNewCombination(true);
		ScientificNamer namer3 = new ScientificNamer(mnode);
		assertNotNull(namer3);
		assertEquals("null", namer3.getName());		
	}

	public void test_empty_values() {
		mnode.setName("");
		mnode.setNameAuthority("");
		mnode.setAuthorityDate(new Integer(0));

		ScientificNamer namer1 = new ScientificNamer(mnode);
		assertNotNull(namer1);
		assertEquals("", namer1.getName());
		
		moname.setName("");
		moname.setAuthority("");
		moname.setAuthorityYear(new Integer(0));
		ScientificNamer namer2 = new ScientificNamer(moname);
		assertNotNull(namer2);
		assertEquals("", namer2.getName());
		
		mnode.setIsNewCombination(true);
		ScientificNamer namer3 = new ScientificNamer(mnode);
		assertNotNull(namer3);
		assertEquals("", namer3.getName());		
	}
	
	public void tearDown() {
		mnode = null;
		moname = null;
	}
}
