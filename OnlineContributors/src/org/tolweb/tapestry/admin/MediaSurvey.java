package org.tolweb.tapestry.admin;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MediaSurveyInjectable;
import org.tolweb.treegrow.main.NodeImage;

public abstract class MediaSurvey extends BaseComponent implements PageBeginRenderListener, 
	ImageInjectable, MediaSurveyInjectable {
	
	public MediaSurvey() {
		setMediaSurveyObject(new org.tolweb.hibernate.MediaSurvey());
	}
	
	public abstract org.tolweb.hibernate.MediaSurvey getMediaSurveyObject();
	public abstract void setMediaSurveyObject(org.tolweb.hibernate.MediaSurvey survey);
	
	@Parameter 
	public abstract NodeImage getMedia();
	
	@Persist("session")
	public abstract Long getMediaId();
	public abstract void setMediaId(Long id);
	
	public abstract byte getRespondentAge();
	public abstract void setRespondentAge(byte age);
	
	public abstract String getMediaTypeDescription();
	public abstract void setMediaTypeDescription(String mediaTypeDesc);
	
	public abstract int getStudentCount();
	public abstract void setStudentCount(int count);

	public abstract String getEducationComments();
	public abstract void setEducationComments(String comments);
	
	public abstract String getSuggestions();
	public abstract void setSuggestions(String suggest);
	
	public abstract String getMediaUseComments();
	public abstract void setMediaUseComments(String comments);
	
	public abstract String getGeoLocation();
	public abstract void setGeoLocation(String location);
	
	public abstract boolean getIsElementarySchool();
	public abstract void setIsElementarySchool(boolean value);
	public abstract boolean getIsMiddleSchool();
	public abstract void setIsMiddleSchool(boolean value);
	public abstract boolean getIsHighSchool();
	public abstract void setIsHighSchool(boolean value);
	public abstract boolean getIsCollegeOrUniversity();
	public abstract void setIsCollegeOrUniversity(boolean value);
	public abstract boolean getIsInformalEducation();
	public abstract void setIsInformalEducation(boolean value);

	public abstract boolean getIsBrowsingForFun();
	public abstract void setIsBrowsingForFun(boolean value);
	public abstract boolean getIsMaterialsForOwnProjects();
	public abstract void setIsMaterialsForOwnProjects(boolean value);
	public abstract boolean getIsClassroomMaterials();
	public abstract void setIsClassroomMaterials(boolean value);
	public abstract boolean getIsForStudentStudies();
	public abstract void setIsForStudentStudies(boolean value);
	public abstract boolean getIsForParentLearning();
	public abstract void setIsForParentLearning(boolean value);
	
	public void pageBeginRender(PageEvent event) {
		if(getMedia() != null) {
			NodeImage mediaFile = getMedia();
			setMediaTypeDescription(mediaFile.getMediaTypeDescription());
			setMediaId(Long.valueOf(mediaFile.getId()));
		}
	}
	
	public void mediaSurveySubmit(IRequestCycle cycle) {
		System.out.println("{info} media survey submission is being processed [" + System.currentTimeMillis() + "]");
		System.out.println("{info} media survey relates to id:[" + getMediaId() + "]");
		getMediaSurveyObject().setMediaId(getMediaId());
		getMediaSurveyObject().setMediaUse(getMediaUseResponse());
		getMediaSurveyObject().setMediaComments(getMediaUseComments());
		getMediaSurveyObject().setStudentCount(Long.valueOf(getStudentCount()));
		getMediaSurveyObject().setEducationLevel(getEducationLevelResponse());
		getMediaSurveyObject().setEducationComments(getEducationComments());
		getMediaSurveyObject().setSuggestions(getSuggestions());
		getMediaSurveyObject().setRespondentLocation(getGeoLocation());
		getMediaSurveyObject().setRespondentAge(getRespondentAge());
		getMediaSurveyDAO().createMediaSurvey(getMediaSurveyObject());
		System.out.println("{info} media survey saved... [" + System.currentTimeMillis() + "]");

		ViewMediaSurveySubmitted surveyResult = (ViewMediaSurveySubmitted)cycle.getPage("admin/ViewMediaSurveySubmitted");
		cycle.activate(surveyResult);
	}
	
	private byte getEducationLevelResponse() {
		byte response = 0;
		response += (getIsElementarySchool()) ? org.tolweb.hibernate.MediaSurvey.KINDERGARDEN_ELEMENTARY : 0;
		response += (getIsMiddleSchool()) ? org.tolweb.hibernate.MediaSurvey.MIDDLE_SCHOOL : 0;
		response += (getIsHighSchool()) ? org.tolweb.hibernate.MediaSurvey.HIGH_SCHOOL : 0;
		response += (getIsCollegeOrUniversity()) ? org.tolweb.hibernate.MediaSurvey.COLLEGE_UNIVERSITY : 0;
		response += (getIsInformalEducation()) ? org.tolweb.hibernate.MediaSurvey.INFORMAL_EDUCATION : 0;
		return response;
	}
	
	private byte getMediaUseResponse() {
		byte response = 0;
		response += (getIsBrowsingForFun()) ? org.tolweb.hibernate.MediaSurvey.BROWSING_FOR_FUN : 0;
		response += (getIsMaterialsForOwnProjects()) ? org.tolweb.hibernate.MediaSurvey.MATERIALS_FOR_OWN_PROJECTS : 0;
		response += (getIsClassroomMaterials()) ? org.tolweb.hibernate.MediaSurvey.TEACHER_LIBRARIAN_CLASSROOM_MATERIALS : 0;
		response += (getIsForStudentStudies()) ? org.tolweb.hibernate.MediaSurvey.STUDENT_FOR_STUDIES : 0;
		response += (getIsForParentLearning()) ? org.tolweb.hibernate.MediaSurvey.PARENT_LEARNING : 0;
		return response; 
	}
}
