package org.tolweb.treegrow.tree.undo;

import org.tolweb.treegrow.tree.*;

/**
 * Move node from one place in tree to the other. To add as sibling, a new
 *node is added, and is then made the parent of the moved node and it's new 
 *sibling. Possibly remove the parent of moved node (if it now has only
 *one child
 */
public class MoveAsSiblingUndoableEdit extends AbstractCollapsableUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4024625330717952612L;
	private Node movee;
    private Node newParent;
    private boolean oldMoveeParentHasChanged;

    public String toString () {
        return "MoveAsSiblingUndoableEdit mover = " + node + " movee = " + movee + " newParent = " + newParent + " nodeToCollapse = " + nodeToCollapse + " oldTreeString = " + oldTreeString;
    }
    
    /** 
     * Creates a new instance of MoveAsSiblingUndoableEdit.
     */
    public MoveAsSiblingUndoableEdit(Node mover, Node movee, Node toCollapse) {
        super(mover, toCollapse);
        
        this.movee = movee;
        affectedNodes.add(movee.getParent());
        oldMoveeParentHasChanged = movee.getParent().getChildrenChanged();
        redo();
    }
    
    /**
     *  Create a new node, add it as the parent of the mover and movee, 
     * then put the new node in place of the movee on movee's parent    
     */
    public void redo() {
        //First step - remove the mover from it's parent
        super.redo();

        Node moveeParent = movee.getParent();
        int moveeindex = moveeParent.indexOfChild(movee);        
        moveeParent.setChildrenChanged(true);
        //Second step - make a new node, set it as the parent for the mover and movee, then put it as child to movee's parent
        if (newParent == null) {
            newParent = new Node(Tree.getNextNodeId());
            newParent.setChangedToTrue();
            TreePanel treePanel = TreePanel.getTreePanel();
            treePanel.getTree().addNode(newParent);
            //treePanel.add(newParent.getTextField());
        } else {
            NodeGraveyard.getGraveyard().getNode(newParent.getId());
        }
        
        node.setParent(newParent);
        movee.setParent(newParent);
        newParent.addToChildren(movee);
        newParent.addToChildren(node);
        
       
        moveeParent.replaceChildAtIndex(moveeindex,newParent);
        
        updateTreePanel();
    }   
    
    
    /** 
     *Retrieve removed node (if removed), then rebuild structure according to tree string
     */
    public void undo() {
        NodeGraveyard.getGraveyard().addNode(newParent);
        newParent.removeChildren();
        movee.getParent().setChildrenChanged(oldMoveeParentHasChanged);
        
        super.undo();
    }
        
    public String getPresentationName() {
        return "Move " + node.getName() + " as sibling of " + movee.getName();
    }    
}
