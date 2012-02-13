/*
 * Created on Nov 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.request.RequestContext;
import org.jdom.Document;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.NodeSearchResultsBuilder;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class NodeSearch extends AuthenticatedPage implements IExternalPage,
		TreeGrowServerInjectable {
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        super.activateExternalPage(parameters, cycle);
        String version = getRequest().getParameterValue(RequestParameters.TREEGROW_VERSION);
        /**
         * Proof of concept version checking code.........
         * double treeGrowVersion = Double.parseDouble(version); 
        if (treeGrowVersion < 2.1) {
            setResultDocument(((Global) getGlobal()).getServerXMLWriter().getBadVersionDocument());
            return;
        }*/
        String exact = getRequest().getParameterValue(RequestParameters.EXACT);
        boolean isExact = (exact == null) || exact != null && exact.equals(XMLConstants.ONE);
        NodeSearchResultsBuilder builder = getNodeSearchResultsBuilder();
        String checkedIds =  getRequest().getParameterValue(RequestParameters.CHECKED_IDS);
        Document resultDoc;
        if (StringUtils.notEmpty(checkedIds)) {
            List nodeIds = new ArrayList();
            // Create a list of node ids
            String[] ids = checkedIds.split(",");
            for (int i = 0; i < ids.length; i++) {
                String currentId = ids[i];
                nodeIds.add(new Long(currentId));
            }
            resultDoc = builder.getNodeUpdates(nodeIds, getContributorFromCycle(cycle), getPassword()); 
        } else {
            resultDoc = builder.buildSearchResultsDocument(getGroupName(), getUserName(), getPassword(), isExact);
        }
        setResultDocument(resultDoc);
    }
}
