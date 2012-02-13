/*
 * AbstractPageEditorUndoableEdit.java
 *
 * Created on July 28, 2003, 4:54 PM
 */

package org.tolweb.treegrow.page.undo;

import javax.swing.undo.*;
import org.tolweb.treegrow.page.*;

/**
 * Abstract superclass that does the following on every undo/redo:
 * <ul>
 *  <li>automatically set the changed from server values</li>
 *  <li>set the appropriate panel in the pageframe</li>
 * </ul>
 */
public class AbstractPageEditorUndoableEdit extends AbstractUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3858326793554878362L;
	protected PageFrame frame;
    protected AbstractPageEditorPanel panel;
    protected ChangedFromServerProvider editedObject;
    protected boolean oldHasChanged, auxiliaryOldHasChanged;
    
    /** Creates a new instance of AbstractPageEditorUndoableEdit */
    public AbstractPageEditorUndoableEdit(PageFrame f, AbstractPageEditorPanel p, ChangedFromServerProvider s) {
        frame = f;
        panel = p;
        editedObject = s;
        if (editedObject != null) {
            oldHasChanged = editedObject.changedFromServer();
            editedObject.setChangedFromServer(true);
            if (editedObject instanceof AuxiliaryChangedFromServerProvider) {
                auxiliaryOldHasChanged = ((AuxiliaryChangedFromServerProvider)editedObject).auxiliaryChangedFromServer();
                ((AuxiliaryChangedFromServerProvider)editedObject).setAuxiliaryChangedFromServer(true);
            }
        }
    }
    
    public void undo() {
        editedObject.setChangedFromServer(oldHasChanged);
        if (editedObject instanceof AuxiliaryChangedFromServerProvider) {
            ((AuxiliaryChangedFromServerProvider)editedObject).setAuxiliaryChangedFromServer(auxiliaryOldHasChanged);
        }
        showAppropriatePanel();
    }
    
    public void redo() {
        editedObject.setChangedFromServer(true);
        if (editedObject instanceof AuxiliaryChangedFromServerProvider) {
            ((AuxiliaryChangedFromServerProvider)editedObject).setAuxiliaryChangedFromServer(true);
        }
        showAppropriatePanel();
    }
    
    public boolean canRedo() {
        return true;
    }
    
    public ChangedFromServerProvider getEditedObject() {
        return editedObject;
    }
    
    protected void showAppropriatePanel() {
        if (frame != null && panel != null) {
            frame.showAppropriatePanel(panel);
        }
    }
}
