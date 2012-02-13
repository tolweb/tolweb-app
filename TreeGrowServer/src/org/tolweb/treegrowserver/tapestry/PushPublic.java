/*
 * Created on Jan 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.jdom.Document;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrowserver.UploadBatch;
import org.tolweb.treegrowserver.dao.UploadBatchDAO;

/**
 * @author dmandel
 *
 * THIS CLASS IS OBSOLETE -- DELETE WHEN SURE ITS OK
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PushPublic extends AuthenticatedPage implements IExternalPage,
		TreeGrowServerInjectable{
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        /*super.activateExternalPage(parameters, cycle);
        Document errorDoc = authenticateUser(cycle);
        if (errorDoc != null) {
            setResultDocument(errorDoc);
        } else {
            Contributor contr = getContributorFromCycle(cycle);
            Long batchId = new Long(getRequest().getParameterValue(RequestParameters.BATCH_ID));
            UploadBatchDAO dao = getUploadBatchDAO();
            UploadBatch batch = dao.getUploadBatchWithId(batchId);
            if (batch.getIsEditor(contr)) {
                // Go ahead and submit it 
                try {
                    getBatchPusher().pushBatchToPublic(batchId);
                    setSuccessDocumentAsResult();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                
            } else {
                // Someone who doesn't have permission tried to push a batch public.
                // Throw a runtime exception so the error page gets emailed.
                throw new RuntimeException("Contributor without privileges: " + contr.getName() + " tried to submit batch id: " + batchId);
            }
        }*/
    }
}
