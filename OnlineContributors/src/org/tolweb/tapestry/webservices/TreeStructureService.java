package org.tolweb.tapestry.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.tolweb.base.xml.BaseXMLWriter;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.tapestry.injections.NodeInjectable;
import org.tolweb.tapestry.injections.PageInjectable;
import org.tolweb.tapestry.injections.TreeGrowServerInjectable;
import org.tolweb.tapestry.injections.UserInjectable;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.RequestParameters;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.ServerXMLWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

/**
 * Page class used for outputting various web services
 * @author dmandel
 *
 * onlinecontributors/app?service=external&page=xml/TreeStructureService&node_id=yyy
 * onlinecontributors/app?service=external&page=xml/TreeStructureService&node_id=yyy&optype=1
 * onlinecontributors/app?service=external&page=xml/TreeStructureService&node_id=yyy&use_working=yes
 */
public abstract class TreeStructureService extends AbstractXMLPage implements IExternalPage, 
		NodeInjectable, PageInjectable, UserInjectable, TreeGrowServerInjectable {
	
	private static final String PAGEIMAGE = "globe.png";
	private static final String PERSONIMAGE = "contr.png";
	private static final String PERSONPAGEIMAGE = "personglobe.png";
	private static final int TOLXML = 0;
	private static final int TREEML = 1;	
	
	public static final int PAGE_DEPTH_UNLIMITED = -1;
	
	public abstract ServerXMLWriter getCurrentServerXMLWriter();
	public abstract void setCurrentServerXMLWriter(ServerXMLWriter writer);
	
	public void activateExternalPage(Object[] arg0, IRequestCycle cycle) {
		String nodeIdString = getRequest().getParameterValue(RequestParameters.NODE_ID);
		Long rootNodeId = 1L;
		if (StringUtils.notEmpty(nodeIdString)) {
			try {
				rootNodeId = Long.parseLong(nodeIdString);
			} catch (NumberFormatException e) {
				rootNodeId = null;
			}
		}
		// optype parameter controls what type of xml response is output
		// as of now it's either a tolxml document or a treeml document for
		// the treeviz applet
		int optype = TOLXML;
		String optypeString = getRequest().getParameterValue(RequestParameters.OPTYPE);
		if (StringUtils.notEmpty(optypeString)) {
			
			try {
				optype = Integer.parseInt(optypeString);
			} catch (Exception e) {}
		}
		boolean useTreeStructure = true;		
		String noTreeParam = getRequest().getParameterValue(RequestParameters.NO_TREE);
		if (StringUtils.notEmpty(noTreeParam)) {
			Pattern pattern = Pattern.compile("yes|true|1", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(noTreeParam);
			useTreeStructure = !matcher.find();
		}
		String pageDepthParam = getRequest().getParameterValue(RequestParameters.PAGE_DEPTH);
		int pageDepth = -1;
		if (StringUtils.notEmpty(pageDepthParam)) {
			try {
				pageDepth = Integer.parseInt(pageDepthParam);
			} catch (Exception e) {}
		}

		boolean useMinimalData = false;
		String useMinimalDataParam = getRequest().getParameterValue(RequestParameters.MINIMAL_DATA);
		if (StringUtils.notEmpty(useMinimalDataParam)) {
			Pattern pattern = Pattern.compile("yes|true|1", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(useMinimalDataParam);
			useMinimalData = matcher.find();
		}		
		
		boolean useWorking = false;
		String useWorkingParam = getRequest().getParameterValue(RequestParameters.USE_WORKING);
		if (StringUtils.notEmpty(useWorkingParam)) {
			Pattern pattern = Pattern.compile("yes|true|1", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(useWorkingParam);
			useWorking = matcher.find();
		}
		
		if (useWorking) {
			setCurrentServerXMLWriter(getServerXMLWriter());
		} else {
			setCurrentServerXMLWriter(getPublicServerXMLWriter());
		}
		
		DataWriter w = getDataWriterForServletResponse();
		if (rootNodeId != null) {
			try {
				getCurrentServerXMLWriter().startDocument(w);
				outputDocumentPrefix(w, optype, useTreeStructure);
				outputInfoForSingleNode(rootNodeId, w, optype, useTreeStructure, pageDepth, useMinimalData);
				outputDocumentPostfix(w, optype, useTreeStructure);				
				getCurrentServerXMLWriter().endDocument(w);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			outputErrorDocument(w, "Bad root node id");
		}
	}

	private void outputDocumentPostfix(DataWriter w, int optype, boolean includeTreeStructure) throws SAXException {
    	if (optype == TREEML) {
    		w.endElement("tree");
    	} else {
    		w.endElement(XMLConstants.TREE);
    	}
	}

	private void outputDocumentPrefix(DataWriter w, int optype, boolean includeTreeStructure) throws SAXException {
    	if (optype == TREEML) {
    		w.startElement("tree");
    		String declarations = "declarations";
    		w.startElement(declarations);
    		addTreeMLDeclarationTag("name", w);
    		addTreeMLDeclarationTag("id", w);
    		addTreeMLDeclarationTag("image", w);
    		addTreeMLDeclarationTag("isLeaf", w);
    		addTreeMLDeclarationTag("description", w);
    		addTreeMLDeclarationTag("contributors", w);
    		w.endElement(declarations);
    	} else {
    		getCurrentServerXMLWriter().startTree(w);
    	}
	}

	private void addTreeMLDeclarationTag(String name, DataWriter w) throws SAXException {
		String attributeDeclTag = "attributeDecl";
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", XMLConstants.name, XMLConstants.name, "CDATA", name);
		atts.addAttribute("", XMLConstants.type, XMLConstants.type, "CDATA", "String");
		w.emptyElement("", attributeDeclTag, attributeDeclTag, atts);
	}

	private void outputErrorDocument(DataWriter w, String errorMessage) {
		try {
			w.startDocument();
			w.dataElement("error", errorMessage);
		} catch (SAXException e) {

		}    	
	}
	
	@SuppressWarnings("unchecked")
    private void outputInfoForSingleNode(Long nodeId, DataWriter w, int xmlType, boolean useTreeStructure, int pageDepth, boolean useMinimalData) throws SAXException {
    	NodeDAO nodeDAO;
    	PageDAO pageDAO;
    	if (xmlType == TREEML) {
    		nodeDAO = getWorkingNodeDAO();
    		pageDAO = getWorkingPageDAO();
    	} else {
    		nodeDAO = getPublicNodeDAO();
    		pageDAO = getPublicPageDAO();
    	}

    	if (xmlType == TOLXML) {
    		if (pageDepth > 0) {
    			getCurrentServerXMLWriter().addTreeStructureXMLForNode(nodeId, useTreeStructure, w, true, pageDepth, useMinimalData);
    		} else {
    			getCurrentServerXMLWriter().addTreeStructureXMLForNode(nodeId, useTreeStructure, w, true, useMinimalData);
    		}
    	} else if (xmlType == TREEML) {
        	Object[] info = nodeDAO.getMinimalNodeInfoForNodeId(nodeId);
        	String name = (String) info[0];
        	boolean isLeaf = (Boolean) info[4];
        	String description = (String) info[5];
        	boolean hasPage = pageDAO.getNodeHasPage(nodeId);
        	List<Long> childIds = nodeDAO.getChildrenNodeIds(nodeId);    		
    		List<Long> ids = new ArrayList<Long>();
    		ids.add(nodeId);
    		Collection<Contributor> attachedToNode = getContributorDAO().getContributorsAttachedToNodeIds(ids, getPermissionChecker(), true);
    		List contrNames = new ArrayList();
    		for (Contributor contributor : attachedToNode) {
				contrNames.add(contributor.getDisplayName());
			}
    		String contrString = StringUtils.returnCommaJoinedString(contrNames);
    		outputTreeMLXML(nodeId, name, hasPage, childIds, isLeaf, description, contrString, w);
    	}
    }
    
    private void outputTreeMLXML(Long nodeId, String name, boolean hasPage, List<Long> childIds, boolean isLeaf, 
    		String description, String contrString, DataWriter w) throws SAXException {
    	w.startElement(XMLConstants.branch);
    	addTreeMLAttributeTag(XMLConstants.name, name, w);
    	addTreeMLAttributeTag("id", nodeId.toString(), w);
		boolean hasContr = StringUtils.notEmpty(contrString);
    	if (hasPage) {
    		if (hasContr) {
    			addTreeMLAttributeTag("image", PERSONPAGEIMAGE, w);
    		} else {
    			addTreeMLAttributeTag("image", PAGEIMAGE, w);
    		}
    	} else if (hasContr) {
    		addTreeMLAttributeTag("image", PERSONIMAGE, w);
    	}
    	if (isLeaf) {
    		addTreeMLAttributeTag("isLeaf", BaseXMLWriter.getBooleanString(isLeaf), w);
    	}
    	if (StringUtils.notEmpty(description)) {
    		addTreeMLAttributeTag("description", description, w);
    	}
    	if (hasContr) {
    		addTreeMLAttributeTag("contributors", contrString, w);
    	}
    	for (Long childId : childIds) {
			outputInfoForSingleNode(childId, w, TREEML, true, PAGE_DEPTH_UNLIMITED, false);
		}
    	w.endElement(XMLConstants.branch);		
	}

	private void addTreeMLAttributeTag(String name, String value, DataWriter w) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", XMLConstants.name, XMLConstants.name, "CDATA", name);
		atts.addAttribute("", XMLConstants.value, XMLConstants.value, "CDATA", cleanCharacters(value));
		w.emptyElement("", XMLConstants.attribute, XMLConstants.attribute, atts);
	}
	
	private String cleanCharacters(String input) {
		input = input.replaceAll("&", "&amp;");
		input = input.replaceAll("<", "&lt;");
		input = input.replaceAll(">", "&gt;");
		input = input.replaceAll("\"", "&quot;");
		return input;
	}
}
