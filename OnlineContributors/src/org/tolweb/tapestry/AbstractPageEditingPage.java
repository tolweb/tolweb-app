/*
 * Created on May 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.SortedSet;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.ReorderHelper;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrowserver.Download;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractPageEditingPage extends BasePage implements IExternalPage, EditIdPage, NodeInjectable, PageInjectable,
		TreeGrowServerInjectable, MiscInjectable, UserInjectable, CookieInjectable, BaseInjectable {
	/*
	 * Tapestry synthesized properties
	 * @return
	 */
    public abstract String getError();
    public abstract void setError(String value);
    public abstract MappedPage getTolPage();
    public abstract void setTolPage(MappedPage value);
    public abstract boolean getPreviewSelected();
    public abstract void setPreviewSelected(boolean value);
    public abstract boolean getSubmitSelected();
    public abstract void setSubmitSelected(boolean value);
    public abstract String getOtherEditPageName();
    public abstract void setOtherEditPageName(String value);
    public abstract Integer getIndex();
    public abstract void setIndex(Integer value);
    public abstract MappedNode getNode();
    public abstract void setNode(MappedNode value);
    public abstract String getOtherEditUrl();
    public abstract void setOtherEditUrl(String value);
    public abstract Integer getMoveUpIndex();
    public abstract void setMoveUpIndex(Integer value);
    public abstract Integer getMoveDownIndex();
    public abstract void setMoveDownIndex(Integer value);
    public abstract Integer getDeleteIndex();
    public abstract void setDeleteIndex(Integer value);
    @Bean
    public abstract ValidationDelegate getValidationDelegate();

    public static final String SEPARATOR = ":";
    public static final String EDIT_TEXT_SECTION_PAGE = "EditTextSection";
    public static final String NEW_PAGE = "NewPage";
    
    protected void savePage() {
        getDAO().savePage(getTolPage());        
    }
    
    public BaseHTMLEditorDelegate getBaseDelegate() {
        return new ArticleNoteHTMLEditorDelegate(getNode().getNodeId());
    }
    
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
	    String nodeId = cycle.getInfrastructure().getRequest().getParameterValue(RequestParameters.NODE_ID);
	    if (nodeId == null) {
	        nodeId = parameters[2].toString();
	    }
        PermissionChecker checker = getPermissionChecker(); 
	    Contributor contr = getCookieAndContributorSource().authenticateExternalPage(cycle);
	    if (contr != null && checker.checkPermissionForNode(contr, contr.getPassword(), Long.valueOf(nodeId)) == null) {
            setupPageAndNode(Long.valueOf(nodeId));
            // only bother checking the checkout status if there hasn't been an error up to this point
            if (getError() == null) {
                String errorMessage = getCheckedOutMessage(contr);
                if (errorMessage != null) {
                    setError(errorMessage);
                } else {
                    // update the lock for the current contributor
                    updateLock(contr);
                }                
            }
	    } else {
	        setError("Your username and password appear to be incorrect or you do not have permissions to edit this page.");
	    }
	}
    
    /**
     * Constructs a message if the page is checked out to someone else,
     * returns null if it isn't checked out
     * @return
     */
    private String getCheckedOutMessage(Contributor contr) {
        PermissionChecker checker = getPermissionChecker();
        
        Contributor lockedContributor = checker.checkPageIsLocked(contr, getEditedPage()); 
        if (lockedContributor != null) {
        	boolean actuallySubmitted = getPermissionChecker().checkPageIsSubmitted(getEditedPage()); 
            boolean canEditSubmitted = !getPermissionChecker().checkPageIsSubmitted(contr, getEditedPage());        	
        	if (!actuallySubmitted || (actuallySubmitted && !canEditSubmitted)) {
        		return checker.getLockedMessageForEditHistory(getEditedPage().getEditHistoryId());
        	}
        }
        return null;
    }
    private void updateLock(Contributor contr) {
        getEditHistoryDAO().updateLock(getEditedPage().getEditHistoryId(), contr);
    }
    private void updateLastEdited(Contributor contr) {
        getEditHistoryDAO().updateLastEdited(getEditedPage().getEditHistoryId(), contr);
    }
    private void setupPageAndNode(Long nodeId) {
        MappedNode node = getNodeDAO().getNodeWithId(nodeId);
        if (node == null) {
            setError("We were unable to locate the node you were looking for.  If you are using TreeGrow and have created a new node, please do an upload and try accessing this page again.");
            throw new PageRedirectException(this);
        } 
        setNode(node);
        MappedPage page = getDAO().getPageForNode(node);
        if (page == null) {
            handleNoPageScenario();
        }
        setTolPage(page);        
    }
    
    protected void handleNoPageScenario() {
        setError("We were unable to locate a page for the specified node.");
    }
	
    public Long getEditedObjectId() {
        MappedNode node = getNode();
        return node != null ? node.getNodeId() : null;
    }
    
    public void setEditedObjectId(Long id) {
        if (id != null) {
            setupPageAndNode(id);
            doAdditionalPageProcessing(getTolPage());
        }
    }	
    
    protected void doAdditionalPageProcessing(MappedPage page) {}
	
	public PageDAO getDAO() {
	    return getWorkingPageDAO();
	} 
    public NodeDAO getNodeDAO() {
        return getWorkingNodeDAO();
    }
    
    public boolean getHasPublicPage() {
        MappedPage page = getEditedPage();
        return getPublicPageDAO().getPageExistsWithId(page.getPageId());
    }
    
	public void previewSubmit(IRequestCycle cycle) {
		setPreviewSelected(true);
	}
	
	public void publicationSubmit(IRequestCycle cycle) {
	    setSubmitSelected(true);
	}  
    
    public void doSave(IRequestCycle cycle) {
        if (getValidationDelegate().getHasErrors()) {
            return;
        }
        // if it's checked out to someone else, then don't proceed with the save
        String errorMessage = getCheckedOutMessage(getContributor());
        if (errorMessage != null) {
            setError(errorMessage);
            return;
        } else {
            updateLastEdited(getContributor());
        }
        handleReorderings();
        savePage();
        doEditedPageSave();
        if (getOtherEditUrl() != null) {
            throw new RedirectException(getOtherEditUrl());
        } else if (getSubmitSelected()) {
            UserSubmitPublication publicationPage = (UserSubmitPublication) cycle.getPage("UserSubmitPublication");
            // clear any cached batches so we see our latest work
            publicationPage.setBatches(null);
            cycle.activate(publicationPage);
        } else if (getOtherEditPageName() != null) {
            // if this is the create new page "page", then just go ahead and create the page
            if (getOtherEditPageName().equals(NEW_PAGE)) {
                MappedPage page = getDAO().addPageForNode(getNode(), getContributor());
                setTolPage(page);
            } else {
                goToOtherEditPage(cycle);
            }
        }
    }
    
    protected void goToOtherEditPage(IRequestCycle cycle) {
        Object[] params = getPageParameters();            
        AbstractPageEditingPage otherEditPage = (AbstractPageEditingPage) cycle.getPage(getOtherEditPageName());
        cycle.setListenerParameters(params);
        otherEditPage.activateExternalPage(params, cycle);
        // in the deletion case it needs a little extra info
        if (getOtherEditPageName().equals("EditDeletePageConfirm")) {
            otherEditPage.setOtherEditPageName(getPageName());
        }
        cycle.activate(otherEditPage);        
    }
    
    private void doEditedPageSave() {
        getEditedPageDAO().addEditedPageForContributor(getEditedPage(), getContributor());
    }
    
    public MappedPage getEditedPage() {
        return getTolPage();
    }
    
    @SuppressWarnings("unchecked")
    private void handleReorderings() {
        SortedSet set = getOrderedCollection();
        if (set != null) {
            ReorderHelper helper = getReorderHelper();
            if (getMoveUpIndex() != null) {
                helper.doSwap(getMoveUpIndex(), true, set);
            } else if (getMoveDownIndex() != null) {
                helper.doSwap(getMoveDownIndex(), false, set);
            } else if (getDeleteIndex() != null) {
                Object toDelete = helper.removeObject(getDeleteIndex(), set);
                doAdditionalDeletionIfNecessary(toDelete);
            }
        }
    }
    
    protected void doAdditionalDeletionIfNecessary(Object objectToDelete) {}
    
    @SuppressWarnings("unchecked")
    protected SortedSet getOrderedCollection() {
        return null;
    }
    /**
     * Returns an array of parameters to pass to the other editing pages
     * via the external service.  Contains three items
     * 1) contributor email
     * 2) contributor pass-hash
     * 3) node id of the page to edit
     * @return
     */
    public Object[] getPageParameters() {
        boolean isTextSection = getOtherEditPageName().startsWith(EDIT_TEXT_SECTION_PAGE); 
        Object[] params = new Object[isTextSection ? 4 : 3];
        params[0] = getContributor().getEmail();
        params[1] = getContributor().getPassword();
        params[2] = getNode().getNodeId();
        if (isTextSection) {
            String editTag = getOtherEditPageName();
            int separatorIndex = editTag.indexOf(SEPARATOR); 
            int index = Integer.parseInt(editTag.substring(separatorIndex + 1));
            params[3] = Integer.valueOf(index);
            setOtherEditPageName(EDIT_TEXT_SECTION_PAGE);
        }
        return params;
    }
    public String getWorkingUrl() {
        return getUrlBuilder().getWorkingURLForObject(getEditedPage());
    }
    
    public boolean getIsDownloadedInTreeGrow() {
        return getDownloadDAO().getNodeIsDownloaded(getNode().getNodeId());
    }
    
    public String getDownloadedMessage() {
        Download download = getDownloadDAO().getOpenDownloadForNode(getNode().getNodeId());
        if (download != null) {
            Contributor contr = download.getContributor();
            String name = contr.getDisplayName();
            String email = contr.getEmail();
            return "<p>The current node is checked out to [" + name + ", " + email + "] via <em>TreeGrow</em>.  You won't be able to add or delete the page for this node until it is checked back in.</p>";
        } else {
            return null;
        }
    }
    /**
     * returns the name of the page being edited
     * @return
     */
    public String getTolPageName() {
        return getNode().getActualPageTitle(false, false, true);
    }
}
