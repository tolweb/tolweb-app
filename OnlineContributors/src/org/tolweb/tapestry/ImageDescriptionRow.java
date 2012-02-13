package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.NodeImage;

public abstract class ImageDescriptionRow extends BaseComponent implements PageInjectable, BaseInjectable {
	public abstract int getHeight();
	
	public NodeImage getImage() {
		return ((ImageOptions) getPage()).getImage();
	}
	public String getNonJavascriptInsertImageUrl() {
		return getUrlBuilder().getNonJavascriptInsertImageHtml(getImage(), 
				getHeight() > 0 ? Integer.valueOf(getHeight()) : null);
	}
	public String getJavascriptInsertImageUrl() {
		return getUrlBuilder().getJavascriptInsertImageUrl(getImage(), 
				getHeight(), ((ImageOptions) getPage()).getIsResize(), true);
	}
}
