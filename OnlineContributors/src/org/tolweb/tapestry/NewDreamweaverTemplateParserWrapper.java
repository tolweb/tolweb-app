/*
 * Created on Jul 24, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.tolweb.tapestry;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Parameter;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.tapestry.wrappers.AbstractWrapper;

/**
 * @author dmandel
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class NewDreamweaverTemplateParserWrapper extends AbstractWrapper implements UserInjectable {
	protected IRender delegate;
	
	@Parameter
	public abstract IRender getAdditionalDelegate();
	@Parameter(required = true)
	public abstract String getTitle();
	
	public NewDreamweaverTemplateParserWrapper() {
		delegate = new IRender() {
			public void render(IMarkupWriter writer, IRequestCycle cycle) {
				if (getAdditionalDelegate() != null) {
					getAdditionalDelegate().render(writer, cycle);
				}
			}			
		};		
	}
	
	public String getHeadContentRegex() {
		return ".*<!-- InstanceBeginEditable name=\"head\" -->[^<]*(.*?)</head>";	
	}	
	public String getBeforeContentRegex() {
		return "<body>(.*?)<!-- TemplateBeginEditable name=\"pagecontent\" -->";		
	}
	public String getAfterContentRegex() {
		return "<!-- TemplateBeginEditable name=\"pagecontent\" -->.*<!-- TemplateEndEditable -->(.*)</body>";
	}	
	public IRender getDelegate() {
		return delegate;
	}
	/**
	 * here for tapestry purposes.  does nothing
	 * @param render
	 */
	public void setDelegate(IRender render) {
	}
}
