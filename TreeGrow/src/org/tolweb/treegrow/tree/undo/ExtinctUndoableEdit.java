package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.Node;
import org.tolweb.treegrow.tree.TreePanel;


/** 
 *Editing extinct value on node. Simply keep track of the new and 
 * previous values for extinct,  and set the new on a redo and old 
 * on an undo
 */
public class ExtinctUndoableEdit extends AbstractNodeValueUndoableEdit {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4628632248936602992L;
	/**
     * Used to store the old extinct values for any child nodes who may
     * have their values changed due to a parent group being marked extinct
     */
    private Hashtable oldExtinctMapping;
    private Hashtable oldExtinctChangedMapping;
    
    public String toString() {
        return "ExtinctUndoableEdit of " + node + " value = " + newValue + " oldTreeString = " + oldTreeString;
    }
    
    public ExtinctUndoableEdit(Node n, int newVal) {
        super(n, newVal);
        oldHasChanged = n.getExtinctChanged();
    }
    
    public void setValue(int val) {
        node.setExtinct(val);   
    }
    
    protected int grabOldValue() {
        return node.getExtinct();   
    }
    
    public void redo() {
        boolean firstTime = oldExtinctMapping == null;
        if (newValue == Node.EXTINCT) {
            if (firstTime) {
                oldExtinctMapping = new Hashtable();
                oldExtinctChangedMapping = new Hashtable();
            }
            Stack childStack = new Stack();
            childStack.push(node);
            while (!childStack.empty()) {
                Node currentNode = (Node) childStack.pop();
                if (! (TreePanel.getTreePanel().getTree().isTerminalNode(currentNode) && currentNode.getChildren().size()>0) ) {
                    if (firstTime) {
                        affectedNodes.add(currentNode);
                        oldExtinctMapping.put(currentNode, new Integer(currentNode.getExtinct()));
                        oldExtinctChangedMapping.put(currentNode, new Boolean(currentNode.getExtinctChanged()));
                    }                    
                    currentNode.setExtinct(Node.EXTINCT);
                    currentNode.setExtinctChanged(true);
                    Iterator it = currentNode.getChildren().iterator();
                    while (it.hasNext()) {
                        Node node = (Node) it.next();
                        if (node.getCheckedOut()) {
                            childStack.push(node);
                        }
                    }
                }
            }
        }
        super.redo();
    }
    
    public void undo() {
        if (oldExtinctMapping != null) {
            Iterator it = oldExtinctMapping.keySet().iterator();
            while (it.hasNext()) {
                Node node = (Node) it.next();
                node.setExtinct(((Integer) oldExtinctMapping.get(node)).intValue());
                node.setExtinctChanged(((Boolean) oldExtinctChangedMapping.get(node)).booleanValue());
            }
        }
        super.undo();
    }
    
    public String getPresentationName() {
        return "Mark Extinct of " + node.getName();
    }
       
    public void setNewHasChanged() {
        node.setExtinctChanged(true);
    }    

    public void setOldHasChanged() {
        node.setExtinctChanged(oldHasChanged);
    }  
    
}

