/*
 * Created on Oct 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.misc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tolweb.dao.GlossaryDAO;
import org.tolweb.hibernate.GlossaryEntry;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlossaryLookup {
	private GlossaryDAO glossaryDao;
	private Hashtable wordsToDefs;
	private Hashtable wordsToActualWord;
	private Hashtable wordsToIds;
	private Hashtable wordsToEntries;
	private Pattern glossWordsRegex;
	private Pattern badGlossRegex;
	private Pattern linkRegex;
	
	public GlossaryLookup() {
	}
	
	/**
	 * @return Returns the glossaryDao.
	 */
	public GlossaryDAO getGlossaryDAO() {
		return glossaryDao;
	}
	/**
	 * @param glossaryDao The glossaryDao to set.
	 */
	public void setGlossaryDAO(GlossaryDAO glossaryDao) {
		this.glossaryDao = glossaryDao;
	}
	/**
	 * @return Returns the glossWordsRegex.
	 */
	public Pattern getGlossWordsRegex() {
		if (glossWordsRegex == null) {
			doInit();
		}
		return glossWordsRegex;
	}
	/**
	 * @param glossWordsRegex The glossWordsRegex to set.
	 */
	public void setGlossWordsRegex(Pattern glossWordsRegex) {
		this.glossWordsRegex = glossWordsRegex;
	}
	/**
	 * @return Returns the wordsToDefs.
	 */
	public Hashtable getWordsToDefs() {
		if (wordsToDefs == null) {
			doInit();
		}
		return wordsToDefs;
	}
	/**
	 * @param wordsToDefs The wordsToDefs to set.
	 */
	public void setWordsToDefs(Hashtable wordsToDefs) {
		this.wordsToDefs = wordsToDefs;
	}
	
	public List replaceGlossaryWords(String text) {
	    return replaceGlossaryWords(text, false);
	}
	
	/**
	 * Takes some page text and replaces any glossary entries with a yellow highlight
	 * and a javascript rollover with the definitions
	 * @param text The text to replace
	 * @param isRecursive Whether it's a recursive call
	 * @return A list containing two items: 1) The replaced text 2) The javascript variables 
	 */
	public List replaceGlossaryWords(String text, boolean isRecursive) {
	    Matcher glossWordsMatcher = getGlossWordsRegex().matcher(text);
	    StringBuffer glossDefinitions = new StringBuffer();
	    StringBuffer javascriptVars = new StringBuffer(); 
	    Hashtable currentlyDefined = new Hashtable();

	    while (glossWordsMatcher.find()) {
	        StringBuffer replacement = new StringBuffer();
	        String beginning = glossWordsMatcher.group(1);
	        String word = glossWordsMatcher.group(2);
	        String end = glossWordsMatcher.group(3);
	        String openLink = null;
            //System.out.println("\n\n\n\n beginning: " + beginning + "\nword is: " + word + "\nend is: " + end + "\n\n\n\n");
	        Long id = (Long) wordsToIds.get(word.toLowerCase());

	        if (beginning != null || currentlyDefined.get(id) != null || beginning == null && end != null) {
	            // Here we've already seen this word so don't bother looking at it again
	            // or, the word appeared inside of an href, so don't do anything
	            continue;
	        }
            replacement.append("<a ");
            if (beginning != null) {
                Matcher linkMatcher = linkRegex.matcher(beginning);
                if (linkMatcher.find()) {
	                //replacement.append(beginning);
	                replacement.append("class=\"GLOSSENTRY\" href=\"/tree/home.pages/glossary.html#" + wordsToActualWord.get(word.toLowerCase()));
	                replacement.append("\"");
	                openLink = linkMatcher.group();
                }
            } else {
                replacement.append("class=\"GLOSSENTRYNODEC\" href=\"/tree/home.pages/glossary.html#");
                replacement.append((String) wordsToActualWord.get(word.toLowerCase()));
                replacement.append("\"");
            }
            if (!isRecursive) {
                replacement.append(" onmouseover=\"if(doTooltip!=null) doTooltip(event,def_");
    	        replacement.append(wordsToIds.get(word.toLowerCase()));
    	        replacement.append(")\" onmouseout=\"if(hideTip!=null) hideTip()\">");    	        
            } else {
	            replacement.append(" onclick=\"javascript:Tooltip.hide();\">");
	        }
	        replacement.append(word);
	        replacement.append("</a>");
	        if (openLink != null) {
	            replacement.append("<a ");
	            replacement.append(openLink);
	            replacement.append(">");
	            replacement.append(end);
	        }
	        String pluralsString = "", synonymsString = "";
	        GlossaryEntry entry = (GlossaryEntry) wordsToEntries.get(word.toLowerCase());

	        if (entry.getPlurals() != null && entry.getPlurals().size() > 0) {
	            pluralsString = StringUtils.returnCommaJoinedString(entry.getPlurals());
	            pluralsString = "<br>(<i>pl.</i> " + pluralsString + ")";
	        }
	        if (entry.getSynonyms() != null && entry.getSynonyms().size() > 0) {
	            synonymsString = StringUtils.returnCommaJoinedString(entry.getSynonyms());
	            synonymsString = "<br>(<i>also:</i>" + synonymsString + ")";
	        }
	        glossWordsMatcher.appendReplacement(glossDefinitions, replacement.toString());
	        currentlyDefined.put(id, replacement.toString());
	        if (!isRecursive) {
	            String replacedDefinitions = (String) replaceGlossaryWords((String) wordsToDefs.get(word.toLowerCase()), true).get(0);
	            replacedDefinitions = "<b><a href=\"/tree/home.pages/glossary.html#" + 
	            	wordsToActualWord.get(word.toLowerCase()) + "\" onclick=\"javascript:Tooltip.hide();\">" + 
	            	wordsToActualWord.get(word.toLowerCase()) + "</a></b>" + pluralsString + synonymsString + 
	            	"<p>" + replacedDefinitions;
	            // Escape quotation marks since this will end up in a javascript string var
	            replacedDefinitions = StringUtils.escapeForJavascript(replacedDefinitions);
	            // I believe the below line is causing javascript variables to have double-escaped text.
	            //replacedDefinitions = replacedDefinitions.replaceAll("\\\"", "\\\\\"");
	            // Same goes for apostrophes
	            replacedDefinitions = replacedDefinitions.replaceAll("'", "\\'");
	            // Newlines
	            replacedDefinitions = replacedDefinitions.replace('\n', ' ');
	            // and formfeeds
	            replacedDefinitions = replacedDefinitions.replace('\r', ' ');	            
	            javascriptVars.append("var def_");
	            javascriptVars.append(wordsToIds.get(word.toLowerCase()));
	            javascriptVars.append(" = '");
	            javascriptVars.append(replacedDefinitions);
	            javascriptVars.append("';\n");
	        }
	        
	    }
	    glossWordsMatcher.appendTail(glossDefinitions);
	    Matcher badGlossMatcher = badGlossRegex.matcher(glossDefinitions);
	    StringBuffer newGlossDefinitions = new StringBuffer();
	    // Strip out any glossary definitions that fell inside of tags
	    while (badGlossMatcher.find()) {
	        badGlossMatcher.appendReplacement(newGlossDefinitions, "$1$2$3");
	    }
	    badGlossMatcher.appendTail(newGlossDefinitions);
	    // Now check the javascript vars, too
	    badGlossMatcher = badGlossRegex.matcher(javascriptVars);
	    StringBuffer newJavascriptVars = new StringBuffer();
	    while (badGlossMatcher.find()) {
	        badGlossMatcher.appendReplacement(newJavascriptVars, "$1$2$3");
	    }
	    badGlossMatcher.appendTail(newJavascriptVars);
	    ArrayList returnList = new ArrayList();
	    returnList.add(newGlossDefinitions.toString());
	    returnList.add(newJavascriptVars.toString());
	    return returnList;
	}
	
	private synchronized void doInit() {
		wordsToDefs = new Hashtable();
		wordsToActualWord = new Hashtable();
		wordsToIds = new Hashtable();
		wordsToEntries = new Hashtable();
		List glossEntries = getGlossaryDAO().getGlossaryEntries();
		Iterator it = glossEntries.iterator();
		StringBuffer wordsRegex = new StringBuffer();
		while (it.hasNext()) {
			GlossaryEntry entry = (GlossaryEntry) it.next();
			String currentDef = entry.getDefinition();
			String word = entry.getWord();
			Long id = entry.getGlossaryId();
			wordsRegex.append(word);
			wordsRegex.append("|");
			String lowercaseWord = word.toLowerCase();
			wordsToDefs.put(lowercaseWord, currentDef);
			wordsToActualWord.put(lowercaseWord, word);
			wordsToIds.put(lowercaseWord, id);
			wordsToEntries.put(lowercaseWord, entry);
			Iterator it2 = entry.getPlurals().iterator();
			while (it2.hasNext()) {
				String nextPlural = (String) it2.next();
				String lowercasePlural = nextPlural.toLowerCase();
				wordsRegex.append(nextPlural);
				wordsRegex.append("|");
				wordsToDefs.put(lowercasePlural, currentDef);
				wordsToActualWord.put(lowercasePlural, word);
				wordsToIds.put(lowercasePlural, id);
				wordsToEntries.put(lowercasePlural, entry);
			}
			it2 = entry.getSynonyms().iterator();
			while (it2.hasNext()) {
				String nextSyn = (String) it2.next();
				String lowercaseSyn = nextSyn.toLowerCase();
				wordsRegex.append(nextSyn);
				wordsRegex.append("|");
				wordsToDefs.put(lowercaseSyn, currentDef);
				wordsToActualWord.put(lowercaseSyn, word);
				wordsToIds.put(lowercaseSyn, id);
				wordsToEntries.put(lowercaseSyn, entry);
			}
		}
		// Remove the last pipe
		wordsRegex.deleteCharAt(wordsRegex.length() - 1);
		glossWordsRegex = Pattern.compile("(<a[^>]+>[^<]*)?(?<=\\W)(" + wordsRegex.toString() + ")(?=\\W)([^<]*<\\/a>)?", Pattern.CASE_INSENSITIVE);
		badGlossRegex = Pattern.compile("(<[^>]*)<a\\s+class=\"GLOSSENTRY[^>]+>([^<]*)<\\/a>([^<]*<)", Pattern.CASE_INSENSITIVE);
		linkRegex = Pattern.compile("(href=[^\\s>]*)");
	}
}
