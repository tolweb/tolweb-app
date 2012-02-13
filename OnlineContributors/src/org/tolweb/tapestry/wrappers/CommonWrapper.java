package org.tolweb.tapestry.wrappers;

import java.util.List;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Parameter;

public abstract class CommonWrapper extends AbstractWrapper {
	@Parameter
	public abstract List<IAsset> getStylesheets();
}
