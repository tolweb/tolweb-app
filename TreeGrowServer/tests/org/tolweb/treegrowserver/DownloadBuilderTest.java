package org.tolweb.treegrowserver;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.output.XMLOutputter;
import org.jdom.Document;
import org.jdom.Element;
import org.tolweb.dao.ApplicationContextTestAbstract;
import org.tolweb.treegrow.main.Contributor;
import org.tolweb.treegrow.main.StringUtils;
import org.tolweb.treegrow.main.XMLConstants;
import org.tolweb.treegrowserver.DownloadBuilder;
import org.tolweb.treegrowserver.dao.DownloadDAO;

public class DownloadBuilderTest extends ApplicationContextTestAbstract{
	private DownloadBuilder downloadBuilder;
	private DownloadDAO downloadDao;
	
	public DownloadBuilderTest(String name) {
		super(name);
		downloadBuilder = (DownloadBuilder) context.getBean("downloadBuilder");
		downloadDao = (DownloadDAO) context.getBean("downloadDAO");
	}
	
	public void testBuildDownloads() {
		/*Contributor danny = new Contributor();
		danny.setId(664);
		Document doc = downloadBuilder.buildDownload("Life", 1, false, true, danny, null, false);
		printOutDocument(doc);
		Download download = getDownloadFromDownloadDocument(doc);
		assertNotNull(download);
		assertEquals(download.getRootNode().getNodeId(), new Long(1));
		assertEquals(download.getIsActive(), true);
		assertEquals(download.getDownloadedNodes().size(), 5);
		checkLifeDownloadNodes(download.getDownloadedNodes());
		System.out.println("treestructure is: " + doc.getRootElement().getChildText(XMLConstants.TREESTRUCTURE));
		assertEquals(doc.getRootElement().getChildText(XMLConstants.TREESTRUCTURE), "1(2,3,4,5)");
		// Try and download life again, should get a lock and not create the download
		doc = downloadBuilder.buildDownload("Life", 1, false, true, danny, null, false);
		assertEquals(doc.getRootElement().getName(), XMLConstants.ERROR);
		doc = downloadBuilder.buildDownload("Eukaryotes", 1, false, true, danny, download, false);
		// There are 12 more nodes on the eukaryotes page -- but Green Plants is checked out so it doesn't count
		assertEquals(download.getDownloadedNodes().size(), 16);
		// It shouldn't change the root node id
		assertEquals(download.getRootNode().getNodeId(), new Long(1));
		// Make sure the document's root starts at eukaryotes
		assertEquals(doc.getRootElement().getChild(XMLConstants.NODES).getChild(XMLConstants.NODE).getChildText(XMLConstants.NAME), "Eukaryotes");
		downloadDao.deleteDownload(download);
		doc = downloadBuilder.buildDownload("Terrestrial Vertebrates", 1, false, true, danny, null, false);
		assertEquals(doc.getRootElement().getChildText(XMLConstants.TREESTRUCTURE), "14952(14975(14976(14977(14978(14979(14980(14981(14982(14983(14984(14985(14986(14987(14988(14989(14990,14991),14992),14993(14994(14995(14996(14997,14998),14999),15000),15001(15002,15003))),15004),15005),15006,15007),15008),15009),15010),15011),15012),15013),15014),15015,15016),15017(15018,15019)),15020)");
		download = getDownloadFromDownloadDocument(doc);
		assertNotNull(download);
		assertEquals(download.getRootNode().getNodeId(), new Long(14952));
		assertEquals(download.getIsActive(), true);
		assertEquals(download.getDownloadedNodes().size(), 47);
		downloadDao.deleteDownload(download);		
		printOutDocument(doc);*/
	}
	
	public void testEditingPrivileges() {
		Contributor danny = new Contributor();
		danny.setId(664);
		Document doc = downloadBuilder.buildDownload("Carnivora", 1, true, true, danny, null, false, false);
		assertEquals(doc.getRootElement().getName(), XMLConstants.ERROR);
		// Now, as Katja, we should be able to download this
		Contributor katja = new Contributor();
		katja.setId(663);
		doc = downloadBuilder.buildDownload("Carnivora", 1, true, true, katja, null, true, false);
		assertEquals(doc.getRootElement().getName(), XMLConstants.TREE);
	}
	
	public void testXMLFields() {
	    /*Document doc = downloadBuilder.buildDownload("Life", 0, true);
	    Element nodeElement = getRootNodeElementFromDocument(doc);
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.ID), "1");
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.HASPAGE), "1");
	    assertEquals(nodeElement.getChildText(XMLConstants.NAME), "Life on Earth");
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.CHILDCOUNT), "4");
	    Element pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getAttributeValue(XMLConstants.PRINTIMAGEDATA), "1");
	    Element imageListElement = pageElement.getChild(XMLConstants.IMAGELIST);
	    Element imgElement = (Element) imageListElement.getChildren(XMLConstants.IMAGE).get(0);
	    assertEquals(imgElement.getAttributeValue(XMLConstants.URL), "/tree/ToLimages/meloid.200.gif");
	    assertEquals(imgElement.getAttributeValue(XMLConstants.IMAGEID), "714");
	    assertEquals(imgElement.getAttributeValue(XMLConstants.ORDER), "0");
	    imgElement = (Element) imageListElement.getChildren(XMLConstants.IMAGE).get(1);
	    assertEquals(imgElement.getAttributeValue(XMLConstants.URL), "/tree/ToLimages/fischerella.200.gif");
	    assertEquals(imgElement.getAttributeValue(XMLConstants.IMAGEID), "1888");
	    assertEquals(imgElement.getAttributeValue(XMLConstants.ORDER), "1");
	    Element textListElement = pageElement.getChild(XMLConstants.TEXTLIST);
	    Element dprElement = textListElement.getChild(XMLConstants.TEXTSECTION);
	    assertEquals(dprElement.getChildText(XMLConstants.HEADING), "Discussion of Phylogenetic Relationships");
	    assertNotNull(dprElement.getChildText(XMLConstants.TEXT));
	    assertTrue(StringUtils.notEmpty(dprElement.getChildText(XMLConstants.TEXT)));
	    assertEquals(doc.getRootElement().getChildText(XMLConstants.TREESTRUCTURE), "1");
	    
	    
	    doc = downloadBuilder.buildDownload("Amphilinidea", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getAttributeValue(XMLConstants.STATUS), XMLConstants.PEER_REVIEWED);
	    Element contributorsElement = pageElement.getChild(XMLConstants.CONTRIBUTORLIST);
	    Element contributorElement = contributorsElement.getChild(XMLConstants.CONTRIBUTOR);
	    assertEquals(contributorElement.getAttributeValue(XMLConstants.IS_AUTHOR), "1");
	    assertEquals(contributorElement.getAttributeValue(XMLConstants.COPYRIGHT), "1");
	    assertEquals(contributorElement.getAttributeValue(XMLConstants.CONTACT), "1");
	    assertEquals(contributorElement.getAttributeValue(XMLConstants.ID), "611");
	    assertEquals(contributorElement.getAttributeValue(XMLConstants.ORDER), "0");

	    doc = downloadBuilder.buildDownload("Catapieseini", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getChildText(XMLConstants.REFERENCES), "Reichardt, H. 1973.  Monograph of Catapiesini, a Neotropical tribe of Carabidae (Coleoptera). Studia Ent., 16:321-342.");
	    
	    doc = downloadBuilder.buildDownload("Admestina", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getChildText(XMLConstants.INTERNETLINKS), "Proszynski's listing for <a href=\"http://salticidae.org/salticid/catalog/Admestin.htm\">Admestina</a>");
	    
	    doc = downloadBuilder.buildDownload("Neobrachinus", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    String postTreeText = pageElement.getChildText(XMLConstants.POSTTREETEXT);
	    assertEquals(postTreeText, "From Erwin (1970)");
	    
	    doc = downloadBuilder.buildDownload("Carabidae", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    String firstOnlineString = pageElement.getChildText(XMLConstants.FIRSTONLINE);
	    assertEquals(firstOnlineString, "1994-11-16");
	    
	    doc = downloadBuilder.buildDownload("Euryarchaeota", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getAttributeValue(XMLConstants.PRINTIMAGECAPTION), "1");
	    
	    doc = downloadBuilder.buildDownload("Japetella", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getChildText(XMLConstants.PRETREETEXT), "A single species is recognized in this genus.");
	    
	    doc = downloadBuilder.buildDownload("Viruses", 0, true);
	    nodeElement = getRootNodeElementFromDocument(doc);
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.CONFIDENCE), "2");
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.PHYLESIS), "1");
	    
	    doc = downloadBuilder.buildDownload("Ankylosauridae", 0, true);
	    nodeElement = getRootNodeElementFromDocument(doc);
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.EXTINCT), "2");

	    doc = downloadBuilder.buildDownload("Eubacteria", 0, true);
	    nodeElement = getRootNodeElementFromDocument(doc);
	    assertEquals(nodeElement.getChildText(XMLConstants.DESCRIPTION), "(\"True bacteria\", mitochondria, and chloroplasts)");
	    
	    doc = downloadBuilder.buildDownload("Homo Sapiens", 0, true);
	    nodeElement = getRootNodeElementFromDocument(doc);
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.LEAF), "1");
	    
	    doc = downloadBuilder.buildDownload("Erwiniana", 0, true);
	    nodeElement = getRootNodeElementFromDocument(doc);
	    assertEquals(nodeElement.getChildText(XMLConstants.AUTHORITY), "Paulsen and Smith");
	    assertEquals(nodeElement.getChildText(XMLConstants.AUTHDATE), "2003");
	    pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getAttributeValue(XMLConstants.WRITEASLIST), "1");
	    
	    doc = downloadBuilder.buildDownload("Ocythoe tuberculata", 0, true);
	    nodeElement = getRootNodeElementFromDocument(doc);
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.SHOWAUTHORITY), "1");
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.SHOWPREFAUTHORITY), "1");
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.SHOWIMPAUTHORITY), "1");
	    
	    doc = downloadBuilder.buildDownload("Bembidion litorale", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getChildText(XMLConstants.IMGCAPTION), "Male. Germany: Emsdetten. ");
	    
	    doc = downloadBuilder.buildDownload("Scolopendromorpha", 0, true);
	    nodeElement = getRootNodeElementFromDocument(doc);
	    assertEquals(nodeElement.getAttributeValue(XMLConstants.NODERANK), "3");
	    
	    doc = downloadBuilder.buildDownload("Astacidae", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    assertEquals(pageElement.getChildText(XMLConstants.ACKS), "Page constructed by Emily Browne.");
	    
	    doc = downloadBuilder.buildDownload("Isopoda", 0, true);
	    pageElement = getRootNodePageElementFromDocument(doc);
	    Element copyrightElement = pageElement.getChild(XMLConstants.COPYRIGHT);
	    assertEquals(copyrightElement.getChildText(XMLConstants.HOLDER), "R. Brusca");
	    assertEquals(copyrightElement.getChildText(XMLConstants.DATE), "1997");

	    doc = downloadBuilder.buildDownload("Ancistrocheirus lesueurii", 0, true);
	    nodeElement = getRootNodeElementFromDocument(doc);
	    Element othernamesElement = nodeElement.getChild(XMLConstants.OTHERNAMES);
	    assertNotNull(othernamesElement);
	    List othernames = othernamesElement.getChildren(XMLConstants.OTHERNAME); 
	    assertEquals(othernames.size(), 2);
	    Element firstOtherName = (Element) othernames.get(0);
	    assertEquals(firstOtherName.getAttributeValue(XMLConstants.ISIMPORTANT), "1");
	    assertEquals(firstOtherName.getAttributeValue(XMLConstants.ISPREFERRED), "0");
	    assertEquals(firstOtherName.getAttributeValue(XMLConstants.SEQUENCE), "0");
	    assertEquals(firstOtherName.getAttributeValue(XMLConstants.DATE), "1849");
	    assertEquals(firstOtherName.getChildText(XMLConstants.NAME), "Ancistrocheirus");
	    assertEquals(firstOtherName.getChildText(XMLConstants.AUTHORITY), "Gray");
	    Element secondOtherName = (Element) othernames.get(1);
	    assertEquals(secondOtherName.getAttributeValue(XMLConstants.ISIMPORTANT), "0");
	    assertEquals(secondOtherName.getAttributeValue(XMLConstants.ISPREFERRED), "1");
	    assertEquals(secondOtherName.getAttributeValue(XMLConstants.SEQUENCE), "1");
	    assertEquals(secondOtherName.getAttributeValue(XMLConstants.DATE), "1912");
	    assertEquals(secondOtherName.getChildText(XMLConstants.NAME), "Ancistrocheiridae");
	    assertEquals(secondOtherName.getChildText(XMLConstants.AUTHORITY), "Pfeffer");*/	
	}
	
	private Element getRootNodeElementFromDocument(Document doc) {
	    assertEquals(doc.getRootElement().getName(), XMLConstants.TREE);
	    Element nodesElement = doc.getRootElement().getChild(XMLConstants.NODES);
	    assertNotNull(nodesElement);
	    Element nodeElement = nodesElement.getChild(XMLConstants.NODE);
	    assertNotNull(nodeElement);
	    return nodeElement;
	}
	
	private Element getRootNodePageElementFromDocument(Document doc) {
	    Element nodeElement = getRootNodeElementFromDocument(doc);
	    Element pageElement = nodeElement.getChild(XMLConstants.PAGE);
	    assertNotNull(pageElement);
	    return pageElement;
	}
	
	private Download getDownloadFromDownloadDocument(Document downloadDoc) {
		String downloadId = downloadDoc.getRootElement().getChild(XMLConstants.DOWNLOAD_ID).getText();
		Long idLong = new Long(downloadId);
		Download download = downloadDao.getDownloadWithId(idLong);
		return download;
	}
	
	private void checkLifeDownloadNodes(Set downloadNodes) {
		Iterator it = downloadNodes.iterator();
		while (it.hasNext()) {
			DownloadNode dn = (DownloadNode) it.next();
			int nodeId = dn.getNodeId().intValue(); 
			if (nodeId == 1) {
				assertEquals(dn.getActive(), DownloadNode.ACTIVE);
			} else if (nodeId == 2 || nodeId == 3 || nodeId == 4 || nodeId == 5) {
				assertEquals(dn.getActive(), DownloadNode.BARRIER_ACTIVE);
			} else {
				System.out.println("bad download id is: " + nodeId);
				assertEquals(true, false);
			}
		}
	}
}