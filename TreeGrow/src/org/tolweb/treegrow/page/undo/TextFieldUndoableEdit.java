/*
 * AcknowledgementsUndoableEdit.java
 *
 * Created on July 28, 2003, 11:11 AM
 */

package org.tolweb.treegrow.page.undo;

import javax.swing.*;
import org.tolweb.treegrow.page.*;

/**
 *
 * @author  dmandel
 */

/**
 * Wrapper class around a JTextField edit that encapsulates the actual edit to the
 * textfield, the selectedItem, and the panel that should be shown
 */
public abstract class TextFieldUndoableEdit extends TableItemUndoableEdit {
    protected JTextField field;
    
    protected String oldValue;
    protected String newValue;
    
    private boolean fromConstructor;
    
    
    /** Creates a new instance of TextFieldUndoableEdit 
     *
     * @param f The pageframe the field is on
     * @param p The panel to select the object on
     * @param s A selected object in a JTable
     * @param t The table that this edit is tied to
     * @param fld The textfield that fired this event
     */
    public TextFieldUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider s, AbstractDraggableTable t, JTextField fld) {
        super(f, p, s, t);
        field = fld;
        newValue = getNewValue();
        oldValue = getOldValue();
        fromConstructor = true;
        /*if (fld != null) {
            System.out.println("setting value to : " + fld.getText());
        }
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if (fld != null) {
            setValue(fld.getText());
        } else {
            setValue(newValue);
        }
        fromConstructor = false;
    }
    
    /**
     * Use this to call super with a null table.  This will prevent the table
     * from trying to highlight a row since there is no table to highlight.
     * This situation arises because there are some text fields associated
     * with table items, and other who are not.
     */
    public TextFieldUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider s, JTextField fld) {
        this(f, p, s, null, fld);
    }    
    
    protected String getNewValue() {
        return field.getText();    
    }
   
    public void undo() {
        justDoIt(oldValue);
        super.undo();
    }
    
    public void redo() {
        justDoIt(newValue);
        super.redo();
    }

    protected void justDoIt(String val) {
        setValue(val);        
        if (field != null) {
            field.setText(val);
            field.requestFocus();        
        } else {
            setValue(val);
        }
    }
    
    /**
     * Sets the value on the object -- called during undo and redo.  This 
     * setting is whatever the edit actually sets, so if it's a page title,
     * it will be something like <code>page.setPage(val)</code>
     *
     * @param val The value to set on the object
     */
    protected abstract void setValue(String val);
    
    /**
     * Called to store the old value (the value that is changed in the edit
     * subclass) of an object.  So if it's setting the title in an edit, it 
     * would look something like <code> oldValue = page.getTitle();</code>
     */
    protected abstract String getOldValue();
}
