/*
 * Created on Oct 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.web.WebRequest;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PagePusher;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.misc.CacheAccess;
import org.tolweb.misc.URLBuilder;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.BatchPusher;
import org.tolweb.treegrowserver.BatchSubmitter;
import org.tolweb.treegrowserver.PublicationBatch;
import org.tolweb.treegrowserver.SubmittedPage;
import org.tolweb.treegrowserver.dao.PublicationBatchDAO;

/**
 * @author dmandel
 *
 */
public abstract class ViewBranchOrLeaf extends AbstractBranchOrLeafPage implements IExternalPage, PageBeginRenderListener,
		CookieInjectable, NodeInjectable, PageInjectable, UserInjectable, TreeGrowServerInjectable {
	@SuppressWarnings("unchecked")
    public abstract void setRandomPics(List value);
	@SuppressWarnings("unchecked")
    public abstract List getRandomPics();
    public abstract void setStartTime(long value);
    public abstract long getStartTime();
    public abstract void setTillusSubmitted(boolean value);
    public abstract boolean getTillusSubmitted();
    public abstract void setPublicationSelected(boolean value);
    public abstract boolean getPublicationSelected();
    public abstract void setPublicationError(boolean value);
    public abstract void setPushedPublic(boolean value);
    public abstract void setPublicationBatchId(Long value);
    public abstract int getRevisionType();
    public abstract void setRevisionType(int value);
    public abstract Long getPageId();
    public abstract void setActualNodeAttachmentName(String value);
    public abstract Contributor getCurrentContributor();
    public abstract String getNodeWithNewPageName();
    public abstract void setNodeWithNewPageName(String value);
    

    private static final String CAN_EDIT = "can_edit";
    private static final String LOCKED = "locked";
    private static final String SUBMITTED = "submitted";
    private static final String CONTR_LIST_ANCHOR = "contributorlist";
    private RevisionTypeModel revisionTypeModel = new RevisionTypeModel();
    
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		super.activateExternalPage(parameters, cycle);
	    if (getRandomPicsEnabled()) {
	        setRandomPics(getPageDAO().getRandomPicsForPage(getTolPage(), false));
	    }
	    
        String subgroups = getWebRequest().getParameterValue(RequestParameters.OPEN_SUBGROUPS);
        if (StringUtils.notEmpty(subgroups)) {
            setOpenSubgroups(true);
        }	
        
	}
	
	@InjectObject("spring:permissionChecker")
	public abstract PermissionChecker getPermissionChecker();
	@InjectObject("spring:urlBuilder")
	public abstract URLBuilder getUrlBuilder();
    @InjectObject("spring:publicationBatchDAO")
    public abstract PublicationBatchDAO getPublicationBatchDAO();
    @InjectObject("infrastructure:request")
    public abstract WebRequest getWebRequest();
    
    
	public boolean getContributorHasPermissions() {
        PermissionChecker checker = getPermissionChecker();
        boolean canEdit = checker.checkHasPermissionForNode(getEditingContributor(), getNode().getNodeId());		
		return canEdit;
	}
	
	public boolean getCanAddPerson() {
		return getContributorHasPermissions() && getEditingContributor().getWillingToCoordinate(); 
	} 
    
    public void pageBeginRender(PageEvent event) {
        setupRequestCycleAttributes();
        if (!getRequestCycle().isRewinding()) {
	        boolean canEdit = false;
	        if (getIsWorking()) {
	            Contributor contr = getEditingContributor();
	            if (contr != null) {
	                canEdit = getContributorHasPermissions();
	                // don't show edit links if they have the preference turned off
	                canEdit = canEdit  
	                	&& !getCookieAndContributorSource().getCookieIsEnabled(ContributorPreferencesMenu.PREVIEW_MODE_COOKIE_NAME)
	                	&& !getCookieAndContributorSource().getCookieIsEnabled(ContributorPreferencesMenu.EDIT_LINK_COOKIE_NAME);
	            } else {
	            	redirectToScientificContributorsLogin(getNode());
	            	return;
	            }
	            boolean canView = contr.getContributorType() == Contributor.SCIENTIFIC_CONTRIBUTOR || 
	            	contr.getContributorType() == Contributor.REVIEWER;
	            if (!canView) {
	            	// don't let them look at the page -- redirect to public
	            	NoPermissionsPage page = (NoPermissionsPage) event.getRequestCycle().getPage("NoPermissionsPage");
	            	page.setPreviousNode(getNode());
	            	throw new PageRedirectException(page);
	            } else {
	            	boolean actuallySubmitted = getPermissionChecker().checkPageIsSubmitted(getTolPage()); 
	                boolean canEditSubmitted = !getPermissionChecker().checkPageIsSubmitted(contr, getTolPage());
	                boolean nonEditorSubmitLock = actuallySubmitted && !canEditSubmitted; 
	                if (nonEditorSubmitLock) {
	                    getRequestCycle().setAttribute(SUBMITTED, true);
	                }	
	                // only check the edit lock if it isn't submitted
	                if (!actuallySubmitted) {
		                Contributor lockedContributor = getPermissionChecker().checkPageIsLocked(contr, getTolPage());
		                if (lockedContributor != null) {
		                    getRequestCycle().setAttribute(LOCKED, true);
		                }
	                }
	            }
	        }
	        getRequestCycle().setAttribute(CAN_EDIT, canEdit);
	        setRevisionType(SubmittedPage.REGULAR_REVISION);
        }
    }
	
    @SuppressWarnings("unchecked")
	public Collection getContributors() {
	    return getTolPage().getContributors();
	}
	
	@InjectObject("spring:publicPageDAO")
	public abstract PageDAO getPublicPageDAO();
    
    public boolean getCanPublishIllustrations() {
        return getPublicPageDAO().getPageExistsWithId(getTolPage().getPageId());
    }
    
    public boolean getAddExtraPForContributors() {
        boolean isWorking = getIsWorking();
        return isWorking && StringUtils.isEmpty(getAuthorsString()); 
    }	
	
	public boolean getInternetInfoOnTop() {
	    return getCookieAndContributorSource().getCookieIsEnabled(CookieAndContributorSource.INET_INFO_COOKIE);
	}

	public String getPublicURL() {
		return getUrlBuilder().getPublicURLForObject(getTolPage());
	}
	
	public boolean getHasRandomPics() {
	    return getRandomPicsEnabled() && (getRandomPics().size() > 0);
	}
	
	public boolean getRandomPicsEnabled() {
	    return getCookieAndContributorSource().getCookieIsEnabled(CookieAndContributorSource.RANDOM_IMAGES_COOKIE);
	}
	public boolean getHasUpload() {
	    return false;
	}
    public Object[] getEditPageParameters() {
        return getParams(getNode().getNodeId());
    }
    public boolean getCanEdit() {
        return ((Boolean) getRequestCycle().getAttribute(CAN_EDIT)).booleanValue() &&
            !getIsLocked() && !getIsSubmitted();
    }
    public boolean getIsLocked() {
        Boolean locked = (Boolean) getRequestCycle().getAttribute(LOCKED);        
        return locked != null && locked.booleanValue();
    }
    public boolean getIsSubmitted() {
        Boolean submitted = (Boolean) getRequestCycle().getAttribute(SUBMITTED);        
        return submitted != null && submitted.booleanValue();        
    }
    public String getLockedMessage() {
        return getPermissionChecker().getLockedMessageForEditHistory(getTolPage().getEditHistoryId());
    }
    
    public String getSubmittedMessage() {
        PublicationBatchDAO dao = getPublicationBatchDAO();
        PublicationBatch batch = dao.getBatchForSubmittedNode(getNode().getNodeId());
        if (batch != null) {
            String message = "This page was submitted by " + batch.getSubmittedContributor().getDisplayName() + 
                " (" + batch.getSubmittedContributor().getEmail() + ") on " + StringUtils.getGMTDateString(batch.getSubmissionDate()); 
            return message;
        } else {
            return "";
        }
    }

    public void publishTitleIllustrations(IRequestCycle cycle) {
        setupRequestCycleAttributes();        
        MappedPage tolPage = getPageFromRequestCycle(cycle);
        getPagePusher().pushTitleIllustrationsToDB(tolPage, getPublicPageDAO());
        getCacheAccess().evictAllPageObjectsFromCache(tolPage);  
        setTillusSubmitted(true);
        setTolPage(tolPage);
        redirectToThisPage(cycle);
    }
    
    @InjectObject("spring:pagePusher")
    public abstract PagePusher getPagePusher();
    @InjectObject("spring:cacheAccess")
    public abstract CacheAccess getCacheAccess();
    @InjectObject("spring:workingPageDAO")
    public abstract PageDAO getWorkingPageDAO();
    @InjectObject("spring:batchSubmitter")
    public abstract BatchSubmitter getBatchSubmitter();
    @InjectObject("spring:batchPusher")
    public abstract BatchPusher getBatchPusher();
    
    public void publishPage(IRequestCycle cycle) {
        Long pageId = getPageId();
        MappedPage tolPage = getWorkingPageDAO().getPageWithId(pageId);
        setTolPage(tolPage);
        BatchSubmitter submitter = getBatchSubmitter();
        BatchPusher pusher = getBatchPusher();
        Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
        int revisionType = getRevisionType();
        PublicationBatch batch = submitter.submitPageForPublication(tolPage, contr, revisionType);
        int pushResult = pusher.conditionallyPushBatchToPublic(batch, contr);
        switch (pushResult) {
            case BatchPusher.NO_PERMISSIONS: 
                setPublicationSelected(true); 
                setPublicationBatchId(batch.getId());
                break;
            case BatchPusher.PUSH_PUBLIC_ERROR: setPublicationError(true); break;
            case BatchPusher.PUSH_PUBLIC_SUCCESSFUL: setPushedPublic(true); break;
        };
        redirectToThisPage(cycle);
    }
	public RevisionTypeModel getRevisionTypeModel() {
		return revisionTypeModel;
	}
	public void setRevisionTypeModel(RevisionTypeModel revisionTypeModel) {
		this.revisionTypeModel = revisionTypeModel;
	}
	public void gotoEditPage(IRequestCycle cycle) {
		Long nodeId = Long.valueOf(1L);
		if (cycle.getListenerParameters() != null) {
			nodeId = (Long) cycle.getListenerParameters()[0];
		}
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		boolean canEdit = getPermissionChecker().checkHasPermissionForNode(contr, nodeId);
		MappedNode node = getWorkingNodeDAO().getNodeWithId(nodeId);		
		if (contr != null && canEdit) {
			String workingUrl = getUrlBuilder().getWorkingURLForObject(node);
			throw new RedirectException(workingUrl);
		} else {
			redirectToScientificContributorsLogin(node);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List getContributorsWithPermissions() {
		PermissionChecker checker = getPermissionChecker();
		return getContributorDAO().getContributorsWithReadWritePermissionForNode(getNode().getNodeId(), checker);
	}
	public void addContributor(IRequestCycle cycle) {
		NewGroupAttachmentContributorSearchPage searchPage = (NewGroupAttachmentContributorSearchPage) cycle.getPage("NewGroupAttachmentContributorSearchPage");
		searchPage.addNodeToContributor(cycle, (Long) cycle.getListenerParameters()[0]);
	}
	public Boolean getCanRemove() {
		PermissionChecker checker = getPermissionChecker();
		boolean canEdit = checker.isEditor(getEditingContributor());
		if (canEdit) {
			// check to see if the contributor is actually attached to this node
			Object actuallyAttached = getPermissionChecker().checkIsAttachedToNode(getEditingContributor().getId(), 
					getNode().getNodeId(), true);
			if (Boolean.class.isInstance(actuallyAttached)) {
				return (Boolean) actuallyAttached;
			} else {
				// in this case, it's the node name they are actually attached to, so
				// set the node name property
				setActualNodeAttachmentName((String) actuallyAttached);
			}
		}
		return false;
	}
	public void clearAttachmentName(IRequestCycle cycle) {
		setActualNodeAttachmentName(null);
	}
	public void removePermissionFromContributor(IRequestCycle cycle) {
		setupRequestCycleAttributes();
		Long nodeId = (Long) cycle.getListenerParameters()[0];
		Integer contributorId = (Integer) cycle.getListenerParameters()[1];
		getPermissionChecker().removeNodeAttachmentForContributor(contributorId, nodeId);
		MappedNode node = getWorkingNodeDAO().getNodeWithId(nodeId);
		doNodeActivate(node, cycle);
		setSelectedAnchor(CONTR_LIST_ANCHOR);		
	}
	private void redirectToScientificContributorsLogin(MappedNode node) {
		if (node == null) {
			node = getWorkingNodeDAO().getRootNode();
		}
		String nodeName = node.getName();
		Object[] parameters = new Object[] {nodeName, node.getNodeId().toString()};
		ScientificContributorsLogin loginPage = (ScientificContributorsLogin) getRequestCycle().getPage("ScientificContributorsLogin");
		loginPage.setupPageCallback(true, parameters, true, getRequestCycle());	
		throw new PageRedirectException(loginPage);
	}
	public void returnFromContributorPrivilegesEditing(Long nodeId, IRequestCycle cycle) {
    	MappedNode workingNode = getWorkingNodeDAO().getNodeWithId(nodeId); 
    	setSelectedAnchor(CONTR_LIST_ANCHOR);
    	doNodeActivate(workingNode, cycle);
    	throw new PageRedirectException(this);
	}
	
	@SuppressWarnings("unchecked")
	public String getCurrentContributorNodeNames() {
		Collection nodeNames = getPermissionChecker().getNodeNamesContributorAttachedTo(getCurrentContributor().getId(), true); 
		return StringUtils.returnCommaJoinedString(nodeNames);
	}
	public boolean getShowIndexLink() {
		return !getNode().getIsLeaf() && 
			!getCookieAndContributorSource().getCookieIsEnabled(ContributorPreferencesMenu.PREVIEW_MODE_COOKIE_NAME);
	}
	public boolean getShowPeopleList() {
		return !getCookieAndContributorSource().getCookieIsEnabled(ContributorPreferencesMenu.PREVIEW_MODE_COOKIE_NAME) &&
			getCookieAndContributorSource().getCookieIsEnabled(ContributorPreferencesMenu.PEOPLE_LISTS_COOKIE_NAME);
	}
	public boolean getNodeIsDownloaded() {
		return getDownloadDAO().getNodeIsDownloaded(getNode().getNodeId());
	}
	
	@SuppressWarnings("unchecked")
	public List getNodesToAddSubgroupsTo() {
		List nodes = getNamedNodesOnPage();
		for (Iterator iter = new ArrayList(nodes).iterator(); iter.hasNext();) {
			MappedNode node = (MappedNode) iter.next();
			if (getDownloadDAO().getNodeIsDownloaded(node.getNodeId())) {
				nodes.remove(node);
			}
		}
		return nodes;
	}
	
	@SuppressWarnings("unchecked")
	public boolean getHasOtherNamesToDisplay() {
		SortedSet synonyms = getNode().getSynonyms();
		int dontlistNames = 0;
		for (Iterator itr = synonyms.iterator(); itr.hasNext(); ) {
			MappedOtherName moname = (MappedOtherName) itr.next();
			if (moname.getIsDontList()) {
				dontlistNames++;
			}
		}
		return (synonyms.size() - dontlistNames) > 0;
	}
}
