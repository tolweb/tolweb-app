/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageValidateListener;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.link.DefaultLinkRenderer;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.AccessoryPageNode;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.page.AccessoryPageContributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditArticleNote extends AbstractEditPage implements PageBeginRenderListener, PageValidateListener,
		EditIdPage, NodesSetPage, IExternalPage, UserInjectable, AccessoryInjectable, TreeGrowServerInjectable,
		MiscInjectable, BaseInjectable {
    public static final String INITIAL_INTERNET_LINKS = "<ul><li><a href=\"http://url\">Name of Site 1</a>. Description for Site 1.</li><li><a href=\"http://url\">Name of Site 2</a>. Description for Site 2.</li></ul>";
    public abstract int getIndex();
    public abstract void setStaleForm(boolean value);
    public abstract void setSubmitSelected(boolean value);
    public abstract boolean getSubmitSelected();
    public abstract boolean getRememberSelected();
    
    public DefaultLinkRenderer getImagesManagerRenderer() {
    	return getRenderer("imagesManagerWindow");
    }
    public DefaultLinkRenderer getImagesHelpRenderer() {
    	return getRenderer("imagesHelpWindow");
    }    
    private DefaultLinkRenderer getRenderer(String windowName) {
    	return getRendererFactory().getLinkRenderer(windowName, 800, 500);
    }
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
	    String pageId = cycle.getInfrastructure().getRequest().getParameterValue(RequestParameters.PAGE_ID);
	    
	    // Allow page invocation via servlet-style (above) or tapestry style parameter passing
	    if (pageId == null) {
	        pageId = ((Long) parameters[2]).toString();
	    }
	    Contributor contr = getCookieAndContributorSource().authenticateExternalPage(cycle);
	    PermissionChecker checker = getPermissionChecker();
	    if (contr != null) {
	        MappedAccessoryPage page = getWorkingAccessoryPageDAO().getAccessoryPageWithId(Long.valueOf(pageId));
	        if (page != null) {
	            if (!page.getIsSubmitted()) {
		            if (checker.checkEditingPermissionForPage(contr, page)) {
		                setAccPage(page);
		            } else {
		                setError("You have no editing permission for that page");
		            }
	            } else {
	                if (checker.checkEditingPermissionForSubmittedPage(contr, page)) {
	                    setAccPage(page);
	                } else {
	                    setError("This page has been submitted for publication.  You will not be able to edit this page until the editor has finished editing it.");
	                }
	            }
	        } else {
	            setError("There is no page with that id.");
	        }
	    } else {
	        setError("Your username and password appear to be incorrect.");
	    }
    }
    
	public void setPrimaryGroup(Long value) {
	    getReorderHelper().setPrimaryGroup(getAccPage().getNodesSet(), value);
	}
	
	public Long getPrimaryGroup() {
	    return getReorderHelper().getPrimaryGroup(getAccPage().getNodesSet());
	}
	
	public boolean getUseEditor() {
	    return !getContributor().getDontUseEditor();
	}
	
	public void setUseEditor(boolean value) {
	    getContributor().setDontUseEditor(!value);
	}
    
	public IRender getDelegate() {
	    if (getUseHTMLEditor()) {
	        return new ArticleNoteHTMLEditorDelegate(null);
	    } else {
	        return null;
	    }
	}
	   
    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        if (!event.getRequestCycle().isRewinding()) {
        	getAccessoryPageHelper().establishEditorUseInRequestCycle();
        }
    }
    	
	public boolean getUseHTMLEditor() {
		return getAccessoryPageHelper().getShouldUseEditor();
	}    
    
    public abstract Integer getMoveUpIndex();
    public abstract Integer getMoveDownIndex();
    public abstract Integer getDeleteIndex();
    public abstract Long getNodeIdToDelete();
    public abstract void setError(String value);
    public abstract void setPreviewSelected(boolean value);
    public abstract boolean getPreviewSelected();

	public void pageValidate(PageEvent event) {
	}
	
	public void previewSubmit(IRequestCycle cycle) {
		setPreviewSelected(true);
	}	
	
	public void doSave(IRequestCycle cycle) {
	    String contributorsAnchor = "articlecredits";
	    saveAccessoryPage();
		if (getRememberSelected()) {
		    getUsePermissionHelper().saveContributorUsePermissionDefault(getContributor(), getAccPage(), false);
		}
	    if (getSubmitSelected()) {
			ArticleNoteDataConfirm page = (ArticleNoteDataConfirm) cycle.getPage("ArticleNoteDataConfirm");
			page.setTreehouse(getAccPage());
			cycle.activate(page);
	    } else if (getContributorSearchSelected()) {
	        ContributorSearchPage searchPage = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
	        searchPage.doActivate(cycle, AbstractWrappablePage.TREEHOUSE_WRAPPER, "EditArticleNote", ((byte) 0), getEditedObjectId());
	    } else if (getMoveUpIndex() != null) {
	        getReorderHelper().doSwap(getMoveUpIndex().intValue(), true,	getAccPage().getContributors());
	        setSelectedAnchor(contributorsAnchor);
	    } else if (getMoveDownIndex() != null) {
	        getReorderHelper().doSwap(getMoveDownIndex().intValue(), false, getAccPage().getContributors());
	        setSelectedAnchor(contributorsAnchor);	        
	    } else if (getDeleteIndex() != null) {
	        getReorderHelper().removeObject(getDeleteIndex().intValue(), getAccPage().getContributors());
	        setSelectedAnchor(contributorsAnchor);	        
	    } else if (getNodeIdToDelete() != null) {
	        removeNode();
	        setSelectedAnchor("articleattach");
	    } else if (getSearchSelected()) {
	        FindNodes searchPage = (FindNodes) cycle.getPage("FindNodes");
	        searchPage.doActivate(cycle, AbstractWrappablePage.NEW_FORM_WRAPPER, "EditArticleNote", true, 
	                getEditedObjectId(), FindNodesResults.EDIT_ARTICLE_NOTE_CALLBACK, false);
	    }
	    saveAccessoryPage();
	}
	
	@SuppressWarnings("unchecked")
	private void removeNode() {
	    Long nodeId = getNodeIdToDelete();
	    AccessoryPageNode nodeToDelete = null;
	    for (Iterator iter = getAccPage().getNodesSet().iterator(); iter.hasNext();) {
            AccessoryPageNode node = (AccessoryPageNode) iter.next();
            if (node.getNode().getNodeId().equals(nodeId)) {
                nodeToDelete = node;
                break;
            }
        }
	    if (nodeToDelete != null) {
	        getAccPage().getNodesSet().remove(nodeToDelete);
	    }
	}
	
    public boolean getCanMoveUp() {
        return getReorderHelper().getCanMoveLeft(getAccPage().getContributors(), getIndex());
    }
    
    public boolean getCanMoveDown() {
        return getReorderHelper().getCanMoveRight(getAccPage().getContributors(), getIndex());
    }
    
    public boolean getCanMoveBoth() {
        return getReorderHelper().getCanMoveBoth(getAccPage().getContributors(), getIndex());
    }	
	
	public String getCurrentRowspan() {
	    if (getCanMoveBoth()) {
	        return "2";
	    } else {
	        return null;
	    }
	}
	
	public String getMoveUpOrDownClass() {
	    if (getCanMoveBoth()) {
	        return "up";
	    } else {
	        return null;
	    }
	}
	
	public String getMoveUpClass() {
	    if (getCanMoveUp()) {
	        return "tinytext";
	    } else {
	        return null;
	    }
	}
    
    public String getWorkingUrl() {
        return getUrlBuilder().getWorkingURLForObject(getAccPage());
    }
    
    @SuppressWarnings("unchecked")
    public Set getNodesSet() {
        return getAccPage().getNodesSet();
    }
    
	
	public Object getEditedObject() {
		return getAccPage();
	}    
    
    public abstract MappedAccessoryPage getAccPage();
    public abstract void setAccPage(MappedAccessoryPage value);
    
    public Long getEditedObjectId() {
        MappedAccessoryPage page = getAccPage();
        return page != null ? page.getAccessoryPageId() : null;
    }
    
    public void doImageSearch(IRequestCycle cycle) {
        ArticleNoteImageSearch searchPage = (ArticleNoteImageSearch) cycle.getPage("ArticleNoteImageSearch");
        Long editedObjectId = getEditedObjectId();
        searchPage.setPageId(editedObjectId);
        searchPage.setIsPageSearch(true);
        cycle.activate(searchPage);
    }
    
    public void setEditedObjectId(Long id) {
        if (id != null) {
            setAccPage(getDAO().getAccessoryPageWithId(id));
        } else {
            setAccPage(new MappedAccessoryPage());
        }
    } 
    
    public void addContributor(AccessoryPageContributor contr) {
        getAccPage().addToContributors(contr);
        saveAccessoryPage();
    }
    
    public AccessoryPageDAO getDAO() {
        return getWorkingAccessoryPageDAO();
    }

    public void saveAccessoryPage() {
        MappedAccessoryPage page = getAccPage(); 
        getDAO().saveAccessoryPage(page);
        getEditedPageDAO().addEditedPageForContributor(page, getContributor());        
    }
    
	public IPrimaryKeyConvertor getContributorConvertor() {
		return new ContributorConvertor();
	}    
	
	public IPrimaryKeyConvertor getNodeConvertor() {
	    return new NodeConvertor();
	}
	
	public String getTitle() {
	    if (getAccPage() != null) {
	        return "Edit " + getAccPage().getPageTitle();
	    } else {
	        return "Error";
	    }
	}
	
    public void submitSelected(IRequestCycle cycle) {
        setSubmitSelected(true);
    }
	
	private Object displayOutOfDateError() {
	    setStaleForm(true);
		throw new PageRedirectException(this);
	}
	
	public IPropertySelectionModel getSelectionModel() {
	    return new IPropertySelectionModel() {
	        public int getOptionCount() {
	            return 2;
	        }
	        
	        public Object getOption(int index) {
	            if (index == 0) {
	                return Boolean.valueOf(false);
	            } else {
	                return Boolean.valueOf(true);
	            }
	        }
	        
	        public String getLabel(int index) {
	            if (index == 0) {
	                return "Minor Modification Only";
	            } else {
	                return "Modification Permitted";
	            }
	        }
	        
	        public String getValue(int index) {
	            return Integer.toString(index);
	        }
	        
	        public Object translateValue(String value) {
	            int index = Integer.parseInt(value);
	            return getOption(index);
	        }
	    };
	}
	
	public boolean getTolModificationDisabled() {
	    return !(getAccPage().getUsePermission() == NodeImage.TOL_USE);
	}
	
	public boolean getShareModificationDisabled() {
	    return !(getAccPage().getUsePermission() == NodeImage.EVERYWHERE_USE);
	}

	private class ContributorConvertor implements IPrimaryKeyConvertor {
		public Object getPrimaryKey(Object objValue) {
			return Integer.valueOf(((AccessoryPageContributor) objValue).getOrder());
		}

		/**
		 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		public Object getValue(Object objPrimaryKey) {
		    Integer order = (Integer) objPrimaryKey;
		    for (Iterator iter = getAccPage().getContributors().iterator(); iter.hasNext();) {
	            AccessoryPageContributor contr = (AccessoryPageContributor) iter.next();
	            Integer nextOrder = Integer.valueOf(contr.getOrder());
	            if (nextOrder.equals(order)) {
	                return contr;
	            }
	        }
		    return displayOutOfDateError();
		} 
	}
	
	private class NodeConvertor implements IPrimaryKeyConvertor {
		public Object getPrimaryKey(Object objValue) {
			return ((AccessoryPageNode) objValue).getNode().getNodeId();
		}

		/**
		 * @see org.apache.tapestry.contrib.table.model.IPrimaryKeyConvertor#getValue(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		public Object getValue(Object objPrimaryKey) {
		    Long nodeId = (Long) objPrimaryKey;
		    for (Iterator iter = getAccPage().getNodesSet().iterator(); iter.hasNext();) {
	            AccessoryPageNode node = (AccessoryPageNode) iter.next();
	            Long nextNodeId = node.getNode().getNodeId();
	            if (nextNodeId.equals(nodeId)) {
	                return node;
	            }
	        }
		    return displayOutOfDateError();
		} 
		
		public IPrimaryKeyConvertor getConvertor() {
			return this;
		}
	    
	}
}
