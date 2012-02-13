/*
 * AddTableItemUndoableEdit.java
 *
 * Created on July 21, 2003, 11:53 AM
 */

package org.tolweb.treegrow.page.undo;

import org.tolweb.treegrow.page.*;

/**
 * Abstract superclass that handles the creation of a new item and adding it
 * to the list of items in the table
 */
public abstract class AddTableItemUndoableEdit extends TableItemUndoableEdit {
    private int selectedRow;
    protected int initialAddLocation;
    
    /** Creates a new instance of AddTableItemUndoableEdit */
    public AddTableItemUndoableEdit(PageFrame f, AbstractPageEditorPanel p, AbstractDraggableTable t) {
        // Here we will pass null as the ChangedFromServerProvider since it hasn't been created yet
        super(f, p, null, t);
        oldHasChanged = true;
        initialAddLocation = getAddLocation();
    }
    
    public void redo() {
        if (editedObject == null) {
            editedObject = getNewItem();
        }
        selectedRow = initialAddLocation;
        table.getList().add(initialAddLocation, editedObject);
        ((AbstractDraggableTable.AbstractEditableTableModel) table.getModel()).addRow(); 
        // Here highlight the newly added item
        super.redo();
    }
    
    public void undo() {
        table.getList().remove(selectedRow);
        ((AbstractDraggableTable.AbstractEditableTableModel) table.getModel()).removeRow(selectedRow);
        // Here just highlight the first one
        selectedRow--;
        super.undo();
    }
    
    public int getSelectedRow() {
        return selectedRow;
    }
    
    public int getAddLocation() {
        if (table.getSelectedRow() != -1 && table.getTableSize() > 0) {
            return table.getSelectedRow() + 1;
        } else {
            return table.getTableSize();
        }
    }
    
    /**
     * Will return a new instance of whatever should be created
     */
    public abstract ChangedFromServerProvider getNewItem();
}
