/*
 * ImagesManager.java
 *
 * Created on May 2, 2004, 3:19 PM
 */

package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.NodeImage;

/**
 *
 * @author  dmandel
 */
public abstract class ImagesManager extends AbstractImageContributorPage implements PageBeginRenderListener, IExternalPage, UserInjectable, ImageInjectable {
	protected String getEditWindowName() {
		return EditImageData.EDIT_IMAGE_WINDOW_NAME;
	}    

	public abstract boolean getIsFromTreehouse();
	public abstract void setIsFromTreehouse(boolean value);
    @InjectObject("service:org.tolweb.tapestry.ImageHelper")
    public abstract ImageHelper getImageHelper();	
	
	public void pageValidate(PageEvent event) {
	    
	}
	
    public void activateExternalPage(Object[] params, IRequestCycle cycle) {
	    if (getCookieAndContributorSource().authenticateExternalPage(cycle) == null) {
	        throw new PageRedirectException("ScientificContributorsLogin");
	    }
    }	
	
	public void pageBeginRender(PageEvent event) {
	    super.pageBeginRender(event);
	    if (getPageName().indexOf("Treehouse") != -1) {
	        setIsFromTreehouse(true);
	    }
	}	

	public void editObject(IRequestCycle cycle) {
		Integer imgId = (Integer) cycle.getListenerParameters()[0];
		NodeImage img = getImageDAO().getImageWithId(imgId.intValue());
		getImageHelper().getEditCallback().actOnImage(img, cycle);	
	}	
	
    public void editRegistrationInfo(IRequestCycle cycle) {
    	Contributor currContributor = getContributor();
    	
    	if (currContributor.isScientificContributor()) {
    		ScientificContributorRegistration sciContributorReg = (ScientificContributorRegistration) cycle.getPage("ScientificContributorRegistration");
    		sciContributorReg.setEditedContributor(currContributor);
    		cycle.activate(sciContributorReg);
    	} else if (currContributor.isGeneralContributor()) {
    		GeneralContributorRegistration genContributorReg = (GeneralContributorRegistration) cycle.getPage("GeneralContributorRegistration");
    		genContributorReg.setEditedContributor(currContributor);
    		cycle.activate(genContributorReg);
    	} else if (currContributor.isTreehouseContributor()) {
    		TreehouseContributorRegistration treeContributorReg = (TreehouseContributorRegistration) cycle.getPage("TreehouseContributorRegistration");
    		treeContributorReg.setEditedContributor(currContributor);
    		cycle.activate(treeContributorReg);
    	} else {
    		ImageContributorRegistration registrationPage = (ImageContributorRegistration) cycle.getPage("ImageContributorRegistration");
    		registrationPage.setEditedContributor(currContributor);
    		cycle.activate(registrationPage);
    	}
    }
    
    public String getTitle() {
        return "ToL Media Manager";
    }    
}
