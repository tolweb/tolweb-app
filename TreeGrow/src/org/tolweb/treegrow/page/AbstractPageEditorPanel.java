/*
 * AbstractPageEditorPanel.java
 *
 * Created on July 16, 2003, 2:05 PM
 */

package org.tolweb.treegrow.page;

import javax.swing.*;
import javax.swing.undo.*;

import org.tolweb.treegrow.tree.*;

/**
 * Abstract class used as a convenience to get easy access to the node and
 * page objects for panels that are used in the PageFrame page editor.
 */
public abstract class AbstractPageEditorPanel extends JPanel {
    protected PageFrame pageFrame;
    protected Node node;
    protected boolean tableChanging = false;
    protected boolean inUndoRedo;    
    //protected Page page;
    
    /** Creates a new instance of AbstractPageEditorPanel */
    public AbstractPageEditorPanel(PageFrame frame) {
        pageFrame = frame;
        if (frame instanceof PageFrame) {
            node = (pageFrame).getNode();
        }
    }
       
    
    /**
     * Overridden to return true in order to get events to fire correctly
     *
     * @return true
     */
    public boolean isFocusable() {
        return true;
    }
    
    /**
     * Updates the pageFrame's undo manager with the passed-in edit
     *
     * @param edit The edit to add to the pageFrame's undomanager
     */
    public void updateUndoStuff(AbstractUndoableEdit edit) {
        pageFrame.updateUndoStuff(edit);
    }
    
   /**
    * Returns the page being edited
    *
    * @return The page being edited
    */
    public Page getPage() {
        return null;
    }
    
    /**
     * Returns the Node being edited
     *
     * @return The Node being edited
     */
    public Node getNode() {
        return node;
    }
    
    /**
     * Returns the frame containing this panel
     *
     * @return The frame containing this panel
     */
    public PageFrame getPageFrame() {
        return pageFrame;
    }
    
    /**
     * Called during internal table event firing to prevent this panel from
     * firing events that shouldn't get fired
     */
    public void setTableChanging(boolean value) {
        tableChanging = value;
    }
    
    public void setInUndoRedo(boolean value) {
        inUndoRedo = value;
    }
    
    public boolean inUndoRedo() {
        return inUndoRedo;
    }    
}
