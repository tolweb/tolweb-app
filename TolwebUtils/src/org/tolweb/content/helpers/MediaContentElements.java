package org.tolweb.content.helpers;

public class MediaContentElements {
	// indicates a successful request and there is data contained
	public static final String ROOT_SUCCESS = "success";
	// something went horribly wrong and you don't have any data yo!
	public static final String ROOT_ERROR = "error";
	
	// <media>/<media-file>, <contributors>, <sources> are the  
	// immediate child of the root element <success> - <error> will not 
	// contain any element data other than error metadata.
	
	// <media> and child elements
	public static final String MEDIA = "media";
	public static final String MEDIA_FILE = "media-file";
	public static final String DESCRIPTION = "description";
	public static final String ALT_TEXT = "alt-text";
	public static final String COMMENTS = "comments";
	public static final String GROUPS = "groups";
	public static final String SPECIMEN_INFO = "specimen-info";
	public static final String BEHAVIOR = "behavior";
	public static final String LANGUAGES = "languages";
	public static final String TECH_INFO = "techinfo";
	public static final String COPYRIGHT = "copyright";
	
	// credits and child elements
	public static final String CREDITS = "credits";
	public static final String CREATOR = "creator";
	public static final String IDENTIFIER = "identifier";
	public static final String ACKNOWLEDGEMENTS = "acknowledgements";
	public static final String REFERENCE = "reference";
	public static final String MEDIA_SOURCE = "media-source";
}
