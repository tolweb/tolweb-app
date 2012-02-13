/*
 * ISPUUndoableEdit.java
 *
 * Created on November 7, 2003, 3:39 PM
 */

package org.tolweb.treegrow.tree.undo;

import org.tolweb.treegrow.tree.*;

/**
 *
 * @author  dmandel
 */
public class ISPUUndoableEdit extends AbstractCollapsableUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2947322015948518042L;
	private boolean oldHasChanged;
    private int oldValue;
    private Node sibling;
    private boolean fromConstructor = true;
    
    public String toString () {
        return "ISPUUndoableEdit of " + node + " oldTreeString = " + oldTreeString;
    }
    
    public ISPUUndoableEdit(Node n, Node toCollapse, Node sib) {
        super(n, toCollapse);
        sibling = sib;
        oldValue = grabOldValue();
        oldHasChanged = n.getConfidenceChanged();
        redo();
    }
    
    public void undo() {
        super.undo();
        setValue(oldValue);
        setOldHasChanged();
        updateTreePanel();      
    }
    
    public void redo() {
        super.redo();
        setValue(Node.INCERT_UNSPECIFIED);
        setNewHasChanged();
        int insertIndex;
        if (sibling != null) {
            insertIndex = oldGrandParent.indexOfChild(sibling) + 1;
        } else {
            insertIndex = parentIndex;
        }
        oldGrandParent.addToChildren(insertIndex, node);
        if (!fromConstructor) {
            updateTreePanel();    
        } else {
            fromConstructor = false;
        }
    }
    
    public void setValue(int val) {
        node.setConfidence(val);
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
