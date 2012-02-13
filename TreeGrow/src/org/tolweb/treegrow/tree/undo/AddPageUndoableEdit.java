
package org.tolweb.treegrow.tree.undo;

import java.util.*;

import org.tolweb.treegrow.tree.*;


/**
 * Add new page to node.  Handle Frame updates as needed
 */
public class AddPageUndoableEdit extends AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9076005037106355315L;
	private boolean oldPageChanged;
    private boolean oldPageAdded;
    private Node node;
    private boolean oldHasPageOnServer;
    
    /** Creates a new instance of AddPageUndoableEdit */
    public AddPageUndoableEdit(Node n) {
        oldPageChanged = n.getPageChanged();
        node = n;
        oldHasPageOnServer = node.getHasPageOnServer();
        oldPageAdded = n.getPageWasAdded();
        redo();
    }
    
    public void redo() {
        node.setHasPageOnServer(true);
        node.setPageChanged(true);
        node.setPageWasAdded(true);
        // set checked out because it is definitely checked out
        // and if it didn't have a page before, it wasn't an issue, but it now is
        node.setCheckedOut(true);
        updateTreePanel();
    }
    
    public void undo() {
        node.setHasPageOnServer(oldHasPageOnServer);
        node.setPageChanged(oldPageChanged);
        node.setPageWasAdded(oldPageAdded);
        updateTreePanel();
    }
    
    /** 
     *rebuild treepanels and pagepanels
     */
    protected void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        if (treePanel != null) {
            treePanel.setActiveNode(node);        

            ArrayList list = new ArrayList();
            list.add(node);
            TreePanelUpdateManager.getManager().rebuildTreePanels(list);
        }
    }
    
    public String getPresentationName() {
        return "Add Page to Node " + node.getName();
    }
}
