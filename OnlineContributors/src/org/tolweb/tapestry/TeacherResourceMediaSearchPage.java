/*
 * Created on May 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.Document;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.hivemind.ImageHelper;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TeacherResourceMediaSearchPage extends BasePage implements PageBeginRenderListener, IExternalPage, 
		TreehouseInjectable, ImageInjectable, MiscInjectable {
    public abstract void setCallbackType(int value);
    @Persist("client")
    public abstract int getCallbackType();
    public abstract int getNewObjectId();
    public abstract void setDelegate(IRender delegate);    
    @InjectObject("service:org.tolweb.tapestry.ImageHelper")
    public abstract ImageHelper getImageHelper();
    
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        Integer newObjectId = (Integer) parameters[0];
        byte callbackType = ((Integer) parameters[1]).byteValue(); 
        Document doc = (Document) getImageDAO().getImageWithId(newObjectId.intValue());
        TeacherResource resource = (TeacherResource) getTreehouse();
        if (callbackType == ImageSearchResults.TR_NATIONAL_STANDARDS_CALLBACK) {
            resource.setNationalStandardsDocument(doc);
        } else if (callbackType == ImageSearchResults.TR_STATE_STANDARDS_CALLBACK) {
            resource.setStateStandardsDocument(doc);
        } else {
            TeacherResourceEditSupportMaterials supportMatsPage = (TeacherResourceEditSupportMaterials) cycle.getPage("TeacherResourceEditSupportMaterials");
            supportMatsPage.doAddSupportDocument(doc, cycle);
            return;	
        }
        TeacherResourceEditLearning page = (TeacherResourceEditLearning) cycle.getPage("TeacherResourceEditLearning");
        page.doSave();
        cycle.activate(page);
    }
    
    public void pageBeginRender(PageEvent event) {
        if (getNewObjectId() > 0) {
        	String url = getTapestryHelper().getExternalServiceUrl(getPageName(), new Object[] {getNewObjectId(), getCallbackType()});
        	setDelegate(getImageHelper().getRedirectDelegate(url));
        }
    }
}
