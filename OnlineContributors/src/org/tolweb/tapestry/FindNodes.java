/*
 * FindNodesForImage.java
 *
 * Created on May 5, 2004, 3:10 PM
 */

package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;


/**
 *
 * @author  dmandel
 */
public abstract class FindNodes extends ReturnablePage {
    
    public void doActivate(IRequestCycle cycle, byte wrapperType, String returnPageName, boolean onlySelectPagedNodes, 
            Long editedObjectId, int callbackType, boolean requireEditPermissions) {
        setWrapperType(wrapperType);
        setReturnPageName(returnPageName);
        setOnlySelectPagedNodes(onlySelectPagedNodes);
        setEditedObjectId(editedObjectId);
        setCallbackType(callbackType);
        setRequireEditPermissions(requireEditPermissions);
        cycle.activate(this);
    }
    
    public abstract boolean getNoMatches();
    public abstract void setNoMatches(boolean value);
    public abstract void setOnlySelectPagedNodes(boolean value);
    @Persist("client")
    public abstract boolean getOnlySelectPagedNodes();
    public abstract void setEditedObjectId(Long id);
    @Persist("client")
    public abstract Long getEditedObjectId();
    public abstract void setCallbackType(int value);
    @Persist("client")
    public abstract int getCallbackType();
    public abstract void setRequireEditPermissions(boolean value);
    @Persist("client")
    public abstract boolean getRequireEditPermissions();
}
