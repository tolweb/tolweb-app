package org.tolweb.tapestry;

import org.apache.tapestry.annotations.Persist;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;

/**
 * Here for the pages that can operate on nodes without pages (the taxon names and media page)
 * @author dmandel
 *
 */
public abstract class AbstractNodeEditingPage extends AbstractPageEditingPage {
    @Persist("session")
	public abstract MappedNode getNode();
    public abstract void setNode(MappedNode value);	
    /**
     * overridden to return a parent page if the node doesn't have a page
     */
    public MappedPage getEditedPage() {
        if (getTolPage() != null) {
            return getTolPage();
        } else {
            return getDAO().getPageNodeIsOn(getNode());
        }   
    }
    /**
     * overridden since it's possible no page exists
     */
    public String getTolPageName() {
        return getNode().getActualPageTitle(false, false, true);
    } 
    /**
     * Overridden to do nothing because this is the one scenario where it is ok to 
     * not have a page in an editing form
     */
    protected void handleNoPageScenario() {}        
}
