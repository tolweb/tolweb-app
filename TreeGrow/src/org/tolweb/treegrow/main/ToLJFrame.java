/*
 * ToLJFrame.java
 *
 * Created on September 18, 2003, 11:19 AM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;

import org.tolweb.treegrow.main.undo.*;

/**
 * JFrame subclass that sets the icon to the miniature ToL icon
 */
public class ToLJFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4746511295486466646L;
	protected UndoManager undoManager;
    protected UndoAction undoAction;
    protected RedoAction redoAction;
    
    /** Creates a new instance of ToLJFrame */
    public ToLJFrame() {
        this("");
    }

    public ToLJFrame(String title) {
        super(title);
       
        /*addWindowListener( new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                System.out.println("to front!");        
            }
        });        
         */
        setIcon();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                /*System.out.println("My class is: " + ToLJFrame.this.getClass() + " and I am being closed by:");
                try {
                    throw new RuntimeException();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }*/
            }
            
            public void windowClosed(WindowEvent e) {
                /*System.out.println("My class is: " + ToLJFrame.this.getClass() + " and this is window closed:");
                try {
                    throw new RuntimeException();
                } catch (Exception e2) {
                    e2.printStackTrace();
                } */               
            }
            
            public void windowOpened(WindowEvent e) {
                /*System.out.println("My class is: " + ToLJFrame.this.getClass() + " and this is window opened:");
                try {
                    throw new RuntimeException();
                } catch (Exception e2) {
                    e2.printStackTrace();
                } */               
            }            
        });
    }   
    
    /**
     * Adds the undoable edit to the manager and updates the undo and redo
     * actions to be enabled or disabled
     *
     * @param edit The edit to add to the manager
     */
    public void updateUndoStuff(AbstractUndoableEdit edit) {
        undoManager.addEdit(edit);
        undoAction.updateUndoState();
        redoAction.updateRedoState();
    } 
    
    public UndoManager getUndoManager() {
        if (undoManager == null) {
            undoManager = new UndoManager();
        }
        return undoManager;
    }    
    
    public void setUndoAction(UndoAction value) {
        undoAction = value;
    }
    
    public UndoAction getUndoAction() {
        return undoAction;
    }
    
    public void setRedoAction(RedoAction value) {
        redoAction = value;
    }
    
    public RedoAction getRedoAction() {
        return redoAction;
    }    

    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("Images/tinyworld.gif")));
    }
}
