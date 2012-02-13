package org.tolweb.tapestry.search;

import org.tolweb.misc.URLBuilder;
import org.tolweb.search.GroupSearchResult;

public class ContainingGroupDecorator extends AbstractGroupDecorator {
	public static final String FORMAT = "(%1$s)";
	public ContainingGroupDecorator(GroupSearchResult result,
			URLBuilder urlBuilder) {
		super(result, urlBuilder);
	}

	@Override
	public String getNameDecoration() {
		if (getResult() != null) { 
			if(!getResult().getHasPage()) {
					return format(getUrlBuilder().getPublicLinkForBranchPage(getResult().getContainingGroup(), true));
			} else {
				return format(getResult().getContainingGroupName());
			}
		} 
		return "";
	}

	private String format(String text) {
		return String.format(FORMAT, text);
	}
}
