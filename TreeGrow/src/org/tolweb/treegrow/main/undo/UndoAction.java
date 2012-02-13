/*
 * UndoAction.java
 *
 * Created on February 17, 2004, 11:37 AM
 */

package org.tolweb.treegrow.main.undo;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;

/**
 *
 * @author  dmandel
 */
public class UndoAction extends AbstractAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = -721892871002240510L;
	private RedoAction redoAction;
    private UndoManager manager;
    private JButton button;
    
    /** Creates a new instance of UndoAction */
    public UndoAction(UndoManager m) {
        super("Undo", new ImageIcon(ClassLoader.getSystemResource("Images/undo.trsp.gif")));
        manager = m;
        updateUndoState();
    }
    
    public void setRedoAction(RedoAction actn) {
        redoAction = actn;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            manager.undo();
        } catch (CannotUndoException ex) {
            System.out.println("Unable to undo: " + ex);
            ex.printStackTrace();
        }
        updateUndoState();
        redoAction.updateRedoState();        
    }
    
    /**
    *Rebuild menu, to show name of currently available undo
    */
    public void updateUndoState() {
        if (manager.canUndo()) {
            setEnabled(true);
            putValue(Action.NAME, manager.getUndoPresentationName());
        } else {
            setEnabled(false);
            putValue(Action.NAME, "Undo");
        }
        if (button != null) {
            button.setText(""); // OSX wants to set the button's text on the toolbar ...but we don't want that. Argh!        
        }
    }
  
    public void setButton (JButton btn) {
        button = btn;
    }
}
