package org.tolweb.tapestry.accessory.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.tapestry.injections.BaseInjectable;

public abstract class AccessoryOrPortfolioLink extends BaseComponent implements BaseInjectable {
	@Parameter(required = true)
	public abstract Object[] getCurrentTreehouse();
    @Parameter(required = true)
    public abstract boolean getIsPortfolio();
	@Parameter(required = true)
	public abstract boolean getIsThisPage();
	
	public String getUrl() {
		Number id = (Number) getCurrentTreehouse()[1];
		return getUrlBuilder().getURLForTreehouse(id);
	}
}
