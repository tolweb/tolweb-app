package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;

public abstract class EditDeletePageConfirm extends AbstractPageEditingPage {
    public abstract void setPageDeleted(boolean value);
    public abstract void setParentGroup(String value);
    public abstract String getParentGroup();
    
    public void deletePage(IRequestCycle cycle) {
        MappedPage page = getTolPage();
        MappedNode parentPageNode = getDAO().getNodeForPageNodeIsOn(page.getMappedNode()); 
        String groupName = "";
        if (parentPageNode != null) {
            groupName = parentPageNode.getName();
        }
        setParentGroup(groupName);
        getDAO().deletePageAndReassignNodes(page);
        setPageDeleted(true);
    }
    public void returnToEditing(IRequestCycle cycle) {
        goToOtherEditPage(cycle);
    }
    public String getWorkingPageUrl() {
        return "'" + getUrlBuilder().getWorkingURLForObject(getParentGroup()) + "'";
    }
}
