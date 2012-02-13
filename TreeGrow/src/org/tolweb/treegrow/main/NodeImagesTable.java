/*
 * NodeImagesTable.java
 *
 * Created on January 13, 2004, 1:25 PM
 */

package org.tolweb.treegrow.main;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import org.tolweb.treegrow.page.*;

/**
 *
 * @author  dmandel
 */
public class NodeImagesTable extends AbstractDraggableTable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4440532465521038537L;
	protected IdCellRenderer idCellRenderer;    
    private static final int ID_COLUMN_WIDTH = 50;
    
    public NodeImagesTable(Vector imgs) {
        this(imgs, null);
    }

    /** Creates a new instance of NodeImagesTable */
    public NodeImagesTable(Vector imgs, AbstractPageEditorPanel p) {
        super(imgs, p);
        setModel(new NodeImagesTableModel(this));
        idCellRenderer = new IdCellRenderer();
        makeStiffColumn(ID_COLUMN_WIDTH, 0);
    }
    
    public boolean isDraggableRow(int index) {
        return false;
    }
    
    public boolean respondToMouseEvents() {
        return false;
    }    
    
    public TableCellRenderer getCellRenderer(int row, int col) {
	if (col == 0) {
	    return idCellRenderer;
	} else {
	    return super.getCellRenderer(row, col);
	}
    }    
    
    private class NodeImagesTableModel extends AbstractDraggableTable.AbstractEditableTableModel {
        
        /**
		 * 
		 */
		private static final long serialVersionUID = 2049837475650256486L;

		public NodeImagesTableModel(AbstractDraggableTable t) {
            super(t);
        }        
        
        public int getColumnCount() {
            return 2;
        }
        
        public String getColumnName(int column) {
            if (column == 1) {
                return "Location";
            } else {
                return "ID";
            }
        }
        
        public boolean isCellEditable(int row, int col) {
            return false;
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            NodeImage img = (NodeImage) list.get(rowIndex);
            if (columnIndex == 1) {
                // If the image is local then display the file prefix, else
                // display its server location
                if (img.getId() > 0) {
                    return Controller.getController().getWebPath() + "tree/ToLimages/" + img.getLocation();
                } else {
                    return "file://" + img.getLocation();
                }
            } else {
                return "";
            }
        }
    }
    
    /**
     * Used to draw a panel with a number on it that indicates which row this
     * is in the table
     */
    protected class IdCellRenderer extends AbstractDraggableTable.IndexCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            NodeImage img = (NodeImage) list.get(row);
            indexLabel.setText(" " + (img.getId()));
	    return panel;
	}
    }    
}
