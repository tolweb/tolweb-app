package org.tolweb.content.helpers;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.tolweb.dao.ImageDAO;

/**
 * Handles the process of text section containing embedded media files.  
 * These embedded tags are properly resolved to full-qualified links using 
 * thumbnail images as their targets and metadata regarding the media id 
 * and thumbnail URLs are harvested and produces as a side-effect. 
 * 
 * @author lenards
 *
 */
public class EmbeddedMediaHandler {
	public static final String TOLWEB_HOSTNAME = "http://tolweb.org";
	public static final String TOLWEB_TREE_HOSTNAME = "http://tolweb.org/tree";
	
	private static final String TREE_DIR_PREFIX = "/tree"; 
	private static final String FULL_QUAL_REPLACEMENT = "href=\"http://tolweb.org/$2\">";
	private static final String ANCHOR_RELATIVE_REGEX = "href=\"(\\.\\.)?/([^>]*)\"([^<])*>";
	private static final String ANCHOR_TARGET_REGEX = "href=\"#(.*)\"";
	public static final String CSS_ATTRIBUTE_REMOVE_REGEX = "<\\s*([^>]*)\\s*([^>]*)\\s*((class|[^_]id|style)=\"?[^>]*\"?)\\s*([^>]*)>";
	private static final String TOL_IMG_REGEX = "<\\s*ToLimg\\s*([^>]*)\\s*id=\"?(\\d+)\"?\\s*([^>]*)>";
	
	private static final String IMAGE_REPLACEMENT = "<!-- ToL Image #START# --> " + 
	"<a href=\"javascript: w = window.open('http://tolweb.org/media/%1$s'" +
	", '%1$s', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();\">" + 
	"<img src=\"%2$s\"/></a><!-- ToL Image #END# -->";

	private ImageDAO imageDAO; 
	private Pattern tolImgRegex; 
	private HashMap<Integer, String> embeddedMedia;
	private String text; 
	
	/**
	 * Constructs a handler instance fo embedded media 
	 * 
	 * @param sectionText
	 * @param pageUrl
	 * @param imageDAO
	 */
	public EmbeddedMediaHandler(String sectionText, String pageUrl, ImageDAO imageDAO) {
		super();
		this.text = sectionText;
		this.imageDAO = imageDAO;
		tolImgRegex = Pattern.compile(TOL_IMG_REGEX, Pattern.CASE_INSENSITIVE);
		embeddedMedia = new HashMap<Integer, String>();
		this.text = processSectionText(text, pageUrl);
	}

	/**
	 * Removes all class, id, and style attributes from the argument text.
	 * 
	 * @param sectionText the string representing the text
	 * @return a version of the argument text that has all CSS class, id,  
	 * and style attributes removed. 
	 */
	private String stripOutClassIdStyleAttributes(String sectionText) {
		// if we have multiple instances, like 
		// 		<div class="imgcenter" style="text-decoration: underline;">
		// we'll only get the second instance removed... this is a hackety-hack, 
		// but just run the regex again (guilty-party 'lenards')
		sectionText = sectionText.replaceAll(CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		sectionText = sectionText.replaceAll(CSS_ATTRIBUTE_REMOVE_REGEX, "<$1>");
		return sectionText;		
	}	

	/**
	 * Rebases all hypertext references (anchor tags) so be fully qualified 
	 * to the tolweb.org host name.
	 * @param sectionText the text to process
	 * @param pageUrl the relative hypertext reference (or link)
	 * @return a version of the argument text that has fully qualified links 
	 */
	private String processAnchors(String sectionText, String pageUrl) {
		sectionText = sectionText.replaceAll(ANCHOR_TARGET_REGEX, "href=\"" + pageUrl + "#$1\"");
		sectionText = sectionText.replaceAll(ANCHOR_RELATIVE_REGEX, FULL_QUAL_REPLACEMENT);
		return sectionText;
	}

	/**
	 * Perform all processing on the argument text. 
	 * @param sectionText the text to process
	 * @param pageUrl the page link where this text appears (the branch 
	 * or leaf page which the text appears)
	 * @return a full cleansed version of the text
	 */
	private String processSectionText(String sectionText, String pageUrl) {

		// handles href="#link", href="/link", and href="../"
		sectionText = processAnchors(sectionText, pageUrl);
		// as method suggests, this is a temporary solution to resolving embedded image tags in section text
		sectionText = processToLImageTags(sectionText);
		// strip out class, id, and style attributes from embedded markup
		sectionText = stripOutClassIdStyleAttributes(sectionText);
		return sectionText;
	}	
	
	/**
	 * Finds and replaces the embedded media tags within the argument 
	 * text. 
	 * 
	 * A side effect of the processing is that all media appearing 
	 * "embedded" in the text are collected and made available via 
	 * the getEmbeddedMedia() accessor.  This is a map of media id 
	 * to thumbnail URL for usage by other classes. 
	 * 
	 * @param sectionText the text to process 
	 * @return a version of the text with all embedded media resolved 
	 * to popup links using thumbnail images are link-targets that 
	 * are linking back to the media data page for each media file. 
	 */
	private String processToLImageTags(String sectionText) {
		//javascript: w = window.open('/onlinecontributors/app?service=external/ViewImageData&sp=1539', '1539', 'resizable,height=500,width=670,scrollbars=yes'); w.focus();
		//		sectionText = sectionText.replaceAll("<\\s*ToLimg\\s*([^>]*)\\s*id=\"?(\\d+)\"?\\s*([^>]*)>", 
		//				"<a href=\"javascript: w = window.open('http://tolweb.org/media/$2', '$2', 'resizable,height=600,width=800,scrollbars=yes'); w.focus();\">Tree of Life Image</a>");
		
		if (sectionText != null) {
			StringBuffer buffer = new StringBuffer();
			Matcher matcher = tolImgRegex.matcher(sectionText);
			while(matcher.find()) {
				String tolMediaId = matcher.group(2);
				Integer id = Integer.valueOf(tolMediaId);
				String thumbnailUrl = imageDAO.getThumbnailUrlForImageWithId(id.intValue());
				if (!thumbnailUrl.startsWith("/tree")) {
					thumbnailUrl = TOLWEB_TREE_HOSTNAME + thumbnailUrl;
				} else {
					thumbnailUrl = TOLWEB_HOSTNAME + thumbnailUrl;
				}
				embeddedMedia.put(id, thumbnailUrl);
				String fmt = IMAGE_REPLACEMENT;
				String output = String.format(fmt, id, thumbnailUrl);
				matcher.appendReplacement(buffer, output);
			}
			matcher.appendTail(buffer);
			return buffer.toString();
		} else {
			return "";
		}
	}

	public static String resolveThumbnailURL(String thumbnailUrl) {
		if (!thumbnailUrl.startsWith(TREE_DIR_PREFIX)) {
			return TOLWEB_TREE_HOSTNAME + thumbnailUrl;
		} else {
			return TOLWEB_HOSTNAME + thumbnailUrl;
		}		
	}
	
	/**
	 * Gets an instance of the media data access object. 
	 * 
	 * @return an object implementing the ImageDAO interface
	 */
	public ImageDAO getImageDAO() {
		return imageDAO;
	}

	/**
	 * Sets the instance to be the argument 
	 * 
	 * @param imageDAO an object implementing the ImageDAO interface
	 */
	public void setImageDAO(ImageDAO imageDAO) {
		this.imageDAO = imageDAO;
	}

	/**
	 * Gets a map defined the media that is embedded within the text section. 
	 * 
	 * This maps the media id of the embedded media to the URL of the media's 
	 * thumbnail. 
	 * 
	 * @return a map defined the media id's of all the embedded media, 
	 * including the media' thumbnail URL
	 */
	public HashMap<Integer, String> getEmbeddedMedia() {
		return embeddedMedia;
	}

	/**
	 * Gets the text of the section associated with the handler
	 * 
	 * @return a string representing the section's text
	 */
	public String getText() {
		return text;
	}
}
