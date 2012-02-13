
package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;

/**
 * Nodes may be collapsed as a byproduct of deleting/moving other nodes.
 *This Abstract class handles that overhead.
 */
public class AbstractCollapsableUndoableEdit extends AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4590056450931989470L;
	protected Node node;
    protected Node nodeToCollapse;
    protected Node oldGrandParent;
    protected int parentIndex;
    
    protected ArrayList affectedNodes;
    protected boolean oldParentHasChanged, oldGrandParentHasChanged;

    /**
     *Track node to collapse, and previous status of "changed" variables
     */
    public AbstractCollapsableUndoableEdit(Node node, Node toCollapse) {
        this.node = node;
        
        nodeToCollapse = toCollapse;
        
        affectedNodes = new ArrayList();
        if ( nodeToCollapse != null ) {
            affectedNodes.add(toCollapse.getParent());
        }
        
        oldParentHasChanged = node.getParent().getChildrenChanged();
        oldGrandParent = node.getParent().getParent();
        if (oldGrandParent != null) {
            oldGrandParentHasChanged = node.getParent().getParent().getChildrenChanged();
        }
        
    }

    /**
     *Collapse node - reassign parent child relationships as needed
     */
    public void redo() {
        Node parent = node.getParent();
        
        if (nodeToCollapse == null ) {
            parent.setChildrenChanged(true);
            parent.removeChild(node);
            return;
        }
        
        if ( nodeToCollapse == parent ) {
            //Node otherChild = parent.getChildAtIndex(1 - parent.indexOfChild(node));
            parentIndex = oldGrandParent.indexOfChild(parent);
            oldGrandParent.removeChild(parent);
            int tempIndex = parentIndex;
            Iterator it = parent.getChildren().iterator();
            while (it.hasNext()) {
                Node ch = (Node) it.next();
                if (ch != node) {
                    oldGrandParent.addToChildren(tempIndex++, ch);
                }  
            }
            oldGrandParent.setChildrenChanged(true);
        } else { 
            //nodeToCollapse is the other child
            parent.removeChild(node);
            parent.removeChild(nodeToCollapse);
            
            Iterator it = nodeToCollapse.getChildren().iterator();
            while (it.hasNext() ) {
                Node ch = (Node)it.next();
                parent.addToChildren(ch);
            }            
            parent.setChildrenChanged(true);
            parent.setChildCountOnServer(parent.getChildren().size());
        }
        NodeGraveyard.getGraveyard().addNode(nodeToCollapse);
        
    }

    /**
     *Pull collapsed node bak onto the tree
     */
    public void undo() {
        if (nodeToCollapse != null ) {
            NodeGraveyard.getGraveyard().getNode(nodeToCollapse.getId());
        }

        node.getParent().setChildrenChanged(oldParentHasChanged);
        if (node.getParent().getParent() != null) {
            node.getParent().getParent().setChildrenChanged(oldGrandParentHasChanged);
        }
        TreePanel.getTreePanel().getTree().updateTree(oldTreeString);      
        
        updateTreePanel();
    }
    
    /**
     *Rebuild treepanels to reflect new tree structure
     */
    public void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.setActiveNode(node);
        TreePanelUpdateManager.getManager().rebuildTreePanels(affectedNodes);
    }

}
