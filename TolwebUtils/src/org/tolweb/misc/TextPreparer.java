/*
 * Created on Jul 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.misc;

import java.awt.Dimension;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.dao.ImageDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TextPreparer extends BaseTextPreparer {
	private ImageDAO dao;
	private ImageUtils imageUtils;
	private CacheAccess cacheAccess;
	private Pattern treehouseImgRegex;
	private Pattern treehouseImgRegexNoSize;
	private Pattern wysiwygImgRegex;
	private Pattern wysiwygImgRegexNoSize;
    private Pattern wysiwygMediaRegex;
	private Pattern tolimgTagRegex;
    private Pattern tolimgTagWidthHeightRegex;
	private Pattern mailtoRegex;
	private Pattern classRegex;
	private Pattern imageDivRegex;
	private GlossaryLookup glossLookup;

    public static final String AT_VAR_NAME = "asdkj";
    public static final String DOT_VAR_NAME = "dkfhsd";	
    private static final String NOT_CLOSE_TAG = "([^>]*)";
	// tolimgid is the id attribute for a standard img tag in wysiwyg
	public static final String TOL_IMG_ID = "tolimgid";
	// tolimgheight is the height attribute for a standard img tag in wysiwyg
	public static final String TOL_IMG_HEIGHT = "tolimgheight";
	private static final String TOL_IMG_ID_OR_HEIGHT = "(" + TOL_IMG_ID + "|" + TOL_IMG_HEIGHT + ")";
	// special attribute used for wysiwyg preview capabilities so email addresses work
	public static final String ESCAPE_MAILTO = "escapemailto";
	// $1 -- img id
	private static final String TREEHOUSE_IMG_REGEX_NO_SIZE_STRING = "(?:TOLIMG|TOLMEDIA)(\\d+)";
	// $1 -- img id, $2 -- size	
	private static final String TREEHOUSE_IMG_REGEX_STRING = "(?:TOLIMG|TOLMEDIA)(\\d+)(SIZE(\\d+))";
	// $1 -- anything before the img id, $2 -- the img id, $3 -- anything after the height
	private static final String WYSIWYG_IMG_REGEX_NO_SIZE_STRING = "<img([^>]*)" +TOL_IMG_ID + "=\"?(\\d+)\"?([^>]*)>"; 
	// $1 -- anything before the img id, $2 -- either the height or id, $3 -- the value of the height or the id  
	// $4 -- other filler before the other height or id tag, $5 -- the other tag, $6 -- the other value of height or id
	// $7 -- anything else after the other tag
	private static final String WYSIWYG_IMG_REGEX_STRING = "<img([^>]*)" + TOL_IMG_ID_OR_HEIGHT + "=\"?(\\d+)\"?([^>]*)" + TOL_IMG_ID_OR_HEIGHT + "=\"?(\\d+)\"?([^>]*)>";
    // #1 -- the id, all we care about
    private static final String WYSIWYG_MEDIA_REGEX_STRING = "<span" + NOT_CLOSE_TAG + TOL_IMG_ID + "=\"?(\\d+)\"?" + NOT_CLOSE_TAG + ">.+?</span>";
	// $1 -- anything before src, $2 -- src attribute, $3 anything after src
	private static final String TOL_IMG_TAG_REGEX_STRING = "<ToLimg([^>]*)(src=\"[^\"]*\")([^>]*)>";
    // $1 -- anything before first width or height, $2 -- first width or height, $3 -- anything after first, before 2nd, $4 -- second width/height, $5 -- after second
    private static final String TOL_IMG_TAG_HEIGHT_WIDTH_REGEX_STRING = "<ToLimg([^>]*)((?:width|height|alt)=\"[^\"]*\")([^>]*)((?:width|height|alt)=\"[^\"]*\")?([^>]*)((?:width|height|alt)=\"[^\"]*\")?([^>]*)>";    
	// $1 -- email address, $2 text in link
	private static final String ESCAPE_MAILTO_REGEX_STRING = "<a[^>]*href=\"mailto:([^\"]*)\"[^>]*>([^<]*)</a>";
	// $1 -- the class
	private static final String CLASS_REGEX_STRING = "class=\"([^\"]+)\"";
	// used to recognize image divs add little image info html
	private static final String IMAGE_DIV_REGEX_STRING = "(<div" + NOT_CLOSE_TAG + "class=\"(?:imgcenter|imgleft|imgright|imgcenterb|imgleftb|imgrightb|imgcenterbb|imgleftbb|imgrightbb|center)\"" + NOT_CLOSE_TAG + ">)";
	// used to recognize block-level elements that contains images
	private static final String BLOCK_IMAGE_REGEX_STRING = "<(table|ul)" + NOT_CLOSE_TAG + ">";
	
	public TextPreparer() {
	    treehouseImgRegex = Pattern.compile(TREEHOUSE_IMG_REGEX_STRING, Pattern.CASE_INSENSITIVE);
	    treehouseImgRegexNoSize = Pattern.compile(TREEHOUSE_IMG_REGEX_NO_SIZE_STRING, Pattern.CASE_INSENSITIVE);
	    wysiwygImgRegex = Pattern.compile(WYSIWYG_IMG_REGEX_STRING, Pattern.CASE_INSENSITIVE);
	    wysiwygImgRegexNoSize = Pattern.compile(WYSIWYG_IMG_REGEX_NO_SIZE_STRING, Pattern.CASE_INSENSITIVE);
	    tolimgTagRegex = Pattern.compile(TOL_IMG_TAG_REGEX_STRING, Pattern.CASE_INSENSITIVE);
        tolimgTagWidthHeightRegex = Pattern.compile(TOL_IMG_TAG_HEIGHT_WIDTH_REGEX_STRING, Pattern.CASE_INSENSITIVE);
	    mailtoRegex = Pattern.compile(ESCAPE_MAILTO_REGEX_STRING, Pattern.CASE_INSENSITIVE);
        wysiwygMediaRegex = Pattern.compile(WYSIWYG_MEDIA_REGEX_STRING, Pattern.CASE_INSENSITIVE);
        classRegex = Pattern.compile(CLASS_REGEX_STRING, Pattern.CASE_INSENSITIVE);
        imageDivRegex = Pattern.compile(IMAGE_DIV_REGEX_STRING, Pattern.CASE_INSENSITIVE);
	}
	
	public String translateImagesFromWysiwygFormat(String text) {
	    if (StringUtils.notEmpty(text)) {
	        Matcher matcher = wysiwygImgRegex.matcher(text);
	        StringBuffer buffer = new StringBuffer();
		    while (matcher.find()) {
		        String replacementString = "<ToLimg";
		        replacementString += matcher.group(1);
		        String secondGroup = matcher.group(2);
		        int imgId, imgHeight;
		        if (secondGroup.equals(TOL_IMG_HEIGHT)) {
		            imgHeight = Integer.parseInt(matcher.group(3));
		            imgId = Integer.parseInt(matcher.group(6));
		        } else {
		            imgId = Integer.parseInt(matcher.group(3));
		            imgHeight = Integer.parseInt(matcher.group(6));
		        }
		        replacementString += "id=\"" + imgId + "\"";
		        replacementString += matcher.group(4);
		        replacementString += "size=\"" + imgHeight + "\"";
		        replacementString += matcher.group(7);
		        replacementString += ">";
		        replacementString = replacementString.replaceAll("/", "");
		    	matcher.appendReplacement(buffer, replacementString);
			}
			matcher.appendTail(buffer); 
			text = buffer.toString();
	        matcher = wysiwygImgRegexNoSize.matcher(text);
	        buffer = new StringBuffer();
	        while (matcher.find()) {
	        	String replacementString = "<ToLimg";
	        	replacementString += matcher.group(1);
	        	replacementString += "id=\"";
	        	replacementString += matcher.group(2);
	        	replacementString += "\"";
	        	replacementString += matcher.group(3);
	        	replacementString += ">";
	        	replacementString = replacementString.replaceAll("/", "");
	        	matcher.appendReplacement(buffer, replacementString);
	        }
	        matcher.appendTail(buffer);
	        text = buffer.toString();
	        // Now strip out all the src attributes inside the ToLimg tags 
	        matcher = tolimgTagRegex.matcher(text);
	        text = matcher.replaceAll("<ToLimg$1$3>");
            // and clean up any media blocks
            matcher = wysiwygMediaRegex.matcher(text);
            text = matcher.replaceAll("<ToLimg id=\"$2\">");
	        return text;
	    } else {
	        return text;
	    }
	}
	
	public String translateImagesToTreehouseFormat(String text) {
	    if (StringUtils.notEmpty(text)) {
	        // Explicitly replace those with size
	        Matcher matcher = imgWithSizeRegex.matcher(text);
	        text = matcher.replaceAll("TOLIMG$2SIZE$4");
	        // Then get the ones without
		    matcher = imgRegex.matcher(text);
		    return matcher.replaceAll("TOLIMG$2");
	    } else {
	        return text;
	    }
	}
	
	public String translateImagesFromTreehouseFormat(String text) {
	    if (StringUtils.notEmpty(text)) {
	        // First get the ones with size, then the ones without
	        Matcher matcher = treehouseImgRegex.matcher(text);
	        text = matcher.replaceAll("<ToLimg id=\"$1\" size=\"$3\">");
	        matcher = treehouseImgRegexNoSize.matcher(text);
	        text = matcher.replaceAll("<ToLimg id=\"$1\">");
	        return text;
	    } else {
	        return text;
	    }
	}
    
    /**
     * Escape the various sorts of encodings we do and make sure all images
     * are stored in the <ToLimg..> format
     * @param text
     * @return
     */
    public String translateImagesFromAllEditFormats(String text) {
        String preparedText = translateImagesFromTreehouseFormat(text);
        preparedText = translateImagesFromWysiwygFormat(preparedText);
        if (StringUtils.notEmpty(text)) {
	        Matcher matcher = tolimgTagWidthHeightRegex.matcher(preparedText);
	        preparedText = matcher.replaceAll("<ToLimg$1$3$5$7>");
        }
        return preparedText;
    }
	
	public String getNonExistentTreehouseImageId(String text) {
	    return (String) getDisallowedTreehouseImage(text, false);
	}
	
	public NodeImage getDisallowedTreehouseImage(String text) {
	    return (NodeImage) getDisallowedTreehouseImage(text, true);
	}
	
	/**
	 * Goes through and verifies that all images in treehouse text are allowed to be
	 * used (i.e. they don't have an image use option set to one-time use in a
	 * specified location)
	 * @param text
	 * @param checkUse Whether to actually check the use permission or just check
	 * 		  whether the image exists
	 * @return The disallowed image if there was one, or a non-existent image id 
	 * if there was one, otherwise null
	 */
	public Object getDisallowedTreehouseImage(String text, boolean checkUse) {
	    if (StringUtils.notEmpty(text)) {
	        Matcher matcher = treehouseImgRegex.matcher(text);
	        while (matcher.find()) {
	            String imgId = matcher.group(1);
	            NodeImage img = dao.getImageWithId(Integer.parseInt(imgId));
	            if (img != null) {
	                if (checkUse) {
		                if (img.getUsePermission() == NodeImage.RESTRICTED_USE) {
		                    return img;
		                }
	                }
	            } else {
	                return imgId;
	            }
	        }
	        return null;
	    } else {
	        return null;
	    }
	}
	
	public String prepareText(String text, byte cacheType, Serializable cacheKey, boolean useCache) {
	    return (String) prepareText(text, cacheType, cacheKey, useCache, false).get(0);
	}
	
	public List prepareText(String text, byte cacheType, Serializable cacheKey, boolean useCache, boolean useGloss) {
        String result = null;
        List<String> returnList = new ArrayList<String>();
        if (useCache) {
            if (cacheType == CacheAccess.ACCESSORY_PAGE_TEXT_CACHE) {
                result = cacheAccess.getAccessoryPageText((MappedAccessoryPage) cacheKey, useGloss);
            } else {
                result = cacheAccess.getTextSectionTextForPage((MappedPage) cacheKey, useGloss);
            }
            if (result != null) {
	            returnList.add(result);
	            if (useGloss) {
	                if (cacheType == CacheAccess.ACCESSORY_PAGE_TEXT_CACHE) {
	                    returnList.add(cacheAccess.getAccessoryPageJavascript((MappedAccessoryPage) cacheKey));
	                } else {
	                    returnList.add(cacheAccess.getPageJavascript((MappedPage) cacheKey));
	                }
	            }	            
            }
        }
        if (result == null) {
            result = prepareImages(text);
            result = transformMailtosToJavascript(result);
            if (useGloss) {
                returnList = getGlossaryLookup().replaceGlossaryWords(result);
                result = (String) returnList.get(0);
                if (useCache) {
                    if (cacheType == CacheAccess.ACCESSORY_PAGE_TEXT_CACHE) {
                        cacheAccess.setAccessoryPageJavascript((MappedAccessoryPage) cacheKey, (String) returnList.get(1));
                    } else {
                        cacheAccess.setPageJavascript((MappedPage) cacheKey, (String) returnList.get(1));
                    }
                }
            } else {
                returnList.add(result);
            }
            if (useCache) {
                if (cacheType == CacheAccess.ACCESSORY_PAGE_TEXT_CACHE) {
                    cacheAccess.setAccessoryPageText((MappedAccessoryPage) cacheKey, result, useGloss);
                } else {
                    cacheAccess.setTextSectionTextForPage((MappedPage) cacheKey, result, useGloss);
                }
            }
        }
        return returnList;
	}
	
	public String prepareImages(String text) {
	    return prepareMedia(text, false);
	}
	
	/**
	 * Translates ToLimg tags into actual image locations
	 * @param text The text to prepare
     * @param isWysiwyg Whether to add the special attributes for WYSIWYG stuff
	 * @return The text with all ToLimg tags replaced with the actual image location
	 * 	on the webserver
	 */
	public String prepareMedia(String text, boolean isWysiwyg) {
	    if (StringUtils.notEmpty(text)) {
			StringBuffer buffer = new StringBuffer();
			Matcher matcher = imgRegex.matcher(text);
			ImageDAO dao = getImageDAO();
			try {
				while (matcher.find()) {
				    String currentMatch = matcher.group(0);
					Pattern imageMatchPattern = Pattern.compile(IMAGE_DIV_REGEX_STRING + ".*?" + currentMatch + ".*?" + "</div>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
					Pattern tableMatchPattern = Pattern.compile("<table" + NOT_CLOSE_TAG + ">.*?" + currentMatch + ".*?" + "</table>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
					Matcher imgDivMatcher = imageMatchPattern.matcher(text);
					Matcher tableMatcher = tableMatchPattern.matcher(text);
					boolean inNoLinkContext = false;
					if (imgDivMatcher.find() || tableMatcher.find()) {
						inNoLinkContext = true;
					} else {
						// check to see if it's in another block-level element
					}
				    String groupOne = matcher.group(1);
				    String imgId = matcher.group(2);
				    String groupThree = matcher.group(3);
				    int imageId = Integer.parseInt(imgId);
				    int mediaType = dao.getMediaType(imageId);
                    if (mediaType == NodeImage.NON_EXISTENT_MEDIA) {
                        // reference to a non-existent image so don't add anything
                    } else {
    				    StringBuffer replacementStringBuffer;
    				    if (mediaType == NodeImage.IMAGE) {
    					    boolean addImageInfoLink = !isWysiwyg;
    					    Matcher classMatcher = classRegex.matcher(currentMatch);
    					    if (classMatcher.find()) {
    					    	// check to see if it has one of the classes that preclude the link
    					    	String classString = classMatcher.group(1);
    					    	if (classString.equalsIgnoreCase("bottom") || classString.equalsIgnoreCase("middle")
    					    			|| classString.equalsIgnoreCase("top") || classString.equalsIgnoreCase("bottombb")
    					    			|| classString.equalsIgnoreCase("middlebb") || classString.equalsIgnoreCase("topbb")
    					    			|| classString.equalsIgnoreCase("bottomb") || classString.equalsIgnoreCase("middleb")
    					    			|| classString.equalsIgnoreCase("topb") || classString.equalsIgnoreCase("inline")) {
    					    		// if it's inline but has the explicit add inline link flag set, we should still add it
    					    		if (!getImageDAO().getShowInlineImageInfoLinkForImageId(imageId)) {
    					    			addImageInfoLink = false;
    					    		}
    					    	}
    					    }
    					    replacementStringBuffer = new StringBuffer();
    					    if (!inNoLinkContext && addImageInfoLink && getImageDAO().getShowImageInfoLinkForImageId(imageId)) {
    					    	replacementStringBuffer.append(getImageInfoBoxHtml());
    					    }
    					    if (addImageInfoLink) {
    					    	Dimension maxVersionDims = dao.getMaxAllowedVersionDimensions(imageId);
    					    	Dimension actualWindowDims = getImageUtils().getImageInfoWindowSize(maxVersionDims.width, maxVersionDims.height);
    					    	replacementStringBuffer.append("<a href=\"javascript: w = window.open('/onlinecontributors/app?service=external/ViewImageData&sp=" + imageId + "', '" + imageId + "', 'resizable,height=" + (actualWindowDims.height + 100) + ",width=" + (actualWindowDims.width + 100) + ",scrollbars=yes'); w.focus();\">");
    					    }
    					    replacementStringBuffer.append("<img $1");
    					    replacementStringBuffer.append(" src=\"");
    					    int height = -1;
    					    String noSize = "";
    					    if (StringUtils.notEmpty(groupThree)) {
    					        Matcher sizeMatcher = sizeRegex.matcher(groupThree);
    						    try {
    						        if (sizeMatcher.find()) {
    							        String versionHeight = sizeMatcher.group(1);
    							        height = Integer.parseInt(versionHeight);
    							        // Strips out the size declaration so that the rest of the stuff
    							        // can be added back into the tag
    							        noSize = sizeMatcher.replaceAll("");
    						        } else {
    						            noSize = groupThree;
    						        }
    						    } catch (Exception e) {
    						        e.printStackTrace();
    						    }
    					    }
    					    replacementStringBuffer.append(dao.getImageVersionLocationForSizedImageWithId(imageId, height));
    					    replacementStringBuffer.append("\"");
    					    if (StringUtils.notEmpty(noSize)) {
    					        replacementStringBuffer.append(noSize);
    					    }
    					    if (!currentMatch.matches(".*alt=.*")) {
    					        // add the alt attribute in from the img
    					        String altText = dao.getAltTextForImageWithId(imageId);
    					        replacementStringBuffer.append(" alt=\"");
    					        if (StringUtils.notEmpty(altText)) {
    					            replacementStringBuffer.append(altText);
    					        } else {
    					            replacementStringBuffer.append(" ");
    					        }
    					        replacementStringBuffer.append("\" ");
    					    }
    					    // These attributes are used in the WYSIWYG editor in order
    					    // to maintain tolimg identity between form posts
    					    if (isWysiwyg) {
    					        addExtraAttributes(replacementStringBuffer, imageId, height);
    					    }
    					    if (!replacementStringBuffer.toString().matches("/")) {
    					    	replacementStringBuffer.append("/");
    					    }
    					    replacementStringBuffer.append(">");
    					    if (addImageInfoLink) {
    					    	replacementStringBuffer.append("</a>");
    					    }
    				    } else {
                            if (isWysiwyg) {
                                replacementStringBuffer = new StringBuffer(getWysiwygViewMediaString(imageId, mediaType));
                            } else {
                                replacementStringBuffer = getViewMediaStringBuffer(imageId, mediaType, false);
                            }
    				    }
    				    matcher.appendReplacement(buffer, replacementStringBuffer.toString());
                    }
				}
				matcher.appendTail(buffer);
			} catch (Exception e) {e.printStackTrace();}
			String returnString = buffer.toString();
			if (!isWysiwyg) {
				Matcher imageDivMatcher = imageDivRegex.matcher(returnString);
				returnString = imageDivMatcher.replaceAll("$1" + getImageInfoBoxHtml());
			}
			return returnString;
	    } else {
	        return "";
	    }
	}
    
    /**
     * Wraps the standard view media string in a span that is easily parseable
     * so we can get rid of it when things are saved.
     * @param mediaId 
     * @param mediaType
     * @return
     */
    public String getWysiwygViewMediaString(int mediaId, int mediaType) {
        String returnString = "<span " + TOL_IMG_ID + "=\"" + mediaId + "\">";
        returnString += getViewMediaStringBuffer(mediaId, mediaType, true);
        returnString += "</span>";
        returnString = returnString.replaceAll("\\n", "");
        // escape all quotes
        returnString = returnString.replaceAll("'", "\\'");
        return returnString;
    }
    
    public String getViewMediaString(int mediaId, int mediaType) {
        return getViewMediaStringBuffer(mediaId, mediaType, false).toString();
    }
    
    public StringBuffer getViewMediaStringBuffer(int mediaId, int mediaType, boolean isWysiwyg) {
        // it's a document, movie, or sound, so output a thumbnail
        // with a link to view
        StringBuffer replacementString = new StringBuffer(getMediaImageThumbnail(mediaType, null));
        String actionString = getMediaActionString(mediaId, mediaType);
        String features = getImageUtils().getJavascriptViewImageWindowFeatures(400, 200);
        String javascriptString = "javascript: w = window.open('/onlinecontributors/app?service=external/ViewImageData&sp=" + mediaId + "', '" + mediaId + "', '" + features + "'); w.focus();";
        // Only show the view link if it's not WYSIWYG
        replacementString.append("<a href=\"");
        if (!isWysiwyg) {
            replacementString.append(javascriptString);
        }
        replacementString.append("\">");
        replacementString.append(actionString);
        replacementString.append("</a>");
        return replacementString;
    }
    
    public String getMediaImageThumbnail(int mediaType, String classString) {
        String returnString = "<img src=\"/tree/ToLimages/";
        if (mediaType == NodeImage.DOCUMENT) {
            returnString += "docmini.gif\" alt=\"document icon\"";
        } else if (mediaType == NodeImage.SOUND) {
            returnString += "soundmini.gif\" alt=\"sound icon\"";
        } else if (mediaType == NodeImage.MOVIE) {
            returnString += "moviemini.gif\" alt=\"movie icon\"";
        }       
        if (StringUtils.notEmpty(classString)) {
            returnString += " class=\"" + classString + "\"";
        }
        returnString += " />";
        return returnString;
    }
    
    public String getMediaActionString(int imageId, int mediaType) {
        String returnString = "";
        String title = getImageDAO().getTitleForMediaWithId(imageId);
        String mediaTypeWord = getWordForMediaType(mediaType);
        String secondPart = StringUtils.notEmpty(title) ? title : mediaTypeWord;
        if (mediaType == NodeImage.DOCUMENT) {
            returnString = "View " + secondPart;
        } else if (mediaType == NodeImage.SOUND) {
            returnString = "Listen to " + secondPart;
        } else if (mediaType == NodeImage.MOVIE) {
            returnString = "View " + secondPart;
        }        
        return returnString;
    }
    
    public String getDetailedMediaActionString(int imageId, int mediaType) {
        String returnString = "";
        String mediaTypeWord = getWordForMediaType(mediaType);
        if (mediaType == NodeImage.DOCUMENT) {
            returnString = "Download " + mediaTypeWord;
        } else if (mediaType == NodeImage.SOUND) {
            returnString = "Listen to " + mediaTypeWord;
        } else {
            returnString = "Play " + mediaTypeWord;
        }
        return returnString;
    }
    
    private String getWordForMediaType(int mediaType) {
    	return StringUtils.capitalizeString(NodeImage.getWordForMediaType(mediaType));
    }
	
	private void addExtraAttributes(StringBuffer replacementString, int imageId, int height) {
        replacementString.append(" " + TOL_IMG_ID + "=\"" + imageId + "\" ");
        if (height > 0) {
            replacementString.append(" " + TOL_IMG_HEIGHT + "=\"" + height + "\" ");
        }	    
	}
	
	public String transformMailtosToJavascript(String original) {
	    Matcher matcher = mailtoRegex.matcher(original);
	    StringBuffer buffer = new StringBuffer();
	    while (matcher.find()) {
	        String email = matcher.group(1);
	        String name = matcher.group(2);
            // if it's not a valid email, don't bother trying to encode it.
            // this will allow kids' pseudonyms to work properly
	        String jabbascript = getEncodedMailLinkString(email, name);
	    	matcher.appendReplacement(buffer, jabbascript);
		}
		matcher.appendTail(buffer);
	    return buffer.toString();
	}
	
	public String oldPrepareImages(String text) {
		int index;
		String beforeId, id, afterId;
		StringBuffer buffer = new StringBuffer(text);
		Matcher matcher = imgRegex.matcher(buffer);
//		ImageDAO dao = getImageDAO();
		while (matcher.find()) {
			index = matcher.start(1);
			if (index != -1) {
				beforeId = buffer.substring(matcher.start(1), matcher.end(1));
			} else {
				beforeId = "";
			}
			id = buffer.substring(matcher.start(2), matcher.end(2));
			index = matcher.start(3);
			if (index != -1) {
				afterId = buffer.substring(matcher.start(3), matcher.end(3));
			} else {
				afterId = "";
			}
			buffer.replace(matcher.start(), matcher.end(), getActualImageString(beforeId, id, afterId));
		}
		return buffer.toString();	    
	}
	
	public void setImageDAO(ImageDAO value) {
		dao = value;
	}
	
	public ImageDAO getImageDAO() {
		return dao;
	}
	
    public GlossaryLookup getGlossaryLookup() {
        return glossLookup;
    }
    public void setGlossaryLookup(GlossaryLookup glossLookup) {
        this.glossLookup = glossLookup;
    }
    
	public void setCacheAccess(CacheAccess value) {
	    cacheAccess = value;
	}
	
	public CacheAccess getCacheAccess() {
	    return cacheAccess;
	}
	
	private String getActualImageString(String beforeId, String id, String afterId) {
		String returnString = "<img src=\"";
		String location = getImageDAO().getImageLocationWithId(Integer.valueOf(id).intValue());
		if (location != null) {		
			returnString += "/tree/ToLimages/" + location + "\"";
			if (StringUtils.notEmpty(beforeId)) {
				returnString += " " + beforeId;
			}
			if (StringUtils.notEmpty(afterId)) {
				returnString += " " + afterId;			
			}
			returnString += ">";
			return returnString;
		} else {
			return "";
		}
	}
	
	public List getNewlineSeparatedList(String input) {
        ArrayList<String> list = new ArrayList<String>();        
	    if (input != null) {
	        LineNumberReader reader = new LineNumberReader(new StringReader(input));
	        String currentLine;
	        try {
		        while ((currentLine = reader.readLine()) != null) {
		            list.add(currentLine);
		        }
	        } catch (Exception e) {
	            e.printStackTrace(); 
	        } finally {
	            try {
	                reader.close();
	            } catch (Exception e) {}
	        }
        }
        return list;
	}
	
	public String getUlListStringWithSemicolons(List list, String liClass) {
	    return getUlListString(list, liClass, true);
	}
	
	public String getUlListString(List list, String liClass, boolean addSemicolons) {
        StringBuilder returnString = new StringBuilder();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            String nextString = (String) iter.next();
            String semicolonString = addSemicolons && iter.hasNext() ? ";" : "";
            String classString = liClass != null ? "class=\"" + liClass + "\"" : "";
            returnString.append("<li"); 
            returnString.append(classString);  
            returnString.append(">");  
            returnString.append(nextString);
			returnString.append(semicolonString);
			returnString.append("</li>"); 
        }
        return returnString.toString();	    
	}
    /**
     * Returns a javascript encoded mail link
     * @param email The email address to send the mail to 
     * @param text The text that the link should say
     * @return The javascript string that emits the link
     */
    public String getEncodedMailLinkString(String email, String text) {
        try {
	        StringBuffer returnString = new StringBuffer();
	        returnString.append("<script language=\"javascript\" type=\"text/javascript\">\n");
	        returnString.append("var ");
	        returnString.append(AT_VAR_NAME);
	        returnString.append("= unescape(\"%40%0A\");\n");
	        returnString.append("var ");
	        returnString.append(DOT_VAR_NAME);
	        returnString.append("= unescape(\"%2E%0A\");\n");
	        List pieces = getPieces(email, text);
	        int i, length = pieces.size();
	        for (i = 0; i < length - 1; i++) {
	            returnString.append(pieces.get(i));
	            returnString.append("\n");
	        }
	        returnString.append("document.write(unescape(\"%3C%61%20%68%72%65%66%3D%22%6D%61%69%6C%74%6F%3A%0A\"));\n");
	        returnString.append("document.write(");
	        returnString.append(pieces.get(i));
	        returnString.append(");\n");
	        returnString.append("document.write(x");
	        returnString.append(i - 1);
	        returnString.append(");\n");
	        returnString.append("document.write(unescape(\"%3C%2F%61%3E%0A\"));\n");
	        returnString.append("</script>");
	        return returnString.toString();
        } catch (Exception e) {
            // if something screwed up it's not a valid email anyway
            // (such as a kids alias), so just return it as is.
            return email;
        }
    }
    
    /**
     * Takes an email address and returns a list of javascrip snippets to write out that
     * address in a mailto: link
     * @param email The email address to break down
     * @param linkText The text that should show up in the link
     * @return A list with the first n - 2 items being javascrip snippets for the address,
     * 		   the n-1 item being the link text, and the nth item being a javscript
     * 		   snippet concatenating all of the individual vars to form the mailto: string 
     */
    private List getPieces(String email, String linkText) {
//        Random random = new Random(System.currentTimeMillis());
        StringBuffer actualEmailJS = new StringBuffer();
        List<String> returnList = new ArrayList<String>();
        int atLoc = email.indexOf('@');
        String beforeAt = email.substring(0, atLoc);
        returnList.add(encodeString(beforeAt, 0));
        actualEmailJS.append("x0 + ");
        actualEmailJS.append(AT_VAR_NAME);
        actualEmailJS.append(" + ");
        String afterAt = email.substring(atLoc + 1);
        String[] tokens = afterAt.split("\\.");
        int i;
        for (i = 0; i < tokens.length; i++) {
            returnList.add(encodeString(tokens[i], i + 1));
            actualEmailJS.append("x");
            actualEmailJS.append(i + 1);
            actualEmailJS.append(" + ");
            if (i != tokens.length - 1) {
	            actualEmailJS.append(DOT_VAR_NAME);
	            actualEmailJS.append(" + ");
            }
        }
        actualEmailJS.append("unescape(\"%22%3E%0A\")");
        // Now encode the link text
        returnList.add(encodeString(linkText, i + 1));
        // Add the email js string to the end of the list
        returnList.add(actualEmailJS.toString());        
        return returnList;
    }
    /**
     * Encodes a plain-text string into a javascript hexencoded variable
     * @param toEncode The string to encode
     * @param num The number of the javascript variable
     * @return
     */
    private String encodeString(String toEncode, int num) {
        String varName = "x" + num;
        StringBuffer encodedString = new StringBuffer("var ");
        encodedString.append(varName);
        encodedString.append(" = unescape('");
        for (int i = 0; i < toEncode.length(); i++) {
            char nextChar = toEncode.charAt(i);
            //encodedString.append(varName);
            encodedString.append("%");
            encodedString.append(Integer.toString((int) nextChar, 16));
        }
        encodedString.append("');\n");
        //encodedString.append("alert(\"val is: \" + " + varName + ");");
        return encodedString.toString();
    }
	public String getContributorNameString(Contributor contr, boolean generateLinks, 
			boolean noJavascriptEmail, boolean escapeGeneratedLinks) {
	    String openBracket = escapeGeneratedLinks ? "&lt;" : "<";
        String closeBracket = escapeGeneratedLinks ? "&gt;" : ">";
        String copyOwnerString;
	    copyOwnerString = contr != null ? contr.getDisplayName() : "";
		// If there is no name for the contributor, then they are an institutional
		// contributor, so return the institution's name
		if (StringUtils.isEmpty(copyOwnerString) && contr != null) {
		    copyOwnerString = contr.getInstitution();
		}
		String openLinkString = "", closeLinkString = "";
		if (generateLinks) {
			String homepage = contr != null ? contr.getHomepage() : "";			
			if (StringUtils.notEmpty(homepage)) {
				if (!homepage.startsWith("http://")) {
					// add in the http prefix to the homepage link so it works
					homepage = "http://" + homepage;
				}
				openLinkString = openBracket + "a href=\"" + homepage + "\"" + closeBracket; 
				closeLinkString = openBracket + "/a" + closeBracket;
			} else if (contr != null && StringUtils.notEmpty(contr.getEmail()) && !contr.dontShowEmail() && !escapeGeneratedLinks) {
			    if (!noJavascriptEmail) {
			        copyOwnerString = getEncodedMailLinkString(contr.getEmail(), copyOwnerString);
			    } else {
			        copyOwnerString = openBracket + "a href=\"mailto:" + contr.getEmail() + "\"" + closeBracket + copyOwnerString + openBracket + "/a" + closeBracket;
			    }
			}
		}
		return openLinkString + copyOwnerString + closeLinkString;		
	}  
	/**
	 * 
	 * @param img the image to get the string for
	 * @param generateLinks whether to generate links to the copyright owner's email or webpage
	 * @param noJavascriptEmail whether to suppress javascript email encoding
	 * @param escapeGeneratedLinks whether to escape the generated links 
	 * @return An html string with information about the image copyright holder
	 */
	public String getCopyrightOwnerString(NodeImage img, boolean generateLinks, boolean noJavascriptEmail, boolean escapeGeneratedLinks) {
        String openBracket = escapeGeneratedLinks ? "&lt;" : "<";
        String closeBracket = escapeGeneratedLinks ? "&gt;" : ">";        
		String returnString = "";
		String openLinkString = "", closeLinkString = "", copyOwnerString = "";
		boolean hasData = (img.getCopyrightOwnerContributor() != null || StringUtils.notEmpty(img.getCopyrightOwner()) && !img.getInPublicDomain());
		if (hasData) {
            String ampString = escapeGeneratedLinks ? "&amp;" : "&";
			returnString += ampString + "copy; ";
			if (StringUtils.notEmpty(img.getCopyrightDate())) {
			    returnString += img.getCopyrightDate() + " ";
			}
			if (img.getCopyrightOwnerContributor() != null) {
				Contributor contr = img.getCopyrightOwnerContributor();
				returnString += getContributorNameString(contr, generateLinks, noJavascriptEmail, escapeGeneratedLinks);
			} else if (img.getCopyrightOwner() != null) {
				copyOwnerString = img.getCopyrightOwner();
				if (generateLinks) {
					if (StringUtils.notEmpty(img.getCopyrightUrl())) {
						openLinkString = openBracket + "a href=\"" + img.getCopyrightUrl() + "\"" + closeBracket; 
						closeLinkString = openBracket + "/a" + closeBracket;					
					} else if (StringUtils.notEmpty(img.getCopyrightEmail()) && !escapeGeneratedLinks) {
					    if (!noJavascriptEmail) {
					        copyOwnerString = getEncodedMailLinkString(img.getCopyrightEmail(), img.getCopyrightOwner());
					    } else {
					        copyOwnerString = openBracket + "a href=\"mailto:" + img.getCopyrightEmail() + "\" " + TextPreparer.ESCAPE_MAILTO + "=\"1\"" + closeBracket + img.getCopyrightOwner() + openBracket + "/a" + closeBracket;
					    }
					}
				}
				returnString += openLinkString + copyOwnerString + closeLinkString;				
			}
		} else if (img.getInPublicDomain()) {
			return "";
		}
		return returnString;
	}	
    /**
     * @return Returns the imageUtils.
     */
    public ImageUtils getImageUtils() {
        return imageUtils;
    }
    /**
     * @param imageUtils The imageUtils to set.
     */
    public void setImageUtils(ImageUtils utils) {
        this.imageUtils = utils;
    }
    
    public String getImageInfoBoxHtml() {
    	return "<div class=\"aboutimg\"><img src=\"/tree/img/magnify.gif\" alt=\"Click on an image to view larger version &amp; data in a new window\" >" + 
    		"<div class=\"aboutimgtext\">Click on an image to view larger version &amp; data in a new window </div></div>";
    }
}
