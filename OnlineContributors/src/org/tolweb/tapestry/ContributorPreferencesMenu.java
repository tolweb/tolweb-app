package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.tolweb.tapestry.injections.CookieInjectable;

public abstract class ContributorPreferencesMenu extends BaseComponent implements CookieInjectable {
	public static final String EDIT_LINK_COOKIE_NAME = "Hide_editlinks";
	public static final String PEOPLE_LISTS_COOKIE_NAME = "Show_peoplelists";
	public static final String PREVIEW_MODE_COOKIE_NAME = "Preview_mode";
	public String getEditString() {
		if (getCookieAndContributorSource().getCookieIsEnabled(EDIT_LINK_COOKIE_NAME)) {
			return "Show Edit Links";
		} else {
			return "Hide Edit Links";
		}
	}
	public String getPeopleListString() {
		if (!getCookieAndContributorSource().getCookieIsEnabled(PEOPLE_LISTS_COOKIE_NAME)) {
			return "Show People Lists";
		} else {
			return "Hide People Lists";
		}
	}
	public String getPreviewString() {
		if (!getInEditMode()) {
			return "Turn On Edit Mode";
		} else {
			return "Turn On Preview Mode";
		}
	}
	public boolean getInEditMode() {
		return !getCookieAndContributorSource().getCookieIsEnabled(PREVIEW_MODE_COOKIE_NAME);
	}
}
