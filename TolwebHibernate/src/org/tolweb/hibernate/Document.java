/*
 * Created on Apr 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.hibernate;

import org.tolweb.treegrow.main.DescriptiveMedia;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @hibernate.subclass discriminator-value="3"
 */
public class Document extends NodeImage implements DescriptiveMedia {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4699538549532149269L;
	protected String description;
    protected String otherLanguage;
    protected String otherContent;
    protected boolean isBeginner;
    protected boolean isIntermediate;
    protected boolean isAdvanced;
    protected boolean isResearch;
    protected boolean isLiterature;
    protected boolean isPresentation;
    protected boolean isTeacherResource;
    protected boolean isLesson;
    protected boolean isLessonSupport;
    protected boolean isStandards;
    protected boolean isVocabulary;
    protected boolean isLecture;
    protected boolean isDataset;
    protected boolean isFiction;
    protected boolean isNonFiction;
    
    public void setValues(NodeImage other, boolean doThumbnail, boolean copyIdsAndLocs) {
        super.setValues(other, doThumbnail, copyIdsAndLocs);
        if (Document.class.isInstance(other)) {
            Document otherDoc = (Document) other;
            setDescription(otherDoc.getDescription());
            setTitle(otherDoc.getTitle());
            setIsBeginner(otherDoc.getIsBeginner());
            setIsIntermediate(otherDoc.getIsIntermediate());
            setIsAdvanced(otherDoc.getIsAdvanced());
            setIsResearch(otherDoc.getIsResearch());
            setIsLiterature(otherDoc.getIsLiterature());
            setIsPresentation(otherDoc.getIsPresentation());
            setIsTeacherResource(otherDoc.getIsTeacherResource());
            setIsLesson(otherDoc.getIsLesson());
            setIsLessonSupport(otherDoc.getIsLessonSupport());
            setIsStandards(otherDoc.getIsStandards());
            setIsVocabulary(otherDoc.getIsVocabulary());
            setIsLecture(otherDoc.getIsLecture());
            setIsDataset(otherDoc.getIsDataset());
        }
    }
    
    /**
     * @hibernate.property
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @hibernate.property column="is_advanced"
     * @return Returns the isAdvanced.
     */
    public boolean getIsAdvanced() {
        return isAdvanced;
    }
    /**
     * @param isAdvanced The isAdvanced to set.
     */
    public void setIsAdvanced(boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
    }
    /**
     * @hibernate.property column="is_beginner"
     * @return Returns the isBeginner.
     */
    public boolean getIsBeginner() {
        return isBeginner;
    }
    /**
     * @param isBeginner The isBeginner to set.
     */
    public void setIsBeginner(boolean isBeginner) {
        this.isBeginner = isBeginner;
    }
    /**
     * @hibernate.property column="is_dataset"
     * @return Returns the isDataset.
     */
    public boolean getIsDataset() {
        return isDataset;
    }
    /**
     * @param isDataset The isDataset to set.
     */
    public void setIsDataset(boolean isDataset) {
        this.isDataset = isDataset;
    }
    /**
     * @hibernate.property column="is_intermediate"
     * @return Returns the isIntermediate.
     */
    public boolean getIsIntermediate() {
        return isIntermediate;
    }
    /**
     * @param isIntermediate The isIntermediate to set.
     */
    public void setIsIntermediate(boolean isIntermediate) {
        this.isIntermediate = isIntermediate;
    }
    /**
     * @hibernate.property column="is_lecture"
     * @return Returns the isLecture.
     */
    public boolean getIsLecture() {
        return isLecture;
    }
    /**
     * @param isLecture The isLecture to set.
     */
    public void setIsLecture(boolean isLecture) {
        this.isLecture = isLecture;
    }
    /**
     * @hibernate.property column="is_lesson"
     * @return Returns the isLesson.
     */
    public boolean getIsLesson() {
        return isLesson;
    }
    /**
     * @param isLesson The isLesson to set.
     */
    public void setIsLesson(boolean isLesson) {
        this.isLesson = isLesson;
    }
    /**
     * @hibernate.property column="is_lesson_support"
     * @return Returns the isLessonSupport.
     */
    public boolean getIsLessonSupport() {
        return isLessonSupport;
    }
    /**
     * @param isLessonSupport The isLessonSupport to set.
     */
    public void setIsLessonSupport(boolean isLessonSupport) {
        this.isLessonSupport = isLessonSupport;
    }
    /**
     * @hibernate.property column="is_literature"
     * @return Returns the isLiterature.
     */
    public boolean getIsLiterature() {
        return isLiterature;
    }
    /**
     * @param isLiterature The isLiterature to set.
     */
    public void setIsLiterature(boolean isLiterature) {
        this.isLiterature = isLiterature;
    }
    /**
     * @hibernate.property column="is_presentation"
     * @return Returns the isPresentation.
     */
    public boolean getIsPresentation() {
        return isPresentation;
    }
    /**
     * @param isPresentation The isPresentation to set.
     */
    public void setIsPresentation(boolean isPresentation) {
        this.isPresentation = isPresentation;
    }
    /**
     * @hibernate.property column="is_research"
     * @return Returns the isResearch.
     */
    public boolean getIsResearch() {
        return isResearch;
    }
    /**
     * @param isResearch The isResearch to set.
     */
    public void setIsResearch(boolean isResearch) {
        this.isResearch = isResearch;
    }
    /**
     * @hibernate.property column="is_standards"
     * @return Returns the isStandards.
     */
    public boolean getIsStandards() {
        return isStandards;
    }
    /**
     * @param isStandards The isStandards to set.
     */
    public void setIsStandards(boolean isStandards) {
        this.isStandards = isStandards;
    }
    /**
     * @hibernate.property column="is_teacher_resource"
     * @return Returns the isTeacherResource.
     */
    public boolean getIsTeacherResource() {
        return isTeacherResource;
    }
    /**
     * @param isTeacherResource The isTeacherResource to set.
     */
    public void setIsTeacherResource(boolean isTeacherResource) {
        this.isTeacherResource = isTeacherResource;
    }
    /**
     * @hibernate.property column="is_vocabulary"
     * @return Returns the isVocabulary.
     */
    public boolean getIsVocabulary() {
        return isVocabulary;
    }
    /**
     * @param isVocabulary The isVocabulary to set.
     */
    public void setIsVocabulary(boolean isVocabulary) {
        this.isVocabulary = isVocabulary;
    }
    
    public String getMediaTypeDescription() {
        return "document";
    }
    
    public int getMediaType() {
        return NodeImage.DOCUMENT;
    }
    /**
     * @hibernate.property column="is_fiction"
     * @return Returns the isFiction.
     */
    public boolean getIsFiction() {
        return isFiction;
    }
    /**
     * @param isFiction The isFiction to set.
     */
    public void setIsFiction(boolean isFiction) {
        this.isFiction = isFiction;
    }
    /**
     * @hibernate.property column="is_non_fiction"
     * @return Returns the isNonFiction.
     */
    public boolean getIsNonFiction() {
        return isNonFiction;
    }
    /**
     * @param isNonFiction The isNonFiction to set.
     */
    public void setIsNonFiction(boolean isNonFiction) {
        this.isNonFiction = isNonFiction;
    }
    /**
     * @hibernate.property column="other_content"
     * @return Returns the otherContent.
     */
    public String getOtherContent() {
        return otherContent;
    }
    /**
     * @param otherContent The otherContent to set.
     */
    public void setOtherContent(String otherContent) {
        this.otherContent = otherContent;
    }
}
