package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.treegrow.main.NodeImage;

public abstract class ViewPortfolioPageImage extends BaseComponent implements ImageInjectable, BaseInjectable {
    public abstract PortfolioPage getPortfolioPage();
    public abstract void setImage(NodeImage value);
    public abstract NodeImage getImage();    
    
    public boolean getHasImage() {
        if (getPortfolioPage().getHasImage()) {
            setImage(getImageDAO().getImageWithId(getPortfolioPage().getImageId().intValue()));
            return true;
        } else {
            return false;
        }
    }
    public String getCopyrightString() {
        return getTextPreparer().getCopyrightOwnerString(getImage(), true, false, false);
    }
    public String getThumbnailUrl() {
    	return getImageDAO().getThumbnailUrlForImageWithId(getPortfolioPage().getImageId());
    }
}
