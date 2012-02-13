package org.tolweb.btol.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hivemind.util.PropertyUtils;
import org.tolweb.btol.FlexibleDateObject;
import org.tolweb.misc.PeekStringTokenizer;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.tree.TabDelimitedNameParser;

public abstract class BaseMassParser {
	private String stringToParse;
	protected PeekStringTokenizer newlineTokenizer;
	protected boolean validateData = true;	

	public String getStringToParse() {
		return stringToParse;
	}

	public void setStringToParse(String stringToParse) {
		this.stringToParse = stringToParse;
		newlineTokenizer = new PeekStringTokenizer(stringToParse, TabDelimitedNameParser.LINE_SEPARATORS);		
	}

	/**
	 * parse a date of the form 1/1/2006 into three ints 
	 * and returns an array of {month, day, year}
	 * @param dateString
	 * @return an array of {month, day, year} or null if the date is bad
	 */
	protected int[] parseDateString(String dateString) {
		if (StringUtils.notEmpty(dateString)) {
			Pattern pattern = Pattern.compile("(\\d+)/(\\d+)/(\\d+)");
			Matcher matcher = pattern.matcher(dateString);
			if (matcher.matches()) {
				int month = Integer.parseInt(matcher.group(1));
				int day = Integer.parseInt(matcher.group(2));
				int year = Integer.parseInt(matcher.group(3));
				return new int[] {month, day, year};
			}
		}
		return null;
	}
	protected String getTabRegexString() {
		return "\\t";
	}
	protected String getNotTabRegexString() {
		return "([^\\t]*)";
	}
	protected String getMaybeTabRegexString() {
		return getTabRegexString() + "?";
	}
	protected MassParseException setErrorMessage(String message) throws MassParseException {
		throw new MassParseException(message);
	}
	protected void parseAndSetDates(FlexibleDateObject object, String dateString) {
		int[] dates = parseDateString(dateString);
		if (dates != null) {
			setDates(object, dates);
		}
	}
	protected void setDates(FlexibleDateObject object, int[] dates) {
		if (object != null && dates != null) {
			object.setCreationMonth(dates[0]);
			object.setCreationDay(dates[1]);
			int year = dates[2];
			if (year < 2000) {
				// e.g. 01/01/06 -- yes this code will be broken in the year 2100
				// but I'll be dead!
				year += 2000;
			}
			object.setCreationYear(year);
		}
	}
	protected String getTabSeparatedRegexString(int numRequiredTabs, int numOptionalTabs) {
		StringBuilder regexString = new StringBuilder();
		regexString.append("");
		for (int i = 0; i < numRequiredTabs; i++) {
			regexString.append(getNotTabRegexString() + getTabRegexString());
		}
		for (int i = 0; i < numOptionalTabs; i++) {
			regexString.append(getNotTabRegexString() + getMaybeTabRegexString());
		}
		return regexString.toString();
	}
	protected void setRequiredField(Matcher matcher, Object object, int group, String fieldNameForError, 
			String propertyName, String line) throws MassParseException {
		String value = matcher.group(group);
		if (StringUtils.isEmpty(value)) {
			setRequiredFieldErrorMessage(fieldNameForError, line);
		} else {
			try {
				PropertyUtils.write(object, propertyName, value);
			} catch (Exception e) {
				System.err.println("Bad property name for MassParser object is: " + propertyName + " value is: " + value);
			}			
		}
	}
	
	protected void setRequiredFieldErrorMessage(String fieldName, String offendingLine) throws MassParseException {
		setErrorMessage(fieldName + " is a required field.\n\n  Offending line is: " + offendingLine);
	}	
}
