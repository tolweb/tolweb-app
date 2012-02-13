/*
 * Created on Dec 8, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.treegrow.tree;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import org.tolweb.treegrow.tree.undo.PriorityUndoableEdit;

public class PriorityPopupMenu extends AbstractPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7960501283651343536L;

	public ArrayList getStrings() {
		return Node.getPriorityStrings();
	}
	
    /** 
     * Returns undoable edit, which will specify the property changes to be 
     * made for the node of interest
     */    
    public AbstractUndoableEdit getEdit(int newVal) {
        return new PriorityUndoableEdit(node, newVal);
    }	

	public int getProperty() {
		return node.getPriority();
	}
}
