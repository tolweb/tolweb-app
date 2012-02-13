/*
 * Permission.java
 *
 * Created on February 16, 2004, 9:26 AM
 */

package org.tolweb.treegrow.main;

import org.tolweb.treegrow.page.*;

/**
 *
 * @author  dmandel
 */
public class Permission extends NodeAttachment implements ChangedFromServerProvider {
    private Contributor contributor;
    
    /** Creates a new instance of Permission */
    public Permission(int id, String nodeName, Contributor contr) {
        super(id, nodeName);
        contributor = contr;
    }
    
    public void setContributor(Contributor value) {
        contributor = value;
    }
    
    public Contributor getContributor() {
        return contributor;
    }
    
    public boolean changedFromServer() {
        return contributor.changedFromServer();
    }
    
    public void setChangedFromServer(boolean value) {
        contributor.setChangedFromServer(value);
    }
    
}
