package org.tolweb.treegrow.tree;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.tolweb.treegrow.main.Controller;
import org.tolweb.treegrow.tree.undo.*;

/**
 * Subclass of JTextField with no border that becomes highlighted 
 * and gets a border when the text in it is being edited.
 */
public class FloatingTextField extends JTextField implements MouseListener,MouseMotionListener, FocusListener, ActionListener, DocumentListener, UndoableEditListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8576378797247880637L;
	private static final int DEFAULT_COLUMNS = 10;
    private static final int X_PADDING = 4;

    //private static FontMetrics myFontMetrics;
    private static Border emptyBorder = BorderFactory.createEmptyBorder();
    private static Border lineBorder = BorderFactory.createLineBorder(Color.black);
    private NodeView nodeView;
    private boolean respondToMouse = false;
    
    
    static {
        JPanel jp = new JPanel();
    }

    /** 
     * Create a FloatingTextfield, associated with a nodeview
     */
    public FloatingTextField(NodeView view) {
        super(view.getNode().getName(), DEFAULT_COLUMNS);
        nodeView = view;
        if (nodeView.getPanel() instanceof TreePanel) {
            respondToMouse = true;
        }
        
        doInit();
        if ( TreePanel.getTreePanel() != null) {
            addKeyListener(TreePanel.getTreePanel().getTreeFrame());
        }
    }
        
    /** 
     *general initialization stuff, called by constructor
     */
    private void doInit() {
        if (nodeView.isTerminal()) {
            setHorizontalAlignment(SwingConstants.LEFT);
        } else {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        setBorder(emptyBorder);

        addFocusListener(this);
        addActionListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        
        getDocument().addDocumentListener(this);
        getDocument().addUndoableEditListener(this);
        setOpaque(false);
        
        setDisabledTextColor(Color.black);
    }

    
    /**
     * Determines minimum size for the text field, in case it's empty
     */
    public int getMinTextFieldWidth() {
        return nodeView.getPanel().getDefaultStringWidth();
    }
    
    /**
     * Determines the current desired width for the text field
     */    
    public int getTextFieldWidth(String str) {
        return Math.max(getMinTextFieldWidth(), nodeView.getPanel().getFontMetrics().stringWidth(str)+4);
    }    
    
    public int getTextFieldHeight() {
        return nodeView.getPanel().getFontMetrics().getHeight() + 5;
    }


    /** 
     *how much longer is the text in this textfield than the current width
     * can hold? The current width is determined from the distance between 
     * the nodeview associated with this field and it's children
     */
    public int getTextOverrun() {
        int textFieldWidth = nodeView.getPanel().getFontMetrics().stringWidth(getText());
        // Here check to see if the textFieldWidth is greater than the distance between a node
        // and its parent.
        if (nodeView.getParent() != null) {
            return Math.max(textFieldWidth - (nodeView.getX() - (nodeView.getParent().getX() + X_PADDING)), 0);
        } else {
            //root 
            return Math.max(textFieldWidth + X_PADDING - getWidth(), 0);
        }
    }   
    
    public NodeView getNodeView() {
        return nodeView;
    }

    public void setNodeView(NodeView view) {
        nodeView = view;
        doInit();
    }

    /**
     * Determines the position of the Text field. Affected by text width 
     * and whether the node is Terminal
     */
    private Point buildTextFieldLoc() {
        int labelX, labelY ;            
        setFont(nodeView.getPanel().getFont());

        if (!nodeView.isTerminal()) {           
            int labelWidth = getTextFieldWidth(getText());
            labelX = nodeView.getX() - labelWidth + 1;
            labelY = nodeView.getY() - getTextFieldHeight() + nodeView.getPanel().getHalfCircleDiameter();
        } else {
            labelX = nodeView.getX() + 2*X_PADDING + nodeView.getPanel().getHalfCircleDiameter();
            labelY = nodeView.getY() - getTextFieldHeight()/2 + 4;
            if (nodeView.getNode().getTermWithSubTree()) {
                labelX += AbstractTreePanel.getSubtreeImageWidth() + X_PADDING;
            }
        }
        
        return new Point(labelX, labelY);
    }
    
    
    /**
     * The textField resizes itself according to whether it is wide enough
     * to hold the current string.  Also calls fuction to resize the containing 
     * NodePanel and its containing TreePanel 
     */    
    public int resizeTextField () {    
        return resizeTextField (false);
    }
    
    
    /**
     * The textField resizes itself according to whether it is wide enough
     * to hold the current string.  Also calls fuction to resize the containing 
     * NodePanel and its containing TreePanel 
     */        
    public int resizeTextField (boolean typing) {    

        int textFieldWidth = getTextFieldWidth(getText()) ;

        if (nodeView.isTerminal()) {
            // Store the old width so we can increase the containing TreePanel's preferred width by the same amt
            if (getWidth() < getMinTextFieldWidth()) {
                setSize(getMinTextFieldWidth(),getHeight());
            }
            int widthDifference = textFieldWidth - getWidth();

            //terminal node...keep expanding
            setSize(textFieldWidth,  getTextFieldHeight());
            return widthDifference;
        } else {
            //internal node
            if (!typing || getTextOverrun() == 0) {                
                //fits in the alloted line length
                setSize(textFieldWidth ,  getTextFieldHeight());
                setPreferredSize(new Dimension(textFieldWidth ,  getTextFieldHeight()));
                resetPosition();
            } // else, just let them type, but don't resize anything
            return 0;
            
        }
    }
    
    /**
     * Call resizeTextField, then deal with layou, viewport issues.
     */        
    public void resizeTextFieldWhileTyping () { 
        int shift = resizeTextField(true);
        if (shift>0) {
            AbstractTreePanel treePanel = nodeView.getPanel();
            
            int textFieldEnd = getX() + getWidth();
            
            if ( textFieldEnd > treePanel.getPreferredSize().width ) {
                treePanel.setPreferredSize(new Dimension(treePanel.getPreferredSize().width + shift,  treePanel.getPreferredSize().height));            
                treePanel.revalidate();
                // Only want to try and scroll a viewPort if the viewport is actually there.
            }

            Container parent = treePanel.getParent();
            if (parent instanceof JViewport) {
                JViewport viewPort = (JViewport) parent;
                if (viewPort!=null &&   textFieldEnd > (viewPort.getViewPosition().x + viewPort.getWidth()) ) {
                    //note: this doesn't always shift correctly, because of a delayed "revalidate" call
                    viewPort.setViewPosition(new Point(textFieldEnd-viewPort.getWidth(), viewPort.getViewPosition().y));
                }
            }                
        }
    }
    
    /** 
     * Cause the text field to reposition itself. May happen as result of
     * reorganizing the tree, or adding the extinct icon.
     */
    public void resetPosition () {
        Point loc = buildTextFieldLoc();
        setLocation( loc.x, loc.y );        
    }
    
    /**
     * Return a cursor, dependant on the selected tool.
     * In the case of many tools that may be selected, the cursor will not
     * change.
     */
    public Cursor getCursor() {
        if (respondToMouse) {
            if ( textableToolSelected()) {
                return Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
            } else {
                return TreePanel.getTreePanel().getCursor();            
            }
        } else {
            return Cursor.getDefaultCursor();
        }
    }
    
    /**
     * Determine if selected tool is one that allows user to edit text on 
     * this textfield. In the case of many tools that may be selected, 
     * the event will simply be passed along to the tree panel.
     */
    private boolean textableToolSelected() {    
        boolean textable = false;
        String activeTool = TreeToolbar.getSelectedTool();
        if ( activeTool != null) {
            textable = activeTool.equals(TreeToolbar.MOVE_BRANCH) || 
                        activeTool.equals(TreeToolbar.NAME_NODE) || 
                        activeTool.equals(TreeToolbar.ADD_SUBGROUP) ||
                        activeTool.equals(TreeToolbar.ADD_SUBGROUPS);
        }
        return textable;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    
    /** 
     * Invoked when a mouse button has been pressed on a component.
     * If a non-selectable tool is active, pass the event to the tree
     * panel on which the text field resides.
     */
    public void mousePressed(MouseEvent e) {        
        if ( !textableToolSelected() && respondToMouse) {
            TreePanel treePanel = TreePanel.getTreePanel();
            treePanel.requestFocus();

            int x;
            int y;
            if (TreeToolbar.getSelectedTool().equals(TreeToolbar.MOVE_TREE)) {
                x = getX() + e.getX();
                y = getY() + e.getY();
            } else {
                x = nodeView.getX();
                y = nodeView.getY();
            }
            
            treePanel.mousePressed(new MouseEvent((Component)e.getSource(),e.getID(),1,1,x,y,1,false));
        }
    }
    
    /** 
     * Invoked when a mouse button has been released on a component.
     * If a non-selectable tool is active, pass the event to the tree
     * panel on which the text field resides.
     */
    public void mouseReleased(MouseEvent e) {

        if ( !textableToolSelected()  && respondToMouse ) {
            TreePanel treePanel = TreePanel.getTreePanel();
            treePanel.requestFocus();        

            int x;
            int y;
            if (TreeToolbar.getSelectedTool().equals(TreeToolbar.MOVE_TREE)) {
                x = getX() + e.getX();
                y = getY() + e.getY();
            } else {
                x = nodeView.getX();
                y = nodeView.getY();
            }
            
            treePanel.mouseReleased(new MouseEvent((Component)e.getSource(),e.getID(),e.getWhen(),e.getModifiers(),x,y,1,false));            
        }
    }

    /** 
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     * If a non-selectable tool is active, pass the event to the tree
     * panel on which the text field resides.
     */
    public void mouseClicked(MouseEvent e) {
        if ( !textableToolSelected() && respondToMouse ) {
            TreePanel treePanel = TreePanel.getTreePanel();
            if (isEditable()) {
                treePanel.requestFocus();        
                treePanel.mouseClicked(new MouseEvent((Component)e.getSource(),e.getID(),e.getWhen(),e.getModifiers(),nodeView.getX(),nodeView.getY(),1,false)); 
            } else {
                treePanel.checkForSubTreeDownload(nodeView);
            }

        } 
    }
    
    /** 
     * If a non-selectable tool is active, pass the event to the tree
     * panel on which the text field resides.
     */
    public void mouseDragged(MouseEvent e) {
        if ( !textableToolSelected()  && respondToMouse ) {
            TreePanel treePanel = TreePanel.getTreePanel();
            //treePanel.requestFocus();
            
            int x;
            int y;
            if (TreeToolbar.getSelectedTool().equals(TreeToolbar.MOVE_TREE)) {
                x = getX() + e.getX();
                y = getY() + e.getY();
            } else {
                x = nodeView.getX();
                y = nodeView.getY();
            }

            
            treePanel.mouseDragged(new MouseEvent((Component)e.getSource(),e.getID(),e.getWhen(),e.getModifiers(),x,y,1,false));
        }
    }    

    /** Invoked when the mouse button has been moved on a component - passes the moved event along to the TreePanel, 
     * if the correct tools are chosen
     *
     */
    public void mouseMoved(MouseEvent e) {
        if ( !textableToolSelected()  && respondToMouse ) {
            TreePanel treePanel = TreePanel.getTreePanel();
            
            treePanel.mouseMoved(new MouseEvent((Component)e.getSource(),e.getID(),e.getWhen(),e.getModifiers(),nodeView.getX(),nodeView.getY(),1,false));
        }
    }
    
    /**
     * Invoked when the text field is clicked. 
     * <p>
     * If this is a field tied to  a node that is not checked out (and is thus 
     * uneditable), offers option of downloading the subtree rooted at that node
     * <p>
     * Otherwise, just give the field a border, and move it fully into view
     * if it isn't already
     */
    public void focusGained(FocusEvent e) { 
        if ( Controller.getController().getMenuCuttingPasting()) {
            // if the text has just been changed by a cut or paste, indicate that it's done
            Controller.getController().setMenuCuttingPasting(false);
        }
        
        if ( textableToolSelected() ) {
            if (!isEditable()) {
                TreePanel.getTreePanel().checkForSubTreeDownload(nodeView);
            } else {
            
                setBorder(lineBorder);
                setOpaque(true);
                JViewport viewPort = (JViewport) TreePanel.getTreePanel().getParent();
                Rectangle rect = viewPort.getViewRect();

                int yDiff=0 ;
                //not dealing with X, because the only "focusGained" events we expect to see are the result of tabs off screen
                if (rect.getY()+rect.getHeight() <  getY()) { //shift the scrollpane up
                    yDiff = (int)( getY()+ getHeight() + TreePanel.getTreePanel().getCircleDiameter() - (rect.getY() + rect.getHeight()) ) ;
                } else if (rect.getY() > getY()) { //shift the scrollpane up
                    yDiff = (int)( getY() - rect.getY()  ) ;
                }
                if (yDiff != 0) {
                    viewPort.setViewPosition(new Point(viewPort.getViewPosition().x, viewPort.getViewPosition().y + yDiff));                                            
                }
            }
        }
    }

    /**
     * Invoked when focus is lost.
     * <p>
     * Remove the border, store the new name of the node (if changed), using
     *the appropriate undoable edit.
     */
    
    public void focusLost(FocusEvent e) {
        setBorder(emptyBorder);
        setOpaque(false);
        Node node = nodeView.getNode();
        
        if ( node.hasPage() && getText().equals("") ) {
            String NAME_REQUIRED = Controller.getController().getMsgString("NAME_REQUIRED");
            JOptionPane.showMessageDialog(nodeView.getPanel().getTreeFrame(), NAME_REQUIRED, "Node with page must have name", JOptionPane.ERROR_MESSAGE);
            setText(node.getName());
            return;
        }
        
        if (!getText().equals(node.getName())) {
            TreePanel p = (TreePanel) nodeView.getPanel();
            
            p.updateUndoStuff(new NameNodeUndoableEdit(nodeView.getNode(), getText()));
            if (getTextOverrun() > 0) { 
                p.setActiveNode(nodeView.getNode());
                p.rebuildTree();
            }
 
        }
  
        //setSelectionEnd(getSelectionStart());
    }

    /** 
     * Invoked when the textrfield is clicked
     */
    public void actionPerformed(ActionEvent e) {
        setCaretPosition(0);
        TreePanel.getTreePanel().requestFocus();
    }

    /** Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     *
     */
    public void changedUpdate(DocumentEvent e) {
    }

    /** Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     *
     */
    public void insertUpdate(DocumentEvent e) {
        resizeTextFieldWhileTyping();
    }

    /** Gives notification that a portion of the document has been
     * removed.  The range is given in terms of what the view last
     * saw (that is, before updating sticky positions).
     *
     * @param e the document event
     *
     */
    public void removeUpdate(DocumentEvent e) {
        resizeTextFieldWhileTyping();
    }    
    
    /** required because it extends an abstract class. Not in use*/
    public void undoableEditHappened(UndoableEditEvent e) {
    }
}
