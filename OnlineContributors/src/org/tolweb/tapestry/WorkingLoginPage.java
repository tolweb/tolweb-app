/*
 * Created on Nov 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.callback.ExternalCallback;
import org.apache.tapestry.html.BasePage;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class WorkingLoginPage extends BasePage implements CookieInjectable, UserInjectable {
    private static final String USERNAME = "ToL";
    private static final String PASSWORD = "develop";
    
    public abstract void setError(boolean value);
    public abstract void setCallback(ExternalCallback callback);
    public abstract ExternalCallback getCallback();
    public abstract String getUsername();
    public abstract String getPassword();
    
    public void loginFormSubmit(IRequestCycle cycle) {
        String email = getUsername();
        String password = getPassword();
        PasswordUtils utils = getPasswordUtils();
        boolean isContributor = utils.checkPassword(email, password);
        if (isContributor) {
            Contributor contr = getContributorDAO().getContributorWithEmail(email);
            getCookieAndContributorSource().loginContributor(contr);
        }
        if (isContributor || (getUsername().equals(USERNAME) && getPassword().equals(PASSWORD))) {
        	getCookieAndContributorSource().addWorkingCookie();
            //((OnlineContributorsEngine) getEngine()).addWorkingCookieToRequestCycle(cycle);
            if (getCallback() != null) {
                getCallback().performCallback(cycle);
            } else {
                
            }
        } else {
            setError(true);
        }
    }
}
