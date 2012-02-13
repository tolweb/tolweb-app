/*
 * Created on Sep 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.hibernate.MappedNode;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CacheAccessTest extends ApplicationContextTestAbstract {
    private CacheAccess access;
    private NodeDAO nodeDao;
    
    public CacheAccessTest(String name) {
        super(name);
        access = (CacheAccess) context.getBean("cacheAccess");
        nodeDao = (NodeDAO) context.getBean("nodeDAO");
    }    
    
    public void testCacheAccess() {
        MappedNode life = nodeDao.getNodeWithId(new Long(1));
        MappedNode number2 = nodeDao.getNodeWithId(new Long(2));
        System.out.println("life is: " + life + " number 2 is: " + number2);
        runTests(life);
        runTests(number2);
    }
    
    private void runTests(MappedNode node) {
        //assertNull(access.getLinkedPagesForNode(node));
        assertNull(access.getOtherGroupsForNode(node));        
        String otherGroups = "some other groups for " + node.getName();
        //access.setLinkedPagesForNode(node, linkedPages);
        access.setOtherGroupsForNode(node, otherGroups);
        //assertEquals(access.getLinkedPagesForNode(node), linkedPages);
        assertEquals(access.getOtherGroupsForNode(node), otherGroups);        
    }

}
