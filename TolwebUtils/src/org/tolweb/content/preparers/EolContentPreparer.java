package org.tolweb.content.preparers;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Text;

import org.apache.commons.lang.StringEscapeUtils;
import org.tolweb.attributes.Phylesis;
import org.tolweb.attributes.PositionConfidence;
import org.tolweb.content.helpers.AgentRole;
import org.tolweb.content.helpers.ContentParameters;
import org.tolweb.content.helpers.DaoBundle;
import org.tolweb.content.helpers.EmbeddedMediaHandler;
import org.tolweb.content.helpers.HtmlTaxonListMaker;
import org.tolweb.content.helpers.MediaContentRepresentation;
import org.tolweb.content.helpers.MediaThumbnailMapper;
import org.tolweb.content.helpers.PageContentElements;
import org.tolweb.content.helpers.RightsHolder;
import org.tolweb.content.helpers.SourceInformation;
import org.tolweb.content.helpers.TextContentRepresentation;
import org.tolweb.content.helpers.TextLinkMaker;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.ForeignDatabase;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.misc.ImageUtils;
import org.tolweb.names.ScientificNamer;
import org.tolweb.tapestry.CitationCreator;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

public class EolContentPreparer extends PageContentPreparer {
	public static final String NS = "http://www.eol.org/transfer/content/0.2";
	public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
	public static final String NS_TOL = "http://tolweb.org/webservices/content/0.1";
	public static final String NS_DUBLIN_CORE = "http://purl.org/dc/elements/1.1/";
	public static final String NS_DUBLIN_CORE_TERMS = "http://purl.org/dc/terms/";
	public static final String NS_GEO = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String NS_DARWIN_CORE = "http://rs.tdwg.org/dwc/dwcore/";
	public static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
	public static final String EOL_XSI_SCHEMA_LOC = "http://www.eol.org/transfer/content/0.2 " +
													"http://services.eol.org/schema/content_0_2.xsd";

	// format expected in the xml:
	// <dcterms:created>2000-01-01T00:00:00</dcterms:created>
	public SimpleDateFormat dcTermsFormat;

	/* xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 * xsi:schemaLocation="http://www.eol.org/transfer/content/0.1
	 * http://services.eol.org/schema/content_0_1.xsd"	 */

	private CitationCreator citationCreator; 
	
	public EolContentPreparer(ContentParameters params, PageDAO pageDao,
			NodeDAO publicNodeDao, ImageDAO imageDao, ImageUtils imageUtils) {
		super(params, pageDao, publicNodeDao, imageDao, imageUtils);
		dcTermsFormat = new SimpleDateFormat("yyyy-MMM-d HH:mm:ss");		
	}

	@Override
	protected void finishContentProcessing(MappedPage mpage, DaoBundle daos) {
		Element root = initializeDocumentRoot();

		MappedNode mnode = mpage.getMappedNode();
		
		Element taxon = establishTaxonElementData(mpage, daos, mnode);
		
		root.appendChild(taxon);
	}

	private Element establishTaxonElementData(MappedPage mpage, DaoBundle daos,
			MappedNode mnode) {
		Element taxon = new Element("taxon", NS);

		addTaxonIdentifierElement(mnode, taxon);
		addTaxonSourceElement(mnode, taxon);
		addTaxonScientificNameElement(mnode, taxon);
		addTaxonCreationElements(mpage, taxon);
		// currently not included in the response because it violated EOL schema
		addTolNodeDataElement(mpage, mnode);
		// currently not included in the response because it violated EOL schema		
		addTolOtherNamesDataElements(mnode);
		// references are now part of the taxon element
		addReferences(mpage, taxon);
		addDataObjects(mpage, mnode, daos, taxon);
		return taxon;
	}

	private void addReferences(MappedPage mpage, Element taxon) {
		List<String> references = createReferencesList(mpage);		

		for (String refText : references) {
			Element refEl = new Element("reference", NS);
			refText = escapeText(refText);
			refEl.appendChild(new Text(refText.trim()));
			taxon.appendChild(refEl);
		}		
	}

	private String escapeText(String text) {
		//text = StringEscapeUtils.escapeXml(text);
		text = StringEscapeUtils.escapeHtml(text);
		return text;
	}

	private List<String> createReferencesList(MappedPage mpage) {
		List<String> references = new ArrayList<String>();
		String input = mpage.getReferences();

		if (input != null) {
			LineNumberReader reader = null;
			String currentLine;
			try {
				reader = new LineNumberReader(new StringReader(input));
				while ((currentLine = reader.readLine()) != null) {
					if (StringUtils.notEmpty(currentLine)) {
						references.add(currentLine);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (Exception e) {	}
			}
		}
		return references;
	}

	private void addDataObjects(MappedPage mpage, MappedNode mnode, DaoBundle daos, Element taxon) {
		HashMap<Integer, String> embeddedMedia = new HashMap<Integer, String>();
		
		addTextSectionDataObjects(mpage, daos, taxon, embeddedMedia);
		// Embedded vs. Attached - embedded media appears within a page/text-section, attached 
		// media is associated with the node and may or may not appear in a page or text-section.
		
		// Embedded Media contains tree diagrams and illustrations that EOL does not want
		// so this is now being "excluded" via a comment out (COMMENTED!) lenards 2009 March 26
		//addEmbeddedMediaDataObjects(daos, taxon, embeddedMedia);
		addAttachedMediaDataObjects(mpage, mnode, daos, taxon, embeddedMedia.keySet());
	}

	@SuppressWarnings("unchecked")
	private void addAttachedMediaDataObjects(MappedPage mpage, MappedNode mnode,  
			DaoBundle daos, Element taxon, Collection excludeIds) {
		// we only want to attach media for leaf nodes 
		if (mnode != null && mnode.getIsLeaf()) {
			// get mediaIds that are attached and not from foreign databases
			List mediaFiles = daos.getImageDAO().getNativeAttachedImagesForNode(mnode.getNodeId(), excludeIds, true);
			MediaThumbnailMapper mapper = new MediaThumbnailMapper(daos.getImageDAO(), mediaFiles);
			Map<Integer, String> mediaThumbs = mapper.createMap();

			processMedia(daos, taxon, mediaThumbs, mediaFiles);
		}
	}
	
	/**
	 * Includes any media embedded in a mappedpage. 
	 * 
	 * Commented out by request of EOL.  They do not wish to have embedded media included as dataObjects. 
	 * 
	 * @param daos data access bundle required to fetch data
	 * @param taxon the XML element to add all embedded media data objects to
	 * @param embeddedMedia the list of embedded media
	 */
//	@SuppressWarnings("unchecked")
//	private void addEmbeddedMediaDataObjects(DaoBundle daos, Element taxon,
//			HashMap<Integer, String> embeddedMedia) {
//		Set<Integer> embeddedMediaIds = embeddedMedia.keySet();
//		List mediaFiles = daos.getImageDAO().getImagesWithIds(embeddedMediaIds);		
//		
//		processMedia(daos, taxon, embeddedMedia, mediaFiles);
//	}

	private boolean requestMatchesLicenseFor(NodeImage mediaFile) {
		ContributorLicenseInfo licInfo = new ContributorLicenseInfo(mediaFile.getUsePermission());
		return licInfo.matchesContentLicenseClass(getParams().getRequestedLicenseClass());
	}
	
	@SuppressWarnings("unchecked")	
	private void processMedia(DaoBundle daos, Element taxon,
			Map<Integer, String> mediaIdsToThumbUrls, List mediaFiles) {
		if (mediaFiles != null) {
			for (Iterator mediaItr = mediaFiles.iterator(); mediaItr.hasNext();) {
				NodeImage mediaFile = (NodeImage) mediaItr.next();
				
				if (requestMatchesLicenseFor(mediaFile)) {
					addMediaDataObject(daos, taxon, mediaIdsToThumbUrls,
							mediaFile);
				}
			}
		}
	}

	private void addMediaDataObject(DaoBundle daos, Element taxon,
			Map<Integer, String> mediaIdsToThumbUrls, NodeImage mediaFile) {
		String thumbnailURL = mediaIdsToThumbUrls.get(Integer.valueOf(
				mediaFile.getId()));
		String mediaURL = getMediaUrl(mediaFile, daos);
		MediaContentRepresentation mediaRep = new MediaContentRepresentation(
				mediaFile, mediaURL);
		if (shouldInclude(mediaRep)) {
			Element dataObject = createMediaDataObject(daos, mediaFile,
				thumbnailURL, mediaRep);
			taxon.appendChild(dataObject);
		}
	}

	private boolean shouldInclude(MediaContentRepresentation mediaRep) {
		return mediaRep.getCopyrightHolder() != null &&
			!mediaRep.isExcludedMediaType();
	}
	
	private String getMediaUrl(NodeImage mediaFile, DaoBundle daos) {
		if (mediaFile.getMediaType() == NodeImage.IMAGE) {
			ImageVersion maxVersion = daos.getImageDAO().getMaxAllowedVersion(mediaFile);
		    return daos.getImageUtils().getVersionUrl(maxVersion);			
		} else {
			return getMediaPrefix(mediaFile) + mediaFile.getLocation();
		}
		
	}
	
	private String getMediaPrefix(NodeImage mediaFile) {
		if (mediaFile.getMediaType() == NodeImage.MOVIE) {
			return "/ToLmovies/";
		} else if (mediaFile.getMediaType() == NodeImage.SOUND) {
			return "/ToLsounds/";
		} else if (mediaFile.getMediaType() == NodeImage.DOCUMENT) {
			return "/ToLdocuments/";
		} else {
			return "/ToLimages/";
		}
	}

	private Element createMediaDataObject(DaoBundle daos,
			NodeImage mediaFile, String thumbnailURL,
			MediaContentRepresentation mediaRep) {
		Element dataObject = new Element("dataObject", NS);
		addMediaIdentifierElement(mediaRep, dataObject);
		addMediaDataTypeElement(mediaRep, dataObject);
		addMediaMimeTypeElement(mediaRep, dataObject);
		addMediaAgentElement(mediaRep, dataObject);
		addMediaCreationElements(mediaRep, dataObject);
		addMediaTitleElement(mediaRep, dataObject);
		addMediaLanguageElement(mediaRep, dataObject);
		addMediaLicenseElement(mediaRep, dataObject);
		addMediaRightsHolderElement(mediaRep, dataObject);
		addMediaSourceElement(mediaRep, dataObject);
		// currently not being added because it violates EOL schema
		addMediaTolSourceData(daos, mediaFile);
		addMediaDescriptionElement(mediaRep, dataObject);
		addMediaURLElement(mediaRep, dataObject);
		addMediaThumbnailElement(thumbnailURL, dataObject);
		addMediaGeoLocElement(mediaRep, dataObject);
		addMediaReferencesElement(mediaRep, dataObject);
		return dataObject;
	}

	private void addMediaReferencesElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		if (mediaRep.getReferences() != null
				&& !mediaRep.getReferences().isEmpty()) {
			Element mediaRefEl = new Element("reference", NS);
			// only one reference is assoicated with media elements
			String txtMediaRef = escapeText(mediaRep
					.getReferences().get(0));
			mediaRefEl.appendChild(new Text(txtMediaRef));
			dataObject.appendChild(mediaRefEl);
		}
	}

	private void addMediaGeoLocElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element geoLocEl = new Element("location", NS);
		geoLocEl.appendChild(new Text(mediaRep.getGeoLocation()));
		dataObject.appendChild(geoLocEl);
	}

	private void addMediaThumbnailElement(String thumbnailURL,
			Element dataObject) {
		// the thumbnailURL is the value in the key/value pair modeled in embeddedMedia
		Element thumbnailUrlEl = new Element("thumbnailURL", NS);
		thumbnailUrlEl.appendChild(new Text(thumbnailURL));
		dataObject.appendChild(thumbnailUrlEl);
	}

	private void addMediaURLElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element mediaUrlEl = new Element("mediaURL", NS);
		mediaUrlEl.appendChild(new Text(mediaRep.getMediaURL()));
		dataObject.appendChild(mediaUrlEl);
	}

	private void addMediaDescriptionElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element mediaDescEl = new Element("dc:description",
				NS_DUBLIN_CORE);
		if (StringUtils.notEmpty(mediaRep.getLanguage())) {
			mediaDescEl.addAttribute(new Attribute("xml:lang",
					NS_XML, mediaRep.getLanguage()));
		}
		if (mediaRep.hasDescription()) {
			mediaDescEl.appendChild(new Text(mediaRep.getDescription()));
			dataObject.appendChild(mediaDescEl);
		}
	}

	private void addMediaTolSourceData(DaoBundle daos,
			NodeImage mediaFile) {
		SourceInformation srcInfo;
		if (mediaFile.getSourceDbId() != null) {
			ForeignDatabase foreignDB = daos.getImageDAO()
					.getForeignDatabaseWithId(
							mediaFile.getSourceDbId());
			srcInfo = new SourceInformation(mediaFile, foreignDB);
		} else {
			srcInfo = new SourceInformation(mediaFile);
		}
		Element mediaSource = new Element("tol:source-info", NS_TOL);
		mediaSource.addAttribute(new Attribute("tol:tol-native",
				NS_TOL, Boolean.valueOf(srcInfo.isTolNative())
						.toString()));
		if (!srcInfo.isTolNative()) {
			mediaSource.addAttribute(new Attribute(
					"tol:source-collection", NS_TOL, srcInfo
							.getSourceCollection()));
			mediaSource.addAttribute(new Attribute(
					"tol:source-collection-url", NS_TOL, srcInfo
							.getSourceCollectionUrl()));
		}
		//dataObject.appendChild(mediaSource);
	}

	private void addMediaSourceElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element mediaDcSource = new Element("dc:source",
				NS_DUBLIN_CORE);
		mediaDcSource.appendChild(new Text(mediaRep.getSource()));
		dataObject.appendChild(mediaDcSource);
	}

	private void addMediaRightsHolderElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element rightsHolderEl = new Element(
				"dcterms:rightsHolder", NS_DUBLIN_CORE_TERMS);
		//			rightsHolderEl.addAttribute(new Attribute("tol:homepage", NS_TOL, mediaRep.getCopyrightHolder().getHomepage()));
		//			rightsHolderEl.addAttribute(new Attribute("tol:email", NS_TOL, mediaRep.getCopyrightHolder().getHomepage()));
		rightsHolderEl.appendChild(new Text(mediaRep
				.getCopyrightHolder().getName()));
		dataObject.appendChild(rightsHolderEl);
	}

	private void addMediaLicenseElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element licenseEl = new Element("license", NS);
		licenseEl.appendChild(new Text(mediaRep.getLicenseLink()));
		dataObject.appendChild(licenseEl);
	}

	private void addMediaLanguageElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element langEl = new Element("dc:language", NS_DUBLIN_CORE);
		langEl.appendChild(new Text(mediaRep.getLanguage()));
		dataObject.appendChild(langEl);
	}

	private void addMediaTitleElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element titleEl = new Element("dc:title", NS_DUBLIN_CORE);
		if (StringUtils.notEmpty(mediaRep.getLanguage())) {
			titleEl.addAttribute(new Attribute("xml:lang", NS_XML,
					mediaRep.getLanguage()));
		}
		titleEl.appendChild(new Text(mediaRep.getTitle()));
		dataObject.appendChild(titleEl);
	}

	private void addMediaCreationElements(
			MediaContentRepresentation mediaRep, Element dataObject) {
		addEmbeddedMediaCreatedElement(mediaRep, dataObject);
		addEmbeddedMediaModifiedElement(mediaRep, dataObject);
	}

	private void addEmbeddedMediaModifiedElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		if (mediaRep.getModifiedDate() != null) {
			Element dataObjModified = new Element(
					"dcterms:modified", NS_DUBLIN_CORE_TERMS);
			dataObjModified.appendChild(new Text(dcTermsFormat
					.format(mediaRep.getModifiedDate())));
			dataObject.appendChild(dataObjModified);
		}
	}

	private void addEmbeddedMediaCreatedElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		if (mediaRep.getCreatedDate() != null) {
			Element dataObjCreated = new Element("dcterms:created",
					NS_DUBLIN_CORE_TERMS);
			dataObjCreated.appendChild(new Text(dcTermsFormat
					.format(mediaRep.getCreatedDate())));
			dataObject.appendChild(dataObjCreated);
		}
	}

	private void addMediaAgentElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element agentEl = new Element("agent", NS);
		
		agentEl.addAttribute(new Attribute("role", mediaRep
				.getAgentRole()));
		
		if (StringUtils.notEmpty(mediaRep.getAgentHomepage())) {
			agentEl.addAttribute(new Attribute("homepage", mediaRep
					.getAgentHomepage()));
		} else if (StringUtils.notEmpty(mediaRep.getCopyrightHolder().getContributorProfile())){
			agentEl.addAttribute(new Attribute("homepage", 
					mediaRep.getCopyrightHolder().getContributorProfile()));
		} // if we don't have anything to give, then don't add the attribute
		
		agentEl.appendChild(new Text(mediaRep.getAgentName()));
		dataObject.appendChild(agentEl);
	}

	private void addMediaMimeTypeElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element mimeTypeEl = new Element("mimeType", NS);
		mimeTypeEl.appendChild(new Text(mediaRep.getMimeType()));
		dataObject.appendChild(mimeTypeEl);
	}

	private void addMediaDataTypeElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element dataTypeEl = new Element("dataType", NS);
		dataTypeEl.appendChild(new Text(mediaRep.getDataType()));
		dataObject.appendChild(dataTypeEl);
	}

	private void addMediaIdentifierElement(
			MediaContentRepresentation mediaRep, Element dataObject) {
		Element mediaDcIdent = new Element("dc:identifier",
				NS_DUBLIN_CORE);
		mediaDcIdent
				.appendChild(new Text(mediaRep.getIdentifier()));
		dataObject.appendChild(mediaDcIdent);
	}

	@SuppressWarnings("unchecked")
	private void addTextSectionDataObjects(MappedPage mpage, DaoBundle daos,
			Element taxon, HashMap<Integer, String> embeddedMedia) {
		SortedSet textSections = mpage.getTextSections();
		for (Iterator<MappedTextSection> itr = textSections.iterator(); itr.hasNext(); ) {
			MappedTextSection mtxt = itr.next();
			
			addTextSectionDataObject(mpage, daos, taxon, embeddedMedia, mtxt);
		}
	}

	private void addTextSectionDataObject(MappedPage mpage, DaoBundle daos,
			Element taxon, HashMap<Integer, String> embeddedMedia,
			MappedTextSection mtxt) {
		TextContentRepresentation textRep = new TextContentRepresentation(mpage, mtxt);
		
		if (StringUtils.isEmpty(mtxt.getText())) {
			return; // skip added this data object is there is no section text
		}
		
		Element dataObject = new Element("dataObject", NS);

		addTextSectionIndentifierElement(textRep, dataObject);
		addTextSectionDataTypeElement(textRep, dataObject);
		addTextSectionMimeTypeElement(textRep, dataObject);
		addTextSectionAuthorElements(textRep, dataObject);
		addTextSectionCreatedElements(textRep, dataObject);
		addTextSectionTitleElement(textRep, dataObject);
		addTextSectionLanguageElement(textRep, dataObject);
		addTextSectionLicenseElement(textRep, dataObject);
		addTextSectionRightsHolderElements(textRep, dataObject);
		addTextSectionBiblioCitation(mpage, dataObject);
		addTextSectionAudienceElements(textRep, dataObject);
		addTextSectionSourceElement(mpage, dataObject); 
		addTextSectionSubjectElement(textRep, dataObject);
		addTextSectionTolSourceData();
		addTextSectionDescription(mpage, daos, embeddedMedia, mtxt, dataObject);		

		//addTextSectionReferencesElements(textRep, dataObject);		
		
		taxon.appendChild(dataObject);
	}

	private void addTextSectionSubjectElement(
			TextContentRepresentation textRep, Element dataObject) {
		Element subject = new Element("subject", NS);
		// he need to use the textRep to determine this, but this will do for now
		subject.appendChild(new Text(textRep.getSubject()));
		dataObject.appendChild(subject);
	}

	private void addTextSectionBiblioCitation(MappedPage mpage,
			Element dataObject) {
		if (getCitationCreator() != null) {
			Element biblioCiteEl = new Element("dcterm:bibliographicCitation", NS_DUBLIN_CORE_TERMS);
			String citation = getCitationCreator().createCitation(mpage);
			citation = getCitationCreator().cleanCitation(citation);
			citation = escapeText(citation);
			if (StringUtils.notEmpty(citation)) {
				biblioCiteEl.appendChild(new Text(citation));
				dataObject.appendChild(biblioCiteEl);
			}
		}
	}

	private void addTextSectionDescription(MappedPage mpage, DaoBundle daos,
			HashMap<Integer, String> embeddedMedia, MappedTextSection mtxt,
			Element dataObject) {
		EmbeddedMediaHandler mediaHandler = new EmbeddedMediaHandler(mtxt.getText(), 
				getPageNameUrl(mpage.getMappedNode()), daos.getImageDAO());
		Element dcDescEl = new Element("dc:description", NS_DUBLIN_CORE);
		dcDescEl.addAttribute(new Attribute("xml:lang", NS_XML, "en"));
		appendDescriptionText(mpage, mtxt, mediaHandler, dcDescEl);
		dataObject.appendChild(dcDescEl);

		// add/put any embedded media in the map for all  
		// text sections so that it can be processed later. 
		embeddedMedia.putAll(mediaHandler.getEmbeddedMedia());
	}

	private void appendDescriptionText(MappedPage mpage,
			MappedTextSection mtxt, EmbeddedMediaHandler mediaHandler,
			Element dcDescEl) {
		if (TextContentRepresentation.PHYLO_DISCUSSION.equals(mtxt.getHeading())) {
			String modifiedText = mediaHandler.getText();
			if (mpage.getWriteAsList()) {
				modifiedText = prependTaxonListToDescription(mpage,
						modifiedText);
			} else {
				// prepend TextLinkMaker, need URL, NodeId, LinkText, in <p> tags
				modifiedText = prependTextLinkToDescription(mpage, modifiedText);
			}
			dcDescEl.appendChild(new Text(escapeText(modifiedText)));			
		} else {
			dcDescEl.appendChild(new Text(escapeText(mediaHandler.getText())));
		}
	}

	@SuppressWarnings("unchecked")
	private String prependTaxonListToDescription(MappedPage mpage,
			String modifiedText) {
		List nodes = getPageDao().getOrderedByParentNodesOnPage(mpage, true);
		String postText = processPostText(mpage);
		// prepend HTML taxon list, basal node, and nodes on page, in <p> tags				
		HtmlTaxonListMaker listMaker = new HtmlTaxonListMaker(
				mpage.getMappedNode(), (List<MappedNode>) nodes);
		String textToInject = listMaker.createList() + postText; 
		modifiedText = textToInject + modifiedText;
		return modifiedText;
	}

	private String prependTextLinkToDescription(MappedPage mpage,
			String modifiedText) {
		TextLinkMaker linkMaker = new TextLinkMaker(getPageNameUrl(mpage.getMappedNode()), 
				"" + mpage.getMappedNode().getNodeId(), 
				"View " + mpage.getMappedNode().getName() + " Tree");
		String postText = processPostText(mpage);
		String textToInject = "<p>" + linkMaker.makeLink() + "</p>" + postText;
		modifiedText = textToInject + modifiedText;
		return modifiedText;
	}

	private String processPostText(MappedPage mpage) {
		String postTreeText = mpage.getPostTreeText();
		if (StringUtils.isEmpty(postTreeText) || postTreeText.equals("null")) {
			return "";
		}
		EmbeddedMediaHandler postTextHandler = new EmbeddedMediaHandler(postTreeText, getPageNameUrl(mpage.getMappedNode()), getImageDao());
		return postTextHandler.getText();
	}
	
	private void addTextSectionTolSourceData() {
		Element tolSourceInfo = new Element("tol:source-info", NS_TOL);
		// TODO for now, all text sections are hard coded to being native
		tolSourceInfo.addAttribute(new Attribute("tol:tol-native", NS_TOL, "true"));
		//dataObject.appendChild(tolSourceInfo);
	}

	private void addTextSectionSourceElement(MappedPage mpage,
			Element dataObject) {
		dataObject.appendChild(createTaxonSourceElement(mpage.getMappedNode()));
	}

	private void addTextSectionAudienceElements(
			TextContentRepresentation textRep, Element dataObject) {
		for (String aud : textRep.getAudience()) {
			Element audienceEl = new Element("audience", NS);
			audienceEl.appendChild(new Text(aud));
			dataObject.appendChild(audienceEl);
		}
	}

	private void addTextSectionRightsHolderElements(
			TextContentRepresentation textRep, Element dataObject) {
		Set<String> keys;
		keys = textRep.getCopyrightHolders().keySet();
		for (Iterator<String> keyItr = keys.iterator(); keyItr.hasNext(); ) {
			String key = keyItr.next();
			RightsHolder tuple = textRep.getCopyrightHolders().get(key);
			if (tuple != null) {
				Element rightHolderEl = new Element("dcterms:rightsHolder", NS_DUBLIN_CORE_TERMS);
				rightHolderEl.appendChild(new Text(tuple.toString()));
				dataObject.appendChild(rightHolderEl);
			}
		}
	}

	private void addTextSectionLicenseElement(
			TextContentRepresentation textRep, Element dataObject) {
		Element licenseEl = new Element("license", NS);
		licenseEl.appendChild(new Text(textRep.getLicenseLink()));
		dataObject.appendChild(licenseEl);
	}

	private void addTextSectionLanguageElement(
			TextContentRepresentation textRep, Element dataObject) {
		Element langEl = new Element("dc:language", NS_DUBLIN_CORE);
		langEl.appendChild(new Text(textRep.getLanguage()));
		dataObject.appendChild(langEl);
	}

	private void addTextSectionTitleElement(TextContentRepresentation textRep,
			Element dataObject) {
		Element titleEl = new Element("dc:title", NS_DUBLIN_CORE);
		titleEl.addAttribute(new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", textRep.getLanguage()));
		titleEl.appendChild(new Text(textRep.getTitle()));
		dataObject.appendChild(titleEl);
	}

	private void addTextSectionCreatedElements(
			TextContentRepresentation textRep, Element dataObject) {
		if (textRep.getCreatedDate() != null) {
			Element dataObjCreated = new Element("dcterms:created",
					NS_DUBLIN_CORE_TERMS);
			dataObjCreated.appendChild(new Text(dcTermsFormat
					.format(textRep.getCreatedDate())));
			dataObject.appendChild(dataObjCreated);
		}
		
		if (textRep.getModifiedDate() != null) {
			Element dataObjModified = new Element("dcterms:modified",
					NS_DUBLIN_CORE_TERMS);
			dataObjModified.appendChild(new Text(dcTermsFormat
					.format(textRep.getModifiedDate())));
			dataObject.appendChild(dataObjModified);
		}
	}

	private void addTextSectionAuthorElements(
			TextContentRepresentation textRep, Element dataObject) {
		List<String> authorNames = textRep.getAuthorNames();
		for (Iterator<String> nameItr = authorNames.iterator(); nameItr.hasNext(); ) {
			String key = nameItr.next();
			
			Contributor contr = textRep.getAuthors().get(key);
			String homepage = contr.getHomepage();
			
			Element agentEl = new Element("agent", NS);
			agentEl.addAttribute(new Attribute("role", AgentRole.Author.toString().toLowerCase()));
			
			if (StringUtils.notEmpty(homepage)) {
				agentEl.addAttribute(new Attribute("homepage", homepage));
			} else {
				agentEl.addAttribute(new Attribute("homepage", contr.getProfileUrl()));
			}
			
			agentEl.appendChild(new Text(key));
			dataObject.appendChild(agentEl);
		}
	}

	private void addTextSectionMimeTypeElement(
			TextContentRepresentation textRep, Element dataObject) {
		Element mimeTypeEl = new Element("mimeType", NS);
		mimeTypeEl.appendChild(new Text(textRep.getMimeType()));
		dataObject.appendChild(mimeTypeEl);
	}

	private void addTextSectionDataTypeElement(
			TextContentRepresentation textRep, Element dataObject) {
		Element dataTypeEl = new Element("dataType", NS);
		dataTypeEl.appendChild(new Text(textRep.getDataType()));
		dataObject.appendChild(dataTypeEl);
	}

	private void addTextSectionIndentifierElement(
			TextContentRepresentation textRep, Element dataObject) {
		Element mtxtDcIdent = new Element("dc:identifier", NS_DUBLIN_CORE);
		mtxtDcIdent.appendChild(new Text(textRep.getIdentifier()));
		dataObject.appendChild(mtxtDcIdent);
	}

	@SuppressWarnings("unchecked")
	private void addTolOtherNamesDataElements(MappedNode mnode) {
		if (mnode.getSynonyms() != null && !mnode.getSynonyms().isEmpty()) {
			Element onamesElement = new Element("tol:othernames", NS_TOL);
			for (Iterator<MappedOtherName> itr = mnode.getSynonyms().iterator(); itr.hasNext(); ) {
				ScientificNamer otherNamer = new ScientificNamer(itr.next());
				Element onameElement = new Element("tol:othername", NS_TOL);
				onameElement.appendChild(new Text(otherNamer.getName()));
				onamesElement.appendChild(onameElement); 
			}
			//taxon.appendChild(onamesElement);
		}
	}

	private void addTolNodeDataElement(MappedPage mpage, MappedNode mnode) {
		Element nodeData = new Element("tol:node-data", NS_TOL);
		nodeData.addAttribute(new Attribute("extinct", Boolean.valueOf(
				mnode.getExtinct() == MappedNode.EXTINCT).toString()));
		PositionConfidence confidence = PositionConfidence.fromInt(mnode.getConfidence());
		nodeData.addAttribute(new Attribute("confidence", confidence.toString()));
		Phylesis phylesis = Phylesis.fromInt(mnode.getPhylesis());
		nodeData.addAttribute(new Attribute("phylesis", phylesis.toString()));
		nodeData.addAttribute(new Attribute("leaf", Boolean.valueOf(mnode.getIsLeaf()).toString()));
		
		if (StringUtils.notEmpty(mnode.getDescription())) {
			Element grpDesc = new Element("tol:group-description", NS_TOL);
			grpDesc.appendChild(new Text(mnode.getDescription()));
			nodeData.appendChild(grpDesc);
		}
		
		if (StringUtils.notEmpty(mpage.getLeadText())) {
			Element grpCmnt = new Element("tol:group-comment", NS_TOL);
			grpCmnt.appendChild(new Text(mpage.getLeadText()));
			nodeData.appendChild(grpCmnt);
		}
		
		//taxon.appendChild(nodeData);
	}

	private void addTaxonCreationElements(MappedPage mpage, Element taxon) {
		addTaxonCreatedElement(mpage, taxon);
		addTaxonModifiedElement(mpage, taxon);
	}

	private void addTaxonModifiedElement(MappedPage mpage, Element taxon) {
		Element modified = new Element("dcterms:modified", NS_DUBLIN_CORE_TERMS);
		if (mpage.getContentChangedDate() != null) {
			modified.appendChild(new Text(dcTermsFormat.format(mpage
					.getContentChangedDate())));
			taxon.appendChild(modified);
		}
	}

	private void addTaxonCreatedElement(MappedPage mpage, Element taxon) {
		Element created = new Element("dcterms:created", NS_DUBLIN_CORE_TERMS);
		if (mpage.getFirstOnlineDate() != null) {
			created.appendChild(new Text(dcTermsFormat.format(mpage
					.getFirstOnlineDate())));
			taxon.appendChild(created);
		}
	}

	private void addTaxonScientificNameElement(MappedNode mnode, Element taxon) {
		ScientificNamer namer = new ScientificNamer(mnode);
		Element dwcSciName = new Element("dwc:ScientificName", NS_DARWIN_CORE);
		String escapedName = escapeText(namer.getName());
		dwcSciName.appendChild(new Text(escapedName));

		taxon.appendChild(dwcSciName);
	}

	private void addTaxonSourceElement(MappedNode mnode, Element taxon) {
		taxon.appendChild(createTaxonSourceElement(mnode));
	}

	private Element createTaxonSourceElement(MappedNode mnode) {
		Element dcSource = new Element("dc:source", NS_DUBLIN_CORE);
		dcSource.appendChild(new Text(getPageNameUrl(mnode)));
		return dcSource;
	}
	
	private void addTaxonIdentifierElement(MappedNode mnode, Element taxon) {
		String taxonIdent = "tol-node-id-" + mnode.getId();
		Element dcIdent = new Element("dc:identifier", NS_DUBLIN_CORE);
		dcIdent.appendChild(new Text(taxonIdent));

		taxon.appendChild(dcIdent);
	}

	private Element initializeDocumentRoot() {
		Element root = new Element(PageContentElements.ROOT_RESPONSE, NS);

		setupNamespaceDeclarations(root);
		setPreparedDocument(new Document(root));
		return root;
	}

	private void setupNamespaceDeclarations(Element root) {
		// the following causes an exception that is rather cryptic about having
		// to define a namespace before using it - it's because I guess you're
		// attempting to define the element without a namespace, yet you're using a
		// namespace prefix in association. Exception: nu.xom.NamespaceConflictException
		// root.addNamespaceDeclaration("xsi",
		// "http://www.w3.org/2001/XMLSchema-instance");
		// root.addAttribute(new Attribute("xsi:schemaLocation",
		// XSI_SCHEMA_LOC));

		root.addAttribute(new Attribute("xsi:schemaLocation", NS_XSI,
				EOL_XSI_SCHEMA_LOC));
		root.addNamespaceDeclaration("dc", NS_DUBLIN_CORE);
		root.addNamespaceDeclaration("dcterms", NS_DUBLIN_CORE_TERMS);
		root.addNamespaceDeclaration("dwc", NS_DARWIN_CORE);
		root.addNamespaceDeclaration("xml", NS_XML);
		root.addNamespaceDeclaration("tol", NS_TOL);
	}

	private String getPageNameUrl(MappedNode mnode) {
		String pageNodeNameUrl = mnode.getName();
		try {
			pageNodeNameUrl = URLEncoder.encode(pageNodeNameUrl, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		pageNodeNameUrl = (StringUtils.notEmpty(pageNodeNameUrl) ? pageNodeNameUrl
				+ "/"
				: "")
				+ mnode.getId();
		return "http://tolweb.org/" + pageNodeNameUrl;
	}

	public CitationCreator getCitationCreator() {
		return citationCreator;
	}

	public void setCitationCreator(CitationCreator citationCreator) {
		this.citationCreator = citationCreator;
	}

}
