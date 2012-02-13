/*
 * Created on Dec 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ClassroomProjectConfirm extends AbstractTreehouseContributorPage implements 
		UserInjectable, TreehouseInjectable, BaseInjectable {
    public abstract void setProject(ClassroomProject value);
    public abstract ClassroomProject getProject();
    public abstract void setProjectSubmitted(boolean value);
    
    public void submitProject(IRequestCycle cycle) {
        ContributorDAO contrDAO = getContributorDAO();
        contrDAO.addProject(getProject(), getMiscEditHistoryDAO());
        setProjectSubmitted(true);
    }
    
    public void editProject(IRequestCycle cycle) {
        cycle.activate("ClassroomRegistrationPage");
    }
}
