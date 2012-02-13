package org.tolweb.tapestry.search;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.StringPropertySelectionModel;
import org.tolweb.search.GroupSearchOptions;
import org.tolweb.tapestry.injections.MiscInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;

public abstract class GroupSearchBox extends BaseComponent implements
		PageBeginRenderListener, NodeInjectable, MiscInjectable {
	public static StringPropertySelectionModel OPTIONS_MODEL;

	public abstract String getSearchTaxon();

	public abstract void setSearchTaxon(String taxonInput);

	public abstract String getOptionSelection();

	public abstract void setOptionSelection(String option);

	@InjectPage("ViewGroupSearchResults")
	public abstract ViewGroupSearchResults getSearchResultsPage();

	public StringPropertySelectionModel getGroupSearchOptionsModel() {
		return GroupSearchBox.OPTIONS_MODEL;
	}

	public void pageBeginRender(PageEvent event) {
		OPTIONS_MODEL = getPropertySelectionFactory().createModelFromList(
				GroupSearchOptions.getOptionsList());
	}

	public void search(IRequestCycle cycle) {
		GroupSearchHelper.performSearch(cycle, getPublicNodeDAO(),
				getSearchResultsPage(), getSearchTaxon(), getOptionSelection());
/*
		GroupSearchAgent agent = new GroupSearchAgent(getPublicNodeDAO());
		List<GroupSearchResult> results = agent.performSearch(getSearchTaxon(),
				getOptionSelection());
		ViewGroupSearchResults resultsPage = getSearchResultsPage();
		resultsPage.setSearchTaxon(getSearchTaxon());
		resultsPage.setGroupSearchResults(results);
		cycle.activate(resultsPage);
 */
	}
}
