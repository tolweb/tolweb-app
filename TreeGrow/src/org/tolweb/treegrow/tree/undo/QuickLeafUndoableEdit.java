/*
 * Created on Mar 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrow.tree.undo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.TreePanel;
import org.tolweb.treegrow.tree.TreePanelUpdateManager;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuickLeafUndoableEdit extends AbstractTreeEditorUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4158897151595931110L;
	private Hashtable oldNodeLeafValues;
    private Hashtable oldNodeLeafChangedValues;
    private Node node;
    
    public QuickLeafUndoableEdit(Node n) {
        node = n;
        oldNodeLeafValues = new Hashtable();
        oldNodeLeafChangedValues = new Hashtable();
        ArrayList subtreeNodes = n.getSubtreeNodes();
        ArrayList newLeaves = new ArrayList();
        for (Iterator iter = subtreeNodes.iterator(); iter.hasNext();) {
            Node nextNode = (Node) iter.next();
            if (nextNode.getChildCountOnServer() == 0 && nextNode.getChildren().size() == 0) {
                newLeaves.add(nextNode);
            }
        }
        for (Iterator iter = newLeaves.iterator(); iter.hasNext();) {
            Node nextNode = (Node) iter.next();
            oldNodeLeafValues.put(nextNode, new Boolean(nextNode.getIsLeaf()));
            oldNodeLeafChangedValues.put(nextNode, new Boolean(nextNode.getLeafChanged()));
        }
        redo();
    }
    
    public void redo() {
        super.redo();
        for (Iterator iter = oldNodeLeafValues.keySet().iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            node.setIsLeaf(true);
            node.setLeafChanged(true);
        }
        updateTreePanel();        
    }
    
    public void undo() {
        super.undo();
        for (Iterator iter = oldNodeLeafValues.keySet().iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            Boolean wasLeaf = (Boolean) oldNodeLeafValues.get(node);
            node.setIsLeaf(wasLeaf.booleanValue());
            Boolean leafWasChanged = (Boolean) oldNodeLeafChangedValues.get(node);
            node.setLeafChanged(leafWasChanged.booleanValue());
        }
        updateTreePanel();
    }
    
    protected void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();        
        
        TreePanelUpdateManager.getManager().rebuildTreePanels(oldNodeLeafValues.keySet());
        treePanel.repaint();        
    }
    
    public String getPresentationName() {
        return "Mark Terminals as Leaves for Node " + node.getName();
    }
        
}
