/*
 * ImageNode.java
 *
 * Created on March 10, 2004, 11:03 AM
 */

package org.tolweb.treegrow.main;

import org.tolweb.treegrow.page.*;

/**
 *
 * @author  dmandel
 */
public class ImageNode extends NodeAttachment implements ChangedFromServerProvider {
    private NodeImage img;
    
    /**
     * Keep track of whether this node was removed (so processing on the
     * server knows which nodes to detach)
     */
    private boolean removed;
    
    /** Creates a new instance of ImageNode */
    public ImageNode(int id, String nm, NodeImage ni) {
        super(id, nm);
        img = ni;
    }
    
    public boolean changedFromServer() {
        return true;
    }
    
    public void setChangedFromServer(boolean value) {
        if (value) {
            img.setChangedFromServer(value);
        }
    }
    
    public boolean wasRemoved() {
        return removed;
    }
    
    public void setWasRemoved(boolean value) {
        removed = value;
    }
    
    public NodeImage getImage() {
        return img;
    }
}
