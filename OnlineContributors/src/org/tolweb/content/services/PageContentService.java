package org.tolweb.content.services;

import java.sql.Date;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Text;

import org.apache.tapestry.IRequestCycle;
import org.tolweb.content.helpers.ContentParameters;
import org.tolweb.content.licensing.ContentLicenseClass;
import org.tolweb.content.preparers.ContentPreparer;
import org.tolweb.content.preparers.EolContentPreparer;
import org.tolweb.tapestry.injections.CitationInjectable;
import org.tolweb.tapestry.injections.ImageInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.treegrow.main.StringUtils;

/**
 * 
 * @author lenards
 * 
 * onlinecontributors/app?page=content/ToLPageContentService&service=external&nodeId=3&license=nc
 * 
 * onlinecontributors/app?page=content/PageContentService&service=external&nodeId=3&license=nc
 */
public abstract class PageContentService extends XMLContentBaseService implements PageInjectable, NodeInjectable, ImageInjectable, CitationInjectable {
	public static final String SERVICE_NAME = "tol-page-content";
	// Service Parameters
	private static final String NODE_ID = "nodeId";
	private static final String LICENSE = "license";

	private static final String NODE_ID_FMT = "%1$s";
	private static final String LICENSE_FMT = "%2$s";	
	
	private static final String PARAMETER_ERROR_MESSAGE = "Error - no request parameters were provided." + 
														   "  Page Content Service only supports `" + 
														   NODE_ID + "` and `" + LICENSE + "` as parameters. " +
														   "Please consult ToL Web Service Documentation for " + 
														   " more info: http://webservices.tolweb.org/";
	private static final String EXCEPTION_MESSAGE = "An exception occurs when attempting to get content for node-id: `" +
													NODE_ID_FMT + "` with a license of `" + LICENSE_FMT + "`.  If the problem " + 
													"persists, email the Tree of Life Web team to inform them of the " +
													"issue (please include this message). "; 
	public String getName() {
		return SERVICE_NAME;
	}

	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String licenseText = getRequest().getParameterValue(LICENSE);
		String nodeIdText = getRequest().getParameterValue(NODE_ID);
		
		if (StringUtils.notEmpty(nodeIdText) && StringUtils.notEmpty(licenseText)) {
			Long nodeId = Long.valueOf(nodeIdText);
			ContentLicenseClass licClass = ContentLicenseClass.createContentLicenseClass(licenseText);
			System.out.println("node id is : " + nodeId);
			System.out.println("license value is : " + licClass);
			
			try {
				ContentParameters cparams = new ContentParameters(nodeId,
						new Date(System.currentTimeMillis()), licClass);
				//			ContentPreparer contentPrep = new PageContentPreparer(cparams, getPublicPageDAO(), getPublicNodeDAO(), getImageDAO());
				ContentPreparer contentPrep = new EolContentPreparer(cparams,
						getPublicPageDAO(), getPublicNodeDAO(), getImageDAO(), getImageUtils());
				((EolContentPreparer)contentPrep).setCitationCreator(getCitationCreator());
				contentPrep.processContent();
				Document document = contentPrep.getDocument();
				setDocumentString(document.toXML());
			} catch (Exception e) {
				createErrorResponse(String.format(EXCEPTION_MESSAGE, nodeIdText, licenseText));
			}
		} else {
			createErrorResponse(PARAMETER_ERROR_MESSAGE);
		}
	}
	
	private void createErrorResponse(String message) {
		Element response = new Element("response", EolContentPreparer.NS);
		Element error = new Element("error", EolContentPreparer.NS);
		error.appendChild(new Text(message));
		response.appendChild(error);
		Document document = new Document(response);
		addServiceNameAttribute(document);
		setDocumentString(document.toXML());		
	}
	
	private void addServiceNameAttribute(Document doc) {
		doc.getRootElement().addAttribute(new Attribute("service", getName()));
	}	
}