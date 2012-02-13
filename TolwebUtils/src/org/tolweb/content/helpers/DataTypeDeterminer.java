package org.tolweb.content.helpers;

import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

import eu.medsea.util.MimeUtil;

/**
 * Determines the proper URI data type for content. 
 * 
 * @author lenards
 *
 */
public class DataTypeDeterminer {
	// all support purl data type URIs
	/** the purl data type URI for text */
	public static final String TEXT_URI = "http://purl.org/dc/dcmitype/Text";
	/** the purl data type URI for still images */
	public static final String STILL_IMAGE_URI = "http://purl.org/dc/dcmitype/StillImage";
	/** the purl data type URI for sounds */
	public static final String SOUND_URI = "http://purl.org/dc/dcmitype/Sound";
	/** the purl data type URI for movies */
	public static final String MOVING_IMAGE_URI = "http://purl.org/dc/dcmitype/MovingImage";
	/** the purl data type URI for datasets */
	public static final String DATASET_URI = "http://purl.org/dc/dcmitype/Dataset";
	// unsupported purl data type URIs
	/** the purl data type URI for interactive resources (not supported) */
	public static final String INTERACTIVE_RESOURCE_URI = "http://purl.org/dc/dcmitype/InteractiveResource";
	/** the purl data type URI for software (not supported) */
	public static final String SOFTWARE_URI = "http://purl.org/dc/dcmitype/Software";

	/** indicates an unknown MIME type (defined by the MimeUtil class) */
	public static final String UNKNOWN_MIME_TYPE = MimeUtil.UNKNOWN_MIME_TYPE;	
	
	/** MIME type for text */
	public static final String TEXT_SECTION_MIME = "text/html";
	
	private int discriminator;
	private boolean isDataSet;
	private String mimeType;
	private boolean isUnknown;
	
	/**
	 * Constructs a determiner for page sections
	 * 
	 * @param mtxt ToL domain object representing a page text section
	 */
	public DataTypeDeterminer(MappedTextSection mtxt) {
		discriminator = NodeImage.DOCUMENT;
		isDataSet = false;
		mimeType = TEXT_SECTION_MIME;
	}

	/**
	 * Constructs a determiner for media 
	 * 
	 * @param mediaFile ToL domain object representing a media file
	 */
	public DataTypeDeterminer(NodeImage mediaFile) {
		discriminator = mediaFile.getMediaType();
		if (Document.class.isInstance(mediaFile)) {
			isDataSet = ((Document)mediaFile).getIsDataset();
		}
		mimeType = MimeUtil.getMimeType(mediaFile.getLocation());
		if (StringUtils.isEmpty(mimeType) || UNKNOWN_MIME_TYPE.equals(mimeType)) {
			mimeType = UNKNOWN_MIME_TYPE;
			isUnknown = true;
		}
	}
	
// 		Documentation from the EOL Schema Documentation pdf
//		(TolwebDocumentation/eol_schema_documentation.pdf)
//		# http://purl.org/dc/dcmitype/Text  
//			use this for all text sections AND for media if Images.discriminator = 3 and Images.is_dataset is not 1
//		# http://purl.org/dc/dcmitype/StillImage 
//			use this if Images.discriminator = 0
//		# http://purl.org/dc/dcmitype/Sound 
//			use this if Images.discriminator = 1
//		# http://purl.org/dc/dcmitype/MovingImage 
//			use this if Images.discriminator = 2
//		# http://purl.org/dc/dcmitype/InteractiveResource 
//			not yet supported
//		# http://purl.org/dc/dcmitype/Software 
//			not yet supported
//		# http://purl.org/dc/dcmitype/Dataset 
//			use this if Images.discriminator = 3 and Images.is_dataset = 1
	/**
	 * Gets the purl URI for the data type of the content
	 * 
	 * @return a string representing the purl URI 
	 */
	public String getDataType() {
		if (isImage()) { // Images.discriminator = 0
			return STILL_IMAGE_URI;
		} else if (isSound()) { // Images.discriminator = 1
			return SOUND_URI;
		} else if (isMovie()) {
			return MOVING_IMAGE_URI;
		} else if (isDocument()) {
			return isDataSet ? DATASET_URI : TEXT_URI;
		} else {
			return "";
		}
	}
	
	/**
	 * Gets the MIME type of the content
	 * 
	 * @return a string representing the MIME type of the content
	 */
	public String getMimeType() {
		return mimeType;
	}

	private boolean isDocument() {
		return discriminator == NodeImage.DOCUMENT;
	}

	private boolean isMovie() {
		return discriminator == NodeImage.MOVIE;
	}

	private boolean isSound() {
		return discriminator == NodeImage.SOUND;
	}

	private boolean isImage() {
		return discriminator == NodeImage.IMAGE;
	}

	/**
	 * @return the isUnknown
	 */
	public boolean isUnknown() {
		return isUnknown;
	}

	/**
	 * @param isUnknown the isUnknown to set
	 */
	public void setUnknown(boolean isUnknown) {
		this.isUnknown = isUnknown;
	}
}
