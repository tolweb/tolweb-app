package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.tapestry.injections.ImageInjectable;

public abstract class ContributorImagesPage extends ContributorDetailPage implements ImageInjectable {
	@SuppressWarnings("unchecked")
	public abstract void setImages(List value);
	
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
    	super.activateExternalPage(parameters, cycle);
    	setImages(getImageDAO().getImagesForContributor(getViewedContributor()));    	
    }
    public String getTitle() {
        if (getViewedContributor() != null) {
            return getViewedContributor().getDisplayName() + " Tree of Life Images ";
        } else {
            return "No Contributor Found";
        }
    }    
}
