/*
 * AbstractDraggableTable.java
 *
 * Created on July 17, 2003, 9:16 AM
 */

package org.tolweb.treegrow.page;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.page.undo.*;
import org.tolweb.treegrow.tree.*;

/**
 * Abstract JTable subclass used to provide dragging functionality.  Subclasses
 * should set their own custom table model.
 */
public abstract class AbstractDraggableTable extends javax.swing.JTable implements MouseListener, MouseMotionListener {
    private static final int INDEX_COLUMN_WIDTH = 30;
    protected Vector list;
    protected int from = -1, initialFrom = -1;
    protected static Cursor openHandCursor, closedHandCursor;
    protected static Cursor defaultCursor;
    protected IndexCellRenderer indexCellRenderer;
    protected boolean dragging = false;
    protected AbstractPageEditorPanel parentPanel;
    protected Vector listSelectionListeners = new Vector();

    static {
	defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    }
    
    /**
     * Constructs a new table using the data list
     *
     * @param data The list to display in the table
     */
    public AbstractDraggableTable(Vector data) {
        this(data, null);
    }

    /**
     * Constructs a new table using the data list and editor panel.  The 
     * editor panel is used to send callbacks when table data or selection
     * changes
     *
     * @param data The list to display in the table
     * @param p The editor panel to send notifications to when things change
     */
    public AbstractDraggableTable(java.util.Vector data, AbstractPageEditorPanel p) {
        super(data.size(), 1);
	openHandCursor = TreeToolbar.getOpenHandCursor();
        closedHandCursor = TreeToolbar.getClosedHandCursor();
        list = data;
        parentPanel = p;
	getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        
        if (respondToMouseEvents()) {            
            // Mouse listener to determine whether we are dragging and reordering
            addMouseListener(this);
            
            // MouseMotion listener to determine whether to draw the different cursor
            // and whether we actually want to reorder the list
            addMouseMotionListener(this);
        }
	getColumnModel().setColumnSelectionAllowed(false);
	getTableHeader().setReorderingAllowed(false);
        indexCellRenderer = new IndexCellRenderer();
        setDefaultEditor(String.class, new TextFieldCellEditor());
    }
    
    /**
     * Can be overridden by subclasses so the hand cursor doesn't show up and
     * AbstractUndoableEdits aren't fired on dragging
     *
     *  @return a boolean indicating whether the hand cursor should show up
     *          above the numbers
     */
    protected boolean respondToMouseEvents() {
        return true;
    }

    /**
     * Should be called by subclasses after table model construction.  This
     * can't occur until after model construction since the column model changes
     * after the table model
     */
    protected void initIndexColumn() {
        makeStiffColumn(INDEX_COLUMN_WIDTH, 0);
    }
    
    /**
     * Used to make a column a fixed size and prevent resizing.
     *
     * @param size The size in pixels of the column to stiffify
     * @param index The index of the column
     */
    protected void makeStiffColumn(int size, int index) {
        TableColumn indexColumn = getColumnModel().getColumn(index);
        indexColumn.setPreferredWidth(size);
        indexColumn.setMinWidth(size);
        indexColumn.setMaxWidth(size);
        indexColumn.setResizable(false);
    }
    
    public void setList(Vector lst) {
        list = lst;
    }
    
    /**
     * Adds the passed-in edit to the editor panel's undomanager
     *
     * @param edit The undoable edit to add to the undo manager
     */
    public void updateUndoStuff(TableItemUndoableEdit edit) {
        if (parentPanel != null) {
            parentPanel.updateUndoStuff(edit);
        }
    }
    
    public TableCellRenderer getCellRenderer(int row, int col) {
	if (col == 0) {
	    return indexCellRenderer;
	} else {
	    return super.getCellRenderer(row, col);
	}
    }
    
    /**
     * Here so that subclasses can prevent certain rows from being dragged
     *
     * @param The row index to check if it can be dragged
     * @return True if the row can be dragged, false otherwise
     */
    public boolean isDraggableRow(int index) {
        return true;
    }
      
    /**
     * Deletes the currently selected item
     */
    public void deleteSelected() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1) {
            list.remove(selectedRow);
            ((AbstractEditableTableModel) getModel()).removeRow(selectedRow);
        }
    }
    
    public Object getSelectedObject() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1) {
            return list.get(selectedRow);
        } else {
            return null;
        }
    }
    
    /**
     * Clears some internal bookkeeping variables so we don't trigger reorder
     * events on the table after we programatically select things.
     */
    public void clearDraggingVars() {
        from = -1;
        initialFrom = -1;
        dragging = false;
    }
    
    /**
     * Returns the number of items in the table
     *
     * @return The number of items in the table
     */
    public int getTableSize() {
        return list.size();
    }
    
    /**
     * Returns the list of items in the table
     *
     * @return The list of items in the table
     */
    public Vector getList() {
        return list;
    }
    
    /**
     * Returns the panel that contains this table
     *
     * @return The panel that contains this table
     */
    public AbstractPageEditorPanel getParentPanel() {
        return parentPanel;
    }
    
           
    /**
     * Called when the selection in the table changes.  Overridden to sync
     * selected objects with the parent panel and to notify other listeners
     * that the selection has changed
     *
     * @param e The event that was triggered by the selection
     */
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        if (!dragging) {
            int selectedRow = getSelectedRow();
            if (selectedRow == -1 && list != null && list.size() > 0) {
                selectedRow = 0;
                setRowSelectionInterval(0, 0);
            }
            System.out.println("");
            if (parentPanel != null && parentPanel instanceof TableSelectableEditorPanel && selectedRow != -1) {
                ((TableSelectableEditorPanel) parentPanel).setSelectedObject((ChangedFromServerProvider) list.get(selectedRow));
            }
        }
        if (listSelectionListeners != null) {
            Iterator it = listSelectionListeners.iterator();
            while (it.hasNext()) {
                ListSelectionListener l = (ListSelectionListener) it.next();
                l.valueChanged(e);
            }
        }
    }
    
    /**
     * Adds a list selection listener to the table
     *
     * @param l The listener to add
     */
    public void addListSelectionListener(ListSelectionListener l) {
        listSelectionListeners.add(l);
    }
    
    /**
     * Overridden to return true to make event-firing work correctly
     *
     * @return true
     */
    public boolean getSurrendersFocusOnKeystroke() {
        return true;
    }
    
    /**
     * Overridden to show the grab-hand cursor if the mouse is over a number
     *
     * @param m The mouseevent fired by motion
     */
    public void mouseMoved(java.awt.event.MouseEvent m) {
        int x = m.getX();
        if (x <= INDEX_COLUMN_WIDTH) {
            setCursor(openHandCursor);
        } else {
            setCursor(defaultCursor);
        }
    }

    /**
     * Overridden to update the UI and list 
     * as the user drags items in the table around
     *
     * @param m The event that  was fired by dragging
     */
    public void mouseDragged(java.awt.event.MouseEvent m) {
        if (m.getX() <= INDEX_COLUMN_WIDTH || dragging) {
            int to = getSelectedRow();
            if (!isDraggableRow(to)) {
                //initialFrom = -1;
                return;
            }
            if (to == from || initialFrom == -1){
                return;
            }
            if (to == -1 && from != -1) {
                to = from;
                setRowSelectionInterval(from, from);
            }
            if (to >= 0 && to < list.size() && from >= 0 && from < list.size()) {
                Object removed = list.remove(from);
                list.add(to, removed);
                //clearSelection();
                ((AbstractTableModel) getModel()).fireTableDataChanged();
                ((AbstractTableModel) getModel()).fireTableRowsUpdated(from, to);
                from = to;

                setRowSelectionInterval(to, to);
            }
        }
    }    
    
    /**
     * Overridden to start drag monitoring
     *
     * @param The mouseevent fired by the press
     */
    public void mousePressed(java.awt.event.MouseEvent m) {
        if (m.getX() <= INDEX_COLUMN_WIDTH && isDraggableRow(getSelectedRow())) { 
            dragging = true;
            from = initialFrom = getSelectedRow();
        }
        if (getCursor() == openHandCursor) {
            setCursor(closedHandCursor);
        }
    }

    /**
     * Overridden to fire an undoable edit (if necessary) once the user releases
     * after dragging
     *
     * @param The mouseevent fired by the press
     */
    public void mouseReleased(MouseEvent e) {
        if (initialFrom != -1) {
            int selectedRow = getSelectedRow();
            // Adjust so that if a drag occurred and was released onto one 
            // of the non-moveable items the edit thinks the release 
            // occurred on the last-draggable item
            if (!isDraggableRow(selectedRow)) {
                if (selectedRow == 0 || selectedRow == 1) {
                    selectedRow = 2;
                } else {
                    selectedRow = list.size() - 3;
                }
            }
            if (selectedRow != -1 && initialFrom != selectedRow) {
                updateUndoStuff(new ReorderTableItemUndoableEdit(parentPanel.getPageFrame(), parentPanel, (ChangedFromServerProvider) list.get(selectedRow), this, initialFrom, selectedRow));
            }
        }
        clearDraggingVars();
        if (e.getX() <= INDEX_COLUMN_WIDTH) {
            setCursor(openHandCursor);
        } else {
            setCursor(defaultCursor);
        }
    }
    
    public void setSelectedObject(Object o) {
        int index = getList().indexOf(o);
        if (index != -1) {
            if (getCellEditor() != null) {
                getCellEditor().cancelCellEditing();
            }
            clearSelection();
            setRowSelectionInterval(index, index);
        }
    }
    
    public void mouseExited(MouseEvent e) {}
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {}
    
    /**
     * TableModel subclass to allow for row deletion and insertion
     */
    public abstract class AbstractEditableTableModel extends AbstractTableModel {
        protected AbstractDraggableTable table;
        
        public AbstractEditableTableModel(AbstractDraggableTable t) {
            super();
            table = t;
        }
        
        public void addRow() {
            fireTableRowsInserted(list.size() - 1, list.size() - 1); 
        }
        
        public void removeRow(int index) {
            fireTableRowsDeleted(index, index);
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
            return list.size();
        }  
        
	public boolean isCellEditable(int row, int col) {
	    return col != 0;
	}          
    }
    
    /**
     * Used to draw a panel with a number on it that indicates which row this
     * is in the table
     */
    protected class IndexCellRenderer implements TableCellRenderer {
	protected JLabel indexLabel;
	protected JPanel panel;

	public IndexCellRenderer() {
	    panel = new JPanel();
	    panel.setBackground(Color.white);
	    panel.setLayout(new BorderLayout());
	    indexLabel = new JLabel("");
	    indexLabel.setBorder(BorderFactory.createEmptyBorder());
	    panel.add(java.awt.BorderLayout.CENTER, indexLabel);
            panel.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    panel.requestFocus();
                }
            });
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
	    indexLabel.setText(" " + (row + 1));
	    return panel;
	}
    }
    
    /**
     * Class used to associate a unique text field with each editor cell.  This
     * is done in order to allow the focusLost method to call setValueAt on the
     * table when focus is lost on the text field.  This is different behavior
     * from the standard JTable event-handling model.
     */
    private class TextFieldCellEditor extends DefaultCellEditor {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1413658835876529886L;
		private Hashtable fieldToCellMapping;
        private boolean resettingText;
        
        public TextFieldCellEditor() {
            super(new JTextField());
            setClickCountToStart(1);
            fieldToCellMapping = new Hashtable();
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            String hashKey = "" + row + ":" + column;
            RowColTextField field = (RowColTextField) fieldToCellMapping.get(hashKey);
            if (field == null) {
                field = new RowColTextField(row, column);
                fieldToCellMapping.put(hashKey, field);
            }
            editorComponent = field;
            resettingText = true;
            field.setText((String) getValueAt(row, column));
            resettingText = false;
            return field;
        }
              
        public Object getCellEditorValue() {
            return ((RowColTextField) editorComponent).getText();
        }
        
        /**
         * Overridden in order to not use the default JTable value-setting
         * mechanism.
         */
        public boolean stopCellEditing() {
            cancelCellEditing();
            return true;
        }
        
        /**
         * TextField subclass to keep track of which row and column this
         * textfield is associated with.
         */
        private class RowColTextField extends ChangeMonitoringTextField { 
            /**
			 * 
			 */
			private static final long serialVersionUID = 4285535146125711345L;
			private final int row;
            private final int col;
            
            public RowColTextField(int r, int c) {
                super();
                row = r;
                col = c;
                addFocusListener(new FocusListener() {
                    public void focusLost(FocusEvent e) {
                        Object value = getModel().getValueAt(row, col);
                        if (value == null) {
                            if (!getText().equals("")) {
                                getModel().setValueAt(getText(), row, col);
                            }
                        } else {                            
                            if (!value.equals(getText())) {
                                getModel().setValueAt(getText(), row, col);
                            }                        
                        }
                    }

                    public void focusGained(FocusEvent e) {
                    }
                });
            }
            
            public int getRow() {
                return row;
            }
            
            public int getCol() {
                return col;
            }
        }
    }    
    
    /*
     * Not going to try and make the expandable rows for now.  Keeping it here in
     * case it gets revived at some point.

     *     public TableCellEditor getCellEditor(int row, int col) {
        if (textAreaCols.contains(new Integer(col))) {
            return textCellEditor;
        } else {
            return super.getCellEditor(row, col);
        }
    }    
     *
    protected java.util.ArrayList textAreaCols;    
    
     private class TextAreaCellRenderer implements TableCellRenderer {
	private ScrollableTextAreaPanel panel;
        
	public TextAreaCellRenderer() {
	    panel = new ScrollableTextAreaPanel();
	}
        
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Synonym syn = (Synonym) list.get(row);
            panel.setText(syn.getSynonym());
            if (isSelected) {
                panel.setBackground(selectedColor);
            } else {
                panel.setBackground(Color.white);
            }
	    return panel;
	}
    }
    
    private class TextAreaCellEditor extends DefaultCellEditor {
        private ScrollableTextAreaPanel panel;
        
        public TextAreaCellEditor() {
            super(new JCheckBox());
	    panel = new ScrollableTextAreaPanel();
            editorComponent = panel;
            setClickCountToStart(1);
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                panel.setBackground(selectedColor);
            } else {
                panel.setBackground(Color.white);
            }
            Synonym syn = (Synonym) list.get(row);
            panel.setText(syn.getSynonym());
            return editorComponent;
        } 
        
        protected void fireEditingStopped() {
            Synonym syn = (Synonym) list.get(getSelectedRow());
            syn.setSynonym(panel.getText());
            System.out.println("Yo Im getting called");
        }
        
        public void cancelCellEditing() {
            System.out.println("cancel");
        }
        
        public Object getCellEditorValue() {
            return panel.getText();
        }
    }
    
    /**
     * Class used to hold a scrollpane with a text area on it.
     /
    private class ScrollableTextAreaPanel extends JPanel {
        private JTextArea wrappingArea;
        
        public ScrollableTextAreaPanel() {
            setLayout(new BorderLayout());
            wrappingArea = new JTextArea();
            wrappingArea.setLineWrap(true);
            wrappingArea.setEditable(true);
            wrappingArea.setEnabled(true);
            wrappingArea.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    int selectedRow = getSelectedRow();
                    if (selectedRow != -1) {
                        int rowHeight = getRowHeight(getSelectedRow());
                        int height = wrappingArea.getFontMetrics(wrappingArea.getFont()).getHeight() * wrappingArea.getLineCount();
                        System.out.println("rowheight  = " + rowHeight + " text area height = " + height + " rows = " + wrappingArea.getLineCount());
                        if (height >= rowHeight) {
                            setRowHeight(selectedRow, height + 5);
                        }
                    }
                }
                public void removeUpdate(DocumentEvent e) {}
                public void changedUpdate(DocumentEvent e) {}
            });
            add(BorderLayout.CENTER, wrappingArea);
        }
        
        public String getText() {
            return wrappingArea.getText();
        }

        public void setText(String value) {
            wrappingArea.setText(value);
        }
        
        public void setBackground(Color c) {
            super.setBackground(c);
            // Looks like this is getting called from a superclass constructor,
            // so check to make sure this isn't null
            if (wrappingArea != null) {
                wrappingArea.setBackground(c);
            }
        }
    }*/
} 
