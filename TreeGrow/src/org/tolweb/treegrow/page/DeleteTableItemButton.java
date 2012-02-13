/*
 * DeleteTableItemButton.java
 *
 * Created on July 29, 2003, 5:13 PM
 */

package org.tolweb.treegrow.page;

import java.awt.event.*;
import javax.swing.*;

import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.page.undo.*;

/**
 * Button that removes the currently selected table item from a table and
 * fires the undoable edit
 */
public class DeleteTableItemButton extends JButton implements ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6271207552844712515L;
	private AbstractPageEditorPanel panel;
    private ToLJFrame frame;
    protected AbstractDraggableTable table;
    
    /** Creates a new instance of DeleteTableItemButton */
    public DeleteTableItemButton(AbstractPageEditorPanel p, AbstractDraggableTable t) {
        super("Delete");
        panel = p;
        table = t;
        addActionListener(this);
    }
    
    /** Creates a new instance of DeleteTableItemButton */
    public DeleteTableItemButton(ToLJFrame f, AbstractDraggableTable t) {
        super("Delete");
        frame = f;
        table = t;
        addActionListener(this);
    }    
    
    public void actionPerformed(ActionEvent e) {
        if (table.getSelectedRow() != -1 && okToDelete()) {
            PageFrame pageFrame = panel != null ? panel.getPageFrame() : null;
            RemoveTableItemUndoableEdit edit = new RemoveTableItemUndoableEdit(pageFrame, panel, (ChangedFromServerProvider) table.getList().get(table.getSelectedRow()), table, table.getSelectedRow());
            if (panel != null) {
                panel.updateUndoStuff(edit);
            } else {
                frame.updateUndoStuff(edit);
            }
        }        
    }
    
    /**
     * Can be overridden by subclasses to perform additional delete checking
     *
     * @return Whether it is ok to delete the selected item
     */
    protected boolean okToDelete() {
        return true;
    }
    
}
