package org.tolweb.tapestry;

import java.util.Iterator;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.ClassroomProject;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.Student;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.page.AccessoryPageContributor;

public abstract class ClassroomProjectPage extends BasePage implements IExternalPage, UserInjectable, AccessoryInjectable {
    public abstract ClassroomProject getProject();
    public abstract void setProject(ClassroomProject project);
    public abstract MappedAccessoryPage getCurrentTreehouse();

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        // TODO Auto-generated method stub
        Number projectId = (Number) parameters[0];
        ClassroomProject project = getContributorDAO().getProjectWithId(Long.valueOf(projectId.intValue()));
        setProject(project);
    }
    
    public int getNumStudents() {
        return getContributorDAO().getNumStudentsInProject(getProject());
    }
    
    public void addStudent(IRequestCycle cycle) {
        Student student = ClassroomRegistrationPage.createStudentWithPseudonym(getNumStudents() + 1, getProject(), getProject().getTeacher());
        if (!getProject().getUseProjectPassword()) {
	        // since this project is already approved, set the password to the initial password
	        student.setPassword(getPasswordUtils().getInitialPasswordForStudent(student)); 
        }
        getProject().addToStudents(student);
        saveProject();
    }
    
    public void searchForEditor(IRequestCycle cycle) {
        ContributorSearchPage searchPage = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
        searchPage.doActivate(cycle, AbstractWrappablePage.DEFAULT_WRAPPER, getPageName());
    }
    
    @SuppressWarnings("unchecked")
    public void addEditor(Contributor contr, IRequestCycle cycle) {
        getProject().getAdditionalEditors().add(contr);
        saveProject();
        cycle.activate(this);
    }
    
    @SuppressWarnings("unchecked")
    public void removeEditor(IRequestCycle cycle) {
        int id = ((Number) cycle.getListenerParameters()[0]).intValue();
        Contributor editorToDelete = null;
        for (Iterator iter = getProject().getAdditionalEditors().iterator(); iter.hasNext();) {
            Contributor editor = (Contributor) iter.next();
            if (editor.getId() == id) {
                editorToDelete = editor;
                break;
            }
        }
        if (editorToDelete != null) {
            getProject().getAdditionalEditors().remove(editorToDelete);
            saveProject();
        }
    }
    
    public void deleteTreehouse(IRequestCycle cycle) {
        int id = ((Number) cycle.getListenerParameters()[0]).intValue();
        MappedAccessoryPage page = getWorkingAccessoryPageDAO().getAccessoryPageWithId(id);
        DeleteTreehouseConfirm confirmPage = (DeleteTreehouseConfirm) cycle.getPage("DeleteTreehouseConfirm");
        confirmPage.setTreehouse(page);
        confirmPage.setReturnPageName(getPageName());
        cycle.activate(confirmPage);
    }
    
    public void saveProject() {
        getContributorDAO().saveProject(getProject());
    }
    
    @SuppressWarnings("unchecked")
    public List getTreehouses() {
        return getWorkingAccessoryPageDAO().getTreehousesForProject(getProject());
    }
    
    @SuppressWarnings("unchecked")
    public String getCurrentAuthorsString() {
    	StringBuilder authorsString = new StringBuilder();
    	authorsString.append("<ul class=\"nodisc\">");
        for (Iterator iter = getCurrentTreehouse().getContributors().iterator(); iter.hasNext();) {
            AccessoryPageContributor contr = (AccessoryPageContributor) iter.next();
            Contributor actualContr = contr.getContributor();
            if (actualContr != null) {
            	authorsString.append("<li>");
                String nameString = actualContr.getFirstName() + " " + actualContr.getLastName();
                if (Student.class.isInstance(actualContr)) {
                	authorsString.append(((Student) actualContr).getAlias());
                	authorsString.append(" (" + nameString + ")");
                } else {
                	authorsString.append(nameString);
                }
                authorsString.append("</li>");
            }
        }
        authorsString.append("</ul>");
        return authorsString.toString();
    }
}
