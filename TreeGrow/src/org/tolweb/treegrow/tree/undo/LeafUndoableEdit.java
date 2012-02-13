package org.tolweb.treegrow.tree.undo;

import org.tolweb.treegrow.page.*;
import org.tolweb.treegrow.tree.Node;


/** 
 *Editing leaf value on node. Simply  keep track of the new and 
 * previous values for leaf,  and set the new on a redo and old 
 * on an undo
 */
public class LeafUndoableEdit extends AbstractNodeValueUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 465893616359071261L;
	private TextSection discussionSection;
    
    public String toString() {
        return "LeafUndoableEdit of " + node + " value = " + newValue + " oldTreeString = " + oldTreeString;
    }

    /** Creates a new instance of LeafUndoableEdit */
    public LeafUndoableEdit(Node n, int newVal) {
        super(n, newVal);
    }
    
    protected int grabOldValue() {
        return node.getIsLeaf() ? Node.LEAF : Node.NOT_LEAF;
    }
        
    public void setValue(int val) {
        node.setIsLeaf(val == 1);
    }
    
    public String getPresentationName() {
        return "Mark Leaf of Node " + node.getName();
    }
    
    public void setNewHasChanged() {
        node.setLeafChanged(true);
    }    
    
    public void setOldHasChanged() {
        node.setLeafChanged(oldHasChanged);
    }    
    
}
