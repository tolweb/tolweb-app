package org.tolweb.tapestry.search;

import org.tolweb.misc.URLBuilder;
import org.tolweb.search.GroupSearchResult;

public class GroupDecorator extends AbstractGroupDecorator {

	public GroupDecorator(GroupSearchResult result, URLBuilder urlBuilder) {
		super(result, urlBuilder);
	}

	@Override
	public String getNameDecoration() {
		if (getResult().getHasPage()) {
			return getUrlBuilder().getPublicLinkForBranchPage(getResult().getNode(), true);
		}
		return getResult().getGroupName();
	}
}
