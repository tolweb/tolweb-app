/*
 * Created on Nov 8, 2004
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

import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.Download;
import org.tolweb.treegrowserver.DownloadBuilder;
import org.tolweb.treegrowserver.UploadBatch;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreeXML extends AuthenticatedPage implements IExternalPage, 
		TreeGrowServerInjectable {
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
    	super.activateExternalPage(parameters, cycle);

        String groupName = getGroupName();
        String nodeId = getRequest().getParameterValue(RequestParameters.NODE_ID);
        String fromEditor = getRequest().getParameterValue(RequestParameters.FROM_EDITOR);
        String fetchSubtree = getRequest().getParameterValue(RequestParameters.FETCH_SUBTREE);
        String downloadId = getRequest().getParameterValue(RequestParameters.DOWNLOAD_ID);
        String batchId = getRequest().getParameterValue(RequestParameters.BATCH_ID);
        String versionParam = getRequest().getParameterValue(RequestParameters.TREEGROW_VERSION);

        boolean isFromEditor = fromEditor != null && fromEditor.equals(XMLConstants.ONE); 
        if (isFromEditor) {
        	// Check permissions
            Document failureDoc = authenticateUser(cycle);
        	if (failureDoc != null) {
        	    setResultDocument(failureDoc);
        		return;
        	}
        }
        DownloadBuilder builder = getDownloadBuilder();
        Contributor contr = getContributorFromCycle(cycle);        
        Document xmlDoc = null;
    	try {
            double version = 1.0;
            try {
            	version = Double.parseDouble(versionParam);
            } catch (Exception e) {}
            boolean shouldIncludeContributors = false;
            
    		String depth = getRequest().getParameterValue(RequestParameters.DEPTH);
    		String verbosity = getRequest().getParameterValue(RequestParameters.VERBOSITY);
    		int depthInt = Integer.parseInt(depth); 
    		boolean isComplete = verbosity != null && verbosity.equals(RequestParameters.COMPLETE);
    		boolean dontCheckSubmitted = false;
    		Download existingDownload = null;
    		if (fetchSubtree != null) {
    			existingDownload = getDownloadDAO().getDownloadWithId(new Long(downloadId));
    		}
    		if (batchId != null) {
    			UploadBatch batch = getUploadBatchDAO().getUploadBatchWithId(new Long(batchId));
    			if (batch != null && contr != null) {
    				dontCheckSubmitted = batch.getIsEditor(contr);
    			}
    		}
            // perform an additional check in case things are submitted via the online tools,
            // in which case the full UploadBatch doesn't get submitted
            if (!dontCheckSubmitted) {
                if (nodeId != null) {
                    dontCheckSubmitted = getPermissionChecker().checkHasEditingPermissionForNode(contr, new Long(nodeId));
                }
            }
    		if (nodeId != null) {
        		Long rootNodeId = new Long(nodeId);
        		xmlDoc = builder.buildDownload(rootNodeId, depthInt, isComplete, isFromEditor && depthInt != 0, 
        				contr, existingDownload, dontCheckSubmitted, shouldIncludeContributors);
        	} else if (groupName != null) {
        		xmlDoc = builder.buildDownload(groupName, depthInt, isComplete, isFromEditor && depthInt != 0, 
        				contr, existingDownload, dontCheckSubmitted, shouldIncludeContributors);
        	}
        } catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
        if (xmlDoc != null) {
        	setResultDocument(xmlDoc);
        }
    }
}
