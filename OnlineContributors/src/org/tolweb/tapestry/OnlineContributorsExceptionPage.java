/*
 * Created on Jul 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.util.exception.ExceptionAnalyzer;
import org.apache.tapestry.util.exception.ExceptionDescription;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.treegrow.main.StringUtils;



/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class OnlineContributorsExceptionPage extends BasePage implements RequestInjectable, BaseInjectable {
	public abstract void setException(Throwable value);
	public abstract Throwable getException();
	
	public ExceptionDescription[] getExceptionDescriptions() {
		return new ExceptionAnalyzer().analyze(getException());
	}
	
	public boolean getSendEmail() {
		String inDevelopment = System.getProperty("org.tolweb.development");
		if (StringUtils.notEmpty(inDevelopment)) {
			// if it's us debugging, always send the email
			return true;
		}
		String message = getException().getMessage();
		if (message == null || getConfiguration().getIsBtol()) {
			return true;
		} else {
			boolean lockedAfterCommit = message.contains("locked after a commit()");
			boolean tableModel = message.contains("tableModel");
			boolean numberFormat = NumberFormatException.class.isInstance(getException());
			boolean pageNotFound = message.contains("not found in application");
			boolean noServiceNamed = message.contains("No engine service named");
			String userAgent = getRequest().getHeader("user-agent");
			boolean isDisregardedUA = false;
			if (StringUtils.notEmpty(userAgent)) {
				String regex = "googlebot|ia_archiver|msnbot|Yahoo";
				Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(regex);
				isDisregardedUA = matcher.find();
			}
			// keep this enabled for now to make sure we aren't messing anything up with the
			// tapestry upgrade
			return !lockedAfterCommit && !tableModel && !numberFormat && !pageNotFound && !noServiceNamed
				&& !isDisregardedUA;
			//return true;
		}
	}	
}
	