package org.tolweb.tapestry.search;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.html.BasePage;
import org.tolweb.search.GroupSearchOptions;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * Provide an interface to search for groups within ToL. 
 * 
 * This is meant to replace the legacy perl script that previously 
 * provided this functionality. 
 * 
 * @author lenards
 *
 */
public abstract class SearchPage extends BasePage implements IExternalPage,
		RequestInjectable, NodeInjectable {
	
	// Query String Parameters
	private static final String TAXON_NAME_QPARAM = "taxon";
	private static final String SEARCH_OPTION_QPARAM = "searchOption";

	@InjectPage("ViewGroupSearchResults")
	public abstract ViewGroupSearchResults getSearchResultsPage();
	
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String taxonText = getRequest().getParameterValue(TAXON_NAME_QPARAM);
		String searchOptionText = getRequest().getParameterValue(SEARCH_OPTION_QPARAM);
		
		// if there is a taxon value provide on the query string, perform the search
		if (StringUtils.notEmpty(taxonText)) {
			String searchOption = GroupSearchOptions.DEFAULT;
			if (StringUtils.isEmpty(searchOptionText)
					&& GroupSearchOptions.OPTIONS.contains(searchOptionText)) {
				searchOption = searchOptionText;
			}
			GroupSearchHelper.performSearch(cycle, getPublicNodeDAO(),
					getSearchResultsPage(), taxonText, searchOption);
		}
	}
}