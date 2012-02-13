package org.tolweb.tapestry;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

public abstract class ContributorBatchUploadResults extends BasePage implements NodeInjectable, UserInjectable {
	public abstract Contributor getCurrentContributor();
	public abstract void setCurrentContributor(Contributor value);
	@SuppressWarnings("unchecked")
	public abstract void setContributorsToMissingNames(Hashtable value);
	@SuppressWarnings("unchecked")
	public abstract Hashtable getContributorsToMissingNames();
	@SuppressWarnings("unchecked")
	public abstract void setContributors(List value);
	@Persist("session") @SuppressWarnings("unchecked")
	public abstract List getContributors();
	@SuppressWarnings("unchecked")
	public abstract void setExistingContributors(List value);
	@Persist("session") @SuppressWarnings("unchecked")
	public abstract List getExistingContributors();
	public abstract String getStoredGroupsEditingPermission();
	public abstract void setStoredGroupsEditingPermission(String value);
	public abstract String getStoredGroupsNoEditingPermission();
	public abstract void setStoredGroupsNoEditingPermission(String value);
	public abstract void setCurrentFirstNodeId(Long value);
	public abstract void setDeletedAllContributors(boolean value);
	@SuppressWarnings("unchecked")
	public abstract void setContributorIdsToSendEmail(Collection value);
	@Persist("session") @SuppressWarnings("unchecked")
	public abstract Collection getContributorIdsToSendEmail();

	public boolean getCurrentContributorIsNew() {
		return !getExistingContributors().contains(getCurrentContributor());
	}
	public boolean getShouldSendEmail() {
		boolean shouldSendEmail = getContributorIdsToSendEmail().contains(getCurrentContributor().getId()); 
		return shouldSendEmail;
	}
	@SuppressWarnings("unchecked")
	public void deleteAllNewContributors(IRequestCycle cycle) {
		List existingContributors = getExistingContributors();
		ContributorDAO contrDAO = getContributorDAO();
		for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
			Contributor nextContr = (Contributor) iter.next();
			if (!existingContributors.contains(nextContr)) {
				contrDAO.deleteContributor(nextContr, null);
			}
		}
		setDeletedAllContributors(true);
	}
	public String getGroupsWithEditingPermission() {
		if (getStoredGroupsEditingPermission() != null) {
			return getStoredGroupsEditingPermission();
		} else {
			String namesString = getPermissionsStringForContributor(true);
			setStoredGroupsEditingPermission(namesString);
			return namesString;
		}
	}
	public String getGroupsNoEditingPermission() {
		if (getStoredGroupsNoEditingPermission() != null) {
			return getStoredGroupsNoEditingPermission();
		} else {
			String namesString = getPermissionsStringForContributor(false);
			setStoredGroupsNoEditingPermission(namesString);
			return namesString;			
		}
	}
	@SuppressWarnings("unchecked")
	private String getPermissionsStringForContributor(boolean checkPermissions) {
		List nodeIds = getPermissionChecker().getNodeAttachmentsForContributor(getCurrentContributor(), checkPermissions);
		if (nodeIds != null && nodeIds.size() > 0) {
			setCurrentFirstNodeId((Long) nodeIds.get(0));
		}
		List names = getWorkingNodeDAO().getNodeNamesWithIds(nodeIds);
		String namesString = StringUtils.returnCommaJoinedString(names);
		return namesString;
	}
	public void clearStoredProperties() {
		setStoredGroupsNoEditingPermission(null);
		setStoredGroupsEditingPermission(null);			
	}	
	public boolean getHasUnmatchedGroups() {
		return getContributorsToMissingNames().get(getCurrentContributor()) != null;
	}
	@SuppressWarnings("unchecked")
	public String getUnmatchedGroups() {
		Collection missingGroups = (Collection) getContributorsToMissingNames().get(getCurrentContributor());
		return StringUtils.returnCommaJoinedString(missingGroups);
	}
}
