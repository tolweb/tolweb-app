package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.MediaSurvey;

public interface MediaSurveyDAO extends BaseDAO {
	public void createMediaSurvey(MediaSurvey survey);
	public void saveMediaSurvey(MediaSurvey survey);
	public MediaSurvey getMediaSurveyWithId(Long id);
	public List getAllMediaSurveys();
}
