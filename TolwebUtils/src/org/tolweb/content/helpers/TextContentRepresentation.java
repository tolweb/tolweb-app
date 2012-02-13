package org.tolweb.content.helpers;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.tolweb.hibernate.MappedPage;
import org.tolweb.hibernate.MappedTextSection;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.PageContributor;

/**
 * An abstracted representation of the text content of a Text Section
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
 */
public class TextContentRepresentation {
	public static final String ENGLISH_LANGUAGE_CODE = "en";
	public static final String IDENTIFIER_PREFIX = "tol-text-id-";
	
	public static final String PHYLO_DISCUSSION = "Discussion of Phylogenetic Relationships";
	
	private String identifier;
	private String dataType;
	private String mimeType;
	private Date createdDate;
	private Date modifiedDate;
	private String title; 
	private Map<String, Contributor> authors;
	private List<String> authorNames;
	private Map<String, RightsHolder> copyrightHolders; 
	private String language; 
	private String licenseLink;
	private List<String> references;
	private String subject; 
	
	/** 
	 * Creates a new instance of a text content representation using the arguments. 
	 * 
	 * Both the parent page and text section are required because at the time of 
	 * implementation the text sections were child objects of (or dependent upon) 
	 * the parent page (the mapped entity object).  There may be motivation for 
	 * refactoring away this dependence because individual attribution of text 
	 * sections is a need that will likely get implemented. But the original intent 
	 * may be of some use in the future. 
	 *  
	 * @param mpage the parent page of the text section 
	 * @param mtxt the text section state that will be represented 
	 */
	public TextContentRepresentation(MappedPage mpage, MappedTextSection mtxt) {
		identifier = IDENTIFIER_PREFIX + mtxt.getTextSectionId();
		dataType = DataTypeDeterminer.TEXT_URI;
		mimeType = DataTypeDeterminer.TEXT_SECTION_MIME;
		// note: text sections are not independent of their parent page  
		// yet so we'll be asking the parent page questions that in the  
		// future may be known or stored in the text section.
		createdDate = mpage.getFirstOnlineDate();
		modifiedDate = mpage.getContentChangedDate();
		title = mtxt.getHeading();
		// default language for Text Sections is currently English
		language = ENGLISH_LANGUAGE_CODE;
		licenseLink = ContributorLicenseInfo.linkString(new ContributorLicenseInfo(mpage.getUsePermission()));
		initializeAuthorNames(mpage);
		initializeCopyrightHolders(mpage);
		initializeReferences(mpage);
		determinedSubject(mtxt);
	}

	private void determinedSubject(MappedTextSection mtxt) {
		TdwgSubjectDeterminer tsd = new TdwgSubjectDeterminer(mtxt);
		subject = tsd.getTdwgUri();
	}

	private void initializeReferences(MappedPage mpage) {
		String input = mpage.getReferences();
		references = new ArrayList<String>();
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
	}

	private void initializeCopyrightHolders(MappedPage mpage) {
		copyrightHolders = RightsHolderFactory.createRightsHolderFor(mpage);
	}

	/**
	 * Initializes the author names (full name -> homepage) using the sortedness 
	 * of the contributors collection off the text section's parent page. 
	 * 
	 * The sorted order defines the "author order" - or the order in which the 
	 * authors are to appear on the page (or when referring to the 
	 * @param mpage
	 */
	private void initializeAuthorNames(MappedPage mpage) {
		SortedSet contributors = mpage.getContributors();
		authors = new HashMap<String, Contributor>();
		authorNames = new LinkedList<String>();
		if (contributors != null) {
			for (Iterator itr = contributors.iterator(); itr.hasNext(); ) {
			    PageContributor contr = (PageContributor) itr.next();
			    if (contr.getIsAuthor()) {
			    	Contributor contributor = contr.getContributor();
			    	String cFullName = getContributorFullName(contributor);
			    	authorNames.add(cFullName);
			    	authors.put(cFullName, contributor);
			    }
			}
		} 
	}
	
	/**
	 * Returns the formatted full name of a contributor. 
	 * @param c the contributor
	 * @return a formatted full name representation for the contributor
	 */
	private String getContributorFullName(Contributor c) {
		return String.format("%1$s %2$s", c.getFirstName(), c.getLastName());
	}

	/**
	 * Gets the created date of the text section.
	 * @return a date representing the created date of the text section
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Gets the modified date of the text section.
	 * @return a date representing the modified date of the text section
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * Gets the title of the text section. 
	 * @return a string representing the title of the text section
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets a map of author names mapped to their homepages. 
	 * @return a map of author names mapped to their homepages.
	 */
	public Map<String, Contributor> getAuthors() {
		return authors;
	}

	public List<String> getAuthorNames() {
		return authorNames;
	}
	
	/**
	 * Gets the language that the text section was written in. 
	 * @return A string representing the language code for 
	 * the text.  Most likely this will always for "en" for 
	 * English.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Gets the license link associated with the text section.
	 * 
	 * @return a string representing the URL for the license 
	 * associated with the text section.  This will likely be 
	 * one of the Creative Commons licenses. 
	 */
	public String getLicenseLink() {
		return licenseLink;
	}

	/**
	 * Gets the identifier for the text content representation. 
	 * 
	 * The identifier is used in the context of the Dublin Core 
	 * identifier and is made up of the IDENTIFIER_PREFIX and 
	 * the database id of record representing the text section. 
	 * 
	 * @return a unique string representation
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Gets a URL representing the DCMI type of the text content 
	 * representation. 
	 * @return
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * Gets the mime-type defined for the text content representation. 
	 * 
	 * This should always be "text/html". 
	 * @return
	 */
	public String getMimeType() {
		return mimeType;
	}

	public Map<String, RightsHolder> getCopyrightHolders() {
		return copyrightHolders;
	}

	/** 
	 * Gets the expected audience of the text content representation. 
	 * 
	 * @return 
	 */
	public List<String> getAudience() {
		List<String> audience = new ArrayList<String>();
		audience.add(Audience.toString(Audience.GeneralPublic));
		if (!PHYLO_DISCUSSION.equals(getTitle())) {
			audience.add(Audience.toString(Audience.ExpertUsers));
		}
		return audience;
	}
	


	public List<String> getReferences() {
		return references;
	}

	/**
	 * Gets the TDWG type for the subject matter of the content.
	 * @return a string URI representing the TDWG-defined subject matter
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the TDWG type for the subject matter of the content.
	 * @param subject a string URI representing the TDWG-defined subject matter
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
}
