/*
 * StringUtils.java
 *
 * Created on April 5, 2004, 11:26 AM
 */

package org.tolweb.treegrow.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

/**
 *
 * @author  dmandel
 */
public class StringUtils {
	private static final int BTOL_NUM_ZEROS = 4;
	
    public static boolean notEmpty(String str) {
        return str != null && !str.trim().equals("");
    } 
    
    public static boolean isEmpty(String str) {
    	return !notEmpty(str);
    }
    
    public static String returnCommaJoinedString(Collection strings) {
		return returnCommaJoinedString(strings, false); 
    }
    
    public static String returnCommaJoinedStringNoSpaces(Collection strings) {
    	return returnJoinedString(strings, false, ",", null, false);
    }
    
    public static String returnHtmlBreakJoinedString(Collection strings) {
    	return returnJoinedString(strings, false, "<br/>", null, true);
    }
    
    public static String returnNewLineJoinedString(Collection strings) {
    	return returnJoinedString(strings, false, "\n", null, true);    	
    }
    
    public static String escapeForJavascript(String value) {
        // Escape quotation marks since this will end up in a javascript string var
        value = value.replaceAll("\\\"", "\\\\\"");
        // Same goes for apostrophes
        value = value.replaceAll("\\\'", "\\\\\\'");
        // Newlines
        value = value.replace('\n', ' ');
        // and formfeeds
        value = value.replace('\r', ' ');
        return value;
    }
    
	
	/**
	 *  Returns 1 string that has the individual strings joined by commas except for the last element, which is joined by 'and'
	 * @param strings The strings to join
	 * @return The 1 string containing the named joined by commas and 'and'
	 */
	public static String returnCommaAndJoinedString(Collection strings) {
		return returnCommaJoinedString(strings, true);
	}
	
	public static String returnJoinedString(Collection strings, String joinString) {
	    return returnJoinedString(strings, false, joinString);
	}
	
	private static String returnCommaJoinedString(Collection strings, boolean addAnd) {
	    return returnJoinedString(strings, addAnd, ",");
	}
    public static String returnSqlCollectionString(Collection strings) {
        return returnSqlCollectionString(strings, false);
    }
    public static String returnSqlCollectionString(Collection strings, boolean includeQuotes) {
    	Character quoteChar = includeQuotes ? Character.valueOf('\'') : null;
    	String commaSeparated = returnJoinedString(strings, false, ",", quoteChar, true);
    	return "in (" + commaSeparated + ")";
    }
    public static String buildAncestorSqlString(Collection descendantIds, Collection ancestorIds) {
    	if (descendantIds != null && descendantIds.size() > 0 &&
    			ancestorIds != null && ancestorIds.size() > 0) {
	    	StringBuffer sqlStringBuffer = new StringBuffer();
	    	for (Iterator iter = descendantIds.iterator(); iter.hasNext();) {
				Long nodeId = (Long) iter.next();
				for (Iterator iterator = ancestorIds.iterator(); iterator.hasNext();) {
					Long ancestorId = (Long) iterator.next();
					sqlStringBuffer.append("(" + nodeId + "," + ancestorId + ")");
					if (iter.hasNext() || iterator.hasNext()) {
						sqlStringBuffer.append(",");
					}
				}
			}
	    	return sqlStringBuffer.toString();
    	} else {
    		return "";
    	}
    }	
    private static String returnJoinedString(Collection strings, boolean addAnd, String joinString) {
    	return returnJoinedString(strings, addAnd, joinString, null, true);
    }
	private static String returnJoinedString(Collection strings, boolean addAnd, String joinString, Character quoteChar, boolean addSpaces) {
		StringBuffer returnStringBuffer = new StringBuffer("");
		Iterator it = strings.iterator();
		int numSeen = 0;
		while (it.hasNext()) {
			String next = it.next().toString();
			if (numSeen > 0) {
			    if (it.hasNext()) {
			        numSeen++;
			        returnStringBuffer.append(joinString);
			        if (addSpaces) {
			        	returnStringBuffer.append(' ');
			        }
				} else {
					if (numSeen > 1 || !addAnd) {
						returnStringBuffer.append(joinString);
						if (addSpaces) {
							returnStringBuffer.append(' ');
						}
					}
					if (addAnd) {
						returnStringBuffer.append(" and ");
					}
				}
			} else {
			    numSeen++;
			}
			if (quoteChar != null) {
				returnStringBuffer.append(quoteChar);
			}
			returnStringBuffer.append(next); 	
			if (quoteChar != null) {
				returnStringBuffer.append(quoteChar);
			}			
		}
		return returnStringBuffer.toString();	    
	}
	
	public static String cleanStringForFilename(String originalString) {
	    String string = removeSpaces(originalString);
	    string = removeParens(string);
	    return string;
	}
	
	public static String removeSpaces(String originalString) {
	    return removeChar(originalString, ' ');
	}
	
	public static String removeParens(String originalString) {
	    String string = removeChar(originalString, '(');
	    string = removeChar(string, ')');
	    return string;
	}
	
	public static String capitalizeString(String toCapitalize) {
	    if (notEmpty(toCapitalize)) {
	    	StringBuffer returnBuffer = new StringBuffer();
	        char firstChar = toCapitalize.charAt(0);
	        returnBuffer.append(("" + firstChar).toUpperCase());
	        for (int i = 1; i < toCapitalize.length(); i++) {
	        	returnBuffer.append(("" + toCapitalize.charAt(i)).toLowerCase());
	        }
	        return returnBuffer.toString();
	    } else {
	        return "";
	    }
	}
	
	public static String getStringOrEmptyString(String originalString) {
	    if (originalString != null) {
	        return originalString;
	    } else {
	        return "";
	    }
	}
	
	private static String removeChar(String originalString, char badChar) {
	    String returnString = "";
	    char[] array = originalString.toCharArray();
	    for (int i = 0; i < array.length; i++) {
            char currentChar = array[i];
            if (currentChar != ' ') {
                returnString += currentChar;
            }
        }
	    return returnString;	    
	}
    
    public static boolean getIsNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static String getGMTDateString(Date date) {
        SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance();
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        formatter.setTimeZone(gmt);
        formatter.applyPattern("dd MMMM");
        String dayString = formatter.format(date);
        formatter = (SimpleDateFormat) DateFormat.getTimeInstance();
        formatter.setTimeZone(gmt);
        formatter.applyPattern("hh:mm zzz yyyy");
        String timeString = formatter.format(date);
        return dayString + " at " + timeString;
    }
    public static String getMySqlDateString(Date date) {
    	SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance();
    	formatter.applyPattern("yyyy-MM-dd");
    	return formatter.format(date);
    }
    public static Calendar parseMySqlDateString(String dateString) {
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(5, 7)) - 1;
        int day = Integer.parseInt(dateString.substring(8, 10));
        int hours = Integer.parseInt(dateString.substring(11, 13));
        int minutes = Integer.parseInt(dateString.substring(14, 16));
        GregorianCalendar calendar = new GregorianCalendar(year, month, day, hours, minutes);
        return calendar;
    }
	public static String getTimeStringFromSeconds(int totalSeconds) {
		int minutes = totalSeconds / 60;
		int seconds = totalSeconds % 60;
		String minutesString = "" + minutes;
		String secondsString = "" + seconds;
		if (seconds < 10) {
			secondsString = "0" + secondsString;
		}
		return minutesString + ":" + secondsString;		
	}
	public static String getProperHttpUrl(String url) {
		String prefix = "http://";
		if (StringUtils.notEmpty(url) && !url.startsWith(prefix)) {
			url = prefix + url;
		}
		return url;
	}
	public static String getLinkMarkup(String url) {
		url = getProperHttpUrl(url);
		return getLinkMarkup(url, url);
	}
	public static String getLinkMarkup(String url, String linkText) {
		url = getProperHttpUrl(url);
		if (StringUtils.isEmpty(linkText)) {
			linkText = url;
		}
		return "<a href=\"" + url + "\">" + linkText + "</a>";
	}
	public static String zeroFillString(String originalString, int numZeros) {
		if (originalString.length() < numZeros) {
			int zerosToAdd = numZeros - originalString.length();
			for (int i = 0; i < zerosToAdd; i++) {
				originalString = "0" + originalString;
			}
		}	
		return originalString;
	}
	public static String zeroFillString(String originalString) {
		return zeroFillString(originalString, BTOL_NUM_ZEROS);
	}
}
