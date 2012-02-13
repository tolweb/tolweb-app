/*
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.tapestry.injections.BaseInjectable;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ArticleNoteDataConfirm extends TreehouseDataConfirm implements BaseInjectable {
	public void editTreehouse(IRequestCycle cycle) {
		EditArticleNote editPage = (EditArticleNote) cycle.getPage("EditArticleNote");
		editPage.setAccPage(getTreehouse());
		cycle.activate(editPage);
		setTreehouse(null);
	}   

    public String getWorkingUrl() {
        return getUrlBuilder().getWorkingURLForObject(getTreehouse());
    }
    
	
	public String getSubmitPageName() {
	    return "ArticleNoteSubmitted";
	}
	
	public String getPropertyName() {
	    return "accPage";
	}
}
