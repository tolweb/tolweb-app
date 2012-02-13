/*
 * NodeAttachmentTable.java
 *
 * Created on March 10, 2004, 11:51 AM
 */

package org.tolweb.treegrow.main;

import java.util.*;

import org.tolweb.treegrow.page.*;

/**
 *
 * @author  dmandel
 */
public class NodeAttachmentTable extends AbstractDraggableTable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 101187703312831057L;

	/** Creates a new instance of NodeAttachmentTable */
    public NodeAttachmentTable(Vector list) {
        super(list, null);
        getColumnModel().setColumnSelectionAllowed(false);
        getTableHeader().setReorderingAllowed(false);
        setModel(new NodeAttachmentModel());
        initIndexColumn();
    }

    public boolean respondToMouseEvents() {
        return false;
    }

    public class NodeAttachmentModel extends AbstractDraggableTable.AbstractEditableTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 808042546085755997L;

		public NodeAttachmentModel() {
            super(NodeAttachmentTable.this);
        }

        /** Returns the number of rows in the model. A
         * <code>JTable</code> uses this method to determine how many rows it
         * should display.  This method should be quick, as it
         * is called frequently during rendering.
         *
         * @return the number of rows in the model
         * @see #getColumnCount
         *
         */
        public int getRowCount() {
            return getList().size();
        }  

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int index) {
            switch (index) {
                case 0: return "    ";
                case 1: return "Group           ";
            }
            return "";
        }

        public Object getValueAt(int row, int col) {
            NodeAttachment na = (NodeAttachment) getList().get(row);
            if (col == 1) {
                return na.getNodeName();
            }
            return "";
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }
}
