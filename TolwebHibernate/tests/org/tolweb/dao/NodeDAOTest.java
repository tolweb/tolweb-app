/*
 * NodeDAOTest.java
 *
 * Created on May 4, 2004, 3:24 PM
 */

package org.tolweb.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.treegrow.tree.OtherName;

/**
 *
 * @author  dmandel
 */
public class NodeDAOTest extends ApplicationContextTestAbstract {
    private NodeDAO dao;
    private NodeDAO publicNodeDao;
    private NodePusher nodePusher;
    
    public NodeDAOTest(String name) {
        super(name);
        dao = (NodeDAO) context.getBean("workingNodeDAO");
        publicNodeDao = (NodeDAO) context.getBean("publicNodeDAO");
        nodePusher = (NodePusher) context.getBean("nodePusher");
    }
    
    public void testCreateNewNodeAncestors() {
        Set newAncestorsSet = new HashSet();
        newAncestorsSet.add(new Long(1));
        newAncestorsSet.add(new Long(2));
        newAncestorsSet.add(new Long(3));
        dao.resetAncestorsForNode(new Long(3), newAncestorsSet);
        assertEquals(dao.getAncestorsForNode(new Long(3)).size(), 3);
    }
    
    public void testGetNodeIsAncestor() {
        // Life is an ancestor of eukaryotes
        assertTrue(dao.getNodeIsAncestor(new Long(3), new Long(1)));
        // Eukaryotes is not an ancestor of life
        assertFalse(dao.getNodeIsAncestor(new Long(1), new Long(3)));
    }
    
    public void testPushingNodes() throws Exception {
        MappedNode archaea = (MappedNode) dao.findNodesExactlyNamed("Archaea").get(0);
        MappedOtherName newName = new MappedOtherName();
        newName.setName("The coolest archaea.  Ever.");
        newName.setAuthority("Daniel J. Mandel, esq. PhD.");
        newName.setAuthorityYear(new Integer(2004));
        int value = ((MappedOtherName) archaea.getSynonyms().last()).getOrder() + 1;
        newName.setOrder(value);
        newName.setIsPreferred(true);
        newName.setIsImportant(false);
        archaea.getSynonyms().add(newName);
        dao.saveNode(archaea);
        nodePusher.pushNodeToDB(archaea, publicNodeDao);
        MappedNode publicArchaea = (MappedNode) publicNodeDao.findNodesExactlyNamed("Archaea").get(0);
        checkNodeValues(archaea, publicArchaea);
        MappedNode hobbit = createNewHobbitNode(); 
        dao.saveNode(hobbit);
        nodePusher.pushNodeToDB(hobbit, publicNodeDao);
        hobbit = (MappedNode) dao.findNodesExactlyNamed("Homo hobbitus").get(0);
        MappedNode publicHobbit = publicNodeDao.getNodeWithId(hobbit.getNodeId());
        checkNodeValues(hobbit, publicHobbit);
    }
    
    private MappedNode createNewHobbitNode() {
        MappedNode hobbit = new MappedNode();
        hobbit.setName("Homo hobbitus");
        hobbit.setParentNodeId(new Long(1));
        hobbit.setPageId(new Long(1));
        hobbit.setOrderOnParent(new Integer(5));
        return hobbit;
    }
    
    private void checkNodeValues(MappedNode node1, MappedNode node2) {
        assertEquals(node1.getNodeId(), node2.getNodeId());
        assertEquals(node1.getPageId(), node2.getPageId());
        assertEquals(node1.getParentNodeId(), node2.getParentNodeId());
        assertEquals(node1.getAuthorityDate(), node2.getAuthorityDate());
        assertEquals(node1.getOrderOnParent(), node2.getOrderOnParent());
        assertEquals(node1.getNodeRankInteger(), node2.getNodeRankInteger());
        assertEquals(node1.getIsSubmitted(), node2.getIsSubmitted());
        assertEquals(node1.getExtinct(), node2.getExtinct());
        assertEquals(node1.getPhylesis(), node2.getPhylesis());
        assertEquals(node1.getConfidence(), node2.getConfidence());
        assertEquals(node1.getIsLeaf(), node2.getIsLeaf());
        assertEquals(node1.getName(), node2.getName());
        assertEquals(node1.getNameAuthority(), node2.getNameAuthority());
        assertEquals(node1.getShowPreferredAuthority(), node2.getShowPreferredAuthority());
        assertEquals(node1.getShowNameAuthority(), node2.getShowNameAuthority());
        assertEquals(node1.getShowImportantAuthority(), node2.getShowImportantAuthority());
        if (node1.getSynonyms() != null || node2.getSynonyms() != null) {
	        Iterator it1 = node1.getSynonyms().iterator(), it2 = node2.getSynonyms().iterator();
	        while (it1.hasNext()) {
	            MappedOtherName name1 = (MappedOtherName) it1.next();
	            MappedOtherName name2 = (MappedOtherName) it2.next();
	            assertEquals(name1.getAuthorityYear(), name2.getAuthorityYear());
	            assertEquals(name1.getOrder(), name2.getOrder());
	            assertEquals(name1.getName(), name2.getName());
	            assertEquals(name1.getAuthority(), name2.getAuthority());
	            assertEquals(name1.getIsImportant(), name2.getIsImportant());
	            assertEquals(name1.getIsPreferred(), name2.getIsPreferred());
	        }
        }
        assertEquals(node1.getDescription(), node2.getDescription());
        assertEquals(node1.getDontPublish(), node2.getDontPublish());
    }
    
    public void testFindNodeIds() {
        List results = dao.findNodesNamed("Life on Earth", true);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), new Long(1));
    }
    
    public void testFindNodesExactly() {
        runAssertions(dao.findNodesNamed("Life on Earth"));
    }
    
    public void testFindNodesByOtherName() {
        runAssertions(dao.findNodesExactlyNamed("Life"));
    }
    
    public void testFindNodesBySubstring() {
        runAssertions(dao.findNodesNamed("Lif"));
    }
    
    public void testFindMultipleNodes() {
    	List results = dao.findNodesNamed("eu");
    	assertEquals(results.size(), 2);
    }
    
    public void testGetNumNodesNamed() {
        Integer results = dao.getNumNodesNamed("eu");
        assertEquals(results.intValue(), 6);
    }
    
    public void testGetNumNodesExactlyNamed() {
        dao.getNumNodesExactlyNamed("Life");
    }
    
   
    /*public void testAncestors() {
        MappedNode eukaryotes = (MappedNode) dao.findNodesExactlyNamed("Eukaryotes").get(0);
        assertEquals(eukaryotes.getAncestors().size(), 2);
        MappedNode firstAncestor = (MappedNode) eukaryotes.getAncestors().iterator().next();
        assertTrue(firstAncestor.getName().equals("Life on Earth") || firstAncestor.getName().equals("Eukaryotes"));/
    }*/
    
    
    public void testPagesForNodes() {
        MappedNode life = (MappedNode) dao.findNodesExactlyNamed("Life").get(0);
        assertEquals(life.getPageId(), new Long(1));
    }
    
    public void testGetChildrenNodes() {
        List list = dao.getChildrenNodes((MappedNode) dao.findNodesExactlyNamed("Life").get(0));
        Iterator it = list.iterator();
        MappedNode nextNode = (MappedNode) it.next();
        // The first one is life.  Skip it because it's just there to make the test data work correctly
        nextNode = (MappedNode) it.next();
        assertEquals(nextNode.getName(), "Eubacteria");
        nextNode = (MappedNode) it.next();
        assertEquals(nextNode.getName(), "Eukaryotes");
        nextNode = (MappedNode) it.next();
        assertEquals(nextNode.getName(), "Archaea");
        nextNode = (MappedNode) it.next();
        assertEquals(nextNode.getName(), "Viruses");
    }
    
    private void runAssertions(List list) {
        assertTrue(list.size() == 1);
        MappedNode life = (MappedNode) list.get(0);
        assertEquals(life.getNodeId(), new Long(1));
        assertEquals(life.getName(), "Life on Earth");
        assertTrue(life.getSynonyms().size() == 1);
        OtherName name = (OtherName) new Vector(life.getSynonyms()).get(0);
        assertEquals(name.getName(), "Life");
    }
}
