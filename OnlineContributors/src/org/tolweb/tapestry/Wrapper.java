/*
 * Wrapper.java
 *
 * Created on April 22, 2004, 3:33 PM
 */

package org.tolweb.tapestry;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.asset.ExternalAsset;
import org.tolweb.tapestry.wrappers.AbstractWrapper;
import org.tolweb.treegrow.main.Contributor;

/**
 *
 * @author  dmandel
 */
@ComponentClass(allowBody = true, allowInformalParameters = false)
public abstract class Wrapper extends AbstractWrapper {
	private ExternalAsset externalContributeAsset;

	@Asset("css/contribute.css")
	public abstract IAsset getContributeAsset();
	
 	public String getLogoutPageName() {
 		return "Home";
 	} 	
 	
 	@SuppressWarnings("unchecked")
 	public List getStylesheets() {
 		List stylesheets = new ArrayList();
 		stylesheets.add(getTolCssStylesheet());
 		stylesheets.add(getContributeStylesheet());
 		if (getAdditionalStylesheet() != null) {
 			stylesheets.add(getAdditionalStylesheet());
 		}
 		return stylesheets;
 	}
 	
 	public IAsset getContributeStylesheet() {
 		if (getConfiguration().getUseExternalStylesheets()) {
 			return getExternalContributeAsset();
 		} else {
 			return getContributeAsset();
 		}
 	}

 	public String getManagerPage() {
 		Contributor currContributor = getContributor();

    	if (currContributor.isScientificContributor() || currContributor.isGeneralContributor()) {
    		return "ScientificMaterialsManager";
    	} else if (currContributor.isTreehouseContributor()) {
    		return "TreehouseMaterialsManager";
    	} else {
    		return "ImagesManager";
    	}
 	}
 	
	public ExternalAsset getExternalContributeAsset() {
		if (externalContributeAsset == null) {
			externalContributeAsset = new ExternalAsset("/tree/css/contribute.css", null);
		}
		return externalContributeAsset;
	}

	public void setExternalContributeAsset(ExternalAsset externalContributeAsset) {
		this.externalContributeAsset = externalContributeAsset;
	}
}
