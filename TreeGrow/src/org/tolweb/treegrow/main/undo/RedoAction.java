/*
 * RedoAction.java
 *
 * Created on February 17, 2004, 11:38 AM
 */

package org.tolweb.treegrow.main.undo;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;

/**
 *
 * @author  dmandel
 */
public class RedoAction extends AbstractAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4200754007991740714L;
	private UndoManager manager;
    private UndoAction undoAction;
    private JButton button;
    
    /** Creates a new instance of RedoAction */
    public RedoAction(UndoManager mngr) {
        super("Redo", new ImageIcon(ClassLoader.getSystemResource("Images/redo.trsp.gif")));        
        manager = mngr;
        updateRedoState();
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            manager.redo();
        } catch (CannotUndoException ex) {
            System.out.println("Unable to redo: " + ex);
            ex.printStackTrace();
        }
        updateRedoState();
        undoAction.updateUndoState();        
    }  
    
    public void setUndoAction(UndoAction actn) {
        undoAction = actn;
    }
    
    /**
     * Updates the action to be enabled or disabled based on whether there is
     * anything in the UndoManager available to redo
     */
    public void updateRedoState() {
        if (manager.canRedo()) {
            setEnabled(true);
            putValue(Action.NAME, manager.getRedoPresentationName());
        } else {
            setEnabled(false);
            putValue(Action.NAME, "Redo");
        }
        if (button != null) {
            button.setText(""); // OSX wants to set the button's text on the toolbar ...but we don't want that. Argh!        
        }
    }

    public void setButton (JButton btn) {
        button = btn;
    }    
}
