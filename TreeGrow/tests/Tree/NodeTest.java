/*
 * NodeTest.java
 *
 * Created on May 6, 2004, 9:15 AM
 */

import junit.framework.TestCase;
import org.tolweb.treegrow.tree.Node;

/**
 *
 * @author  dmandel
 */
public class NodeTest extends TestCase {
    
    public void testEquals() {
        Node node1 = new Node();
        node1.setId(2);
        node1.setName("Bubba");
        
        Node node2 = new Node();
        node2.setId(2);
        node2.setName("Bubba");
        
        assertTrue(node1.equals(node2));
    }
}
