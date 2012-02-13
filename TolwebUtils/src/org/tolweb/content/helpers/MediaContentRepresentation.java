package org.tolweb.content.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.tolweb.hibernate.MappedNode;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.DescriptiveMedia;
import org.tolweb.treegrow.main.Languages;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * An abstracted representation of the media content for a Media File
 *
 * This representation is used when generating the XML output for the 
 * content services (exposed via the web, so "content web services" or 
 * "web services" are other ways of referring to this usage).  The 
 * information is related to the defined EOL transfer schema.  The 
 * specifics of that transfer schema will be isolated in the calling 
 * object such that other usages for this representation may be used. 
 * However, the original drive for what is contained herein came from 
 * the implementation of the EOL transfer schema. 
 * 
 * @author lenards
 *
 */
public class MediaContentRepresentation {
	public static final String IDENTIFIER_PREFIX = "tol-media-id-";
	public static final String MEDIA_LINK_PREFIX = "http://tolweb.org/media/";
	public static final String HOST_DOMAIN_NAME = "http://tolweb.org";
	public static final String HOST_MEDIA_URL = "http://tolweb.org/tree";
	
	private String identifier;
	private String dataType;
	private String mimeType;
	private Date createdDate;
	private Date modifiedDate;
	private String title; 
	private Agent agent;
	private RightsHolder copyrightHolder; 
	private String language; 
	private ContributorLicenseInfo licenseInfo;
	private String licenseLink;
	private List<String> references;
	private String geoLocation;
	private String source; 
	private String mediaURL;
	private String description; 
	private boolean excludedMediaType;
	
	/**
	 * Constructs a representation of a media content object given the media file 
	 * and the copyright owning contributor. 
	 * @param mediaFile the media file to pull content metadata from
	 */
	public MediaContentRepresentation(NodeImage mediaFile, String mediaUrl) {
		identifier = IDENTIFIER_PREFIX + mediaFile.getId();
		DataTypeDeterminer dtd = new DataTypeDeterminer(mediaFile);
		dataType = dtd.getDataType();
		mimeType = dtd.getMimeType();
		setExcludedMediaType(StringUtils.isEmpty(dataType) || dtd.isUnknown());
		createdDate = mediaFile.getCreationDate();
		modifiedDate = mediaFile.getLastEditedDate();
		title = StringUtils.notEmpty(mediaFile.getTitle()) ? 
				mediaFile.getTitle() : mediaFile.getLocation();
		licenseInfo = new ContributorLicenseInfo(mediaFile.getUsePermission());
		determineLicenseLink();
		geoLocation = mediaFile.getGeoLocation();
		agent = new Agent(mediaFile);
		copyrightHolder = RightsHolderFactory.createRightsHolderFor(mediaFile);
		resolveMediaUrl(mediaUrl); 
		determineSource(mediaFile);
		determineLanguage(mediaFile);
		determineReferences(mediaFile);
		determineDescription(mediaFile);
	}

	private void resolveMediaUrl(String mediaUrl) {
		if (mediaUrl.trim().startsWith("tree") || mediaUrl.trim().startsWith("/tree")) {
			if (!mediaUrl.trim().startsWith("/")) {
				mediaUrl = "/" + mediaUrl;
			}
			mediaURL = HOST_DOMAIN_NAME + mediaUrl;
		} else {
			mediaURL = HOST_MEDIA_URL + mediaUrl;
		}
		
	}

	private void determineLicenseLink() {
		if (isNonCreativeCommons()) {
			if (isPublicDomain()) {
				licenseLink = ContributorLicenseInfo.CC_PUB_DOMAIN;
				if (!licenseLink.endsWith("/")) {
					licenseLink += "/";
				}
			} else {
				licenseLink = HOST_DOMAIN_NAME + ContributorLicenseInfo.linkString(licenseInfo);
			}
		} else {
			licenseLink = ContributorLicenseInfo.linkString(licenseInfo);
		}
	}

	private boolean isNonCreativeCommons() {
		return isToLRelated() || isPublicDomain();
	}
	
	private void determineDescription(NodeImage mediaFile) {
		StringBuilder desc = new StringBuilder();
		if (StringUtils.notEmpty(mediaFile.getScientificName()) ||
			(mediaFile.getNodesSet() != null && !mediaFile.getNodesSet().isEmpty())) {
			desc.append("Group: ");
			desc.append(StringUtils.notEmpty(mediaFile.getScientificName()) ? mediaFile.getScientificName() : "");
			String groupAttachments = getGroupAttachmentsString(mediaFile.getNodesSet(), 
					StringUtils.notEmpty(mediaFile.getScientificName()));
			desc.append(groupAttachments);
			desc.append("; ");
		}
		
		if (StringUtils.notEmpty(mediaFile.getIdentifier())) {
			desc.append("Identified by: " + mediaFile.getIdentifier() + "; ");
		}
		if (!NodeImage.class.equals(mediaFile.getClass())) {
			DescriptiveMedia descMedia = (DescriptiveMedia)mediaFile;
			if (StringUtils.notEmpty(descMedia.getDescription())) {
				desc.append("Description: " + descMedia.getDescription() + "; ");
			}
		}
		if (StringUtils.notEmpty(mediaFile.getComments())) {
			desc.append("Comments: " + mediaFile.getComments() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getBehavior())) {
			desc.append("Behavior: " + mediaFile.getBehavior() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getSex()) && 
				!"unknown".equals(mediaFile.getSex().toLowerCase())) {
			desc.append("Sex: " + mediaFile.getSex() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getStage())) {
			desc.append("Life Cycle Stage: " + mediaFile.getStage() + "; ");
		}
		if (StringUtils.notEmpty(mediaFile.getBodyPart())) {
			desc.append("Body Part: " + mediaFile.getBodyPart() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getSize())) {
			desc.append("Size: " + mediaFile.getSize() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getView())) {
			desc.append("View: " + mediaFile.getView() + "; ");
		}		
		if (mediaFile.getIsFossil()) {
			desc.append("Fossil: ; ");
		}
		if (StringUtils.notEmpty(mediaFile.getPeriod())) {
			desc.append("Period: " + mediaFile.getPeriod() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getAcknowledgements())) {
			desc.append("Acknowledgements: " + mediaFile.getAcknowledgements() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getCollection())) {
			String collectionAcronym = "";
			if (StringUtils.notEmpty(mediaFile.getCollectionAcronym())) {
				collectionAcronym = " (" + mediaFile.getCollectionAcronym() + ")";
			} 
			desc.append("Collection: " + mediaFile.getCollection() + collectionAcronym + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getType())) {
			desc.append("Type: " + mediaFile.getType() + "; ");
		}		
		if (StringUtils.notEmpty(mediaFile.getCollector())) {
			desc.append("Collector: " + mediaFile.getCollector() + "; ");
		}				
		if (StringUtils.notEmpty(mediaFile.getTechnicalInformation())) {
			desc.append("Technical Information: " + mediaFile.getTechnicalInformation() + "; ");
		}				
		description = desc.toString().trim();
	}

	private String getGroupAttachmentsString(Set nodesSet, boolean notSciNameEmpty) {
		if(nodesSet != null && !nodesSet.isEmpty()) {
			StringBuilder grpAttach = new StringBuilder();
			if (notSciNameEmpty) {
				grpAttach.append(", ");
			}
			Object[] nodes = nodesSet.toArray();
			for(int i = 0; i < nodes.length-1; i++) {
				grpAttach.append(((MappedNode)nodes[i]).getName() + ", ");
			}
			grpAttach.append(((MappedNode)nodes[nodes.length-1]).getName());
			return grpAttach.toString();
		}
		return "";
	}

	private void determineReferences(NodeImage mediaFile) {
		references = new ArrayList<String>();
		if (StringUtils.notEmpty(mediaFile.getReference())) {
			references.add(mediaFile.getReference());
		}
	}

	private void determineSource(NodeImage mediaFile) {
		source = mediaFile.getSourceCollectionUrl();
		if (StringUtils.isEmpty(source)) {
			source = MEDIA_LINK_PREFIX + mediaFile.getId();
		}
	}

	private void determineLanguage(NodeImage mediaFile) {
		if (mediaFile.getLanguages().getHasAnyFields()) {
			Languages langs = mediaFile.getLanguages();
			if (langs.getEnglish()) {
				language = Language.toLanguageCode(Language.English);
			} else if (langs.getFrench()) {
				language = Language.toLanguageCode(Language.French);
			} else if (langs.getGerman()) {
				language = Language.toLanguageCode(Language.German);
			} else if (langs.getSpanish()) {
				language = Language.toLanguageCode(Language.Spanish);
			}
		} else {
			language = Language.ENGLISH_CODE;
		}
	}

	/**
	 * Indicates if the media is considered to be in the public domain. 
	 * @return true if the media is in the public domain; otherwise false. 
	 */
	public boolean isPublicDomain() {
		return licenseInfo.isPublicDomain();
	}
	
	/**
	 * Indicates if the media license is a ToL Use only license.
	 * @return true if the media license is ToL Use only; otherwise false.
	 */
	public boolean isToLRelated() {
		return licenseInfo.isToLRelated();
	}
	
	/**
	 * Gets the Agent's name
	 * @return a string representing the Agent's name
	 */
	public String getAgentName() {
		return getAgent().getName();
	}
	
	/**
	 * Gets the Agent's role
	 * @return a string representing the Agent's role (in lowercase) 
	 */
	public String getAgentRole() {
		return getAgent().getRole().toString().toLowerCase();
	}
	
	/**
	 * Gets the Agent's homepage
	 * @return a string representing the URL of the Agent's homepage
	 */
	public String getAgentHomepage() {
		return getAgent().getHomepage();
	}
	
	/**
	 * Gets the identifier for this media content representation. 
	 * 
	 * The identifier is defined as tol-media-id-{id}, this value 
	 * is used to supply a Dublin Core compliant identifier for 
	 * the representation. 
	 * 
	 * @return a unique alpha-numberic identifier for the representation.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets the data type for this media content representation.
	 * 
	 * The data type is a URI defined by Dublin Core.  A helper 
	 * class, DataTypeDeterminer, is used to discern the correct 
	 * URI to return for this media object. 
	 *   
	 * @return a string representing the URI associated with this 
	 * media data type. 
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Gets the MIME type for this media content representation. 
	 * 
	 * The MIME type is a set of two pieces of text seperated by 
	 * a slash. The first piece defines the content type and the 
	 * second defines the content subtypes (example: text/html). 
	 *   
	 * http://www.iana.org/assignments/media-types/
	 * 
	 * MIME types are determined using a third-party jar, 
	 * mime-util-1.0.jar, that was downloaded from SourceForge. 
	 * 
	 * @return a string representing the MIME type associated with 
	 * this media. 
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Gets the date the media file was created. 
	 * @return the creation date of the media file
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Gets the date the media file was modified. 
	 * @return the last edit date of the media file
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * Gets the title associated with the media file.
	 * @return a string representing the title of the 
	 * media file
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the language code for the textual data 
	 * associated with the media file. 
	 * 
	 * This is a language code defined by ISO 639-1.
	 * Two example would be: English is "en" & French is "fr".
	 * 
	 * http://www.loc.gov/standards/iso639-2/php/code_list.php
	 *  
	 * @return a string representing a language code
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Gets the URL of the Creative Commons License 
	 * governing the media file.  
	 * 
	 * The URL has the type of Creative Commons license 
	 * along with the version of that license.
	 * 
	 * @return a string representing the URL for the 
	 * Creative Commons license. 
	 */
	public String getLicenseLink() {
		return licenseLink;
	}

	/**
	 * Gets a list of references. 
	 * 
	 * The references are simple modeled as string. 
	 * 
	 * @return a collection of string representing 
	 * the references for the media content.  
	 */
	public List<String> getReferences() {
		return references;
	}

	/**
	 * Gets the geographic location related to the 
	 * media file. 
	 * 
	 * @return a string presenting the geographic 
	 * location associated with the media file. 
	 */
	public String getGeoLocation() {
		return geoLocation;
	}

	/**
	 * Gets the agent associated with the media file.
	 * @return
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * Gets the name, homepage, & email for the 
	 * copyright owner for the media file (also 
	 * known as "rights holder" in the Dublin Core 
	 * terminology). 
	 * 
	 * @return
	 */
	public RightsHolder getCopyrightHolder() {
		return copyrightHolder;
	}

	/**
	 * Gets the source of the media file. 
	 * 
	 * This is a Dublin Core bit of data that 
	 * state where the media file came from.
	 * 
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Gets the Tree of Life Web URL for the 
	 * media file.
	 *  
	 * @return a string representing the link 
	 * to the media on the tolweb.org website. 
	 */
	public String getMediaURL() {
		return mediaURL;
	}

	/**
	 * Determines if there is a description available 
	 * for usage. 
	 * @return true, if there is textual data available; 
	 * otherwise false.
	 */
	public boolean hasDescription() {
		return !StringUtils.isEmpty(getDescription());
	}
	
	/**
	 * Gets the Tree of Life Web URL for the 
	 * thumbnail for the media file.
	 *  
	 * @return a string representing the link 
	 * to the thumbnail for the media on the 
	 * tolweb.org website. 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the excluded
	 */
	public boolean isExcludedMediaType() {
		return excludedMediaType;
	}

	/**
	 * @param excluded the excluded to set
	 */
	public void setExcludedMediaType(boolean excluded) {
		this.excludedMediaType = excluded;
	}
}
