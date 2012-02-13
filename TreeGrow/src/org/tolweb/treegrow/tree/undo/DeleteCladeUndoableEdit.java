
package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;

/**
 * Delete a node and subtree
 */
public class DeleteCladeUndoableEdit extends AbstractCollapsableUndoableEdit {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2670843553612947900L;


	public String toString() {
        return "DeleteCladeUndoableEdit of " + node + " toCollapse = " + nodeToCollapse + " oldTreeString = " + oldTreeString;
    }

    /** Creates a new instance of DeleteNodeUndoableEdit */
    public DeleteCladeUndoableEdit(Node n, Node toCollapse) {
        super(n,toCollapse);
        affectedNodes.add(n);
        redo();
    }

    /**
     * remove the whole subtree - but leave the relationships intact so Undo 
     *can unzombie the nodes. 
     */
    public void redo() {
        
        super.redo();
        
        Stack nodeStack = new Stack();
        nodeStack.push(node);
        while (!nodeStack.empty()) {
            Node n = (Node)nodeStack.pop();
            Iterator it = n.getChildren().iterator();
            while (it.hasNext() ) {
                nodeStack.push(it.next());
            }
            NodeGraveyard.getGraveyard().addNode(n);
        }
                
        updateTreePanel();
    }   
    
    /**
     *Just pull the nodes back from the graveyard, then rebuild
     *the tree according to the original structure
     */
    public void undo() {
        //walk the subtree - unzombie nodes along the way
        Stack nodeStack = new Stack();
        nodeStack.push(node);
        while (!nodeStack.empty()) {
            Node n = (Node)nodeStack.pop();
            NodeGraveyard.getGraveyard().getNode(n.getId());
            
            if (n.getChildren().size() > 0 ) {
                Node firstChild = n.getChildAtIndex(0);
                if (oldTreeString.indexOf(""+firstChild.getId()) >= 0) { //only add children if they were originally in the tree
                    Iterator it = n.getChildren().iterator();
                    while (it.hasNext() ) {
                        nodeStack.push(it.next());
                    }
                }
            }
        }    
        super.undo();
    }
    
    
    public String getPresentationName() {
        return "Deletion of clade " + node.getName() ;
    }    
}
