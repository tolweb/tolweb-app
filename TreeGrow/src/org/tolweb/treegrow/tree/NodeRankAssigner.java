/*
 * Created on Apr 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NodeRankAssigner {
    public static List recursivelyAddNodes(List list, Node root, Tree tree, Node actualRoot, int nodeRank, boolean ignoreExisting, boolean setNodeRankChanged) {
        if (list == null) {
            list = new ArrayList();
        }
        Iterator it = root.getChildren().iterator();
        // Add nodes recursively if the current root node doesn't have a 
        // page or if it's not a terminal node and it has children or if
        // the actual root is a terminal node, then add its children or it's
        // the actual root node of the page
        if ((root == actualRoot) || !root.hasPage() && !tree.isTerminalNode(root) || tree.isTerminalNode(actualRoot)) {
            while (it.hasNext()) {
                Node next = (Node) it.next();
                if (StringUtils.notEmpty(next.getName())) {
                    OtherName fakeParent = next.getFirstPreferredOtherName();
                    if (fakeParent != null) {
                        list.add(fakeParent);
                    }
                    list.add(next);
                }                
                if (next.getNodeRank() == -1 || ignoreExisting) {
                    next.setNodeRank(nodeRank);
                } else {
                    Node ancestorNode = getAncestorNode(next, list);

                    // Here, a node rank was previously set, so don't overwrite
                    // it, but make sure that we are at least 1 more than our parent
                    if (ancestorNode != null && ancestorNode.getNodeRank() == next.getNodeRank()) {
                        next.setNodeRank(next.getNodeRank() + 1);
                    }
                }
                if (setNodeRankChanged) {
                    next.setNodeRankChanged(true);
                }
                int rankIncrement = StringUtils.notEmpty(next.getName()) ? 1 : 0;
                recursivelyAddNodes(list, next, tree, actualRoot, nodeRank + rankIncrement, ignoreExisting, setNodeRankChanged);
            }
        }
        return list;
    }
    
    /**
     * Returns the nearest ancestor that is in the list
     *
     * @param node The node to find the ancestor for
     * @return The nearest ancestor in the list, or null if there is none
     */ 
    private static Node getAncestorNode(Node currentNode, List list) {
        Node ancestorNode = currentNode.getParent();
        
        while (ancestorNode != null) {
            if (list.contains(ancestorNode)) {
                break;
            }
            ancestorNode = ancestorNode.getParent();
        }
        return ancestorNode;
    }    
}
