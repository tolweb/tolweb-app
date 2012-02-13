/*
 * FindNodesResults.java
 *
 * Created on May 5, 2004, 4:02 PM
 */

package org.tolweb.tapestry;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.AccessoryPageNode;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.UploadBuilder;

/**
 *
 * @author  dmandel
 */
public abstract class FindNodesResults extends ReturnablePage implements UserInjectable, NodeInjectable, PageInjectable, 
		TreeGrowServerInjectable, MiscInjectable, BaseInjectable, PageDetachListener {
    public static final int EDIT_IMAGE_CALLBACK = 0;
    public static final int EDIT_TREEHOUSE_CALLBACK = 1;
    public static final int EDIT_ARTICLE_NOTE_CALLBACK = 2;
    public static final int ADD_PORTFOLIO_PAGE_CALLBACK = 3;
    public static final int ADD_CONTRIBUTOR_PERMISSIONS_CALLBACK = 4;
    public static final int ADD_CONTRIBUTOR_PERMISSIONS_CUTOFF_CALLBACK = 7;    
    public static final int UPLOAD_NEW_NODES_CALLBACK = 5;
    public static final int MOVE_BRANCH_CALLBACK = 6;
    
    public abstract Long getSelectedNodeId();
    public abstract void setCallbackType(int value);
    public abstract int getCallbackType();
    public abstract void setSearchPageName(String value);
    public abstract String getSearchPageName();
    @SuppressWarnings("unchecked")
    public abstract Hashtable getAdditionalValues();
    @SuppressWarnings("unchecked")
    public abstract void setAdditionalValues(Hashtable value);
    @SuppressWarnings("unchecked")
    public abstract void setNodes(List value);
    @SuppressWarnings("unchecked")
    public abstract List getNodes();
    public abstract void setOnlySelectPagedNodes(boolean value);
    public abstract boolean getOnlySelectPagedNodes();
    public abstract void setHasPage(boolean value);
    public abstract void setEditedObjectId(Long value);
    public abstract Long getEditedObjectId();   
    public abstract boolean getCheckPossibleParent();
    public abstract void setCheckPossibleParent(boolean value);
    public abstract void setError(String value);
    public abstract String getError();
    public abstract MappedNode getNodeToMove();
    public abstract void setNodeToMove(MappedNode value);
    public abstract boolean getNoPublicPage();
    public abstract void setNoPublicPage(boolean value);
    public abstract String getContainingPageName();
    public abstract void setContainingPageName(String value);
    private MappedNode node;

    
    public void pageDetached(PageEvent event) {
    	node = null;
    }
    
    @SuppressWarnings("unchecked")
    public void searchAgain(IRequestCycle cycle) {
		IPage searchPage = cycle.getPage(getSearchPageName());
		if (FindNodes.class.isInstance(searchPage)) {
			((FindNodes) searchPage).doActivate(cycle, getWrapperType(), getReturnPageName(), getOnlySelectPagedNodes(), 
			        getEditedObjectId(), getCallbackType(), false);
		} else {
	        Hashtable additionalValues = getAdditionalValues();
	        getPagePropertySetter().setPageProperties(searchPage, additionalValues);
		    cycle.activate(searchPage);
		}
    }
    
    public void returnToEditing(IRequestCycle cycle) {
        IPage page = cycle.getPage(getReturnPageName());
        if (page instanceof EditIdPage) {
            ((EditIdPage) page).setEditedObjectId(getEditedObjectId());
        }
        cycle.activate(page);
    }
    
    @SuppressWarnings("unchecked")
    public IPage selectNode(IRequestCycle cycle) {
        Long nodeId = getSelectedNodeId();
        MappedNode toAdd = getMiscNodeDAO().getNodeWithId(nodeId);
        PageDAO dao = getWorkingPageDAO();
        if (getOnlySelectPagedNodes() && !(dao.getNodeHasPage(toAdd))) {
            // Choose the node of the containing page
            toAdd = dao.getPageWithId(toAdd.getPageId()).getMappedNode(); 
        }
        int callbackType = getCallbackType();
        if (callbackType == EDIT_ARTICLE_NOTE_CALLBACK || callbackType == EDIT_IMAGE_CALLBACK ||
                callbackType == EDIT_TREEHOUSE_CALLBACK || callbackType == ADD_CONTRIBUTOR_PERMISSIONS_CALLBACK) {
	        NodesSetPage editPage = (NodesSetPage) cycle.getPage(getReturnPageName());
	        IPage page = (IPage) editPage;
	        try {
	            PropertyUtils.write(page, "editedObjectId", getEditedObjectId());
	        } catch (Exception e) {}
	        // This isn't really the best way to express this, but it works for now
	        if (callbackType == EDIT_ARTICLE_NOTE_CALLBACK || callbackType == EDIT_TREEHOUSE_CALLBACK){
	            AccessoryPageNode node = new AccessoryPageNode();
	            node.setNode(toAdd);
	            node.setShowLink(true);
	            // If this is the first node to be added, have it be the
	            // default primary group of attachment
	            if (editPage.getNodesSet().size() == 0) {
	                node.setIsPrimaryAttachedNode(true);
	            }
	            editPage.getNodesSet().add(node);
	        } else {
	            editPage.getNodesSet().add(toAdd);
	        }
			if (editPage instanceof EditImageData) {
				editPage.setSelectedAnchor("attach");
				((EditImageData) editPage).saveImage();
			} else if (editPage instanceof TreehouseEditAttachments) {
			    editPage.setSelectedAnchor("trhs2attach");
			} else if (editPage instanceof EditArticleNote) {
			    editPage.setSelectedAnchor("articleattach");
			    ((EditArticleNote) editPage).saveAccessoryPage();
			}
			return page;
        } else if (callbackType == ADD_PORTFOLIO_PAGE_CALLBACK) {
            TreehouseEditPortfolio editPage = (TreehouseEditPortfolio) cycle.getPage(getReturnPageName());
            editPage.addToPortfolio(toAdd, cycle);
            return editPage;
        } else if (callbackType == UPLOAD_NEW_NODES_CALLBACK) {
        	UploadNewNodes uploadPage = (UploadNewNodes) cycle.getPage(getReturnPageName());
        	uploadPage.setNodeId(toAdd.getNodeId());
        	return uploadPage;
        } else if (callbackType == MOVE_BRANCH_CALLBACK) {
        	EditMoveBranch moveBranchPage = (EditMoveBranch) cycle.getPage("EditMoveBranch");
        	moveBranchPage.setEditedObjectId(getEditedObjectId());
        	UploadBuilder builder = getUploadBuilder();
        	MappedNode nodeToMove = getWorkingNodeDAO().getNodeWithId(getEditedObjectId());
        	builder.moveBranch(nodeToMove, toAdd, getContributor());
        	moveBranchPage.setNewParentName(toAdd.getName());
        	// verify that the target node
        	// This is where we would actually move the old node to its
        	// new parent and modify all of the node ancestors and page
        	// ancestors -- of course, we need to check if the node to be moved is a parent of the
        	// target and that the current parent has more than one child ... etc.  and then that the
        	// target node isn't downloaded in TreeGrow, etc.
        	return moveBranchPage;
        } else if (callbackType == ADD_CONTRIBUTOR_PERMISSIONS_CUTOFF_CALLBACK) {
        	ScientificContributorRegistrationOther registrationPage = getContrRegistrationPage();
        	registrationPage.getCutoffNodes().add(toAdd);
        	return registrationPage;
        }
        return null;
    }
    
    @InjectPage("ScientificContributorRegistrationOther")
    public abstract ScientificContributorRegistrationOther getContrRegistrationPage();
    
    /**
     * Cover method so we don't make the same db query back to back
     * @return
     */
    public boolean getNodeHasPage() {
        boolean result = getPublicPageDAO().getNodeHasPage(getNode());
        setHasPage(result);
        MappedPage page = getPublicPageDAO().getPageWithId(getNode().getPageId());
        setNoPublicPage(page == null);        
        return result;
    }
    
    public String getPreviewPageUrl() {
    	return getUrlBuilder().getURLForObject(getNode());
    }
    
    public String getContainingPageUrl() {
    	MappedPage page = getPublicPageDAO().getPage(getNode());
    	setContainingPageName(page.getMappedNode().getName());
    	return getUrlBuilder().getPublicURLForObject(page);
    }
    
    public String getContainingPageNodeName() {
        if (getNode() != null) {
            MappedNode node = getNode();
	        MappedPage page = getPublicPageDAO().getPageWithId(getNode().getPageId());
	        if (page != null) {
	            return page.getMappedNode().getName();
	        } else {
                MappedNode parentNode = getWorkingPageDAO().getNodeForPageNodeIsOn(node);
	            return parentNode.getName();
	        }
        } else {
            return "";
        }
    }
    
    @SuppressWarnings("unchecked")
    public IPrimaryKeyConvertor getConvertor() {
	    return new IPrimaryKeyConvertor() {
			public Object getPrimaryKey(Object objValue) {
				return ((MappedNode) objValue).getNodeId();
			}

			/**
			 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
			 */
			public Object getValue(Object objPrimaryKey) {
			    Long nodeId = (Long) objPrimaryKey;
			    for (Iterator iter = getNodes().iterator(); iter.hasNext();) {
		            MappedNode node = (MappedNode) iter.next();
		            Long nextId = node.getNodeId();
		            if (nextId.equals(nodeId)) {
		                return node;
		            }
		        }
			    return null;
			} 

	    };
	}
    public boolean getShowSelectLink() {
    	return !(getCheckPossibleParent() && StringUtils.notEmpty(getError()));
    }

    public MappedNode getNode() {
    	return node;
    }
	public void setNode(MappedNode node) {
		this.node = node;
		// this is a concrete property so we can do the necessary error check if we 
		// are examining possible new parents
		if (getCheckPossibleParent()) {
	    	if (getNodeToMove() == null) {
	    		NodeDAO dao = getWorkingNodeDAO();
	    		setNodeToMove(dao.getNodeWithId(getEditedObjectId()));
	    	}			
			UploadBuilder builder = getUploadBuilder();
			setError(builder.getIsLegalNewParent(getNodeToMove(), node));
		}
	}      
}
