package org.tolweb.tapestry;

import org.apache.tapestry.annotations.ComponentClass;

@ComponentClass(allowBody = true, allowInformalParameters = false)
public abstract class FeaturesWrapper extends Wrapper {
//
// 	public List getStylesheets() {
// 		List stylesheets = new ArrayList();
// 		stylesheets.add(getTolCssStylesheet());
// 		if (getAdditionalStylesheet() != null) {
// 			stylesheets.add(getAdditionalStylesheet());
// 		}
// 		return stylesheets;
// 	}	
}
