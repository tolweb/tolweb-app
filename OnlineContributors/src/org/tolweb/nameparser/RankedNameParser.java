package org.tolweb.nameparser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.TabDelimitedNameParser;

public class RankedNameParser {
	@SuppressWarnings("unchecked")
	public static List parseNamesString(String namesString, String lineSeparator, String authorityRegex, String replacementRegex) {
		// the idea here is to transform these ranks into a tab-separated string
		namesString = namesString.replaceAll("Superfamily\\s+", "");
		namesString = namesString.replaceAll("Family\\s+", "\t");
		namesString = namesString.replaceAll("Subfamily\\s+", "\t\t");
		namesString = namesString.replaceAll("(\\w+\\s*lineage)", "\t\t\t$1");
		namesString = namesString.replaceAll("Tribe\\s+", "\t\t\t\t");
		namesString = namesString.replaceAll("(\\w+\\s*branch)", "\t\t\t\t\t$1");		
		namesString = namesString.replaceAll("Subtribe\\s+", "\t\t\t\t\t\t");
		namesString = namesString.replaceAll("(\\w+\\s*generic group)", "\t\t\t\t\t\t\t$1");
		namesString = namesString.replaceAll("Genus\\s+", "\t\t\t\t\t\t\t\t");
		// strip out blank lines
		Pattern pattern = Pattern.compile("^\\s*$", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(namesString);
		namesString = matcher.replaceAll("");
		// decapitalize all capitalized names
		pattern = Pattern.compile("\\p{Upper}{2,}");
		matcher = pattern.matcher(namesString);
		StringBuffer replacementBuffer = new StringBuffer();
		while (matcher.find()) {
			String name = matcher.group();
			matcher.appendReplacement(replacementBuffer, StringUtils.capitalizeString(name));
		}
		matcher.appendTail(replacementBuffer);
		namesString = replacementBuffer.toString();
		pattern = Pattern.compile(authorityRegex, Pattern.MULTILINE);
		matcher = pattern.matcher(namesString);
		replacementBuffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(replacementBuffer, replacementRegex);
		}
		matcher.appendTail(replacementBuffer);
		namesString = replacementBuffer.toString();
		//namesString = namesString.replaceAll(authorityRegex, replacementRegex);
		return TabDelimitedNameParser.parseNamesString(namesString, lineSeparator);
	}
}
