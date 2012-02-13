package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;


/**
*Given a node, identify the most recent common ancestor (MRCA) with a page.
*Collapse that node's decendants onto it (as in "CollapseSubTreeUndoableEdit", 
*collapsing all nodes within that node's 1-page-depth subtree), sparing 
*the input node...whose subtree should be allowed to retain it's structure
*<p>
*In other words: identify all the nodes with pages that are immediate 
*descendants of the given node (no intermediate node with page exists), and 
*collapse all nodes on the paths to those immediate-descendant nodes, so that
*the result is a bush of nodes-with-pages tied directly to the given node.
*<p>
* If nodes 
*        c, d, f, and g have pages, and node e is given as argument
*<pre>
*        ,--c             ,--c
*    --b|                |
*   |   `--d             |---d
* a|             --->   a|
*  |    ,--f             |    ,--f
*  `--e|                 `--e|
*      `--g                  `--g
*</pre>
*/
public class CollapseRestOfTreeUndoableEdit extends ZombiedNodesUndoableEdit {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5045723349639835610L;
	private Node node;
    private Node startNode;
    private ArrayList affectedNodes;
    private boolean oldStartNodeHasChanged;

    public String toString() {
        return "CollapseRestOfTreeUndoableEdit of " + node + " zombiedNodes = " + zombiedNodes + " oldTreeString = " + oldTreeString;
    }
    
    /** 
     *param n Node whose MRCA will be collapsed on
     */    
    public CollapseRestOfTreeUndoableEdit(Node n) {
        node = n;

        //walk down tree until we've reached the parent with a page (or the root)
        Node root = TreePanel.getTreePanel().getTree().getRoot();
        startNode = node.getParent();
        while (! startNode.hasPage() && startNode != root ) {
            startNode = startNode.getParent();
        }
        
        affectedNodes = new ArrayList();
        affectedNodes.add(startNode);
        
        oldStartNodeHasChanged =  startNode.getChildrenChanged();

        redo();        
    }    
    
    /**
     *Collapse.  Lots of child removing and parent/child reordering
     */    
    public void redo() {
        startNode.setChildrenChanged(true);
        
        Stack childStack = new Stack();
        childStack.push(startNode);
        Tree tree = TreePanel.getTreePanel().getTree();
        
        
        while (!childStack.empty()) {
            Node currentNode = (Node) childStack.pop();
            
            if (currentNode != startNode && (currentNode == node  || currentNode.hasPage() ||  tree.isTerminalNode(currentNode) ) ) {
                currentNode.getParent().removeChild(currentNode);
                currentNode.setParent(startNode);
                startNode.addToChildren(currentNode);
            } else {
                //internal to the rest of the tree
                Stack tempStack = new Stack();
                Iterator it = currentNode.getChildren().iterator();
                while (it.hasNext()) {
                    tempStack.push(it.next());
                }
                while (!tempStack.empty()) {  
                    //loop this way to get chldren in correct order
                    childStack.push(tempStack.pop());
                }

                if (currentNode != startNode) {    
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
        startNode.setChildrenChanged(oldStartNodeHasChanged);
        
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
        return "Collapse tree below " + node.getName();
    }
           
}

