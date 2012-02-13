package org.tolweb.treegrow.tree;

import java.awt.event.*;
import javax.swing.*;

/** 
 *Actions employed by the TreeToolbar
 */
public class TreeAction extends AbstractAction {      
        
    /**
	 * 
	 */
	private static final long serialVersionUID = 8166867687218432683L;


	/** Creates a new instance of ToLAction */
    public TreeAction(String name) {
        try{
            putValue(javax.swing.Action.NAME,name);
        } catch(Exception error) {
        }
    }

    public TreeAction(String name, Icon icon)  {
        try{
            putValue(javax.swing.Action.NAME, name);
            putValue(javax.swing.Action.SMALL_ICON, icon);
        } catch(Exception error) {
        }
    }


    /** 
     *set the selected tool, based on a mouse click
     */
    public void actionPerformed(ActionEvent evt) {          
        ((TreeToolbar) ((JButton) evt.getSource()).getParent()).setSelectedTool(((ToolbarButton) evt.getSource()));
    }
}
