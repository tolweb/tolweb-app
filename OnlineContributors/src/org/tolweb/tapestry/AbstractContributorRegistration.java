/*
 * Created on Jun 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.request.IUploadFile;
import org.apache.tapestry.valid.ValidationDelegate;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.EditHistoryDAO;
import org.tolweb.hibernate.EditHistory;
import org.tolweb.misc.ImageUtils;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractContributorRegistration extends BasePage implements PageBeginRenderListener,
		IExternalPage, ImageInjectable, BaseInjectable, UserInjectable {
	public abstract EditHistory getHistory();
	public abstract void setHistory(EditHistory value);

	public abstract void setImageFile(IUploadFile file);
	@Persist("session")
	public abstract IUploadFile getImageFile();
	@Persist("session")
	public abstract Contributor getEditedContributor();
	public abstract void setEditedContributor(Contributor value);
	@Persist("session")
	public abstract Contributor getExistingContributor();
	public abstract void setExistingContributor(Contributor value);
	public abstract void setAgreeToTerms(boolean value);
	public abstract boolean getAgreeToTerms();
	public abstract void setPassword1(String value);
	public abstract String getPassword1();
	public abstract void setPassword2(String value);
	public abstract String getPassword2();
	public abstract void setError(String value);
	public abstract String getError();
	public abstract boolean getReviewDefaultLicenses();
	public abstract void setReviewDefaultLicenses(boolean value);
	@Bean
	public abstract ValidationDelegate getDelegate();
	
	public void editContributor(Contributor contr) {
		clearPersistentProperties();
		setEditedContributor(contr);
	}
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		Integer contributorId = (Integer) parameters[0];
		Contributor contr = getContributorDAO().getContributorWithId(contributorId.toString());
		setEditedContributor(contr);
	}
	
	public void pageBeginRender(PageEvent event) {
		if (getEditedContributor() == null) {
			setEditedContributor(getNewContributor());
		}
		Long editHistoryId = getEditedContributor().getEditHistoryId();
		if (editHistoryId != null && editHistoryId.intValue() > 0) {
			EditHistoryDAO dao = getMiscEditHistoryDAO();
			EditHistory history = dao.getEditHistoryWithId(editHistoryId);
			setHistory(history);
		}
	}
    
	/**
	 * This probably really needs to be refactored at some point.  It's
	 * a bit spaghettiish at the moment
	 * @param cycle
	 */
	public void registrationFormSubmit(IRequestCycle cycle) {
		boolean isExistingContributor = !getIsNewContributor();
		boolean result = checkForRedirectOrErrors(cycle);
		if (!result) {
			return;
		}

		boolean otherContributorWithSameEmail = checkForExistingContributor();
		if (otherContributorWithSameEmail) {
			return;
		}
		checkAndWriteOutImage();
		if (isExistingContributor) {
			boolean passwordsMatch = checkAndSetPassword();
			if (!passwordsMatch) {
				return;
			}
		}
		doAdditionalProcessing();
		saveContributorToDb();
		doPostSaveAdditionalProcessing();
		
		// if you're already a contributor and they need to review license
		// then send them to the new page to set their preferences
		if (isExistingContributor && getReviewDefaultLicenses()) {
			IPage reviewPage = getContributorLicenseReviewDestination();
			cycle.activate(reviewPage);
		} else if (isExistingContributor) { // if they're existing, send them through the old path
			Object destination = getExistingContributorPageDestination();
			if (String.class.isInstance(destination)) {
				cycle.activate((String) destination);
			} else {
				cycle.activate((IPage) destination);
			}
		} else {
		    BasePage page = (BasePage)cycle.getPage(getConfirmationPageName());
		    // if they're a new contributor and want to review licenses, 
		    // send them to the page to set their preferences
		    if(getReviewDefaultLicenses()) { 
		    	page = (BasePage)getContributorLicenseReviewDestination();
		    }
		    try {
		        PropertyUtils.write(page, "contributor", getEditedContributor());
		        setAdditionalConfirmationPageProperties(page);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			cycle.activate(page);
		}
		clearPersistentProperties();		
	}
	/**
	 * don't allow the save to proceed if they are trying to register with
	 * another person's email
	 * @return true if there is another contributor with the same email, false otherwise
	 */
	private boolean checkForExistingContributor() {
		// Check to see if it is a new Contributor trying to register with an existing Contributor's email
		ContributorDAO contrDao = getContributorDAO();
		Contributor otherContr = contrDao.getContributorWithEmail(getEditedContributor().getEmail());
		if (otherContr != null && otherContr.getId() != getEditedContributor().getId()) {
			// Yes, so set the error and return
			setExistingContributor(contrDao.getContributorWithEmail(getEditedContributor().getEmail()));
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkAndSetPassword() {
		// If they have a value for one or the other password fields, make
		// sure the two passwords are equal
		if (StringUtils.notEmpty(getPassword1())) {
			if (!getPassword1().equals(getPassword2())) {
				setError("Your passwords do not match.  Please retype them and ensure that they match.");
				return false;
			} else {
				getEditedContributor().setPassword(getPasswordUtils().getMD5Hash(getPassword1()));
			}
		}
		return true;
	}
	
	private void checkAndWriteOutImage() {
		if (getImageFile() != null && getImageFile().getSize() > 0) {
			ImageUtils imgUtils = getImageUtils();
			String newFilename = imgUtils.writeImageFileToDisk(getImageFile(), imgUtils.getContributorsImagesDirectory());
			newFilename = imgUtils.stripSlashesFromFilename(newFilename);
			getEditedContributor().setImageFilename(newFilename);
		}		
	}
	
	/**
	 * Can be overridden by subclasses in order to prevent a save from occurring
	 * (for instance if a user wants to add or remove a node)
	 * @param cycle 
	 * @return If true, then continue with save, if false, don't
	 */
	protected boolean checkForRedirectOrErrors(IRequestCycle cycle) {		
		checkForAdditionalErrors();
		boolean hasAdditionalError = StringUtils.notEmpty(getError());
		if (hasAdditionalError) {
			return false;
		} else {
			ValidationDelegate delegate = (ValidationDelegate) getBeans().getBean("delegate");
			return !delegate.getHasErrors();
		}
	}
    
	public boolean getIsNewContributor() {
		return getEditedContributor().getId() <= 0;
	}
    
	public Contributor getNewContributor() {
		Contributor contr = new Contributor();
		contr.setContributorType(getContributorType());
		return contr;
	}
	
	protected void removeSelectedNode() {}
       
	private void saveContributorToDb() {
		try {
			ContributorDAO contrDao = getContributorDAO();
			EditHistoryDAO editHistoryDao = getMiscEditHistoryDAO();
			if (getIsNewContributor()) {
				contrDao.addContributor(getEditedContributor(), editHistoryDao);
			} else {
				contrDao.saveContributor(getEditedContributor(), editHistoryDao, getContributor());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}        
	}
    
	public void emailPassword(IRequestCycle cycle) {
	    Contributor contributor = getExistingContributor();
		setExistingContributor(null);  
		AbstractWrappablePage passwordSentPage = (AbstractWrappablePage) cycle.getPage(Login.PASSWORD_SENT_PAGE);
		passwordSentPage.setWrapperType(getContributorType() == Contributor.TREEHOUSE_CONTRIBUTOR ? AbstractWrappablePage.LEARNING_WRAPPER : AbstractWrappablePage.DEFAULT_WRAPPER);
		PropertyUtils.write(passwordSentPage, "contributor", contributor);
		cycle.activate(Login.PASSWORD_SENT_PAGE);
	}
	
	/**
	 *  Subclasses can override this to do additional form processing if necessary
	 */
	protected void doAdditionalProcessing() {
		if (getIsNewContributor()) {
			// set this to be the type they will eventually become, if approved
			getEditedContributor().setUnapprovedContributorType(getUnapprovedContributorType());
		}
	}
	
	/**
	 *  Subclasses can override this to do additional post-save form processing if necessary
	 */
	protected void doPostSaveAdditionalProcessing() {
	}
	protected void checkForAdditionalErrors() {
		if (!getAgreeToTerms() && getIsNewContributor()) {
			setError("You must agree to the terms and conditions in order to register with the ToL.  Please check the box at the bottom of the page indicating your agreement.");
		}		
	}
	protected void setAdditionalConfirmationPageProperties(IPage page) {	
	}
	public void clearPersistentProperties() {
		setEditedContributor(null);
		setExistingContributor(null);
		setImageFile(null);
	}

	
	protected abstract Object getExistingContributorPageDestination();	
	protected abstract String getConfirmationPageName();	
	protected abstract byte getContributorType();
	
	protected IPage getContributorLicenseReviewDestination() {
		ContributorLicensePreferences licensePrefPage = (ContributorLicensePreferences)getRequestCycle().getPage("ContributorLicensePreferences");
		licensePrefPage.setViewedContributor(getEditedContributor());
		return licensePrefPage;
	}
	
	/**
	 * Needs to be implemented by subclasses, the 
	 * type a new contributor will be set to if they need to be approved first
	 */
	protected abstract byte getUnapprovedContributorType();	

	/**
	 * Used in subclasses
	 * @return
	 */
	public boolean getRemoveNodeSelected() {
		return false;
	}
	public boolean getRemoveCutoffNodeSelected() {
		return false;
	}
}
