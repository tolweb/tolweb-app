package org.tolweb.tapestry;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.Student;
import org.tolweb.misc.PasswordUtils;
import org.tolweb.tapestry.injections.UserInjectable;

public abstract class CloseProjectConfirm extends BasePage implements UserInjectable {
    public abstract void setProjectId(Long value);
    public abstract Long getProjectId();
    public abstract void setProject(ClassroomProject project);
    
    public void cancel(IRequestCycle cycle) {
        cycle.activate("ClassroomProjectsMaterialsManager");
    }
    
    @SuppressWarnings("unchecked")
    public void closeProject(IRequestCycle cycle) {
        ClassroomProject project = getContributorDAO().getProjectWithId(getProjectId());
        if (project != null) {
            project.setIsClosed(true);
            for (Iterator iter = project.getStudents().iterator(); iter.hasNext();) {
                Student nextStudent = (Student) iter.next();
                nextStudent.setPassword(PasswordUtils.INACTIVE_STUDENT_PASSWORD);
            }
            getContributorDAO().saveProject(project);
        }
        cycle.activate("ClassroomProjectsMaterialsManager");
    }
}
