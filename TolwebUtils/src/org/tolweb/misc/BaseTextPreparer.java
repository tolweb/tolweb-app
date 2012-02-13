package org.tolweb.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseTextPreparer {
	// $1 -- stuff in tag before img id, $2 -- img id, $3 -- stuff in tag after img id -- including size info	
	protected static final String TOL_IMG_REGEX_STRING;// = "<\\s*ToLimg\\s*([^>]*)\\s*id=\"?(\\d+)\"?\\s*([^>]*)>";
	protected static final String TOL_IMG_REGEX_SIZE_STRING;
	protected static final String SIZE_REGEX_STRING = "size=\"?(\\d+)\"?";
	protected Pattern imgRegex;
	protected Pattern imgWithSizeRegex;
	protected Pattern sizeRegex;
	protected Pattern controlCharsRegex;
	
	static {
	    String whitespace = "\\s*";
	    String id = "id=\"?(\\d+)\"?";
	    String notClosingBracket = "([^>]*?)";
	    TOL_IMG_REGEX_STRING = "<" + whitespace + "tolimg" + notClosingBracket + id + notClosingBracket + ">";
	    TOL_IMG_REGEX_SIZE_STRING = "<" + whitespace + "tolimg" + notClosingBracket + id + notClosingBracket + SIZE_REGEX_STRING + ">";
	}
	
	public BaseTextPreparer() {
	    imgRegex = Pattern.compile(TOL_IMG_REGEX_STRING, Pattern.CASE_INSENSITIVE);
	    imgWithSizeRegex = Pattern.compile(TOL_IMG_REGEX_SIZE_STRING, Pattern.CASE_INSENSITIVE);
	    sizeRegex = Pattern.compile(SIZE_REGEX_STRING, Pattern.CASE_INSENSITIVE);
	    controlCharsRegex = Pattern.compile("\\p{Cntrl}");
	}
	
	public List getImagesInText(String text) {
		List imgIds = new ArrayList();
		Matcher matcher = imgRegex.matcher(text);
		while (matcher.find()) {
			imgIds.add(matcher.group(2));
		}
		return imgIds;
	}
	
	public String replaceControlCharacters(String original) {
	    if (original != null) {
	        Matcher matcher = controlCharsRegex.matcher(original);
	        StringBuffer buffer = new StringBuffer();
	        while (matcher.find()) {
	            String currentMatch = matcher.group();
	            if (currentMatch.matches("\\s")) {
	                // If it's whitespace, keep it
	                matcher.appendReplacement(buffer, currentMatch);
	            } else {
	                // Otherwise strip it out
	                matcher.appendReplacement(buffer, "");
	            }
	        }
	        matcher.appendTail(buffer);
	        return buffer.toString();
	    } else {
	        return null;
	    }
	}
}