package org.tolweb.treegrow.tree.undo;

import java.util.*;
import org.tolweb.treegrow.tree.*;


/** 
 *Abstract Class for editing extinct/confidence/phylesis/leaf status. Simply
 *keep track of the new and previous values for the variable of interest,
 *and set the new on a redo and old on an undo
 */
public abstract class AbstractNodeValueUndoableEdit extends AbstractTreeEditorUndoableEdit {
    protected Node node;
    protected boolean oldHasChanged;
    protected int oldValue;
    protected int newValue;
    protected ArrayList affectedNodes;
    
    /**
     *Store old value and new value
     */
    public AbstractNodeValueUndoableEdit(Node n, int newVal) {
        super();
        node = n;
        oldValue = grabOldValue(); //node.getExtinct();
        newValue = newVal;

        affectedNodes = new ArrayList();
        affectedNodes.add(node);
        redo();
    }

    /** 
     *set to new value
     */
    public void redo() {
        setValue(newValue);
        setNewHasChanged();
        updateTreePanel();
    }
    
    /**
     *set to old value
     */
    public void undo() {
        setValue(oldValue);
        setOldHasChanged();
        updateTreePanel();
    }   
    
    /**
     *Cause treepanels and pagepanels to represent the new value
     */
    protected void updateTreePanel() {
        TreePanel treePanel = TreePanel.getTreePanel();
        treePanel.setActiveNode(node);        
        
        TreePanelUpdateManager.getManager().rebuildTreePanels(affectedNodes);
        treePanel.repaint();
    }

    public int getOldValue(){
        return oldValue;
    }

    public int getNewValue(){
        return newValue;
    }    

    
    public abstract String getPresentationName();
    
    /**
     *Abstract, overridden to assign value to correct variable
     */
    public abstract void setValue(int val);

    
    /**
     *Abstract, overridden to capture value from correct variable
     */    
    protected abstract int grabOldValue();

    /**
     *Abstract, overridden to assign correct "changed" variable
     */        
    public abstract void setNewHasChanged();

    /**
     *Abstract, overridden to assign correct "changed" variable
     */            
    public abstract void setOldHasChanged();
}

