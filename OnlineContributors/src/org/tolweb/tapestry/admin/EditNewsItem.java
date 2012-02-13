package org.tolweb.tapestry.admin;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.NewsItem;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.NewsInjectable;
import org.tolweb.treegrow.main.Contributor;

public abstract class EditNewsItem extends BasePage implements IExternalPage, NewsInjectable, CookieInjectable {

	public abstract String getNewsItemText();
	public abstract void setNewsItemText(String text);

	public abstract Long getNewsItemId();
	public abstract void setNewsItemId(Long id);

	public abstract Boolean getNewsItemActive();
	public abstract void setNewsItemActive(Boolean active);
	
	public abstract String getNewsItemPostedBy();
	public abstract void setNewsItemPostedBy(String postedBy);
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
        Long id = (Long) args[0];
        NewsItem item = getNewsItemDAO().getNewsItemWithId(id);
        if (item != null) {
        	setNewsItemText(item.getNewsItemText());
        	setNewsItemId(item.getNewsItemId());
        	if (StringUtils.isEmpty(item.getCreatedBy())) {
        		setNewsItemPostedBy(getContributorName());
        	} else {
        		setNewsItemPostedBy(item.getCreatedBy());
        	}
        	setNewsItemActive(item.getActive());
        }
	}
	
	public void doSaveAndPreview(IRequestCycle cycle) {
		NewsItem item = getNewsItem(getNewsItemId());
		item.setNewsItemText(getNewsItemText());
		item.setCreatedBy(getNewsItemPostedBy());
		item.setActive((getNewsItemActive() == null) ? false : getNewsItemActive());
		getNewsItemDAO().save(item);
		ViewNewsItemPreview previewPage = (ViewNewsItemPreview)cycle.getPage("admin/ViewNewsItemPreview");
		previewPage.setCurrentNewsItem(item);
		cycle.activate(previewPage);
	}
	
	public void cancel(IRequestCycle cycle) {
		ViewAllNewsItems viewPage = (ViewAllNewsItems)cycle.getPage("admin/ViewAllNewsItems");
		cycle.activate(viewPage);
	}
	
	private String getContributorName() {
		Contributor contr = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		return (contr != null) ? contr.getName() : "";
	}
	
	public String getActionVerb() {
		return getNewsItemId() != null ? "Edit" : "Add";
	}
	
	private NewsItem getNewsItem(Long id) {
		NewsItem item = getNewsItemDAO().getNewsItemWithId(getNewsItemId());
		if (item == null) {
			// if this is true, we're creating not editing
			item = new NewsItem();
		}
		return item;
	}
}
