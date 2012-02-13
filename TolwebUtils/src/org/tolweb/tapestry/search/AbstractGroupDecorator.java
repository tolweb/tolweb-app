package org.tolweb.tapestry.search;

import org.tolweb.misc.URLBuilder;
import org.tolweb.search.GroupSearchResult;

public abstract class AbstractGroupDecorator {
	private GroupSearchResult result;
	private URLBuilder urlBuilder;
	
	public AbstractGroupDecorator(GroupSearchResult result,
			URLBuilder urlBuilder) {
		super();
		this.result = result;
		this.urlBuilder = urlBuilder;
	}
	
	public abstract String getNameDecoration();

	/**
	 * @return the result
	 */
	protected GroupSearchResult getResult() {
		return result;
	}

	/**
	 * @return the urlBuilder
	 */
	protected URLBuilder getUrlBuilder() {
		return urlBuilder;
	}
}
