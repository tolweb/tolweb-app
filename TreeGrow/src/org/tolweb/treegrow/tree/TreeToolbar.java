package org.tolweb.treegrow.tree;


import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.tolweb.treegrow.main.Controller;

/**
 *This is the toolbar that sits on the left portion of the TreeFrame.
 *Handles layout, selection, OS-specific cursor image names, 
 *alternate buttons (when modifier keys are pressed) and enabling/disabling
 */
public class TreeToolbar extends JToolBar {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8669212577881642278L;
	private int xpos=0;
    private int ypos=0;
    private ArrayList buttons = new ArrayList(); 
    private static final int BUTTONWIDTH = 30;
    private static final int BUTTONHEIGHT = 30;
    private static final int SEPARATORHEIGHT = 12;
    private static final String UNDO_STRING = "Undo";
    private static final String REDO_STRING = "Redo";
    public static final String MOVE_BRANCH = "Move Branch";
    public static final String ADD_SUBGROUP = "Add Subgroup";
    public static final String ADD_SUBGROUPS = "Add Multiple Subgroups";
    public static final String DELETE_NODE = "Delete Node";
    public static final String DELETE_CLADE = "Delete Group";
    public static final String COLLAPSE_ALL = "Collapse All";
    public static final String COLLAPSE_BELOW = "Collapse Below";
    public static final String NAME_NODE = "Name Node";
    public static final String ADD_PAGE = "Add Page";
    public static final String REMOVE_PAGE = "Remove Page";    
    public static final String VIEW_PAGE = "Edit Online";
    public static final String OPEN_NODE = "Node Info";
    public static final String LEAF_NODE = "Leaf Node";
    public static final String MULTI_LEAF_NODE = "Mark Terminals As Leaves";
    public static final String PHYLESIS = "Phylesis";
    public static final String CONFIDENCE = "Confidence of Placement";
    public static final String EXTINCTION = "Extinct Node";
    public static final String FULL = "Full";
    public static final String ZOOM_IN = "Zoom In";
    public static final String ZOOM_OUT = "Zoom Out";
    public static final String MOVE_TREE = "Move Tree";
    public static final String COPY_PAGE = "Copy Page Content";
    public static final String MOVE_PAGE = "Move Page Content";
    public static final String FETCH_SUBGROUP = "Fetch Subgroup";
    public static final String SUBGROUPS_INCOMPLETE = "Subgroups Incomplete";
    public static final String PRIORITY = "Priority";
    
    
    private static final String ACTIVE_SUFFIX = ".act.gif";
    private static final String INACTIVE_SUFFIX = ".trsp.gif";
    private static final String WINDOWS_CURSOR_SUFFIX = ".cursor32.gif";    
    private static final String MAC_CURSOR_SUFFIX = ".cursor16.gif";    
    
    private static final String OS_MAC = "Mac";
    private static final String OS_WIN = "Win";
    private static final String OS_NIX = "X";
    private static ToolbarButton selectedTool, previousTool;
        
    private static Cursor openHandCursor, closedHandCursor;
    
    private Point hotPointMac, hotPointWin, hotPointOther;
    private Point hotPointMacAlt, hotPointWinAlt, hotPointOtherAlt; //only used if the hotpoints are different
    
    private ToolbarButton moveTreeButton, defaultButton;
    
    /** 
     * Creates a new instance of ToLToolbar. Note that layout is done via
     *absolute positioning, since none of the layout managers offered the
     *desired flexibility. Adding works like flow (just add it), but it 
     *intelligently adds the buttons in two-column rows, handling insertion 
     *of separators along the way.
     */
    public TreeToolbar() {
            this.setFloatable(false);
            this.setOrientation(javax.swing.SwingConstants.VERTICAL);
            setOpaque(false);

            setLayout(null);
            
            ToolbarButton button;
    
            defaultButton = new ToolbarButton(MOVE_BRANCH, "Images/treeedit", getCursor());
            addButton(defaultButton, false);
            
            hotPointMac   = new Point(0,13);
            hotPointWin   = new Point(6,23);
            hotPointOther = new Point(2,19);
            button = new ToolbarButton(NAME_NODE, "Images/label", buildCursor("Images/label"));
            addButton (button,true);

            hotPointMac   = new Point(0,8);
            hotPointWin   = new Point(6,15);
            hotPointOther = new Point(3,11);
            button = new ToolbarButton(ADD_SUBGROUP, "Images/addsubgroups", buildCursor("Images/addsubgroups"));
            addButton (button, false);
            
            button = new ToolbarButton(ADD_SUBGROUPS, "Images/addmultiplesubgroups",  buildCursor("Images/addmultiplesubgroups"));
            addButton (button, false);            
            
            hotPointMac   = new Point(0,8);
            hotPointWin   = new Point(6,16);
            hotPointOther = new Point(2,12);            
            button = new ToolbarButton(DELETE_NODE, "Images/collapse", buildCursor("Images/collapse"));
            addButton (button, false);

            hotPointMac   = new Point(1,2);
            hotPointWin   = new Point(9,10);
            hotPointOther = new Point(5,6);
            button = new ToolbarButton(DELETE_CLADE, "Images/clip", buildCursor("Images/clip"));
            addButton (button, false);
            
            hotPointMac   = new Point(4,15);
            hotPointWin   = new Point(12,22);
            hotPointOther = new Point(7,17);
            hotPointMacAlt = new Point(14,2);
            hotPointWinAlt = new Point(22,10);
            hotPointOtherAlt = new Point(18,7);
            button = new ToolbarButton(COLLAPSE_ALL, COLLAPSE_BELOW, "Images/collapseall", "Images/collapsebelow", buildCursor("Images/collapseall"), buildCursor("Images/collapsebelow", true));
            addButton (button, false);
            
            hotPointMac   = new Point(0,7);
            hotPointWin   = new Point(5,16);
            hotPointOther = new Point(1,13);
            button = new ToolbarButton(FETCH_SUBGROUP, "Images/expandsubgroup", buildCursor("Images/expandsubgroup"));
            addButton (button, false);            

            
            addSep();         

            hotPointMac   = new Point(0,13);
            hotPointWin   = new Point(6,23);
            hotPointOther = new Point(2,19);
            button = new ToolbarButton(ADD_PAGE, REMOVE_PAGE, "Images/addpage", "Images/removepage", buildCursor("Images/addpage"), buildCursor("Images/removepage"));
            addButton (button, false);
            
            hotPointMac   = new Point(0,13);
            hotPointWin   = new Point(6,23);
            hotPointOther = new Point(2,19);
            button = new ToolbarButton(VIEW_PAGE, "Images/editonline", buildCursor("Images/editonline"));
            addButton (button, false);            
            

            
            /*hotPointMac   = new Point(0,13);
            hotPointWin   = new Point(6,23);
            hotPointOther = new Point(2,19);
            button = new ToolbarButton(MOVE_PAGE, COPY_PAGE, "Images/MovePageContent", "Images/CopyPageContent", buildCursor("Images/MovePageContent"), buildCursor("Images/CopyPageContent") );
            addButton (button, false);*/        
            

            addSep();            
            
            hotPointMac   = new Point(0,15);
            hotPointWin   = new Point(6,24);
            hotPointOther = new Point(2,19);
            button = new ToolbarButton(LEAF_NODE, MULTI_LEAF_NODE, "Images/leaf", "Images/multileaf", buildCursor("Images/leaf"), buildCursor("Images/multileaf"));
            addButton(button,false);
            
                        
            hotPointMac   = new Point(0,5);
            hotPointWin   = new Point(6,13);
            hotPointOther = new Point(2,9);
            button = new ToolbarButton(PHYLESIS, "Images/nonmono", buildCursor("Images/nonmono"));
            addButton(button,true);		

            hotPointMac   = new Point(0,3);
            hotPointWin   = new Point(7,10);
            hotPointOther = new Point(2,19);
            button = new ToolbarButton(CONFIDENCE, "Images/incertaesedis", buildCursor("Images/incertaesedis"));
            addButton(button,true);
            
            hotPointMac   = new Point(1,5);
            hotPointWin   = new Point(9,13);
            hotPointOther = new Point(5,9);
            button = new ToolbarButton(EXTINCTION, "Images/extinct", buildCursor("Images/extinct"));
            addButton(button,true); 
            
            hotPointMac   = new Point(0,8);
            hotPointWin   = new Point(6,15);
            hotPointOther = new Point(3,11);
            button = new ToolbarButton(SUBGROUPS_INCOMPLETE, "Images/incomplete", buildCursor("Images/incomplete"));
            addButton(button,true);   
            
            /*hotPointMac   = new Point(1,5);
            hotPointWin   = new Point(9,13);
            hotPointOther = new Point(5,9);
            button = new ToolbarButton(PRIORITY, "Images/priority", buildCursor("Images/priority"));
            addButton(button,true);             
            
            */
            addSep();
            
            hotPointMac   = new Point(0,0);
            hotPointWin   = new Point(0,0);
            hotPointOther = new Point(0,0);            

            button = new ToolbarButton(ZOOM_OUT, "Images/zoomout", buildCursor("Images/zoomout"));
            addButton(button,true);
            button = new ToolbarButton(ZOOM_IN, "Images/zoomin", buildCursor("Images/zoomin"));
            addButton(button,true);
            
            hotPointMac   = new Point(8,8);
            hotPointWin   = new Point(16,16);
            hotPointOther = new Point(12,12);
            openHandCursor = buildCursor("Images/bmovetree");
            closedHandCursor = buildCursor("Images/grabtree");
            moveTreeButton = new ToolbarButton(MOVE_TREE, "Images/bmovetree", openHandCursor);
            addButton(moveTreeButton,true);
            
            addSep();
            
            hotPointMac   = new Point(0,0);
            hotPointWin   = new Point(0,0);
            hotPointOther = new Point(0,0);            
            
            button = new UndoToolbarButton(UNDO_STRING, "Images/undo", getCursor());
            addButton(button, false);
            button = new RedoToolbarButton(REDO_STRING, "Images/redo", getCursor());
            addButton(button, false);
            
            TreePanel treePanel = TreePanel.getTreePanel();
            treePanel.getUndoAction().updateUndoState();
            treePanel.getRedoAction().updateRedoState();
            
            setPreferredSize(new Dimension(62, getHeight()));    
            addKeyListener((TreeFrame)getParent());
    }
    
    /** 
     * Buttons are added via absolute positioning. This method sets those
     * positions, and manipulates/tracks the variables required to get the
     *right position
     *
     */
    private void addButton (ToolbarButton btn, boolean enabled) {
        buttons.add(btn);
        Insets insets = getInsets();
        btn.setBounds(  xpos*BUTTONWIDTH + insets.left, ypos + insets.top, 26, 26);
        //btn.setText(desc);
        add(btn);
        Rectangle r = btn.getBounds();

        if (xpos==1) {
            ypos += BUTTONHEIGHT;
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        xpos = 1 - xpos;   //flip-flop between 0 and 1
        
        hotPointMacAlt = hotPointWinAlt = hotPointOtherAlt = null;        
    }
    
    private void addRightSep() {
        xpos = 0;
        ypos += BUTTONHEIGHT;
    }

    /** 
     *Adds vertical separator between sets of buttons.
     */
    private void addSep (){
            if (xpos==1)
            {
                xpos =0;
                ypos += BUTTONHEIGHT;
            }        
            
            ypos += SEPARATORHEIGHT;
    }
        
    
    /**
     *Build cursor from an image name 
     *
     *@param imgName Used as a base for the final name of the cursor image, 
     * which is the combination of imgName and OS-specific suffix. Different 
     * images for Different OSs because each OS's jvm presents a different 
     * sized cursor
     *
     *@param hotPoint hotpoint for the cursor being created
     */
    public static Cursor buildCursor(String imgName, Point hotPoint) {
        if (Controller.getController().isMac()) {
            imgName += MAC_CURSOR_SUFFIX;
        } else if (Controller.getController().isWindows()) {
            imgName += WINDOWS_CURSOR_SUFFIX;
        } else {
            imgName += INACTIVE_SUFFIX;
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.createCustomCursor(toolkit.createImage(ClassLoader.getSystemResource(imgName)), hotPoint, "");
    }
    
    /**
     *Build cursor from an image name. 
     *
     *@param imgName Used as a base for the final name of the cursor image, 
     * which is the combination of imgName and OS-specific suffix. Different 
     * images for Different OSs because each OS's jvm presents a different 
     * sized cursor
     */
    private Cursor buildCursor(String imgName) {
        return buildCursor(imgName, false);
    }
    
    /**
     *Build cursor from an image name. 
     *
     *@param imgName Used as a base for the final name of the cursor image, 
     * which is the combination of imgName and OS-specific suffix. Different 
     * images for Different OSs because each OS's jvm presents a different 
     * sized cursor
     *
     *@param isAlt Determines if the image is the primary or alternate image. 
     *        Just does this because we've set up regular and alt hotpoints
     *        prior to each run of "addButton"...for readability. Default is 
     *          false.
     */    
    private Cursor buildCursor(String imgName, boolean isAlt) {
        Point hotPoint;
        if (Controller.getController().isMac()) {
            if (isAlt) {
                hotPoint = hotPointMacAlt;
            } else {
                hotPoint = hotPointMac;
            }
        } else if (Controller.getController().isWindows()) {
            if (isAlt) {
                hotPoint = hotPointWinAlt;   
            } else {
                hotPoint = hotPointWin ;                
            }            
        } else {
            if (isAlt) {
                hotPoint = hotPointOtherAlt;   
            } else {
                hotPoint = hotPointOther;                
            }
        }        
        return buildCursor(imgName, hotPoint);
    }
    
    /**
     * Moving over the tree with the "move" tool active
     */
    public static Cursor getOpenHandCursor() {
        return openHandCursor;
    }

    /**
     * Clicked on the "move" tool, and moving (i.e. dragging)
     */    
    public static Cursor getClosedHandCursor() {
        return closedHandCursor;
    }
    
    /** 
     * Total hieght of the toolbar, after all buttons have been added
     */
    public int getHeight () {
        return ypos+5;
    }
    
    
    /** 
     * Sets the selected tool, updates the cursor, and keeps track of the tool
     *that was selected prior the newly selected one
     */
    public void setSelectedTool(ToolbarButton button) {
               
        previousTool = selectedTool;      
        
        if (selectedTool != null) {
            selectedTool.setSelected(false);
        }
        selectedTool = button;
        selectedTool.setSelected(true);

        if ( TreePanel.getTreePanel() != null && Controller.getController().getPreferenceManager().getUseCustCursors() ) {
            TreePanel.getTreePanel().setCursor(button.getButtonCursor());
        } else if (TreePanel.getTreePanel() != null) {
            TreePanel.getTreePanel().setCursor(Cursor.getDefaultCursor());
        }
        TreePanel.getTreePanel().requestFocus();
        
    }

    /**
     * Revert selection to the tool that had been selected prior to the 
     *current one. Generally used to go back to the previously selected
     *tool after disabling/re-enabling the entire toolbar
     */
    public void returnToPreviousTool() {
        if (previousTool != null) {
            setSelectedTool(previousTool);
        } else {
            setDefaultSelectedTool();
        }
        previousTool = null;        
    }
    
    public void setDefaultSelectedTool() {
        setSelectedTool(defaultButton);
    }
    
    public void setHandSelectedTool() {
        setSelectedTool(moveTreeButton);
    }
    
    public static String getSelectedTool() {
        if (selectedTool==null){
            return null;
        }
        return (String) selectedTool.getAction().getValue(javax.swing.Action.NAME)  ;
    }
  
    public static Cursor getSelectedCursor() {
        return selectedTool.getButtonCursor();
    }

    public void enableAllButtons() {
        Iterator it = buttons.iterator();
        while (it.hasNext()) {
            JButton btn = (JButton) it.next();
            if (!btn.getToolTipText().equals(UNDO_STRING) && !btn.getToolTipText().equals(REDO_STRING)) {
                btn.setEnabled(true);
            } else {
                if (btn.getToolTipText().equals(UNDO_STRING)) {
                    btn.setEnabled(Controller.getController().getTreeEditor().getUndoManager().canUndo());
                } else {
                    btn.setEnabled(Controller.getController().getTreeEditor().getUndoManager().canRedo());
                }
            }
        }
    }
    
    public void disableAllButtons() {
        Iterator it = buttons.iterator();
        while (it.hasNext()) {
            JButton btn = (JButton)it.next();
            btn.setEnabled(false);
        }    
    }
    
    /** 
     *In response to pressing the modifier key (e.g. "alt"), the buttons that
     * have "alt" images (and their correspoing cursor, if selected) will
     * flip so that the alt image is shown/hidden
     *
     *@param value  If true, show alt buttons/cursors; otherwise, show standard 
     */
    public void setAlternateButtons(boolean value) {
        Iterator it = buttons.iterator();
        while ( it.hasNext() ) {
            ((ToolbarButton)it.next()).setIsAlt(value);
        }

        if (TreePanel.getTreePanel() != null) {
            TreePanel.getTreePanel().setCursor(selectedTool.getButtonCursor());
        }
        repaint();
    }
    
}

