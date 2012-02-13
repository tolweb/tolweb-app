package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hivemind.CookieAndContributorSource;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.treegrow.main.StringUtils;

public abstract class SetPreferencesPage extends BasePage implements IExternalPage, BaseInjectable, 
		CookieInjectable {
/*
 * value="ognl:showGlossaryEnabled"
 * value="ognl:showRandomImagesEnabled"
 * value="ognl:showLosLinksEnabled"
 * value="ognl:showTaxonListsEnabled"
 * 
 * 	public static final String GLOSSARY_COOKIE = "Show_glossary";
 *	public static final String INET_INFO_COOKIE = "Move_internet_info";
 *	public static final String TAXON_LIST_COOKIE = "Show_taxon_list";
 *	public static final String RANDOM_IMAGES_COOKIE = "Show_random_images";
 */
	public abstract String getRedirectURL();
	public abstract void setRedirectURL(String url);
	public abstract boolean getShowGlossaryEnabled();
	public abstract void setShowGlossaryEnabled(boolean enabled);
	public abstract boolean getShowRandomImagesEnabled();
	public abstract void setShowRandomImagesEnabled(boolean enabled);
	public abstract boolean getShowLosLinksEnabled();
	public abstract void setShowLosLinksEnabled(boolean enabled);
	public abstract boolean getShowTaxonListsEnabled();
	public abstract void setShowTaxonListsEnabled(boolean enabled);

	@Override
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		// when entering the page, show the user's current settings...
		setShowGlossaryEnabled(getCookieAndContributorSource()
				.getCookieIsEnabled(CookieAndContributorSource.GLOSSARY_COOKIE));
		setShowRandomImagesEnabled(getCookieAndContributorSource()
				.getCookieIsEnabled(
						CookieAndContributorSource.RANDOM_IMAGES_COOKIE));
		setShowLosLinksEnabled(getCookieAndContributorSource()
				.getCookieIsEnabled(CookieAndContributorSource.INET_INFO_COOKIE));
		setShowTaxonListsEnabled(getCookieAndContributorSource()
				.getCookieIsEnabled(
						CookieAndContributorSource.TAXON_LIST_COOKIE));
		buildRedirectURL();
	}

	private void buildRedirectURL() {
		String host = getConfiguration().getHostPrefix();
		String url = "http://" + host + "/tree/home.pages/mytol.html";
		setRedirectURL(url);
	}
	private void redirect() {
		if (StringUtils.isEmpty(getRedirectURL()))
		{
			buildRedirectURL();
		}
		throw new RedirectException(getRedirectURL());
	}
	
	public void setPreferences(IRequestCycle cycle) {
		handleGlossaryPreference();
		handleRandomImagePreference();
		handleInetLinksPreference();
		handleTaxonListPreference();
		// ensure that we're back at the URL 
		redirect();
	}

	public void resetDefaults(IRequestCycle cycle) {
		setShowGlossaryEnabled(false);
		setShowRandomImagesEnabled(false);
		setShowLosLinksEnabled(false);
		setShowTaxonListsEnabled(false);
		setPreferences(cycle);
		// ensure that we're back at the URL 
		redirect();		
	}
	
	private void handleTaxonListPreference() {
		if(getShowTaxonListsEnabled()) {
			getCookieAndContributorSource().addTaxonListCookie();
		} else {
			getCookieAndContributorSource().removeTaxonListCookie();
		}
	}
	
	private void handleInetLinksPreference() {
		if(getShowLosLinksEnabled()) {
			getCookieAndContributorSource().addInetInfoCookie();
		} else {
			getCookieAndContributorSource().removeInetInfoCookie();
		}
	}
	
	private void handleRandomImagePreference() {
		if(getShowRandomImagesEnabled()) {
			getCookieAndContributorSource().addRandomImagesCookie();
		} else {
			getCookieAndContributorSource().removeRandomImagesCookie();
		}
	}
	
	private void handleGlossaryPreference() {
		if(getShowGlossaryEnabled()) {
			getCookieAndContributorSource().addGlossaryCookie();
		} else {
			getCookieAndContributorSource().removeGlossaryCookie();
		}
	}
}