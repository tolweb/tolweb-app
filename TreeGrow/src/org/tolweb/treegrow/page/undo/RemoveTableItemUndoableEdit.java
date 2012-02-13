/*
 * RemoveTableItemUndoableEdit.java
 *
 * Created on July 21, 2003, 1:42 PM
 */

package org.tolweb.treegrow.page.undo;

import org.tolweb.treegrow.page.*;

/**
 * Removes the currently-selected item in any AbstractDraggableTable
 */
public class RemoveTableItemUndoableEdit extends TableItemUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7145309174488754719L;
	private int index;
    protected Object removed;
    
    /** Creates a new instance of RemoveTableItemUndoableEdit */
    public RemoveTableItemUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider s, AbstractDraggableTable t, int i) {
        super(f, p, s, t);
        index = i;
        removed = table.getList().get(index);
        redo();
    }
    
    public void redo() {
        table.getList().remove(index);
        if (index < table.getList().size()) {
            selectedRow = index;
        } else {
            selectedRow = index - 1;
        }
        super.redo();
    }
    
    public void undo() {
        table.getList().insertElementAt(removed,  index);
        selectedRow = index;
        super.undo();
    }
    
    public int getSelectedRow() {
        return selectedRow;
    }
    
}
