/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.request.RequestContext;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.DownloadCheckin;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Checkin extends AuthenticatedPage implements TreeGrowServerInjectable {
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        super.activateExternalPage(parameters, cycle);
        Document errorDoc = authenticateUser(cycle);
        String downloadId = getRequest().getParameterValue(RequestParameters.DOWNLOAD_ID);
        String rootNodeId = getRequest().getParameterValue(RequestParameters.ROOTNODE_ID);
        if (errorDoc != null) {
            setResultDocument(errorDoc);
        } else {
            DownloadCheckin checkin = getDownloadCheckin();
            if (StringUtils.notEmpty(rootNodeId)) {
                checkin.doSubtreeDownloadCheckin(new Long(downloadId), new Long(rootNodeId));
            } else {
                checkin.doFullDownloadCheckin(new Long(downloadId));
            }
            Document resultDoc = new Document();
            Element successElement = new Element(XMLConstants.SUCCESS);
            successElement.setText(XMLConstants.SUCCESS);
            resultDoc.setRootElement(successElement);
            setResultDocument(resultDoc);
        }
    }
}
