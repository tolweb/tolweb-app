/*
 * Created on Dec 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractNodeWrapper {
    private Long nodeId;

    /**
     * @hibernate.property column="node_id"
     * @return
     */
    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long value) {
        nodeId = value;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof AbstractNodeWrapper)) {
            return false;
        } else {
            AbstractNodeWrapper other = (AbstractNodeWrapper) o;
            if (other.getNodeId() != null && getNodeId() != null) {
                return other.getNodeId().equals(getNodeId());
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        if (getNodeId() != null) {
            return getNodeId().hashCode();
        } else {
            return System.identityHashCode(this);
        }
    }
    
    public String toString() {
        return getClass().getName() + ": with id: " + getNodeId(); 
    }
}
