package org.tolweb.dao;

import java.util.List;

import org.tolweb.hibernate.MediaSurvey;

public class MediaSurveyDAOImpl extends BaseDAOImpl implements MediaSurveyDAO {

	public MediaSurvey getMediaSurveyWithId(Long value) {
        try {
        	MediaSurvey survey = (MediaSurvey) getHibernateTemplate().load(org.tolweb.hibernate.MediaSurvey.class, value);
            return survey;
        } catch (Exception e) {
        	e.printStackTrace();
//        	String s = e.toString();
            return null;
        }
	}
    
    public void saveMediaSurvey(MediaSurvey survey) {
    	getHibernateTemplate().update(survey);
    }

    public void createMediaSurvey(MediaSurvey survey) {
    	getHibernateTemplate().save(survey);
    }
    
	public List getAllMediaSurveys() {
		return getHibernateTemplate().find("from org.tolweb.hibernate.MediaSurvey");
	}
}
