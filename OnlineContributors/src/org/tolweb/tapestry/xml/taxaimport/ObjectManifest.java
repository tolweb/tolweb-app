package org.tolweb.tapestry.xml.taxaimport;

import nu.xom.Document;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.tolweb.content.services.XMLContentBaseService;
import org.tolweb.tapestry.injections.CookieInjectable;
import org.tolweb.tapestry.injections.MetaInjectable;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;

/**
 * 
 * @author lenards
 * onlinecontributors/app?page=xml/ObjectManifest&service=external&node_id=1366
 */
public abstract class ObjectManifest extends XMLContentBaseService implements PageBeginRenderListener, 
	NodeInjectable,	MetaInjectable, CookieInjectable {
	public static final String NS = "http://tolweb.org/webservices/objectmanifest/2008";
	
	private static final String NODE_ID = "node_id";
	public static final String SERVICE_NAME = "tol-object-manifest";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public abstract Long getCurrentNodeId();
	public abstract void setCurrentNodeId(Long id);
	
	public void activateExternalPage(Object[] args, IRequestCycle cycle) {
		String nodeIdString = cycle.getParameter(NODE_ID);
		Long rootNodeId = null;
		if (StringUtils.notEmpty(nodeIdString)) {
			try {
				rootNodeId = Long.parseLong(nodeIdString);
			} catch (NumberFormatException e) {
				rootNodeId = null;
			}
		} else {
			rootNodeId = getCurrentNodeId();
		}
		
		createObjectManifest(rootNodeId);
	}

	public void pageBeginRender(PageEvent event) {
		createObjectManifest(getCurrentNodeId());
	}
	
	private void createObjectManifest(Long rootNodeId) {
		Document doc = ObjectManifestHelper.createObjectManifest(rootNodeId, getWorkingNodeDAO(), 
								getMetaNodeDAO());
		Contributor user = getCookieAndContributorSource().getContributorFromSessionOrAuthCookie();
		ObjectManifestHelper.saveObjectManifest(doc, getCurrentNodeId(), user, getObjectManifestLogDAO());
		setDocumentString(doc.toXML());
		System.out.println(doc.toXML());
	}
	
	
}
