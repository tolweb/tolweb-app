package org.tolweb.tapestry;

import org.tolweb.treegrow.main.StringUtils;

/**
 * Models the title component of a page citation
 * 
 * A title is made up of the supertitle, title, and 
 * subtitle of the page. 
 * 
 * @author lenards
 *
 */
public class TitleCitation implements PageCitationComponent {
	private String pageSupertitle;
	private String pageTitle;
	private String pageSubtitle;
	
	/**
	 * Constructs an instance given the three title pieces. 
	 * @param pageSupertitle the supertitle for the page
	 * @param pageTitle the title for the page
	 * @param pageSubtitle the subtitle for the page.
	 */
	public TitleCitation(String pageSupertitle, String pageTitle, String pageSubtitle) {
		super();
		this.pageSupertitle = pageSupertitle;
		this.pageTitle = pageTitle;
		this.pageSubtitle = pageSubtitle;
	}
	
	/**
	 * Returns the string representation of the title citation
	 * @return a formatted version of the title citation 
	 */
	public String getCitationString() {
		StringBuilder titleCitation = new StringBuilder();
		if (StringUtils.notEmpty(pageSupertitle)) {
			titleCitation.append(pageSupertitle + ". ");
		}
		if (StringUtils.notEmpty(pageTitle)) {
			titleCitation.append(pageTitle + ". ");
		}
		if (StringUtils.notEmpty(pageSubtitle)) {
			titleCitation.append(pageSubtitle + ".");
		}
		return titleCitation.toString();
	}
}