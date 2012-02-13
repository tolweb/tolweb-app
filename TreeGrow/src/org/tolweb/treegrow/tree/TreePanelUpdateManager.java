package org.tolweb.treegrow.tree;

import java.util.*;

/**
 * Manages the association of nodes with nodeviews, and
 * the rebuilding of treepanels containing those nodes
 */
public class TreePanelUpdateManager {
    private static TreePanelUpdateManager manager;
    
    /*
     * used to identify go from a node to a nodeview. Key=node, value=view
     */
    private Hashtable nodeToViews = new Hashtable();
    
    static {
        manager = new TreePanelUpdateManager();
    }
    
    
    /** Creates a new instance of TreePanelUpdateManager */
    public TreePanelUpdateManager() {
    }


    /**
     * Once a node has been placed in the graveyard, it's associated
     * textfield must be hidden. This does it.
     */
    public void zombieNode(Node node) {
        updateTextFields(node, false);
    }

    /**
     * Once a node has been retrieved from the graveyard, it's associated
     * textfield must be unhidden. This does it.
     */    
    public void unZombieNode(Node node) {
        updateTextFields(node, true);
    }

    /** 
     *add key-value pair
     */
    public void tieViewToNode(Node node, NodeView view) {
        ArrayList views = (ArrayList)nodeToViews.get(node);
        if (views == null) {
            views = new ArrayList();
            nodeToViews.put(node,views);
        }
        views.add(view);
        
    }

    /** 
     *remove key-value pair
     */    
    public void removeViewFromNode(Node node, NodeView view) {
        //ArrayList views = (ArrayList)nodeToViews.get(new Integer(node.getId()));
        ArrayList views = (ArrayList)nodeToViews.get(node);
        if (views != null) {    
            views.remove(view);
        }
    }

    
    /**
     * Causes both the primary TreePanel and any PageFrames containing 
     * the given node to be rebuilt. Called when structure has been changed, 
     * or a parameter of the node that impacts drawing is changed.
     */    
    public void rebuildTreePanels (Node n) {
        ArrayList touchedNodes = new ArrayList();
        touchedNodes.add(n);
        rebuildTreePanels(touchedNodes);
    }
    

    /**
     * Causes both the primary TreePanel and any PageFrames containing one of 
     * the listed nodes to be rebuilt. Called when structure has been changed, 
     * or a parameter of the node that impacts drawing is changed.
     */
    public void rebuildTreePanels (Collection touchedNodes) {
        Iterator nodesIterator = touchedNodes.iterator();
        HashSet panels = new HashSet();
        
        while (nodesIterator.hasNext()) {
            Node n = (Node)nodesIterator.next();
            // Take care of tree panel stuff
            ArrayList views = (ArrayList)nodeToViews.get(n);
            if (views != null ) {
                Iterator viewsIterator = views.iterator();
                while (viewsIterator.hasNext()) {
                    NodeView v = (NodeView)viewsIterator.next();
                    panels.add(v.getPanel());
                    FloatingTextField field = v.getTextField();
                    // Dont fire undoable edit events at this point because they
                    // didn't originate from the actual tree panel
                    field.getDocument().removeUndoableEditListener(field);
                    if (!field.getText().equals(n.getName())) {
                        field.setText(n.getName());
                    }
                    field.getDocument().addUndoableEditListener(field);
                }
            }
        }
        
        Iterator it = panels.iterator();
        while (it.hasNext()) {
            AbstractTreePanel tp = (AbstractTreePanel)it.next();
            tp.rebuildTree();
        }
        
        /*it = Controller.getController().getPageEditorIterator();
        while (it.hasNext()) {
            PageFrame f = (PageFrame) it.next();
            f.updateList();
        }*/
    }
    
    /** for all panels showing the node in question, hide/unhide
     * the textfield ted to that node
     */
    private void updateTextFields(Node node, boolean isVisible) {
        ArrayList views = (ArrayList)nodeToViews.get(node);
        if (views != null) {
            Iterator viewsIterator = views.iterator();
            while (viewsIterator.hasNext()) {
                NodeView v = (NodeView)viewsIterator.next();
                v.getTextField().setVisible(isVisible);
            }
        }
    }
    
    
    public static TreePanelUpdateManager getManager() {
        return manager;
    }
}
