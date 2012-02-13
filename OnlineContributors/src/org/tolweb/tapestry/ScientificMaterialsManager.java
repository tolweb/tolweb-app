/*
 * Created on Feb 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ScientificMaterialsManager extends AbstractScientificContributorPage implements IExternalPage,
	ProjectInjectable, PageBeginRenderListener, UserInjectable, NodeInjectable, BaseInjectable {
    public abstract String getNodeName();
    /**
     * The projects this contributor is assigned to
     */
    @SuppressWarnings("unchecked")
    public abstract void setProjects(List value);
    @SuppressWarnings("unchecked")
    public abstract List getProjects();
    public abstract void setCurrentProject(Object[] value);
    public abstract Object[] getCurrentProject();
    
    public boolean getIsCoreContributor() {
    	Contributor contr = getContributor();
    	return contr.getContributorType() == Contributor.SCIENTIFIC_CONTRIBUTOR; 
    }
    
    public int getCurrentContributorId() {
    	return getContributor().getId();
    }
    public void activateExternalPage(Object[] params, IRequestCycle cycle) {
        Contributor contr = getCookieAndContributorSource().authenticateExternalPage(cycle); 
	    if (contr == null) {
	        throw new PageRedirectException("ScientificContributorsLogin");
	    }
    }
    
    @SuppressWarnings("unchecked")
    public List getNodeIds() {
        Contributor contr = getContributor();
        List nodeIds = getPermissionChecker().getPermissionsForContributor(contr);
        filterOutInactiveNodeIds(nodeIds);
        return nodeIds;
    }
    
    @SuppressWarnings("unchecked")
    private void filterOutInactiveNodeIds(List nodeIds) {
    	List<Long> toRemove = new ArrayList<Long> ();
    	for (Iterator itr = nodeIds.iterator(); itr.hasNext(); ) {
    		Long nodeId = (Long)itr.next();
    		MappedNode mnode = getWorkingNodeDAO().getNodeWithId(nodeId, true);
    		if (mnode != null && !mnode.getStatus().equals(MappedNode.ACTIVE)) {
    			toRemove.add(nodeId);
    		}
    	}
		for (Long remove : toRemove) {
			nodeIds.remove(remove);
		}
	}
	@SuppressWarnings("unchecked")
    public String getQuicklinksString() {
        StringBuilder returnString = new StringBuilder();
        for (Iterator iter = getNodeIds().iterator(); iter.hasNext();) {
            Long nextNodeId = (Long) iter.next();
            String nextName = getWorkingNodeDAO().getNameForNodeWithId(nextNodeId);
            String workingUrl = getUrlBuilder().getWorkingURLForObject(nextName);
            returnString.append("<a href=\"" + workingUrl + "\">" + nextName + "</a>");
            if (iter.hasNext()) {
            	returnString.append(",  ");
            }
        }
        return returnString.toString();
    }
    
    @SuppressWarnings("unchecked")
    public void doEditPageSearch(IRequestCycle cycle) {
        String nodeName = getNodeName();
        if (StringUtils.notEmpty(nodeName)) {
            List nodes = getWorkingNodeDAO().findNodesNamed(nodeName);
            if (nodes.size() == 1) {
                String url = getUrlBuilder().getWorkingURLForObject(((MappedNode) nodes.get(0)).getName());
                throw new RedirectException(url);
            } else {
                ScientificManagerNodesSearchResults resultsPage = (ScientificManagerNodesSearchResults) cycle.getPage("ScientificManagerNodesSearchResults");
                resultsPage.setNodes(nodes);
                cycle.activate(resultsPage);
            }
        }
    }
    public String getLoginPageName() {
        return Login.SCIENTIFIC_LOGIN_PAGE;
    }    
    public void editRegistrationInfo(IRequestCycle cycle) {
    	String pageName = getIsCoreContributor() ? "ScientificContributorRegistration" : "GeneralContributorRegistration";
        AbstractContributorRegistration registrationPage = (AbstractContributorRegistration) cycle.getPage(pageName);
        registrationPage.setEditedContributor(getContributor());
        cycle.activate(registrationPage);
    }
    
    public void pageBeginRender(PageEvent event) {
    	super.pageBeginRender(event);
    	setProjects(getProjectDAO().getProjectsForContributor(getContributor()));
    }
    /**
     * Indicates if the contributor is allowed to perform tasks that are 
     * related to the role of "editor."  
     * 
     * @return true, if the contributor is an editor; otherwise false.
     */
    public boolean getCanDoEditorTasks() {
    	return getPermissionChecker().isEditor(getContributor());
    }    
}
