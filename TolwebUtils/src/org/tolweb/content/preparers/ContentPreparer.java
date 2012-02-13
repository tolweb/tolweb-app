package org.tolweb.content.preparers;

import org.tolweb.content.licensing.LicenseCriteria;
import org.tolweb.content.licensing.LicenseCriteriaFactory;
import org.tolweb.content.helpers.ContentParameters;
import org.tolweb.dao.ImageDAO;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.misc.ImageUtils;

import nu.xom.Document;
import nu.xom.Element;

public abstract class ContentPreparer {
	public static final String NS = "http://www.eol.org/transfer/content/0.1";	
	private ContentParameters params; 
	private LicenseCriteria criteria; 
	private PageDAO pageDao;
	private NodeDAO publicNodeDao;
	private ImageDAO imageDao;
	private ImageUtils imageUtils;
	private Document preparedDocument;
	
	public abstract void processContent();
	
	protected Element createElement(String elementName, String namespace) {
		return new Element(elementName, namespace);
	}
	
	public ContentParameters getParams() {
		return params;
	}
	
	public void setParams(ContentParameters params) {
		this.params = params;
		this.criteria = LicenseCriteriaFactory.getLicenseCriteria(params.getRequestedLicenseClass());
	}
	
	public Document getDocument() {
		return preparedDocument;
	}
	
	protected LicenseCriteria getCriteria() {
		return criteria;
	}

	protected void setCriteria(LicenseCriteria criteria) {
		this.criteria = criteria;
	}

	protected PageDAO getPageDao() {
		return pageDao;
	}

	protected void setPageDao(PageDAO pageDao) {
		this.pageDao = pageDao;
	}

	protected Document getPreparedDocument() {
		return preparedDocument;
	}

	protected void setPreparedDocument(Document preparedDocument) {
		this.preparedDocument = preparedDocument;
	}

	protected NodeDAO getPublicNodeDao() {
		return publicNodeDao;
	}

	protected void setPublicNodeDao(NodeDAO publicNodeDao) {
		this.publicNodeDao = publicNodeDao;
	}

	protected ImageDAO getImageDao() {
		return imageDao;
	}

	protected void setImageDao(ImageDAO imageDao) {
		this.imageDao = imageDao;
	}

	public ImageUtils getImageUtils() {
		return imageUtils;
	}

	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}
}
