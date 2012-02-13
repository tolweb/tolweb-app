package org.tolweb.content.export;

import java.util.regex.Matcher;

public class ContentFinder extends AbstractElementFinder {
	public static final String CONTENT_REGEX = "(.*)(<(dataObject|reference|dc:description)[^>]*>(.*))";

	private boolean contentPresent;
	
	public ContentFinder(String input) {
		contentPresent = false;
		Matcher m = createMatcherFor(CONTENT_REGEX, input);
		if (m.find()) {
			contentPresent = true;
		}
	}

	public boolean getHasContent() {
		return contentPresent;
	}
}
