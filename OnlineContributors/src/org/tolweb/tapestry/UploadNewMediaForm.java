/*
 * Created on May 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.Movie;
import org.tolweb.hibernate.Sound;
import org.tolweb.treegrow.main.NodeImage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class UploadNewMediaForm extends UploadNewImageForm implements PageBeginRenderListener {
	public abstract Integer getNewMediaType();
	public abstract void setNewMediaType(Integer value);
    public static final Integer IMAGE = Integer.valueOf(0);
    public static final Integer SOUND = Integer.valueOf(1);
    public static final Integer MOVIE = Integer.valueOf(2);
    public static final Integer DOCUMENT = Integer.valueOf(3);
    
	public void pageBeginRender(PageEvent event) {
	    if (getNewMediaType() == null) {
	        setNewMediaType(IMAGE);
	    }
	}
	
	protected NodeImage constructNewImageInstance() {
	    Integer newMediaType = getNewMediaType();
	    if (newMediaType.equals(IMAGE)) {
	        return new NodeImage();
	    } else if (newMediaType.equals(SOUND)) {
	        return new Sound();
	    } else if (newMediaType.equals(MOVIE)) {
	        return new Movie();
	    } else {
	        return new Document();
	    }
	}
}
