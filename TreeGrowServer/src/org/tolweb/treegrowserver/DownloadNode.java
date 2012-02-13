/*
 * Created on Oct 1, 2004
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
public class DownloadNode extends AbstractNodeWrapper {
	public static final int NOT_ACTIVE = 0;
	public static final int ACTIVE = 1;
	public static final int BARRIER_ACTIVE = 2;
    private int active;
    private boolean wasDeleted;
    
    /**
     * @hibernate.property column="active"
     * @return
     */
    public int getActive() {
        return active;
    }
    public void setActive(int active) {
        this.active = active;
    }
    /**
     * @hibernate.property column="deleted"
     * @return Returns the wasDeleted.
     */
    public boolean getWasDeleted() {
        return wasDeleted;
    }
    /**
     * @param wasDeleted The wasDeleted to set.
     */
    public void setWasDeleted(boolean wasDeleted) {
        this.wasDeleted = wasDeleted;
    }
}
