package org.tolweb.hivemind;

import java.util.List;

import org.jdom.Document;
import org.tolweb.treegrow.main.Contributor;

public interface ContributorUploader {
	@SuppressWarnings("unchecked")
    public List saveContributors(Document contributorsDocument, Contributor creatingContributor,
    		boolean updateExistingContributors);
}
