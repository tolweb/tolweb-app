package org.tolweb.tapestry.admin;

import java.util.List;

import org.tolweb.dao.BaseDAO;
import org.tolweb.hibernate.MediaSurvey;
import org.tolweb.tapestry.AbstractViewAllObjects;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MediaSurveyInjectable;
import org.tolweb.treegrow.main.NodeImage;

public abstract class ViewAllMediaSurveys extends AbstractViewAllObjects implements MediaSurveyInjectable, ImageInjectable {

	public abstract MediaSurvey getCurrentMediaSurvey();
	
    public BaseDAO getDAO() {
        return getMediaSurveyDAO();
    }
    
    @SuppressWarnings("unchecked")
    public Class getObjectClass() {
        return MediaSurvey.class;
    }
    
    @SuppressWarnings("unchecked")
    public List getMediaSurveys() {
    	return getMediaSurveyDAO().getAllMediaSurveys();
    }
    
    public NodeImage getImage() {
    	return getImageDAO().getImageWithId(getCurrentMediaSurvey().getMediaId().intValue());
    }
    
    public String getColumnsString() {
    	String colsString =  "!view, ID:id, Media:mediaId, Age:respondentAgeString, Location:respondentLocation, Media Usage:mediaUsageResponseString, ";
    	colsString += "Usage Cmt:hasMediaComments, Student #:studentCount, Education:educationLevelString, Edu Cmt:hasEducationComments, Suggest:hasSuggestions, Date:timestamp";
    	return colsString;
    }
    
    public String getTableId() {
    	return "mediaSurveysTable";
    }
    
    public String getMediaLink() {
    	return "http://tolweb.org/media/" + getCurrentMediaSurvey().getMediaId().toString();
    }
}
