package org.tolweb.tapestry;

import org.tolweb.treegrow.main.StringUtils;

/**
 * Models the page status component of a page citation. 
 * 
 * @author lenards
 *
 */
public class PageStatusCitation implements PageCitationComponent {
	private String pageStatus;
	
	/**
	 * Constructs an instance given a the string representation of the page status
	 * @param pageStatus
	 */
	public PageStatusCitation(String pageStatus) {
		super();
		this.pageStatus = pageStatus != null ? pageStatus : "";
	} 
	
	/**
	 * Returns a formatted version of the status needed for the citation 
	 * @return a formatted string representation of the page citation status
	 */
	public String getStatus() {
		String tmpStatus = pageStatus;
		tmpStatus = addParens(tmpStatus);
		return format(tmpStatus); 
	}

	/**
	 * Returns a version of the argument with a space and parenthesizes it. 
	 * @param tmpStatus the argument to format
	 * @return a formatted version of the argument 
	 */
	private String addParens(String tmpStatus) {
		return (StringUtils.notEmpty(tmpStatus)) ? " (" + tmpStatus + ")" : tmpStatus;
	}

	/**
	 * Returns a version of the argument with a space and period if the 
	 * argument is not null nor empty. 
	 * @param status the argument to format
	 * @return if not null nor empty, a formatted version of the argument
	 */
	private String format(String status) {
		String period = StringUtils.notEmpty(status) ? "." : "";
		String space = StringUtils.notEmpty(status) ? " " : "";
		return space + status + period;
	}

	/**
	 * Returns a formatted version of the status needed for the citation 
	 * @return a formatted string representation of the page citation status
	 */	
	public String getCitationString() {
		return getStatus();
	}
}
