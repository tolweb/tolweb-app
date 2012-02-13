package org.tolweb.tapestry;

import org.apache.tapestry.asset.ExternalAsset;
import org.tolweb.tapestry.wrappers.AbstractWrapper;

public abstract class BuildTreehouseWrapper extends AbstractWrapper {
	private ExternalAsset buildTreehouseCss;

    public ExternalAsset getBuildTreehouseAsset() {
    	if (buildTreehouseCss == null) {
    		buildTreehouseCss = new ExternalAsset("/tree/css/trhscontribute.css", null);
    	}
    	return buildTreehouseCss;
    }
}
