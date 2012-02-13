/*
 * TextAreaEdit.java
 *
 * Created on July 23, 2003, 1:46 PM
 */

package org.tolweb.treegrow.page.undo;

import javax.swing.text.*;
import javax.swing.undo.*;

import org.tolweb.treegrow.page.*;

/**
 * Wrapper class around a JTextArea edit that encapsulates the actual edit to the
 * text area, the selectedItem, and the panel that should be shown
 */
public class TextAreaUndoableEdit extends TableItemUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3466616692805133269L;
	private UndoableEdit actualEdit;
    protected JTextComponent area;
    private int redoCaretPosition, undoCaretPosition;
    
    /** Creates a new instance of TextAreaUndoableEdit 
     *
     * @param e The Actual undoableEdit to be undone
     * @param a The textarea that fired this event
     * @param o A selected object in a JTable
     * @param t The table that this edit is tied to
     * @param p The panel to select the object on
     */
    public TextAreaUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider o, AbstractDraggableTable t, UndoableEdit e, JTextComponent a) {
        super(f, p, o, t);
        actualEdit = e;
        area = a;
        redoCaretPosition = area.getCaretPosition();
        undoCaretPosition = ((AbstractDocument.DefaultDocumentEvent) actualEdit).getOffset();
    }
    
    /** Creates a new instance of TextAreaUndoableEdit -- in this case it's not
     * tied to a table
     *
     * @param e The Actual undoableEdit to be undone
     * @param a The textarea that fired this event
     * @param o A selected object in a JTable
     * @param p The panel to select the object on
     */
    public TextAreaUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider o, UndoableEdit e, JTextComponent a) {
        this(f, p, o, null, e, a);
    }
    
    public void undo() {
        super.undo();
        if (!area.hasFocus()) {
            area.requestFocus();
        }
        if (panel != null) {
            panel.setInUndoRedo(true);
        }       
        actualEdit.undo();
        if (panel != null) {
            panel.setInUndoRedo(false);
        }        
        //TextUI ui = (TextUI) area.getUI();
        //ui.damageRange(area, 0, area.getDocument().getLength());
        //area.updateUI();
        //area.revalidate();
        //area.repaint();
        area.setCaretPosition(undoCaretPosition);
    }
    
    public void redo() {
        super.redo();
        if (!area.hasFocus()) {
            area.requestFocus();
        }
        actualEdit.redo();       
        area.setCaretPosition(redoCaretPosition);
    }
}
