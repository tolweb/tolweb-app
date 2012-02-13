/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.Student;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ApproveRejectProjectPage extends AbstractTreehouseContributorPage implements UserInjectable {
    public abstract void setIsApprove(Boolean isApprove);
	public abstract Boolean getIsApprove();
    public abstract void setProject(ClassroomProject project);
    public abstract ClassroomProject getProject();
	public abstract void setComments(String value);
	public abstract String getComments();
	
	@SuppressWarnings("unchecked")
	public void commentsSubmit(IRequestCycle cycle) {	
		ClassroomProject project = getProject();
		String submittorEmail = project.getTeacher().getEmail();
		ContributorDAO contrDAO = getContributorDAO();
		
		boolean isApprove = getIsApprove().booleanValue();
		String initialPassword = project.getPassword();
        boolean useProjectPassword = StringUtils.notEmpty(initialPassword);        
		if (isApprove) {
		    project.setIsApproved(true);
		    Iterator it = project.getStudents().iterator();
		    initialPassword = getPasswordUtils().getMD5Hash(initialPassword);
		    while (it.hasNext()) {
		        Student stud = (Student) it.next();
                if (!useProjectPassword) {
                    initialPassword = getPasswordUtils().getInitialPasswordForStudent(stud);
                }
		        stud.setPassword(initialPassword);
		        contrDAO.saveContributor(stud);
		    }
			contrDAO.saveProject(project);		    
		} else {
		    contrDAO.deleteProject(project);
        }
		EditNewClassroomProjects newProjectsPage = (EditNewClassroomProjects) cycle.getPage("EditNewClassroomProjects");
		newProjectsPage.setApprovedOrRejectedProject(project);
		newProjectsPage.setWasApprovedProject(isApprove);
		newProjectsPage.setWasRejectedProject(!isApprove);
		newProjectsPage.setToEmail(submittorEmail);
		if (StringUtils.notEmpty(getComments())) {
		    newProjectsPage.setComments(getComments());
		}
		cycle.activate(newProjectsPage);
	}    
}
