/*
 * ServerChangeable.java
 *
 * Created on July 28, 2003, 4:55 PM
 */

package org.tolweb.treegrow.page;

/**
 * Interface that objects implement in order to track their state of having
 * been changed from the server.  This is used to automatically set objects to
 * changed during UndoableEdits.
 */
public interface ChangedFromServerProvider {
    public boolean changedFromServer();
    public void setChangedFromServer(boolean value);
}
