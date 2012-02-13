/*
 * Created on Jan 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.request.RequestContext;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.NodeDAO;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.BatchSubmitter;
import org.tolweb.treegrowserver.UploadBatch;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;

/**
 * @author dmandel
 * 
 * THIS CLASS IS OBSOLETE -- WE SHOULD DELETE IT SOON
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class BatchSubmission extends AuthenticatedPage implements IExternalPage, RequestInjectable, 
		NodeInjectable, TreeGrowServerInjectable {
    public abstract void setDisappearedNodes(List nodes);
    public abstract void setRootGroupName(String name);
    public abstract void setSubmittedContributor(Contributor contr);
    public abstract void setBatchId(Long value);
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        /*super.activateExternalPage(parameters, cycle);
        Document errorDoc = authenticateUser(cycle);
        if (errorDoc != null) {
            setResultDocument(errorDoc);
        } else {
            Contributor contributor = getContributorFromCycle(cycle);
            Long batchId = new Long(getRequest().getParameterValue(RequestParameters.BATCH_ID));
            String unsubmit = getRequest().getParameterValue(RequestParameters.UNSUBMIT);
            BatchSubmitter submitter = getBatchSubmitter();
            if (unsubmit == null) {
	            List missingNodes = submitter.submitBatchForPublication(batchId, contributor);
	            setDisappearedNodes(missingNodes);
	            UploadBatchDAO uploadBatchDAO = getUploadBatchDAO();
	            UploadBatch batch = uploadBatchDAO.getUploadBatchWithId(batchId);
	            Long nodeId = uploadBatchDAO.getRootNodeIdForBatch(batch);
	            NodeDAO workingNodeDAO = getWorkingNodeDAO();
	            setRootGroupName(workingNodeDAO.getNameForNodeWithId(nodeId));
	            setSubmittedContributor(contributor);
	            setBatchId(batchId);
            } else {
                submitter.unsubmitBatchForPublication(batchId, contributor);
            }
            setSuccessDocumentAsResult();
        }*/
    }
}
