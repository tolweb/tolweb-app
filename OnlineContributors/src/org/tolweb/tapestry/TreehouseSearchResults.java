/*
 * Created on Jul 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.tapestry.IPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InitialValue;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.contrib.table.components.Table;
import org.tolweb.hibernate.MappedAccessoryPage;
import org.tolweb.misc.RendererFactory;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.TreehouseInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class TreehouseSearchResults extends AbstractSearchResults implements UserInjectable, TreehouseInjectable, 
		AccessoryInjectable, MiscInjectable, BaseInjectable {
	public static final int EDIT_PAGE = 0;
	public static final int DELETE_PAGE = 1;
	public static final int COPY_PAGE = 2;
	public static final int EDIT_ARTICLE_NOTE = 3;
	public static final int DELETE_ARTICLE_NOTE = 4;
	public static final int ADD_TO_PORTFOLIO = 5;
	
	@Persist("client")
	@InitialValue("@org.tolweb.tapestry.AbstractWrappablePage@DEFAULT_WRAPPER")
	public abstract byte getWrapperType();
	
	public String getEditWindowName() {
		return RendererFactory.EDIT_TREEHOUSE_WINDOW_NAME;
	}
	
	public abstract void setCallbackType(int value);
	public abstract int getCallbackType();
	@SuppressWarnings("unchecked")
	public abstract void setPages(List value);
	@SuppressWarnings("unchecked")
	public abstract List getPages();
	@SuppressWarnings("unchecked")
	public abstract void setAdditionalValues(Hashtable value);
	@SuppressWarnings("unchecked")
	public abstract Hashtable getAdditionalValues();
	public abstract Long getSelectedAccPageId();
	
	public void searchAgain(IRequestCycle cycle) {
	    IPage page = cycle.getPage(getSearchPageName());
	    getPagePropertySetter().setPageProperties(page, getAdditionalValues());
	    cycle.activate(page);
	}
	
	public boolean getIsTreehouses() {
	    int callbackType = getCallbackType();
	    return callbackType != EDIT_ARTICLE_NOTE && callbackType != DELETE_ARTICLE_NOTE; 
	}
	
	public String getObjectType() {
	    if (getIsTreehouses()) {
	        return "treehouse";
	    } else {
	        return "page";
	    }
	}
	
	public String getCapitalizedObjectType() {
	    String type = getObjectType();
	    return StringUtils.capitalizeString(type);
	}
	
	public void performAction(IRequestCycle cycle) {
		Number treehouseId = getSelectedAccPageId();
		MappedAccessoryPage page = getWorkingAccessoryPageDAO().getAccessoryPageWithId(treehouseId.intValue());
		if (getCallbackType() == EDIT_PAGE) {
			editPage(page, cycle);		
		} else if (getCallbackType() == DELETE_PAGE || getCallbackType() == DELETE_ARTICLE_NOTE) {
			goToDeletePage(page, cycle, getCallbackType() == DELETE_PAGE);
		} else if (getCallbackType() == COPY_PAGE) {
			copyPage(page, cycle);
		} else if (getCallbackType() == ADD_TO_PORTFOLIO) {
		    String returnPageName = (String) getAdditionalValues().get("returnPageName");
		    TreehouseEditPortfolio editPage = (TreehouseEditPortfolio) cycle.getPage(returnPageName);
		    editPage.addToPortfolio(page, cycle);
		}
	}
	
	public boolean getCanEdit() {
	    MappedAccessoryPage treehouse = getCurrentTreehouse();
	    boolean isSubmitted = treehouse.getIsSubmitted() || treehouse.getIsSubmittedToTeacher();
	    return !isSubmitted;
	}
    
    public String getTableColumns() {
        String columns = "title, ID:getAccessoryPageId(), ";
        if (getIsTreehouses()) {
            columns += "!history, ";
        }
        columns += "!edit, !select";
        return columns;
    }
    
    public MappedAccessoryPage getCurrentTreehouse() {
        return (MappedAccessoryPage) ((Table) getComponents().get("table")).getTableRow();
    }
    
    public boolean getIsPublished() {
        return getPublicAccessoryPageDAO().getAccessoryPageExistsWithId(getCurrentTreehouse().getAccessoryPageId());
    }
    
    public String getPublishedUrl() {
        return getUrlBuilder().getPublicURLForObject(getCurrentTreehouse());
    }
	
    public String getWorkingUrl() {
    	return getUrlBuilder().getWorkingURLForObject(getCurrentTreehouse());
    }
    
	private void editPage(MappedAccessoryPage page, IRequestCycle cycle) {
		page.setLastEditedContributor(getContributor());
		page.setLastEditedDate(new Date());
		setTreehouse(page);
		cycle.activate("TreehouseEditor");		
	}
	
	private void goToDeletePage(MappedAccessoryPage page, IRequestCycle cycle, boolean isTreehouse) {
	    DeleteTreehouseConfirm confirm = (DeleteTreehouseConfirm) cycle.getPage("DeleteTreehouseConfirm");
	    confirm.setTreehouse(page);
        if (isTreehouse) {
            confirm.setReturnPageName("TreehouseMaterialsManager");
        } else {
            confirm.setReturnPageName("ScientificMaterialsManager");
        }
		cycle.activate(confirm);
	}
	
	private void copyPage(MappedAccessoryPage page, IRequestCycle cycle) {
		MappedAccessoryPage currentlyEditedPage = getTreehouse();
		byte currentType = currentlyEditedPage.getTreehouseType();
		currentlyEditedPage.copyValues(page);
		currentlyEditedPage.setTreehouseType(currentType);
		cycle.activate("TreehouseEditor");
	}
}
