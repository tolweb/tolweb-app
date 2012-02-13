package org.tolweb.content.helpers;

/**
 * Represents a textual link used when hyperlinking to content. 
 * 
 * @author lenards
 *
 */
public class TextLinkMaker {
	/* (Sample documentation)
	 * Example: 
	 *  <a href="javascript: w = window.open('http://tolweb.org/media/4748', 
	 *  	'4748', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();">
	 *  	<img src="http://tolweb.org/tree/ToLimages/beetleflying.100a.gif"/></a>
	 */

	/**
	 * the hyperlink format for text links 
	 */
	public static final String LINK_FORMAT = "<a href=\"javascript: w = window.open('%1$s#toptree'," +
		"'%2$s', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();\">%3$s</a>";
	
	private String url;
	private String objectId;
	private String linkText;
	
	/**
	 * Constructs a link maker instance. 
	 * 
	 * @param url the URL the hyperlink will refer
	 * @param objectId the object id of the media 
	 * @param linkText the text that will appear within the link
	 */
	public TextLinkMaker(String url, String objectId, String linkText) {
		super();
		this.url = url;
		this.objectId = objectId;
		this.linkText = linkText;
	}

	/**
	 * Returns a textual hyperlink for the state of the maker
	 * 
	 * @return a string representing a valid HTML hyperlink 
	 */
	public String makeLink() {
		return String.format(LINK_FORMAT, url, objectId, linkText);
	}
}