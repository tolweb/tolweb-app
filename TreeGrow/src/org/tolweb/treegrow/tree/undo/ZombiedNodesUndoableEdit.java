/*
 * ZombiedNodesUndoableEdit.java
 *
 * Created on November 7, 2003, 2:21 PM
 */

package org.tolweb.treegrow.tree.undo;

import java.util.*;

/**
 *
 * @author  dmandel
 */
public abstract class ZombiedNodesUndoableEdit extends AbstractTreeEditorUndoableEdit {
    protected ArrayList zombiedNodes;
    protected boolean fromConstructor;
    
    /** Creates a new instance of ZombiedNodesUndoableEdit */
    public ZombiedNodesUndoableEdit() {
        zombiedNodes = new ArrayList();
        fromConstructor = true;
    }
    
    
    public ArrayList getZombiedNodes() {
        return zombiedNodes;
    }    
    
    public abstract void updateTreePanel();
    
}
