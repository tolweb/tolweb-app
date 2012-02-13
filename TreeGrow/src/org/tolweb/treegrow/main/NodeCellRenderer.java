/*
 * NodeCellRenderer.java
 *
 * Created on March 12, 2004, 9:35 AM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import javax.swing.*;
import org.tolweb.treegrow.tree.*;

/**
 * Overrides the renderer to show the name of the node
 */
public class NodeCellRenderer extends DefaultListCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6130122802749762406L;

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ((JLabel) result).setText(((Node) value).getName());
        return result;
    }  
}  