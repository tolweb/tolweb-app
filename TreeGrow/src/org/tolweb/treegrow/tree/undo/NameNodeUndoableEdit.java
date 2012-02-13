package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;

/**
 * Alter the name of a node, keeping track of the previous name,
 *and previous changed state
 */
public class NameNodeUndoableEdit extends AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2275681493970992569L;
	protected Node node;
    private String oldName, newName;
    private boolean oldNameChanged;
    private ArrayList affectedNodes;

    public String toString() {
        return "NameNodeUndoableEdit of node " + node + " newName = " + newName + " oldTreeString = " + oldTreeString;
    }
    
    /** Creates a new instance of NameNodeUndoableEdit */
    public NameNodeUndoableEdit(Node n, String name) {
        node = n;
        oldName = node.getName();
        newName = name;
        oldNameChanged = node.getNameChanged();
        affectedNodes = new ArrayList();
        affectedNodes.add(node);
        redo();
    }
    
    public void redo() {
        node.setName(newName);
        node.setNameChanged(true);
        updateTreePanel();
    }
    
    public void undo() {
        node.setName(oldName);
        node.setNameChanged(oldNameChanged);
        updateTreePanel();
    }
    
    /**
     *Cause treepanels and pageframes to update, if necessary
     */
    public void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.setActiveNode(node);
        TreePanelUpdateManager.getManager().rebuildTreePanels(affectedNodes);
    }    
    
    public String getPresentationName() {
        return "Name Change of " + newName;
    }
}
