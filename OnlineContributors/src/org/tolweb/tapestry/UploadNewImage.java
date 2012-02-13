/*
 * Created on Mar 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.html.BasePage;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class UploadNewImage extends BasePage implements IExternalPage, 
		RequestInjectable, UserInjectable, CookieInjectable, MiscInjectable {
    public abstract int getNewObjectId();
    public abstract void setNodeId(Long value);
    @Persist("client")
    public abstract Long getNodeId();
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
	    String nodeId = getRequest().getParameterValue(RequestParameters.NODE_ID);
	    Contributor contr = getCookieAndContributorSource().authenticateExternalPage(cycle);
	    if (contr != null) {
	        if (StringUtils.notEmpty(nodeId)) {
	            setNodeId(Long.valueOf(nodeId));
	        }
	    } else {
	        throw new PageRedirectException("ScientificContributorsLogin");
	    }
    }
    
    public String getEditObjectUrl() {
    	Contributor contr = getContributor();    	
        Object[] parameters = {contr.getEmail(), contr.getPassword(), Integer.valueOf(getNewObjectId())};
    	String url = getTapestryHelper().getExternalServiceUrl("EditImageData", parameters);                
        return url;
    }
}
