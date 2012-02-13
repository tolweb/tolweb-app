package org.tolweb.tapestry.admin;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.MediaSurvey;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MediaSurveyInjectable;
import org.tolweb.treegrow.main.NodeImage;

public abstract class ViewMediaSurveyDetail extends BasePage implements IExternalPage, MediaSurveyInjectable, ImageInjectable {
	
	public String getTitle() {
		String title = "Media Survey";
		title += (getDetail() != null) ? " #" + getDetail().getId() : "";
		return title;
	}

	public abstract MediaSurvey getDetail();
	public abstract void setDetail(MediaSurvey msurvey);
	
	public abstract NodeImage getImage();
	public abstract void setImage(NodeImage media);
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
        Long id = (Long) args[0];
        setDetail(getMediaSurveyDAO().getMediaSurveyWithId(id));
        setImage(getImageDAO().getImageWithId(getDetail().getMediaId().intValue()));
	}
}
