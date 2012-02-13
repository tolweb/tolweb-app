package org.tolweb.treegrow.tree;

import java.util.*;

/**
 * Nodes are stored here when they are removed, so that they can be 
 * retrieved in the case of an undo.
 */
public class NodeGraveyard {
    private static NodeGraveyard graveyard = new NodeGraveyard();
    private ArrayList nodes = new ArrayList();
    
    public static NodeGraveyard getGraveyard() {
        return graveyard;
    }
    
    /** 
     * Pull the node off the list of zombies, and put it back on the tree list.
     * Essentially brings the previously removed node back to life.
     */
    public Node getNode(int id) {        
        Node returnedNode = null;
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            if (node.getId() == id) {
                returnedNode = node;
                break;
            }
        }
        if (returnedNode != null) {
            nodes.remove(returnedNode);
            TreePanel.getTreePanel().getTree().addNode(returnedNode);
            //returnedNode.unZombie();
            TreePanelUpdateManager.getManager().unZombieNode(returnedNode);
        }
        
        return returnedNode;
    }

    /** 
     * Pull the node off the list of live nodes, and put it back on the zombie 
     * list. Essentially buries the node...in such a way that it can be 
     * retrieved if necessary.
     */    
    public void addNode(Node node) {
        //node.zombie(); 
        TreePanelUpdateManager.getManager().zombieNode(node);
        TreePanel.getTreePanel().getTree().removeNode(node);
        nodes.add(node);
    }
}
