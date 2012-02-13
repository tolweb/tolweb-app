package org.tolweb.content.export;

import java.util.regex.Matcher;

public class ResponseFinder extends AbstractElementFinder {
	public static final String RESPONSE_REGEX = "(<\\?xml[^\\?>]*\\?>)(.*)(<response[^>]*>)(.*)";
	public static final String CLOSING_TAG = "</response>";
	
	private String processingText;
	private String responseText;
	
	public ResponseFinder(String input) {
		processingText = "";
		responseText = "";
		Matcher m = createMatcherFor(RESPONSE_REGEX, input);
		if (m.find()) {
			processingText = m.group(1);
			responseText = m.group(3);
		}
	}
	
	public String getProcessingText() {
		return processingText;
	}
	
	public String getResponseText() {
		return responseText;
	}
	
	public String getDocumentHeading() {
		return processingText + responseText;
	}
}
