
package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;

/**
 * Delete a node and subtree
 */
public class DeleteNodeUndoableEdit extends AbstractCollapsableUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5381259078074857635L;
	private NodeView view;

    public String toString() {
        return "DeleteNodeUndoableEdit of " + node + " toCollapse = " + nodeToCollapse + " oldTreeString = " + oldTreeString;
    }

    /** Creates a new instance of DeleteNodeUndoableEdit */
    public DeleteNodeUndoableEdit(NodeView v, Node toCollapse) {
        super(v.getNode(),toCollapse);
        view = v;
        affectedNodes.add(v.getNode());
        redo();
    }

    /**
     *remove node, and collapse children onto parent
     */
    public void redo() {
        if (view.isTerminal()) {
            super.redo();
        } else {
            Node parent = node.getParent();
            int index = parent.indexOfChild(node);
            
            ArrayList children = node.getChildren();
            int childrenSize = children.size();
            for (int i = childrenSize - 1; i >= 0; i--) {
                Node child = (Node) children.get(i);
                parent.addToChildren(index, child);
                child.setParent(parent);
            }
            parent.removeChild(node);
            parent.setChildrenChanged(true);        
        }
                
        NodeGraveyard.getGraveyard().addNode(node);
        
        updateTreePanel();
    }   
    
    /**
     *Pull deleted node off graveyard, and rebuild according to original 
     *treestring
     */
    public void undo() {
        NodeGraveyard.getGraveyard().getNode(node.getId());
        super.undo();
    }

    
    public String getPresentationName() {
        return "Deletion of node " + node.getName() ;
    }    
}
