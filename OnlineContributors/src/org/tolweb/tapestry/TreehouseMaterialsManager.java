/*
 * Created on Jun 22, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.misc.RendererFactory;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class TreehouseMaterialsManager extends AbstractTreehouseContributorPage implements PageBeginRenderListener, UserInjectable, TreehouseInjectable, 
		AccessoryInjectable, BaseInjectable, MiscInjectable {
    public abstract byte getTreehouseType();
    public abstract void setTreehouseType(byte value);
    public abstract void setRedirectUrl(String value);
    @SuppressWarnings("unchecked")
    public abstract void setMostRecentlyEditedTreehouses(List value);
    public abstract void setCreatePortfolioSelected(boolean value);
    public abstract boolean getCreatePortfolioSelected();
    public abstract MappedAccessoryPage getCurrentTreehouse();
    
    //private String initialText;
    public static final int WEBQUEST = 10;
    
    public String getSearchInstructionsString() {
        return "<p>Click the 'edit' link to edit your treehouse <br /> <span class=\"trhspublished\">Treehouse titles with a green background have previously been published.</span></p>";        
    }
    
    public void updateMediaFormPreference() {
    	
    }
    
	public void pageValidate(PageEvent event) {
	    super.pageValidate(event);
	    if (getIsToolTryout()) {
            setContributor(null);
            throw new PageRedirectException("TreehouseContributorLogin");
        } else {
    		if (getContributor() != null && 
    		        StringUtils.notEmpty(getContributor().getFirstName()) &&
    		        StringUtils.notEmpty(getContributor().getLastName())) {
    			return;
    		}
    		IPage page = getRequestCycle().getPage("StudentNameEntryPage");
    		throw new PageRedirectException(page);
        }
	}    
    
    public String getEditWindowName() {
    	return RendererFactory.EDIT_TREEHOUSE_WINDOW_NAME;
    }
    
    @SuppressWarnings("unchecked")
    public void pageBeginRender(PageEvent e) {
        super.pageBeginRender(e);
        setTreehouseType(MappedAccessoryPage.INVESTIGATION);
        Contributor contributor = getContributor();
        List recentTreehouses = getWorkingAccessoryPageDAO().getMostRecentlyEditedTreehouses(contributor);
        setMostRecentlyEditedTreehouses(recentTreehouses);
    }
    
	public void composeNewTreehouseSubmit(IRequestCycle cycle) {
		Contributor contr = getContributor();
		setNewObjectUploaded(true);
		byte treehouseType = getTreehouseType();
		if (getCreatePortfolioSelected()) {
		    treehouseType = MappedAccessoryPage.PORTFOLIO;
		}

		String pageName = "TreehouseEditor";
		if (getTreehouseType() == MappedAccessoryPage.TEACHERRESOURCE) {
		    pageName = "TeacherResourceChooseResourceType";
		} else {
		    int resourceType = -1;
		    if (getTreehouseType() == WEBQUEST) {
		        // Special case for webquests.  They are teacher resources
		        // but don't show up like that in the UI, so change the values a bit
		        resourceType = TeacherResource.WEBQUEST;
		        treehouseType = MappedAccessoryPage.TEACHERRESOURCE;
		    }
			getAccessoryPageHelper().initializeNewAccessoryPageInstance(contr, 
			        treehouseType, true, getWorkingAccessoryPageDAO(), resourceType, true);
		}
		String url = getTapestryHelper().getPageServiceUrl(pageName);
		setRedirectUrl(url);		
	}
    
    public void registerNewProject(IRequestCycle cycle) {
        ClassroomRegistrationPage page = (ClassroomRegistrationPage) cycle.getPage("ClassroomRegistrationPage");
        page.registerNewProject(cycle);
    }
	
	public void editObject(IRequestCycle cycle) {
	    Integer imgId = (Integer) cycle.getListenerParameters()[0];
		MappedAccessoryPage page = getWorkingAccessoryPageDAO().getAccessoryPageWithId(imgId.intValue());
		setTreehouse(page);
		cycle.activate("TreehouseEditor");		    
	}
	
	public void editRegistrationInfo(IRequestCycle cycle) {
        TreehouseContributorRegistration registrationPage = (TreehouseContributorRegistration) cycle.getPage("TreehouseContributorRegistration");
        registrationPage.setEditedContributor(getContributor());
        cycle.activate(registrationPage);
	}
	
	public void newPortfolioSelected(IRequestCycle cycle) {
	    setCreatePortfolioSelected(true);
	}
    
    public boolean getHasProjects() {
        return getContributorDAO().getNumProjectsForContributor(getContributor()) > 0;
    }
    
    public boolean getCanEdit() {
    	return !getCurrentTreehouse().getIsSubmitted() && !getCurrentTreehouse().getIsSubmittedToTeacher();
    }
	
	/*private String getInitialPageText() {
	    if (initialText == null) {
	    initialText = "<p>Compose your treehouse text here. You may use the instructions written here" + 
	    " to help you build your page. Feel free to erase this help text at any time.</p>\n\n" + 

	    " <p>Put the text for your paragraphs inside p tags like those that you see \n" + 
	    " around this sentence.</p> \n" + 
	    " <p>To begin a new paragraph put it inside new p tags.</p> \n\n" + 

	    " <p>To put text in a bulleted list do the following:</p> \n" +
	    "<ul> \n" + 
	    "<li>This is the 1st thing in my list</li> \n" + 
	    "<li>This is the 2nd thing in my list</li> \n" +
	    "<li>This is the 3rd thing in my list</li> \n" +
	    "<li>This is the last thing in my list</li> \n" +
	    "</ul> \n" +
	    "<p>To put text in a numbered list change the ul at the beginning to ol and the \n" +  
	    "/ul at the end to /ol</p>\n\n" + 

	    "<p><strong>To make text bold</strong> put strong tags around it. <em>To\n" + 
	    "italicize text</em> put em tags around it.</p>\n\n" +

	    "<p>To add images type TOLIMG in the place you want them to go on your page.\n" +  
	    "Next, click Find Image and search for an image in the ToL database. When you\n" +  
	    "have found an image go back to the place you typed TOLIMG and add the image\n" + 
	    "number, for example TOLIMG5746 </p>\n\n" +

	    "<p>If you click Save and Preview you can see what each type of code looks like\n" +  
	    "in a web page. All of this information and more is available in the All the\n" +  
	    "HTML You Need link found in the help menu. </p>\n\n" +

	    "<p>Have fun building your page!</p>";
	    }
	    return initialText;
	}*/
	public String getWorkingUrl() {
		return getUrlBuilder().getWorkingURLForObject(getCurrentTreehouse());
	}
}
