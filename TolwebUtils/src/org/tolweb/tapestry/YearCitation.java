package org.tolweb.tapestry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Models the Year component of a page citation. 
 * 
 * @author lenards
 *
 */
public class YearCitation implements PageCitationComponent {
	private Date mostRecentVersion;
	private SimpleDateFormat yearFormat;
	
	/**
	 * Constructs an instance with the most recent version of a page.
	 * @param mostRecentVersion
	 */
	public YearCitation(Date mostRecentVersion) {
		this.mostRecentVersion = mostRecentVersion; 
		yearFormat = new SimpleDateFormat("yyyy");
	}
	
	/**
	 * Returns the citation string for this component
	 * @return
	 */
	public String getCitationString() {
		Date date = mostRecentVersion;
		if (date == null) {
			date = new Date();
		}
		return yearFormat.format(date) + ". ";
	}
}
