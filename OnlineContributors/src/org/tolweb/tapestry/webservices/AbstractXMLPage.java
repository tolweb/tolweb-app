package org.tolweb.tapestry.webservices;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry.AbstractPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.util.ContentType;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

public abstract class AbstractXMLPage extends AbstractPage implements RequestInjectable, TreeGrowServerInjectable {

	@InjectObject("service:tapestry.globals.HttpServletResponse")
	public abstract HttpServletResponse getServletResponse();	
	
	public ContentType getResponseContentType() {
        return new ContentType("text/xml");
    }

	protected DataWriter getDataWriterForServletResponse() {
		return getServerXMLWriter().getDataWriterForServletResponse(getServletResponse());
	}

	protected void outputNodeNameElement(String name, DataWriter w) throws SAXException {
		getServerXMLWriter().outputNodeNameElement(name, w);
	}	
	
    /** finishLoad is overridden here so that no template file is loaded. */
    public void finishLoad(IRequestCycle cycle, IPageLoader loader, 
    		IComponentSpecification specification) {
    }	
}
