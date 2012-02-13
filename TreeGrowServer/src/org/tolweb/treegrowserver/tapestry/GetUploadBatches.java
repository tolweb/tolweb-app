/*
 * Created on Jan 10, 2005
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

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class GetUploadBatches extends AuthenticatedPage implements IExternalPage,
		TreeGrowServerInjectable {
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        super.activateExternalPage(parameters, cycle);
        Document errorDoc = authenticateUser(cycle);
        if (errorDoc != null) {
            setResultDocument(errorDoc);
        } else {
            Contributor contributor = getContributorFromCycle(cycle);
	    	Document resultsDoc = getBatchResultsBuilder().buildUploadBatchResultsDocument(contributor);
	    	setResultDocument(resultsDoc);
        }
    }
}
