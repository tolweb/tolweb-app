/*
 * Created on Jan 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.ListEditMap;
import org.tolweb.hibernate.AccessoryPageNode;
import org.tolweb.tapestry.injections.MiscInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreehouseEditAttachments extends AbstractTreehouseEditingPage implements PageBeginRenderListener, 
		MiscInjectable, NodesSetPage {
    public abstract void setSearchSelected(boolean value);
    public abstract boolean getSearchSelected();
    public abstract void setRemovedGroup(boolean value);
    public abstract boolean getRemovedGroup();
	public abstract void setNodesListEditMap(ListEditMap value);
	public abstract ListEditMap getNodesListEditMap(); 	
	public abstract void setCurrentNode(AccessoryPageNode value);
	public abstract AccessoryPageNode getCurrentNode();
	public abstract AccessoryPageNode getAccPageNodeToDelete();
	public abstract void setAccPageNodeToDelete(AccessoryPageNode value);
	public static final String PROGRESS_PROPERTY = "attachProgress"; 
	
	public boolean doAdditionalFormProcessing(IRequestCycle cycle) {
	    if (getSearchSelected()) {
			FindNodes findNodesPage = (FindNodes) cycle.getPage("FindNodes");
			findNodesPage.doActivate(cycle, AbstractWrappablePage.TREEHOUSE_WRAPPER, "TreehouseEditAttachments", 
			        true, null, FindNodesResults.EDIT_TREEHOUSE_CALLBACK, false);
			return true;
	    } else if (getRemovedGroup()) {
	        removeNode();
	    }
	    return false;
	}
	
	public int getStepNumber() {
	    if (getIsWebquest()) {
	        return 9;
	    } else if (getIsTeacherResource() || getIsPortfolio()) {
	        if (getIsOther() || getIsPortfolio()) {
	            return 7;
	        } else {
	            return 8;
	        }
	    } else {
	        return 6;
	    }
	}
	
	public void pageBeginRender(PageEvent event) {
	    initNodesMap();	    
	}
	
	@SuppressWarnings("unchecked")
	private void initNodesMap() {
	    ListEditMap nodesMap = new ListEditMap();
	    Iterator it = getTreehouse().getNodesSet().iterator();
	    while (it.hasNext()) {
	        AccessoryPageNode node = (AccessoryPageNode) it.next();
	        nodesMap.add(node.getNode().getNodeId(), node);
	    }
	    setNodesListEditMap(nodesMap);
	}
	
	public void synchronizeNode(IRequestCycle cycle) {
	    ListEditMap map = getNodesListEditMap();
	    AccessoryPageNode node = (AccessoryPageNode) map.getValue();
	    if (node == null) {
	        //displaySynchError();
	    }
	    setCurrentNode(node);
	}
	
	@SuppressWarnings("unchecked")
	public Set getNodesSet() {
		return getTreehouse().getNodesSet();
	}
	
	@SuppressWarnings("unchecked")
	protected void removeNode() {
	    AccessoryPageNode node = getAccPageNodeToDelete();
	    Long nodeId = node.getNode().getNodeId();
	    Iterator it = new HashSet(getTreehouse().getNodesSet()).iterator();
	    while (it.hasNext()) {
	        AccessoryPageNode nextNode = (AccessoryPageNode) it.next();
	        if (nextNode.getNode().getNodeId().equals(nodeId)) {
	            getTreehouse().getNodesSet().remove(nextNode);
	            setSelectedAnchor("trhs2attach");
	            return;
	        }
	    }
	}
	
	public void setPrimaryGroup(Long value) {
	    getReorderHelper().setPrimaryGroup(getTreehouse().getNodesSet(), value);
	}
	
	public Long getPrimaryGroup() {
	    return getReorderHelper().getPrimaryGroup(getTreehouse().getNodesSet());
	}
	
	public void removeGroup(IRequestCycle cycle) {
		setRemovedGroup(true);
		return;
	}
	
	public void addGroupSubmit(IRequestCycle cycle) {
		setSearchSelected(true);
	}	
	
	public String getProgressMethodPropertyName() {
	    return PROGRESS_PROPERTY;
	}		
}
