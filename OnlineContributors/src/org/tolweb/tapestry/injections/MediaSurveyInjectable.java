package org.tolweb.tapestry.injections;

import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.dao.MediaSurveyDAO;

/**
 * An injectable interface for media survey to gather data about 
 * the usage of media by the users of tolweb.org.
 *
 * Date: 1/8/2008
 * @author lenards
 * 
 */
public interface MediaSurveyInjectable {
	@InjectObject("spring:mediaSurveyDAO")
	public MediaSurveyDAO getMediaSurveyDAO();
}
