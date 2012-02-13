/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.valid.IValidationDelegate;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.Student;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class StudentNameEntryPage extends AbstractTreehouseContributorPage implements UserInjectable {
    public abstract String getPassword1();
    public abstract void setPassword1(String value);
    public abstract String getPassword2();
    public abstract void setPassword2(String value);
    public abstract void setPasswordsDontMatch(boolean value);
    
    public void nameFormSubmit(IRequestCycle cycle) {
        Contributor contr = getContributor();
        if (((IValidationDelegate) getBeans().getBean("delegate")).getHasErrors()) {
            return;
        }
        PasswordUtils pwUtils = getPasswordUtils();
        String password = contr.getPassword();
        // if they already have a password, there is no need to check if they've
        // filled in the password fields as they are not present
        if (StringUtils.isEmpty(password) || password.equals(pwUtils.getInitialPasswordForStudent((Student) contr))) {
            if (StringUtils.notEmpty(getPassword1()) && getPassword1().equals(getPassword2())) {
                getPasswordUtils().setContributorPassword(contr, getPassword1());
            } else {
                setPasswordsDontMatch(true);
                setPassword1(null);
                setPassword2(null);
                return;
            }
        }
        ContributorDAO dao = getContributorDAO();
        dao.saveContributor(contr);
        cycle.activate("TreehouseMaterialsManager");
    }
}
