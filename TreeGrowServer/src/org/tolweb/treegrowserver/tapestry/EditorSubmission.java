/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver.tapestry;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.request.RequestContext;
import org.jdom.Document;
import org.tolweb.tapestry.injections.BaseInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.treegrow.main.RequestParameters;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditorSubmission extends XMLPage implements IExternalPage, 
		BaseInjectable, TreeGrowServerInjectable {
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		super.activateExternalPage(parameters, cycle);
		String xmlString = getRequest().getParameterValue(RequestParameters.XML);
		xmlString = getTextPreparer().replaceControlCharacters(xmlString);
		Document doc = null;
		try {
		    doc = getUploadBuilder().buildUpload(xmlString);
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}
		//System.out.println("response is: " + getSerializedDocument(doc));
		setResultDocument(doc);
	}
}
