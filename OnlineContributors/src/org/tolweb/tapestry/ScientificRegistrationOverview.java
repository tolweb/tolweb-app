package org.tolweb.tapestry;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ScientificRegistrationOverview extends BasePage implements IExternalPage, UserInjectable, 
		PageInjectable, BaseInjectable, CookieInjectable {
	
    public void activateExternalPage(Object[] params, IRequestCycle cycle) {
    	getCookieAndContributorSource().authenticateExternalPage(cycle);
    }		
	
	public void updateExistingInfo(IRequestCycle cycle) {
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		if (contr == null) {
			IPage loginPage = cycle.getPage("ScientificContributorsLogin");
			PropertyUtils.write(loginPage, "dynamicPageName", true);
			cycle.activate(loginPage);
		} else {
			String pageName = getUrlBuilder().getRegistrationPageNameForContributor(contr);
			AbstractContributorRegistration registrationPage = (AbstractContributorRegistration) cycle.getPage(pageName);
			registrationPage.setEditedContributor(contr);
			cycle.activate(registrationPage);
		}
	}
	
	public void registerAnother(IRequestCycle cycle) {
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		if (contr != null) {
			byte contributorType = contr.getContributorType();			
			// don't want media or treehouse people doing this
			if (contributorType == Contributor.SCIENTIFIC_CONTRIBUTOR ||
					contributorType == Contributor.ACCESSORY_CONTRIBUTOR) { 
				ScientificContributorRegistrationOther editPage = (ScientificContributorRegistrationOther) cycle.getPage("ScientificContributorRegistrationOther");
				// clear persistent properties in case someone else was being edited
				editPage.clearPersistentProperties();
				cycle.activate(editPage);
			}
		} else {
			gotoLoginPage(cycle, "ScientificContributorRegistrationOther", false);
		}
	}
	
	public void updateAnother(IRequestCycle cycle) {
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		if (contr != null) {			
			byte contributorType = contr.getContributorType();			
			// don't want media or treehouse people doing this
			if (contributorType == Contributor.SCIENTIFIC_CONTRIBUTOR ||
					contributorType == Contributor.ACCESSORY_CONTRIBUTOR) {			
				ContributorSearchPage page = (ContributorSearchPage) cycle.getPage("ContributorSearchPage");
				page.doActivate(cycle, AbstractWrappablePage.DEFAULT_WRAPPER, "ScientificContributorRegistrationOther", 
						AbstractContributorSearchOrResultsPage.EDIT_CONTRIBUTOR, null, Contributor.ACCESSORY_CONTRIBUTOR,
						true, true);
			}
		} else {
			gotoLoginPage(cycle, "ContributorSearchPage", true);
		}
	}
	
	private void gotoLoginPage(IRequestCycle cycle, String pageName, boolean addExternalParams) {
		IPage loginPage = cycle.getPage("ScientificContributorsLogin");
		PropertyUtils.write(loginPage, "destinationPageName", pageName);
		if (addExternalParams) {
			// doing this will trigger the destination page to be called externally
			PropertyUtils.write(loginPage, "externalPageParameters", new Object[] {});
		}
		cycle.activate(loginPage);		
	}
}
