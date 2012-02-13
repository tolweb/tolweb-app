/*
 * Created on Jun 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.link.DefaultLinkRenderer;
import org.tolweb.tapestry.injections.BaseInjectable;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class HelpLink extends BaseComponent implements BaseInjectable {
	public DefaultLinkRenderer getRenderer() {
		return getRendererFactory().getLinkRenderer("helpWindow", 400, 300, "menubar=no,toolbar=yes,scrollbars=yes,resizable=yes,location=yes");
	}
}
