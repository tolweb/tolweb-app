package org.tolweb.content.export;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractElementFinder {
	protected String cleanseInput(String input) {
		return input.replaceAll("\n", "")
					.replaceAll("\t", "")
					.replaceAll("\r", "");
	}
	
	protected Matcher createMatcherFor(String regex, String input) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		input = (input != null) ? cleanseInput(input) : "";
		return pattern.matcher(input);		
	}
}
