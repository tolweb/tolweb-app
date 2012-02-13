package org.tolweb.tapestry.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.NodeImage;

public abstract class ViewAllUnattachedMedia extends BasePage implements IExternalPage, ImageInjectable, BaseInjectable {
	public abstract NodeImage getCurrentMedia();
	public abstract void setCurrentMedia(NodeImage media);

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
			
	}

	public String getTitle() {
		return "Unattached Tree of Life Media";
	}
   
    @SuppressWarnings("unchecked")
    public List getUnattachedMedia() {
    	Set ids = getImageDAO().getUnattachedMedia();
    	List media = getImageDAO().getImagesWithIds(ids);
    	Collections.sort(media, new Comparator<NodeImage>() {
    		// this will get us results sorted from larger to smallest by id
			public int compare(NodeImage lhs, NodeImage rhs) {
				return rhs.getId() - lhs.getId();
			} 
    	});
    	return media;
    }
    
    public NodeImage getImage() {
    	return getImageDAO().getImageWithId(getCurrentMedia().getId());
    }
    
    public String getColumnsString() {
    	String colsString = "!ID:getId(), !thumbnail, !data";
    	return colsString;
    }
    
    public String getTableId() {
    	return "unattachedMediaTable";
    }
    
    public String getMediaLink() {
    	return "http://tolweb.org/media/" + getCurrentMedia().getId();
    }	
}
