
package org.tolweb.treegrow.tree.undo;

import org.tolweb.treegrow.tree.*;

/**
 * Reset parent of mover, possibly creating a new one to pair up with
 *of the new parent previously had no children
 */
public class MoveAsChildUndoableEdit extends AbstractCollapsableUndoableEdit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7459539632569849741L;
	private boolean newParentWasLeaf;
    protected Node newParent;
    private NodeView newParentView;
    private Node secondChild;
    private int index;
    private boolean oldNewParentHasChanged;

    
    /** 
     * Creates a new instance of MoveAsChildUndoableEdit.
     *  No index given, so place the new child at the end of the parent's list of children
     */
    public MoveAsChildUndoableEdit(Node mover, NodeView newParentView, Node toCollapse) {
        this(mover, newParentView, newParentView.getChildren().size(), toCollapse);
    }

    /** 
     * Creates a new instance of MoveAsChildUndoableEdit.
     * Index given, so place the new child at that position 
     * on the parent's list of children
     */    
    public MoveAsChildUndoableEdit(Node mover, NodeView newParentView, int index, Node toCollapse) {
        super(mover,toCollapse);

        this.newParentView = newParentView;
        newParent = newParentView.getNode();
        int currentSequence = mover.getSequence();
        if (mover.getParent() == newParent && currentSequence < index) {
            // If it's the same parent and we are moving up, this doesn't apply
            this.index = index-1;
        } else {
            this.index = index;
        }
        
        newParentWasLeaf = newParent.getIsLeaf();
       
        affectedNodes.add(newParentView.getNode());
        oldNewParentHasChanged = newParent.getChildrenChanged();
        redo();
    }    

     /** 
     *Place mover on parent. Possibly delete the original parent of the mover 
     *(if that parent now has only one child).
     */    
    public void redo() {
        super.redo();
                
        //Second step - add mover to new parent
        boolean wasTerminal = newParentView.isTerminal();
        node.setParent(newParent);
        newParent.addToChildren(index,node);
        newParent.setChildrenChanged(true);
        newParent.setIsLeaf(false);        
        if (wasTerminal) {
            if (secondChild == null) {
                secondChild = new Node(Tree.getNextNodeId());
                secondChild.setChangedToTrue();
                TreePanel treePanel = TreePanel.getTreePanel();
                treePanel.getTree().addNode(secondChild);
                //treePanel.add(secondChild.getTextField());
            } else {
                NodeGraveyard.getGraveyard().getNode(secondChild.getId());
            }            
            newParent.addToChildren(secondChild);
        }
        
        updateTreePanel();
    }   
    

    /**
     * Move original mover back to original parent. This is done mostly by
     *retrieving deleted nodes from graveyard and rebuilding the tree from 
     *the original treestring. Possibly need to remove the new child of the
     *new paretn...if it was added in the redo step
     */    
    public void undo() {
        
        if (secondChild != null) {
            NodeGraveyard.getGraveyard().addNode(secondChild);
        }
        // Only necessary if newParent used to be a terminal, but it doesn't harm anything to
        // always do it.
        newParent.clearChildren();
        newParent.setIsLeaf(newParentWasLeaf);
        newParent.setChildrenChanged(oldNewParentHasChanged);

        super.undo();
        
    }
    
    public String toString () {
        return "MoveAsChildUndoableEdit childNode = " + node + " newParent = " + newParent + " nodeToCollapse = " + nodeToCollapse + " secondChild = " + secondChild + " oldTreeString = " + oldTreeString;
    }
    
    public String getPresentationName() {
        return "Move " + node.getName() + " onto " + newParent.getName();
    }    
}
