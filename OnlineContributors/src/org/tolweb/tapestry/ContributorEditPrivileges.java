package org.tolweb.tapestry;

import java.util.Date;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.ContributorPermission;
import org.tolweb.hibernate.EditHistory;
import org.tolweb.treegrow.main.Contributor;

public abstract class ContributorEditPrivileges extends BaseComponent {
	@Parameter(defaultValue = "page.editedContributor")
	public abstract Contributor getEditedContributor();
	public abstract ContributorPermission getCurrentPermission();
	@InjectObject("spring:contributorDAO")
	public abstract ContributorDAO getContributorDAO();	
	
	private EditHistory getHistory() {
		return ((AbstractContributorRegistration) getPage()).getHistory();
	}
	
	public boolean getHasSubmitter() {
		Long submitterId = getSubmitterId();
		if (submitterId != null && submitterId.intValue() > 0) {
			return true;
		}
		return false;
	}
	
	public String getSubmitterName() {
		Long submitterId = getSubmitterId();
		return getContributorDAO().getNameForContributorWithId(submitterId.intValue());
	}
	
	public Date getSubmissionDate() {
		return getHistory().getCreationDate();
	}
	
	private Long getSubmitterId() {
		EditHistory history = getHistory();
		if (history != null) {
			return history.getCreatedContributorId();
		} else {
			return null;
		}
	}
	
	public boolean getHasConfirmer() {
		Long confirmerContributorId = getEditedContributor().getConfirmerContributorId(); 
		return  confirmerContributorId != null && confirmerContributorId.intValue() > 0;
	}
	
	public String getConfirmerName() {
		return getContributorDAO().getNameForContributorWithId(getEditedContributor().getConfirmerContributorId().intValue());
	}
	
	public boolean getHasLastEdited() {
		Long lastEditedId = getLastEditedContributorId();
		return lastEditedId != null && lastEditedId.intValue() > 0;
	}
	
	private Long getLastEditedContributorId() {
		EditHistory history = getHistory();
		if (history != null) {
			return history.getLastEditedContributorId();
		} else {
			return null;
		}		
	}
	
	public String getLastEditedName() {
		Long lastEditedId = getLastEditedContributorId();
		return getContributorDAO().getNameForContributorWithId(lastEditedId.intValue());
	}
	public Date getLastEditedDate() {
		return getHistory().getLastEditedDate();
	}
	public PermissionConvertor getPermissionConvertor() {
		return new PermissionConvertor(getEditedContributor().getContributorPermissions());
	}
	public String getCurrentContributorName() {
		return getContributorDAO().getNameForContributorWithId(getCurrentPermission().getContributorId());
	}
}
