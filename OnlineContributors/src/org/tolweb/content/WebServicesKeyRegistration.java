package org.tolweb.content;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.valid.IFieldTracking;
import org.apache.tapestry.valid.ValidationConstraint;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.content.licensing.ContentLicenseClass;
import org.tolweb.hibernate.WebServicesKey;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.WebServicesInjectable;
import org.tolweb.treegrow.main.Contributor;

/**
 * Enables users to register for Web Services user 
 * keys, which are required for access to the Tree of 
 * Life Web Services.  
 * 
 * The purpose of the User Key is to enable the collection 
 * of metrics along with determining the intent of the 
 * users harvesting Tree of Life content.  
 * 
 * One addition benefit is the ability to block users.  
 * The logic for blocking identities and individuals is 
 * not fully in place (as of 8/14/2008), but requiring a 
 * user key will mean that blocking and filtering *could* 
 * happen.  It is much better to require the key now before 
 * the services are rolled out than adding it later.  
 * 
 * @author lenards
 *
 */
public abstract class WebServicesKeyRegistration extends BasePage 
	implements IExternalPage, WebServicesInjectable, CookieInjectable {

	/**
	 * Gets the title for the page. 
	 * @return a string representing the title
	 */
	public String getTitle() {
		return "ToL Web Services: User Key";
	}

	/**
	 * Gets the injected validation delegate. 
	 * 
	 * Responsible for validation services that 
	 * are provided to the page. 
	 * @return an injected instance of the ValidationDelegate
	 */
	@Bean
	public abstract ValidationDelegate getDelegate();

	/**
	 * Gets the current field tracking instance.
	 * 
	 * A field tracking instance is really a field within the 
	 * form/page that is being "watched" by the validation 
	 * delegate.  This field tracking instance is used to 
	 * provider user feedback regarding validation errors. 
	 * 
	 * @return an instance of IFieldTracking representing a 
	 * field of data being provided by user input.
	 */
	public abstract IFieldTracking getCurrentFieldTracking();
	public abstract void setCurrentFieldTracking(IFieldTracking ift);
	
	/**
	 * Gets the injected results page. 
	 * 
	 * This is the page that will be activated following the 
	 * collection of valid user data. 
	 * @return
	 */
	@InjectPage("content/WebServicesKeyResults")
	public abstract WebServicesKeyResults getResultsPage();
	
	/**
	 * Activates the page upon external request and fills in 
	 * data based on their session or cookie information. 
	 * 
	 * This page may be request by unauthenticated users, so 
	 * the external page interface needs to be support.  
	 */
	public void activateExternalPage(java.lang.Object[] parameters, IRequestCycle cycle) {
		autoFillFields();
	}

	/**
	 * Fills in user name, user email, user URL, and license 
	 * defaults based on the session or cookie information 
	 * available.  
	 */
	private void autoFillFields() {
		Contributor loggedInUser = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		if (loggedInUser != null) {
			autoFillUserName(loggedInUser);
			autoFillUserEmail(loggedInUser);
			autoFillUserUrl(loggedInUser);
			autoFillLicenseCategory(loggedInUser);
		}
	}

	/**
	 * Fills in the user name based on the session or cookie 
	 * information available.
	 * @param loggedInUser the contributor associated with 
	 * the session or available via cookie
	 */
	private void autoFillUserName(Contributor loggedInUser) {
		if (getUserName() == null) {
			setUserName(loggedInUser.getDisplayName());
		}
	}

	/**
	 * Fills in the user email based on the session or cookie 
	 * information available.
	 * @param loggedInUser the contributor associated with 
	 * the session or available via cookie
	 */	
	private void autoFillUserEmail(Contributor loggedInUser) {
		if (getUserEmail() == null) {
			setUserEmail(loggedInUser.getEmail());
		}
	}

	/**
	 * Fills in the user URL based on the session or cookie 
	 * information available.
	 * @param loggedInUser the contributor associated with 
	 * the session or available via cookie
	 */	
	private void autoFillUserUrl(Contributor loggedInUser) {
		if (getUserUrl() == null) {
			setUserUrl(loggedInUser.getHomepage());
		}
	}

	/**
	 * Fills in the license default based on the session or cookie 
	 * information available.
	 * @param loggedInUser the contributor associated with 
	 * the session or available via cookie
	 */	
	private void autoFillLicenseCategory(Contributor loggedInUser) {
		if (getLicenseCategory() == null) {
			// TODO UGH!  This licensing stuff really needs to be refactored... BAD MOVE ANDY! 
			Byte licCode = determineLicenseDefault(loggedInUser);
			ContributorLicenseInfo licInfo = new ContributorLicenseInfo(licCode);
			setLicenseCategory(licInfo.getContentLicenseClass());
		}
	}
	
	/**
	 * Determines the default license code selected by the 
	 * contributor. 
	 * 
	 * A contributor may have a default associated with their 
	 * text contributions and their media contributions. These 
	 * values may be different. If both are present, the text 
	 * contributions default is used.  If the text contributions 
	 * are missing, the media contributions default is used. 
	 * If neither are available, will be returned. 
	 *  
	 * @param loggedInUser the contributor associated with 
	 * the session or available via cookie
	 * @return a byte representing the users content license 
	 * default if defined, otherwise null. 
	 */
	private Byte determineLicenseDefault(Contributor loggedInUser) {
		return loggedInUser.getNoteUseDefault() != null ? loggedInUser.getNoteUseDefault() : loggedInUser.getImageUseDefault();
	}

	/* Properties making user input available to the page */
	/* -------------------------------------------------- */
	public abstract ContentLicenseClass getLicenseCategory();
	public abstract void setLicenseCategory(ContentLicenseClass licenseClass);
	
	public abstract String getIntendedUse();
	public abstract void setIntendedUse(String usageComment);
	
	public abstract String getUserName();
	public abstract void setUserName(String userName);
	
	public abstract String getUserEmail();
	public abstract void setUserEmail(String userEmail);
	
	public abstract String getUserUrl();
	public abstract void setUserUrl(String userUrl);

	public abstract boolean getCompliesWithTerms();
	public abstract void setCompliesWithTerms(boolean response);
	
	public abstract boolean getTakesResponsibility();
	public abstract void setTakesResponsibility(boolean response);

	/**
	 * Handles the submission of data to generate a Web Services 
	 * user key. 
	 * @param cycle the current request cycle 
	 */
	public void userKeySubmit(IRequestCycle cycle) {
		validateUserInput();
		
		// if we don't have any errors, let's process their request
		if (!getDelegate().getHasErrors()) {
			if (!getWebServicesKeyDAO().getWebServicesKeyExistsWithEmail(getUserEmail())) {
				registerUserKey();
			} else {
				handleDuplicateRegistrationAttempt();
			}
			getResultsPage().setFromRegistration(true);
			cycle.activate(getResultsPage());
		}
	}

	/**
	 * Handle an duplicate attempt at registration by mailing the 
	 * user their original User Key from the initial registration. 
	 */
	private void handleDuplicateRegistrationAttempt() {
		getResultsPage().setDuplicateEntry(true);
		getResultsPage().setWebServicesKey(
				getWebServicesKeyDAO().getWebServicesKeyWithEmail(
						getUserEmail()));
	}

	/**
	 * Create the User Key for the user given the input. 
	 */
	private void registerUserKey() {
		WebServicesKey wsKey = WebServicesKey.createWebServicesKey();
		wsKey.setUserName(getUserName());
		wsKey.setUserEmail(getUserEmail());
		wsKey.setUserUrl(getUserUrl());
		wsKey.setUseCategory(ContentLicenseClass.byteValue(getLicenseCategory()));
		wsKey.setIntendedUse(getIntendedUse());
		getWebServicesKeyDAO().createWebServicesKey(wsKey);
		
		getResultsPage().setWebServicesKey(wsKey);
	}

	private void validateUserInput() {
		// use the validation delegate (indirectly) to ensure  
		// that the checkboxes have been clicked by the user. 
		if (!getTakesResponsibility()) {
            recordFieldInvalidity("takesResponsibility", 
            		Boolean.valueOf(getTakesResponsibility()).toString(), 
            		"Must take responsibility for usage");
		}
		if (!getCompliesWithTerms()) {
            recordFieldInvalidity("compliesWithTerms", 
            		Boolean.valueOf(getCompliesWithTerms()).toString(), 
            		"Must comply with terms");			
		}
	}
	
	/**
	 * Records the necessary information for the validation delegate 
	 * when a field is invalid. 
	 * @param componentName name of the form component to set on the delegate
	 * @param fieldInputValue value of the field input
	 * @param validationMessage validation messaged to display to the user
	 */
	private void recordFieldInvalidity(String componentName, String fieldInputValue, String validationMessage) {
		getDelegate().setFormComponent((IFormComponent) getComponent(componentName));
		getDelegate().recordFieldInputValue(fieldInputValue);
		getDelegate().record(validationMessage, ValidationConstraint.REQUIRED);		
	}
}
