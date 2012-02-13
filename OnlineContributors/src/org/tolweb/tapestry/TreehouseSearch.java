/*
 * Created on Jul 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.hivemind.util.PropertyUtils;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.PermissionChecker;
import org.tolweb.tapestry.injections.AccessoryInjectable;
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
public abstract class TreehouseSearch extends AbstractSearch implements UserInjectable, TreehouseInjectable, AccessoryInjectable {
	public abstract String getCopyrightYear();
	public abstract String getTitle();
	public abstract String getTreehouseId();
	public abstract void setShowAllMyTreehouses(boolean value);
	public abstract boolean getShowAllMyTreehouses();
	public abstract boolean getIsTreehouses();
	public abstract boolean getIsBoth();
	public abstract boolean getDontCheckPermissions();
	public abstract void setCheckPermissions(boolean value);
	public abstract boolean getCheckPermissions();
	public abstract String getEditedObjectType();
	public abstract void setEditedObjectTypeProperty(String value);
	public abstract String getEditedObjectTypeProperty();
	@SuppressWarnings("unchecked")
	public abstract Hashtable getAdditionalValues();
	public abstract String getLastName();
	@Parameter
	public abstract String getReturnPageName();	
	@Parameter(required = false, defaultValue = "@org.tolweb.tapestry.AbstractWrappablePage@LEARNING_WRAPPER")
	public abstract byte getWrapperType();	
	
	public void showAllMyTreehousesSelected(IRequestCycle cycle) {
	    setShowAllMyTreehouses(true);
	}
	
	protected void prepareForRender(IRequestCycle cycle) {
		super.prepareForRender(cycle);
		setCheckPermissions(!getDontCheckPermissions());
		setEditedObjectTypeProperty(getEditedObjectType());
	}
	
	@SuppressWarnings("unchecked")
	public void doSearch(IRequestCycle cycle) {
		String groupNameStr = getGroupName();
		String copyYear = getCopyrightYear();
		String title = getTitle();
		String id = getTreehouseId();
		String lastName = getLastName();
		
		Hashtable args = new Hashtable();
		boolean addContrCriteria = true;
	    // if they are an editor they get to see everything
	    PermissionChecker checker = getPermissionChecker();
	    Contributor contr = getContributor();
	    boolean isBoth = getIsBoth();
		if (!getIsTreehouses() || isBoth) {
		    if (!isBoth) {
		        args.put(AccessoryPageDAO.BONUSPAGES, Integer.valueOf(1));
		    }		    
		    if (checker.isEditor(contr)) {
		        addContrCriteria = false;
		    }
		} 
		if (getIsTreehouses() || isBoth){
		    if (!isBoth) {
		        args.put(AccessoryPageDAO.TREEHOUSES, Integer.valueOf(1));
		    }
		    if (checker.isLearningEditor(contr)) {
		        addContrCriteria = false;
		    }
		}
		boolean dontCheckPermissions = !getCheckPermissions();
		if (dontCheckPermissions) {
		    addContrCriteria = false;
		}
		if (addContrCriteria || getShowAllMyTreehouses()) {
		    args.put(AccessoryPageDAO.CONTRIBUTOR, getContributor());
		}
		if (!getShowAllMyTreehouses()) {
			addIfNotNull(groupNameStr, AccessoryPageDAO.GROUP, args);
			addIfNotNull(copyYear, AccessoryPageDAO.COPY_YEAR, args);
			addIfNotNull(title, AccessoryPageDAO.TITLE, args);
			addIfNotNull(id, AccessoryPageDAO.ID, args);
			if (StringUtils.notEmpty(lastName)) {
				List contrs = getContributorDAO().getContributorIdsWithLastNameOrAlias(lastName);
				if (contrs.size() == 0) {
					setNoResults(true);
					setPageProperties();
					return;
				} else {
					args.put(AccessoryPageDAO.CONTRIBUTOR_IDS, contrs);
				}
			}
		}

		List resultPages;
		try {
			resultPages = getWorkingAccessoryPageDAO().getAccessoryPagesMatchingCriteria(args);        	
		} catch (Exception e) {
			resultPages = new ArrayList();
			e.printStackTrace();
		}
		if (resultPages.size() == 0) {
			setNoResults(true);
			setPageProperties();
		} else {
			TreehouseSearchResults results = (TreehouseSearchResults) cycle.getPage("TreehouseSearchResults");
			results.setPages(resultPages);
			setSearchResultsVariables(results);
			results.setCallbackType(getCallbackType());
			results.setWrapperType(getWrapperType());
			results.setReturnPageName(getReturnPageName());
			results.setAdditionalValues(getAdditionalValues());
			cycle.activate(results);
			return;	
		}	
	}
	
	protected void setPageProperties() {
	    try {
            String returnPageName = getReturnPageName();
	        PropertyUtils.write(getPage(), "returnPageName", returnPageName);
	    } catch (Exception e) {}
	    try {
	    	PropertyUtils.write(getPage(), "dontCheckPermissions", Boolean.valueOf(!getCheckPermissions()));
	    } catch (Exception e) {}
	    try {
	    	PropertyUtils.write(getPage(), "editedObjectType", getEditedObjectTypeProperty());
	    } catch (Exception e) {}
	}
	
	public String getHeaderElement() {
	    if (getIsTreehouses()) {
	        return "td";
	    } else {
	        return "th";
	    }
	}
	
	
	public String getHeaderClass() {
	    if (getIsTreehouses()) {
	        return "boldtext";
	    } else {
	        return null;
	    }
	}	
	
	public String getSubmitButtonString() {
	    if (getIsTreehouses()) {
	        return "Find Contributions";
	    } else {
	        return "Find Articles/Notes";
	    }
	}
	
	public String getObjectType() {
	    if (getIsTreehouses()) {
	        return "Treehouse";
	    } else {
	        return "Article/Note";
	    }
	}
}
