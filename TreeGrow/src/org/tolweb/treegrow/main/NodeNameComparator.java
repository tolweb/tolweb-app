/*
 * NodeNameComparator.java
 *
 * Created on March 12, 2004, 9:37 AM
 */

package org.tolweb.treegrow.main; 

import java.util.*;
import org.tolweb.treegrow.tree.*;
        
/**
 * Used to sort nodes by their names
 */
public class NodeNameComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        Node node1 = (Node) o1;
        Node node2 = (Node) o2;

        return node1.getName().compareTo(node2.getName());
    }
}

