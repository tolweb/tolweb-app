package org.tolweb.treegrowserver;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.PersistentObject;
import org.tolweb.treegrow.main.Contributor;

/**
 *@hibernate.class table="PublicationBatches"
 */
public class PublicationBatch extends PersistentObject {
    private Contributor submittedContributor;
    private Contributor editingContributor;
    private Date submissionDate;
    private Date decisionDate;
    private Set submittedPages;
    private boolean wasPublished;
    private boolean isClosed;
    private List sortedUploadPages;
    private Long uploadBatchId;
    
    /**
     * here because xdoclet is too numbskulled to be able to look in a superclass
     * for the id attribute
     * @hibernate.id generator-class="native" column="id" unsaved-value="null"
     * @return Returns the id.
     */
    public Long getId() {
        return super.getId();
    }
    
    /**
     * @hibernate.many-to-one column="editingContributorId" class="org.tolweb.treegrow.main.Contributor"
     * @return Returns the editingContributor.
     */
    public Contributor getEditingContributor() {
        return editingContributor;
    }
    /**
     * @param editingContributor The editingContributor to set.
     */
    public void setEditingContributor(Contributor editingContributor) {
        this.editingContributor = editingContributor;
    }
    /**
     * @hibernate.property
     * @return Returns the submissionDate.
     */
    public Date getSubmissionDate() {
        return submissionDate;
    }
    /**
     * @param submissionDate The submissionDate to set.
     */
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    /**
     * @hibernate.many-to-one column="submittedContributorId" class="org.tolweb.treegrow.main.Contributor"
     * @return Returns the submittedContributor.
     */
    public Contributor getSubmittedContributor() {
        return submittedContributor;
    }
    /**
     * @param submittedContributor The submittedContributor to set.
     */
    public void setSubmittedContributor(Contributor submittedContributor) {
        this.submittedContributor = submittedContributor;
    }
    /**
     * @hibernate.set table="SubmittedPages"
     * @hibernate.collection-composite-element class="org.tolweb.treegrowserver.SubmittedPage"
     * @hibernate.collection-key column="batchId"
     * @hibernate.collection-cache usage="nonstrict-read-write"
     *
     * @return Returns the submittedPages.
     */
    public Set getSubmittedPages() {
        return submittedPages;
    }
    /**
     * @param submittedPages The submittedPages to set.
     */
    public void setSubmittedPages(Set submittedPages) {
        this.submittedPages = submittedPages;
    }
    /**
     * @hibernate.property
     * @return Returns the wasPublished.
     */
    public boolean getWasPublished() {
        return wasPublished;
    }
    /**
     * @param wasPublished The wasPublished to set.
     */
    public void setWasPublished(boolean wasPublished) {
        this.wasPublished = wasPublished;
    }
    /**
     * @hibernate.property
     * @return Returns the decisionDate.
     */
    public Date getDecisionDate() {
        return decisionDate;
    }
    /**
     * @param decisionDate The decisionDate to set.
     */
    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    /**
     * @hibernate.property
     * @return Returns the isClosed.
     */
    public boolean getIsClosed() {
        return isClosed;
    }

    /**
     * @param isClosed The isClosed to set.
     */
    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    /**
     * @return Returns the sortedUploadPages.
     */
    public List getSortedUploadPages() {
        return sortedUploadPages;
    }

    /**
     * @param sortedUploadPages The sortedUploadPages to set.
     */
    public void setSortedUploadPages(List sortedUploadPages) {
        this.sortedUploadPages = sortedUploadPages;
    }

    /**
     * @hibernate.property
     * @return Returns the uploadBatchId.
     */
    public Long getUploadBatchId() {
        return uploadBatchId;
    }

    /**
     * @param uploadBatchId The uploadBatchId to set.
     */
    public void setUploadBatchId(Long uploadBatchId) {
        this.uploadBatchId = uploadBatchId;
    }
    
    public String toString() {
    	String contributorName = getSubmittedContributor() != null ? getSubmittedContributor().getDisplayName() : "null";
    	return "publication batch with id: " + getId() + " closed: " + getIsClosed() + " num submitted pages: " + getSubmittedPages().size() + " submitted contributor " 
    		+ contributorName + " submission date: " + getSubmissionDate(); 
    }
}
