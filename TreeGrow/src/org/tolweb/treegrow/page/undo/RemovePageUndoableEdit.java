/*
 * RemovePageUndoableEdit.java
 *
 * Created on August 18, 2003, 11:56 AM
 */

package org.tolweb.treegrow.page.undo;

import java.util.*;

import org.tolweb.treegrow.tree.*;
import org.tolweb.treegrow.tree.undo.*;

/**
 * Removes the page from the specified node
 */
public class RemovePageUndoableEdit extends AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2842071851495534124L;
	private Node node;
    private boolean oldPageChanged;
    private boolean oldPageWasRemoved;
    
    /** Creates a new instance of RemovePageUndoableEdit */
    public RemovePageUndoableEdit(Node n) {
        node = n;
        oldPageChanged = node.getPageChanged();
        oldPageWasRemoved = node.getPageWasRemoved();
        redo();
    }
    
    public void redo() {
        node.setPageChanged(true);
        node.setHasPageOnServer(false);
        node.setPageWasRemoved(true);
        updateTreePanel();
    }
    
    public void undo() {
        node.setPageChanged(oldPageChanged);
        node.setHasPageOnServer(true);
        node.setPageWasRemoved(oldPageWasRemoved);
        updateTreePanel();
    }
    
    public boolean canRedo() {
        return true;
    }
    
    /**
     * Updates the tree panel so that the node circle no longer has the page
     * rectangle in the middle of it.  Also removes the page-related tree 
     * items
     */
    protected void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.setActiveNode(node);        
        
        ArrayList list = new ArrayList();
        list.add(node);
        TreePanelUpdateManager.getManager().rebuildTreePanels(list);

        //updatePageEditorForNode(node);
    }
    
    public String getPresentationName() {
        return "Remove Page";
    }
    
}
