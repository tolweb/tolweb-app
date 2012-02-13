package org.tolweb.tapestry;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.request.IUploadFile;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.tolweb.hivemind.ContributorUploader;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ContributorBatchUpload extends ImageZipUpload implements UserInjectable {
	public abstract boolean getUpdateExistingContributors();
	@InjectObject("service:org.tolweb.tapestry.ContributorUploader")
	public abstract ContributorUploader getContributorUploader();
	
	@SuppressWarnings("unchecked")
	public void doUpload(IRequestCycle cycle) {
        IUploadFile xmlFile = getXmlFile();
        String error = null;
        Document doc = null;
        if (xmlFile == null || xmlFile.getSize() == 0) {
            error = "You must provide an xml file which annotates the people you are uploading.";
        } else {
            try {
                doc = getDocumentFromUploadFile(xmlFile);
            } catch (JDOMException e) {
                error = "XML Processing Error: " + e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                error = "Input Error: " + e.getMessage();
                e.printStackTrace();
            }
        }
        Contributor contr = getContributor();
        if (error == null) {
            List results = getContributorUploader().saveContributors(doc, contr, getUpdateExistingContributors());
            Hashtable contributorsToMissingNames;
            List existingContributors;
            List contributorsToSendEmail;
            if (results.size() > 2) {
            	// peel off the second to last element, which is the missing names
            	contributorsToSendEmail = (List) results.remove(results.size() - 1);
            	existingContributors = (List) results.remove(results.size() - 1);            	
            	contributorsToMissingNames = (Hashtable) results.remove(results.size() - 1);
            	ContributorBatchUploadResults resultsPage = (ContributorBatchUploadResults) cycle.getPage("ContributorBatchUploadResults");
            	resultsPage.setContributors(results);
            	resultsPage.setExistingContributors(existingContributors);
            	resultsPage.setContributorsToMissingNames(contributorsToMissingNames);
            	resultsPage.setContributorIdsToSendEmail(contributorsToSendEmail);
            	cycle.activate(resultsPage);
            } else {
            	setError("There were no valid contributor records contained in the xml file you selected.  Please check your file and try again");
            }
        } else {
            setError(error);
        }        
	}
}
