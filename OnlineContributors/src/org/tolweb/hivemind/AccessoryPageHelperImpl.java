package org.tolweb.hivemind;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.hibernate.EditComment;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.hibernate.Portfolio;
import org.tolweb.hibernate.PortfolioSection;
import org.tolweb.hibernate.TeacherResource;
import org.tolweb.hibernate.Webquest;
import org.tolweb.misc.AccessoryPageSubmitter;
import org.tolweb.misc.ReorderHelper;
import org.tolweb.misc.UsePermissionHelper;
import org.tolweb.tapestry.EditArticleNote;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.Keywords;
import org.tolweb.treegrow.main.Languages;
import org.tolweb.treegrow.main.NodeImage;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.page.AccessoryPageContributor;
import org.tolweb.treegrow.page.InternetLink;

public class AccessoryPageHelperImpl extends AppStateManagerAware implements AccessoryPageHelper {
	private UsePermissionHelper usePermissionHelper;
	private AccessoryPageSubmitter accessoryPageSubmitter;
	private ReorderHelper reorderHelper;
	private AccessoryPageDAO workingAccessoryPageDAO;
	private IRequestCycle requestCycle;
	
	private static Logger logger;
	static {
		logger = Logger.getLogger(AccessoryPageHelperImpl.class);
	}   	
			
	public MappedAccessoryPage initializeNewAccessoryPageInstance(Contributor contr, boolean isTreehouse, 
			AccessoryPageDAO dao) {
	    return initializeNewAccessoryPageInstance(contr, ((byte) 0), isTreehouse, dao, -1, false);
	}
	
	@SuppressWarnings("unchecked")
	public MappedAccessoryPage initializeNewAccessoryPageInstance(Contributor contr, byte treehouseType, 
			boolean isTreehouse, AccessoryPageDAO dao, int teacherResourceType, boolean setInVisit) {
		MappedAccessoryPage page;
		if (treehouseType == MappedAccessoryPage.TEACHERRESOURCE) {
		    if (teacherResourceType != TeacherResource.WEBQUEST) {
		        page = new TeacherResource();
		    } else {
		        page = new Webquest();
		    }
		    ((TeacherResource) page).setResourceType(teacherResourceType);
		} else {
		    page = new MappedAccessoryPage();
		}
	    page.setLanguages(new Languages());		
		if (treehouseType == MappedAccessoryPage.TEACHERRESOURCE || treehouseType == MappedAccessoryPage.PORTFOLIO) {
		    Portfolio portfolio = new Portfolio();
		    PortfolioSection newSection = new PortfolioSection();
		    newSection.setOrder(0);
		    portfolio.getSections().add(newSection);
		    page.setPortfolio(portfolio);
		}
		AccessoryPageContributor contributor = new AccessoryPageContributor();
		contributor.setContributor(contr);
		contributor.setIsAuthor(true);
		contributor.setIsContact(true);
		contributor.setIsCopyOwner(true);
		page.setContributor(contr);
        page.setContributorId(contr.getId());
		page.setCreationDate(new Date());
		page.setIsTreehouse(isTreehouse);
		page.setUsePermission(NodeImage.EVERYWHERE_USE);
		if (isTreehouse) {
		    if (Webquest.class.isInstance(page)) {
		        page.setTreehouseType(MappedAccessoryPage.WEBQUEST);
		    } else {
		        page.setTreehouseType(treehouseType);
		    }
			// Use a different title for teacher resources since they have subcategories
			String title;
			String additionalDescription = "";
			if (TeacherResource.class.isInstance(page)) {
			    additionalDescription += " " + ((TeacherResource) page).getResourceTypeString();
			} 
			title = contr.getDisplayName() + "'s New " + page.getTreehouseTypeString() + additionalDescription + " Contribution";
			page.setMenu(title);
			page.setPageTitle(title);
			InternetLink newLink = new InternetLink();
			newLink.setComments("");
			newLink.setSiteName("");
			newLink.setUrl("");
			page.addToInternetLinks(newLink);			
		} else {
			String title = contr.getDisplayName() + "'s New Note";
			page.setMenu(title);
			page.setPageTitle(title);		    
			page.setInternetInfo(EditArticleNote.INITIAL_INTERNET_LINKS);
		}
		getUsePermissionHelper().initializeNewPermissions(contr, page, false);		
		page.addToContributors(contributor);
		page.setCopyrightYear(new GregorianCalendar().get(Calendar.YEAR) + "");
		page.setText("");
		page.setKeywords(new Keywords());
		dao.addAccessoryPage(page, contr);
		if (setInVisit) {
		    setTreehouse(page);
		}
		if (logger.isDebugEnabled()) {
		    String type = isTreehouse ? "treehouse" : "note";
			logger.debug("Contributor " + getContributor().getName() + " just created " + type + ": " + 
					page.getTreehouseTypeString() + " with id: " + page.getAccessoryPageId());		    
		}					
		return page;
	}
	@SuppressWarnings("unchecked")
	public void submitTreehouse(IRequestCycle cycle, MappedAccessoryPage treehouse, String submitPageName, 
			String treehousePropertyName, boolean isSubmitToTeacher, String submitComments, Hashtable additionalPageArgs) {
    	Contributor contr = getContributor();
    	getAccessoryPageSubmitter().nonEditorSubmitAccessoryPage(treehouse, contr);
    	if (isSubmitToTeacher) {
            treehouse.setIsSubmittedToTeacher(true);
            treehouse.setIsSubmitted(false);
        } else {
            treehouse.setIsSubmittedToTeacher(false);
            treehouse.setIsSubmitted(true);
        }

        EditComment comment = new EditComment();
        String commentsString = "";
        if (isSubmitToTeacher) {
            commentsString = "Treehouse submitted to teacher for approval";
        } else {
            commentsString = "Treehouse submitted to editor for approval";
        }
        if (StringUtils.notEmpty(submitComments)) {
            commentsString += " with comments: <p>" + submitComments + "</p>";
        }
        comment.setComment(commentsString);
        comment.setCommentContributor(contr);
        comment.setCommentDate(new Date());
        getReorderHelper().addToSet(treehouse.getEditComments(), comment);
        getWorkingAccessoryPageDAO().saveAccessoryPage(treehouse);
        IPage page = cycle.getPage(submitPageName);
        PropertyUtils.write(page, treehousePropertyName, treehouse);
        if (additionalPageArgs != null) {
            for (Iterator iter = additionalPageArgs.keySet().iterator(); iter.hasNext();) {
                String nextProperty = (String) iter.next();
                PropertyUtils.write(page, nextProperty, additionalPageArgs.get(nextProperty));
            }
        }
        cycle.activate(page);
	}
    public void establishEditorUseInRequestCycle() {
		boolean useEditor = true;
		if (getContributorExists()) {
			Contributor contr = getContributor();
			useEditor = (contr != null && !contr.getDontUseEditor());
		}
	    getRequestCycle().setAttribute(USE_EDITOR, Boolean.valueOf(useEditor));    	
    }
    public boolean getShouldUseEditor() {
	    Boolean returnVal = (Boolean) getRequestCycle().getAttribute(AccessoryPageHelper.USE_EDITOR);
	    return returnVal != null && returnVal.booleanValue();    	
    }
	public UsePermissionHelper getUsePermissionHelper() {
		return usePermissionHelper;
	}
	public void setUsePermissionHelper(UsePermissionHelper usePermissionHelper) {
		this.usePermissionHelper = usePermissionHelper;
	}
	public AccessoryPageSubmitter getAccessoryPageSubmitter() {
		return accessoryPageSubmitter;
	}
	public void setAccessoryPageSubmitter(
			AccessoryPageSubmitter accessoryPageSubmitter) {
		this.accessoryPageSubmitter = accessoryPageSubmitter;
	}
	public ReorderHelper getReorderHelper() {
		return reorderHelper;
	}
	public void setReorderHelper(ReorderHelper reorderHelper) {
		this.reorderHelper = reorderHelper;
	}
	public AccessoryPageDAO getWorkingAccessoryPageDAO() {
		return workingAccessoryPageDAO;
	}
	public void setWorkingAccessoryPageDAO(AccessoryPageDAO workingAccessoryPageDAO) {
		this.workingAccessoryPageDAO = workingAccessoryPageDAO;
	}
	public IRequestCycle getRequestCycle() {
		return requestCycle;
	}
	public void setRequestCycle(IRequestCycle cycle) {
		this.requestCycle = cycle;
	}
}
