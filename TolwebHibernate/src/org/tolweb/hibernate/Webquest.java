/*
 * Created on Jun 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.subclass discriminator-value="2"
 */
public class Webquest extends TeacherResource {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8908257343316829129L;
	private String processIntroduction;
    private String task;
    private String conclusion;
    private String teacherTips;
    
    private int processProgress;
    private int conclusionProgress;
    private int teacherPageProgress;
    
    
    /**
     * @hibernate.property column="process_introduction"
     * @return Returns the processIntroduction.
     */
    public String getProcessIntroduction() {
        return processIntroduction;
    }
    /**
     * @param processIntroduction The processIntroduction to set.
     */
    public void setProcessIntroduction(String processIntroduction) {
        this.processIntroduction = processIntroduction;
    }
    /**
     * @hibernate.property
     * @return Returns the task.
     */
    public String getTask() {
        return task;
    }
    /**
     * @param task The task to set.
     */
    public void setTask(String task) {
        this.task = task;
    }
    
    public String getResourceTypeString() {
        return "WebQuest";
    }
    /**
     * @hibernate.property
     * @return Returns the conclusion.
     */
    public String getConclusion() {
        return conclusion;
    }
    /**
     * @param conclusion The conclusion to set.
     */
    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }
    /**
     * @hibernate.property column="teacher_tips"
     * @return Returns the teacherTips.
     */
    public String getTeacherTips() {
        return teacherTips;
    }
    /**
     * @param teacherTips The teacherTips to set.
     */
    public void setTeacherTips(String teacherTips) {
        this.teacherTips = teacherTips;
    }
    public String getTreehouseTypeString() {
        return "webquest";
    }
    /**
     * @hibernate.property column="evaluation_progress"
     * @return Returns the evaluationProgress.
     */
    public int getConclusionProgress() {
        return conclusionProgress;
    }
    /**
     * @param evaluationProgress The evaluationProgress to set.
     */
    public void setConclusionProgress(int evaluationProgress) {
        this.conclusionProgress = evaluationProgress;
    }
    /**
     * @hibernate.property column="process_progress"
     * @return Returns the processProgress.
     */
    public int getProcessProgress() {
        return processProgress;
    }
    /**
     * @param processProgress The processProgress to set.
     */
    public void setProcessProgress(int processProgress) {
        this.processProgress = processProgress;
    }
    /**
     * @hibernate.property column="teacher_page_progress"
     * @return Returns the teacherPageProgress.
     */
    public int getTeacherPageProgress() {
        return teacherPageProgress;
    }
    /**
     * @param teacherPageProgress The teacherPageProgress to set.
     */
    public void setTeacherPageProgress(int teacherPageProgress) {
        this.teacherPageProgress = teacherPageProgress;
    }
}
