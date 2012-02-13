/*
 * Created on Dec 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.class table="ClassroomProjects"
 */
public class ClassroomProject implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6578244243786267744L;
	public static final int STUDENTS_UNDER_13 = 0;
    public static final int STUDENTS_UNDER_18 = 1;
    public static final int STUDENTS_OVER_18 = 2;
    
    private Contributor teacher;
    private String name;
    private String classGroup;
    private String description;
    private Set students;
    private Long projectId;
    private int studentAges;
    private String pseudonym;
    private String password;
    private boolean isApproved;
    private boolean isClosed;
    private Set additionalEditors;
    
	/**
	 * 
	 * @hibernate.id generator-class="native" column="project_id" unsaved-value="null"
	 * @return
	 */
	public Long getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId The projectId to set.
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
    /**
     * @hibernate.property column="class_group"
     * @return
     */
    public String getClassGroup() {
        return classGroup;
    }
    public void setClassGroup(String classGroup) {
        this.classGroup = classGroup;
    }
    /**
     * @hibernate.property
     * @return
     */
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @hibernate.property
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
	/**
	 * @hibernate.many-to-one column="teacher_id" class="org.tolweb.treegrow.main.Contributor" cascade="none"
	 * @return Returns the teacher
	 */
    public Contributor getTeacher() {
        return teacher;
    }
    public void setTeacher(Contributor teacher) {
        this.teacher = teacher;
    }
    /**
     * @hibernate.set cascade="all-delete-orphan"
     * @hibernate.collection-key column="project_id"
     * @hibernate.collection-one-to-many class="org.tolweb.hibernate.Student"
     * @hibernate.cache usage="nonstrict-read-write"
     */    
    public Set getStudents() {
    	if (students == null) {
    		students = new HashSet();
    	}
        return students;
    }
    public void setStudents(Set students) {
        this.students = students;
    }
    public void addToStudents(Student student) {
    	if (student != null) {
    	    getStudents().add(student);
    	    student.setProject(this);
    	}
    }
    /**
     * @hibernate.property column="student_ages"
     * @return
     */
    public int getStudentAges() {
        return studentAges;
    }
    public void setStudentAges(int studentAges) {
        this.studentAges = studentAges;
    }
    /**
     * @hibernate.property
     * @return
     */
    public String getPseudonym() {
        return pseudonym;
    }
    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }
    /**
     * @hibernate.property column="is_approved"
     * @return
     */
    public boolean getIsApproved() {
        return isApproved;
    }
    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }
    /**
     * @hibernate.property column="is_closed"
     * @return Returns the isClosed.
     */
    public boolean getIsClosed() {
        return isClosed;
    }
    /**
     * @param isClosed The isClosed to set.
     */
    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }    
    /**
     * @hibernate.property
     * @return
     */
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean getUseProjectPassword() {
        return StringUtils.notEmpty(getPassword());
    }
    /**
     * @hibernate.set table="Additional_Contributors_To_Projects"
     * @hibernate.collection-key column="project_id"
     * @hibernate.collection-many-to-many class="org.tolweb.treegrow.main.Contributor" column="contributor_id"
     * @hibernate.cache usage="nonstrict-read-write"
     */
    public Set getAdditionalEditors() {
        if (additionalEditors == null) {
            additionalEditors = new HashSet();
        }
        return additionalEditors;
    }
    /**
     * @param additionalEditors The additionalEditors to set.
     */
    public void setAdditionalEditors(Set additionalEditors) {
        this.additionalEditors = additionalEditors;
    }
    public void addToAdditionalEditors(Contributor contr) {
        getAdditionalEditors().add(contr);
    }
}
