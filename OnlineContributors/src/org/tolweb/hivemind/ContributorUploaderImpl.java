package org.tolweb.hivemind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.EditHistoryDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.ServerXMLReader;

public class ContributorUploaderImpl implements ContributorUploader {
	private ContributorDAO contributorDAO;
	private EditHistoryDAO editHistoryDAO;
	private PermissionChecker permissionChecker;
	private ServerXMLReader serverXMLReader;
	
    /**
     * saves all of the contributors in the xml document passed-in.
     * @param contributorsDocument the document containing the contributors
     * @param creatingContributor the contributor creating these new contributors
     * @param updateExistingContributors whether to update existing contributors if they 
     * match records in the database
     * @return The list of saved contributors, with a hashtable of contributors to unmatched 
     * group names on the end, followed by a list of the contributors who already existed and
     * were updated
     */
	@SuppressWarnings("unchecked")
    public List saveContributors(Document contributorsDocument, Contributor creatingContributor,
    		boolean updateExistingContributors) {
    	List contributorIdsToSendEmail = new ArrayList();
    	List savedContributors = new ArrayList();
    	List contributorElements = contributorsDocument.getRootElement().getChildren(XMLConstants.person);
    	Hashtable contributorsToUnmatchedNodes = new Hashtable();
    	List existingSavedContributors = new ArrayList();
    	ContributorDAO dao = getContributorDAO();
    	EditHistoryDAO editHistoryDAO = getEditHistoryDAO();
    	PermissionChecker checker = getPermissionChecker();
    	for (Iterator iter = contributorElements.iterator(); iter.hasNext();) {
			Element nextContributorElement = (Element) iter.next();
			String email = nextContributorElement.getChildText(XMLConstants.email);
			Contributor existingContributor = dao.getContributorWithEmail(email);
			// if they've chosen not to update existing contributors and we've found one
			// then skip to the next contributor
			if (existingContributor != null && !updateExistingContributors) {
				continue;
			}
			boolean wasExisting = existingContributor != null;			
			List newContributorStuff = getServerXMLReader().getContributorAndNodesFromElement(nextContributorElement, existingContributor);
			Contributor nextContributor = (Contributor) newContributorStuff.get(0);
			Collection nodesNoPermission = (Collection) newContributorStuff.get(1);
			Collection nodes = (Collection) newContributorStuff.get(2);			
			Collection unmatchedNames = (Collection) newContributorStuff.get(3);
			Boolean sendEmail = (Boolean) newContributorStuff.get(4);
			if (!wasExisting) {
				// they are a scientific contributor if they have nodes, another scientist
				// otherwise?
				/*if (nodes.size() > 0) {
					nextContributor.setContributorType(Contributor.SCIENTIFIC_CONTRIBUTOR);
				} else {
					nextContributor.setContributorType(Contributor.OTHER_SCIENTIST);
				}*/
				dao.addContributor(nextContributor, creatingContributor, editHistoryDAO);
			} else {
				dao.saveContributor(nextContributor, editHistoryDAO, creatingContributor);
				existingSavedContributors.add(nextContributor);
			}
			if (sendEmail) {
				contributorIdsToSendEmail.add(nextContributor.getId());
			}
			if (unmatchedNames.size() > 0) {
				contributorsToUnmatchedNodes.put(nextContributor, unmatchedNames);
			}
			if (nodes.size() > 0) {
				for (Iterator iterator = nodes.iterator(); iterator
				.hasNext();) {
					MappedNode nextNode = (MappedNode) iterator.next();					
					checker.addNodeAttachmentForContributor(nextContributor.getId(), nextNode.getNodeId(), true);
				}
			}
			if (nodesNoPermission.size() > 0) {
				for (Iterator iterator = nodesNoPermission.iterator(); iterator
						.hasNext();) {
					MappedNode nextNode = (MappedNode) iterator.next();
					checker.addNodeAttachmentForContributor(nextContributor.getId(), nextNode.getNodeId(), false);
				}
			}
			savedContributors.add(nextContributor);
		}
    	savedContributors.add(contributorsToUnmatchedNodes);
    	savedContributors.add(existingSavedContributors);
    	savedContributors.add(contributorIdsToSendEmail);
    	return savedContributors;
    }

	public ContributorDAO getContributorDAO() {
		return contributorDAO;
	}

	public void setContributorDAO(ContributorDAO contributorDAO) {
		this.contributorDAO = contributorDAO;
	}

	public EditHistoryDAO getEditHistoryDAO() {
		return editHistoryDAO;
	}

	public void setEditHistoryDAO(EditHistoryDAO editHistoryDAO) {
		this.editHistoryDAO = editHistoryDAO;
	}

	public PermissionChecker getPermissionChecker() {
		return permissionChecker;
	}

	public void setPermissionChecker(PermissionChecker permissionChecker) {
		this.permissionChecker = permissionChecker;
	}

	public ServerXMLReader getServerXMLReader() {
		return serverXMLReader;
	}

	public void setServerXMLReader(ServerXMLReader serverXMLReader) {
		this.serverXMLReader = serverXMLReader;
	}
}
