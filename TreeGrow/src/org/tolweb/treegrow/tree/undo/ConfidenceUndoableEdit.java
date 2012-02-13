
package org.tolweb.treegrow.tree.undo;

import org.tolweb.treegrow.tree.*;


/** 
 *Editing confidence value on node. Simply  keep track of the new and 
 * previous values for confidence,  and set the new on a redo and old 
 * on an undo
 */
public class ConfidenceUndoableEdit extends AbstractNodeValueUndoableEdit {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6067348292389113877L;

	public String toString () {
        return "ConfidenceUndoableEdit of " + node + " to " + newValue + " oldTreeString = " + oldTreeString;
    }
    
    public ConfidenceUndoableEdit(Node n, int newVal) {
        super(n, newVal);
        oldHasChanged = n.getConfidenceChanged();
    }
       
    public void setValue(int val) {
        node.setConfidence(val);
        node.setConfidenceChanged(true);
    }
    
    public void undo() {
        super.undo();
        node.setConfidenceChanged(oldHasChanged);
        TreePanel.getTreePanel().getTree().updateTree(oldTreeString);
        super.updateTreePanel();
    }
    
    public void redo() {
        super.redo();
        node.setConfidenceChanged(true);
    }
    
    protected int grabOldValue() {
        return node.getConfidence();   
    }
    
    public String getPresentationName() {
        return "Set Confidence of " + node.getName();
    }
    
    
    public void setNewHasChanged() {
        node.setConfidenceChanged(true);
    }    

    public void setOldHasChanged() {
        node.setConfidenceChanged(oldHasChanged);
    }  
    
}
