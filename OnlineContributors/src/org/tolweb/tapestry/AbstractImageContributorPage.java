/*
 * Created on Jul 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.tapestry.event.PageEvent;
import org.tolweb.treegrow.main.Contributor;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractImageContributorPage extends AbstractContributorPage {
	public void pageValidate(PageEvent event) {}
	
	protected byte getUserType() {
		return Contributor.IMAGES_CONTRIBUTOR;
	}
}
