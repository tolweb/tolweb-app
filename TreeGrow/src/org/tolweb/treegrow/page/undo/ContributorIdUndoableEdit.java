/*
 * ContributorUndoableEdit.java
 *
 * Created on December 23, 2003, 8:37 AM
 */

package org.tolweb.treegrow.page.undo;

import javax.swing.table.*;
import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.page.*;

/**
 * Undoable edit class that's fired when a user changes the contributor in
 * the combo box for a give PageContributor
 */
public class ContributorIdUndoableEdit extends TableItemUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5954043934369929275L;
	private int oldId, newId;
    private boolean oldCorrespondent;
    
    /** Creates a new instance of ContributorUndoableEdit */
    public ContributorIdUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider s, AbstractDraggableTable t, int contId) {
        super(f, p, s, t);
        oldId = ((PageContributor) editedObject).getContributorId();
        newId = contId;
        oldCorrespondent = ((PageContributor) editedObject).isContact();
        ((PageContributor) editedObject).setContributorId(newId);
        updateCorrespondent();
        repaintTable();
    }
    
    public void redo() {
        ((PageContributor) editedObject).setContributorId(newId);
        updateCorrespondent();        
        super.redo();
        repaintTable();
    }
    
    public void undo() {
        ((PageContributor) editedObject).setContributorId(oldId);
        ((PageContributor) editedObject).setIsContact(oldCorrespondent);
        super.undo();  
        repaintTable();        
    }

    private void repaintTable() {
        ((AbstractTableModel) table.getModel()).fireTableDataChanged();
        table.setSelectedObject(editedObject);
        table.repaint();        
    }
    
    private void updateCorrespondent() {
        Contributor contr = Controller.getController().getEditableContributor(newId);
        if (contr.dontShowEmail()) {
            ((PageContributor) editedObject).setIsContact(false);
        }
    }
    
    /**
     * Overridden so that if the user was choosing away from the blank 
     * contributor that this edit cannot be undone
     */
    public boolean isSignificant() {
        return oldId != Contributor.BLANK_CONTRIBUTOR.getId();
    }
}
