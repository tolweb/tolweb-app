package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.tacos.ajax.AjaxUtils;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.dao.EditedPageDAO;
import org.tolweb.hibernate.EditedPage;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.misc.EditedPagesBuilder;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrowserver.BatchPusher;
import org.tolweb.treegrowserver.PublicationBatch;
import org.tolweb.treegrowserver.SubmittedPage;
import org.tolweb.treegrowserver.UploadBatch;
import org.tolweb.treegrowserver.UploadPage;

public abstract class UserSubmitPublication extends AbstractScientificContributorPage implements PageBeginRenderListener, IExternalPage, UserInjectable, 
		NodeInjectable, PageInjectable, AccessoryInjectable, TreeGrowServerInjectable, CookieInjectable, BaseInjectable {
	private RevisionTypeModel revisionTypeModel;
    public abstract Object getCurrentBatch();
    public abstract UploadPage getCurrentUploadPage();
    public abstract void setRootGroupName(String value);
    @InjectObject("spring:editedPagesBuilder")
    public abstract EditedPagesBuilder getEditedPagesBuilder();
    public UserSubmitPublication() {
    	revisionTypeModel = new RevisionTypeModel();
    }
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        getCookieAndContributorSource().authenticateExternalPage(cycle);
    }
    public abstract int getIndex();
    public abstract void setLastNumAncestors(int value);
    public abstract int getLastNumAncestors();
    @SuppressWarnings("unchecked")
    public abstract void setBatches(List value);
    @SuppressWarnings("unchecked")
    public abstract List getBatches();
    @SuppressWarnings("unchecked")
    public abstract void setEditedAccessoryPages(List value);
    @SuppressWarnings("unchecked")
    public abstract List getEditedAccessoryPages();
    public abstract AccessoryPagePublishWrapper getCurrentEditedAccessoryPage();
    public abstract int getNumOpenLists();
    public abstract void setNumOpenLists(int value);
    public abstract UploadBatch getSubmittedBatch();
    public abstract void setSubmittedBatch(UploadBatch batch);
    public abstract void setError(String value);
    public abstract void setPublicationBatchId(Long value);
    public abstract void setDontRefetchBatches(boolean value);
    public abstract boolean getDontRefetchBatches();
    public abstract int getBatchIndex();
    
    @SuppressWarnings("unchecked")
    public void pageBeginRender(PageEvent event) {
    	super.pageBeginRender(event);
    	if (!AjaxUtils.isAjaxCycle(getRequestCycle())) {
	        Contributor contr = getContributor();
	        if (contr == null) {
	            contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
	        }
	        if (contr == null) {
	            throw new PageRedirectException("ScientificContributorsLogin");
	        }
	        if (!event.getRequestCycle().isRewinding() && !getDontRefetchBatches()) {
	            List batches = getEditedPagesBuilder().buildPageBlocksForContributor(contr);
	            // initialize the downloaded pages to not be set to published, so the
	            // checkbox doesn't show up as being selected
	            setLockedPagesToNotPublish(batches);
	            setBatches(batches);
	            // initialize all of the batches to be of the regular revision variety
	            for (Iterator iter = batches.iterator(); iter.hasNext();) {
					UploadBatch nextBatch = (UploadBatch) iter.next();
					nextBatch.setRevisionType(SubmittedPage.REGULAR_REVISION);
				}
	            setEditedAccessoryPages(getEditedAccessoryPagesForContributor(contr));
	        }
    	}
    }
    
    @SuppressWarnings("unchecked")
    private List getEditedAccessoryPagesForContributor(Contributor contr) {
    	ArrayList editedAccessoryPages = new ArrayList();
    	EditedPageDAO dao = getEditedPageDAO();
    	List accPageIds = dao.getEditedPageIdsForContributor(contr, EditedPage.ARTICLE_NOTE_TYPE);
    	for (Iterator iter = accPageIds.iterator(); iter.hasNext();) {
			Long accPageId = (Long) iter.next();
			AccessoryPagePublishWrapper wrapper = new AccessoryPagePublishWrapper();
			MappedAccessoryPage page = getWorkingAccessoryPageDAO().getAccessoryPageWithId(accPageId);
			if (page != null) {
				wrapper.setPage(page);
				wrapper.setShouldBePublished(!page.getIsSubmitted());
				editedAccessoryPages.add(wrapper);
			}
		}
    	return editedAccessoryPages;
    }
    
    @SuppressWarnings("unchecked")
    private void setLockedPagesToNotPublish(List batches) {
        for (Iterator iter = batches.iterator(); iter.hasNext();) {
            UploadBatch nextBatch = (UploadBatch) iter.next();
            for (Iterator iterator = nextBatch.getSortedUploadedPages().iterator(); iterator.hasNext();) {
                UploadPage nextPage = (UploadPage) iterator.next();
                if (nextPage.getLockedElement() != null) {
                    nextPage.setShouldBePublished(false);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void submitAccessoryPages(IRequestCycle cycle) {
    	ArrayList submittedPages = new ArrayList();
    	Contributor contr = getContributor();
    	boolean isEditor = getPermissionChecker().isEditor(contr); 
    	for (Iterator iter = getEditedAccessoryPages().iterator(); iter.hasNext();) {
			AccessoryPagePublishWrapper nextWrapper = (AccessoryPagePublishWrapper) iter.next();
			if (nextWrapper.getShouldBePublished()) {
				MappedAccessoryPage nextPage = nextWrapper.getPage();
				if (isEditor) {
					getAccessoryPageSubmitter().editorSubmitAccessoryPage(nextPage, true);
				} else {
					getAccessoryPageSubmitter().nonEditorSubmitAccessoryPage(nextPage, contr);	
				}
				submittedPages.add(nextPage);
			}
		}
    	IPage pagesSubmittedPage = cycle.getPage("AccessoryPagesSubmitted");
    	PropertyUtils.write(pagesSubmittedPage, "submittedPages", submittedPages);
    	PropertyUtils.write(pagesSubmittedPage, "wasEditor", isEditor);
    	PropertyUtils.write(pagesSubmittedPage, "submittedContributor", getContributor());
    	cycle.activate(pagesSubmittedPage);
    }
    
    public void submitPages(IRequestCycle cycle) {
        if (!checkForPublishError(getSubmittedBatch())) {
            PublicationBatch publicationBatch = getBatchSubmitter().submitSelectedPagesForPublication(getSubmittedBatch(), getContributor());
            IPage batchSubmittedPage = cycle.getPage("BatchSubmitted");
            String rootPages = getRootPagesStringForPages(getSubmittedBatch().getSortedUploadedPages());
            PropertyUtils.write(batchSubmittedPage, "rootGroupName", rootPages);
            PropertyUtils.write(batchSubmittedPage, "publicationBatchId", publicationBatch.getId());
            PropertyUtils.write(batchSubmittedPage, "contributor", getContributor());
            if (StringUtils.notEmpty(getSubmittedBatch().getSubmissionComment())) {
            	PropertyUtils.write(batchSubmittedPage, "submissionComment", getSubmittedBatch().getSubmissionComment());
            }
            int publicationResult = getBatchPusher().conditionallyPushBatchToPublic(publicationBatch, getContributor());
            if (publicationResult == BatchPusher.PUSH_PUBLIC_SUCCESSFUL) {
                PropertyUtils.write(batchSubmittedPage, "publicationBatchId", null);
            }
            // TODO add error handling here
            cycle.activate(batchSubmittedPage);            
            // refetch things to refresh the view
            setBatches(null);
        } else {
            setSubmittedBatch(null);
        }
    }
    
    @SuppressWarnings("unchecked")
    public String getRootPagesStringForPages(List pages) {
        return StringUtils.returnCommaAndJoinedString(getRootPageNames(pages));
    }    
    
    @SuppressWarnings("unchecked")
    protected List<String> getRootPageNames(List pages) {
    	if (pages == null) {
    		return null;
    	}
        List copyOfPages = new ArrayList();
        // remove any pages that aren't going to be published
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
			UploadPage nextPage = (UploadPage) iter.next();
			if (nextPage.getShouldBePublished()) {
				copyOfPages.add(nextPage);
			}
		}
    	List pageNames = new ArrayList();
        pages = getBatchPusher().getRootMostPages(copyOfPages);
        // go ahead and resort them because we are done with the original sorting at this point
        for (Iterator iter = pages.iterator(); iter.hasNext();) {
            UploadPage nextPage = (UploadPage) iter.next();
            pageNames.add(nextPage.getGroupName());
        }
        return pageNames;
    }
    
    protected boolean checkForPublishError(UploadBatch batch) {
        UploadPage[] offendingPages = getIncompatiblePublishScenario(batch.getSortedUploadedPages());
        if (offendingPages != null) {
            setError("You cannot publish page: <em>" + offendingPages[1].getGroupName() + "</em> because it's ancestor page: <em>"
                    + offendingPages[0].getGroupName() + "</em> has not been published and doing so would result in an inconsistent tree structure.");
            return true;
        } else {
            if (!getHasAnyPublishedPages(batch)) {
                setError("You must select at least one page to publish.");
                return true;
            }
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean getHasAnyPublishedPages(UploadBatch batch) {
        for (Iterator iter = batch.getSortedUploadedPages().iterator(); iter.hasNext();) {
            UploadPage page = (UploadPage) iter.next();
            if (page.getShouldBePublished()) {
                return true;
            }
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public List getCurrentSortedUploadedPages() {
        return ((UploadBatch) getCurrentBatch()).getSortedUploadedPages();
    }
    
    public boolean getPageExistsInPublic() {
        return getPublicPageDAO().getPageExistsWithId(getCurrentUploadPage().getPageId());
    }
    
    public boolean getAccessoryPageExistsInPublic() {
    	AccessoryPagePublishWrapper accPage = getCurrentEditedAccessoryPage();
    	return getPublicAccessoryPageDAO().getAccessoryPageExistsWithId(accPage.getPage().getAccessoryPageId());
    }
    
    public boolean getCanPublishAccessoryPage() {
    	return !getCurrentEditedAccessoryPage().getPage().getIsSubmitted();
    }
    
    public boolean getCanPublish() {
        boolean result = getCurrentUploadPage().getLockedElement() == null;
        if (!result) {
            // there will be no checkbox to deselect, so set it to not publish right now
            getCurrentUploadPage().setShouldBePublished(false);
        }
        return result;
    }
    
    public String getCurrentErrorMessage() {
        if (getCurrentUploadPage().getLockedElement() == null) {
            return null;
        } else {
            return getDownloadDAO().getMessageForLockedElement(getCurrentUploadPage().getLockedElement());
        }
    }
    
    public String getOpenListString() {
        int index = getIndex();
        int currentAncestors = getCurrentUploadPage().getNumAncestors();
        int lastAncestors = getLastNumAncestors();
        if (index == 0 || currentAncestors > lastAncestors) {
            setNumOpenLists(getNumOpenLists() + 1);
            String classString = index == 0 ? " class=\"links\" " : " class=\"publishsub\" ";
            return "<ul" + classString + ">";
        } else {
            return null;
        }
    }
    
    public String getCloseListString() {
        int currentAncestors = getCurrentUploadPage().getNumAncestors();
        int lastAncestors = getLastNumAncestors();
        if (currentAncestors < lastAncestors) {
            setNumOpenLists(getNumOpenLists() - 1);
            return "</ul>";
        } else {
            return null;
        }        
    }
    
    public String getAdditionalCloseListsString() {
        if (getNumOpenLists() > 0) {
            StringBuilder resultString = new StringBuilder();
            for (int i = 0; i < getNumOpenLists(); i++) {
            	resultString.append("</ul>");
            }
            return resultString.toString();
        } else {
            return null;
        }
    }
    
    public String getCurrentPageName() {
        setLastNumAncestors(getCurrentUploadPage().getNumAncestors());
        return getCurrentUploadPage().getGroupName();
    }
    
    @SuppressWarnings("unchecked")
    public Collection getEditedPages() { 
        return getEditedPagesBuilder().buildPageBlocksForContributor(getContributor());
    }
    
    /**
     * Returns a 2-element array with the first element being the ancestor
     * page scheduled to not be published and the second being the descendant
     * page that is scheduled to be published, or null if no such scenario
     * exists
     * @param pages
     * @return
     */
    @SuppressWarnings("unchecked")
    private UploadPage[] getIncompatiblePublishScenario(Collection pages) {
        return getBatchPusher().getIncompatiblePublishScenario(pages);
    }
	public RevisionTypeModel getRevisionTypeModel() {
		return revisionTypeModel;
	}
	public void setRevisionTypeModel(RevisionTypeModel revisionTypeModel) {
		this.revisionTypeModel = revisionTypeModel;
	}
	public String getCurrentAccessoryPageUrl() {
		return getUrlBuilder().getWorkingURLForObject(getCurrentEditedAccessoryPage().getPage());
	}
	public boolean getIsEditor() {
		return getPermissionChecker().isEditor(getContributor());
	}	
}

class AccessoryPagePublishWrapper {
	private MappedAccessoryPage page;
	private boolean shouldBePublished;
	public MappedAccessoryPage getPage() {
		return page;
	}
	public void setPage(MappedAccessoryPage page) {
		this.page = page;
	}
	public boolean getShouldBePublished() {
		return shouldBePublished;
	}
	public void setShouldBePublished(boolean shouldBePublished) {
		this.shouldBePublished = shouldBePublished;
	}
}
