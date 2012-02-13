package org.tolweb.btol.tapestry;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.tolweb.btol.injections.ProjectInjectable;
import org.tolweb.tapestry.wrappers.AbstractWrapper;

public abstract class ProjectWrapper extends AbstractWrapper implements ProjectInjectable {
	@Asset("css/taxonsampling.css")
	public abstract IAsset getBtolStylesheet();
}
