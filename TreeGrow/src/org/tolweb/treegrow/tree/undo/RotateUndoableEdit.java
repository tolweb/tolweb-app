
package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;

/**
 * Reverse order of children on a node
 */
public class RotateUndoableEdit extends AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = -460127762375756965L;
	private Node parent;
    private boolean oldParentHasChanged;
    private ArrayList affectedNodes;

    public String toString() {
        return "RotateUndoableEdit of node " + parent + " oldTreeString = " + oldTreeString;
    }
    
    /** 
     *  Identify parent of reversed nodes, and it's previous "changed" status
     */    
    public RotateUndoableEdit(Node parent) {
        this.parent = parent;
        
        affectedNodes = new ArrayList(); 
        affectedNodes.add(parent); 
        oldParentHasChanged = parent.getChildrenChanged();

        redo();
    }    
    
    public void redo() {
        reverseChildOrder();
        parent.setChildrenChanged(true);
        updateTreePanel();
    }   
    
    public void undo() {      
        reverseChildOrder();
        parent.setChildrenChanged(oldParentHasChanged);
        updateTreePanel();        
    }

    private void reverseChildOrder () {
        Stack stack = new Stack();
        Iterator it = parent.getChildren().iterator();
    
        while (it.hasNext()) {
            stack.push(it.next());
        }
        
        parent.clearChildren();
        
        while( !stack.empty()) {
            parent.addToChildren((Node)stack.pop());
        }
    }
    
    private void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.setActiveNode(parent);
        TreePanelUpdateManager.getManager().rebuildTreePanels(affectedNodes);
        treePanel.repaint();
    }
    
    public String getPresentationName() {
        return "Rotate subgroups of " + parent.getName() ;
    }    
}
