/*
 * Created on May 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.subclass discriminator-value="1"
 */
public class TeacherResource extends MappedAccessoryPage {
    /**
	 * 
	 */
	private static final long serialVersionUID = 919458177025323378L;
	public static final int LESSON = 0;
    public static final int ACTIVITY = 1;
    public static final int PROJECT = 2;
    public static final int OTHER = 3;
    public static final int WEBQUEST = 4;
    public static final int UNIT = 5;
    
    private int resourceType;
    private String learnerSectionIntro;
    private String learningObjective;
    private String preparation;
    private String priorKnowledge;
    private String physicalMaterials;
    private String introduction;
    private String lesson;
    private String evaluation;
    
    private int supportMaterialsProgress;
	protected SortedSet supportMaterials;
	protected SortedSet supportMaterialDocuments;
    
    public String getResourceTypeString() {
        switch (getResourceType()) {
        	case LESSON: return "Lesson";
        	case ACTIVITY: return "Activity";
        	case PROJECT: return "Project";
        	case UNIT: return "Unit";
        	case WEBQUEST: return "WebQuest";
        	default: return "Other";
        }
    }
    
    public boolean getHasLearnerSection() {
        if (StringUtils.notEmpty(getLearnerSectionIntro())) {
            return true;
        } else {
            return getHasLearnerSupportMaterials();
        }
    }
    
    public boolean getHasLearnerSupportMaterials() {
        return getLearnerSupportMaterials().size() > 0 || getLearnerSupportMaterialDocuments().size() > 0;
    }
    
    public boolean getHasOverview() {
        return StringUtils.notEmpty(getDescription());
    }
    
    public boolean getHasAnyLearningInfo() {
        return super.getHasAnyLearningInfo();
    }
    
    public boolean getHasIntroduction() {
        return StringUtils.notEmpty(getIntroduction());
    }
    
    public boolean getHasPreparation() {
        return StringUtils.notEmpty(getPreparation());
    }
    
    public boolean getHasPhysicalObjects() {
        return StringUtils.notEmpty(getPhysicalMaterials());
    }
    
    public boolean getHasPriorKnowledge() {
        return StringUtils.notEmpty(getPriorKnowledge());
    }    
    
    public boolean getHasLesson() {
        return StringUtils.notEmpty(getLesson());
    }
    
    public boolean getHasEvaluation() {
        return StringUtils.notEmpty(getEvaluation());
    }
    
    public boolean getHasPortfolio() {
        return getPortfolio() != null && !getPortfolio().getIsEmpty();
    }
    
    public boolean getHasLearnerSectionIntro() {
        return StringUtils.notEmpty(getLearnerSectionIntro());
    }
    
    public boolean getHasSupportMaterials() {
        for (Iterator iter = getSupportMaterials().iterator(); iter.hasNext();) {
            SupportMaterial support = (SupportMaterial) iter.next();
            if (StringUtils.notEmpty(support.getText()) || StringUtils.notEmpty(support.getTitle())) {
                return true;
            }
        }
        // Now check linked docs
        for (Iterator iter = getSupportMaterialDocuments().iterator(); iter.hasNext();) {
            SupportMaterialDocument supportDoc = (SupportMaterialDocument) iter.next();
            if (supportDoc.getDocumentId().intValue() > 0) {
                return true;
            }
        }
        // Didn't find any, so it doesn't really have any
        return false;
    }
      
    private Set getLearnerMaterials(Collection supportMats) {
        SortedSet set = new TreeSet();
        // Loop through all of the support materials and see if any have 
        // been marked as showing up in the learner section.
        for (Iterator iter = supportMats.iterator(); iter.hasNext();) {
            AbstractSupportMaterial mat = (AbstractSupportMaterial) iter.next();
            if (mat.getIsLearner()) {
                set.add(mat);
            }
        }        
        return set;        
    }
    
    public Set getLearnerSupportMaterials() {
        return getLearnerMaterials(getSupportMaterials());
    }
    
    public Set getLearnerSupportMaterialDocuments() {
        return getLearnerMaterials(getSupportMaterialDocuments());
    }
    
    /**
     * @hibernate.property column="resource_type"
     * @return Returns the resourceType.
     */
    public int getResourceType() {
        return resourceType;
    }
    /**
     * @param resourceType The resourceType to set.
     */
    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }
    /**
     * Cover method since the terminology is used differently in different places
     * @return
     */
    public String getOverview() {
        return getDescription();
    }  
    /**
	 * @hibernate.set table="SupportMaterials" lazy="false" order-by="support_order asc" sort="natural" cascade="all"
	 * @hibernate.collection-composite-element class="org.tolweb.hibernate.SupportMaterial"
	 * @hibernate.collection-key column="teacher_resource_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    public SortedSet getSupportMaterials() {
        if (supportMaterials == null) {
            supportMaterials = new TreeSet();
        }
        return supportMaterials;
    }
    /**
     * @param supportMaterials The supportMaterials to set.
     */
    public void setSupportMaterials(SortedSet supportMaterials) {
        this.supportMaterials = supportMaterials;
    }
    /**
     * @hibernate.set table="SupportMaterialDocuments" lazy="false" order-by="support_order asc" sort="natural" cascade="all"
	 * @hibernate.collection-composite-element class="org.tolweb.hibernate.SupportMaterialDocument" dynamic-update="true"
	 * @hibernate.collection-key column="teacher_resource_id"
	 * @hibernate.collection-cache usage="nonstrict-read-write"
     */
    public SortedSet getSupportMaterialDocuments() {
        if (supportMaterialDocuments == null) {
            supportMaterialDocuments = new TreeSet();
        }
        return supportMaterialDocuments;
    }
    /**
     * @param supportMaterialDocuments The supportMaterialDocuments to set.
     */
    public void setSupportMaterialDocuments(SortedSet supportMaterialDocuments) {
        this.supportMaterialDocuments = supportMaterialDocuments;
    }
    /**
     * @hibernate.property column="learner_section_intro"
     * @return Returns the learnerSectionIntro.
     */
    public String getLearnerSectionIntro() {
        return learnerSectionIntro;
    }
    /**
     * @param learnerSectionIntro The learnerSectionIntro to set.
     */
    public void setLearnerSectionIntro(String learnerSectionIntro) {
        this.learnerSectionIntro = learnerSectionIntro;
    }
    /**
     * @hibernate.property
     * @return Returns the evaluation.
     */
    public String getEvaluation() {
        return evaluation;
    }
    /**
     * @param evaluation The evaluation to set.
     */
    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }
    /**
     * @hibernate.property
     * @return Returns the introduction.
     */
    public String getIntroduction() {
        return introduction;
    }
    /**
     * @param introduction The introduction to set.
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    /**
     * @hibernate.property column="learning_objective"
     * @return Returns the learningObjective.
     */
    public String getLearningObjective() {
        return learningObjective;
    }
    /**
     * @param learningObjective The learningObjective to set.
     */
    public void setLearningObjective(String learningObjective) {
        this.learningObjective = learningObjective;
    }
    /**
     * @hibernate.property
     * @return Returns the lesson.
     */
    public String getLesson() {
        return lesson;
    }
    /**
     * @param lesson The lesson to set.
     */
    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
    /**
     * @hibernate.property column="physical_materials"
     * @return Returns the physicalMaterials.
     */
    public String getPhysicalMaterials() {
        return physicalMaterials;
    }
    /**
     * @param physicalMaterials The physicalMaterials to set.
     */
    public void setPhysicalMaterials(String physicalMaterials) {
        this.physicalMaterials = physicalMaterials;
    }
    /**
     * @hibernate.property
     * @return Returns the preparation.
     */
    public String getPreparation() {
        return preparation;
    }
    /**
     * @param preparation The preparation to set.
     */
    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }
    /**
     * @hibernate.property column="prior_knowledge"
     * @return Returns the priorKnowledge.
     */
    public String getPriorKnowledge() {
        return priorKnowledge;
    }
    /**
     * @param priorKnowledge The priorKnowledge to set.
     */
    public void setPriorKnowledge(String priorKnowledge) {
        this.priorKnowledge = priorKnowledge;
    }
    /**
     * @hibernate.property column="support_materials_progress"
     * @return Returns the supportMaterialsProgress.
     */
    public int getSupportMaterialsProgress() {
        return supportMaterialsProgress;
    }
    /**
     * @param supportMaterialsProgress The supportMaterialsProgress to set.
     */
    public void setSupportMaterialsProgress(int supportMaterialsProgress) {
        this.supportMaterialsProgress = supportMaterialsProgress;
    }
}
