/*
 * AccessoryPageContributor.java
 *
 * Created on March 1, 2004, 1:43 PM
 */

package org.tolweb.treegrow.page;

import java.io.Serializable;

/**
 * @author  dmandel
 */
public class AccessoryPageContributor extends PageContributor  implements Cloneable, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5889611386292417425L;
	private AccessoryPage accPage;
    
	public Object clone() {
			AccessoryPageContributor contr = (AccessoryPageContributor) super.clone();
			return contr;
	}    
    
    /** Creates a new instance of AccessoryPageContributor */
    public AccessoryPageContributor(AccessoryPage p, int contId, boolean author, boolean corr, boolean copy, int order) {
        super(null, contId, author, corr, copy, order);
        accPage = p;
    }
    
    public AccessoryPageContributor() {
    	super();
    }
    
    public void setAccessoryPage(AccessoryPage page) {
    	accPage = page;
    }
    
    /**
     * @hibernate.parent name="accessoryPage"
     * @return
     */
    public AccessoryPage getAccessoryPage() {
    	return accPage;
    }
    
    /**
     * Returns whether the page this author is attached to thinks that 
     * its authors have changed
     * 
     * @return Whether the page thinks its authors have changed
     */
    public boolean auxiliaryChangedFromServer() {   
        return accPage.changedFromServer();
    }

    public void setAuxiliaryChangedFromServer(boolean value) {
        accPage.setChangedFromServer(value);
    }
}
