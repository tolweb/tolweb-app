
package org.tolweb.treegrow.tree;

import java.util.*;
import javax.swing.undo.AbstractUndoableEdit;
import org.tolweb.treegrow.tree.undo.PhylesisUndoableEdit;


/** Defines the popup that arises when the user clicks on a node while 
 * the phylesis tool is active. 
 **/
public class PhylesisPopupMenu extends AbstractPopupMenu {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6490198721478329553L;


	/** 
     * Returns list of choices to appear on the popup
     */
    public ArrayList getStrings() {
        return Node.getPhylesisStrings();
    }


    /** 
     * Returns undoable edit, which will specify the property changes to be 
     * made for the node of interest
     */    
    public AbstractUndoableEdit getEdit(int newVal) {
        return new PhylesisUndoableEdit(node, newVal);
    }


    /**
     * Determine the current value of the phylesis property 
     * on the node of interest
     */    
    public int getProperty() {
        return node.getPhylesis();
    }
    
}

