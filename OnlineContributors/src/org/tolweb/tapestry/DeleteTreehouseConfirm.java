/*
 * Created on Sep 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class DeleteTreehouseConfirm extends ReturnablePage implements AccessoryInjectable {
    public abstract void setTreehouse(MappedAccessoryPage treehouse);
    public abstract MappedAccessoryPage getTreehouse();
    
    public void deleteTreehouse(IRequestCycle cycle) {
        getWorkingAccessoryPageDAO().deleteAccessoryPage(getTreehouse());
        // Also delete it from public -- it might not exist
        try {
            getPublicAccessoryPageDAO().deleteAccessoryPage(getTreehouse());
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
        cycle.activate(getReturnPageName());
    }
    
    public void dontDeleteTreehouse(IRequestCycle cycle) {
        cycle.activate(getReturnPageName());
    }
    
    public String getPageType() {
        if (getIsTreehouse()) {
            return "Treehouse";
        } else {
            return "Page";
        }
    }
    
    public boolean getIsTreehouse() {
        return getReturnPageName().equals("TreehouseMaterialsManager");
    }
    
    public byte getWrapperType() {
        if (getIsTreehouse()) {
            return AbstractWrappablePage.LEARNING_WRAPPER;
        } else {
            return AbstractWrappablePage.DEFAULT_WRAPPER;
        }
    }
}
