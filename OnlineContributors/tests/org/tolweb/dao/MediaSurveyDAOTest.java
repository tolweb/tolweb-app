package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.MediaSurvey;

public class MediaSurveyDAOTest extends ApplicationContextTestAbstract {
	private MediaSurveyDAO dao;
	
	public MediaSurveyDAOTest(String name) {
		super(name);
		dao = (MediaSurveyDAO) context.getBean("mediaSurveyDAO");
	}
	
	public void testMediaSurveyCreate() {
		MediaSurvey survey = new MediaSurvey();
		String comments = "something witty...";
		survey.setMediaId(new Long(1));
		survey.setMediaUse(MediaSurvey.BROWSING_FOR_FUN);
		survey.setStudentCount(new Long(140));
		survey.setEducationLevel(MediaSurvey.COLLEGE_UNIVERSITY);
		survey.setEducationComments(comments);
		survey.setMediaComments(comments);
		survey.setRespondentAge((byte)31);
		survey.setRespondentLocation("Tucson, AZ");
		dao.createMediaSurvey(survey);
	}
	
	@SuppressWarnings("unchecked")
	public void testFindSomeMediaSurveys() {
		List surveys = dao.getAllMediaSurveys();
		assertTrue(surveys != null);
		assertTrue(surveys.size() > 0);
	}
	
	public void testRetrieveMediaSurveyWithId() {
		MediaSurvey survey = dao.getMediaSurveyWithId(new Long(1));
		assertTrue(survey != null);
		assertTrue(survey.getId() == 1L);
	}
}
