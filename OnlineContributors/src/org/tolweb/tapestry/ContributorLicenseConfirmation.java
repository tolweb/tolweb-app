package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ContributorLicenseConfirmation extends BasePage implements
		PageBeginRenderListener, IExternalPage, UserInjectable {

    @Persist("session")
	public abstract Contributor getViewedContributor();	
	public abstract void setViewedContributor(Contributor value);
	
    public abstract boolean getWasRetroTextSelected();
    public abstract boolean getWasRetroMediaSelected();	        
	
	public String getCurrentViewedContributorName() {
		return getViewedContributor().getName();
	}

	public int getCurrentViewContributorId() {
		return getViewedContributor().getId();
	}
	
	public boolean getTextLicenseEOLCompatibility() {
		return isCompatible(getViewedContributor().getNoteUseDefault());
	}
	
	public String getTextLicenseUrl() {
		ContributorLicenseInfo licInfo = getLicenseInfo(getViewedContributor().getNoteUseDefault());
		// TODO WRAP toString with Link to License Information ...  
		//<a href="http://creativecommons.org/licenses/by/3.0/">Attribution Creative Commons License</a>		
		return (licInfo != null) ? licenseLink(licInfo) : "#";
	}
	
	public String getTextLicenseDisplayName() {
		return getLicenseInfo(getViewedContributor().getNoteUseDefault()).toString();		
	}
	
	public boolean getMediaLicenseEOLCompatibility() {
		return isCompatible(getViewedContributor().getImageUseDefault());
	}
	
	public String getMediaLicenseUrl() {
		ContributorLicenseInfo licInfo = getLicenseInfo(getViewedContributor().getImageUseDefault());
		// TODO WRAP toString with Link to License Information ...  
		//<a href="http://creativecommons.org/licenses/by/3.0/">Attribution Creative Commons License</a>
		return (licInfo != null) ? licenseLink(licInfo) : "#";		
	}
	
	public String getMediaLicenseDisplayName() { 
		return getLicenseInfo(getViewedContributor().getImageUseDefault()).toString();
	}
	
	private String licenseLink(ContributorLicenseInfo licInfo) {
		return ContributorLicenseInfo.linkString(licInfo);
	}
	
	private boolean isCompatible(Byte useCode) {
		if (useCode != null) {
			ContributorLicenseInfo licInfo = new ContributorLicenseInfo(useCode.byteValue());
			return licInfo.isEolCompatible();
		}
		return false;
	}
	
	private ContributorLicenseInfo getLicenseInfo(Byte code) {
		if (code != null) {
			ContributorLicenseInfo licInfo = new ContributorLicenseInfo(code.byteValue());
			return licInfo;
		}
		return null;
	}
	
	public void pageBeginRender(PageEvent arg0) {
		// TODO Auto-generated method stub

	}

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        try {
            int contributorId = ((Number) parameters[0]).intValue();
            ContributorDAO contrDao = getContributorDAO(); 
            Contributor contr = contrDao.getContributorWithId("" + contributorId);
            setViewedContributor(contr);
        } catch (Exception e) {
        	e.printStackTrace();
        	setViewedContributor(null);
        }
    }
    
	public void editDefaultsSubmit(IRequestCycle cycle) {
		ContributorLicensePreferences licensePrefsPage = (ContributorLicensePreferences)getRequestCycle().getPage("ContributorLicensePreferences");
		licensePrefsPage.setViewedContributor(getViewedContributor());
		cycle.activate(licensePrefsPage);		
	}	
}
