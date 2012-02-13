package org.tolweb.treegrow.tree;
/*
 * TreePanel.java
 *
 * Created on June 2, 2003, 1:59 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;
import java.util.*;

import org.tolweb.treegrow.main.*;
import org.tolweb.treegrow.main.undo.*;
import org.tolweb.treegrow.page.undo.RemovePageUndoableEdit;
import org.tolweb.treegrow.tree.undo.*;

/**Subclass
 * Subclass of the AbstractTreePanel. It's the main graphical Tree manipulator 
 * Panel, draws the Tree to be edited.  Contains a bunch of lines and circles 
 * and textFields, then monitors user interaction with the panel.
 */
public class TreePanel extends AbstractTreePanel implements MouseListener, MouseMotionListener { 
    /**
	 * 
	 */
	private static final long serialVersionUID = 4531920089575388974L;

	/**
     * The node that was last clicked on or edited in another way.  Used for
     * resetting the viewPort
     */
    private NodeView activeView;

    /**
     * The node that was last clicked on or edited in another way.  Since 
     * activeView gets reset by mouse movement, must have some way to hold
     * onto the node when message boxes need to pop up
     */    
    private NodeView tempActiveView;
    
    /**
     * The hash of circles that dont have full data associated with them.  This 
     * will only be used during a download.  These circles will be painted gray.
     * As nodes get fully downloaded, they will be removed from the hash.
     */
    private Hashtable nodeToGrayCircles;
         
    /**
     * Used to store arrays of the circles (nodes) for which we need to monitor
     * mouse events
     */
    private ArrayList listenedCircles = new ArrayList();
    /**
     * Used to store arrays of the horizontal rectangles (branches) for which
     * we need to monitor mouse events
     */
    private ArrayList listenedHorizRects = new ArrayList();
    /**
     * Used to store arrays of the vertical rectangles (branches) for which we
     * need to monitor mouse events
     */
    private ArrayList listenedVertRects = new ArrayList();  
    /**
     * Used to briefly store a list of the nodes that are about to be deleted.
     * After completing the delete, this list should be emptied
     */
    private ArrayList deletedNodes = new ArrayList();
    /**
     * Used to indicate that certain nodes (terminals w/ subtrees) cannot be
     * edited
     */
    private ArrayList uneditableCircleValues = new ArrayList();
    /**
     * The rectangle to be drawn if a dragged-line is over a dropLocation
     */
    private Rectangle highlightedRect;
    /**
     * The destination point of a mouseDragged event (if we are originated on a
     * clickable Node)
     */
    private Point destinationPoint;
    
    /**
     * The point for mouse dragging on a Tree Moved action.  Different from the
     * previous since we don't care if the moving happens on shapes or not
     */
    private Point treeMovedDestinationPoint;
    /**
     * Used to keep track of which Node's circle needs to be highlighted because
     * the mouse has moved over it
     */
    private TreeCircle          highlightedCircle = null;
    /**
     * Used to keep track of when a circle is dragged over and it is a valid
     * drop destination
     */
    private TreeCircle          highlightedDestinationCircle=null;    
    /**
     * Used to keep track of when a horizontal rect is dragged over and it is a
     * valid drop destination
     */    
    private TreeHorizontalRect  highlightedHorizRect=null;
    /**
     * Used to keep track of when a vertical rect is dragged over and it is a
     * valid drop destination
     */    
    private TreeVerticalRect    highlightedVertRect=null;    
    /**
     * Used to keep track of the last shape that was highlighted
     */    
    private AbstractTreeShape    highlightedShape=null;     
    
    /**
     * Used to keep track of the last shape that was highlighted.  Since 
     * highlightedShape gets reset by mouse movement, must have some way to hold
     * onto the node when message boxes need to pop up
     */    
    private AbstractTreeShape    tempHighlightedShape=null;     
      
    /**
     * Used in the case of a node action that results in the need to collapse
     * either the parent or sibling of a node. Set by the function okToContinue,
     * and should be set to null once it's tested.
     */
    private Node nodeToCollapse;
    
    private UndoAction undo;
    private RedoAction redo;
    
    /** 
     * The singular instance of this panel
     */
    private static TreePanel treePanel;
    
    private static Color highlightedLineBlue = new Color(0,0,222);

    /** 
     * Menu that pops up when phylesis tool is active and node is clicked
     */    
    private PhylesisPopupMenu phylesisPopupMenu;

    /** 
     * Menu that pops up when confidence tool is active and node is clicked
     */    
    private ConfidencePopupMenu confidencePopupMenu;
    
    private PriorityPopupMenu priorityPopupMenu;

    private Cursor openHand, closedHand ;
       
    public static TreePanel getTreePanel () {
        return treePanel;
    }           
    
    /**
     * Used when TreeFrame gets disposed
     */
    public static void nullifyTreePanel() {
        treePanel = null;
    }
    
    /** 
     * Creates a new instance of TreePanel 
     *
     * @param tr The tree to draw on this panel
     * @param frame The TreeFrame containing this panel
     * @param isDownload Indicates whether this panel is constructed during a download or whether it's being read from a file
     */
    public TreePanel(Tree tr, TreeFrame frame, boolean isDownload) {
        super(tr, frame);
        //FloatingTextField.resetFontMetrics(this);
        treePanel=this;
        setBackground(Color.white);

        addMouseListener(this);
        addMouseMotionListener(this);

        phylesisPopupMenu = new PhylesisPopupMenu();
        confidencePopupMenu = new ConfidencePopupMenu();
        priorityPopupMenu = new PriorityPopupMenu();

        Point hotPoint;
        if (Controller.getController().isMac()) {
            hotPoint = new Point(8,8);
        } else if (Controller.getController().isWindows()) {
            hotPoint = new Point(16,16);
        } else {
            hotPoint = new Point(12,12); 
        }        
        closedHand = TreeToolbar.buildCursor("Images/grabtree", hotPoint);
        //closedHand = toolkit.createCustomCursor(toolkit.createImage(ClassLoader.getSystemResource(TreeToolbar.addImgSuffix("Images/grabtree"))), hotPoint, "");
        // Set this equal to a new hashtable so it doesn't bother adding things to it
        if (!isDownload) {
            nodeToGrayCircles = new Hashtable();
        }
        addKeyListener(getTreeFrame());
        //initializeMaps();
    }
    
    public UndoManager getUndoManager() {
        return getTreeFrame().getUndoManager();
    }
    
    public void setUndoAction(UndoAction u) {
        undo = u;
    }
    
    public void setRedoAction(RedoAction r) {
        redo = r;
    }

    public UndoAction getUndoAction() {
        return undo;
    }
    
    public RedoAction getRedoAction() {
        return redo;
    }
     
    public int getCircleDiameter() {
        return circleDiameter;
    }

    public int getHalfCircleDiameter() {
        return circleDiameter / 2;
    }
    
    public void setCircleDiameter(int value) {
        circleDiameter = value;
    }

    /**
     * Make tree and text bigger. Only allow zoom up to a certain limit 
     * (2 steps in)
     */
    public void zoomIn (MouseEvent e) {
        if (zoomFactor >= -1) {
            zoomFactor--;
        }  
        zoomUpdate(e);
    }

    /**
     * Make tree and text smaller. Only allow zoom down to a certain limit 
     * (3 steps out)
     */    
    public void zoomOut (MouseEvent e) {
        if (zoomFactor <= 3) {
            zoomFactor++;
        }      
        zoomUpdate(e);
    }
    
    protected void zoomUpdate(MouseEvent e) {
        super.zoomUpdate(null);
        rebuildTree();
        JViewport viewPort = (JViewport)getParent();        
        float logicalXDiff = e.getX() - viewPort.getViewPosition().x;
        float logicalYDiff = e.getY() - viewPort.getViewPosition().y;
        
        float widthPct = (float)e.getX()/getWidth();
        float heightPct = (float)e.getY()/getHeight();        
        int newX = (int)(getWidth() * widthPct - logicalXDiff);
        int newY = (int)(getHeight() * heightPct - logicalYDiff);
        
        viewPort.setViewPosition(new Point(newX,newY));        
    }
    
    
    /**
     * When the node information has been completely downloaded, this is
     *called to make the panel completely redraw the node (color, indicators,
     * etc.)
     */
    public void setNodeComplete(Node node) {
        nodeToGrayCircles.remove(node);
              
        NodeView view = (NodeView) nodeToNodeView.get(node);
        if (view == null) {
            return;
        }
        if (node.hasPage()) {
            addPageIndicator(view);
        }
        
        if (node.getTermWithSubTree() || !node.getCheckedOut()) {
            view.getTextField().setEditable(false);
            if (node.getTermWithSubTree()) {
                addSubtreeIndicator(view);
            }
        }

        if (!node.getCheckedOut() && !node.getLocked()) {
            int[] location = new int[] {view.getX(), view.getY()};
            uneditableCircleValues.add(location);
        }
        
        repaint(view.getX(), view.getY(), getCircleDiameter() + 5, getCircleDiameter());      
    }
    
    /**
     * Returns an arrayList of sorted nodes according to their y-position on the screen.
     *
     * @return An arrayList of sorted nodes according to their y-position on the screen.
     */
    public ArrayList getSortedNodes() {
        Collections.sort(nodeViews);
        Iterator it = nodeViews.iterator();
        ArrayList returnList = new ArrayList();
        while (it.hasNext()) {
            returnList.add(((NodeView) it.next()).getNode());
        }
        return returnList;
    }
       
    
    
    /** 
     * Checks to see if the location of the mouse click is found within the 
     *boundaries of any of the active circles or rectangles. If so, identify
     *the nodeview (and corresponding node) that is associated with that shape.
     *Based on the selected tool, performs some action, generally by checking 
     *that it's an ok action to perform, then initiating an undoable edit
     */
    public void mouseClicked(MouseEvent e) {   
        
        highlightedCircle = (TreeCircle)containmentBasedRepaint (highlightedCircle, listenedCircles,  e );
        String actionName = getTool(e);

        
        
        if (actionName.equals(TreeToolbar.ZOOM_IN)) {  
            zoomIn(e);
            return;
        } else if (actionName.equals(TreeToolbar.ZOOM_OUT)) {  
            zoomOut(e);        
            return;
        } 
        
        Node activeNode = null;
        if (highlightedCircle != null  &&  activeView != null) {
            activeNode = activeView.getNode();
        } else {
            return;
        } 
        
        if (activeNode.getLocked()) {
            Vector vec = new Vector();
            vec.add(activeNode.getName());
            vec.add(activeNode.getLockUser());
            vec.add(activeNode.getLockDate());
            
            String lockText = "";
            if ( activeNode.getLockType().equals(XMLConstants.SUBMITTED) ) {
                lockText = Controller.getController().getMsgString("NODE_IS_SUBMITTED", vec);
            } else {
                lockText = Controller.getController().getMsgString("NODE_IS_LOCKED", vec);
            }            

            JOptionPane.showMessageDialog(this,lockText,activeNode.getName() + "is unavailable",JOptionPane.INFORMATION_MESSAGE);

        } else if ( !(!activeNode.getCheckedOut() && !isAllowedTerminalAction(actionName)) ) {
            
            if (!activeNode.getCheckedOut() && shouldAskAboutSubtreeDownload(actionName)) {
                checkForSubTreeDownload(activeView, actionName);
                return;
            }              
            
            activeView = highlightedCircle.getNodeView();
            if (actionName.equals(TreeToolbar.EXTINCTION)) {
                int newVal = activeNode.getExtinct() == Node.EXTINCT ? Node.NOT_EXTINCT : Node.EXTINCT;
                System.out.println("new val is: " + newVal);
                if (newVal == Node.NOT_EXTINCT && activeNode.getParent() != null && activeNode.getParent().getExtinct() == Node.EXTINCT) {
                    String EXTINCT_PARENT = Controller.getController().getMsgString("EXTINCT_PARENT");
                    JOptionPane.showMessageDialog(this, EXTINCT_PARENT, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    ExtinctUndoableEdit edit = new ExtinctUndoableEdit(activeNode, newVal);
                    updateUndoStuff(edit);                    
                }
            } else if (actionName.equals(TreeToolbar.PHYLESIS)) {
                phylesisPopupMenu.setNode(activeNode);
                phylesisPopupMenu.show(this, activeView.getX() + getCircleDiameter(),  activeView.getY());
            } else if (actionName.equals(TreeToolbar.CONFIDENCE)) {
                confidencePopupMenu.setView(activeView);
                confidencePopupMenu.setNode(activeNode);
                confidencePopupMenu.show(this, activeView.getX() + getCircleDiameter(),  activeView.getY());
            } else if (actionName.equals(TreeToolbar.PRIORITY)) {
                priorityPopupMenu.setNode(activeNode);
                priorityPopupMenu.show(this, activeView.getX() + getCircleDiameter(),  activeView.getY());
            } else if (actionName.equals(TreeToolbar.MOVE_BRANCH)) {
                RotateUndoableEdit edit = new RotateUndoableEdit(activeNode);
                updateUndoStuff(edit);
            } else if (actionName.equals(TreeToolbar.SUBGROUPS_INCOMPLETE)) {
                IncompleteSubgroupsUndoableEdit edit = new IncompleteSubgroupsUndoableEdit(activeNode);
                updateUndoStuff(edit);
            } else if (actionName.equals(TreeToolbar.LEAF_NODE)) {
                // For compatibility purposes, send an int value to the undoable edits
                int newVal = activeNode.getIsLeaf() ? Node.NOT_LEAF : Node.LEAF;
                LeafUndoableEdit edit = new LeafUndoableEdit(activeNode,  newVal);
                updateUndoStuff(edit);
            } else if (actionName.equals(TreeToolbar.MULTI_LEAF_NODE)) {
                QuickLeafUndoableEdit edit = new QuickLeafUndoableEdit(activeNode);
                updateUndoStuff(edit);
            } else if (actionName.equals(TreeToolbar.DELETE_NODE)) {
                if ( activeNode.hasPage() ) {
                    highlightedCircle = null;                
                    repaint();
                    alertHasPage();
                } else {
                    tempActiveView = activeView;
                    if (okToHandleNode()) {
                        Thread toDo = new Thread() {
                            public void run() {
                                highlightedCircle=null;
                                NodeView collapseView = null;
                                int firstShowLength = 1500;
                                if (nodeToCollapse != null) {
                                    collapseView = (NodeView)nodeToNodeView.get(nodeToCollapse);
                                    firstShowLength = 500;
                                }
                                try {
                                    boolean shown = showNodeDelete(tempActiveView,firstShowLength, true);
                                    if (collapseView != null) {
                                        showNodeDelete(collapseView, 1500, !shown);
                                    }
                                    DeleteNodeUndoableEdit edit = new DeleteNodeUndoableEdit(tempActiveView, nodeToCollapse);
                                    finishNodeHandle(edit);
                                } catch (Exception e) {
                                    // user cancelled delete
                                }
                                tempActiveView = null;
                            }
                        };
                        toDo.start();
                    }
                    
                    return;
                }


            } else if (actionName.equals(TreeToolbar.DELETE_CLADE)) {
                Node descendantWithPage;
                if ( activeNode.hasPage() ) {
                    highlightedCircle = null;
                    repaint();                    
                    alertHasPage();
                } else if (  (descendantWithPage = activeNode.firstDescendantWithPage()) != null ){
                    highlightedCircle = null;
                    repaint();
                    alertDescendantsHavePage( descendantWithPage.getName() );  
                } else {
                    tempActiveView = activeView;
                    if (okToHandleNode()) {
                        Thread toDo = new Thread() {
                            public void run() {
                                highlightedCircle=null;
                                NodeView collapseView = null;
                                int firstShowLength = 1500;
                                if (nodeToCollapse != null) {
                                    collapseView = (NodeView)nodeToNodeView.get(nodeToCollapse);
                                    firstShowLength = 500;
                                }
                                // build a list of all nodes to see if we need to
                                // show the warning message
                                ArrayList nodeViews = new ArrayList();
                                Stack viewsStack = new Stack();
                                viewsStack.push(tempActiveView);
                                while (!viewsStack.isEmpty()) {
                                    NodeView currentView = (NodeView) viewsStack.pop();
                                    nodeViews.add(currentView);
                                    for (Iterator iter = currentView.getChildren().iterator(); iter
                                            .hasNext();) {
                                        NodeView currentChildView = (NodeView) iter.next();
                                        viewsStack.push(currentChildView);
                                    }
                                }
                                try {
                                    boolean warningShown = showDeleteWarningMessage(nodeViews, true);
                                    warningShown = showNodeDelete(tempActiveView,firstShowLength, warningShown);
                                    if (collapseView != null) {
                                        showNodeDelete(collapseView,1500, !warningShown);
                                    }                                
                                    DeleteCladeUndoableEdit edit = new DeleteCladeUndoableEdit(tempActiveView.getNode(), nodeToCollapse);
                                    finishNodeHandle(edit);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        };
                        toDo.start();
                    }
                    return;
                } 
                
            } else if (actionName.equals(TreeToolbar.NAME_NODE)) {
                activeView.getTextField().requestFocus();
            } else if (actionName.equals(TreeToolbar.ADD_SUBGROUP) || actionName.equals(TreeToolbar.ADD_SUBGROUPS)) {
                tempActiveView = activeView;
                if (!activeNode.getCheckedOut()) {

                    String SUBTREE_DOWNLOAD_REQUIRED = Controller.getController().getMsgString("SUBTREE_DOWNLOAD_REQUIRED");
                    int result = JOptionPane.showConfirmDialog(treeFrame, SUBTREE_DOWNLOAD_REQUIRED,
                                        "Fetch subtree?",
                                        JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);                    
                    if ( result == 1) { //no
                        return;
                    }
                    Thread toDo = new Thread() {
                        public void run() {
                            Node node = tempActiveView.getNode();
                            if (!node.getCheckedOut()) {
                                fetchSubTree(tempActiveView);
                            }                        
                        }
                    };
                    toDo.start();                                                    
                } else {
                    if ( TreeToolbar.getSelectedTool() == TreeToolbar.ADD_SUBGROUPS ) {
                        Controller.getController().getTreeEditor().getToolbar().setAlternateButtons(false);
                        NewNodesFrame nf = new NewNodesFrame(activeNode, treeFrame);
                    } else {
                        //just add one node, unnamed
                        ArrayList oneNameList = new ArrayList();
                        Node newNode = new Node();
                        newNode.setName("");
                        oneNameList.add(newNode);
                        AddTaxaUndoableEdit edit = new AddTaxaUndoableEdit(activeNode, oneNameList);
                        updateUndoStuff(edit);
                    }                            
                }                   
            } else if (actionName.equals(TreeToolbar.ADD_PAGE)) {
                if (okToAddPageToNode(activeNode)) {
                    AddPageUndoableEdit edit = new AddPageUndoableEdit(activeNode);
                    updateUndoStuff(edit);
                }
            } else if (actionName.equals(TreeToolbar.REMOVE_PAGE)) {
                boolean doDelete = true;
                if (activeNode.hasPage() && activeNode.getId() > 0) {
                    if (Controller.getController().getWarnAboutPageDeletion()) {
                        DeletePageDialog deleteDialog = new DeletePageDialog(getTreeFrame());
                        deleteDialog.setVisible(true);
                        doDelete = deleteDialog.getContinueWithDelete(); 
                    }
                }
                if (doDelete) {
                    RemovePageUndoableEdit edit = new RemovePageUndoableEdit(activeNode);
                    updateUndoStuff(edit);
                }
            } else if (actionName.equals(TreeToolbar.VIEW_PAGE)) {
                if (StringUtils.notEmpty(activeNode.getName())) {
                    String viewUrl = HttpRequestMaker.getWorkingPreviewUrlForNode(activeNode);
                    if (!activeNode.hasPage()) {
                        // add a special parameter that tells the subgroup menu to open
                        viewUrl += "&" + RequestParameters.OPEN_SUBGROUPS + "=true";
                    }
                    OpenBrowser ob = new OpenBrowser(viewUrl);
                    ob.start();
                }
            } else if (actionName.equals(TreeToolbar.COLLAPSE_ALL) )  {
                if ( activeNode.getChildren().size() > 0) {
                    CollapseSubTreeUndoableEdit edit = new CollapseSubTreeUndoableEdit(activeNode);
                    ShowCollapseNodesThread toDo = new ShowCollapseNodesThread(edit);
                    toDo.start();
                } else {
                    highlightedCircle = null;
                    repaint();
                }
            } else if (actionName.equals(TreeToolbar.COLLAPSE_BELOW)) {
                if ( activeNode != tree.getRoot() ) {
                    CollapseRestOfTreeUndoableEdit edit = new CollapseRestOfTreeUndoableEdit(activeNode);
                    ShowCollapseNodesThread toDo = new ShowCollapseNodesThread(edit);
                    toDo.start();                    
                }           
            } else if (actionName.equals(TreeToolbar.FETCH_SUBGROUP)) {
                //if ( activeNode.getTermWithSubTree() ) {
                    fetchSubTree(activeView);
                //}
            }
        }
    }
  
    
    private boolean shouldAskAboutSubtreeDownload(String actionName) {
        return !actionName.equals(TreeToolbar.COLLAPSE_BELOW) && !actionName.equals(TreeToolbar.FETCH_SUBGROUP);
    }
    
    public void checkForSubTreeDownload(final NodeView view) {
        checkForSubTreeDownload(view, "");
    }

    /**
     *User clicked on a node that isn't currently checked out (must be a 
     *terminal node, with a page).  Ask if they want to check it out
     */
    public void checkForSubTreeDownload (final NodeView view, final String actionName) {
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String SUBTREE_DOWNLOAD_REQUIRED = Controller.getController().getMsgString("SUBTREE_DOWNLOAD_REQUIRED");

        int result = JOptionPane.showConfirmDialog(treeFrame, SUBTREE_DOWNLOAD_REQUIRED,
                            "Fetch subtree?",
                            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);                    
        if ( result == 1) { //no
            requestFocus();
            return ;
        }
        Thread toDo = new Thread() {
            public void run() {
                fetchSubTree(view, actionName);
            }
        };
        toDo.start();  
    }

    private void fetchSubTree(final NodeView view) {
        fetchSubTree(view, "");
    }
    
    /**
     * User wants to download a subtree. Fetches a one-deep tree rooted
     *at the node corresponding to the nodeview argument 
     */
    private void fetchSubTree(final NodeView view, final String actionName) {
        Thread toDo = new Thread() {
            public void run() {        
                DataDownloader down = new DataDownloader(view.getNode().getId(), Controller.getController().getEditorBatchId(), false);
                boolean downloaded = down.startSubtreeDownload();
                if (!downloaded) {
                    System.out.println("error - unable to download");
                    return;
                }
                if (!view.isTerminal()) {
                    view.getTextField().setEnabled(true);
                }
                int nodeId = view.getNode().getId();
                rebuildTree();
            }
        };
        toDo.start();                   
    }
    
    
    /**
     * When moving a page from one node to another, make sure that there aren't
     *any links to accessory pages on the moving page, such that the links will 
     *now be on pages that are ancestral to the new location of the moved page
     /
    private boolean cleanLinkedAccPages(Node fromNode, Node toNode) {    
        
        Set nodesWithLinkedAccPages = tree.getAllLinkedAccPagesNodes(fromNode.getPageObject()); 
        ArrayList leapfroggedNodes = new ArrayList();
        
        Iterator it = nodesWithLinkedAccPages.iterator();
        while (it.hasNext()) {
            Node linkedNode = (Node)it.next();
            if ( toNode.isDescendantOf(linkedNode) ) {
                leapfroggedNodes.add(linkedNode);
            }
        }
        
        boolean result = true;
        if (leapfroggedNodes.size()>0) {
            result = fromNode.getPageObject().removeAllAccessoryPagesLinkedToMe();
        }
        
        return result;
    }*/
    
    /**
     * Checks the case where the fromNode is currently a descendent of toNode
     * and fromNode links to an accessory page that will no longer be an 
     * ancestor, so it must be deleted
     *
     * @param fromNode The Node that contains the page that is moving
     * @param toNode The Node that the page will move to
     * @return Whether the user decided to go through with the page moving
     /
    private boolean cleanLinkedAncestorAccPages(Node fromNode, Node toNode) {
        Page page = fromNode.getPageObject();
        Iterator it = new ArrayList(page.getAccessoryPages()).iterator();
        boolean prompt = true;
        Controller controller = Controller.getController();
        while (it.hasNext()) {
            AccessoryPage accPage = (AccessoryPage) it.next();
            // It is a linked page
            if (!accPage.useContent()) {
                if (accPage.getLinkedAccPage() != null && accPage.getLinkedAccPage().getPage().getNode().isDescendantOf(toNode)) {                   
                    PageFrame frame = controller.getPageEditor(fromNode);
                    Vector pageTitle = new Vector();
                    pageTitle.add(accPage.getMenu());
                    if (prompt) {
                        String MOVE_PAGE_DELETE_ACCPAGE = controller.getMsgString("MOVE_PAGE_DELETE_ACCPAGE", pageTitle);
                        int response = JOptionPane.showConfirmDialog(this, MOVE_PAGE_DELETE_ACCPAGE, "Continue Move Page?", JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.NO_OPTION) {
                            return false;
                        } else {
                            prompt = false;
                        }
                    }
                    if (frame != null) {
                        frame.removeLinkedAccPage(accPage.getLinkedAccPage());
                        frame.refreshAccessoryPagesPanelTable();
                    } else {
                        Vector accessories = page.getAccessoryPages();
                        accessories.remove(accPage);
                        page.setAccessoriesChanged(true);
                    }
                }
            }
        }
        /*System.out.println("acc pages = ");
        Iterator it2 = page.getAccessoryPages().iterator();
        while (it2.hasNext()) {
            AccessoryPage apage = (AccessoryPage) it2.next();
            System.out.println("acc page = " + apage.getMenu());
        }/
        return true;
    }*/
    
    /**
     * When adding a page to a node, need to ensure that the node has a name.
     *This function asks for a name if not already present. If none provided,
     *the page addition is cancelled
     */
    private boolean okToAddPageToNode(Node node) {
        String name = node.getName();
        
        if (name == null || name.equals("")) {
            String NAME_REQUIRED = Controller.getController().getMsgString("NAME_REQUIRED");
            name = JOptionPane.showInputDialog(treeFrame, 
                                NAME_REQUIRED, 
                                "Name required",JOptionPane.QUESTION_MESSAGE);
            if (name == null || name.equals("")) {
                return false;
            }
            NameNodeUndoableEdit edit = new NameNodeUndoableEdit(node, name);
            updateUndoStuff(edit);
        }
        
        return true;
    }
    
    /** 
     *Generally returns the name of the selected tool in the toolbar. But if the
     *meta(or shift, for windows/linux) key is held down, returns the
     *default arrow
     */
    private String getTool(MouseEvent e) {
        boolean isMac = Controller.getController().isMac();
        boolean metaIsDown = (isMac && e.isMetaDown()) || (!isMac && e.isShiftDown()) ;
        
        return metaIsDown ? TreeToolbar.MOVE_BRANCH : TreeToolbar.getSelectedTool() ;
    }    
    
    /**
     * Draws a red "x" through nodes that are about to be deleted, then
     * sleeps for a little while so the visual effect is fully realized
     * @param showWarningMessage TODO
     */
    private boolean showNodeDelete(NodeView view, int time, boolean showWarningMessage) throws Exception {
        ArrayList list = new ArrayList();
        list.add(view);
        return showNodeDelete(list, time, true);
    }
    
    private boolean showNodeDelete(ArrayList views, int time, boolean showWarningMessage) throws Exception {
        boolean showedWarningMessage = showDeleteWarningMessage(views, showWarningMessage);
        deletedNodes.addAll(views);
        repaint();
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }    
        return showedWarningMessage;
    }
    
    private boolean showDeleteWarningMessage(ArrayList views, boolean showWarningMessage) throws Exception {
        boolean showedWarningMessage = false;        
        boolean hasServerNodes = false;        
        for (Iterator iter = views.iterator(); iter.hasNext();) {
            NodeView nextView = (NodeView) iter.next();
            if (nextView.getNode().getId() > 0 && StringUtils.notEmpty(nextView.getNode().getName())) {
                hasServerNodes = true;
            }
        }
        if (showWarningMessage && 
                Controller.getController().getWarnAboutNodeDeletion() && 
                hasServerNodes) {
            DeleteNodeDialog deleteDialog = new DeleteNodeDialog(getTreeFrame());
            deleteDialog.setVisible(true);
            if (!deleteDialog.getContinueWithDelete()) {
                throw new Exception("User cancelled delete operation!");
            }
            showedWarningMessage = true;
        }        
        return showedWarningMessage;
    }
    
    /**
     *Clean up after handling/moving a node
     */
    private void finishNodeHandle(AbstractUndoableEdit edit) {
        updateUndoStuff(edit);
        deletedNodes.clear();
        repaint();    
    }
    
    /**
     *get undo menus up to date
     */
    public void updateUndoStuff(AbstractUndoableEdit edit) {
        System.out.println("just added edit: " + edit + " to undo manager: " + getUndoManager());
        getUndoManager().addEdit(edit);
        getUndoAction().updateUndoState();
        getRedoAction().updateRedoState();  
    }
    
    /**
     * Can't delete a node because it has a page attached. Say so.
     */
    private void alertHasPage () {
        String NODE_HAS_PAGE = Controller.getController().getMsgString("NODE_HAS_PAGE");
        JOptionPane.showMessageDialog(
                                      getTreeFrame(),
                                      NODE_HAS_PAGE,
                                      "Message Window",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Can't delete a node because one of its decendants has a page attached. 
     * Say so.
     */    
    private void alertDescendantsHavePage (String nodeName) {
        Vector args = new Vector();
        args.add(nodeName);
        String PAGE_ON_A_DESCENDANT = Controller.getController().getMsgString("PAGE_ON_A_DESCENDANT",args);
        JOptionPane.showMessageDialog(
                                      getTreeFrame(),
                                      PAGE_ON_A_DESCENDANT,
                                      "Message Window",
                                      JOptionPane.INFORMATION_MESSAGE);                                        
    }    

    /** 
     * Either the whole panel is being dragged around (if the MOVE_TREE tool
     *is active), or an individual element (either a node, or something 
     *associated with a node) is being dragged around on the screen . Draw a 
     *"rubberband" line from the point of drag-origination to the current 
     *mouse position.
     *<p>
     *Along the way, highlight nodes and shapes as they are moved over, and 
     *scroll the tree panel around as necessary.
     */    
    public void mouseDragged(MouseEvent e) {
        String actionName = getTool(e);

        Point oldDestination;
        if (TreeToolbar.getSelectedTool().equals(TreeToolbar.MOVE_TREE)) {
            moveTree(e);
        } else {
            // If we have an origin circle, then check to see if we have a valid destinatino
            boolean draggableTool = actionName.equals(TreeToolbar.MOVE_BRANCH) || 
                                    actionName.equals(TreeToolbar.MOVE_PAGE) ||
                                    actionName.equals(TreeToolbar.COPY_PAGE) ;
            
            if ( highlightedCircle!=null && draggableTool ) {
                
                oldDestination = destinationPoint;
                destinationPoint = new Point(e.getX(), e.getY());
                
                repaintDraggedLine(oldDestination);
                

                // Check for each type of possibly highlighted shape.  Once one has been found we can return
                // since it is only possible to have one highlighted destination at a time.
                highlightedDestinationCircle = (TreeCircle)containmentBasedRepaint (highlightedShape, listenedCircles,  e );
                if (highlightedDestinationCircle != null) {
                    Node node = highlightedDestinationCircle.getNodeView().getNode();
                    //boolean terminalWithChildren = highlightedDestinationCircle.getNodeView().isTerminal() && node.getChildren().size() > 0;
                    
                    // Don't allow dropping on locked nodes
                    if (node.getLocked()) {
                        highlightedDestinationCircle = null;    
                    }
                                        
                    highlightedShape=highlightedDestinationCircle;                        
                    return;
                } 
                highlightedHorizRect = (TreeHorizontalRect)containmentBasedRepaint (highlightedShape, listenedHorizRects,  e ); 
                if (highlightedHorizRect != null) {
                    highlightedShape=highlightedHorizRect;
                    return;
                } 
                
                highlightedVertRect = (TreeVerticalRect)containmentBasedRepaint (highlightedShape, listenedVertRects,  e ); 
                highlightedShape=highlightedVertRect;

                // If the drag has occurred off screen, then we want to scroll to where the drag destination is
                JViewport viewPort = (JViewport)getParent();
                Rectangle rect = viewPort.getViewRect();

                int xDiff=0 ;
                if (rect.getX()+rect.getWidth() <  e.getX()) { //shift the scrollpane up
                    xDiff = (int)( e.getX() - (rect.getX() + rect.getWidth()) ) ;
                } else if (rect.getX() > e.getX()) { //shift the scrollpane up
                    xDiff = (int)( e.getX() - rect.getX()  ) ;
                }
                if (xDiff != 0) {
                    int xPos = viewPort.getViewPosition().x + xDiff;
                    xPos = Math.min(getWidth()-viewPort.getWidth(),xPos);
                    xPos = Math.max(0,xPos);
                    viewPort.setViewPosition(new Point(xPos, viewPort.getViewPosition().y));                  
                }                                

                int yDiff=0 ;
                if (rect.getY()+rect.getHeight() <  e.getY()) { //shift the scrollpane up
                    yDiff = (int)( e.getY() - (rect.getY() + rect.getHeight()) ) ;
                } else if (rect.getY() > e.getY()) { //shift the scrollpane up
                    yDiff = (int)( e.getY() - rect.getY()  ) ;
                }
                if (yDiff != 0) {
                    int yPos = viewPort.getViewPosition().y + yDiff;
                    yPos = Math.min(getHeight()-viewPort.getHeight(),yPos);
                    yPos = Math.max(0,yPos);
                    viewPort.setViewPosition(new Point(viewPort.getViewPosition().x, yPos ));  
                } 
            }                         
        }
    }
    
    /**
     * Used to repaint the the TreePanel in areas where the rubberband line
     * previously was but no longer is.  Doing this instad of just calling
     * repaint() allows us to repaint only the area of interest, for better
     * efficiency.
     */
    private void repaintDraggedLine (Point oldDestination) {
        if (oldDestination == null) {
            oldDestination = destinationPoint;
        }
        int minX = (int) Math.min(highlightedCircle.getX()+ getHalfCircleDiameter()+2, Math.min(oldDestination.x, destinationPoint.x)) - getHalfCircleDiameter() ;  // -1 = a little buffer
        int minY = (int) Math.min(highlightedCircle.getY()+ getHalfCircleDiameter()+2, Math.min(oldDestination.y, destinationPoint.y)) - getHalfCircleDiameter() ;  // -1 = a little buffer
        int maxX = (int) Math.max(highlightedCircle.getX()+ getHalfCircleDiameter()+2, Math.max(oldDestination.x, destinationPoint.x)) + getHalfCircleDiameter() ;  // +1 = a little buffer
        int maxY = (int) Math.max(highlightedCircle.getY()+ getHalfCircleDiameter()+2, Math.max(oldDestination.y, destinationPoint.y)) + getHalfCircleDiameter() ;  // +1 = a little buffer        
       
        int repaintWidth = maxX - minX;
        int repaintHeight = maxY - minY;
        
        repaint(minX, minY, repaintWidth, repaintHeight);
    }
    
    /** 
     * Called by mouseDragged when MOVE_TREE tool is active. Handles the
     *task of repositioning the viewport
     */        
    private void moveTree(MouseEvent e) {
        // All of the viewPortPosition math is there to account for the fact that mouse events are reported
        // in view coordinates related to the viewable area of the screen, not actual coordinates.
        int xDiff=0, yDiff=0;
        
        JViewport viewPort = (JViewport)getParent();
        int logicalX = e.getX() - viewPort.getViewPosition().x;
        int logicalY = e.getY() - viewPort.getViewPosition().y;

        if (treeMovedDestinationPoint == null) {
            treeMovedDestinationPoint = new Point(logicalX, logicalY);
        }
        xDiff = treeMovedDestinationPoint.x - logicalX;
        yDiff = treeMovedDestinationPoint.y - logicalY;
        treeMovedDestinationPoint = new Point(logicalX, logicalY);                
        if (Math.abs(xDiff) > 0) {
            int xPos = viewPort.getViewPosition().x + xDiff;
            xPos = Math.min(getWidth()-viewPort.getWidth(),xPos);
            xPos = Math.max(0,xPos);
            viewPort.setViewPosition(new Point(xPos, viewPort.getViewPosition().y));                  
        }                                
        if (Math.abs(yDiff) > 0) {
            int yPos = viewPort.getViewPosition().y + yDiff;
            yPos = Math.min(getHeight()-viewPort.getHeight(),yPos);
            yPos = Math.max(0,yPos);
            viewPort.setViewPosition(new Point(viewPort.getViewPosition().x, yPos ));  
        }    
    }
    
    /** 
     * Not handled
     */
    public void mouseEntered(MouseEvent e) {
    }
    
    /** 
     * Not handled
     */
    public void mouseExited(MouseEvent e) {
    }
    
    /** 
     * Invoked when the mouse button has been moved on a component
     * (with no buttons down).  Checks to see if the mouse is over a node (or
     * other related shape), and if so, draws the appropriate highlighted 
     *shape tied to that node. Note that Some functionality/highlighting
     *only occurs under specific conditions related to the selected tool
     * 
     * @param e The MouseEvent that triggered this function
     */
    public void mouseMoved(MouseEvent e) {
        TreeCircle oldHighlightedCircle = highlightedCircle;
        highlightedCircle = (TreeCircle)containmentBasedRepaint (oldHighlightedCircle, listenedCircles,  e );

        if (highlightedCircle != null) {
            activeView = highlightedCircle.getNodeView();
            Node activeNode = activeView.getNode();        
            String actionName = getTool(e);
            if ((activeNode.getLocked() && !actionName.equals(TreeToolbar.MOVE_BRANCH)) ||  (!isAllowedTerminalAction(actionName) && !activeNode.getCheckedOut())) {
                highlightedCircle = null;
                repaint();       
            } else if (actionName.equals(TreeToolbar.DELETE_NODE)  ||  actionName.equals(TreeToolbar.DELETE_CLADE)) {
                if ( !okToHandleNode(true) || activeNode.hasPage() ) {
                    highlightedCircle = null;
                    repaint();
                }
            } else if (actionName.equals(TreeToolbar.COLLAPSE_ALL) ) {
               if (activeNode.getChildren().size() == 0  ) {
                    highlightedCircle = null;
                    repaint();
                }                 
            } else if (actionName.equals(TreeToolbar.COLLAPSE_BELOW) ) { 
                if ( activeNode == tree.getRoot() ) {
                    highlightedCircle = null;
                    repaint();
                }
            } else if (actionName.equals(TreeToolbar.VIEW_PAGE)) {
                if (StringUtils.isEmpty(activeNode.getName())) {
                    highlightedCircle = null;
                    repaint();                                   
                }
            } else if (actionName.equals(TreeToolbar.REMOVE_PAGE)) {
                if (!activeNode.hasPage()) {
                    highlightedCircle = null;
                    repaint();
                }
            } else if (actionName.equals(TreeToolbar.SUBGROUPS_INCOMPLETE)) {
                if (activeNode.getIsLeaf()) {
                    highlightedCircle = null;
                    repaint();
                }
            }
        }
    }
    
    /**
     * Determines if the passed-in actionName can be performed on a terminal
     * node that has a subtree
     * 
     * @param actionName The actionName to check
     */
    private boolean isAllowedTerminalAction(String actionName) {
        return !actionName.equals(TreeToolbar.LEAF_NODE);
    }
    
    /** 
     * Invoked when a mouse button has been pressed on a component. Sets the
     *grabhand cursor if MOVE_TREE tool is selected
     */
    public void mousePressed(MouseEvent e) {
        requestFocus();
        
        String actionName = getTool(e);
        
        if (actionName.equals(TreeToolbar.MOVE_TREE)) {
            if(openHand==null) {
                openHand = getCursor();
            }
            setCursor(closedHand);
        }

    }
    
    /** 
     * Invoked when a mouse button has been released on a component. This\
     *is a followup to mouseDragged, and relates to the dropping of one node 
     *(or data tied to that node) onto another. This function tests if
     *the action is allowed, and if so perfomrs the action, as an 
     *undoable edit.
     */
    public void mouseReleased(MouseEvent e) {
        String actionName = getTool(e);

        if (actionName.equals(TreeToolbar.MOVE_TREE)) {
            setCursor(openHand);
        }
        
        if (highlightedCircle!=null) {
            if (actionName.equals(TreeToolbar.MOVE_BRANCH)) {       
                boolean keepchecking = true;
                if (highlightedDestinationCircle != null) {
                        if (highlightedDestinationCircle != highlightedCircle) {
                            tempActiveView = activeView;
                            tempHighlightedShape = highlightedDestinationCircle;
                            final NodeView destinationView = highlightedDestinationCircle.getNodeView();
                            final Node destinationNode = destinationView.getNode();
                            if (!destinationNode.getCheckedOut()) {
                                checkForSubTreeDownload(destinationView);
                                return;
                            }
                            
                            // Dont allow dragging of ISPU nodes if the destination has no children
                            if (destinationNode.getChildren().size() == 0 && tempActiveView.getNode().getConfidence() == Node.INCERT_UNSPECIFIED) {
                                String NO_ISPU_TWO_CHILDREN = Controller.getController().getMsgString("NO_ISPU_TWO_CHILDREN");
                                JOptionPane.showMessageDialog(Controller.getController().getTreeEditor(), NO_ISPU_TWO_CHILDREN, "Error", JOptionPane.ERROR_MESSAGE);
                                keepchecking = false;
                            }

                            if (keepchecking && okToHandleNode()) {
                                Thread toDo = new Thread() {
                                    public void run() {
                                        highlightedCircle=null;
                                        try {
                                            if (nodeToCollapse != null) {
                                                NodeView collapseView = (NodeView)nodeToNodeView.get(nodeToCollapse);
                                                showNodeDelete(collapseView,1500, true);
                                            }
                                            MoveAsChildUndoableEdit edit = new MoveAsChildUndoableEdit(tempActiveView.getNode(), destinationView, nodeToCollapse); 
                                            finishNodeHandle(edit);
                                            tempActiveView = null;
                                            tempHighlightedShape = null;
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                toDo.start();                                
                            }
                        }
                        keepchecking = false;
                }  

                if (keepchecking) {
                    if (highlightedHorizRect != null) {
                        tempActiveView = activeView;
                        tempHighlightedShape = highlightedHorizRect;
                        // Dont allow dragging of ISPU nodes if the destination has no children
                        if (tempHighlightedShape.getNodeView().getNode().getChildren().size() == 0 && tempActiveView.getNode().getConfidence() == Node.INCERT_UNSPECIFIED) {
                            String NO_ISPU_TWO_CHILDREN = Controller.getController().getMsgString("NO_ISPU_TWO_CHILDREN");
                            JOptionPane.showMessageDialog(Controller.getController().getTreeEditor(), NO_ISPU_TWO_CHILDREN, "Error", JOptionPane.ERROR_MESSAGE);
                            keepchecking = false;
                        }                        
                        if (keepchecking && okToHandleNode()) { 
                            Thread toDo = new Thread() {
                                public void run() {
                                    try {
                                        highlightedCircle=null;
                                        if (nodeToCollapse != null) {
                                            NodeView collapseView = (NodeView)nodeToNodeView.get(nodeToCollapse);
                                            showNodeDelete(collapseView,1500, true);
                                        }

                                        MoveAsSiblingUndoableEdit edit = new MoveAsSiblingUndoableEdit(tempActiveView.getNode(), 
                                        											tempHighlightedShape.getNodeView().getNode(),
                                        											nodeToCollapse);
                                        finishNodeHandle(edit);
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    tempActiveView = null;
                                    tempHighlightedShape = null;                                    
                                }
                            };
                            toDo.start();                                
                            
                            keepchecking = false;                        
                        }
                    }
                }

                if (keepchecking) {
                    if ( highlightedVertRect != null) {
                        tempActiveView = activeView;
                        tempHighlightedShape = highlightedVertRect;                        
                        
                        if (okToHandleNode()) {
                            Thread toDo = new Thread() {
                                public void run() {
                                    TreeVerticalRect destRect = (TreeVerticalRect)tempHighlightedShape;
                                    highlightedCircle=null;
                                    try {
                                        if (nodeToCollapse != null) {
                                            NodeView collapseView = (NodeView)nodeToNodeView.get(nodeToCollapse);
                                            showNodeDelete(collapseView,1500, true);
                                        }

                                        MoveAsChildUndoableEdit edit = new MoveAsChildUndoableEdit(tempActiveView.getNode(), 
                                        										destRect.getNodeView(), destRect.getChildIndex()+1, 
                                        										nodeToCollapse);
                                        finishNodeHandle(edit);
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    tempActiveView = null;
                                    tempHighlightedShape = null;                                       
                                }
                            };
                            toDo.start();                                                            
                        }
                    }
                }
            }
        }
        
        destinationPoint = null;
        treeMovedDestinationPoint = null;
        if (!confidencePopupMenu.isVisible()) {
            highlightedCircle = null;
        }
        highlightedVertRect = null;
        highlightedHorizRect = null;
        highlightedDestinationCircle = null;
        repaint();
    }
    
    /**
     * Test if the parent node will be deleted, even though it isn't legal 
     * to do so. If that's the case, complain, and return false
     */    
    public boolean okToHandleNode() {
        return okToHandleNode(false);
    }

    public boolean okToHandleNode(boolean value) {
        return okToHandleNode(value, highlightedCircle.getNodeView());
    }
    
    public boolean okToHandleNode(NodeView view) {
        return okToHandleNode(false, view);
    }
    
    /**
     * Test if the parent node will be deleted, even though it isn't legal 
     * to do so. If that's the case, complain, and return false. By default,
     * it is assumed that the panel is the TreePanel (not a preview panel)
     */
    public boolean okToHandleNode(boolean preview, NodeView view){
        Node node = view.getNode();   
        Node parent = node.getParent();
        Node otherChild = null;
        int otherChildIndex = -1;
        int numNonISPUNodes = parent != null ? parent.getNumNonISPUChildren() : -1;
        if (node.getConfidence() != Node.INCERT_UNSPECIFIED) {
            // In this case, bump down numNonISPUNodes by 1 since we are deleting
            // one of them
            numNonISPUNodes--;
        }
        // Find the index of the non-ISPU node
        int counter = 0;
        if (parent != null && numNonISPUNodes == 1) {
            Iterator it = parent.getChildren().iterator();
            while (it.hasNext()) {
                Node next = (Node) it.next();
                if (next != node) {
                    if (!(next.getConfidence() == Node.INCERT_UNSPECIFIED)) {
                        otherChildIndex = counter;
                    }
                }
                counter++;
            }
        }
        
        boolean willDeleteUndeletableParent = false;
        
        if ( parent == null) { //root
            willDeleteUndeletableParent = true;
        } else {
            String actionName = TreeToolbar.getSelectedTool();  
            // In this case it's an internal node, so the children will just
            // fall back to the parent's parent
            if (actionName.equals(TreeToolbar.DELETE_NODE) && !view.isTerminal() ) {
                if (!preview) {
                    nodeToCollapse = null;
                }
                return true;
            }

            if ( numNonISPUNodes != 1 ) { 
                if (!preview) {
                    nodeToCollapse = null;
                }
                return true; //nobody needs to be deleted, move along -- these are not the droids you're looking for
            } else {
                otherChild = parent.getChildAtIndex(otherChildIndex);
                if ( parent.hasPage() && otherChild.hasPage() ){
                    willDeleteUndeletableParent = true;
                }
            }
        }
        
        if ( willDeleteUndeletableParent ) {
            if (!preview) {
                String ACTION_DELETE_UNDELETABLE_NODE = Controller.getController().getMsgString("ACTION_DELETE_UNDELETABLE_NODE");
                JOptionPane.showMessageDialog(
                                          getTreeFrame(),
                                          ACTION_DELETE_UNDELETABLE_NODE,
                                          "Message Window",
                                          JOptionPane.INFORMATION_MESSAGE);                        
            }

            highlightedCircle = null;
            destinationPoint = null;
            repaint();

            return false;
        }
        
            //two children, and at least one of the parent/other_child pair can legitimately be deleted      
        if ( parent.getName().equals("") ) {
            if (!preview) {
                nodeToCollapse = parent;
            }
            return true;
        } else if ( otherChild.getName().equals("") )  { 
            if (!preview) {
                nodeToCollapse = otherChild;
            }
            //System.out.println("parent name is: " + parent.getName() + " parent's child size = " + parent.getChildren().size() + " collapse name is: " + nodeToCollapse.getName() + " nodeToCollapse' child size = " + nodeToCollapse.getChildren().size());
            // Case where one or more of the nodes is ISPU
            if (parent.getChildren().size() > 2 && nodeToCollapse.getChildren().size() == 0) {
                String ISPU_INCONSISTENT = Controller.getController().getMsgString("ISPU_INCONSISTENT");
                JOptionPane.showMessageDialog(TreePanel.getTreePanel(), ISPU_INCONSISTENT, "Error", JOptionPane.ERROR_MESSAGE);

                highlightedCircle = null;
                destinationPoint = null;
                repaint();

                return false;            
            } else {
                return true;
            }
        }
        
        /*
         * At least one of the nodes doesn't have a page, and both have a name
         * so now it's just a matter of either 
         *  - picking the node that can be deleted and asking for confirmation, or
         *  - asking the user to pick which one to delete
         */
        if (preview) {
            return true;
        }
        if (!otherChild.hasPage() && !otherChild.hasPageOnServer() &&  !parent.hasPage() ){ 
            // ok to delete either one - ask which one the user wants to delete

            //show a custom dialog here ... which node would you like to delete:
            String WHICH_NODE_TO_COLLAPSE= Controller.getController().getMsgString("WHICH_NODE_TO_COLLAPSE");
            String[] choices = new String[] {parent.getName(), otherChild.getName(), "neither one, thanks" };
            int response = RadioOptionPane.showRadioButtonDialog(
                Controller.getController().getTreeEditor(), 
                WHICH_NODE_TO_COLLAPSE, 
                "Which node to collapse?", 
                choices);

            switch (response) {
                case 0:  
                    nodeToCollapse = parent;
                    return true; 
                case 1:  
                    nodeToCollapse = otherChild;
                    return true; 
                default: 
                    nodeToCollapse = null;
                    return false; 
            }
        } else {
            //ask user if it's ok to delete the named node that doesn't have a page
            nodeToCollapse = otherChild.hasPage() || otherChild.hasPageOnServer() ? parent : otherChild;

            Vector vec = new Vector();
            vec.add(nodeToCollapse.getName());
            String DELETE_NODE_OK = Controller.getController().getMsgString("DELETE_NODE_OK",vec);
            int result = JOptionPane.showConfirmDialog(treeFrame, DELETE_NODE_OK,
                                "OK to delete node?",
                                JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);                    

            if ( result == 0) { //yes
                return true;
            } else {
                nodeToCollapse = null;
                return false;
            }
        }
    }    
    
    /**
     * Used by mouse motion events to determine containment of the mouse point 
     *by available shapes, and to redraw if necessary
     * 
     * @param oldShape The old highlighted shape if there was one
     * @param list The list of shapes to check
     * @param e The MouseEvent that triggered this function call
     */
    private AbstractTreeShape containmentBasedRepaint(AbstractTreeShape oldShape, ArrayList list,  MouseEvent e ) {
        Iterator it = (new ArrayList(list)).iterator();
        AbstractTreeShape newShape=null ,ts = null;
        while (it.hasNext()) {
            ts = (AbstractTreeShape) it.next();
            if (ts.contains(e.getPoint())) { 
                newShape = ts;
                break;
            }
        }    
        String actionName = getTool(e);

        //we're dragging, over something, with the MOVE_BRANCH tool
        if ( actionName.equals(TreeToolbar.MOVE_BRANCH) && destinationPoint != null && newShape!=null ) {
            Node draggedOverNode = newShape.getNodeView().getNode();
            Node highlightedCircleNode = highlightedCircle.getNodeView().getNode();
            boolean draggingOverSelf = (draggedOverNode == highlightedCircleNode);
            //the dragged-over node is a descendant of the dragging node
            boolean isDescendant = ( draggedOverNode.isDescendantOf(highlightedCircleNode)); 
            //the dragged-over node is the parent of the dragging node
            boolean isChild = ( !(newShape instanceof TreeVerticalRect) && draggedOverNode == highlightedCircleNode.getParent() ); 

            // Here root only has 2 children and this is one of them, so do not allow dragging
            boolean dualChildOfRoot = (highlightedCircleNode.getParent() == getRootNode()) && (getRootNode().getChildren().size() == 2);
            
            // ISPU nodes can't be dragged as siblings
            boolean ispuDraggedAsSibling = highlightedCircleNode.getConfidence() == Node.INCERT_UNSPECIFIED && newShape instanceof TreeHorizontalRect;
            
            if (draggingOverSelf || isDescendant || isChild || dualChildOfRoot || ispuDraggedAsSibling) {
                newShape = null;
            } else if (newShape instanceof TreeVerticalRect) {
                //not necessarily mutually exclusive of the first part of the if...but easier to read (?)
                int index = ((TreeVerticalRect)newShape).getChildIndex();
                Node draggedNode = highlightedCircleNode;
                
                boolean vertLineBelowDragged = (draggedOverNode.getChildren().get(index) == draggedNode);
                boolean vertLineAboveDragged = (draggedOverNode.getChildren().get(index+1) == draggedNode );
                
                if (draggedOverNode == draggedNode.getParent()   &&   (vertLineBelowDragged || vertLineAboveDragged) ){
                    newShape = null;
                }
           } else if (newShape instanceof TreeHorizontalRect) {
               // Here we want to disallow the move as sibling if there are only 2 nodes
               // since they already are siblings.
               Node parent = draggedOverNode.getParent();
               if (parent == highlightedCircleNode.getParent() && parent.getChildren().size() == 2) {
                    newShape = null;
               }
           }
        }
        
        NodeView view = null;
        Node node = null;
        if (newShape != null) {
            view = newShape.getNodeView();
            node = view.getNode();
        }

        if (actionName.equals(TreeToolbar.LEAF_NODE) && newShape != null) {
            if (!view.isTerminal() || !node.getCheckedOut()) {
                newShape = null;
            }
        }

        
        if (actionName.equals(TreeToolbar.FETCH_SUBGROUP) && newShape != null) {
            if ( node.getCheckedOut()) {
                newShape = null;
            }
        }
        
        if ( actionName.equals(TreeToolbar.MOVE_PAGE) || actionName.equals(TreeToolbar.COPY_PAGE)) {
            if (!(newShape instanceof TreeCircle)) {
                newShape = null;
            }
            
            if (actionName.equals(TreeToolbar.MOVE_PAGE) && node == tree.getRoot()  ) {
                newShape = null;
            }
        }
        
        if ( actionName.equals(TreeToolbar.ZOOM_IN) || actionName.equals(TreeToolbar.ZOOM_OUT) || actionName.equals(TreeToolbar.MOVE_TREE)) {
            newShape = null;        
        }

        if (  (oldShape!=null || newShape!=null)  && oldShape!=newShape) {

            // Repaint the newShape so it does show up highlighted
            if (newShape!=null) {
                repaint((int)newShape.getX()-1, (int)newShape.getY()-1, (int)newShape.getWidth()+2, (int)newShape.getHeight()+2);
            }
            
            // Repaint the oldShape so it doesn't show up highlighted
            if (oldShape!=null) {
                //System.out.println("OldShape = " + oldShape);
                repaint((int)oldShape.getX()-1, (int)oldShape.getY()-1, (int)oldShape.getWidth()+2, (int)oldShape.getHeight()+2);
            }
        }

        return newShape;
    }    

    /** 
     *Not allowed to delete the root, nodes that aren't checked out, and nodes
     *with pages
     */
    private boolean cantDeleteNode(Node node) {
        return node.hasPage() || !node.getCheckedOut() || node.getParent() == null;
    }
    
    /** 
     *Not allowed to delete clades containing and nodes
     *with pages
     */    
    private boolean cantDeleteClade(Node node) {
        Stack stack = new Stack();
        stack.push(node);
        while(!stack.empty()) {
            Node nextNode = (Node) stack.pop();
            if (cantDeleteNode(nextNode)) {
                return true;
            } else {
                Iterator it = nextNode.getChildren().iterator();
                while (it.hasNext()) {
                    stack.push(it.next());
                }
            }
        }
        return false;
    }
    
    /**
     * Extends the paintComponent method of the AbstractTreePanel, drawing 
     *additional things not handled by the abstract class (e.g. rubberband
     * lines for dragging, shape highlighting for mouse movement)
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //long currentTime = System.currentTimeMillis();
        Color origColor = g.getColor();            
                            
        g.setColor(Color.black);
        //in the middle of dragging, so paint as necessary
        if (highlightedCircle != null) {
            if (destinationPoint != null) {
                g.drawLine((int) highlightedCircle.getX() + getHalfCircleDiameter() + 4, (int) highlightedCircle.getY() + getHalfCircleDiameter() + 4, destinationPoint.x, destinationPoint.y);
            }            
            
            g.setColor(Color.blue);
            g.drawOval((int)highlightedCircle.getX()+2, (int)highlightedCircle.getY()+2, (int)highlightedCircle.getWidth()-4, (int)highlightedCircle.getHeight()-4);           

            //g.setColor(Color.red);
            g.setColor(highlightedLineBlue);
            if (highlightedDestinationCircle != null) {
                g.drawOval((int)highlightedDestinationCircle.getX()+2, (int)highlightedDestinationCircle.getY()+2, (int)highlightedDestinationCircle.getWidth()-4, (int)highlightedDestinationCircle.getHeight()-4);
            } else {
                if (highlightedHorizRect != null) {
                    g.fillRect((int)highlightedHorizRect.getX()+2, (int)highlightedHorizRect.getY()+2, (int)highlightedHorizRect.getWidth()-4, (int)highlightedHorizRect.getHeight()-4);
                } else {
                    if (highlightedVertRect != null) {
                        g.fillRect((int)highlightedVertRect.getX()+2, (int)highlightedVertRect.getY()+2, (int)highlightedVertRect.getWidth()-4, (int)highlightedVertRect.getHeight()-4);
                    }        
                }
            }
        }

        Rectangle repaintRect = g.getClipBounds();
        // Paint the uneditable nodes
        g.setColor(Color.lightGray);
        Iterator it = new ArrayList(uneditableCircleValues).iterator();
        while (it.hasNext()) {
            int[] coords = (int[]) it.next();            
            if (coords[0] + getCircleDiameter() >= repaintRect.getX()  && coords[0] <= repaintRect.getX() + repaintRect.getWidth()   &&
                coords[1] + getCircleDiameter() >= repaintRect.getY() &&  coords[1] <= repaintRect.getY() + repaintRect.getHeight() ) {
                        //only paint this oval if it in some way overlaps the requests rectangle of repainting
                        g.fillOval(coords[0], coords[1], getCircleDiameter(), getCircleDiameter());
            }           
        }

        drawPageRects(repaintRect, g);
        
        g.setColor(Color.lightGray);
        it = new ArrayList(nodeToGrayCircles.values()).iterator();
        while (it.hasNext()) {
            int[] coords = (int[]) it.next();
            if (coords[0] + getCircleDiameter() >= repaintRect.getX()  && coords[0] <= repaintRect.getX() + repaintRect.getWidth()   &&
                coords[1] + getCircleDiameter() >= repaintRect.getY() &&  coords[1] <= repaintRect.getY() + repaintRect.getHeight() ) {
                        //only paint this oval if it in some way overlaps the requests rectangle of repainting
                        g.fillOval(coords[0], coords[1], getCircleDiameter(), getCircleDiameter());
            }
        }

        g.setColor(Color.red);
        it = new ArrayList(deletedNodes).iterator();
        while (it.hasNext()) {
            NodeView view = (NodeView)it.next();
            int x = view.getX();
            int y = view.getY();
            int halfCirc = getHalfCircleDiameter();
            int oneAndHalfCirc = getCircleDiameter()+halfCirc;
            
            //diag slash, starting from top left
            g.drawLine(x-halfCirc+1, y-halfCirc+1, x+oneAndHalfCirc+1, y+oneAndHalfCirc+1);
            g.drawLine(x-halfCirc, y-halfCirc+1, x+oneAndHalfCirc, y+oneAndHalfCirc+1);
            //diag slash, starting from bottom right
            g.drawLine(x-halfCirc+1, y+oneAndHalfCirc+1, x+oneAndHalfCirc+1, y-halfCirc+1);
            g.drawLine(x-halfCirc, y+oneAndHalfCirc+1, x+oneAndHalfCirc, y-halfCirc+1);
        }
               
        g.setColor(origColor);
    }
    
    /**
     * Used to rebuild and redraw the tree if a resize is necessary
     */
    public void rebuildTree() {
        listenedCircles = new ArrayList();
        listenedHorizRects = new ArrayList();
        listenedVertRects = new ArrayList();  
        uneditableCircleValues = new ArrayList();
        
        highlightedRect = null;
        highlightedCircle = null;
        highlightedDestinationCircle=null;    
        highlightedHorizRect=null;
        highlightedVertRect=null;
        destinationPoint = null;

        super.rebuildTree();
    }
           
    /**
     * Determines the positions of all the nodes and the shapes used to 
     *monitor for mousee events that equire redrawing to occur
     */
    public void constructLocations() {  
        super.constructLocations();
        Iterator it = nodeViews.iterator();
        // This works since we only want to have the gray circles appear on an initial download.
        // Once the hashtable is not null, we don't ever worry about these values
        boolean useGrayCircles = !treeFrame.getToolbarEnabled();
        boolean initializeCircles = false;
        if (nodeToGrayCircles == null) {
            nodeToGrayCircles = new Hashtable();
            initializeCircles = true;
        }
        if (!useGrayCircles) {
            nodeToGrayCircles.clear();
        }
        // Add the necessary listener shapes to the arrays so we can interpret mouse events later.
        while (it.hasNext()) {
            NodeView currentView = (NodeView) it.next();
            Node node = currentView.getNode();
            int yLoc = currentView.getY() + getHalfCircleDiameter();
            listenedCircles.add(new TreeCircle(currentView.getX()-4, currentView.getY()-4, getCircleDiameter()+8, getCircleDiameter()+8, currentView ));              
            if (currentView.getParent() != null) {
                //not the root
                int listenedHorizWidth = currentView.getX()-currentView.getParent().getX() - (getCircleDiameter()+getHalfCircleDiameter()) ;
                listenedHorizRects.add(new TreeHorizontalRect(currentView.getParent().getX()+(getCircleDiameter()) , yLoc-4 ,listenedHorizWidth , 9, currentView ));
            }
            if (!currentView.isTerminal()) {
                ArrayList children = currentView.getChildren();
                NodeView viewA, viewB = (NodeView)children.get(0);
                for (int i=1; i<children.size();i++) { 
                    viewA = viewB;
                    viewB = (NodeView)children.get(i);
                    
                    listenedVertRects.add(new TreeVerticalRect(currentView.getX()-1, viewA.getY()+getCircleDiameter(), 9, viewB.getY()-viewA.getY()-getCircleDiameter(), currentView,i-1));
                }
            } else {
                // If it is a terminal node w/ a subtree, it isn't allowed to 
                // be edited, so add it to the special list.
                if (!node.getCheckedOut() && !node.getLocked()) {
                    int[] location = new int[] {currentView.getX(), currentView.getY()};
                    uneditableCircleValues.add(location);
                }
            }
            if (useGrayCircles && initializeCircles) {
                nodeToGrayCircles.put(currentView.getNode(), new int[] {currentView.getX(), currentView.getY()});
            }
        }    
        
        // Here we have removed any nodes that have finished, but we need to re-add the remaining gray ones
        if (useGrayCircles && !initializeCircles) {
            it = new HashSet(nodeToGrayCircles.keySet()).iterator();
            while (it.hasNext()) {
                Node node = (Node) it.next();
                NodeView currentView = (NodeView) nodeToNodeView.get(node);
                nodeToGrayCircles.remove(node);
                nodeToGrayCircles.put(node, new int[] {currentView.getX(), currentView.getY()});
            }
        }
        
        if (getParent() != null) {
            // Check to see if the activeNode is in the viewport.  If it isn't then scroll it into view.
            if (activeView != null ){ //&& !activeView.isTerminal()) {
                bringNodeViewToViewPort(activeView);
                activeView = null;
            }
        }

        repaint();
    }

    /**
     *Ensures that the view tied to the node passed as an argument is visible.
     *This is specifically relevant for the find functionality
     *
     * @param node Node to be shown
     *
     * @returns the nodeview that's been placed into view
     */    
    public NodeView bringNodeToViewPort(Node node) {
        NodeView view = (NodeView)nodeToNodeView.get(node);
        bringNodeViewToViewPort(view);
        return view;
    }
    
    /**
     *Ensures that the view passed as an argument is visible.
     *This is specifically relevant for undo events (where you may be 
     *undoing/redoing something on a currently unseen node), and for 
     *the find functionality
     *
     * @param view NodeView to be shown
     */
    public void bringNodeViewToViewPort(NodeView view) {
        JViewport viewPort = (JViewport) getParent();
        Rectangle rect = viewPort.getViewRect();
            
        int testX = view.getX() + getCircleDiameter(), testY = view.getY() + getCircleDiameter();
        int xLoc = viewPort.getViewPosition().x; 
        int yLoc = viewPort.getViewPosition().y; 
        if (!rect.contains(testX, rect.getY())) {
            xLoc = (int) (testX - (rect.getX() + rect.getWidth()/2));
            xLoc += viewPort.getViewPosition().x;
            if (xLoc < 0) {
                xLoc = 0;
            }
            if (xLoc > getWidth() - viewPort.getWidth() ) {
                xLoc = getWidth() - viewPort.getWidth();
            }                                  
        }

        if (!rect.contains(rect.getX(), testY)) {
            yLoc = (int) (testY - (rect.getY() + rect.getHeight()/2));
            yLoc += viewPort.getViewPosition().y;
            if (yLoc < 0) {
                yLoc = 0;
            }
            if (yLoc > getHeight() - viewPort.getHeight() ) {
                yLoc = getHeight() - viewPort.getHeight();
            }                    
        }
        viewPort.setViewPosition(new Point(xLoc, yLoc));
    }
    
    /**
     *undownloaded/uncheckedout nodes
     */
    public void addGrayCircles(ArrayList nodes) {
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            NodeView view = (NodeView) nodeToNodeView.get(node);
            if (view != null) {
                nodeToGrayCircles.put(node, new int[] {view.getX(), view.getY()});
            }
        }
    }

    /**
     * Clean up after nodes have been downloaded
     */
    public void removeGrayCircles(ArrayList nodes) {
        Iterator it = nodes.iterator();
    
        while (it.hasNext()) {
            Node node = (Node) it.next();
            nodeToGrayCircles.remove(node);
        }
    }
    /**
     *Called in case of a cancelled subtree download, to remove the nodes that 
     *were just added
     *
     * @param nodes The list of nodes to remove
     */
    public void removeNewNodes(ArrayList nodes) {
        Iterator it = nodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            nodeToGrayCircles.remove(node);
            removeNode(node);
        }
    }
    
    /**
     * Removes the passed-in node (and it's textfield) from the field of view
     *
     * @param node The Node to remove
     */
    public void removeNode(Node node) {
        nodeToNodeView.remove(node);
        TreePanelUpdateManager.getManager().zombieNode(node);
    }
    
    /**
     * Textfields are disabled hen they aren't checked out
     */
    public void disableTextFieldForNode(Node node) {
        NodeView view = (NodeView) nodeToNodeView.get(node);
        if (view != null) {
            view.getTextField().setEditable(false);
        }
    }
    
    /**
     * Overridden in order to allow TextFields to lose focus
     *
     * @return true Since this component is focusable
     */
    public boolean isFocusable () {
        return true;
    }
    
    /**
     * Overridden to not draw children of terminal nodes in the tree
     */
    public boolean dontDrawChildren(NodeView view) {
        return tree.isTerminalNode(view.getNode());
    }
    
    /**
     * The Node that was associated with the last event -- needed so we can 
     *make sure it's onscreen
     * 
     * @param node The node to set as the active one.
     */
    public void setActiveNode(Node node) {
        activeView = (NodeView)nodeToNodeView.get(node);
    }
    
    private class ShowCollapseNodesThread extends Thread {
        private ZombiedNodesUndoableEdit edit;
        
        public ShowCollapseNodesThread(ZombiedNodesUndoableEdit ed) {
            edit = ed;
        }
        
        public void run() {
            highlightedCircle=null;
            Iterator it = edit.getZombiedNodes().iterator();
            ArrayList collapseViews = new ArrayList();
            while (it.hasNext()) {
                collapseViews.add(nodeToNodeView.get(it.next()));
            }

            try {
                showNodeDelete(collapseViews,1500, false);
                updateUndoStuff(edit);
                edit.updateTreePanel();
                deletedNodes.clear();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public ISPUUndoableEdit doISPUUndoableEdit(Node node, Node siblingNode) {
        ISPUUndoableEdit edit = null;
        try {
            Node parentNode = node.getParent();
            Node otherNode = nodeToCollapse == parentNode ? siblingNode : parentNode;
            edit = new ISPUUndoableEdit(node, nodeToCollapse, otherNode);
            showNodeDelete((NodeView) nodeToNodeView.get(nodeToCollapse), 1500, false);
            updateUndoStuff(edit);
            edit.updateTreePanel();
            deletedNodes.clear();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return edit;
    }      
}




