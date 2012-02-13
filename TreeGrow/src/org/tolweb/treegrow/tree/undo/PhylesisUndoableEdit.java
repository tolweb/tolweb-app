package org.tolweb.treegrow.tree.undo;

import org.tolweb.treegrow.tree.*;

/** 
 *Editing phylesis value on node. Simply  keep track of the new and 
 * previous values for phylesis,  and set the new on a redo and old 
 * on an undo
 */
public class PhylesisUndoableEdit extends AbstractNodeValueUndoableEdit{

    /**
	 * 
	 */
	private static final long serialVersionUID = 2903369882837007380L;

	public String toString() {
        return "PhylesisUndoableEdit of " + node + " to " + newValue + " oldTreeString = " + oldTreeString;
    }
    
    public PhylesisUndoableEdit(Node n, int newVal) {
        super(n, newVal);
        oldHasChanged = n.getPhylesisChanged();
    }
    
    public void undo() {
        super.undo();
    }
    
    public void setValue(int val) {
        node.setPhylesis(val);   
    }
    
    protected int grabOldValue() {
        return node.getPhylesis();   
    }
       
    public String getPresentationName() {
        return "Phylesis Change of " + node.getName();
    }
    
    public void setNewHasChanged() {
        node.setPhylesisChanged(true);
    }    

    public void setOldHasChanged() {
        node.setPhylesisChanged(oldHasChanged);
    }    
}
