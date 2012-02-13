
package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;

/**
*Given a node, collapse all nodes within that node's 1-page-depth subtree.
*In other words: identify all the nodes with pages that are immediate 
*descendants of the given node (no intermediate node with page exists), and 
*collapse all nodes on the paths to those immediate-descendant nodes, so that
*the result is a bush of nodes-with-pages tied directly to the given node.
*<p>
* If nodes 
*        c, d, f, and g have pages, and node a is given as argument
*<pre>
*        ,--c             ,--c
*    --b|                |
*   |   `--d             |---d
* a|             --->   a|
*  |    ,--f             |---f
*  `--e|                 |
*      `--g              `--g
*</pre>
*/
public class CollapseSubTreeUndoableEdit extends ZombiedNodesUndoableEdit {

    /**
	 * 
	 */
	private static final long serialVersionUID = -341341718238278811L;
	private Node node;
    private ArrayList affectedNodes;
    private boolean oldChildrenChanged;

    public String toString() {
        return "CollapseSubTreeUndoableEdit of " + node + " zombiedNodes = " + zombiedNodes + " oldTreeString = " + oldTreeString;
    }
    
    /** 
     *param n Node upon which to collapse children
     */
    public CollapseSubTreeUndoableEdit(Node n) {
        node = n;
        affectedNodes = new ArrayList();
        affectedNodes.add(n);
        
        oldChildrenChanged = n.getChildrenChanged();
        redo();
    }    
    
    /**
     *Collapse.  Lots of child removing and parent/child reordering
     */
    public void redo() {
        Stack childStack = new Stack();
        childStack.push(node);
        node.setChildrenChanged(true);
        
        while (!childStack.empty()) {
            Node currentNode = (Node) childStack.pop();
            Tree tree = TreePanel.getTreePanel().getTree();
            
            if ( currentNode != node &&  (currentNode.hasPage()  || tree.isTerminalNode(currentNode)) ) {
                currentNode.getParent().removeChild(currentNode);
                currentNode.setParent(node);
                node.addToChildren(currentNode);
            } else {
                //internal to the subtree
                Stack tempStack = new Stack();
                Iterator it = currentNode.getChildren().iterator();
                while (it.hasNext()) {
                    tempStack.push(it.next());
                }
                while (!tempStack.empty()) {  
                    //loop this way to get chldren in correct order
                    childStack.push(tempStack.pop());
                }

                
                if (currentNode != node) { 
                    currentNode.getParent().removeChild(currentNode);
                    NodeGraveyard.getGraveyard().addNode(currentNode);
                    zombiedNodes.add(currentNode);
                }
            }
            
        }
        // The first time through we want to do the pretty X animation for 
        // deleted nodes, so don't update the tree panel immediately
        if (!fromConstructor) {
            updateTreePanel();   
        } else {
            fromConstructor = false;
        }
    }
    
    /**
     *Decollapse - pull nodes out of graveyard, and rebuild from treestring
     */
    public void undo() {
        node.setChildrenChanged(oldChildrenChanged);
        
        Iterator it = zombiedNodes.iterator();

        while( it.hasNext() ) {
            Node curNode = (Node)it.next();
            NodeGraveyard.getGraveyard().getNode(curNode.getId());    
        }
        
        TreePanel.getTreePanel().getTree().updateTree(oldTreeString);
        updateTreePanel();        
    }

    /**
     *rebuild treepanel after structure has changed
     */
    public void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.setActiveNode(node);        
        
        TreePanelUpdateManager.getManager().rebuildTreePanels(affectedNodes);
    }    
    
    public String getPresentationName() {
        return "Collapse Subtree of " + node.getName();
    }
           
}

