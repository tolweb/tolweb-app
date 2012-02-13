package org.tolweb.tapestry.admin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.InjectObject;
import org.tolweb.flickrimport.FlickrImporter;
import org.tolweb.tapestry.EditImageData;
import org.tolweb.tapestry.ImageDataConfirm;
import org.tolweb.treegrow.main.NodeImage;

public abstract class FlickrImport extends EditImageData {
	@InjectObject("service:org.tolweb.tapestry.FlickrImporter")
	public abstract FlickrImporter getImporter();
	public abstract String getUrlOrId();
	
	private static Pattern URL_PATTERN;
	private static Pattern ALT_URL_PATTERN;
	private static final NodeImage DUMMY_IMAGE = new NodeImage();
	
	static {
		URL_PATTERN = Pattern.compile("/([^/]+)/?$");
		ALT_URL_PATTERN = Pattern.compile("photos/[A-Za-z0-9@_\\-]*/[0-9]*/");
	}
	
    public NodeImage getMedia() {
        return DUMMY_IMAGE;
    }	
	
	public IPage importPhoto() {
		String urlOrId = getUrlOrId();
		// check to see if it's a url or an imageid
		if (!org.apache.commons.lang.StringUtils.isNumeric(urlOrId)) {
			// not numeric so parse out the image id
			// http://www.flickr.com/photos/roger/5525813/
			Matcher alt = ALT_URL_PATTERN.matcher(urlOrId);
			if (alt.find()) {
				String grp = alt.group();
				Matcher id = URL_PATTERN.matcher(grp);
				if (id.find()) {
					urlOrId = id.group(1);
				} else {
					return null;
				}
					
			} else {
				Matcher matcher = URL_PATTERN.matcher(urlOrId);
				if (matcher.find()) {
					urlOrId = matcher.group(1);
				} else {
					setErrorMessage("bad url");
					return null;
				}
			}
		}
		NodeImage image = getImporter().importFlickrPicture(urlOrId);
		if (image == null) {
			setErrorMessage("parsing error");
			return null;
		} else {
			ImageDataConfirm confirmPage = (ImageDataConfirm) goToConfirmPage();
			confirmPage.setImage(image);
			// don't return to us as we want to go back to the actual edit page 
			confirmPage.setReturnPageName("EditImageData");
			return confirmPage;
		}
	}
}
