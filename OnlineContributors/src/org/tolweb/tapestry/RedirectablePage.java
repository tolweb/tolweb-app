package org.tolweb.tapestry;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;
import org.tolweb.treegrow.main.StringUtils;

public abstract class RedirectablePage extends BasePage {
	public abstract void setRedirectURL(String value);
	public abstract String getRedirectURL();
	
	public IRender getRedirectDelegate() {
		return new IRender() {
			public void render(IMarkupWriter arg0, IRequestCycle arg1) {
				if (StringUtils.notEmpty(getRedirectURL())) {
					arg0.printRaw("<meta http-equiv=\"refresh\" content=\"0;url=" + getRedirectURL() + "\">");
				}
			}
		};
	}
}
