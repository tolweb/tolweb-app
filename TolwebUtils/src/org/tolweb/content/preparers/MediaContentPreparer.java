package org.tolweb.content.preparers;
 
import java.util.SortedSet;
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Text;

import org.tolweb.content.helpers.ContentParameters;
import org.tolweb.content.helpers.DaoBundle;
import org.tolweb.content.helpers.MediaContentElements;
import org.tolweb.content.helpers.PageContentElements;
import org.tolweb.content.preparers.MediaCoPreparer;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

public class MediaContentPreparer extends ContentPreparer {
	public static final String NS = "http://www.eol.org/transfer/content/0.1";
	
	public MediaContentPreparer(ContentParameters params, ImageDAO imageDao, NodeDAO publicNodeDao) {
		setParams(params);
		setImageDao(imageDao);
		setPublicNodeDao(publicNodeDao);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void processContent() {
		if (hasValidParameters()) {
			DaoBundle daos = new DaoBundle();
			daos.setImageDAO(getImageDao());
			NodeImage media = retrieveMediaByCriteria();
			
			if (media != null) {
				ContributorLicenseInfo licInfo = new ContributorLicenseInfo(media.getUsePermission());
				// determine if this "media" file retrieved should be served - in other words, 
				// does the license of the media match/satsify the license from the paramters
				if(licInfo.matchesContentLicenseClass(getParams().getRequestedLicenseClass())) {
					finishContentProcessing(media, daos);
				} else {
					handleContentLicenseMismatch();
				}
			} else {
				handleContentProcessingError();
			}
		}
	}

	private void finishContentProcessing(NodeImage media, DaoBundle daos) {
		Element root;
		root = createElement(MediaContentElements.ROOT_SUCCESS);
		setPreparedDocument(new Document(root));
		
		Element mediaElement = createElement(MediaContentElements.MEDIA);
		Element contributorsElement = createElement(PageContentElements.CONTRIBUTORS);
		Element sourcesElement = createElement(PageContentElements.SOURCES);
		
		generateMediaElement(mediaElement, media, daos);
		generateContributorElement(contributorsElement, media, daos);
		generateSourcesElement(sourcesElement, media, daos);	
		
		root.appendChild(mediaElement);
		root.appendChild(contributorsElement);
		root.appendChild(sourcesElement);
	}
	
	private void handleContentProcessingError() {
		System.out.println("\t content processing error - issues with fetching media-id: " + getParams().getMediaId());
		Element root = new Element(PageContentElements.ROOT_ERROR, PageContentPreparer.NS);
		setPreparedDocument(new Document(root));
		Element message = new Element("message", PageContentPreparer.NS);
		message.appendChild(new Text("an error has occurred while attempting to fetch information related to media-id: " + getParams().getMediaId()));
		root.appendChild(message);
	}	
	
	private void handleContentLicenseMismatch() {
		System.out.println("\t content license class mismatch occurred... ");
		Element root = new Element(PageContentElements.ROOT_SUCCESS, PageContentPreparer.NS);
		setPreparedDocument(new Document(root));
		Element message = new Element("message", PageContentPreparer.NS);
		message.appendChild(new Text("Sorry, requested content not available through this service."));
		message.addAttribute(new Attribute("license-class", getParams().getRequestedLicenseClass().toString()));
		root.appendChild(message);
	}
	
	protected boolean hasValidParameters() {
		return getParams() != null && (getParams().getMediaId() != null || StringUtils.notEmpty(getParams().getGroupName()));
	}
	
	protected NodeImage retrieveMediaByCriteria() {
		return getImageDao().getImageWithId(getParams().getMediaId().intValue());
	}
	
	protected void generateMediaElement(Element mediaElement, NodeImage media, DaoBundle daos) {
		MediaCoPreparer mediaCoPrep = new MediaCoPreparer();
		mediaCoPrep.setContentSource(media, daos, mediaElement);
		mediaCoPrep.setPreparerNamespace(MediaContentPreparer.NS);
		mediaCoPrep.processContent();
	}
	
	@SuppressWarnings("unchecked")
	protected void generateContributorElement(Element contributorsElement, NodeImage media, DaoBundle daos) {
		ContributorCoPreparer contrCoPrep = new ContributorCoPreparer();
		SortedSet contributors = getContributorsList(media);				
		contrCoPrep.setContentSource(contributors, daos, contributorsElement);
		contrCoPrep.setPreparerNamespace(MediaContentPreparer.NS);
		contrCoPrep.processContent();
	}
	
	protected void generateSourcesElement(Element sourcesElement, NodeImage media, DaoBundle daos) {
		SourceCoPreparer srcCoPrep = new SourceCoPreparer();
		srcCoPrep.setContentSource(new Object(), daos, sourcesElement);
		srcCoPrep.setPreparerNamespace(MediaContentPreparer.NS);
		srcCoPrep.processContent();	
	}
	
	protected Element createElement(String elementName) {
		return createElement(elementName, NS);
	}
	
	@SuppressWarnings("unchecked")
	private SortedSet getContributorsList(NodeImage media) {
		TreeSet tset = new TreeSet();
		tset.add(media.getContributor());
		return tset;
	}
}
