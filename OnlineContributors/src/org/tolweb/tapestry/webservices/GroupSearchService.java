package org.tolweb.tapestry.webservices;

import java.util.List;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.RequestInjectable;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.XMLConstants;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

public abstract class GroupSearchService extends AbstractXMLPage implements IExternalPage, RequestInjectable,
		NodeInjectable {

	public void activateExternalPage(Object[] arg0, IRequestCycle arg1) {
		String group = getRequest().getParameterValue(RequestParameters.GROUP);
		List<Object[]> resultNodes = getPublicNodeDAO().findNodeNamesParentIdsAndIdsExactlyNamed(group);
		if (resultNodes == null || resultNodes.size() == 0) {
			resultNodes = getPublicNodeDAO().findNodeNamesParentIdsAndIdsNamed(group);
		}
		DataWriter w = getDataWriterForServletResponse();		
		if (resultNodes.size() > 0) {
			try {
				// if more than one match, find the name of the parent group for each match
				boolean addParentGroupName = resultNodes.size() > 1;
				w.startDocument();
				AttributesImpl atts = new AttributesImpl();
				atts.addAttribute("", XMLConstants.COUNT, XMLConstants.COUNT, "CDATA", "" + resultNodes.size());
				w.startElement("", XMLConstants.NODES, "", atts);
				for (Object[] nodeInfo : resultNodes) {
					outputMatchInfoForNode(nodeInfo, addParentGroupName, w);
				}
				w.endElement(XMLConstants.NODES);
				w.endDocument();
			} catch (Exception e) {
			}			
		} else {
			outputNoResultsDocument(group, w);
		}
	}

	private void outputMatchInfoForNode(Object[] nodeInfo, boolean addParentGroupName, DataWriter w) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", XMLConstants.ID, XMLConstants.ID, "CDATA", nodeInfo[1].toString());
		w.startElement("", XMLConstants.NODE, "", atts);
		outputNodeNameElement((String) nodeInfo[0], w);
		if (addParentGroupName) {
			w.startElement(XMLConstants.PARENTGROUP);
			String parentGroupName = getPublicNodeDAO().getNameForNodeWithId((Long) nodeInfo[2]);
			w.characters(parentGroupName);
			w.endElement(XMLConstants.PARENTGROUP);
		}
		w.endElement(XMLConstants.NODE);
	}

	private void outputNoResultsDocument(String group, DataWriter w) {
		try {
			w.startDocument();
			w.dataElement("error", "There were no nodes found matching the name '" + group + "'");
			w.endDocument();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
