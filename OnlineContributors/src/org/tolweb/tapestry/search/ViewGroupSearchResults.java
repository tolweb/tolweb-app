package org.tolweb.tapestry.search;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.tolweb.misc.URLBuilder;
import org.tolweb.search.GroupSearchResult;
import org.tolweb.search.GroupSearchResultComparator;
import org.tolweb.search.GroupSearchResultsProcessor;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;

public abstract class ViewGroupSearchResults extends BasePage implements
		PageBeginRenderListener, NodeInjectable, PageInjectable {
	public static final int MAX_RESULTS = 100;

    @InjectObject("spring:urlBuilder")
    public abstract URLBuilder getUrlBuilder(); 

	public abstract String getSearchTaxon();

	public abstract void setSearchTaxon(String taxonInput);

	public abstract List<GroupSearchResult> getGroupSearchResults();

	public abstract void setGroupSearchResults(List<GroupSearchResult> results);

	public abstract List<GroupSearchResult> getOriginalResults();

	public abstract void setOriginalResults(List<GroupSearchResult> results);
	
	public abstract GroupSearchResult getCurrentGroupSearchResult();

	public abstract void setCurrentGroupSearchResult(GroupSearchResult result);

	public abstract GroupSearchResult getCurrentRelatedGroupSearchResult();

	public abstract void setCurrentRelatedGroupSearchResult(GroupSearchResult result);
	
	public abstract boolean getShouldProcessAllResults();
	
	public abstract void setShouldProcessAllResults(boolean all);
	
	public void pageBeginRender(PageEvent event) {
		if (!event.getRequestCycle().isRewinding()) {
			GroupSearchResultsProcessor proc = new GroupSearchResultsProcessor(
					getMiscNodeDAO(), getPublicNodeDAO(), getPublicPageDAO());

			proc.establishAncestorCounts(getGroupSearchResults());
			Collections.sort(getGroupSearchResults(), new GroupSearchResultComparator());
			
			if (!getShouldProcessAllResults() && getHasMoreResults()) {
				// reduce the size of the results processed
				setOriginalResults(getGroupSearchResults());
				setGroupSearchResults(getOriginalResults().subList(0, MAX_RESULTS));
			}
			
			proc.establishContainingGroupRelations(getGroupSearchResults());
			proc.establishDescendentRelations(getGroupSearchResults());
			
			Set<GroupSearchResult> toRemove = proc.determineRelatedGroups(getGroupSearchResults());
			getGroupSearchResults().removeAll(toRemove);
		}
	}

	public boolean getHasMoreResults() {
		return (getGroupSearchResults() != null && 
				getGroupSearchResults().size() > MAX_RESULTS);
	}

	public boolean getShowSeeAllResultsLink() {
		return (getOriginalResults() != null &&
				getOriginalResults().size() > MAX_RESULTS);
	}
	
	
	public String getCurrentGroup() {
		return getGroup(getCurrentGroupSearchResult());
	}
	
	public String getCurrentRelatedGroup() {
		return getGroup(getCurrentRelatedGroupSearchResult());
	}
    
	public String getGroup(GroupSearchResult result) {
		GroupDecorator decorator = new GroupDecorator(result, getUrlBuilder());
		return decorator.getNameDecoration();
	}	
	
	public String getCurrentContainingGroup() {
		return getContainingGroup(getCurrentGroupSearchResult());
	}	
	
	public String getCurrentRelatedContainingGroup() {
		return getContainingGroup(getCurrentRelatedGroupSearchResult());
	}
	public String getContainingGroup(GroupSearchResult result) {
		ContainingGroupDecorator decorator = new ContainingGroupDecorator(result, getUrlBuilder());
		return decorator.getNameDecoration();
	}
	
	public String getCurrentAncestorCount() {
		return "" + getCurrentGroupSearchResult().getAncestorCount();
	}
	
	public void seeAllResults(IRequestCycle cycle) {
		// link back to this page with a "show all results" sort of indicator
		setGroupSearchResults(getOriginalResults());
		setSearchTaxon(getSearchTaxon());
		setShouldProcessAllResults(true);
		cycle.activate(this);
	}
}
