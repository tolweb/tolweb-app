/*
 * BorderedRenderer.java
 *
 * Created on April 12, 2004, 11:23 AM
 */

package org.tolweb.treegrow.page;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 * Used to make the other columns look uniform
 */
public class BorderedRenderer extends JLabel implements TableCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7706118417403653108L;

	public BorderedRenderer() {
        setBorder(BorderFactory.createEtchedBorder());
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((String) value);
        return this;
    }

}
