package org.tolweb.content.helpers;

/**
 * Contains string constants for XML elements used in generating XML responses 
 * to web service requests. 
 * 
 * The element names are based on the EOL Transfer Schema (see the file in 
 * TolwebDocumentation named eol_schema_documentation.pdf)
 * 
 * @author lenards
 *
 */
public class PageContentElements {
	// indicates a successful request and there is data contained
	public static final String ROOT_SUCCESS = "success";
	// something went horribly wrong and you don't have any data yo!
	public static final String ROOT_ERROR = "error";
	
	public static final String ROOT_RESPONSE = "response";
	// <pages>, <contributors>, <sources> are the immediate 
	// child of the root element <success> - <error> will not 
	// contain any element data other than error metadata.
	
	// <pages> and child elements
	public static final String PAGES = "pages";
	public static final String PAGE = "page";
	
	public static final String GROUP = "group";
	public static final String GROUP_DESCRIPTION = "group-description";
	public static final String GROUP_COMMENT = "group-comment";
	
	public static final String NAMES = "names";
	public static final String NAME = "name";
	public static final String OTHERNAMES = "othernames";
	public static final String OTHERNAME = "othername";
	
	public static final String SUBGROUPS = "subgroups";
	public static final String TREEIMAGE = "treeimage";
	public static final String NEWICKTREE = "newicktree";
	public static final String TAXON_LIST = "taxonlist";
	public static final String TREE_COMMENT = "treecomment";
	
	public static final String SECTIONS = "sections";
	public static final String SECTION = "section";
	public static final String SECTION_TEXT = "section-text";
	public static final String SECTION_SOURCE = "section-source";
	public static final String SECTION_MEDIA = "section-media";
	public static final String MEDIA_SET = "media-set";
	public static final String MEDIA_CAPTION = "media-caption";
	public static final String REFERENCES = "references";
	public static final String REFERENCE = "reference";
	public static final String INTERNET_INFO = "internet-info";
	
	// <contributors> and child element
	public static final String CONTRIBUTORS = "contributors";
	public static final String CONTRIBUTOR = "contributor";
	public static final String INSTITUTION = "institution";
	public static final String ADDRESS = "address";
	
	// <sources> and child element
	public static final String SOURCES = "sources";
	public static final String SOURCE = "source";
}
