/*
 * Attachment.java
 *
 * Created on March 10, 2004, 10:09 AM
 */

package org.tolweb.treegrow.main;

/**
 * Abstract class used for attaching some object to a node
 */
public abstract class NodeAttachment {
    protected int nodeId;
    protected String nodeName;
    
    /** Creates a new instance of Attachment */
    public NodeAttachment(int id, String nodeName) {
        this.nodeId = id;
        this.nodeName = nodeName;        
    }
    
    public String getNodeName() {
        return nodeName;
    }
    
    public void setNodeName(String value) {
        nodeName = value;
    }
    
    public int getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(int value) {
        nodeId = value;
    }   
}
