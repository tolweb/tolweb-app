package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrowserver.PublicationBatch;

public abstract class EditorSubmitPublication extends UserSubmitPublication implements IExternalPage, PageBeginRenderListener {
    public abstract void setBatchToBePublished(PublicationBatch batch);    
    /**
     * If an editor is going to publish a submitted batch, then this is set
     * @return
     */
    public abstract PublicationBatch getBatchToBePublished();
    public abstract PublicationBatch getRejectedBatch();
    public abstract void setPublished(boolean value);
    public abstract boolean getPublished();
    public abstract void setRejected(boolean value);
    public abstract void setBatchClosed(boolean value);
    public abstract boolean getNoPermissions();
    public abstract void setNoPermissions(boolean value);
    public abstract void setUnpublishedNodeNames(String value);
    public abstract void setCurrentRootGroupName(String value);
    public abstract String getCurrentRootGroupName();
    
    public void pageBeginRender(PageEvent event) {
        if (getBatchToBePublished() != null && !event.getRequestCycle().isRewinding()) {
            initBatches();
        }
    }
    
    public String getTitle() {
    	if (getNoPermissions()) {
    		return "Insufficient Permissions";
    	} else {
    		return "Publish ToL Pages Submitted by " + getBatchToBePublished().getSubmittedContributor().getDisplayName();
    	}
    }
    
    @SuppressWarnings("unchecked")
    private void initBatches() {
        ArrayList batches = new ArrayList();
        batches.add(getBatchToBePublished());
        setBatches(batches);        
    }
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters != null && parameters.length > 0) {
            Long publicationBatchId = (Long) parameters[0];
            PublicationBatch batch = getPublicationBatchDAO().getBatchWithId(publicationBatchId);
            if (batch != null) {
                if (batch.getIsClosed()) {
                    setBatchClosed(true);
                }
                Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
                if (getPublicationBatchDAO().getContributorCanPublishBatch(contr, batch)) {
                    setBatchToBePublished(batch);
                } else {
                    setNoPermissions(true);
                }
            }
        }
    }
    
    public void setSubmittedBatch(PublicationBatch batch) {
        
    }
    
    public Long getPublicationBatchID() {
        return getBatchToBePublished().getId();
    }
    
    public void setPublicationBatchID(Long value) {
        PublicationBatch batch = getPublicationBatchDAO().getBatchWithId(value); 
        setBatchToBePublished(batch);
        initBatches();
    }
    
    public boolean getCanPublish() {
        return true;
    }
    
    public void submitPages(IRequestCycle cycle) {
        if (getRejectedBatch() != null) {
            // they are rejecting the pages
            getBatchSubmitter().unsubmitPublicationBatch(getBatchToBePublished(), getContributor());
            setRejected(true);
        } else {
            try {
                String unpublishedNodes = getBatchPusher().pushPublicationBatchToPublic(getBatchToBePublished(), getContributor());
                setPublished(true);
                setUnpublishedNodeNames(unpublishedNodes);
                // clear the user's view of the publication batches
                ((UserSubmitPublication) cycle.getPage("UserSubmitPublication")).setBatches(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public List getCurrentSortedUploadedPages() {
        Set submittedPages = getBatchToBePublished().getSubmittedPages();
        List sortedPages = getBatchPusher().getSortedUploadPages(submittedPages, getContributor(), true, true);
        getBatchToBePublished().setSortedUploadPages(sortedPages);
        return sortedPages;        
    }
    
    @SuppressWarnings("unchecked")
    public List getRootPagesList() {
    	return getRootPageNames(getBatchToBePublished().getSortedUploadPages());
    }
    
    public String getCurrentLinkURL() {
    	if (getPublished()) {
    		return getUrlBuilder().getPublicURLForBranchPage(getCurrentRootGroupName(), null);
    	} else {
    		return getUrlBuilder().getWorkingURLForBranchPage(getCurrentRootGroupName(), null);
    	}
    }
    
    public String getRootPagesString() {
        return getRootPagesStringForPages(getBatchToBePublished().getSortedUploadPages());
    }
}
