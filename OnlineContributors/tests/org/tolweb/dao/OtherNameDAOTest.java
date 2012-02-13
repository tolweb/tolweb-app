package org.tolweb.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;

public class OtherNameDAOTest extends ApplicationContextTestAbstract {
	private NodeDAO nodeDAO;
	private OtherNameDAO otherNameDAO;
	
	public OtherNameDAOTest(String name) {
		super(name);
		nodeDAO = (NodeDAO)context.getBean("workingNodeDAO");
		otherNameDAO = (OtherNameDAO)context.getBean("workingOtherNameDAO");
	}
	
	public void testOtherNameFetch() {
		MappedNode nd = nodeDAO.getNodeWithId(new Long(9164));
		Map<Long, String> otherNames = otherNameDAO.getOtherNamesInfoForNode(nd);
		assertNotNull(otherNames);
		otherNames = otherNameDAO.getOtherNamesInfoForNode(new Long(9164));
		assertNotNull(otherNames);
	}
	
	public void testOtherNameEquals() {
		MappedOtherName oname1 = createOtherName(2);
		MappedOtherName oname2 = createOtherName(2);
		oname2.setIsImportant(false);
		oname2.setIsPreferred(false);
		assertTrue(oname1.equals(oname2));
	}
	
	@SuppressWarnings("unchecked")
	public void testNewOtherNameColumns() {
		List results = nodeDAO.findNodesExactlyNamed("Vombatidae");
		MappedNode vombatidae = (MappedNode) results.get(0); 
		assertNotNull(vombatidae);
		int synCount = vombatidae.getSynonyms().size();
		MappedOtherName newName = createOtherName(synCount);
		String oname = newName.getName();
		vombatidae.getSynonyms().add(newName);
		nodeDAO.saveNode(vombatidae);
		
		MappedNode updated = nodeDAO.getNodeWithId(vombatidae.getNodeId());
		assertNotNull(updated);
		assertNotNull(updated.getSynonyms());
		MappedOtherName nameAdded = null;
		for (Iterator itr = updated.getSynonyms().iterator(); itr.hasNext(); ) {
			MappedOtherName moname = (MappedOtherName) itr.next();
			if (moname.getName().equals(oname)) {
				nameAdded = moname;
				break;
			}
		}
		assertNotNull(nameAdded);
		assertTrue(nameAdded.getIsCommonName());
		assertTrue(nameAdded.getIsImportant());
		assertTrue(nameAdded.getIsDontList());
		assertTrue(nameAdded.getIsPreferred());
	}
	
	@SuppressWarnings("unchecked")
	public void testUpdateNewOtherNameColumns() {
		List results = nodeDAO.findNodesExactlyNamed("Vombatidae");
		MappedNode vombatidae = (MappedNode) results.get(0); 
		assertNotNull(vombatidae);
		
		int firstCount = vombatidae.getSynonyms().size();
		List ihatesets = new ArrayList(vombatidae.getSynonyms());
		for (int i = 0; i < firstCount; i++) {
			if (i % 2 == 0) {
				MappedOtherName moname = (MappedOtherName) ihatesets.get(i);
				moname.setIsImportant(false);
				moname.setIsCommonName(false);
			}
		}

		System.out.println("##### Saving Node #########");
		// don't remove the magic line of code (explanation below) - it'll break everything
		vombatidae.setSynonyms(new TreeSet(vombatidae.getSynonyms()));
		nodeDAO.saveNode(vombatidae);

		/* Explanation: if you don't do this mutation of the collection, Hibernate will 
		 * attempt to execute SQL that deletes on the old version of the OtherNames.   
		 * The word "attempt" is used because the SQL executed has a flaw related to the 
		 * handling of 'null' - it includes fields that have null as the value in the 
		 * where clause of the delete (... where auth_year=null).  Is will never work, 
		 * but is apparently valid SQL.  SO!  If you set the synonyms to a new instance 
		 * of a collection containing the old elements, it will cause Hibernate to delete 
		 * the *entire* collection and re-insert the elements.  This delete succeeds 
		 * because it's only using the node ID ("DELETE FROM OTHERNAMES WHERE node_id = ?").   
		 */		
		assertEquals(firstCount, vombatidae.getSynonyms().size());
		
		System.out.println("##### Fetching Updated Node #########");
		MappedNode updated = nodeDAO.getNodeWithId(vombatidae.getNodeId());
		int count = updated.getSynonyms().size();
		
		assertEquals(firstCount, count);
		
		ihatesets = new ArrayList(updated.getSynonyms());
		for (int i = 0; i < count; i++) {
			if (i % 2 == 0) {
				MappedOtherName moname = (MappedOtherName) ihatesets.get(i);
				assertFalse(moname.getIsImportant());
				assertFalse(moname.getIsCommonName());
			}
		}

	}

	private MappedOtherName createOtherName(int count) {
		MappedOtherName newName = new MappedOtherName();
		String oname = "New Other Name " + count;
		newName.setName(oname);
		newName.setIsCommonName(true);
		newName.setIsImportant(true);
		newName.setIsDontList(true);
		newName.setIsPreferred(true);
		newName.setOrder(count);
		return newName; 
	}
}
