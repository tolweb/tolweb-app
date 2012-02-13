package org.tolweb.btol.xml;

import java.util.List;

import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.ServerXMLWriter;

public abstract class ContributorList extends SequenceNameService implements UserInjectable, PageBeginRenderListener {
	public void pageBeginRender(PageEvent event) {
		Long projectId = getProjectIdOrDefault();
		ServerXMLWriter writer = getServerXMLWriter();
		Document doc = new Document();
		Element rootElement = new Element(XMLConstants.CONTRIBUTORLIST);
		doc.setRootElement(rootElement);
		List<Integer> contributorIds = getProjectDAO().getContributorIdsForProject(projectId);
		List<Contributor> contributors = getContributorDAO().getContributorsWithIds(contributorIds, true);
		for (Contributor nextContr : contributors) {
			rootElement.addContent(writer.encodeContributor(nextContr, false));
		}
		setResultDocument(doc);
	}	
}
