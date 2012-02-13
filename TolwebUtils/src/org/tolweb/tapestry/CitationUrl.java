package org.tolweb.tapestry;

/**
 * Models the URL component of a page citation. 
 * 
 * The URL component is made up of the archived URL, the project 
 * name, and the project website. 
 * 
 * @author lenards
 *
 */
public class CitationUrl implements PageCitationComponent {
	// paraphrased example from @AboutPage tapestry component:
	// <citationUrl> <em>in</em> The Tree of Life Web Project, http://tolweb.org/
	public static final String FORMAT = "%1$s in %2$s, %3$s";
	public static final String PROJECT_NAME = "The Tree of Life Web Project";
	public static final String PROJECT_PAGE = "http://tolweb.org/";
	private String archivedUrl;
	private String projectName;
	private String hostUrl;

	/**
	 * Constructs an instance given the archived URL
	 * @param archivedUrl the URL for the archived version of the page
	 */
	public CitationUrl(String archivedUrl) {
		this(archivedUrl, PROJECT_PAGE);
	}
	
	/**
	 * Constructs an instance given the archive URL and the project 
	 * webpage URL 
	 * @param archivedUrl the URL for the archived version of the page
	 * @param hostUrl the URL for the project webpage
	 */
	public CitationUrl(String archivedUrl, String hostUrl) {
		super();
		this.archivedUrl = archivedUrl != null ? archivedUrl : "";
		this.hostUrl = hostUrl != null ? hostUrl : "";
		this.projectName = PROJECT_NAME;
	}
	
	/**
	 * Returns a formatted version of the citation information for 
	 * this component. 
	 */
	public String getCitationString() {
		return String.format(FORMAT, archivedUrl, projectName, hostUrl);
	}

	/**
	 * Gets the name of the project. 
	 * @return a string representing the name of the project
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Sets the name of the project used when formatting the 
	 * citation information. 
	 * @param projectName the name of the project
	 */
	public void setProjectName(String projectName) {
		this.projectName = (projectName != null) ? projectName : "";
	}
}
