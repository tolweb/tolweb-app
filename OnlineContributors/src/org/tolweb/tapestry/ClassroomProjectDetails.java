/*
 * Created on Dec 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.hibernate.ClassroomProject;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ClassroomProjectDetails extends BaseComponent {
    public abstract ClassroomProject getProject();
    public String getStudentAgesString() {
        switch(getProject().getStudentAges()) {
            case ClassroomProject.STUDENTS_UNDER_13: return "Students under 13 years of age";
            case ClassroomProject.STUDENTS_UNDER_18: return "Students under 18 years of age";
            case ClassroomProject.STUDENTS_OVER_18: return "Students over 18 years of age";
            default: return "";
        }
    }
}
