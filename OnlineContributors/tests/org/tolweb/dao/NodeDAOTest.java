/*
 * NodeDAOTest.java
 *
 * Created on May 4, 2004, 3:24 PM
 */

package org.tolweb.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

import org.apache.commons.lang.time.DateUtils;
import org.tolweb.hibernate.ExtendedNodeProperties;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.tree.OtherName;

/**
 *
 * @author  dmandel
 */
public class NodeDAOTest extends ApplicationContextTestAbstract {
    private NodeDAO dao;
    private NodeDAO miscDAO;
    public NodeDAOTest(String name) {
        super(name);
        dao = (NodeDAO) context.getBean("workingNodeDAO");
        miscDAO = (NodeDAO) context.getBean("nodeDAO");
    }
    
    public void test_activation_bookkeeping_properties_committed() {
    	MappedNode mnode = dao.getNodeWithId(Long.valueOf(15994L), true);
    	assertNotNull(mnode);
    	
    	mnode.setStatus(MappedNode.ACTIVE);
    	mnode.setStatus(MappedNode.INACTIVE);
    	mnode.setStatus(MappedNode.RETIRED);
    	
    	assertNotNull(mnode.getLastInactivated());
    	assertNotNull(mnode.getLastActivated());
    	assertNotNull(mnode.getLastRetired());
    	
    	mnode.setStatus(MappedNode.ACTIVE);
    	
    	Date lastInactive = mnode.getLastInactivated();
    	Date lastActive = mnode.getLastActivated();
    	Date lastRetire = mnode.getLastRetired();
    	
    	dao.saveNode(mnode);
    	
    	mnode = dao.getNodeWithId(Long.valueOf(15994L), true);

    	assertTrue(DateUtils.isSameDay(lastInactive, mnode.getLastInactivated()));
    	assertTrue(DateUtils.isSameDay(lastActive, mnode.getLastActivated()));
    	assertTrue(DateUtils.isSameDay(lastRetire, mnode.getLastRetired()));
    	
    	mnode.setLastInactivated(null);
    	mnode.setLastActivated(null);
    	mnode.setLastRetired(null);
    	
    	dao.saveNode(mnode);
    	
    	assertNull(mnode.getLastInactivated());
    	assertNull(mnode.getLastActivated());
    	assertNull(mnode.getLastRetired());
    	
    	mnode = dao.getNodeWithId(Long.valueOf(15994L), true);
    	
    	assertNull(mnode.getLastInactivated());
    	assertNull(mnode.getLastActivated());
    	assertNull(mnode.getLastRetired());
    	
    }
    
    public void test_italicized_single_child_with_no_page() {
    	// 131099
    	MappedNode mnode = dao.getNodeWithId(Long.valueOf(131099L));
    	SortedSet ss = mnode.getSynonyms();
    	assertEquals("Critterus proboscidea", mnode.getName());
    	String pageSub = mnode.getPageSubtitle();
    	String pageTitle = mnode.getPageTitle();
    	String pageSuper = mnode.getPageSupertitle();
    	//assertEquals("<em>Critterus proboscidea</em>", pageSub);
    	assertEquals("<em>Critterus proboscidea</em>", pageTitle);
    	//assertEquals("<em>Critterus proboscidea</em>", pageSuper);
    }
    
    public void testExtendedNodeProperties() {
    	List wombats = dao.findNodesExactlyNamed("Vombatidae");
    	assertNotNull(wombats);
    	assertFalse(wombats.isEmpty());
    	assertTrue(wombats.size() == 1);
    	MappedNode wombat = (MappedNode)wombats.get(0);
    	wombat.setExtendedNodeProperties(new ExtendedNodeProperties());
    	assertTrue(wombat.getExtendedNodeProperties() != null);
    	wombat.getExtendedNodeProperties().setGeoDistDescription("somewhere in Australia... lots of places... like Victoria");
    	dao.saveNode(wombat);
    	MappedNode updated = dao.getNodeWithId(wombat.getNodeId());
    	assertTrue(updated.getExtendedNodeProperties() != null);
    	assertTrue(updated.getExtendedNodeProperties().getGeoDistDescription() != null);
    	assertEquals("somewhere in Australia... lots of places... like Victoria", 
    			updated.getExtendedNodeProperties().getGeoDistDescription());
    }
    
//    public void testFindNodesExactly() {
//        runAssertions(dao.findNodesNamed("Life on Earth"));
//    }
//    
//    public void testFindNodesByOtherName() {
//        runAssertions(dao.findNodesExactlyNamed("Life"));
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void testFindNodesBySubstring() {
//        List results = dao.findNodesNamed("Lif");
//        assertTrue(results.size() >= 1);
//        MappedNode life = new MappedNode(); 
//        for (Iterator itr = results.iterator(); itr.hasNext(); ) {
//        	life = (MappedNode) itr.next();
//        	if (life.getName().equals("Life on Earth")) {
//        		break;
//        	}
//        }
//        assertEquals(life.getNodeId(), new Long(1));
//        assertEquals(life.getName(), "Life on Earth");
//        assertTrue(life.getSynonyms().size() == 1);
//        OtherName name = (OtherName) new Vector(life.getSynonyms()).get(0);
//        assertEquals(name.getName(), "Life");        
//    }
    
    @SuppressWarnings("unchecked")
    public void testFindMultipleNodes() {
    	List results = dao.findNodesNamed("formes");
    	assertTrue(results.size() >= 121);
    }
    
    @SuppressWarnings("unchecked")
    private void runAssertions(List list) {
        assertTrue(list.size() >= 1);
        MappedNode life = (MappedNode) list.get(0);
        assertEquals(life.getNodeId(), new Long(1));
        assertEquals(life.getName(), "Life on Earth");
        assertTrue(life.getSynonyms().size() == 1);
        OtherName name = (OtherName) new Vector(life.getSynonyms()).get(0);
        assertEquals(name.getName(), "Life");
    }
    
    public void testAncestors() {
        MappedNode eukaryotes = (MappedNode) dao.findNodesExactlyNamed("Eukaryotes").get(0);
        assertTrue(eukaryotes != null);
        int ancestorCount = miscDAO.getAncestorCount(eukaryotes.getNodeId());
        assertEquals(2, ancestorCount);
        Set s = miscDAO.getAncestorsForNode(eukaryotes.getNodeId());
        Long firstAncestorId = (Long) s.iterator().next();
        assertTrue(firstAncestorId.equals(new Long(1)) || firstAncestorId.equals(new Long(2)));
    }
    
//    public void testPagesForNodes() {
//        MappedNode life = (MappedNode) dao.findNodesExactlyNamed("Life").get(0);
//        assertEquals(life.getPageId(), new Long(1));
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void testGetChildrenNodes() {
//    	List<String> expected = new ArrayList<String>(
//    			Arrays.asList(new String[] {"Eubacteria", "Eukaryotes", "Archaea", "Viruses"}));
//    	List list = dao.getChildrenNodes((MappedNode) dao.findNodesExactlyNamed("Life").get(0));
//        Iterator it = list.iterator();
//        MappedNode nextNode;
//        nextNode = (MappedNode) it.next();
//        assertTrue(expected.contains(nextNode.getName()));
//        nextNode = (MappedNode) it.next();
//        assertTrue(expected.contains(nextNode.getName()));
//        nextNode = (MappedNode) it.next();
//        assertTrue(expected.contains(nextNode.getName()));
//        nextNode = (MappedNode) it.next();
//        assertTrue(expected.contains(nextNode.getName()));
//    }
}
