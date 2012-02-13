package org.tolweb.content.services;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Text;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.content.helpers.ContentParameters;
import org.tolweb.content.helpers.MediaContentElements;
import org.tolweb.content.licensing.ContentLicenseClass;
import org.tolweb.content.preparers.ContentPreparer;
import org.tolweb.content.preparers.MediaContentPreparer;
import org.tolweb.content.preparers.TillusContentPreparer;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * 
 * @author lenards
 * onlinecontributors/app?page=content/MediaContentService&service=external&mediaId=3
 * onlinecontributors/app?page=content/MediaContentService&service=external&group=archostemata 
 */
public abstract class MediaContentService extends XMLContentBaseService implements ImageInjectable, NodeInjectable, PageInjectable {
	public static final String SERVICE_NAME = "tol-media-content";
	// Service Parameters
	private static final String MEDIA_ID = "mediaId";
	private static final String GROUP_NAME = "group";
	private static final String LICENSE = "license";
	
	private static final String PARAMETER_ERROR_MESSAGE = "Error - no request parameters were provided." + 
														  "  Media Content Service only supports `" + 
														  MEDIA_ID + "` and `" + LICENSE + "` as parameters. " +
														  "Please consult ToL Web Service Documentation for more info: http://webservices.tolweb.org/";
	
	public String getName() {
		return SERVICE_NAME;
	}

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String mediaIdText = getRequest().getParameterValue(MEDIA_ID);
		String groupNameText = getRequest().getParameterValue(GROUP_NAME);
		String licenseText = getRequest().getParameterValue(LICENSE);
		
		if (StringUtils.notEmpty(mediaIdText)) {
			Long mediaId = Long.parseLong(mediaIdText);
			ContentLicenseClass licClass = ContentLicenseClass.createContentLicenseClass(licenseText);
			ContentParameters cparams = new ContentParameters(mediaId, licClass);
			ContentPreparer contentPrep = new MediaContentPreparer(cparams, getImageDAO(), getPublicNodeDAO());
			contentPrep.processContent();
			Document document = contentPrep.getDocument();
			addServiceNameAttribute(document);
			setDocumentString(document.toXML());
		} else if (StringUtils.notEmpty(groupNameText)) {
			ContentParameters cparams = new ContentParameters();
			cparams.setGroupName(groupNameText);
			ContentPreparer contentPrep = new TillusContentPreparer(cparams, getImageDAO(), getPublicNodeDAO(), getPublicPageDAO());
			contentPrep.processContent();
			Document document = contentPrep.getDocument();
			addServiceNameAttribute(document);
			setDocumentString(document.toXML());
		} else {
			Element rootError = new Element(MediaContentElements.ROOT_ERROR, MediaContentPreparer.NS);
			rootError.appendChild(new Text(PARAMETER_ERROR_MESSAGE));
			Document document = new Document(rootError);
			addServiceNameAttribute(document);
			setDocumentString(document.toXML());
		}
	}
	
	private void addServiceNameAttribute(Document doc) {
		doc.getRootElement().addAttribute(new Attribute("service", getName()));
	}
	
	//private void 
}
