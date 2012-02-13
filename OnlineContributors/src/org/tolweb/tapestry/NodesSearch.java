/*
 * Created on Jun 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class NodesSearch extends AbstractSearch implements UserInjectable, NodeInjectable,
		MiscInjectable {
    public abstract boolean getNoResults();
    public abstract void setNoResults(boolean value);
    public abstract void setOnlySelectPagedNodes(boolean value);
    public abstract boolean getOnlySelectPagedNodes();
    public abstract void setEditedObjectId(Long id);
    public abstract Long getEditedObjectId();
    public abstract String getGroupName();
	public abstract void setBodyStyle(String value);
	public abstract String getBodyStyle();
	public abstract boolean getRequireEditPermissions();
	@SuppressWarnings("unchecked")
	public abstract Hashtable getAdditionalValues();
    public abstract boolean getCheckPossibleParent();
	@Parameter(required = true)
	public abstract String getReturnPageName();    
    
	@SuppressWarnings("unchecked")
    public void doSearch(IRequestCycle cycle) {
        ValidationDelegate delegate = (ValidationDelegate) getBeans().getBean("delegate");
        if (delegate.getHasErrors()) {
            reinitializeProperties();
            return;
        } else {
            List nodes = getMiscNodeDAO().findNodesNamed(getGroupName());
            boolean checkPermissions = getRequireEditPermissions();
            if (checkPermissions) {
                PermissionChecker checker = getPermissionChecker();
                Contributor contr = getContributor();
                for (Iterator iter = new ArrayList(nodes).iterator(); iter.hasNext();) {
                    MappedNode node = (MappedNode) iter.next();
                    if (!checker.checkHasPermissionForNode(contr, node.getNodeId())) {
                        nodes.remove(node);
                    }
                }
            }            
            if (nodes == null || nodes.size() == 0) {
                setNoResults(true);
                reinitializeProperties();
                Hashtable additionalValues = getAdditionalValues();
                getPagePropertySetter().setPageProperties(getPage(), additionalValues);
                return;
            }
            FindNodesResults resultsPage = (FindNodesResults) cycle.getPage("FindNodesResults");
            resultsPage.setNodes(nodes);
            resultsPage.setWrapperType(getWrapperType());
            resultsPage.setReturnPageName(getReturnPageName());
            resultsPage.setOnlySelectPagedNodes(getOnlySelectPagedNodes());
            resultsPage.setEditedObjectId(getEditedObjectId());
            resultsPage.setCallbackType(getCallbackType());
            resultsPage.setSearchPageName(getPage().getPageName());
            resultsPage.setCheckPossibleParent(getCheckPossibleParent());
            resultsPage.setAdditionalValues(getAdditionalValues());
            //setOnlySelectPagedNodes(false);
            if (getBodyStyle() != null) {
                resultsPage.setBodyStyle(getBodyStyle());
            }
            cycle.activate(resultsPage);
        }
    }
    

	private void reinitializeProperties() {
        try {
            PropertyUtils.write(getPage(), "editedObjectId", getEditedObjectId());
        } catch (Exception e) {}
        try {
            PropertyUtils.write(getPage(), "wrapperType", Byte.valueOf(getWrapperType()));
        } catch (RuntimeException e1) {}
        try {
            PropertyUtils.write(getPage(), "returnPageName", getReturnPageName());
        } catch (Exception e2) {}
        try {
            PropertyUtils.write(getPage(), "onlySelectPagedNodes", Boolean.valueOf(getOnlySelectPagedNodes()));
        } catch (Exception e2) {}
        try {
            PropertyUtils.write(getPage(), "bodyStyle", getBodyStyle());
        } catch (Exception e2) {}
        try {
            PropertyUtils.write(getPage(), "callbackType", Integer.valueOf(getCallbackType()));
        } catch (Exception e2) {}   
        try {
            PropertyUtils.write(getPage(), "dontCheckPermissions", Boolean.valueOf(!getRequireEditPermissions()));
        } catch (Exception e2) {}        
    }
}
