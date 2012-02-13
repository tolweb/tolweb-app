package org.tolweb.tapestry;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedPage;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.selectionmodels.StringToIntPropertySelectionModel;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.page.PageContributor;

public abstract class ContributorLicensePreferences extends BasePage implements	
	PageBeginRenderListener, IExternalPage, UserInjectable, ImageInjectable, 
	PageInjectable, CookieInjectable, BaseInjectable, AccessoryInjectable {
	
	public static final String TYPE_DEFAULT = "" + ContributorLicenseInfo.CREATIVE_COMMONS;
	
	private IPropertySelectionModel ccLicenseName; 
	private IPropertySelectionModel ccLicenseVersion; 
	
    @Persist("session")
	public abstract Contributor getViewedContributor();	
	public abstract void setViewedContributor(Contributor value);


	public abstract String getTextCreativeCommonsLicenseType();
	public abstract void setTextCreativeCommonsLicenseType(String value);

	public abstract String getTextCreativeCommonsLicenseVersion();
	public abstract void setTextCreativeCommonsLicenseVersion(String value);
	

	public abstract String getMediaCreativeCommonsLicenseType();
	public abstract void setMediaCreativeCommonsLicenseType(String value);

	public abstract String getMediaCreativeCommonsLicenseVersion();
	public abstract void setMediaCreativeCommonsLicenseVersion(String value);
	
	@InitialValue(TYPE_DEFAULT)
    public abstract byte getMediaLicenseType();
    public abstract void setMediaLicenseType(byte value);
    
    @InitialValue(TYPE_DEFAULT)
    public abstract byte getTextLicenseType();
    public abstract void setTextLicenseType(byte value);

    public abstract int getMediaModificationType();
    public abstract void setMediaModificationType(int value);

    @InitialValue("false")
    public abstract boolean getRetroTextSelected();
    public abstract void setRetroTextSelected(boolean value);
    
    @InitialValue("false")
    public abstract boolean getRetroMediaSelected();
    public abstract void setRetroMediaSelected(boolean value);
    
    // DEVN - required by the New Contributor Email component
	public abstract boolean getIsFromRegistration();
	@SuppressWarnings("unchecked")
	public abstract Set getNodesSet();
	
	// DEVN - required by the New Contributor Email component
	@SuppressWarnings("unchecked")
	public String getNodesString() {
		Set nodesSet = getNodesSet();
		
		StringBuilder nodesNamesString = new StringBuilder(); 
		if (nodesSet != null) {
			for (Iterator iter = nodesSet.iterator(); iter.hasNext();) {
				MappedNode element = (MappedNode) iter.next();
				nodesNamesString.append(element.getName());
				if (iter.hasNext()) {
					nodesNamesString.append(", ");
				}
			}
		}
		return nodesNamesString.toString();
	}	
	
	// DEVN - required by the New Contributor Email component
	public boolean getEmailOtherRegistrationNotice() {
		boolean sameContributor = true;
		Contributor registeringContributor = getEditingContributor();
		if (registeringContributor != null) {
			sameContributor = registeringContributor.getId() == getContributor().getId();
		}
		return !sameContributor;
	}
	
	// DEVN - required by the New Contributor Email component
	public Contributor getEditingContributor() {
		return getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
	}	
	
	public String getCurrentViewedContributorName() {
		return getViewedContributor().getName();
	}
	
	public IPropertySelectionModel getCreativeCommonsLicenseTypeSelectionModel() {
		if (ccLicenseName == null) {
			ccLicenseName = new StringPropertySelectionModel(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_NAMES);
		}
		return ccLicenseName;
	}
	
	public IPropertySelectionModel getCreativeCommonsLicenseVersionSelectionModel() {
		if (ccLicenseVersion == null) {
			ccLicenseVersion = new StringPropertySelectionModel(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_VERSIONS);
		}
		return ccLicenseVersion;
	}
	
	public IPropertySelectionModel getMediaModificationSelectionModel() {
		return new StringToIntPropertySelectionModel(Arrays.asList(ContributorLicenseInfo.MEDIA_MODIFICATION_CHOICES));		
	}	

    public String getCheckboxDisplayName() {
        return "Apply this license to all my previous media contributions";
    }	
    
	public void pageBeginRender(PageEvent arg0) {
		initLicenseSelections();
	}

	private void initLicenseSelections() {
		Byte textUseDefault = getViewedContributor().getNoteUseDefault();
		Byte mediaUseDefault = getViewedContributor().getImageUseDefault();
		
		// determine text license selections - if there are no previous defaults, go w/ CC latest version		
		if (textUseDefault == null) {
			setTextCCDefaults();
			setTextLicenseType(ContributorLicenseInfo.CREATIVE_COMMONS);
		} else {
			byte tUse = textUseDefault.byteValue();
			// Note: ToL Partner Use is no longer supported - all former defaults of this value should map to ToL Use.
			tUse = (tUse == ContributorLicenseInfo.TOL_PARTNER_USE) ? ContributorLicenseInfo.TOL_USE : tUse;
			ContributorLicenseInfo textLicInfo = new ContributorLicenseInfo(tUse);
			setTextLicenseType(textLicInfo.getLicenseType());
			handlePropertySelections(textLicInfo, true);
			if (textUseDefault.byteValue() < ContributorLicenseInfo.CC_FIRST_LIC_CODE) {
				setTextCCDefaults();
			}
		}
		
		// determine media license selections - if there are no previous defaults, go w/ CC latest version
		if (mediaUseDefault == null) {
			setMediaCCDefaults();
			setMediaLicenseType(ContributorLicenseInfo.CREATIVE_COMMONS);
		} else {
			Boolean imgModDefault = getContributor().getImageModificationDefault();
			boolean modDefault = (imgModDefault == null) ? true : imgModDefault.booleanValue();
			ContributorLicenseInfo mediaLicInfo = new ContributorLicenseInfo(mediaUseDefault.byteValue(), modDefault);
			setMediaLicenseType(mediaLicInfo.getLicenseType());
			handlePropertySelections(mediaLicInfo, false);
			if (mediaUseDefault.byteValue() < ContributorLicenseInfo.CC_FIRST_LIC_CODE) {
				setMediaCCDefaults();
			}
		}		
	}
	
	private void setTextCCDefaults() {
		setTextCreativeCommonsLicenseType((String)getCreativeCommonsLicenseTypeSelectionModel().getOption(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_NAME_DEFAULT));
		setTextCreativeCommonsLicenseVersion((String)getCreativeCommonsLicenseVersionSelectionModel().getOption(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_VERSION_DEFAULT));		
	}
	
	private void setMediaCCDefaults() {
		setMediaCreativeCommonsLicenseType((String)getCreativeCommonsLicenseTypeSelectionModel().getOption(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_NAME_DEFAULT));
		setMediaCreativeCommonsLicenseVersion((String)getCreativeCommonsLicenseVersionSelectionModel().getOption(ContributorLicenseInfo.CREATIVE_COMMONS_LICENSE_VERSION_DEFAULT));
	}
	
	private void handlePropertySelections(ContributorLicenseInfo cLicInfo, boolean isText) {
		if (cLicInfo.isCreativeCommons()) {
			if (isText) {
				setTextCreativeCommonsLicenseType(cLicInfo.getLicenseName());
				setTextCreativeCommonsLicenseVersion(cLicInfo.getLicenseVersion());
			} else {
				setMediaCreativeCommonsLicenseType(cLicInfo.getLicenseName());
				setMediaCreativeCommonsLicenseVersion(cLicInfo.getLicenseVersion());
			}
		} else if (ContributorLicenseInfo.isToLRelatedLicenseType(cLicInfo.getLicenseType())) {
			if (!isText) {
				setMediaModificationType(!cLicInfo.isModificationPermitted() ? 0 : 1);
			}
		}
	}
	
	public void activateExternalPage(Object[] arg0, IRequestCycle arg1) {

	}

	/**
	 * Handles the submission of the license preferences for the viewing contributor
	 * @param cycle
	 */
	public void licensePreferencesSubmit(IRequestCycle cycle) {
		// determine license by using settings from the page...
		ContributorLicenseInfo textLicInfo = new ContributorLicenseInfo(-1);
		ContributorLicenseInfo mediaLicInfo = new ContributorLicenseInfo(-1);
		processLicenseSettings(textLicInfo, getTextLicenseType(), true);
		processLicenseSettings(mediaLicInfo, getMediaLicenseType(), false);
		byte textCode = (byte)textLicInfo.produceNewLicenseCode();
		byte mediaCode = (byte)mediaLicInfo.produceNewLicenseCode();
		// set the new license preferences on the viewed contributor
		getViewedContributor().setNoteUseDefault(Byte.valueOf(textCode));
		getViewedContributor().setImageUseDefault(Byte.valueOf(mediaCode));
		if (ContributorLicenseInfo.isToLRelatedLicenseCode(mediaCode)) {
			getViewedContributor().setImageModificationDefault(Boolean.valueOf(mediaLicInfo.isModificationPermitted()));
		}
	
		// save who re/viewed the license changes so that we can track that back (might be the user themselves)
		getViewedContributor().setLicenseReviewerContributorId(Long.valueOf(getContributor().getId()));
		
		// commit changes to CONTRIBUTORS
		getContributorDAO().saveContributor(getViewedContributor());
		
		// cascade the license changes to all the viewed contributor's materials
		// retroactively set text contributions in accordance w/ new license preferences
		if (getRetroTextSelected()) {
			doRetroactiveTextLicenseProcess(textLicInfo, textCode);
		}
		// retroactively set media contributions in accordance w/ new license preferences
		if (getRetroMediaSelected()) {
			doRetroactiveMediaLicenseProcess(mediaLicInfo, mediaCode);
		}
		
		// DEVN - for some strange reason the 'active' contributor was not equal to the 
		// viewed contributor in the session cache.  This created a bug where the first 
		// time an image was updated, the license details were wrong.  This appears to 
		// be the easiest workaround to solve the issue.  But it's hackish nature is acknowledged
		if (getContributor().getId() == getViewedContributor().getId()) {
			setContributor(getViewedContributor());
		}
		
		getCacheAccess().evictContributorFromCache(getViewedContributor());
		
		// move the viewed contributor to the confirmation page
		ContributorLicenseConfirmation licenseConfPage = (ContributorLicenseConfirmation)getRequestCycle().getPage("ContributorLicenseConfirmation");
		licenseConfPage.setViewedContributor(getViewedContributor());
		//Contributor c = getContributor();
	    try {
	        PropertyUtils.write(licenseConfPage, "wasRetroTextSelected", getRetroTextSelected());
	        PropertyUtils.write(licenseConfPage, "wasRetroMediaSelected", getRetroMediaSelected());	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }		
		cycle.activate(licenseConfPage);
	}
	
	/**
	 * Takes a license and uses settings to process what has actually been selected
	 * @param licInfo the target license object which license values will be stored 
	 * @param licType the integer-coded type of license or flavor (e.g. Creative Commons, Public Domain, etc.)
	 * @param isText indicates whether text or media licenses are being processed for the given call
	 */
	private void processLicenseSettings(ContributorLicenseInfo licInfo, byte licType, boolean isText) {
		if (licType == ContributorLicenseInfo.CREATIVE_COMMONS) {
			if (isText) {
				licInfo.setLicenseName(getTextCreativeCommonsLicenseType());
				licInfo.setLicenseVersion(getTextCreativeCommonsLicenseVersion());
			} else {
				licInfo.setLicenseName(getMediaCreativeCommonsLicenseType());
				licInfo.setLicenseVersion(getMediaCreativeCommonsLicenseVersion());
			}
		} else {
			licInfo.setLicenseName(ContributorLicenseInfo.getNonCCLicenseName(licType));
			if (!isText && ContributorLicenseInfo.isToLRelatedLicenseType(getMediaLicenseType())) {
				licInfo.setModificationPermitted( (getMediaModificationType() == 1) ? true : false);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doRetroactiveMediaLicenseProcess(ContributorLicenseInfo mediaLicInfo, byte mediaCode) {
		List images = getImageDAO().getImagesForContributor(getViewedContributor());
		// to deal with potential cache issues, we're doing update/select/flush to try to force the right values are in the images
		doRetroactiveMediaUpdates(images, mediaLicInfo, mediaCode);
		doRetroactiveMediaSelects(images);
		doRetroactiveMediaCacheFlush(images);
	}

	@SuppressWarnings("unchecked")
	private void doRetroactiveMediaUpdates(List images, ContributorLicenseInfo mediaLicInfo, byte mediaCode) {
		for (Iterator iter = images.iterator(); iter.hasNext();) {
			NodeImage image = (NodeImage) iter.next();
			if (isImageCopyrightOwner(image, getViewedContributor())) {
				image.setUsePermission(mediaCode);
				image.setModificationPermitted(mediaLicInfo.isModificationPermitted());
				getImageDAO().saveImage(image);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doRetroactiveMediaSelects(List images) {
		for (Iterator iter = images.iterator(); iter.hasNext(); ) {
			NodeImage image = (NodeImage) iter.next();
			getImageDAO().getImageWithId(image.getId());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doRetroactiveMediaCacheFlush(List images) {
		for (Iterator iter = images.iterator(); iter.hasNext(); ) {
			NodeImage image = (NodeImage) iter.next();		
			getCacheAccess().evictImageFromCache(image);
		}
	}
	
	private void doRetroactiveTextLicenseProcess(ContributorLicenseInfo textLicInfo, byte textCode) {
		doRetroactiveTextLicenseProcessForPageDAO(getPublicPageDAO(), textLicInfo, textCode);
		doRetroactiveTextLicenseProcessForPageDAO(getWorkingPageDAO(), textLicInfo, textCode);
		doRetroactiveTextLicenseProcessForAccPageDAO(getPublicAccessoryPageDAO(), textLicInfo, textCode);
		doRetroactiveTextLicenseProcessForAccPageDAO(getWorkingAccessoryPageDAO(), textLicInfo, textCode);
		
	}
	
	@SuppressWarnings("unchecked")
	private void doRetroactiveTextLicenseProcessForAccPageDAO(AccessoryPageDAO currDAO, ContributorLicenseInfo textLicInfo, byte textCode) {
		int contrId = getViewedContributor().getId();
		List accPages = currDAO.getAccessoryPagesForContributor(getViewedContributor());
		for (Iterator itr = accPages.iterator(); itr.hasNext(); ) {
			MappedAccessoryPage accPage = (MappedAccessoryPage)itr.next();
			if (accPage.getContributors() != null && !accPage.getContributors().isEmpty()) {
				if (isEligibleForLicenseChange(accPage.getContributors(), contrId)) {
					accPage.setUsePermission(textCode);
					currDAO.saveAccessoryPage(accPage);
				}
			}
		}
		
		// do selects... 
		for (Iterator itr = accPages.iterator(); itr.hasNext(); ) {
			MappedAccessoryPage accPage = (MappedAccessoryPage)itr.next();
			Long accPageId = Long.valueOf(accPage.getId());
			if (currDAO.getAccessoryPageExistsWithId(accPageId)) {
				currDAO.getAccessoryPageWithId(accPageId);
			}
		}
		// flush cache for acc-pages
		for (Iterator itr = accPages.iterator(); itr.hasNext(); ) {
			MappedAccessoryPage accPage = (MappedAccessoryPage)itr.next();
			if (accPage != null) {
				getCacheAccess().evictAccessoryPageObjectsFromCache(accPage);
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void doRetroactiveTextLicenseProcessForPageDAO(PageDAO currDAO, ContributorLicenseInfo textLicInfo, byte textCode) {
		int contrId = getViewedContributor().getId();
		List pages = currDAO.getPagesForContributor(getViewedContributor());
		for (Iterator iter = pages.iterator(); iter.hasNext();) {
			MappedPage page = (MappedPage) iter.next();
			if (page.getContributors() != null && !page.getContributors().isEmpty()) {
				if (isEligibleForLicenseChange(page.getContributors(), contrId)) {
					page.setUsePermission(textCode);
					currDAO.savePage(page);
				}
			}
		}
		doRetroactivePageSelects(pages, currDAO);
		doRetroactivePageCacheFlush(pages);

	}
	
	@SuppressWarnings("unchecked")
	private void doRetroactivePageSelects(List pages, PageDAO currDAO) {
		for (Iterator iter = pages.iterator(); iter.hasNext(); ) {
			MappedPage page = (MappedPage) iter.next();
			Long pageId = page.getPageId();
			if (currDAO.getNodeHasPage(pageId)) {
				currDAO.getPageId(pageId);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doRetroactivePageCacheFlush(List pages) {
		for (Iterator iter = pages.iterator(); iter.hasNext(); ) {
			MappedPage page = (MappedPage) iter.next();		
	        if (page != null) {
	            getCacheAccess().evictAllPageObjectsFromCache(page);
	        }
		}
	}
	
	private static boolean isImageCopyrightOwner(NodeImage nimg, Contributor cid) {
		return nimg.getCopyrightOwnerContributor() != null && nimg.getCopyrightOwnerContributor().getId() == cid.getId();
	}
	
	@SuppressWarnings("unchecked")
	private static boolean isEligibleForLicenseChange(SortedSet authorSeq, int searchId) {
		return isFirstAuthorAndCopyrightOwner(authorSeq, searchId) ||
		isNotFirstAuthorButCopyrightOwner(authorSeq, searchId);
	}
	
	@SuppressWarnings("unchecked")
	private static boolean isFirstAuthorAndCopyrightOwner(SortedSet authorSeq, int searchId) {
    	PageContributor pc = (PageContributor)authorSeq.first();
    	return isFirstAuthor(pc, searchId) && (pc != null && pc.isCopyOwner());
    }
    
    private static boolean isFirstAuthor(PageContributor pc, int searchId) {
    	return pc != null && pc.getContributorId() == searchId;
    }
    
    @SuppressWarnings("unchecked")
    private static boolean isNotFirstAuthorButCopyrightOwner(SortedSet authorSeq, int searchId) {
    	TreeSet clone = new TreeSet(authorSeq);
    	for(Iterator itr = clone.iterator(); itr.hasNext(); ) {
    		PageContributor pc = (PageContributor)itr.next();
    		if (pc != null && (pc.getContributorId() != searchId && pc.isCopyOwner())) {
    			return false;
    		}
    		if (pc != null && (pc.getContributorId() == searchId && pc.isCopyOwner())) {
    			return true;
    		}
    	}
    	return false; 
    }
    
}
