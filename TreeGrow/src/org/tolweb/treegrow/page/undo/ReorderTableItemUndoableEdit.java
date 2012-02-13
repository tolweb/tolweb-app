/*
 * PageReorderTableItemUndoableEdit.java
 *
 * Created on July 21, 2003, 9:56 AM
 */

package org.tolweb.treegrow.page.undo;

import org.tolweb.treegrow.page.*;

/**
 * Abstract UndoableEdit superclass that represents reordering some object in
 * a table.  This edit is different from the other edits in that it has already
 * been "done" by the time the edit is constructed.  This is done for 
 * user-interactivity reasons.
 */
public class ReorderTableItemUndoableEdit extends TableItemUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 803922911155176258L;
	protected int oldIndex, newIndex;
    
    /** 
     * Takes in the list and moves the item at oldLoc to newLoc
     *
     * @param oldLoc The old (current) location of the item
     * @param newLoc The desired location of the item
     */
    public ReorderTableItemUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider s, AbstractDraggableTable t, int oldLoc, int newLoc) {
        super(f, p, s, t);
        oldIndex = oldLoc;
        newIndex = newLoc;
    }
    
    public void redo() {
        moveItem(oldIndex, newIndex);
        selectedRow = newIndex;
        super.redo();
    }
    
    public void undo() {
        moveItem(newIndex, oldIndex);
        selectedRow = oldIndex;
        super.undo();
    }
    
    /**
     * Takes the item at fromIndex and moves it to toIndex
     *
     * @param fromIndex The index to move from
     * @param toIndex The index to move to
     */
    private void moveItem(int fromIndex, int toIndex) {
        Object removed = table.getList().remove(fromIndex);
        table.getList().add(toIndex, removed);
    }
    

}
