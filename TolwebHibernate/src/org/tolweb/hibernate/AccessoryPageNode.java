/*
 * Created on Sep 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;

/**
 * @author dmandel
 *
 * Used to map an accessory page to a node.  Includes information
 * about whether the node is the accessory page's primary node of
 * attachment and whether a link to the page should show up on the
 * node's branch page
 */
public class AccessoryPageNode implements Cloneable, Comparable, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5685397796976746676L;
	private MappedNode node;
    private boolean isPrimaryAttachedNode;
    private boolean showLink;
    
    /**
     * @hibernate.property column="is_primary"
     * @return
     */
    public boolean getIsPrimaryAttachedNode() {
        return isPrimaryAttachedNode;
    }
    public void setIsPrimaryAttachedNode(boolean isPrimaryAttachedNode) {
        this.isPrimaryAttachedNode = isPrimaryAttachedNode;
    }
    /**
     * @hibernate.many-to-one class="org.tolweb.hibernate.MappedNode" column="node_id"
     * @return
     */
    public MappedNode getNode() {
        return node;
    }
    public void setNode(MappedNode node) {
        this.node = node;
    }
    /**
     * @hibernate.property column="show_link"
     * @return
     */
    public boolean getShowLink() {
        return showLink;
    }
    public void setShowLink(boolean showLink) {
        this.showLink = showLink;
    }
    
    public boolean equals(Object other) {
        if (other == null || !(other instanceof AccessoryPageNode)) {
            return false;
        } else {
            AccessoryPageNode otherNode = (AccessoryPageNode) other;
            if (getNode() == null || otherNode.getNode() == null) {
                return false;
            } else {
                return getNode().getNodeId().equals(otherNode.getNode().getNodeId());
            }
        }
    }
    
    public int hashCode() {
        if (getNode() != null) {
            return getNode().getNodeId().hashCode();
        } else {
            return System.identityHashCode(this);
        }
    }
    
    public Object clone() {
        try {
            AccessoryPageNode clone = (AccessoryPageNode) super.clone();
            clone.setNode((MappedNode) getNode().clone());
            return clone;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public int compareTo(Object other) {
        if (other == null || !(other instanceof AccessoryPageNode) || ((AccessoryPageNode) other).getNode() == null) {
            return 1;
        } else if (getNode() == null) {
            return -1;
        } else {
            return getNode().getName().compareTo(((AccessoryPageNode) other).getNode().getName());
        }
    }
}
