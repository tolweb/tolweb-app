/*
 * Created on Dec 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.subclass discriminator-value="1" 
 */
public class Student extends Contributor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1570681813296932645L;
	private String alias;
	private Contributor teacher;
	private ClassroomProject project;	
	
	/**
	 * @hibernate.property
	 * @return Returns the alias.
	 */
	public String getAlias() {
		return alias;
	}
	/**
	 * @param alias The alias to set.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	/**
	 * @hibernate.many-to-one column="teacher_id" class="org.tolweb.treegrow.main.Contributor" cascade="none"
	 * @return Returns the teacher
	 */
	public Contributor getTeacher() {
		return teacher;
	}
	/**
	 * @param teacher The teacher to set.
	 */
	public void setTeacher(Contributor teacher) {
		this.teacher = teacher;
	}
	
    /**
     * @hibernate.many-to-one column="project_id" class="org.tolweb.hibernate.ClassroomProject" cascade="none"
     * @return Returns the project.
     */
    public ClassroomProject getProject() {
        return project;
    }
    /**
     * @param project The project to set.
     */
    public void setProject(ClassroomProject project) {
        this.project = project;
    }	
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Student)) {
			return false;
		} else {
			Student other = (Student) o;
			if (getAlias() == null || other.getAlias() == null) {
				return false;
			} else {
				return getAlias().equals(other.getAlias());
			}
		}
	}
	
	public int hashCode() {
		if (getAlias() != null) {
			return getAlias().hashCode();
		} else {
			return System.identityHashCode(this);
		}
	}
	
    /** 
     * Overridden so students' actual names don't show up on the site
     */
	public String getDisplayName() {
        return getAlias();
    }
	protected String getContributorTypeString(byte contributorType) {
		return "Class Project Treehouse";
	}
}
