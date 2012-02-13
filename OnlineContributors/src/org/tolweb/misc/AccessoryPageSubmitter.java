package org.tolweb.misc;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.AccessoryPagePusher;
import org.tolweb.dao.EditedPageDAO;
import org.tolweb.hibernate.EditedPage;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.treegrow.main.Contributor;

public class AccessoryPageSubmitter {
	private AccessoryPageDAO workingDAO;
	private AccessoryPageDAO publicDAO;
	private CacheAccess cacheAccess;
	private AccessoryPagePusher pusher;
	private EditedPageDAO editedPageDAO;
	
	@SuppressWarnings("unchecked")
	public void editorSubmitAccessoryPage(MappedAccessoryPage page, boolean isApprove) {
		page.setIsSubmitted(false);
		page.setIsSubmittedToTeacher(false);
		page.setSubmissionDate(null);
		page.setSubmittedContributorId(null);
		page.setSubmittedContributor(null);
		getWorkingDAO().saveAccessoryPage(page); 
		if (isApprove) {
			getPusher().pushAcessoryPageToDB(page, getPublicDAO());
			getCacheAccess().evictAccessoryPageObjectsFromCache(page);
			Set pageIdSet = new HashSet();
			pageIdSet.add(page.getAccessoryPageId());
			// treehouses don't use the editedpage system
			if (!page.getIsTreehouse()) {
				getEditedPageDAO().deleteEditedPagesWithIds(pageIdSet, EditedPage.ARTICLE_NOTE_TYPE);
			}
		}
	}
    public void nonEditorSubmitAccessoryPage(MappedAccessoryPage page, Contributor contr) {
        page.setSubmittedContributor(contr);
        page.setSubmittedContributorId(Long.valueOf(contr.getId()));
        page.setSubmissionDate(new Date());    
        page.setIsSubmitted(true);
        getWorkingDAO().saveAccessoryPage(page);
    }	
	public CacheAccess getCacheAccess() {
		return cacheAccess;
	}
	public void setCacheAccess(CacheAccess cacheAccess) {
		this.cacheAccess = cacheAccess;
	}
	public AccessoryPageDAO getPublicDAO() {
		return publicDAO;
	}
	public void setPublicDAO(AccessoryPageDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	public AccessoryPageDAO getWorkingDAO() {
		return workingDAO;
	}
	public void setWorkingDAO(AccessoryPageDAO workingDAO) {
		this.workingDAO = workingDAO;
	}
	public AccessoryPagePusher getPusher() {
		return pusher;
	}
	public void setPusher(AccessoryPagePusher pusher) {
		this.pusher = pusher;
	}
	public EditedPageDAO getEditedPageDAO() {
		return editedPageDAO;
	}
	public void setEditedPageDAO(EditedPageDAO editedPageDAO) {
		this.editedPageDAO = editedPageDAO;
	}    
}
