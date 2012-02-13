package org.tolweb.fixscripts;

import java.util.List;

import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.EditHistoryDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.EditHistory;
import org.tolweb.treegrow.main.Contributor;

public class CleanEditHistories extends AbstractFixScript {
	private void cleanEditHistories() {
		// TODO Auto-generated method stub
		PageDAO pageDAO = (PageDAO) context.getBean("workingPageDAO");
		EditHistoryDAO editHistoryDAO = (EditHistoryDAO) context.getBean("editHistoryDAO");
		PermissionChecker checker = (PermissionChecker) context.getBean("permissionChecker");
		ContributorDAO contrDAO = (ContributorDAO) context.getBean("contributorDAO");
		NodeDAO nodeDAO = (NodeDAO) context.getBean("workingNodeDAO");
		List<Object[]> nodeIdsAndHistoryIds = pageDAO.getNodeIdsAndEditHistoryIds();
		for (Object[] objects : nodeIdsAndHistoryIds) {
			Long nodeId = (Long) objects[0];
			Long editHistoryId = (Long) objects[1];
			EditHistory history = editHistoryDAO.getEditHistoryWithId(editHistoryId);
			Long lastEditedContributorId = history.getLastEditedContributorId(); 
			Contributor contributor = contrDAO.getContributorWithId(lastEditedContributorId);
			if (lastEditedContributorId != null && !checker.checkHasPermissionForNode(contributor, nodeId)) {
				// bad!
				System.out.println("contributor w/ no permission for node is: " + contributor.getDisplayName() + " node is: " + nodeDAO.getNameForNodeWithId(nodeId));
				editHistoryDAO.updateLastEdited(editHistoryId, null);
			}
		}
	}	
	
	public static void main(String[] args) {
		CleanEditHistories cleaner = new CleanEditHistories();
		cleaner.cleanEditHistories();
	}
}
