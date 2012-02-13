/*
 * Created on Jan 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.tapestry.BaseComponent;
import org.tolweb.misc.ContributorLicenseInfo;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.PageContributor;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class MinorAboutPage extends BaseComponent {
	@SuppressWarnings("unchecked")
    public abstract Collection getContributors();
    public abstract Integer getUsePermission();
    
    @SuppressWarnings("unchecked")
    public String getCopyrightHoldersString() {
        ArrayList holderList = new ArrayList();
        for (Iterator iter = getContributors().iterator(); iter.hasNext();) {
            PageContributor nextContributor = (PageContributor) iter.next();
            if (nextContributor.getIsCopyOwner()) {
                Contributor actualContributor = nextContributor.getContributor();
                holderList.add(actualContributor.getDisplayName());
            }
        }
        return StringUtils.returnCommaAndJoinedString(holderList);
    }
    
	public String getLicenseInfo() {
		if (getUsePermission() != null) {
			int licVal = getUsePermission().intValue();
			ContributorLicenseInfo licInfo = new ContributorLicenseInfo(licVal);
			return "   " + licInfo.toString() + " - " + licInfo.getLicenseVersion() + "   ";
		} 
		return "[No License Setting...]";
	}    
}
