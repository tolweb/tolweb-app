/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.tolweb.treegrowserver;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.base.xml.BaseXMLReader;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.dao.NodeDAO;
import org.tolweb.dao.PageDAO;
import org.tolweb.hibernate.MappedNode;
import org.tolweb.hibernate.MappedOtherName;
import org.tolweb.hibernate.MappedPage;

/**
 * @author dmandel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServerXMLReaderTest extends ApplicationContextTestAbstract {
	private ServerXMLReader serverXMLReader;
	private NodeDAO nodeDAO;
	private PageDAO pageDAO;
	
	public ServerXMLReaderTest(String name) {
		super(name);
		serverXMLReader = (ServerXMLReader) context.getBean("serverXMLReader");
		nodeDAO = (NodeDAO) context.getBean("nodeDAO");
		pageDAO = (PageDAO) context.getBean("workingPageDAO");
	}
	
	/*public void testGetNodeIdsFromRootNodeElement() {
	    Document doc = serverXMLReader.getDocumentFromString(getNodeTestString());
	    Element rootNodeElement = doc.getRootElement();
	    Set nodeIdsSet = serverXMLReader.getNodeIdsFromRootNodeElement(rootNodeElement);
	    assertEquals(nodeIdsSet.size(), 3);
	}
	
	public void testServerParsing() {
		String testString = "<TREE><NODES><NODE ID=\"1\"/></NODES><DOWNLOAD_ID>12</DOWNLOAD_ID></TREE>";
		Document doc = serverXMLReader.getDocumentFromString(testString);
		assertEquals(serverXMLReader.fetchDownloadId(doc.getRootElement()), 12);
	}
	
	public void testGetChildNodeElements() {
		String testString = getNodeTestString();
		Document doc = serverXMLReader.getDocumentFromString(testString);
		Element nodeElement = doc.getRootElement();
		List childNodeElements = serverXMLReader.getChildNodeElements(nodeElement);
		assertEquals(childNodeElements.size(), 2);
	}
	
	public void testFetchNodeProperties() {
		String testString = getNodeTestString();
		Document doc = serverXMLReader.getDocumentFromString(testString);
		Element nodeElement = doc.getRootElement();
		assertEquals(serverXMLReader.getNodeIdFromNodeElement(nodeElement), 16418);
		MappedNode node = nodeDAO.getNodeWithId(new Long(16418));
		serverXMLReader.readNodeProperties(node, nodeElement);
		assertEquals(node.getName(), "Homox");
		assertEquals(node.getExtinct(), 0);
		assertEquals(node.getConfidence(), 0);
		assertEquals(node.getPhylesis(), 0);
		assertEquals(node.getShowNameAuthority(), false);
		assertEquals(node.getShowPreferredAuthority(), false);
		assertEquals(node.getShowImportantAuthority(), false);
		assertEquals(node.getIsLeaf(), false);
		assertEquals(node.getNodeRankInteger(), new Integer(0));
		assertEquals(node.getAuthorityDate(), new Integer(0));
		assertEquals(node.getSynonyms().size(), 2);
		Iterator it = node.getSynonyms().iterator();
		MappedOtherName otherName = (MappedOtherName) it.next();
		assertEquals(otherName.getOrder(), 0);
		assertEquals(otherName.getIsImportant(), false);
		assertEquals(otherName.getIsPreferred(), false);
		assertEquals(otherName.getDate(), 0);
		assertEquals(otherName.getName(), "humans");
		otherName = (MappedOtherName) it.next();
		assertEquals(otherName.getOrder() == 1, true);
		assertEquals(otherName.getIsImportant(), false);
		assertEquals(otherName.getIsPreferred(), false);
		assertEquals(otherName.getDate(), 0);
		assertEquals(otherName.getName(), "man");
		nodeDAO.saveNode(node);
	}
	
	public void testFetchPageProperties() {
		String testString = "<PAGE PRINTIMAGECAPTION=\"0\" PRINTIMAGEDATA=\"1\" NEW_FROM_SERVER=\"FALSE\" REFERENCESCHANGED=\"FALSE\" INTERNETLINKSCHANGED=\"FALSE\" CHANGED=\"FALSE\" IMAGECHANGED=\"FALSE\" PRETREETEXTCHANGED=\"FALSE\" POSTTREETEXTCHANGED=\"FALSE\" LISTCHANGED=\"FALSE\" ACCESSORYCHANGED=\"FALSE\" STATUS=\"Under Construction\" STATUSCHANGED=\"FALSE\" WRITEASLIST=\"0\" WRITECHANGED=\"FALSE\" IMGCAPTIONCHANGED=\"FALSE\" ACKSCHANGED=\"FALSE\" CONTRIBUTORCHANGED=\"FALSE\">" + 
        "<REFERENCES><![CDATA[Some refs]]></REFERENCES>" + 
        "<INTERNETLINKS><![CDATA[<a href=\"http://tolweb.org\">Tree of Life Web Project</a>]]></INTERNETLINKS>" + 
        "<IMAGELIST />" + 
        "<PRETREETEXT></PRETREETEXT>" + 
        "<POSTTREETEXT></POSTTREETEXT>" + 
        "<TEXTLIST>" + 
            "<TEXTSECTION SEQUENCE=\"1\" INDENT=\"0\" CHANGED=\"FALSE\"><HEADING><![CDATA[Introduction]]></HEADING><TEXT></TEXT></TEXTSECTION>" + 
            "<TEXTSECTION SEQUENCE=\"2\" INDENT=\"0\" CHANGED=\"FALSE\"><HEADING><![CDATA[Characteristics]]></HEADING><TEXT></TEXT></TEXTSECTION>" + 
            "<TEXTSECTION SEQUENCE=\"3\" INDENT=\"0\" CHANGED=\"FALSE\"><HEADING><![CDATA[Discussion of Phylogenetic Relationships]]></HEADING><TEXT></TEXT></TEXTSECTION>" + 
            "<TEXTSECTION SEQUENCE=\"4\" INDENT=\"0\" CHANGED=\"FALSE\"><HEADING><![CDATA[New test text section]]></HEADING><TEXT><![CDATA[test text]]></TEXT></TEXTSECTION>" +
        "</TEXTLIST>" + 
        "<IMGCAPTION></IMGCAPTION>" +
        "<ACKS></ACKS>" + 
        "<COPYRIGHT DATECHANGED=\"FALSE\" HOLDERCHANGED=\"FALSE\">" + 
            "<DATE>1999</DATE>" + 
            "<HOLDER />" + 
        "</COPYRIGHT>" + 
        "<CONTRIBUTORLIST />" + 
		"</PAGE>";
		Document doc = serverXMLReader.getDocumentFromString(testString);
		Element pageElement = doc.getRootElement();
		MappedPage page = pageDAO.getPageForNode(nodeDAO.getNodeWithId(new Long(16418)));
		serverXMLReader.readPageProperties(page, pageElement);
		assertEquals(page.getPrintCustomCaption(), false);
		assertEquals(page.getPrintImageData(), true);
		assertEquals(page.getStatus(), "Under Construction");
		assertEquals(page.getWriteAsList(), false);
		assertEquals(page.getReferences(), "Some refs");
		assertEquals(page.getInternetLinks(), "<a href=\"http://tolweb.org\">Tree of Life Web Project</a>");
		assertEquals(page.getTitleIllustrations().size(), 0);
		assertEquals(page.getTextSections().size(), 4);
		assertEquals(page.getCopyrightDate(), "1999");
		pageDAO.savePage(page);
	}*/
	
	private String getNodeTestString() {
		return "<NODE HASPAGE=\"1\" NAMECHANGED=\"TRUE\" ID=\"16418\" LOCKED=\"0\" EXTINCT=\"0\" EXTINCTCHANGED=\"FALSE\" CONFIDENCE=\"0\" CONFIDENCECHANGED=\"FALSE\" PHYLESIS=\"0\" PHYLESISCHANGED=\"FALSE\" SHOWAUTHORITY=\"0\" SHOWAUTHORITYCHANGED=\"FALSE\" SHOWPREFAUTHORITY=\"0\" SHOWPREFAUTHORITYCHANGED=\"FALSE\" SHOWIMPAUTHORITY=\"0\" SHOWIMPAUTHORITYCHANGED=\"FALSE\" CHILDCOUNT=\"5\" CHILDRENCHANGED=\"FALSE\" SEQUENCE=\"1\" SEQUENCECHANGED=\"FALSE\" LEAF=\"0\" LEAFCHANGED=\"FALSE\" NODERANK=\"0\" NODERANKCHANGED=\"FALSE\" PAGECHANGED=\"FALSE\" DESCRIPTIONCHANGED=\"FALSE\" OTHERNAMESCHANGED=\"FALSE\">" + 
        "<NAME><![CDATA[Homox]]></NAME>" + 
        "<AUTHORITY CHANGED=\"FALSE\"></AUTHORITY>" + 
        "<AUTHDATE CHANGED=\"FALSE\">0</AUTHDATE>" + 
        "<OTHERNAMES CHANGED=\"FALSE\">" +
        "<OTHERNAME ISIMPORTANT=\"0\" ISPREFERRED=\"0\" SEQUENCE=\"0\" CHANGED=\"FALSE\" DATE=\"0\">" + 
            "<NAME><![CDATA[humans]]></NAME>" + 
        "</OTHERNAME>" + 
        "<OTHERNAME ISIMPORTANT=\"0\" ISPREFERRED=\"0\" SEQUENCE=\"1\" CHANGED=\"FALSE\" DATE=\"0\">" + 
            "<NAME><![CDATA[man]]></NAME>" + 
        "</OTHERNAME>" + 
		"</OTHERNAMES>" + 
		"<NODES>" +
		"<NODE ID=\"666\"/>" +
		"<NODE ID=\"667\"/>" +
		"</NODES>" +
		"</NODE>";		
	}
	public void testGetNames() {
		Document xmlDoc = BaseXMLReader.getDocumentFromString(getNodeTestString());
		List names = serverXMLReader.getAllNodeNames(xmlDoc);
		assertEquals(names.size(), 1);
		String firstName = (String) names.get(0);
		assertEquals(firstName, "Homox");
	}
}
