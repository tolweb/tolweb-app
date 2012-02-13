/*
 * AuxiliaryChangedFromServerProvider.java
 *
 * Created on July 28, 2003, 4:57 PM
 */

package org.tolweb.treegrow.page;

/**
 * Interface that is used to monitor changes from server that aren't actually
 * changed on the object, but rather cause a containing object to change.  
 * Some examples of this are:
 *  <ul>
 *      <li>When an accessory page is changed, it needs to notify the containing
 *          page that its accessory pages have changed</li>
 *      <li>When an author is changed, it needs to notify the containing page
 *          that its authors have changed</li>
 *  </ul>
 */
public interface AuxiliaryChangedFromServerProvider extends ChangedFromServerProvider {
    public boolean auxiliaryChangedFromServer();
    public void setAuxiliaryChangedFromServer(boolean value);
}
