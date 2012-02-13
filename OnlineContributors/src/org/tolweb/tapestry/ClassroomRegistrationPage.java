/*
 * Created on Dec 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.Student;
import org.tolweb.misc.ContributorLicenseInfo;
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
public abstract class ClassroomRegistrationPage extends AbstractTreehouseContributorPage implements PageBeginRenderListener, UserInjectable {
    public abstract ClassroomProject getProject();
    public abstract void setProject(ClassroomProject project);
    public abstract void setExistingPseudonym(boolean value);
    public abstract int getNumPseudonyms();
    public abstract void setNoPasswordSelected(boolean value);
    public abstract String getPassword();
    public abstract int getPasswordSelection();
    
    public static final int STUDENT_PASSWORD = 0;
    public static final int PROJECT_PASSWORD = 1;
    
    public boolean getHasLocation() {
        Contributor contr = getContributor();
        return StringUtils.notEmpty(contr.getCity()) || StringUtils.notEmpty(contr.getState()) ||
        	StringUtils.notEmpty(contr.getCountry());
    }
    
    public boolean getHasLocationAndInstitution() {
        Contributor contr = getContributor();        
        return getHasLocation() && StringUtils.notEmpty(contr.getInstitution());
    }
    
    public void pageBeginRender(PageEvent event) {
        if (getProject() == null) {
            ClassroomProject project = new ClassroomProject();
            project.setTeacher(getContributor());
            setProject(project);
        }
    }
    
    public void registerNewProject(IRequestCycle cycle) {
        setProject(null);
        cycle.activate(this);
    }
    
    public void submitProject(IRequestCycle cycle) {
        if (((ValidationDelegate) getBeans().getBean("delegate")).getHasErrors()) {
            return;
        } else if (!getHasLocation()) {
            return;
        } else {
            // create the students' accounts and go to the preview page
            ContributorDAO dao = getContributorDAO();
            // Save the contributor in case they added some location information
            dao.saveContributor(getContributor()); 
            ClassroomProject project = dao.getProjectWithPseudonym(getProject().getPseudonym());
            if (project != null) {
                setExistingPseudonym(true);
                return;
            }
            project = getProject();            
            // Check to make sure that they have typed a password if they selected
            // the project password option
            if (getPasswordSelection() == PROJECT_PASSWORD) {
                if (StringUtils.isEmpty(getPassword())) {
                    setNoPasswordSelected(true);
                    return;
                } else {
                    project.setPassword(getPassword());
                }
            }
            // Create the students and forward to the confirmation page
            for(int i = 0; i < getNumPseudonyms(); i++) {
                project.addToStudents(createStudentWithPseudonym(i + 1, getProject(), getContributor()));
            }
            ClassroomProjectConfirm confirmationPage = (ClassroomProjectConfirm) cycle.getPage("ClassroomProjectConfirm");
            confirmationPage.setProject(getProject());
            cycle.activate(confirmationPage);
        }
    }
    
    public String getCheckboxDisplayName() {
        return "<strong class=\"red\"> I have read the above</strong><strong> and take responsibility for ensuring that students do not upload information and media to the ToL database that would compromise their privacy.</strong>";
    }
    
    static Student createStudentWithPseudonym(int currentCount, ClassroomProject project, Contributor teacher) {
        Student stud = new Student();
        String alias = project.getPseudonym() + currentCount; 
        stud.setAlias(alias);
        stud.setFirstName("");
        stud.setLastName("");
        // need to set this as their alias for media editing purposes
        stud.setEmail(alias);
        stud.setPassword(PasswordUtils.INACTIVE_STUDENT_PASSWORD);
        stud.setTeacher(teacher);
        // image mod default, image use, note mod, note use are required as of 11/07
        stud.setImageModificationDefault(true);   
        stud.setImageUseDefault(Byte.valueOf(ContributorLicenseInfo.LICENSE_DEFAULT));
        stud.setNoteModificationDefault(true);
        stud.setNoteUseDefault(Byte.valueOf(ContributorLicenseInfo.LICENSE_DEFAULT));
        // set students to be 'approved' because they are approved by their 
        // project and not on an individual basis
        stud.setUnapprovedContributorType(Contributor.ANY_CONTRIBUTOR);
        return stud;
    }
}
