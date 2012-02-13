package org.tolweb.tapestry;

import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.html.BasePage;
import org.tolweb.dao.AccessoryPageDAO;
import org.tolweb.dao.ContributorDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.tapestry.injections.AccessoryInjectable;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class ViewPublishedPages extends BasePage implements IExternalPage, 
		PageInjectable, AccessoryInjectable, BaseInjectable {
	@InjectObject("spring:contributorDAO")
	public abstract ContributorDAO getContributorDAO();	
	public abstract void setContributor(Contributor contr);
	public abstract Contributor getContributor();
	@SuppressWarnings("unchecked")
	public abstract void setArticleNoteNamesIds(List value);
	@SuppressWarnings("unchecked")
	public abstract List getArticleNoteNamesIds();
	public abstract Object[] getCurrentArticleNoteNameId();
	public abstract Object[] getCurrentTreehouseNameId();
	@SuppressWarnings("unchecked")
	public abstract void setTreehouseNamesIds(List value);
	@SuppressWarnings("unchecked")
	public abstract List getTreehouseNamesIds();
	@SuppressWarnings("unchecked")
	public abstract void setBranchLeafPageNames(List value);
	@SuppressWarnings("unchecked")
	public abstract List getBranchLeafPageNames();
	
	@SuppressWarnings("unchecked")
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		Number contributorId = (Number) parameters[0];
		Contributor contr = getContributorDAO().getContributorWithId(contributorId.toString());
		setContributor(contr);
		PageDAO pageDAO = getPublicPageDAO();
		List branchLeafNames = pageDAO.getGroupNamesContributorOwns(contr);
		setBranchLeafPageNames(branchLeafNames);
		AccessoryPageDAO accPageDAO = getPublicAccessoryPageDAO();
		List articleNotes = accPageDAO.getArticleNoteTitlesIdsForContributor(contr);
		setArticleNoteNamesIds(articleNotes);
		List treehouses = accPageDAO.getTreehouseTitlesIdsForContributor(contr);
		setTreehouseNamesIds(treehouses);
	}	
	public boolean getHasBranchOrLeaf() {
		return getBranchLeafPageNames().size() > 0;
	}
	public boolean getHasArticlesNotes() {
		return getArticleNoteNamesIds().size() > 0;
	}
	public boolean getHasTreehouses() {
		return getTreehouseNamesIds().size() > 0;
	}
	public String getCurrentArticleNoteHref() {
		Object[] currentArticleNoteNameId = getCurrentArticleNoteNameId();
		boolean isArticle = (Boolean) currentArticleNoteNameId[2];
		String href = "";
		if (isArticle) {
			href = getUrlBuilder().getURLForArticle(((Number) currentArticleNoteNameId[1]).intValue());
		} else {
			href = getUrlBuilder().getURLForNote(((Number) currentArticleNoteNameId[1]).intValue());			
		}
		return href;		
	}
	public String getCurrentTreehouseUrl() {
		Object[] currentTreehouse = getCurrentTreehouseNameId();
		return getUrlBuilder().getURLForTreehouse((Number) currentTreehouse[1]);
	}
}
