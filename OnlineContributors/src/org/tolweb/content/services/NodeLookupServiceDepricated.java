package org.tolweb.content.services;

import java.util.List;

import org.apache.tapestry.IRequestCycle;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.tapestry.XMLPage;

public abstract class NodeLookupServiceDepricated extends XMLPage implements NodeInjectable {
	public static final String SERVICE_NAME = "nodelookup";
	private static final String NODE_NAME = "nodeName";

	public String getName() {
		return SERVICE_NAME;
	}
	
	@SuppressWarnings("unchecked")
	public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
		String nodeName = getRequest().getParameterValue(NODE_NAME);
		List names = getPublicNodeDAO().findNodesExactlyNamed(nodeName);
		
		Document document = new Document();
		Element root = null;
		if (names == null) {
			root = new Element(XMLConstants.ERROR);
			root.setAttribute(XMLConstants.ERRORNUM, "404");
		} else {
			root = new Element(XMLConstants.SUCCESS);
			root.setAttribute("node-name", nodeName);
			root.setAttribute("count", "" + names.size());
			root.setAttribute("service", getName());
			for (Object obj : names) {
				MappedNode mnode = (MappedNode) obj;
				Element nodeElement = new Element("node");
				nodeElement.setAttribute("name", mnode.getName());
				nodeElement.setAttribute("node-id", ""+mnode.getNodeId());
				nodeElement.setAttribute("has-page", mnode.getHasPage() ? "true" : "false");
				nodeElement.setAttribute("page-id", ""+mnode.getPageId());
				nodeElement.setAttribute("extinct", mnode.getExtinct() == 1 ? "true" : "false");
				List children = getPublicNodeDAO().getChildrenNodes(mnode);
				addNodeChildren(children, nodeElement);
				root.addContent(nodeElement);
			}
			
		}
		document.setRootElement(root);
		setResultDocument(document);
	}
	
	@SuppressWarnings("unchecked")
	public void addNodeChildren(List children, Element root) {
		Element childrenElement = new Element("children");
		childrenElement.setAttribute("count", ""+children.size());
		for (Object obj : children) {
			MappedNode child = (MappedNode) obj;
			Element childElement = new Element("node");
			childElement.setAttribute("name", child.getName());
			childElement.setAttribute("node-id", ""+child.getNodeId());
			childElement.setAttribute("has-page", child.getHasPage() ? "true" : "false");
			childElement.setAttribute("page-id", ""+child.getPageId());
			childElement.setAttribute("extinct", child.getExtinct() == 2 ? "true" : "false");			
			childrenElement.addContent(childElement);
		}
		root.addContent(childrenElement);
	}
}