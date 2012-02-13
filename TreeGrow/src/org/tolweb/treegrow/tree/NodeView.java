package org.tolweb.treegrow.tree;

import java.util.*;

/**
 * Controls drawing-related feature related to a node. Stores information such
 * as x,y location, textfield and parent/child relationships. (note, one node 
 * may be related to many nodeviews - one for each tree panel on which the node 
 * will be drawn).
 */
public class NodeView implements Comparable {
    private Node node;
    private AbstractTreePanel panel;
    private int yLocation, xLocation;
    private FloatingTextField textField;
    private NodeView parent;
    private ArrayList children;
    private String nameString;
    private String authorityString;

    /** 
     * used to keep track of whether the node will pbe printed on a page
     * as a node in a tree or an element in a list. If true, line
     * drawn coming into node will be grey, otherwise, black
     */
    private boolean onWriteAsListPage;
    
    
    public NodeView(Node n, AbstractTreePanel p) {
        this(n, p, null);
    }
    
    /**
     * Creates a new instance of NodeView. 
     *
     * @param n Node with which this view is associated.
     * @param p TreePanel on which this view will be drawn 
     */
    public NodeView(Node n, AbstractTreePanel p, NodeView pa) {
        node = n;
        panel = p;
        parent = pa;
        if (parent != null) {
            parent.addToChildren(this);
        }
        // only instantiate the text field if we are being used in an editing context
        if (TreePanel.class.isInstance(p)) {
            textField = new FloatingTextField(this);
            if (panel != null) {
                panel.add(textField);
            }
        }
        children = new ArrayList();
        TreePanelUpdateManager.getManager().tieViewToNode(node,this);
    }
    
    public int getX() {
        return xLocation;
    }
    
    public void setX(int value) {
        xLocation = value;
    }
    
    public int getY() {
        return yLocation;
    }
    
    public void setY(int value) {
        yLocation = value;
    }
    
    public Node getNode() {
        return node;
    }
    
    public AbstractTreePanel getPanel() {
        return panel;
    }
    
    /**
     * @return  number of steps away from root
     */
    public int getDepth() {
        Node top = node.getParent();
        int depth = 0;
        while (top != null) {
            depth++;
            top = top.getParent();
        }
        return depth;
    }
    
    public NodeView getParent() {
        return parent;
    }
    
    public void setParent(NodeView p) {
        parent = p;
    }

    /** 
     *Node may not be terminal on the full tree, but it's view may be terminal
     * on the panel that this view is drawn on. This tests that
     */
    public boolean isTerminal() {
        if (panel != null && panel.dontDrawChildren(this)) {
            return true;
        } else {
            return node.getChildren() == null || node.getChildren().size() == 0;
        }
    }
    
    public boolean getIsTerminal() {
    	return getChildren() == null || getChildren().size() == 0;
    }
        
    public String toString() {
        return "Node view containing node with name: " + node.getName() + " and id: " + node.getId() +  " xLocation = "  + xLocation + " yLocation: " + yLocation;
    }

    public ArrayList getChildren() {
        return children;
    }
    
    public void addToChildren(NodeView child) {
        if (child != null) {
            children.add(child);
            child.setParent(this);
        }
    }

    public void addAsFirstChild(NodeView child) {
        if (child != null) {
            children.add( 0 ,child);
            child.setParent(this);
        }
    } 
    
    public void clearChildren() {
        children.clear();
    }
    
    public FloatingTextField getTextField() {
        return textField;
    }
    
    public boolean getIsRootView() {
        return getParent() == null;
    }
    
    
    /**
     * * @param   value whether this node will show up as member 
     * of a list or a tree
     */    
    public void setOnWriteAsListPage (boolean value) {
        onWriteAsListPage = value;
    }
    
    /**
     * @return  whether this node will show up as member of a list or a tree
     */    
    public boolean getOnWriteAsListPage () {
        return onWriteAsListPage;
    }      

    public void finalize() {
        TreePanelUpdateManager.getManager().removeViewFromNode(node,this);        
    }
    
    /** Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     * 		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object.
     *
     */
    public int compareTo(Object o) {
        NodeView other = (NodeView) o;
        if (getY() > other.getY()) {
            return 1;
        } else if (getY() < other.getY()) {
            return -1;
        } else {
            return 0;
        }
    }

	public String getAuthorityString() {
		return authorityString;
	}

	public void setAuthorityString(String nodeAuthorityString) {
		this.authorityString = nodeAuthorityString;
	}

	public String getNameString() {
		return nameString;
	}

	public void setNameString(String nodeNameString) {
		this.nameString = nodeNameString;
	}
}
