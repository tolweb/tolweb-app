package org.tolweb.content.helpers;

/**
 * Contains string constants for XML attributes used in generating XML responses 
 * to web service requests. 
 * 
 * The element names are based on the EOL Transfer Schema (see the file in 
 * TolwebDocumentation named eol_schema_documentation.pdf)
 * 
 * @author lenards
 *
 */
public class PageContentAttributes {
	// general attribute definition
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String URL = "url";
	
	// defined in <page>
	public static final String PAGE_URL = "page-url";
	public static final String PAGE_STATUS = "page-status";
	public static final String DATE_CREATED = "date-created";
	public static final String DATE_CHANGED = "date-changed";
	
	// defined in <group>
	public static final String NODE = "node-id";
	public static final String EXTINCT = "extinct";
	public static final String CONFIDENCE = "confidence";
	public static final String PHYLESIS = "phylesis";
	public static final String LEAF = "leaf";
	
	// defined in <name> (child of <names>) and <othername> 
	// (child of <othernames>)
	public static final String ITALICIZE_NAME = "italicize-name";
	public static final String AUTHORITY = "authority";
	public static final String AUTH_DATE = "auth-date";
	public static final String NAME_COMMENT = "name-comment";
	public static final String NEW_COMBINATION = "new-combination";
	public static final String COMBINATION_AUTHOR = "combination-author";
	public static final String COMBINATION_DATE = "combination-date";
	public static final String IS_IMPORTANT = "is-important";
	public static final String IS_PREFERRED = "is-preferred";
	public static final String SEQUENCE = "sequence";
	
	// defined in <section>
	public static final String SECTION_TITLE = "section-title";
	public static final String PAGE_ORDER = "page-order";
	public static final String COPYRIGHT_DATE = "copyright-date";
	public static final String LICENSE = "license";
	public static final String AUTHORS = "authors";
	public static final String CORRESPONDENTS = "correspondents";
	public static final String COPYRIGHT_OWNERS = "copyright-owners";
	public static final String OTHER_COPYRIGHT = "other-copyright";
	public static final String CONTENT_CHANGED = "content-changed";
	
	// defined in <section-source>
	public static final String SOURCE_COLLECTION = "source-collection";
	public static final String SOURCE_TITLE = "source-title";
	public static final String SOURCE_URL = "source-url";
	public static final String MORE_SOURCE = "more-source";
	
	// defined in <contributor>
	public static final String LAST_NAME = "last-name";
	public static final String FIRST_NAME = "first-name";
	public static final String EMAIL = "email";
	public static final String HOMEPAGE = "homepage";
	public static final String TOL_PROFILE = "tol-profile";
	public static final String PORTRAIT = "portrait";
	
	// define in <source>
	public static final String LOGO = "logo";
}
