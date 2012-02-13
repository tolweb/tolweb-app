package org.tolweb.tapestry;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.GlossaryEntry;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.misc.GlossaryLookup;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.GlossaryInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class GlossaryPage extends BasePage implements IExternalPage, CookieInjectable, GlossaryInjectable {	
    public abstract boolean getUseGlossary();
    public abstract void setUseGlossary(boolean value);
    
	@Persist("session")
	public abstract GlossaryLookup getLookup();
	public abstract void setLookup(GlossaryLookup gl);
	
	public abstract GlossaryEntry getCurrentGlossaryEntry();
	
	@Persist("session")
	public abstract List<String> getJavaScriptVariables();
	public abstract void setJavaScriptVariables(List<String> vars);
	
	@Persist("session")
	public abstract List<GlossaryEntry> getCurrentGlossaryEntries();
	public abstract void setCurrentGlossaryEntries(List<GlossaryEntry> entries);
	
	@SuppressWarnings("unchecked")
	@Override
	public void activateExternalPage(Object[] arg0, IRequestCycle cycle) {
		setCurrentGlossaryEntries((List<GlossaryEntry>)getGlossaryDAO().getGlossaryEntriesInOrder());
		
		if (getLookup() == null) {
			GlossaryLookup lookup = getGlossaryLookup();
			lookup.getGlossWordsRegex();
			setLookup(lookup);
		}
		
		if (getJavaScriptVariables() == null) {
			setJavaScriptVariables(new LinkedList<String>());
		}
		boolean useGloss = getIsGlossaryOn();
		setUseGlossary(getIsGlossaryOn());
	}

	public String getJavaScriptVars() {
		StringBuilder vars = new StringBuilder();
		
		for(Iterator<String> itr = getJavaScriptVariables().iterator(); itr.hasNext(); ) {
			vars.append(itr.next());
			vars.append('\n');
		}
		return vars.toString();
	}
	
    public boolean getIsGlossaryOn() {
        return getCookieAndContributorSource().getCookieIsEnabled(CookieAndContributorSource.GLOSSARY_COOKIE);
    }	
	
	public boolean getCurrentGlossaryEntryHasMore() {
		return !getCurrentGlossaryEntry().getSynonyms().isEmpty() ||
			!getCurrentGlossaryEntry().getPlurals().isEmpty();
	}
	
	public String getCurrentGlossaryEntryDefinition() {
/*
	    TextPreparer preparer = getTextPreparer();
	    boolean useCache = cacheKey > 0 ? getUseCache() : false;
	    List result = preparer.prepareText(text, cacheKey, getTreehouse(), useCache, getUseGlossary());
	    String resultText = (String) result.get(0);
	    if (getUseGlossary()) {
	        String javascript = (String) result.get(1);
	        setJavascriptVars(javascript);
	    }
		return resultText;	 		
 */
 		if (getUseGlossary()) {
			List returnList = getLookup().replaceGlossaryWords(getCurrentGlossaryEntry().getDefinition());
			if (returnList != null) {
				// grab the replaced text
				String result = (String)returnList.get(0);
				// stash the variables need in the js var list
				getJavaScriptVariables().add((String)returnList.get(1));
				return result;
			}
 		}
		return getCurrentGlossaryEntry().getDefinition(); 
	}
	
	@SuppressWarnings("unchecked")
	public String getCurrentGlossaryEntryPluralsSynonyms() {
		StringBuilder moreInfo = new StringBuilder("(");
		Collection curr = getCurrentGlossaryEntry().getSynonyms();
		// ghetto way to handle the fencepost problem, but it works
		for (Iterator itr = curr.iterator(); itr.hasNext(); ) {
			moreInfo.append(itr.next().toString());
			// only append a comma if they are more elements
			if (itr.hasNext()) {
				moreInfo.append(", ");
			}
		}
		curr = getCurrentGlossaryEntry().getPlurals();
		if (!curr.isEmpty() && !(moreInfo.charAt(moreInfo.length()-1) == '(')) {
			moreInfo.append(", ");
		}
		for (Iterator itr = curr.iterator(); itr.hasNext(); ) {
			moreInfo.append(itr.next().toString());
			// only append a comma if they are more elements
			if (itr.hasNext()) {
				moreInfo.append(", ");
			}			
		}
		moreInfo.append(")");
		return moreInfo.toString();
	}
	
	public void showGlossaryOn(IRequestCycle cycle) {
		// set the cookie value for Show_glossary to be on... 
		String value = cycle.getParameter(CookieAndContributorSource.GLOSSARY_COOKIE);
		if (StringUtils.notEmpty(value)) {
			getCookieAndContributorSource().writeCookieValue(CookieAndContributorSource.GLOSSARY_COOKIE, value);
		}
	}
}
