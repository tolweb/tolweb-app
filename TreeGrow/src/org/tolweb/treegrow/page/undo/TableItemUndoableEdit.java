/*
 * TableItemUndoableEdit.java
 *
 * Created on July 21, 2003, 11:54 AM
 */

package org.tolweb.treegrow.page.undo;

import javax.swing.*;

import org.tolweb.treegrow.page.*;

/**
 * Abstract superclass that contains the logic for selecting items in a table
 * and calling appropriate methods in containing panels so the UI accurately
 * reflects the selected item
 */
public abstract class TableItemUndoableEdit extends AbstractPageEditorUndoableEdit {
    protected AbstractDraggableTable table;

    /**
     * Here so subclasses can manipulate this as they please (since most of
     * them will want to do so)
     */
    protected int selectedRow = -1;
    
    /** Creates a new instance of TableItemUndoableEdit */
    public TableItemUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider s, AbstractDraggableTable t) {
        super(f,  p,  s);
        table = t;
    }
    
    public void undo() {
        super.undo();
        updateTable();
    }
    
    public void redo() {
        super.redo();
        updateTable();
    }
    
    /**
     * Method that individual edits can override in order to highlight a 
     * selected table row after the edit occurs.
     *
     * @return The index of the row to highlight.
     */
    public int getSelectedRow() {
        if (editedObject != null) {
            selectedRow = table.getList().indexOf(editedObject);
            // If the selected row is already selected then there is no need
            // to re-select it, so just set it to -1 so nothing happens
            if (selectedRow == table.getSelectedRow()) {
                selectedRow = -1;
            }
        } else {
            selectedRow = -1;
        }
        return selectedRow;
    }

    /**
     * Does the actual work of selecting rows in tables and calling panel
     * methods so the UI and memory are tied to each other
     */
    protected void updateTable() {
        if (table != null) {
            if (panel != null) {
                panel.setInUndoRedo(true);
            }
            table.clearDraggingVars();
            // Cancel editing so that other edits don't get fired while this
            // one is being done or undone
            if (table.getCellEditor() != null) {
                ((DefaultCellEditor) table.getCellEditor()).cancelCellEditing();
            }
            // Same idea here, we don't want to fire edits while we are going
            // through the process of altering the UI to sync with memory
            int row = getSelectedRow();
            if (row != -1 && row < table.getTableSize()) {
                table.setRowSelectionInterval(row, row);
            }
            table.revalidate();
            table.repaint();
            if (panel != null) {
                panel.setInUndoRedo(false);
            }
        }
        if (frame != null && panel != null) {
            frame.showAppropriatePanel(panel);
        }
    }
    
    /**
     * Available to subclasses in order to "turn-off" event handling while
     * the UI is being updated
     *
     * @param If true, turns event-handling off, if false, turns it back on
     */
    protected void setProgrammaticallySelecting(boolean value) {
        if (panel != null) {
            panel.setTableChanging(value);
        }
    }
}
