package org.tolweb.tapestry.admin;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.html.BasePage;
import org.tolweb.hibernate.NewsItem;
import org.tolweb.tapestry.injections.NewsInjectable;

public abstract class ViewNewsItemPreview extends BasePage implements IExternalPage, NewsInjectable {

	@Persist("client")
	public abstract NewsItem getCurrentNewsItem();
	public abstract void setCurrentNewsItem(NewsItem item);	
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
        if (args.length == 1) {
			Long id = (Long) args[0];
	        NewsItem item = getNewsItemDAO().getNewsItemWithId(id);
	        setCurrentNewsItem(item);
        }
	}	
	
	public void doItemActivate(IRequestCycle cycle) {
		getCurrentNewsItem().setActive(true);
		getNewsItemDAO().save(getCurrentNewsItem());
		
		ViewAllNewsItems viewPage = (ViewAllNewsItems)cycle.getPage("admin/ViewAllNewsItems");
		cycle.activate(viewPage);
	}	
	
	public void doEdit(IRequestCycle cycle) {
		EditNewsItem editPage = (EditNewsItem)cycle.getPage("admin/EditNewsItem");
		editPage.setNewsItemId(getCurrentNewsItem().getNewsItemId());
		cycle.activate(editPage);
	}		
}
