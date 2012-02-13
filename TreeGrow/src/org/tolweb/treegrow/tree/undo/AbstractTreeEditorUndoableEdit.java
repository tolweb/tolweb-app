
package org.tolweb.treegrow.tree.undo;

import javax.swing.undo.*;
import org.tolweb.treegrow.tree.*;

/**
 * Simple Abstract class used to capture the current tree structure. Used
 *to simplify the undo-ing of edits, because it allows the undo to 
 *simply retrieve a node that may have been zombied, then rebuild based on the 
 *string describing the current structure.
 */
public abstract class AbstractTreeEditorUndoableEdit extends AbstractUndoableEdit {
    protected String oldTreeString;
    
    /** 
     *Captures the string describing the structure of the tree prior
     *to enactment of the edit
     */
    public AbstractTreeEditorUndoableEdit() {
        if (TreePanel.getTreePanel() != null) {
            oldTreeString = TreePanel.getTreePanel().getTree().toString();
        }
    }
    
    public boolean canUndo() {
        return true;
    }
    
    public boolean canRedo() {
        return true;
    }
        
}
