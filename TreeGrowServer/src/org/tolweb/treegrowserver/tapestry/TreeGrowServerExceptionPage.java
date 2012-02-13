/*
 * Created on Jan 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import org.apache.tapestry.html.BasePage;
import org.apache.tapestry.util.exception.ExceptionAnalyzer;
import org.apache.tapestry.util.exception.ExceptionDescription;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class TreeGrowServerExceptionPage extends BasePage {
	public abstract void setException(Throwable value);
	public abstract Throwable getException();
	
	public ExceptionDescription[] getExceptionDescriptions() {
		return new ExceptionAnalyzer().analyze(getException());
	}
}
