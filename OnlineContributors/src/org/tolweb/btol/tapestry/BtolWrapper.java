package org.tolweb.btol.tapestry;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IRender;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.wrappers.AbstractWrapper;

public abstract class BtolWrapper extends AbstractWrapper implements UserInjectable {
	@Parameter(required = true)
	public abstract String getTitle();
	@Parameter
	public abstract IRender getAdditionalDelegate();
	@Asset("css/tol.css")
	public abstract IAsset getStylesheet();
}
