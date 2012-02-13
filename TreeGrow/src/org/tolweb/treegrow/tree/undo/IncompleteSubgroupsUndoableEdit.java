package org.tolweb.treegrow.tree.undo;

import java.util.ArrayList;

import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.TreePanel;
import org.tolweb.treegrow.tree.TreePanelUpdateManager;

public class IncompleteSubgroupsUndoableEdit extends
        AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8438520060875075397L;
	private Node node;
    private boolean oldIncompleteSubgroupsChanged = false;
    
    public IncompleteSubgroupsUndoableEdit(Node node) {
        this.node = node;
        oldIncompleteSubgroupsChanged = node.getIncompleteSubgroupsChanged();
        redo();
    }
    
    public void redo() {
        node.setIncompleteSubgroupsChanged(true);
        undoRedo();
    }
    
    public void undo() {
        node.setIncompleteSubgroupsChanged(oldIncompleteSubgroupsChanged);
        undoRedo();
    }
    
    public String toString() {
        return "IncompleteSubgroupsUndoableEdit of " + node + " oldTreeString = " + oldTreeString;
    }
    
    public void undoRedo() {
        node.setHasIncompleteSubgroups(!node.getHasIncompleteSubgroups());
        updateTreePanel();
    }
    
    /**
     *Cause treepanels and pagepanels to represent the new value
     */
    protected void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.setActiveNode(node);        
        ArrayList affectedNodes = new ArrayList();
        affectedNodes.add(node);
        TreePanelUpdateManager.getManager().rebuildTreePanels(affectedNodes);
        treePanel.repaint();
    }    
}
