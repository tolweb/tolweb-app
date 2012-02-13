/*
 * Created on Jan 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.request.RequestContext;
import org.jdom.Document;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.RequestParameters;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AuthenticatedPage extends XMLPage implements RequestInjectable, UserInjectable {
    public Document authenticateUser(IRequestCycle cycle) {
        String password = getRequest().getParameterValue(RequestParameters.PASSWORD);
        Contributor contr  = getContributorFromCycle(cycle);
    	PermissionChecker checker = getPermissionChecker();
    	Document failureDoc = checker.checkPermission(contr, password);
    	return failureDoc;
    }
    
    public Contributor getContributorFromCycle(IRequestCycle cycle) {
        String userEmail = getRequest().getParameterValue(RequestParameters.USER_ID);        
        Contributor contr = getContributorDAO().getContributorWithEmail(userEmail);
        return contr;
    }
}
