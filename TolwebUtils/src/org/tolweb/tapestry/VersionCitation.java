package org.tolweb.tapestry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Models the version component of a page citation. 
 * 
 * The version is simply the year of the published version of 
 * the page. 
 * 
 * @author lenards
 *
 */
public class VersionCitation implements PageCitationComponent {
	public static final String PREFIX = "Version ";
	private SimpleDateFormat dateFormat;
	public static final String ABOUT_PAGE_DATE_FORMAT_STRING = "dd MMMM yyyy";
	private Date contentDate;
	private boolean pageHasStatus; 
	
	/**
	 * Constructs an instance given the date the content change and 
	 * whether the page has a status
	 * @param contentDate the date content changed for the page
	 * @param pageHasStatus the text indicating the page status (temporary, under construction, complete)
	 */
	public VersionCitation(Date contentDate, boolean pageHasStatus) {
		this.dateFormat = new SimpleDateFormat(ABOUT_PAGE_DATE_FORMAT_STRING);
		this.contentDate = (contentDate == null) ? new Date() : contentDate;
		this.pageHasStatus = pageHasStatus;
	}
	
	/**
	 * Returns a formatted version of the citation information. 
	 */
	public String getCitationString() {
		String period = pageHasStatus ? "" : ".";
		return PREFIX + dateFormat.format(contentDate) + period;
	}
}
