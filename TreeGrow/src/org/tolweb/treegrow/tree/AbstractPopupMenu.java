package org.tolweb.treegrow.tree;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;

/** Abstract class used to define popups that arise when the user
 * clicks on a node while a particular tool is active. Each tool results
 * in a different popup, and the popup is responsible for altering
 * some node property
 **/
public abstract class AbstractPopupMenu extends JPopupMenu implements ActionListener {
    protected Node node;
    protected JRadioButtonMenuItem[] items;
    
    /** 
     * Creates a new instance of the popup menu
     */
    public AbstractPopupMenu() {
        super();
        initializeRadioButtons();
    }
    
    private void initializeRadioButtons() {
        ArrayList strings = getStrings();
        int size = strings.size();
        items = new JRadioButtonMenuItem[size];
        for (int i = 0; i < size; i++) {
            JRadioButtonMenuItem currentItem = new JRadioButtonMenuItem((String) strings.get(i), false);
            currentItem.addActionListener(this);
            add(currentItem);
            items[i] = currentItem;
        }
    }   
    
    /** 
     * Associate the popup with the given node. This is the node whose
     * properties will be altered by the popup.
     */
    public void setNode(Node selected) {
        node = selected;
        for (int i = 0; i < items.length; i++) {
            items[i].setSelected(i == getProperty());
        }
    }
    
    /** 
     * Invoked when an element is selected.
     */
    public void actionPerformed(ActionEvent e) {
        int newVal = getStrings().indexOf(e.getActionCommand());
        TreePanel treePanel = TreePanel.getTreePanel();
        if (newVal != getProperty()) {
            AbstractUndoableEdit edit = getEdit(newVal);
            if (edit != null) {
                treePanel.updateUndoStuff(edit);
            }
        }
        setVisible(false);
    }

    /** 
     *Abstract method to get list of choices to appear on the popup
     */
    public abstract ArrayList getStrings();
    
    /** 
     *Abstract method to get get an instance of an undoable edit, which
     * wilspecify the property changes to be made for the node of interest
     */
    public AbstractUndoableEdit getEdit(int newVal) {
        return null;
    }

    /** 
     *Abstract method to determine the current value of the property of interest 
     * on the node of interest
     */    
    public abstract int getProperty();
}
