package org.tolweb.content.preparers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.tolweb.content.helpers.DaoBundle;
import org.tolweb.content.helpers.MediaContentAttributes;
import org.tolweb.content.helpers.MediaContentElements;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.Movie;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.ImageVersion;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;

public class MediaCoPreparer extends AbstractCoPreparer {
	boolean isImage = false; 
	boolean isMovie = false; 
	boolean isSound = false; 
	boolean isDocument = false;
	
	public void setContentSource(Object payload, DaoBundle daos, Element doc) {
		if (Movie.class.isInstance(payload)) {
			setMediaFile((Movie)payload);
			isMovie = true;
		} else if (org.tolweb.hibernate.Document.class.isInstance(payload)) {
			setMediaFile((org.tolweb.hibernate.Document)payload);
			isDocument = true;
		} else if (org.tolweb.hibernate.Sound.class.isInstance(payload)) {
			setMediaFile((org.tolweb.hibernate.Sound)payload);
			isSound = true;
		} else if (NodeImage.class.isInstance(payload)) {
			setMediaFile((NodeImage)payload);
			isImage = true;
		} else {
			throw new IllegalArgumentException("Unknown Media File - content passed to preparer is not an image, movie, document, or sound");
		}
		setDaoBundle(daos);
		setParentElement(doc);
		setPreparedElement(doc);
		setPreparerNamespace(ContentPreparer.NS);		
	}

	private Element createElement(String elementName) {
		return new Element(elementName, getPreparerNamespace());
	}
	
	public void processContent() {
		Element mediaFile = createElement(MediaContentElements.MEDIA_FILE);
		
		getPreparedElement().appendChild(mediaFile);
		
		
		mediaFile.addAttribute(new Attribute(MediaContentAttributes.ID, ""+getMediaFile().getId()));
		
		ImageVersion masterImgVer = (ImageVersion)getDaoBundle().getImageDAO().getMasterVersion(getMediaFile().getId());
		String thumbnailUrl = getDaoBundle().getImageDAO().getThumbnailUrlForImageWithId(getMediaFile().getId());
		String imageFileName = masterImgVer != null ? masterImgVer.getFileName() : "";
		
		if (getMediaFile().getMediaType() == NodeImage.IMAGE) {
			mediaFile.addAttribute(new Attribute(MediaContentAttributes.URL, "http://tolweb.org/tree/ToLimages/" + imageFileName));
		} else {
			mediaFile.addAttribute(new Attribute(MediaContentAttributes.URL, ""));
		}
		
		
		mediaFile.addAttribute(new Attribute(MediaContentAttributes.THUMBNAIL, "http://tolweb.org" + thumbnailUrl));
		
		if (isSound) {
			mediaFile.addAttribute(new Attribute(MediaContentAttributes.LINK_IMAGE, "http://tolweb.org/tree/ToLimages/soundmini.gif"));
		} else if (isDocument) {
			mediaFile.addAttribute(new Attribute(MediaContentAttributes.LINK_IMAGE, "http://tolweb.org/tree/ToLimages/docmini.gif"));
		} else {
			mediaFile.addAttribute(new Attribute(MediaContentAttributes.LINK_IMAGE, "http://tolweb.org" +  thumbnailUrl));
		}
		
		mediaFile.addAttribute(new Attribute(MediaContentAttributes.TYPE, safeToString(getMediaFile().getMediaTypeDescription())));
		mediaFile.addAttribute(new Attribute(MediaContentAttributes.SUBTYPE, safeToString(getMediaFile().getImageType())));
		mediaFile.addAttribute(new Attribute(MediaContentAttributes.CONTENT_TYPE, buildContentTypeString()));
		mediaFile.addAttribute(new Attribute(MediaContentAttributes.DATE_CREATED,  safeToString(getMediaFile().getCreationDate())));
		mediaFile.addAttribute(new Attribute(MediaContentAttributes.DATE_CHANGED, safeToString(getMediaFile().getLastEditedDate())));
		
		Element mediaDesc = createElement(MediaContentElements.DESCRIPTION);
		mediaDesc.appendChild(new Text(determineDescription()));
		mediaFile.appendChild(mediaDesc);
		
		Element altText = createElement(MediaContentElements.ALT_TEXT);
		altText.appendChild(new Text(getMediaFile().getScientificName()));
		mediaFile.appendChild(altText);
		
		Element comments = createElement(MediaContentElements.COMMENTS);
		comments.appendChild(new Text(getMediaFile().getComments()));
		mediaFile.appendChild(comments);
		
		Element groups = createElement(MediaContentElements.GROUPS);
		groups.addAttribute(new Attribute(MediaContentAttributes.NODE, getNodeIdsString(getMediaFile())));
		// TODO if scientific name is empty/null create a string w/ node-names for all attached nodes
		groups.addAttribute(new Attribute(MediaContentAttributes.SCIENTIFIC_NAME, getSafeString(getMediaFile().getScientificName())));
		mediaFile.appendChild(groups);
		
		Element specimenInfo = createElement(MediaContentElements.SPECIMEN_INFO);
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.LOCATION, 
				getSafeString(getMediaFile().getGeoLocation())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.DATE, 
				getSafeString(getMediaFile().getUserCreationDate())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.CONDITION, 
				getSafeString(getMediaFile().getAlive())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.PERIOD, 
				getSafeString(getMediaFile().getPeriod())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.SEX, 
				getSafeString(getMediaFile().getSex())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.LIFE_STAGE, 
				getSafeString(getMediaFile().getStage())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.BODY_PART, 
				getSafeString(getMediaFile().getBodyPart())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.SIZE, 
				getSafeString(getMediaFile().getSize())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.VIEW, 
				getSafeString(getMediaFile().getView())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.COLLECTOR, 
				getSafeString(getMediaFile().getCollector())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.COLLECTION, 
				getSafeString(getMediaFile().getCollection())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.COLLECTION_ACRONYM, 
				getSafeString(getMediaFile().getCollectionAcronym())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.TYPE, 
				getSafeString(getMediaFile().getType())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.VOUCHER_NUMBER, 
				getSafeString(getMediaFile().getVoucherNumber())));
		specimenInfo.addAttribute(new Attribute(MediaContentAttributes.VOUCHER_NUMBER_COLLECTION, 
				getSafeString(getMediaFile().getVoucherNumberCollection())));
		mediaFile.appendChild(specimenInfo);
		
		Element behavior = createElement(MediaContentElements.BEHAVIOR);
		behavior.appendChild(new Text(getMediaFile().getBehavior()));
		specimenInfo.appendChild(behavior);

		Element techinfo = createElement(MediaContentElements.TECH_INFO);
		if (masterImgVer != null) {
			techinfo.addAttribute(new Attribute(MediaContentAttributes.FILE_SIZE, safeToString(masterImgVer.getFileSize())));
			techinfo.addAttribute(new Attribute(MediaContentAttributes.WIDTH, safeToString(masterImgVer.getWidth())));
			techinfo.addAttribute(new Attribute(MediaContentAttributes.HEIGHT, safeToString(masterImgVer.getHeight())));
		}
		if (isMovie || isSound) {
			String runningTime = "";
			if (isMovie) runningTime = ((Movie)getMediaFile()).getRunningTime();
			if (isSound) runningTime = ((org.tolweb.hibernate.Sound)getMediaFile()).getRunningTime();
			if (StringUtils.notEmpty(runningTime)) {
				techinfo.addAttribute(new Attribute(MediaContentAttributes.RUNNING_TIME, runningTime));
			}
		}
		techinfo.appendChild(new Text(getMediaFile().getTechnicalInformation()));
		mediaFile.appendChild(techinfo);
		
		Element copyright = createElement(MediaContentElements.COPYRIGHT);
		copyright.addAttribute(new Attribute(MediaContentAttributes.COPYRIGHT_DATE, 
				getSafeString(getMediaFile().getCopyrightDate())));
		copyright.addAttribute(new Attribute(MediaContentAttributes.LICENSE, 
				getLicenseShortName(getMediaFile().getUsePermission())));
		copyright.addAttribute(new Attribute(MediaContentAttributes.ID, 
				""+getMediaFile().getCopyrightContributorId()));
		
		if (getMediaFile().getCopyrightContributorId() == 0) {
			copyright.addAttribute(new Attribute(MediaContentAttributes.COPYRIGHT_OWNER, 
					getMediaFile().getCopyrightOwner()));
			copyright.addAttribute(new Attribute(MediaContentAttributes.URL, 
					getMediaFile().getCopyrightUrl())); 
			copyright.addAttribute(new Attribute(MediaContentAttributes.EMAIL, 
					getMediaFile().getCopyrightEmail())); 
		}
		
		mediaFile.appendChild(copyright);
		
		Element credits = createElement(MediaContentElements.CREDITS);

		String creditsCreator = getMediaFile().getCreator();
		if (StringUtils.notEmpty(creditsCreator)) {
			Element creator = createElement(MediaContentElements.CREATOR);
			creator.appendChild(new Text(creditsCreator));
			credits.appendChild(creator);
		}
		
		String creditsIdentifier = getMediaFile().getIdentifier();
		if (StringUtils.notEmpty(creditsIdentifier)) {
			Element identifier = createElement(MediaContentElements.IDENTIFIER);
			identifier.appendChild(new Text(creditsIdentifier));
			credits.appendChild(identifier);
		}

		String creditsAcknowledgements = getMediaFile().getAcknowledgements();
		if (StringUtils.notEmpty(creditsAcknowledgements)) {
			Element acknowledgements = createElement(MediaContentElements.ACKNOWLEDGEMENTS);
			acknowledgements.appendChild(new Text(creditsAcknowledgements));
			credits.appendChild(acknowledgements);
		}

		String creditsReference = getMediaFile().getReference();
		if (StringUtils.notEmpty(creditsReference)) {
			Element reference = createElement(MediaContentElements.REFERENCE);
			reference.appendChild(new Text(creditsReference));
			credits.appendChild(reference);
		}
		
		Element mediaSource = createElement(MediaContentElements.MEDIA_SOURCE);
		mediaSource.addAttribute(new Attribute(MediaContentAttributes.SOURCE_COLLECTION, 
				getMediaFile().getSourceDbId() == null ? "0" : safeToString(getMediaFile().getSourceDbId())));
		mediaSource.addAttribute(new Attribute(MediaContentAttributes.SOURCE_TITLE, 
				getSafeString(getMediaFile().getSourceCollectionTitle())));
		mediaSource.addAttribute(new Attribute(MediaContentAttributes.SOURCE_URL, 
				getSafeString(getMediaFile().getSourceCollectionUrl())));
		mediaSource.addAttribute(new Attribute(MediaContentAttributes.MORE_SOURCE, 
				"[future-use]"));
		credits.appendChild(mediaSource);
		
		mediaFile.appendChild(credits);
	}

	private String getLicenseShortName(byte licenseCode) {
		ContributorLicenseInfo cLicInfo = new ContributorLicenseInfo(licenseCode);
		return cLicInfo.toShortString();
	}
	
	private String determineDescription() {
		if (isImage) {
			return getMediaFile().getAltText();
		} else if (isSound) {
			return ((org.tolweb.hibernate.Sound)getMediaFile()).getDescription();
		} else if (isMovie) {
			return ((Movie)getMediaFile()).getDescription();
		} else if (isDocument) {
			return ((org.tolweb.hibernate.Document)getMediaFile()).getDescription();
		} else {
			return "...";
		}
	}

	@SuppressWarnings("unchecked")
	private String getNodeIdsString(NodeImage mediaFile) {
		ArrayList ids = new ArrayList();
		
		Set s = mediaFile.getNodesSet();
		for (Iterator itr = s.iterator(); itr.hasNext(); ) {
			MappedNode mnode = (MappedNode)itr.next();
			ids.add(mnode.getId());
		}
		return StringUtils.returnCommaJoinedString(ids);
	}
	
	private String buildContentTypeString() {
		StringBuilder contentType = new StringBuilder();
		
		if (getMediaFile() != null) {
			if (getMediaFile().getIsSpecimen()) {
				contentType.append("Specimens, ");
			}
			if (getMediaFile().getIsBodyParts()) {
				contentType.append("Body Parts, ");
			}
			if (getMediaFile().getIsUltrastructure()) {
				contentType.append("Ultrastructure, ");
			}
			if (getMediaFile().getIsHabitat()) {
				contentType.append("Habitat, ");
			}
			if (getMediaFile().getIsEquipment()) {
				contentType.append("Apparatus, ");
			}
			if (getMediaFile().getIsPeopleWorking()) {
				contentType.append("People, ");
			}
			// must determine if the media file is a specialized type of Sound to precede
			if (isSound) {
				if (((org.tolweb.hibernate.Sound)getMediaFile()).getIsOrganism()) {
					contentType.append("Organism Sound, ");
				}
				if (((org.tolweb.hibernate.Sound)getMediaFile()).getIsEnvironmental()) {
					contentType.append("Environmental Sound, ");
				}				
				if (((org.tolweb.hibernate.Sound)getMediaFile()).getIsNarrative()) {
					contentType.append("Narrative, ");
				}
			}
		}
		// trim the lazy appending comma-space from the string before returning
		if (contentType.toString().endsWith(", ")) {
			int lastIndex = contentType.lastIndexOf(", ");
			contentType.delete(lastIndex, lastIndex+2);
		}
		
		return contentType.toString();
	}
}
