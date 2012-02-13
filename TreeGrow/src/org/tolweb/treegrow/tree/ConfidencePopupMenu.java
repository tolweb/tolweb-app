package org.tolweb.treegrow.tree;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;

import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.tree.undo.*;


/** Defines the popup that arises when the user clicks on a node while 
 * the confidence tool is active. 
 **/
public class ConfidencePopupMenu extends AbstractPopupMenu {
    /**
	 * 
	 */
	private static final long serialVersionUID = 233218077440222391L;
	private NodeView view;
    
    /** 
     * Returns list of choices to appear on the popup
     */
    public ArrayList getStrings() {
        return Node.getConfidenceStrings(); 
    }
    
    public void setView(NodeView nv) {
        view = nv;
    }

    /** 
     * Returns undoable edit, which will specify the property changes to be 
     * made for the node of interest
     */
    public void doISPUEdit() {
        int oldValue = node.getConfidence();
        Node parentNode = node.getParent();
        // set this beforehand so the okToHandleNode function works right
        node.setConfidence(Node.INCERT_UNSPECIFIED);
        // Check to see if deleting the parent would be a problem
        if (!TreePanel.getTreePanel().okToHandleNode(view)) {
            node.setConfidence(oldValue);
            return;
        }
        node.setConfidence(oldValue);        
        int nodeIndex = parentNode.indexOfChild(node);
        // The siblingNode is used to tell where to reinsert the node ==
        // in order to reuse the existing node deletion code
        Node siblingNode;
        if (nodeIndex == 0) {
            siblingNode = null;
        } else {
            siblingNode = parentNode.getChildAtIndex(nodeIndex - 1);
        }
        UpdateTreePanelThread thread = new UpdateTreePanelThread(node, siblingNode);
        thread.start();
    }
    
    /** 
     * Overriden so that node deletion can look right if it's an ISPU on a node
     * with 2 children
     */
    public void actionPerformed(ActionEvent e) {
        int newVal = getStrings().indexOf(e.getActionCommand());
        TreePanel treePanel = TreePanel.getTreePanel();
        Node parentNode = node.getParent();
        if (newVal != getProperty()) {
            if (!(parentNode.getNumNonISPUChildren() == 2 && newVal == Node.INCERT_UNSPECIFIED)) {
                AbstractUndoableEdit edit = new ConfidenceUndoableEdit(node, newVal);
                treePanel.updateUndoStuff(edit);
            } else {
                if (!parentNode.hasPage()) {
                    setVisible(false);
                    doISPUEdit();
                } else {
                    String ISPU_INCONSISTENT = Controller.getController().getMsgString("ISPU_INCONSISTENT");
                    JOptionPane.showMessageDialog(TreePanel.getTreePanel(), ISPU_INCONSISTENT, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        setVisible(false);
    }    

    /**
     * Determine the current value of the confidence property 
     * on the node of interest
     */
    public int getProperty() {
        return node.getConfidence();
    }
    
    private class UpdateTreePanelThread extends Thread {
        private Node node, siblingNode;
        
        public UpdateTreePanelThread(Node n, Node s) {
            node = n;
            siblingNode = s;
        }
        
        public void run() {
            TreePanel.getTreePanel().doISPUUndoableEdit(node, siblingNode);
        }
    }
        
    
}
