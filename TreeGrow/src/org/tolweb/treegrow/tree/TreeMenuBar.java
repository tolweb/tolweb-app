/*
 * TreeMenuBar.java
 *
 * Created on July 18, 2003, 11:19 AM
 */

package org.tolweb.treegrow.tree;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.main.undo.*;

/**
 * Menu Bar for the TreeEditor
 */
public class TreeMenuBar extends ToLMenuBar {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4123436852869453588L;

	/** Creates a new instance of TreeMenuBar */
    public TreeMenuBar(ToLJFrame frame) {
        super(frame);
        TreePanel.getTreePanel().setUndoAction((UndoAction) undoItem.getAction());
        TreePanel.getTreePanel().setRedoAction((RedoAction) redoItem.getAction());        
    }
    
    /**
     * Adds items to the file menu
     */
    protected void addFileMenuItems() {
        super.addFileMenuItems(); 
        fileMenu.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent e) {
                TreePanel.getTreePanel().requestFocus();
            }
        });
        JMenuItem writeDebugInfo = new JMenuItem("Write debug information to file"); 
        writeDebugInfo.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("actionlist.txt"));
                    writer.write(frame.getUndoManager().toString());
                    writer.write("\n\nNode Views\n\n");
                    writer.write(TreePanel.getTreePanel().getViewsDebugString());
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {

                }
            }
        });
        fileMenu.addSeparator();
        fileMenu.add(writeDebugInfo);
    }
    
    /** 
     * Adds the undo and redo items to the edit menu
     */
    protected void addEditMenuItems() {
        super.addEditMenuItems();
        JMenuItem findNode = new JMenuItem("Find Node"); 
        findNode.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                try {
                    FindNodeFrame findNodeForm = new FindNodeFrame();
                    findNodeForm.show();                    
                } catch (Exception ex) {
                }
            }
        });
        editMenu.addSeparator();        
        editMenu.add(findNode);
    }
}
