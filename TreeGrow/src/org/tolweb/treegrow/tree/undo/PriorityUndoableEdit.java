/*
 * Created on Dec 8, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.treegrow.tree.undo;

import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.TreePanel;

public class PriorityUndoableEdit extends AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7028326158836581942L;
	private Node node;
    private int oldPriority, newPriority;
    private boolean oldPriorityChanged = false;
    
    public PriorityUndoableEdit(Node node, int newPriority) {
        this.node = node;
        oldPriorityChanged = node.getIncompleteSubgroupsChanged();
        oldPriority = node.getPriority();
        this.newPriority = newPriority;
        redo();
    }
    
    public void redo() {
        node.setPriorityChanged(true);
        node.setPriority(newPriority);
        TreePanel.getTreePanel().repaint();
    }
    
    public void undo() {
    	node.setPriorityChanged(oldPriorityChanged);
        node.setPriority(oldPriority);
        TreePanel.getTreePanel().repaint();        
    }
    
    public String toString() {
        return "PriorityUndoableEdit of " + node + " oldTreeString = " + oldTreeString;
    }
}
