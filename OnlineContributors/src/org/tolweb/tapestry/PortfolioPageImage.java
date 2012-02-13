package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.hibernate.PortfolioPage;
import org.tolweb.tapestry.injections.ImageInjectable;

public abstract class PortfolioPageImage extends BaseComponent implements ImageInjectable {
	public abstract PortfolioPage getPortfolioPage();
	
	public String getImageUrl() {
		return getImageDAO().getThumbnailUrlForImageWithId(getPortfolioPage().getImageId());
	}
}
