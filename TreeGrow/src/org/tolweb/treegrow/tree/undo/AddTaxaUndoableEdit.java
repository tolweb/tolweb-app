
package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;

/**
 * Given a list of names to be added, and a parent to add them to, 
 * add a bunch of nodes to the end of the parent's list of children
 */
public class AddTaxaUndoableEdit extends AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = -408727305324065941L;
	private boolean oldHasChanged;
    private Node parent;
    private Node secondChild;
    private ArrayList newChildren;
    private ArrayList affectedNodes ;
    private boolean parentWasLeaf;

    public String toString () {
        return "AddTaxaUndoableEdit, parentNode = " + parent + " secondChild = " + secondChild + " newChildren = " + newChildren + " oldTreeString = " + oldTreeString;
    }
    
    /** 
     *  Add new nodes to the end of the parent's children list
     *
     * @param node  The parent
     * @param names List of the names for the new nodes to be added.
     */
    public AddTaxaUndoableEdit(Node node, List nodes) {
        parent = node;    
        parentWasLeaf = parent.getIsLeaf();
        Iterator it = nodes.iterator();
        newChildren = new ArrayList();
        while (it.hasNext()) {
            Node nextNode = (Node) it.next();
            nextNode.setId(Tree.getNextNodeId());
            if (parent.getExtinct() == Node.EXTINCT) {
                nextNode.setExtinct(Node.EXTINCT);
            }
            nextNode.setChangedToTrue();
            newChildren.add(nextNode);
            NodeGraveyard.getGraveyard().addNode(nextNode);
        }
        oldHasChanged = parent.getChildrenChanged();        
        affectedNodes = new ArrayList();
        
        redo();
    }

    /**
     *Add the nodes.  note that if they've been added before and since removed 
     *(by an undo of this edit), then just pluck them out of graveyard... 
     *otherwise make new ones
     */
    public void redo() {      
        affectedNodes.clear();
        affectedNodes.add(parent);
        parent.setChildrenChanged(true);
        parent.setIsLeaf(false);
        Iterator it = newChildren.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            NodeGraveyard.getGraveyard().getNode(node.getId());
            if (node.getParent() == null) {
                parent.addToChildren(node);
            }
            affectedNodes.add(node);
        }      
        
        if (parent.getChildren().size() == 1) {
            if (secondChild == null) {
                secondChild = new Node(Tree.getNextNodeId());
                secondChild.setChangedToTrue();
                if (parent.getExtinct() == Node.EXTINCT) {
                    secondChild.setExtinct(Node.EXTINCT);
                }
                TreePanel treePanel = TreePanel.getTreePanel();
                treePanel.getTree().addNode(secondChild);
                //treePanel.add(secondChild.getTextField());
            } else {
                NodeGraveyard.getGraveyard().getNode(secondChild.getId());
            }            
            parent.addToChildren(secondChild);
            affectedNodes.add(secondChild);            
        }      
        updateTreePanel();
    }   
    
    /**
     *Take all the newly added nodes, and put them in the graveyard
     */
    public void undo() {
        affectedNodes.clear();
        affectedNodes.add(parent);
        parent.setChildrenChanged(oldHasChanged);
        parent.setIsLeaf(parentWasLeaf);
        Iterator it = newChildren.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            NodeGraveyard.getGraveyard().addNode(node);
            affectedNodes.add(node);
            if (node.getParent() == parent) {
                // added support for remembering tree structures.
                // this way if a node is connected to the main 
                // parent it is re-attached, otherwise the structure
                // is preserved in memory                
                node.setParent(null);
            }
        }
        
        if (secondChild != null) {
            NodeGraveyard.getGraveyard().addNode(secondChild);
            affectedNodes.add(secondChild);
        }
        parent.clearChildren();
        TreePanel.getTreePanel().getTree().updateTree(oldTreeString);
        updateTreePanel();        
    }
    
    public boolean canUndo() {
        return true;
    }
    
    public boolean canRedo() {
        return true;
    }
    
    /**
     *Update treepanels and page frames as necessary
     */
    private void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.setActiveNode(parent);
        treePanel.rebuildTree();
        TreePanelUpdateManager.getManager().rebuildTreePanels(affectedNodes);
        treePanel.repaint();
    }
    
    public String getPresentationName() {
        return "Add subgroups to " + parent.getName();
    }    
}
